package pl.com.fireflies.quizapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity {
    private EditText login_edit, password_edit;
    private Button login_button, register_button;
    static private Intent intent;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private CheckBox checkBox;
    public static final String PREF_VAR = "pref_vars";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(login_edit.getText()) && !TextUtils.isEmpty(password_edit.getText())) {
                    if (isNetworkConnected()) {
                        login(login_edit.getText().toString(), password_edit.getText().toString());
                    } else {
                        Toast.makeText(LoginActivity.this, "Brak połączenia z internetem.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
    }

    protected void initViews() {
        login_edit = (EditText) findViewById(R.id.login_edit);
        password_edit = (EditText) findViewById(R.id.password_edit);
        login_button = (Button) findViewById(R.id.login_button);
        register_button = (Button) findViewById(R.id.register_button);
        checkBox = (CheckBox) findViewById(R.id.remember_check_box);
        progressDialog = new ProgressDialog(this);

        sharedPreferences = getSharedPreferences(PREF_VAR, 0);
        editor = sharedPreferences.edit();

        login_edit.setText(sharedPreferences.getString("login", ""));
        password_edit.setText(sharedPreferences.getString("password", ""));
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    private void login(String login, String password) {
        progressDialog.setMessage("Trwa logowanie...");
        progressDialog.show();
        DataHolder.getInstance().firebaseAuth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Logowanie nie powiodło się.", Toast.LENGTH_SHORT).show();
                        } else {
                            if(checkBox.isChecked())
                            {
                                editor.putString("login", login_edit.getText().toString());
                                editor.putString("password", password_edit.getText().toString());
                                editor.commit();
                            }
                            else
                            {
                                /**
                                 * Kasuje zapisane dane logowania.
                                 * */
//                                editor.clear();
//                                editor.commit();
                            }
                            progressDialog.dismiss();
                            intent = new Intent(LoginActivity.this, UserPanelActivity.class);
                            LoginActivity.this.startActivity(intent);
                        }
                    }
                });
    }
}
