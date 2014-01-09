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
import com.picknic.android.MainActivity;
import com.picknic.android.PopularListFragment;
import com.picknic.android.PopularMasterFragment;
import com.picknic.android.content.RewardListContent.RewardItem;

public class NewsfeedContent {
	public static boolean dataLoaded = false;
	public static MainActivity activity;
	public static PopularMasterFragment list_fragment;
	/**
	 * An array of reward items.
	 */
	public static List<NewsfeedItem> ITEMS = new ArrayList<NewsfeedItem>();

	/**
	 * A map of reward items, by ID.
	 */
	public static Map<String, NewsfeedItem> ITEM_MAP = new HashMap<String, NewsfeedItem>();

	public static void setContent(Context context, PopularMasterFragment fragment){
		/*
		 *  TODO: write a parse query that finds all picknik user that are friends of the
		 *  current user, finds their transactions, and organizes them chronologically
		 */
		
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
	
	private static void addItem(NewsfeedItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * An item that shows up on the newsfeed
	 */
	public static class NewsfeedItem {
		public String id;
		public String user; // name of the person who claimed the deal
		public ParseObject deal;

		public NewsfeedItem(String id, String user, ParseObject deal) {
			this.id = id;
			this.user = user;
			this.deal = deal;
		}

		@Override
		public String toString() {
			return user + " claimed a deal from " + deal.getString("sponsor");
		}
	}
}
