package pl.com.fireflies.quizapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class ChallengesActivity extends AppCompatActivity {
    android.support.design.widget.TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

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
        setContentView(R.layout.activity_challenges);

        /*
         * TODO TabLayout
         */
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_help);
        viewPager = (ViewPager) findViewById(R.id.view_pager_help);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.AddFragment(new ChallengeFragment(), "Current challenges");
        viewPagerAdapter.AddFragment(new HistoryFragment(), "History");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

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
}
