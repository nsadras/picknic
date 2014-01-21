package com.picknic.android;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.picknic.android.popular.PopularDetailActivity;
import com.picknic.android.popular.PopularDetailFragment;
import com.picknic.android.popular.PopularListFragment;
import com.picknic.android.tabAdapter.TabPagerAdapter;

public class MainActivity extends FragmentActivity implements 
	ActionBar.TabListener, PopularListFragment.Callbacks{
	
	private ViewPager viewPager;
	private TabPagerAdapter mAdapter;
	private ActionBar actionBar;
	private boolean mTwoPane;
	public Dialog progressDialog;
	
	// tab labels
	private String[] tabs = { "Top Rated", "Newsfeed", "My Basket" }; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
	    	        ParseUser.getCurrentUser().put("fbId", user.getId());
	    	        ParseUser.getCurrentUser().put("name", user.getFirstName() + " " + user.getLastName());
	    	        ParseUser.getCurrentUser().saveInBackground();
	    	      }
			}
		}).executeAsync(); 
		
		viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabPagerAdapter(getSupportFragmentManager());
 
        viewPager.setAdapter(mAdapter);
        
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        
        // make tabs respond to swipes
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        	@Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
 
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        viewPager.setOffscreenPageLimit(3); // forces viewpager to load all 3 child fragments
        
        
        mTwoPane = getResources().getBoolean(R.bool.isTablet);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.action_logout:
				if(ParseFacebookUtils.getSession() != null){
					ParseFacebookUtils.getSession().closeAndClearTokenInformation();
				}
				ParseUser.logOut();
				showLoginActivity();
				return true;
			default:
				return super.onOptionsItemSelected(item);
				
		}
	}

	// TODO implement auto-generated methods
	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		// change view when tab is selected
		viewPager.setCurrentItem(tab.getPosition());
		
	}


	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Callback method from {@link PopularListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(PopularDetailFragment.ARG_ITEM_ID, id);
			PopularDetailFragment fragment = new PopularDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.event_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, PopularDetailActivity.class);
			detailIntent.putExtra(PopularDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		} 
	}
	
	private void showLoginActivity(){
		Intent intent = new Intent(this,LoginActivity.class);
    	startActivity(intent);
	}
}
