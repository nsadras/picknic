package com.picknic.android.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.picknic.android.MainActivity;
import com.picknic.android.newsfeed.NewsfeedMasterFragment;

public class NewsfeedContent {
	public static boolean dataLoaded = false;
	public static MainActivity activity;
	public static NewsfeedMasterFragment list_fragment;
	/**
	 * An array of reward items.
	 */
	public static List<NewsfeedItem> ITEMS = new ArrayList<NewsfeedItem>();

	/**
	 * A map of reward items, by ID.
	 */
	public static Map<String, NewsfeedItem> ITEM_MAP = new HashMap<String, NewsfeedItem>();

	public static void setContent(Context context, NewsfeedMasterFragment fragment){
		
		/*
		 *  TODO: write a parse query that finds all picknic user that are friends of the
		 *  current user, finds their transactions, and organizes them chronologically
		 */
		if(dataLoaded){
			return;
		}
		list_fragment = fragment;
		activity = (MainActivity) context;
		//addItem(new NewsfeedItem("1", "Bob"));
		//addItem(new NewsfeedItem("2", "Bill"));
		//addItem(new NewsfeedItem("3", "Lisa"));
 
		
		Session session = ParseFacebookUtils.getSession();
		if(session != null && session.isOpened()){
			Log.d("newsfeed", "open session");
			Log.d("newsfeed", session.toString());
			makeFriendRequest(session);
		} else {
			Log.d("newsfeed", "null or closed session");
		}
		
		
	}
	
	private static void makeFriendRequest(Session session){
		Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
			
			@Override
			public void onCompleted(List<GraphUser> users, Response response) {
				Log.d("newsfeed", response.toString());
				if (users != null) {
					  Log.d("newsfeed", "friendlist not null. hooray");
					  List<String> friendsList = new ArrayList<String>();
				      for (GraphUser user : users) {
				        friendsList.add(user.getId());
				      }

				      // Construct a ParseUser query that will find friends whose
				      // facebook IDs are contained in the current user's friend list.
				      ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
				      friendQuery.whereContainedIn("fbId", friendsList);

				      // findObjects will return a list of ParseUsers that are friends with
				      // the current user
				      try{
				    	  List<ParseUser> friendUsers = friendQuery.find();
				    	  int id = 0;
				    	  for(ParseUser friend : friendUsers){
				    		  //GO THROUGH THE TRANSACTIONS FOR EACH FRIEND AND ADD IT AS A NEWSFEEDITEM
				    		  List<ParseObject> transactions = friend.getRelation("transactions").getQuery().find();
				    		  
				    		  for (ParseObject transaction : transactions) {
				    			  ParseUser tempUser = transaction.getParseUser("user");
//				    			  Log.d("DEBUG", "TRANSACTION'S USER'S NAME: " + tempUser.getString("name"));

				    			  NewsfeedItem news = new NewsfeedItem(Integer.toString(id), 
				    					  transaction.getParseUser("user").fetchIfNeeded().getString("name"),
				    					  transaction.getParseObject("deal").fetchIfNeeded());

				    			  addItem(news);
				    			  Log.d("newsfeed", "found friend");
				    			  id++;
				    		  }
				    	  }
				    	  
				      } catch(ParseException e){
				    	  Log.d("newsfeed", e.toString());
				      }
				      //NewsfeedListFragment listFragment = (NewsfeedListFragment) (list_fragment.getFragmentManager().findFragmentByTag("newsfeed_list"));
				    	
			    	// refresh listFragment
				      dataLoaded = true;
				     if(list_fragment != null){ 
				    	 list_fragment.setListAdapter(new ArrayAdapter<NewsfeedContent.NewsfeedItem>(activity,
							android.R.layout.simple_list_item_activated_1,
							android.R.id.text1, NewsfeedContent.ITEMS));
				     }
				}
			}
			
		}).executeAsync();
		
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

		public NewsfeedItem(String id, String user) {
			this.id = id;
			this.user = user;
		}
		
		//NEW CONSTRUCTOR TO INCLUDE DEAL
		public NewsfeedItem(String id, String user, ParseObject deal) {
			this.id = id;
			this.user = user;
			this.deal = deal;
		}
		
		/**
		public NewsfeedItem(String id, ParseObject transaction) {
			this.id = id;
			this.user = transaction.getString("user");
			this.deal = transaction.getParseObject("deal");
		}**/

		@Override
		public String toString() {
			return user + " claimed a deal from " + deal.getString("sponsor");
		}
	}
}
