package com.picknic.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.picknic.android.scanner.QRCodeScannerActivity;

public class BasketMasterFragment extends Fragment{
	
	private Button scanButton;
	private static TextView pointDisplay;
	private static MainActivity activity;
	private ParseUser user;
	String scanResult;
	
	private static final int MY_REQUEST_CODE = 2;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		activity = (MainActivity) getActivity();
		View rootView = inflater.inflate(R.layout.fragment_basket, container, false);		
		user = ParseUser.getCurrentUser();		
		pointDisplay = (TextView) rootView.findViewById(R.id.points);
		
		if(user != null) {
			user.refreshInBackground(new RefreshCallback() {
	            
				@Override
	            public void done(ParseObject user, ParseException exception) {	
					if(exception == null) {
						pointDisplay.setText(Integer.toString(user.getInt("points")) +" points");	
					}
					else {
						//Print the stack
						exception.printStackTrace();
					}
				}				
			});
			Log.d("parse_debug", Boolean.toString(user.containsKey("points")));
		}
		
		scanButton = (Button) rootView.findViewById(R.id.btnScan);
        scanButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onScanButtonClick(v);	
			}
			
		});        
        return rootView;
    }
	
	 /**
     * Launch the Scan QR Code activity
     */
	private void onScanButtonClick(View v){
		// start QR scan activity
		startActivityForResult(new Intent(activity,QRCodeScannerActivity.class), MY_REQUEST_CODE);
	}
	
	/**
	 * Listen for results.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // See which child activity is calling us back.
	    switch (requestCode) {
	        case MY_REQUEST_CODE:
	            // This is the standard resultCode that is sent back if the activity crashed or didn't supply an explicit result.
	        	if (resultCode == Activity.RESULT_OK && data != null) {
	        		scanResult = data.getStringExtra("ScanResult");
	        		pointDisplay.setText(scanResult);
	        		
	        		ParseQuery<ParseUser> query = ParseUser.getQuery();
	        		query.getFirstInBackground(new GetCallback() {
	                    @Override
	                    public void done(ParseObject update, ParseException exception) {
	                        if(exception==null) {
	                        	//Get the previous points
	                        	int prevPoints = (Integer) ParseUser.getCurrentUser().get("points");
	                        	int currPoints = Integer.parseInt(scanResult);
	                        	ParseUser.getCurrentUser().put("points", prevPoints+=currPoints);
	                        	ParseUser.getCurrentUser().saveInBackground();                	                    
	                        }
	                        else {
	                        	exception.printStackTrace();
	                        }
	                    }
	                });        		
	        	}
	        	else if (resultCode == Activity.RESULT_CANCELED) {
	        		Log.d("resultCode", "Operation Cancelled!!");
	        	}
	        	else {
	        		Log.d("resultCode", "Something went wrong!!");
	        	}
	        	break;
	        default:
	            break;
	    }
	}
	
	/**
	 * Listen for events that would cause a change.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  //setContentView(R.layout.fragment_basket);
	}
		
	/**
	 * show progress dialog.
	 */
	public static void startLoading() {
	    activity.progressDialog = ProgressDialog.show(activity, "", "Loading...", true);
	}

	/**
	 * dismiss progress dialog.
	 */
	public static void stopLoading() {
	    activity.progressDialog.dismiss();
	    activity.progressDialog = null;
	}
}
