package pl.com.fireflies.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

public class CategoriesActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_categories);
        CardView cardview = (CardView) findViewById(R.id.matematyka);
        cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(CategoriesActivity.this, SelectCategories.class);
                intent.putExtra("category", "matematyka");
                CategoriesActivity.this.startActivity(intent);
            }
        });
        CardView cardview2 = (CardView) findViewById(R.id.sport);
        cardview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(CategoriesActivity.this, SelectCategories.class);
                intent.putExtra("category", "sport");
                CategoriesActivity.this.startActivity(intent);
            }
        });
        CardView cardview3 = (CardView) findViewById(R.id.inne);
        cardview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(CategoriesActivity.this, SelectCategories.class);
                intent.putExtra("category", "inne");
                CategoriesActivity.this.startActivity(intent);
            }
        });
        initViews();
    }

    protected void initViews() {

    }
}