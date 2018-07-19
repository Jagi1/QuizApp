package pl.com.fireflies.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import java.util.ArrayList;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) setTheme(R.style.DarkAppTheme);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_categories);
        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);
        ArrayList<CardView> cards = new ArrayList<CardView>()
        {{
           add((CardView) findViewById(R.id.matematyka));
           add((CardView) findViewById(R.id.sport));
           add((CardView) findViewById(R.id.inne));
           add((CardView) findViewById(R.id.filmy));
           add((CardView) findViewById(R.id.muzyka));
           add((CardView) findViewById(R.id.angielski));
        }};
        for (CardView card : cards) card.setOnClickListener(this);
        if(DataHolder.getInstance().dark_theme)
        {
            cards.get(0).setCardBackgroundColor(getColor(R.color.colorMaterialDark1));
            cards.get(1).setCardBackgroundColor(getColor(R.color.colorMaterialDarkPink));
            cards.get(2).setCardBackgroundColor(getColor(R.color.colorMaterialDarkViolet));
            cards.get(3).setCardBackgroundColor(getColor(R.color.colorMaterialDark2));
            cards.get(4).setCardBackgroundColor(getColor(R.color.colorMaterialDarkYellow));
            cards.get(5).setCardBackgroundColor(getColor(R.color.colorMaterialDarkRed));
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.matematyka:
                startActivity(new Intent(CategoriesActivity.this, SelectCategories.class)
                        .putExtra("category", "matematyka"));
                break;

            case R.id.sport:
                startActivity(new Intent(CategoriesActivity.this, SelectCategories.class)
                        .putExtra("category", "sport"));
                break;

            case R.id.inne:
                startActivity(new Intent(CategoriesActivity.this, SelectCategories.class)
                        .putExtra("category", "inne"));
                break;

            case R.id.filmy:
                startActivity(new Intent(CategoriesActivity.this, SelectCategories.class)
                        .putExtra("category", "filmy"));
                break;

            case R.id.muzyka:
                startActivity(new Intent(CategoriesActivity.this, SelectCategories.class)
                        .putExtra("category", "muzyka"));
                break;

            case R.id.angielski:
                startActivity(new Intent(CategoriesActivity.this, SelectCategories.class)
                        .putExtra("category", "angielski"));
                break;
        }
    }
}