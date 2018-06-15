package pl.com.fireflies.quizapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TextView loginText, emailText;
    private EditText newEmail;
    private Button changePassword, changeEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        initViews();

        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            loginText.setText(name);
            emailText.setText(email + " " + emailVerified);

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_email:
                String stringNewEmail = String.valueOf(newEmail.getText());

                user.updateEmail(stringNewEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AccountSettingsActivity.this, "User email address updated.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
        }
    }

    protected void initViews() {
        changeEmail = (Button) findViewById(R.id.change_email);
        changePassword = (Button) findViewById(R.id.change_password);
        changeEmail.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        loginText = (TextView) findViewById(R.id.login);
        emailText = (TextView) findViewById(R.id.email);
        newEmail = (EditText) findViewById(R.id.new_email);
    }
}
