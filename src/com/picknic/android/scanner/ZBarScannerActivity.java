package com.picknic.android.scanner;

import com.picknic.android.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

public class ZBarScannerActivity extends Activity implements Camera.PreviewCallback, ZBarConstants {

    private CameraPreview mPreview;
    private Camera mCamera;
    private ImageScanner mScanner;
    private Handler mAutoFocusHandler;
    private boolean mPreviewing = true;
	public boolean run = false;
	String title = "";	
    AimView aimview = null;
    private Boolean backlight = Boolean.valueOf(false);
    private Button button;
    private Button button_help;
    private Integer flashException = Integer.valueOf(0);
    private Integer focusCount = Integer.valueOf(0);

    static {
        System.loadLibrary("iconv");
    }

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    //Handling autofocus
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if(mCamera != null && mPreviewing) {
                mCamera.autoFocus(autoFocusCB);
            }
            
            if(focusCount.intValue() > 45) {
            	Intent localIntent = new Intent();
            	localIntent.putExtra("SCAN_RESULT", "3");
            	localIntent.putExtra("SCAN_RESULT_TYPE", "sleepmode");
            	setResult(1, localIntent);
            	finish();
            }
            
            if (focusCount.intValue() == 2)
            	button_help.setVisibility(0);
            
            if (focusCount.intValue() > 40)
            	button_help.setVisibility(8);

            ZBarScannerActivity.this.focusCount = Integer.valueOf(1 + ZBarScannerActivity.this.focusCount.intValue());            
        }
	};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
        if(!isCameraAvailable()) {
	            cancelRequest(); //Cancel request if there is no rear-facing camera.
            return;
        }
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //if actionbar is not required
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_black_scanner)); 
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.BLUE));
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //if transparent actionbar is required       
	        
	        if (Build.VERSION.SDK_INT >= 13)
	            getActionBar().setDisplayHomeAsUpEnabled(true);
	        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

	        this.title = getIntent().getExtras().getString("title");
            if (!this.run) {
        mAutoFocusHandler = new Handler();

        // Create and configure the ImageScanner;
        setupScanner();

                // Draw a reticle on the preview screen
                DrawAimView();                
        
                setContentView( R.layout.overlay_control);

                // Preview overlay
                RelativeLayout frameLayout = (RelativeLayout) findViewById(R.id.camera_preview);
                // Create a RelativeLayout container that will hold a SurfaceView, and set it as the content of our activity.
                this.mPreview = new CameraPreview(this, this, autoFocusCB);
                this.mPreview.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));//test???
                this.mPreview.setBackgroundResource(R.drawable.black);
                frameLayout.addView(this.mPreview);
                frameLayout.addView(this.aimview);
                setTitle(this.title);
                this.button = ((Button)findViewById(R.id.flash));
                this.button.setText(getIntent().getExtras().getString("backlight"));
                String str = getIntent().getExtras().getString("button_help");
                this.button_help = ((Button)findViewById(R.id.scanHelp));
                SpannableString localSpannableString = new SpannableString(str);
                localSpannableString.setSpan(new UnderlineSpan(), 0, localSpannableString.length(), 0);
                this.button_help.setText(localSpannableString);
                RelativeLayout relativeLayoutControls = (RelativeLayout) findViewById(R.id.controls_layout);
                relativeLayoutControls.bringToFront(); 
            }
            this.run = true;
            return;        	
        }
        catch (Exception localException) {
        	localException.printStackTrace();
        	Intent localIntent = new Intent("android.intent.action.MAIN");
        	localIntent.setComponent(new ComponentName("com.picknic.android", "com.picknic.android.basket.BasketMasterFragment"));
        	localIntent.setFlags(67108864); //FLAG_ACTIVITY_CLEAR_TOP
        	startActivity(localIntent);
        }
    }

    @SuppressWarnings("deprecation")
	void DrawAimView() {
    	Display localDisplay = getWindowManager().getDefaultDisplay();
        Rect localRect = new Rect(0, 0, localDisplay.getWidth(), localDisplay.getHeight());
        
    	this.aimview = new AimView(this);
        this.aimview.SetPreviewRect(localRect);
        this.aimview.setVisibility(0);
    }

    public void setupScanner() {
        mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);

        int[] symbols = getIntent().getIntArrayExtra(SCAN_MODES);
        if (symbols != null) {
            mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
            for (int symbol : symbols) {
                mScanner.setConfig(symbol, Config.ENABLE, 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.focusCount = Integer.valueOf(0);
        this.flashException = Integer.valueOf(0);
        this.backlight = Boolean.valueOf(false);
        this.button_help.setVisibility(8);        
        try {
        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        if(mCamera == null) {
            // Cancel request if mCamera is null.
            cancelRequest();
            return;
        }
        mPreview.setCamera(mCamera);
        mPreview.showSurfaceView();
	        mPreviewing = true;

	        ((Button)findViewById(R.id.flash)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_no_flash), null, null);
	        getWindow().addFlags(128);
        }
        catch(Exception localException) {
        	localException.printStackTrace();
        	Intent localIntent = new Intent("android.intent.action.MAIN");
        	localIntent.setComponent(new ComponentName("com.picknic.android", "com.picknic.android.basket.BasketMasterFragment"));
        	localIntent.setFlags(67108864); //FLAG_ACTIVITY_CLEAR_TOP
        	startActivity(localIntent);
        }	        
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.cancelAutoFocus();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();

            // According to Jason Kuang on http://stackoverflow.com/questions/6519120/how-to-recover-camera-preview-from-sleep,
            // there might be surface recreation problems when the device goes to sleep. So lets just hide it and
            // recreate on resume
            mPreview.hideSurfaceView();
            mPreviewing = false;
            mCamera = null;
        }
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void cancelRequest() {
        Intent dataIntent = new Intent();
        dataIntent.putExtra(ERROR_INFO, "Camera unavailable");
        setResult(Activity.RESULT_CANCELED, dataIntent);
        finish();
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();

        Image barcode = new Image(size.width, size.height, "Y800");
        barcode.setData(data);

        int result = mScanner.scanImage(barcode);

        if (result != 0) {
            mCamera.cancelAutoFocus();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mPreviewing = false;
            SymbolSet syms = mScanner.getResults();
            for (Symbol sym : syms) {
                String symData = sym.getData();
                if (!TextUtils.isEmpty(symData)) {
                    Intent dataIntent = new Intent();
                    dataIntent.putExtra(SCAN_RESULT, symData);
                    dataIntent.putExtra(SCAN_RESULT_TYPE, sym.getType());
                    setResult(Activity.RESULT_OK, dataIntent);
                    finish();
                    break;
                }
            }
        }
    }
    
    // On Up button click (setDisplayHomeAsUpEnabled(true))
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { 
    	switch (item.getItemId()) {
    		case android.R.id.home: 
    			onBackPressed();
    			return true;
    	}
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * When backlight is OFF
     */
    private void backlightOFF(){
      
    	try {
    		if (getPackageManager().hasSystemFeature("android.hardware.camera.flash")) {
    			this.button.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_no_flash), null, null);
    			Camera.Parameters localParameters = this.mCamera.getParameters();
    			localParameters.setFlashMode("off");
    			this.mCamera.setParameters(localParameters);
    			this.backlight = Boolean.valueOf(false);
    			return;
    		}
    		Toast.makeText(this, "Flash not available", Toast.LENGTH_SHORT).show();    		
            }
    	catch (Exception localException) {
    		
    		do {
    			this.backlight = Boolean.valueOf(true);
    			localException.printStackTrace();
    		}while (this.flashException.intValue() >= 4);
    		backlightOFF();
    		this.flashException = Integer.valueOf(1 + this.flashException.intValue());
    	}
    }

    /**
     * When backlight is ON
     */
    private void backlightON() {
    	
    	try {
    		if (getPackageManager().hasSystemFeature("android.hardware.camera.flash")) {
    			this.button.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_flash), null, null);
    			Camera.Parameters localParameters = this.mCamera.getParameters();
    			localParameters.setFlashMode("torch");
    			this.mCamera.setParameters(localParameters);
    			this.backlight = Boolean.valueOf(true);
    			return;
    		}
    		Toast.makeText(this, "Flash not available", Toast.LENGTH_SHORT).show();
    	}
    	catch (Exception localException){
    		this.backlight = Boolean.valueOf(false);
    		
    		if (this.flashException.intValue() < 4){ // My retry logic
    			backlightON();
    			this.flashException = Integer.valueOf(1 + this.flashException.intValue());
    		}
    		localException.printStackTrace();
        }
    }
    
    /**
     * When backlight is clicked
     */
    public void backlight(View paramView) {
    	
    	if (!this.backlight.booleanValue()) {
    		this.flashException = Integer.valueOf(0);
    		backlightON();
    		return;
    	}
    	this.flashException = Integer.valueOf(0);
    	backlightOFF();
    }

    /**
     * When showHelp is clicked
     */
    public void showHelp(View paramView) {
    	startActivity(new Intent(this, HelpMeScanActivity.class));
        }
}
