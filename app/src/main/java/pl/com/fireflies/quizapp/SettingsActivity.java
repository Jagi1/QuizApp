package pl.com.fireflies.quizapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, Switch.OnCheckedChangeListener
{
    public static final String PREF_VAR = "pref_vars";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) setTheme(R.style.DarkAppTheme);
        else setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_settings);
        if (DataHolder.getInstance().dark_theme) getWindow().setBackgroundDrawableResource(R.drawable.background_dark);
        else getWindow().setBackgroundDrawableResource(R.drawable.background);
        initViews();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.card5:
                if (DataHolder.getInstance().dark_theme)
                {
                    new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                            .setMessage(getString(R.string.help_message,"bandurski.sebastian@gmail.com"))
                            .create()
                            .show();
                }
                else
                {
                    new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
                            .setMessage(getString(R.string.help_message,"bandurski.sebastian@gmail.com"))
                            .create()
                            .show();
                }
                break;

            case R.id.card4:
                if (DataHolder.getInstance().dark_theme)
                {
                    new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                            .setMessage(getString(R.string.info_message,"\nSebastian Bandurski","\nDaniel Biskup","\nMateusz Błaszczak"))
                            .create()
                            .show();
                }
                else
                {
                    new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
                            .setMessage(getString(R.string.info_message,"\nSebastian Bandurski","\nDaniel Biskup","\nMateusz Błaszczak"))
                            .create()
                            .show();
                }
                break;

            case R.id.card1:
                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setData(Uri.fromParts("package", getPackageName(), null)));
                break;
        }
    }

    protected void initViews() {
        ArrayList<CardView> cards = new ArrayList<CardView>()
        {{
            add((CardView) findViewById(R.id.card5));
            add((CardView) findViewById(R.id.card1));
            add((CardView) findViewById(R.id.card4));
            add((CardView) findViewById(R.id.card2));
        }};
        for (CardView card : cards) card.setOnClickListener(this);
        Switch theme_switch = (Switch) findViewById(R.id.theme);
        theme_switch.setChecked(getSharedPreferences(PREF_VAR, 0).getBoolean("dark_theme", false));
        theme_switch.setOnCheckedChangeListener(this);
        if (DataHolder.getInstance().dark_theme) theme_switch.setChecked(true);
        if(DataHolder.getInstance().dark_theme)
        {
            cards.get(0).setCardBackgroundColor(getColor(R.color.colorMaterialDarkRed));
            cards.get(1).setCardBackgroundColor(getColor(R.color.colorMaterialDark1));
            cards.get(2).setCardBackgroundColor(getColor(R.color.colorMaterialDark2));
            cards.get(3).setCardBackgroundColor(getColor(R.color.colorMaterialDarkYellow));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        switch (buttonView.getId())
        {
            case R.id.theme:
                if (isChecked)
                {
                    DataHolder.getInstance().dark_theme = true;
                    getSharedPreferences(PREF_VAR, 0).edit().putBoolean("dark_theme",true).apply();
                }
                else
                {
                    DataHolder.getInstance().dark_theme = false;
                    getSharedPreferences(PREF_VAR, 0).edit().putBoolean("dark_theme", false).apply();
                }
                DataHolder.getInstance().theme_changed = true;
                recreate();
                break;
        }
    }
}
