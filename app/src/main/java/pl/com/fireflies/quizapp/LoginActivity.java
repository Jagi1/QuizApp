package pl.com.fireflies.quizapp;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.StorageReference;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextInputLayout inputLayout;
    private TextInputEditText login_edit, password_edit;
    private Button login_button, register_button;
    private ProgressDialog progressDialog;
    private CheckBox checkBox;
    public static final String PREF_VAR = "pref_vars";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DataHolder.getInstance().dark_theme = getSharedPreferences(PREF_VAR,0).getBoolean("dark_theme", false);
        if (DataHolder.getInstance().dark_theme) setTheme(R.style.DarkAppTheme);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_login);
        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);
        initViews();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        if (DataHolder.getInstance().theme_changed) recreate();
    }

    protected void initViews()
    {
        login_edit = (TextInputEditText) findViewById(R.id.login_edit_2);
        inputLayout = (TextInputLayout) findViewById(R.id.login_edit);
        password_edit = (TextInputEditText) findViewById(R.id.password_edit_2);
        login_button = (Button) findViewById(R.id.login_button);register_button = (Button) findViewById(R.id.register_button);
        login_button.setOnClickListener(this);
        register_button.setOnClickListener(this);
        checkBox = (CheckBox) findViewById(R.id.remember_check_box);
        ImageView logo = (ImageView)findViewById(R.id.logo_image);
        if (DataHolder.getInstance().dark_theme)
        {
            logo.setImageResource(R.drawable.iluminati_logo_dark);
            progressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Dialog_Alert);
        }
        else
        {
            logo.setImageResource(R.drawable.iluminati_logo);
            progressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        }
        checkBox.setChecked(getSharedPreferences(PREF_VAR,0).getBoolean("pass_checked", false));
        login_edit.setText(getSharedPreferences(PREF_VAR,0).getString("login", ""));
        password_edit.setText(getSharedPreferences(PREF_VAR,0).getString("password", ""));
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    private void login(String login, String password)
    {
        progressDialog.setMessage("Trwa logowanie...");
        progressDialog.show();
        DataHolder.firebaseAuth.signInWithEmailAndPassword(login, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (!DataHolder.firebaseUser.isEmailVerified())
                        {
                            progressDialog.dismiss();
                            // TODO: Replace standard dialog with custom one.
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setMessage("Before you sign in you need to verify your account via email. Should we send email again?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DataHolder.firebaseUser.sendEmailVerification();
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                        else
                        {
                            SharedPreferences.Editor editor = getSharedPreferences(PREF_VAR,0).edit();
                            if (checkBox.isChecked()) // Save login data.
                            {
                                editor.putString("login", login_edit.getText().toString());
                                editor.putString("password", password_edit.getText().toString());
                                editor.putBoolean("pass_checked", true);
                            }
                            else editor.clear(); // Erase login data.
                            editor.apply();
                            setAvatarImage();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    protected void setAvatarImage()
    {
        DataHolder.storageReference.child("user")
                .child(DataHolder.firebaseUser.getUid())
                .child("avatarImage.jpg")
                .getBytes(1024 * 1024 * 3)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // TODO: It would be better to download image one time and save it to file and load it from it instead of downloading it every time you sign in.
                        DataHolder.getInstance().avatarBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        progressDialog.dismiss();
                        LoginActivity.this.startActivity(new Intent(LoginActivity.this, UserPanelActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("ERROR_avatar", "blad");
                    }
                });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.login_button:
                if (!TextUtils.isEmpty(login_edit.getText()) && !TextUtils.isEmpty(password_edit.getText()))
                {
                    if (isNetworkConnected()) login(login_edit.getText().toString(), password_edit.getText().toString());
                    else Toast.makeText(LoginActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.register_button:
                Pair[] pairs = new Pair[4];
                pairs[0] = new Pair<View, String>(login_edit, "emailTransition");
                pairs[1] = new Pair<View, String>(password_edit, "passwordTransition");
                pairs[2] = new Pair<View, String>(login_button, "date_button_transition");
                pairs[3] = new Pair<View, String>(register_button, "register_button_transition");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pairs);
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class), options.toBundle());
                break;
        }
    }
}
