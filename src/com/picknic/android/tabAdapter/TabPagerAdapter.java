package com.picknic.android.tabAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.picknic.android.basket.BasketMasterFragment;
import com.picknic.android.newsfeed.NewsfeedMasterFragment;
import com.picknic.android.popular.PopularMasterFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {
	
	public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }
	
	@Override
    public Fragment getItem(int index) {
        switch (index) {
        case 0:
            // Most Popular fragment activity
            return new PopularMasterFragment();
        case 1:
            // Newsfeed fragment activity
            return new NewsfeedMasterFragment();
        case 2:
            // My Basket (user info) fragment activity
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
