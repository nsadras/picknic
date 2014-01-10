package com.picknic.android;

import android.app.Activity;
import android.app.ProgressDialog;
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

public class BasketMasterFragment extends Fragment{
	
	private Button scanButton;
	private static TextView pointDisplay;
	private static MainActivity activity;
	private ParseUser user;

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		activity = (MainActivity) getActivity();
		View rootView = inflater.inflate(R.layout.fragment_basket, container, false);
		
		user = ParseUser.getCurrentUser();

		
		pointDisplay = (TextView) rootView.findViewById(R.id.points);
		user.refreshInBackground(new RefreshCallback() {
            
			@Override
            public void done(ParseObject user, ParseException err) {			
				pointDisplay.setText(Integer.toString(user.getInt("points")) + " points");
			}
			
		});
		Log.d("parse_debug", Boolean.toString(user.containsKey("points")));
		
		
		
		
		scanButton = (Button) rootView.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onScanButtonClick();	
			}
			
		});
        
        return rootView;
    }
	
	private void onScanButtonClick(){
		// start QR scan activity
		Log.d("debug","button pressed");
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
}
