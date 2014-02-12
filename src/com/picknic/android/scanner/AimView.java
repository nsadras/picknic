package com.picknic.android.scanner;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import java.util.Vector;

public final class AimView extends View {
	static Bitmap m_pBitmap = null;
	static Canvas m_pCanvas = null;
	static Paint m_pPaint = null;
	Activity activity = null;
	int m_Color = 0;
	Rect m_RectView = null;
	Vector<AimVector> m_ViewfinderVectors = null;
	int m_nViewfinderPercentSize = 0;

	public AimView(Activity paramActivity) {
		super(paramActivity);
		this.activity = paramActivity;
		SetViewfinder();
	}

	public void SetPreviewRect(Rect paramRect) {
		this.m_RectView = new Rect(paramRect);
		
		Runnable local1 = new Runnable() {
			public void run() {
				try {
					AimView.this.layout(AimView.this.m_RectView.left, AimView.this.m_RectView.top, AimView.this.m_RectView.right, AimView.this.m_RectView.bottom);
					return;
				}
				catch (Throwable localThrowable) {
					localThrowable.printStackTrace();
				}
			}
		};
		this.activity.runOnUiThread(local1);
	}

	void SetViewfinder() {
		try {
			if (this.m_ViewfinderVectors == null) {
				this.m_ViewfinderVectors = new Vector<AimVector>();
				this.m_ViewfinderVectors.add(new AimVector(0, 0, 3, 20));
				this.m_ViewfinderVectors.add(new AimVector(0, 40, 3, 20));
				this.m_ViewfinderVectors.add(new AimVector(0, 80, 3, 20));
				this.m_ViewfinderVectors.add(new AimVector(97, 0, 3, 20));
				this.m_ViewfinderVectors.add(new AimVector(97, 40, 3, 20));
				this.m_ViewfinderVectors.add(new AimVector(97, 80, 3, 20));
				this.m_ViewfinderVectors.add(new AimVector(0, 0, 20, 3));
				this.m_ViewfinderVectors.add(new AimVector(40, 0, 20, 3));
				this.m_ViewfinderVectors.add(new AimVector(80, 0, 20, 3));
				this.m_ViewfinderVectors.add(new AimVector(0, 97, 20, 3));
				this.m_ViewfinderVectors.add(new AimVector(40, 97, 20, 3));
				this.m_ViewfinderVectors.add(new AimVector(80, 97, 20, 3));
				this.m_ViewfinderVectors.add(new AimVector(0, 48, 10, 3));
				this.m_ViewfinderVectors.add(new AimVector(90, 48, 10, 3));
				this.m_ViewfinderVectors.add(new AimVector(48, 0, 3, 10));
				this.m_ViewfinderVectors.add(new AimVector(48, 90, 3, 10));
			}
			this.m_nViewfinderPercentSize = 70;
			this.m_Color = 13185586;
			return;
		}
		catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	public void onDraw(Canvas paramCanvas) {
		
		try {
			super.onDraw(paramCanvas);
			
			if ((this.m_nViewfinderPercentSize > 0) 
					&& (this.m_ViewfinderVectors != null) 
					&& (this.m_ViewfinderVectors.size() > 0) 
					&& (this.m_RectView != null)) {
		
				int i = this.m_RectView.width();
				
				if (i > this.m_RectView.height()) {
					i = this.m_RectView.height();
				}
				int j = i * this.m_nViewfinderPercentSize / 100;
				int k = 0xFF000000 | this.m_Color;
				m_pPaint.setColor(k);
				m_pPaint.setStyle(Paint.Style.FILL);
        
				for (int m = 0; ; m++) {
					if (m >= this.m_ViewfinderVectors.size()) {
						paramCanvas.drawBitmap(m_pBitmap, 0.0F, 0.0F, m_pPaint);
						return;
					}
					AimVector localAimVector = (AimVector)this.m_ViewfinderVectors.elementAt(m);
					int n = this.m_RectView.left;
					int i1 = this.m_RectView.top;
					int i2 = this.m_RectView.right - this.m_RectView.left;
					int i3 = this.m_RectView.bottom - this.m_RectView.top;
					int i4 = n + i2 / 2 - j / 2 + j * localAimVector.m_nX / 100;
					int i5 = i1 + i3 / 2 - j / 2 + j * localAimVector.m_nY / 100;
					int i6 = n + i2 / 2 - j / 2 + j * (localAimVector.m_nX + localAimVector.m_nW) / 100;
					int i7 = i1 + i3 / 2 - j / 2 + j * (localAimVector.m_nY + localAimVector.m_nH) / 100;
					paramCanvas.drawRect(i4, i5, i6, i7, m_pPaint);
				}
			}
		}
		catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
		
		try {
			if ((m_pBitmap == null) || (m_pBitmap.getWidth() != paramInt1) || (m_pBitmap.getHeight() != paramInt2)) {
				m_pBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
				m_pCanvas = new Canvas(m_pBitmap);
				m_pPaint = new Paint();
			}
			return;
		}
		catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
	
		switch (paramMotionEvent.getAction()) {
			default:
			case 0:
		}
		while (true) {
			return true;
			//((ZBarScannerActivity)this.activity).AutoFocus();
		}
	}

	class AimVector {
		public int m_nH = 0;
		public int m_nW = 0;
		public int m_nX = 0;
		public int m_nY = 0;

		public AimVector(int paramInt1, int paramInt2, int paramInt3, int arg5) {
			this.m_nX = paramInt1;
			this.m_nY = paramInt2;
			this.m_nW = paramInt3;
			int i = arg5;
			this.m_nH = i;
		}
	}
}