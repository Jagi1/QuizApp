package pl.com.fireflies.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MyQuizzesActivity extends AppCompatActivity {
    private Intent intent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quiz);
        initViews();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_settings:
                intent = new Intent(MyQuizzesActivity.this,settingsActivity.class);
                MyQuizzesActivity.this.startActivity(intent);
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    protected void initViews()
    {
        toolbar = (Toolbar)findViewById(R.id.user_bar);
    }
}
