package com.ashleyjain.class_pundit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by ashleyjain on 21/05/16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    int NumOfTabs;
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.NumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            filterfragment frag = new filterfragment();
            return frag;
        }
        if(position == 1){
            filterfragment frag1 = new filterfragment();
            return frag1;
        }
        if(position == 2){
            filterfragment frag2 = new filterfragment();
            return frag2;
        }
        return null;
    }

    @Override
    public int getCount() {
        return NumOfTabs;
    }

}
