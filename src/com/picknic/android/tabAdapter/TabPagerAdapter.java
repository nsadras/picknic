package com.picknic.android.tabAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.picknic.android.TopMasterFragment;
import com.picknic.android.NewsfeedMasterFragment;
import com.picknic.android.BasketMasterFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {
	
	public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }
	
	@Override
    public Fragment getItem(int index) {
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new TopMasterFragment();
        case 1:
            // Games fragment activity
            return new NewsfeedMasterFragment();
        case 2:
            // Movies fragment activity
            return new BasketMasterFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
}
