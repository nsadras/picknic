package com.picknic.android.basket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.picknic.android.R;

public class PurchaseHistoryActivity extends Activity {
		 
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listGroupData;
    Multimap<String, ArrayList<PurchaseHistory>> listItemData;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);
 
        if (Build.VERSION.SDK_INT >= 13)
			getActionBar().setDisplayHomeAsUpEnabled(true);
        
        //Get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
 
        //Populate list data from parse
        populateListDataFromParse(); 
    }
 
    /*
     * Populate list data from parse
     */
    private void populateListDataFromParse() {
    	
    	listGroupData = new ArrayList<String>();
    	listItemData = ArrayListMultimap.create();
    	
    	//Get user points from parse
		ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPoints");
		query.orderByDescending("createdAt");

		query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null) {
                	boolean isObjOk = (objects.size() == 0 ? false:true);
                	
                	if(isObjOk) {                		
                		int index = 0;
                    	for(ParseObject object: objects) {
                    		if(new String(object.get("username").toString()).equals(ParseUser.getCurrentUser().getUsername())) {
                    			
                    			final ArrayList<PurchaseHistory> listOfPurHistory = new ArrayList<PurchaseHistory>();
                    			try {
                    				String provider = (String) object.get("provider");
                    				int points = (Integer) object.get("points");                        		
                    				Date createdDate = object.getCreatedAt();                    			                        		
                    				String createdDateInString = createdDate.toString();
                    				
                    				//The date from parse is in this format - EEE MMM dd HH:mm:ss z yyyyy
                    				Calendar cal = Calendar.getInstance();
                    				cal.setTime(createdDate);
                    			        
                    				String fDay = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
                    				int fDayofMonth = cal.get(Calendar.DAY_OF_MONTH);
                    				String fMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                    				int fYear = cal.get(Calendar.YEAR);                    			        
                    				
                    				String formattedDate = fDay + ", " + fMonth + " " + fDayofMonth + ", " + fYear;
                    				Log.d("populateListDataFromParse", formattedDate);
                    				
                    				if (!listGroupData.contains(formattedDate)) 
                    					listGroupData.add(formattedDate); 
                    				else --index;
                    				
                    				PurchaseHistory purHist = new PurchaseHistory(provider, Integer.toString(points), createdDateInString);
                    				listOfPurHistory.add(purHist);
                            		listItemData.put(listGroupData.get(index), listOfPurHistory);
                            		index++;
                    			}
        		        		catch (NumberFormatException exp) {
        		        			exp.printStackTrace();
        		        		}
                    		}
                    	}
                	}
                	else {
    	        		Log.d("PUR_HIST_REQUEST", "User points not available!!");
    	        		ArrayList<PurchaseHistory> childItems = new ArrayList<PurchaseHistory>();
    	        		listGroupData.add("There are no points in your basket");
    	        		childItems.add(new PurchaseHistory("", "0", ""));
    	        		listItemData.put(listGroupData.get(0), childItems);
                	}
                	listAdapter = new com.picknic.android.basket.ExpandableListAdapter(PurchaseHistoryActivity.this, listGroupData, listItemData);
                        
                	// setting list adapter
                	expListView.setAdapter(listAdapter);
                 
                	// Listview Group click listener
                	expListView.setOnGroupClickListener(new OnGroupClickListener() {

                		@Override
                		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                			// Toast.makeText(getApplicationContext(), "Group Clicked " + listDataHeader.get(groupPosition), Toast.LENGTH_SHORT).show();
                			return false;
                		}
                	});
                 
                	// Listview Group expanded listener
                	expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
                		
                		@Override
                		public void onGroupExpand(int groupPosition) {
                			//Toast.makeText(getApplicationContext(), listGroupData.get(groupPosition) + " Expanded", Toast.LENGTH_SHORT).show();
                		}
                	});
                 
                	// Listview Group collapsed listener
                	expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
                 
                		@Override
                		public void onGroupCollapse(int groupPosition) {
                			//Toast.makeText(getApplicationContext(), listGroupData.get(groupPosition) + " Collapsed", Toast.LENGTH_SHORT).show();                
                		}
                	});
                 
                	// Listview on child click listener
                	expListView.setOnChildClickListener(new OnChildClickListener() {
                 
                		@Override
                		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                			//Toast.makeText( getApplicationContext(), listGroupData.get(groupPosition) + 
                			//		" : " + listItemData.get(listGroupData.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                			return false;
                		}
                	});
                }
                else {
                	e.printStackTrace();
                }
            }
        });
    }
    
    // On Up button click (setDisplayHomeAsUpEnabled(true))
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { 
    	switch (item.getItemId()) {
    		case android.R.id.home: 
    			onBackPressed();
    			return true;
    	}
        return super.onOptionsItemSelected(item);
    }
}
