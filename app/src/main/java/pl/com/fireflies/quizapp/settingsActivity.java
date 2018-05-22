package pl.com.fireflies.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.daimajia.swipe.SwipeLayout;

public class settingsActivity extends AppCompatActivity {
    private Intent intent;
    private Toolbar toolbar;
    private ListView settings_list;

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
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        return super.onCreateOptionsMenu(menu);
    }

    protected void initViews()
    {
        toolbar = (Toolbar)findViewById(R.id.user_bar);
    }
}
