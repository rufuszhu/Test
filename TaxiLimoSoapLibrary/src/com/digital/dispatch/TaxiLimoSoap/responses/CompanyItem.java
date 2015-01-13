package com.digital.dispatch.TaxiLimoSoap.responses;

import java.io.Serializable;

import android.util.Log;



public class CompanyItem implements Serializable{
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
	public String carFile;
	public String dupChkTime;
	public String ccPayEnable;
	public String phoneNr;
	
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
		this.carFile = "";
		this.dupChkTime = "";
		this.ccPayEnable = "";
		phoneNr = "";
	}
	
	public CompanyItem(String dID, String cName, String cLogo, int cLogoVer, String desc, String attr, int sysID, int bRate, int rPerDist, String cmultiPay, String carFile, String dupChkTime, String ccPayEnable, String phoneNr) {
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
		this.carFile = carFile;
		this.dupChkTime = dupChkTime;
		this.ccPayEnable = ccPayEnable;
		this.phoneNr = phoneNr;
	}
	
	public static void printCompanyItem(CompanyItem item){
		Log.d(TAG, "destId: " + item.destID);
		Log.d(TAG, "name: " + item.name);
		Log.d(TAG, "logo: " + item.logo);
		Log.d(TAG, "logoVersion: " + item.logoVersion);
		Log.d(TAG, "description: " + item.description);
		Log.d(TAG, "attributes: " + item.attributes);
		Log.d(TAG, "systemID: " + item.systemID);
		Log.d(TAG, "baseRate: " + item.baseRate);
		Log.d(TAG, "ratePerDistance: " + item.ratePerDistance);
		Log.d(TAG, "multiPay: " +  item.multiPay);
		Log.d(TAG, "carFile: " +  item.carFile);
		Log.d(TAG, "dupChkTime: " +  item.dupChkTime);
		Log.d(TAG, "ccPayEnable: " +  item.ccPayEnable);
		Log.d(TAG, "phoneNr: " +  item.phoneNr);
		Log.d(TAG, "--------------------------------");
	}
}
