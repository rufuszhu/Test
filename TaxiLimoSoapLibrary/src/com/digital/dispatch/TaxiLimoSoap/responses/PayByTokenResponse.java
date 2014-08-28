package com.digital.dispatch.TaxiLimoSoap.responses;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class PayByTokenResponse extends ResponseWrapper {
	private String seqNum, response, authorizationCode, referenceNum, email;
	private int resCode;
	private boolean emailSent;
	private static String TAG = "Soap-PayByTokenRes";
	
	public PayByTokenResponse() {
		super();
	}
	
	public PayByTokenResponse(SoapObject soap) {
		super(soap);
		
		try {
	    	seqNum = this.getProperty("seqNo").toString();
	    	resCode = Integer.parseInt(this.getProperty("rspCode").toString());
	    	response = checkExistAndGet(this, "rspMsg", false);
	    	authorizationCode = checkExistAndGet(this, "authCode", false);
	    	referenceNum = checkExistAndGet(this, "refNo", false);
	    	email = checkExistAndGet(this, "email", false);
	    	String tmpEmailSent = checkExistAndGet(this, "emailSent", false);
	    	
	    	if (tmpEmailSent.equalsIgnoreCase("true")) {
	    		emailSent = true;
	    	}
	    	else {
	    		emailSent = false;
	    	}
		}
		catch (Exception e) {
			resCode = 7; // declined by verbiage is used as the generic error code
			
			if (GlobalVar.logEnable) {
				Log.e(TAG, "ERROR: " + e.toString());
			}
		}
	}
	
	public boolean GetEmailSent() {
		return emailSent;
	}
	
	public String GetEmail() {
		return email;
	}
	
	public String GetSequenceNum() {
		return seqNum;
	}
	
	public String GetResponse() {
		return response;
	}
	
	public String GetAuthorizationCode() {
		return authorizationCode;
	}
	
	public String GetReferenceNum() {
		return referenceNum;
	}
	
	public int GetResponseCode() {
		return resCode;
	}
	
	private String checkExistAndGet(SoapObject so, String propertyName, boolean isInt) {
		if (so.hasProperty(propertyName)) {
			String temp = so.getProperty(propertyName).toString();
			if (temp.equalsIgnoreCase("anyType{}")) {
				return "";
			}
			else {
				return temp;
			}
		}
		else {
			if (isInt) {
				return "-1";
			}
			else {
				return "";
			}
		}
	}
}