package com.picknic.android.basket;

import java.util.List;

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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.picknic.android.MainActivity;
import com.picknic.android.R;
import com.picknic.android.scanner.QRCodeScannerActivity;

public class BasketMasterFragment extends Fragment{
	
	private Button scanButton;
	private Button purHistoryButton;
	private static TextView pointDisplay;
	private static MainActivity activity;
	private ParseUser user;
	String scanResult = "";
	String scanProvider = "";
	String scanPoints = "";
		
	private static final int PUR_HIST_REQUEST = 1;
	private static final int SCAN_REQUEST = 2;
	
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
        
        purHistoryButton = (Button) rootView.findViewById(R.id.btnPurHist);
        purHistoryButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onPurHistoryButtonClick(v);	
			}			
		});  
        return rootView;
    }
	
	 /**
     * Launch the Scan QR Code activity
     */
	private void onScanButtonClick(View v){
		// start QR scan activity
		startActivityForResult(new Intent(activity,QRCodeScannerActivity.class), SCAN_REQUEST);
	}

	 /**
     * Launch the Purchase History activity
     */
	private void onPurHistoryButtonClick(View v){
		// start purchase history activity
		startActivityForResult(new Intent(activity,PurchaseHistoryActivity.class), PUR_HIST_REQUEST);
	}
	
	/**
	 * Listen for results.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // See which child activity is calling us back.
	    switch (requestCode) {
	        case SCAN_REQUEST:
	            // This is the standard resultCode that is sent back if the activity crashed or didn't supply an explicit result.
	        	if (resultCode == Activity.RESULT_OK && data != null) {
	        		this.scanResult = data.getStringExtra("ScanResult");
	        		//this.scanResult = "Whole Foods Market|15"; //test
	        		
	        		boolean isFormatCorrect = ValidateAndParseScanResult(this.scanResult);
	        		
	        		if(isFormatCorrect) {
	        			pointDisplay.setText(this.scanPoints);
		        		
		        		//Update user points on parse
		        		ParseQuery<ParseUser> query = ParseUser.getQuery();
		        		query.getFirstInBackground(new GetCallback() {
		                    @Override
		                    public void done(ParseObject update, ParseException exception) {
		                        if(exception==null) {
		                        	
		                        	try {
		                        		//Get the previous points
		                        		int prevPoints = (Integer) ParseUser.getCurrentUser().get("points");
		                        		//Get scanned points
		                        		int currPoints = Integer.parseInt(scanPoints);	                        	
		                        	
		                        		ParseUser.getCurrentUser().put("points", prevPoints+=currPoints);
		                        		//Save locally if the connection is not available
		                        		ParseUser.getCurrentUser().saveEventually(); //Note: I may want to check the network conn before doing this.
		                        		//ParseUser.getCurrentUser().saveInBackground();  
		                        	}
	        		        		catch (NumberFormatException exp) {        		        			
	        		        			//Toast.makeText(activity, "This QR Code has no points info", Toast.LENGTH_SHORT).show();
	        		        			exp.printStackTrace();
	        		        		}
		                        }
		                        else {
		                        	exception.printStackTrace();
		                        }
		                    }
		                });
		        			        		
		        		//Set user points on parse
		        		ParseQuery<ParseObject> points_query = ParseQuery.getQuery("UserPoints");
		        		points_query.findInBackground(new FindCallback<ParseObject>() {
		        			public void done(List<ParseObject> objects, ParseException e) {
		        		        if (e == null) {
		        		        	ParseObject userPoints = new ParseObject("UserPoints");
		        		        	
		        		        	try {
		        		        		userPoints.put("username", ParseUser.getCurrentUser().getUsername());
		        		        		userPoints.put("provider", scanProvider);
		        		        		userPoints.put("points", Integer.parseInt(scanPoints));
			                        	//Save locally if the connection is not available
			        		        	userPoints.saveEventually(); //Note: I may want to check the network conn before doing this.
			        		        	//userPoints.saveInBackground();
		        		        	}
		        		        	catch (NumberFormatException exception) {
		        		        		Toast.makeText(activity, "Please scan a compatible Picknic QR code", Toast.LENGTH_SHORT).show();
		        		        		exception.printStackTrace();
		        		        	}
		        		        }
		        		        else {
		        		        	e.printStackTrace();
		        		        }
		        		    }
		        		});	        			
	        		}
	        		else {
	        			Toast.makeText(activity, "QR code format error!!", Toast.LENGTH_SHORT).show();
	        		}
	        	}
	        	else if (resultCode == Activity.RESULT_CANCELED) {
	        		Log.d("SCAN_REQUEST", "Operation Cancelled!!");
	        	}
	        	else {
	        		Log.d("SCAN_REQUEST", "Something went wrong!!");
	        	}
	        	break;
	        case PUR_HIST_REQUEST:
	        	//lets skip for now unless we want back any info here.
	            break;
	    }
	}

	
	/**
	 * Check the scan result string formatting. This is how we want the format to be:
	 * Example: "Whole Foods Market|15" where: Provider is "Whole Foods Market", Points is "15" 
	 * and "|" is the delimiter.
	 */
	private boolean ValidateAndParseScanResult(String strRes) {
		if(!strRes.isEmpty() && strRes.contains("|")) {
			String[] tokens = strRes.split("\\|");
			
			this.scanProvider=tokens[0];
			this.scanPoints=tokens[1];
			
			if(this.scanProvider.isEmpty() || this.scanPoints.isEmpty()) {
				Log.d("ValidateAndParseScanResult", "Empty string value");
				return false;
			}					
			return true;
		}
		return false;
	}
	
	/**
	 * Listen for events that would cause a change.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
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
