package com.picknic.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class BasketMasterFragment extends Fragment{
	
	private Button scanButton;
	private Activity activity;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		activity = getActivity();
		View rootView = inflater.inflate(R.layout.fragment_basket, container, false);
		
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
}
