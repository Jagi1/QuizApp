package pl.com.fireflies.quizapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "RegisterActivity";
    private Button date_button;
    private TextInputEditText email_edit, password_edit, login_edit;
    private DatePickerDialog.OnDateSetListener date_listener;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) setTheme(R.style.DarkAppTheme);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_register);
        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);
        initViews();
        date_listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyyy: " + month + "/" + dayOfMonth + "/" + year);
                date = month + "/" + dayOfMonth + "/" + year;
                date_button.setText(date);
            }
        };
    }

    /**
     * If application theme have been changed, activity will be recreated.
     * */
    @Override
    protected void onRestart()
    {
        super.onRestart();
        if (DataHolder.getInstance().theme_changed) recreate();
    }

    /**
     * This method checks if there is network connection.
     */
    private boolean isNetworkConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    /**
     * Method used to initialize layout views in one place.
     * */
    protected void initViews() {
        login_edit = (TextInputEditText) findViewById(R.id.login_edit_2);
        email_edit = (TextInputEditText) findViewById(R.id.email_edit_2);
        password_edit = (TextInputEditText) findViewById(R.id.password_edit_2);
        date_button = (Button) findViewById(R.id.date_button);
        Button register_button = (Button) findViewById(R.id.register_button);
        register_button.setOnClickListener(this);
        date_button.setOnClickListener(this);
    }

    private void registry(String login, String password) {
        DataHolder.firebaseAuth
                .createUserWithEmailAndPassword(login, password)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    /**
                     * Process of singing up new user succeed.
                     * Toast will be created with note about succeed.
                     * Method create records in database about user level, his in-game currency etc.
                     * Then account verification email is sent.
                     * */
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(RegisterActivity.this, "Rejestracja powiodła się.", Toast.LENGTH_SHORT).show();
                        DataHolder.firebaseDatabase.child("users").child(DataHolder.firebaseUser.getUid()).child("level").setValue("1");
                        DataHolder.firebaseDatabase.child("users").child(DataHolder.firebaseUser.getUid()).child("currency").setValue("0");
                        DataHolder.firebaseDatabase.child("users").child(DataHolder.firebaseUser.getUid()).child("name").setValue(login_edit.getText().toString());
                        DataHolder.firebaseAuth // Send verification email
                                .signInWithEmailAndPassword(email_edit.getText().toString(), password_edit.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        DataHolder.firebaseUser.sendEmailVerification();
                                        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(login_edit.toString()).build();
                                        DataHolder.firebaseUser.updateProfile(profile);
                                        DataHolder.firebaseAuth.signOut();
                                    }
                                });
                        RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    /**
                     * Process of singing up new user failed.
                     * Toast will be created with note why this process failed.
                     * */
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.register_button:
                if (!TextUtils.isEmpty(email_edit.getText()) && !TextUtils.isEmpty(password_edit.getText()))
                {
                    if (isNetworkConnected()) registry(email_edit.getText().toString(), password_edit.getText().toString());
                    else Toast.makeText(RegisterActivity.this, "Brak połączenia z internetem.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.date_button:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
                        android.R.style.Theme_Holo_Light_NoActionBar_Overscan, date_listener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                break;
        }
    }
}


