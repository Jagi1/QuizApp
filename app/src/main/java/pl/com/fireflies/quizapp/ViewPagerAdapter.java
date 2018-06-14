package pl.com.fireflies.quizapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seba on 19.05.2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> list_fragment = new ArrayList<>();
    private final List<String> list_strings = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return list_fragment.get(position);
    }

    @Override
    public int getCount() {
        return list_strings.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list_strings.get(position);
    }

    public void AddFragment(Fragment fragment, String title) {
        list_fragment.add(fragment);
        list_strings.add(title);
    }
}
