package pl.com.fireflies.quizapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ChallengesActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * findViewById dla tabLayout i viewPager zwraca null i program sie wywala nie wiem dlaczego.
         *
         * */
        /*
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_help);
        viewPager = (ViewPager) findViewById(R.id.view_pager_help);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.AddFragment(new ChallengeFragment(),"Current challenges");
        viewPagerAdapter.AddFragment(new HistoryFragment(),"History");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        */
    }
}
