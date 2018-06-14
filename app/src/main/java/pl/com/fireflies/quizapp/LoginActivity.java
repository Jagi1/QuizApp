package pl.com.fireflies.quizapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText login_edit, password_edit;
    private Button login_button, register_button;
    static private String login = "admin", password = "password";
    static private Intent intent;
    final FirebaseAuth auth = FirebaseAuth.getInstance();

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
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    private void login(String login, String password) {
        auth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Logowanie nie powiodło się.", Toast.LENGTH_SHORT).show();
                        } else {
                            intent = new Intent(LoginActivity.this, UserPanelActivity.class);
                            LoginActivity.this.startActivity(intent);
                        }
                    }
                });
    }
}
