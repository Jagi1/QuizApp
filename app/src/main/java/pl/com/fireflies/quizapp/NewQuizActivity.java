package pl.com.fireflies.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class NewQuizActivity extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    private Toolbar toolbar;
    private ImageButton avatar, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quiz);
        initViews();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.settings_gear:
                intent = new Intent(NewQuizActivity.this, SettingsActivity.class);
                NewQuizActivity.this.startActivity(intent);
                break;

            case R.id.avatar:
                intent = new Intent(NewQuizActivity.this, AccountSettingsActivity.class);
                NewQuizActivity.this.startActivity(intent);
                break;
        }
    }

    protected void initViews() {
        toolbar = (Toolbar)findViewById(R.id.user_bar);
        avatar = (ImageButton) findViewById(R.id.avatar);
        settings = (ImageButton) findViewById(R.id.settings_gear);
    }


}
