package com.picknic.android;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	private Button loginButton;
    private Dialog progressDialog;       
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    
        //Set parse analytics as a tracker for picknic
        ParseAnalytics.trackAppOpened(getIntent());
        
        if (isConnectedToNetwork()) {        	
        	//lets check if the user has already logged in.
        	Context context=this.getApplicationContext();
        	SharedPreferences settings=context.getSharedPreferences("myPreference", 0);
        	boolean isLogged = settings.getBoolean("isLogged", false);
        	if(isLogged && (ParseUser.getCurrentUser() != null)) {
        		//Usr is already logged in
        		showMainActivity();
        	}
        	else {
                loginButton = (Button) findViewById(R.id.loginButton);               
                loginButton.setOnClickListener(new View.OnClickListener() {        			
        			@Override
        			public void onClick(View v) {
        				onLoginButtonClick();	
        			}        			
        		});
                
                ParseUser currentUser = ParseUser.getCurrentUser();
                if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
                	// Go to the main activity
                	showMainActivity();
                }
        	}           
        }
        else {
        	// There is no network connection
        	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
        	.setTitle("No Connection")
        	.setMessage("No Internet Connection detected. Make sure you are connected to the Internet!!!")
			.setCancelable(false)
			.setNegativeButton("Settings",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, open the settings to make appropriate changes.
					Intent settingsIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
					startActivity(settingsIntent);
				}
			})
			.setPositiveButton("Quit",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close the current activity
					LoginActivity.this.finish();
				}
			});
			// Create alert dialog and show
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
        }
    }
    
    /*
     * Check if network connection is available, bail out if no connection.
     */
	public boolean isConnectedToNetwork() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        
		if (connectivity == null) {
			return false;
        } 
		else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
        return false;
	}
    
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
            
            Context context=this.getApplicationContext();
            SharedPreferences settings=context.getSharedPreferences("myPreference", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isLogged", false);
            editor.commit();
    }

    private void onLoginButtonClick() {
        LoginActivity.this.progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);
        List<String> permissions = Arrays.asList("basic_info", "user_about_me", "user_relationships", "user_birthday", "user_location");
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
        	
        	@Override
        	public void done(ParseUser user, ParseException err) {
                        
				try {
        			LoginActivity.this.progressDialog.dismiss();
				} 
				catch (Exception e) {
                	// nothing
				}
        		
        		if (user == null) {
        			Log.d(PicknicApplication.TAG, "Uh oh. The user cancelled the Facebook login.");
					Log.d(PicknicApplication.TAG,"Code " + err.getMessage());
        		}
        		else if (user.isNew()) {
        			Log.d(PicknicApplication.TAG, "User signed up and logged in through Facebook!");
					showMainActivity();
        		}
        		else {
        			Log.d(PicknicApplication.TAG, "User logged in through Facebook!");
					showMainActivity();
        		}
        	}
        });
        Context context=this.getApplicationContext();
        SharedPreferences settings=context.getSharedPreferences("myPreference", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isLogged", true);
        editor.commit();
    }

    /**
     * Launch the Main Activity
     */
    private void showMainActivity(){
    	Intent intent = new Intent(this,MainActivity.class);
    	startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
    
}
