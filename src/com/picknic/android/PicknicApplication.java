package com.picknic.android;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class PicknicApplication extends Application {
	
	@Override
	public void onCreate(){
		Parse.initialize(this, "IpryyB7iGpSODvXUmtyodRRXh1VEfYoTfo6eCahe", "LFJfZFQAGJnFRqsNk427H9maYsN6T8dwKgsZ9uy0");
		ParseFacebookUtils.initialize(getString(R.string.app_id));
	}
}