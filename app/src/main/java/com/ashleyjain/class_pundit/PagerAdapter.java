package com.ashleyjain.class_pundit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ashleyjain on 21/05/16.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
        {
            // find first fragment...
            kidsfragment ft1 = new kidsfragment();
            return ft1;
        }
        if (position == 1)
        {
            // find first fragment...
            adultsfragment ft2 = new adultsfragment();
            return ft2;
        }
        else if (position == 2)
        {
            // find first fragment...
            petsfragment ft3 = new petsfragment();
            return ft3;
        }

        return null;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "KIDS";
            case 1:
                return "ADULTS";
            case 2:
                return "PETS";
        }
        return null;
    }
}