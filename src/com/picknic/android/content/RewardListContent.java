package com.picknic.android.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.picknic.android.EventListActivity;
import com.picknic.android.EventListFragment;
import com.picknic.android.R;

/**
 * Class for fetching app content from Parse
 */
public class RewardListContent {
	
	public static boolean dataLoaded = false;
	public static EventListActivity activity;
	/**
	 * An array of reward items.
	 */
	public static List<RewardItem> ITEMS = new ArrayList<RewardItem>();

	/**
	 * A map of reward items, by ID.
	 */
	public static Map<String, RewardItem> ITEM_MAP = new HashMap<String, RewardItem>();

	public static void setContent(Context context){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Deal");
		activity = (EventListActivity) context;
		
		/**
		//synchronous version, use if asynchronous version explodes
		startLoading();
		try{
			List<ParseObject> dealList = query.find();
			int id = 1;
			for(ParseObject deal : dealList){
				Log.d("score", deal.getString("descShort"));
				RewardItem reward = new RewardItem(Integer.toString(id), deal.getString("descShort"), deal.getString("descLong"));
				addItem(reward);
				id++;
			}
		} catch (com.parse.ParseException e) {
			Log.d("reward", e.toString());
		}
		stopLoading();
		activity.notify();
		**/
		
		if(dataLoaded){ // check if list has already been updated
			return;
		}
		startLoading();
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> dealList, ParseException e) {
		       
		    	if (e == null) {
		            Log.d("score", "Retrieved " + dealList.size() + " deals");
		            int id = 1;
		            for(ParseObject deal : dealList){
			    		Log.d("score", deal.getString("descShort"));
			        	RewardItem reward = new RewardItem(Integer.toString(id), deal.getString("descShort"), deal.getString("descLong"));
			    		addItem(reward);
			        	id++;
			        }
		        } else {
		            Log.d("score", "Error: " + e.getMessage());
		        }
		    	stopLoading();
		    	dataLoaded = true;
		    	activity.recreate(); // creates annoying black flash, but is necessary for now
		    	
		    }
		});
		
	}
	

	// show progress dialog
	public static void startLoading() {
	    //progressDialog = new ProgressDialog(this);
	    activity.progressDialog = ProgressDialog.show(
				activity, "", "Fetching data...", true
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
		public String id;
		public String shortDesc;
		public String longDesc;

		public RewardItem(String id, String shortDesc, String longDesc) {
			this.id = id;
			this.shortDesc = shortDesc;
			this.longDesc = longDesc;
		}

		@Override
		public String toString() {
			return shortDesc;
		}
	}
}
