package com.digital.dispatch.TaxiLimoSoap.responses;

import android.util.Log;



public class CompanyItem {
	private static final String TAG = "CompanyItem";
	public String destID;
	public String name;
	public String logo;
	public int logoVersion;
	public String description;
	public String attributes;
	public int systemID;
	public int baseRate;
	public int ratePerDistance;
	public String multiPay;
	
	public CompanyItem() {
		destID = "";
		name = "";
		logo = "";
		logoVersion = 0;
		description = "";
		attributes = "";
		systemID = 0;
		baseRate = 0;
		ratePerDistance = 0;
		multiPay = "";
	}
	
	public CompanyItem(String dID, String cName, String cLogo, int cLogoVer, String desc, String attr, int sysID, int bRate, int rPerDist, String cmultiPay) {
		destID = dID;
		name = cName;
		logo = cLogo;
		logoVersion = cLogoVer;
		description = desc;
		attributes = attr;
		systemID = sysID;
		baseRate = bRate;
		ratePerDistance = rPerDist;
		multiPay = cmultiPay;
	}
	
	public static void printCompanyItem(CompanyItem item){
		Log.e(TAG, "destId: " + item.destID);
		Log.e(TAG, "name: " + item.name);
		Log.e(TAG, "logo: " + item.logo);
		Log.e(TAG, "logoVersion: " + item.logoVersion);
		Log.e(TAG, "description: " + item.description);
		Log.e(TAG, "attributes: " + item.attributes);
		Log.e(TAG, "systemID: " + item.systemID);
		Log.e(TAG, "baseRate: " + item.baseRate);
		Log.e(TAG, "ratePerDistance: " + item.ratePerDistance);
		Log.e(TAG, "multiPay: " +  item.multiPay);
		Log.e(TAG, "--------------------------------");
	}
}
