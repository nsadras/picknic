package com.picknic.android.scanner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

public class QRCodeScannerActivity extends Activity {	
	
	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	private final int SCAN_TYPE_QR = 64;
	private final String SCAN_RESULT = "ScanResult";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.camera);
		
		if (isCameraAvailable()) {
            Intent intent = new Intent(this, ZBarScannerActivity.class);
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
            //intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE}); // This would restrict the scan to only QR codes
    		//startActivityForResult(intent, ZBAR_QR_SCANNER_REQUEST);
        } 
		else {
            Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
        }
	}

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if (resultCode == RESULT_OK) {
    		
    		switch (requestCode) {
	        	case ZBAR_SCANNER_REQUEST:
	            case ZBAR_QR_SCANNER_REQUEST:	            	
	            	String qrCode = data.getStringExtra(ZBarConstants.SCAN_RESULT);
	            	Integer qrCodeType = data.getIntExtra(ZBarConstants.SCAN_RESULT_TYPE,0);
	            		
	            	if (qrCodeType == SCAN_TYPE_QR) { //Scan QR code only
	            		//Toast.makeText(this, "Result = " + resultText, Toast.LENGTH_SHORT).show();
	            		Intent sendBackRes = new Intent();
	            		sendBackRes.putExtra(SCAN_RESULT, qrCode);
	            		setResult(RESULT_OK, sendBackRes);
	            		finish();
	            	}
	            	else {
	            		Toast.makeText(this, "Not a QR code. Scan only QR codes", Toast.LENGTH_SHORT).show();				
	        		}
	            break;
    		}
    	}
    	else if(resultCode == RESULT_CANCELED) {
    		if(data != null) {
            	String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
                
                if(!TextUtils.isEmpty(error)) {
                	Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }    			
    		}
    		else { // case where back button was pressed
    			Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();
    		}
    		finish();
        }    	    	
    }        
}