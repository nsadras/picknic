package com.picknic.android.scanner;

import com.picknic.android.R;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class HelpMeScanActivity extends Activity {
	
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_helpmescan);
		
		if (Build.VERSION.SDK_INT >= 13)
			getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public boolean onCreateOptionsMenu(Menu paramMenu) {
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
		
		switch (paramMenuItem.getItemId()){
			default:
				return super.onOptionsItemSelected(paramMenuItem);
			case 16908332: //home
				//do nothing for now
		}
		finish();
		return true;
	}
}