package com.picknic.android.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.picknic.android.MainActivity;
import com.picknic.android.popular.PopularListFragment;
import com.picknic.android.popular.PopularMasterFragment;

/**
 * Class for fetching app content from Parse
 */
public class RewardListContent {
	
	public static boolean dataLoaded = false;
	public static MainActivity activity;
	public static PopularMasterFragment list_fragment;
	/**
	 * An array of reward items.
	 */
	public static List<RewardItem> ITEMS = new ArrayList<RewardItem>();

	/**
	 * A map of reward items, by ID.
	 */
	public static Map<String, RewardItem> ITEM_MAP = new HashMap<String, RewardItem>();

	public static void setContent(Context context, PopularMasterFragment fragment){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Deal");
		query.orderByDescending("numClaimed"); 	// order by popularity TODO: factor in how old the deal is
		activity = (MainActivity) context;
		list_fragment = fragment;

		if(dataLoaded){ // check if list has already been updated
			return;
		}
		startLoading();
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> dealList, ParseException e) {
		       
		    	if (e == null) {
		            int id = 1;
		            ParseUser user = ParseUser.getCurrentUser();
		            ParseRelation<ParseObject> dealsClaimedRelation = user.getRelation("deals");
		            List<ParseObject> dealsClaimed; 
		            
		            // get list of claimed deals
		            try{
		            	dealsClaimed = dealsClaimedRelation.getQuery().find(); // do synchronously because we're already in an async callback
		            } catch(Exception err){
		            	Log.d("relation query callback error", err.toString());
		            	return;
		            }
		            
		            boolean claimed;
		            for(ParseObject deal : dealList){
			    		Log.d("original deal", deal.toString());
			    		claimed = false;
			    		// crude n^2 solution, but it works
			    		for(ParseObject claimedDeal : dealsClaimed){
			    			if(deal.hasSameId(claimedDeal)){ // check if already claimed
			    				claimed = true;
			    				break;
			    			}
			    		}
			    		RewardItem reward = new RewardItem(Integer.toString(id), claimed, deal);
			    		addItem(reward);
			        	id++;
			        }
		        } else {
		            Log.d("RewardListContent error", "Error: " + e.getMessage());
		        }
		    	stopLoading();
		    	dataLoaded = true;
		    	//PopularListFragment listFragment = (PopularListFragment) (list_fragment.getChildFragmentManager().findFragmentByTag("popular_list"));
		    	PopularListFragment listFragment = (PopularListFragment) (list_fragment.getFragmentManager().findFragmentByTag("popular_list"));
		    	
		    	// refresh listFragment
		    	listFragment.setListAdapter(new ArrayAdapter<RewardListContent.RewardItem>(activity,
						android.R.layout.simple_list_item_activated_1,
						android.R.id.text1, RewardListContent.ITEMS));		    	
		    }
		});
		
	}
	

	// show progress dialog
	public static void startLoading() {
	    activity.progressDialog = ProgressDialog.show(
				activity, "", "Loading...", true
		);
	}

	// dismiss progress dialog
	public static void stopLoading() {
	    activity.progressDialog.dismiss();
	    activity.progressDialog = null;
	}
	
	private static void addItem(RewardItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * A reward that can be claimed by the user
	 */
	public static class RewardItem {
		public ParseObject deal;
		public String id;
		public boolean claimed;

		public RewardItem(String id, boolean claimed, ParseObject deal ) {
			this.deal = deal;
			this.id = id;
			this.claimed = claimed;
		}

		@Override
		public String toString() {
			return deal.getString("sponsor") + ": " + deal.getString("descShort"); 
		}

	}
}
