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


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    private ListView settings_list;
    private Switch theme_switch;
    private CardView help_cardView, info_card, set_card;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String PREF_VAR = "pref_vars";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DataHolder.getInstance().dark_theme) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_settings);
        initViews();

        theme_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DataHolder.getInstance().dark_theme = true;
                } else {
                    DataHolder.getInstance().dark_theme = false;
                    editor.putBoolean("dark_theme", false);
                }
                DataHolder.getInstance().theme_changed = true;
                editor.commit();
                recreate();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card5:
                new AlertDialog.Builder(this)
                        .setMessage("W razie jakichkolwiek pytań skontaktuj się z działem pomocy klienta pod adresem: bandurski.sebastian@gmail.com")
                        .create()
                        .show();
                break;
            case R.id.card4:
                new AlertDialog.Builder(this)
                        .setMessage("Aplikacja została stworzona jako projekt do zaliczenia przedmiotu.\nWykonali:\nSebastian Bandurski\nDaniel Biskup\nMateusz Błaszczak\n")
                        .create()
                        .show();
                break;
            case R.id.card1:
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                break;
        }
    }

    protected void initViews() {
        help_cardView = (CardView) findViewById(R.id.card5);
        set_card = (CardView) findViewById(R.id.card1);
        info_card = (CardView) findViewById(R.id.card4);
        theme_switch = (Switch) findViewById(R.id.theme);
        help_cardView.setOnClickListener(this);
        set_card.setOnClickListener(this);
        info_card.setOnClickListener(this);
        sharedPreferences = getSharedPreferences(PREF_VAR, 0);
        editor = sharedPreferences.edit();
        theme_switch.setChecked(sharedPreferences.getBoolean("dark_theme", false));

        if (DataHolder.getInstance().dark_theme) {
            theme_switch.setChecked(true);
        }
    }
}
