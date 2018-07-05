package pl.com.fireflies.quizapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

public class CategoriesActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_categories);
        CardView cardview = (CardView) findViewById(R.id.card_view1);
        cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Button is clicked", Toast.LENGTH_LONG).show();
            }
        });
        initViews();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(DataHolder.getInstance().theme_changed)
        {
            DataHolder.getInstance().theme_changed = false;
            recreate();
        }
    }

    protected void initViews() {

    }
}