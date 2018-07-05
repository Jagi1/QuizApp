package pl.com.fireflies.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class MyQuizzesActivity extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    private Toolbar toolbar;
    private ImageButton avatar, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(DataHolder.getInstance().dark_theme)
        {
            setTheme(R.style.DarkAppTheme);
        }
        else
        {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_my_quizzes);
        initViews();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(DataHolder.getInstance().theme_changed)
        {
            recreate();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_gear:
                intent = new Intent(MyQuizzesActivity.this, SettingsActivity.class);
                MyQuizzesActivity.this.startActivity(intent);
                break;


            case R.id.avatar:
                intent = new Intent(MyQuizzesActivity.this, AccountSettingsActivity.class);
                MyQuizzesActivity.this.startActivity(intent);
                break;
        }
    }

    protected void initViews() {
        toolbar = (Toolbar) findViewById(R.id.user_bar);
        avatar = (ImageButton) findViewById(R.id.avatar);
        settings = (ImageButton) findViewById(R.id.settings_gear);
    }
}
