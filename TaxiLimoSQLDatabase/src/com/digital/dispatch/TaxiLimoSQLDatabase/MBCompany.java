package com.digital.dispatch.TaxiLimoSQLDatabase;

import android.util.Log;

public class MBCompany {
	private final String TAG = "MBDatabase";
		
	private int id;
	private int destID;
	private String companyName;
	private String logoLocation;
	private String attributes;
	private int logoVersion;
	
	public MBCompany() {
		destID = 0;
		attributes = "";
		logoLocation = "";
		companyName = "";
		logoVersion = 0;
	}
	
	public MBCompany(int dID, String lLoc, String compName, String attrs, int lVer) {
		destID = dID;
		logoLocation = lLoc;
		companyName = compName;
		attributes = attrs;
		logoVersion = lVer;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getDestID() {
		return destID;
	}
	
	public void setDestID(int dID) {
		this.destID = dID;
	}
	
	public String getLogoLocation() {
		return logoLocation;
	}
	
	public void setLogoLocation(String lLoc) {
		this.logoLocation = lLoc;
	}
	
	public String getAttributes() {
		return attributes;
	}
	
	public void setAttributes(String attrs) {
		this.attributes = attrs;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String compName) {
		this.companyName = compName;
	}
	
	public int getLogoVersion() {
		return logoVersion;
	}
	
	public void setLogoVersion(int lVer) {
		this.logoVersion = lVer;
	}
	
	public void print() {	
		if (DatabaseHandler.dbDebug) {
			Log.v(TAG, "----MBCompany =" + id);
			Log.v(TAG, "   name = " + companyName);
			Log.v(TAG, "   destID = " + destID);
			Log.v(TAG, "   attributes = " + attributes);
			Log.v(TAG, "   logo location = " + logoLocation);
			Log.v(TAG, "   logo version = " + logoVersion);
			Log.v(TAG, "-------------------------------");
			Log.v(TAG, " ");
		}
	}
}