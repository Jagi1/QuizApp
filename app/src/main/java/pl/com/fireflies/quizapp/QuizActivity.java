package pl.com.fireflies.quizapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity
{

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
        setContentView(R.layout.activity_quiz);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(DataHolder.getInstance().theme_changed)
        {
            recreate();
        }
    }
}
