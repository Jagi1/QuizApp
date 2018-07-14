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
        if (DataHolder.getInstance().dark_theme) setTheme(R.style.DarkAppTheme);
        else setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_categories);

        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);

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
        CardView cardview4 = (CardView) findViewById(R.id.filmy);
        cardview4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(CategoriesActivity.this, SelectCategories.class);
                intent.putExtra("category", "filmy");
                CategoriesActivity.this.startActivity(intent);
            }
        });
        CardView cardview5 = (CardView) findViewById(R.id.muzyka);
        cardview5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(CategoriesActivity.this, SelectCategories.class);
                intent.putExtra("category", "muzyka");
                CategoriesActivity.this.startActivity(intent);
            }
        });
        CardView cardview6 = (CardView) findViewById(R.id.angielski);
        cardview6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(CategoriesActivity.this, SelectCategories.class);
                intent.putExtra("category", "angielski");
                CategoriesActivity.this.startActivity(intent);
            }
        });
        if(DataHolder.getInstance().dark_theme) {
            cardview.setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDark1));
            cardview2.setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDarkPink));
            cardview3.setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDarkViolet));
            cardview4.setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDark2));
            cardview5.setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDarkYellow));
            cardview6.setCardBackgroundColor(getResources().getColor(R.color.colorMaterialDarkRed));
        }
    }
}