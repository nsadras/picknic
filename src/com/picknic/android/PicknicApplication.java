package com.picknic.android;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class PicknicApplication extends Application {
	
	static final String TAG = "PicknicApp";
	@Override
	public void onCreate(){
		super.onCreate();
		Parse.initialize(this, "2QbO4m0NBhrXwznMA4tBabNf7c76R1obugfDSpFM", "4dFhCqIPJCo87BUVXPahVUJSvgcZNOQ6jwd25yeO");
		ParseFacebookUtils.initialize(getString(R.string.app_id));
	}
}