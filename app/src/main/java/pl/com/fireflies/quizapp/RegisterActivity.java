package pl.com.fireflies.quizapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private Button date_button, register_button;
    private EditText email_edit, password_edit, login_edit;
    private DatePickerDialog.OnDateSetListener date_listener;
    private String date;
    private Integer year, month, day;
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    static private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this,
                        android.R.style.Theme_Holo_Light_NoActionBar_Overscan,
                        date_listener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email_edit.getText()) && !TextUtils.isEmpty(login_edit.getText()) && !TextUtils.isEmpty(password_edit.getText())) {
                    if (isNetworkConnected()) {
                        registry(email_edit.getText().toString(), password_edit.getText().toString());
                    } else {
                        Toast.makeText(RegisterActivity.this, "Brak połączenia z internetem.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        date_listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyyy: " + month + "/" + dayOfMonth + "/" + year);
                date = month + "/" + dayOfMonth + "/" + year;
                Toast.makeText(RegisterActivity.this, date, Toast.LENGTH_LONG).show();
            }
        };

    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    protected void initViews() {
        email_edit = (EditText) findViewById(R.id.email_edit);
        login_edit = (EditText) findViewById(R.id.login_edit);
        password_edit = (EditText) findViewById(R.id.password_edit);
        date_button = (Button) findViewById(R.id.date_button);
        register_button = (Button) findViewById(R.id.register_button);
    }

    private void registry(String login, String password) {
        auth.createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Rejestracja nie powiodła się.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Rejestracja powiodła się.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.startActivity(intent);
                }
            }
        });
    }
}

