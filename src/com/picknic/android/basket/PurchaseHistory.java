package com.picknic.android.basket;

public class PurchaseHistory {
	private String provider;
	private String points;
	private String time;
    
	// Constructor for the PurchaseHistory class
	public PurchaseHistory(String provider, String points, String time) {
		super();
		this.provider = provider;
		this.points = points;
		this.time = time;
	}
    
	// Getter and setter methods for all the fields.
	//I may not be using these now, but its a good idea to have them for now.
	public String getProvider() {
		return this.provider;
	}
    
	public void setProvider(String provider) {
		this.provider = provider;
	}
   
	public String getPoints() {
		return this.points;
	}
   
	public void setPoints(String points) {
		this.points = points;
	}
    
	public String getTime() {
		return this.time;
	}
    
	public void setTime(String time) {
		this.time = time;
	}
}