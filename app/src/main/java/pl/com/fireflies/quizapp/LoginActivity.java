package pl.com.fireflies.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity
{
    private EditText login_edit,password_edit;
    private Button login_button,register_button;
    static private String login="admin",password="password",l,p;
    static private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Placeholder
                 * */
                if(login.equals(login_edit.getText().toString()) && password.equals(password_edit.getText().toString()))
                {
                    intent = new Intent(LoginActivity.this,UserPanelActivity.class);
                    LoginActivity.this.startActivity(intent);
                }
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
    }

    protected void initViews()
    {
        login_edit = (EditText)findViewById(R.id.login_edit);
        password_edit = (EditText)findViewById(R.id.password_edit);
        login_button = (Button)findViewById(R.id.login_button);
        register_button = (Button)findViewById(R.id.register_button);
    }
}
