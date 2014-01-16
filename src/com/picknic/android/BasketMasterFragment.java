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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.picknic.android.scanner.QRCodeScannerActivity;

public class BasketMasterFragment extends Fragment{
	
	private Button scanButton;
	private static TextView pointDisplay;
	private static MainActivity activity;
	private ParseUser user;

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
	            public void done(ParseObject user, ParseException err) {			
					pointDisplay.setText(Integer.toString(user.getInt("points")) + " points");
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
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // See which child activity is calling us back.
	    switch (requestCode) {
	        case MY_REQUEST_CODE:
	            // This is the standard resultCode that is sent back if the activity crashed or didn't supply an explicit result.
	        	if (resultCode == Activity.RESULT_OK && data != null) {
	        		String result = data.getStringExtra("ScanResult");
	        		pointDisplay.setText(result);	        		
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
