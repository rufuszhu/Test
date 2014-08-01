package com.digital.dispatch.TaxiLimoSoap.responses;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class ResponseWrapper extends SoapObject {
	private int status = 0;
	private String errorString = "";
	private int errorCode= 0;
	private static String TAG = "Soap-Wrapper";
	
	private final String REQUEST_STATUS = "request_status";
	private final String ERROR_STRING = "error_string";
	private final String ERROR_CODE = "errorCode";
	
	public ResponseWrapper() {
		super();
	}
	
	public ResponseWrapper(SoapObject soap) {
		try {
			this.properties = soap.getAllProperty();
			this.name = soap.getName();
			this.namespace = soap.getNamespace();
			
			if ( this.hasProperty(REQUEST_STATUS)) {
				status = Integer.parseInt(this.getProperty(REQUEST_STATUS).toString());
			}
			
			if ( this.hasProperty(ERROR_STRING)) {
				errorString = this.getProperty(ERROR_STRING).toString();
			}
			
			if ( this.hasProperty(ERROR_CODE)) {
				errorCode = Integer.parseInt(this.getProperty(ERROR_CODE).toString());
			}
		}
		catch (Exception e) {
			status = 1; // fail to parse
			errorCode = 99; // fail to parse
			
			if (GlobalVar.logEnable) {
				Log.e(TAG, "ERROR: " + e.toString());
			}
		}
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getErrorString() {
		return errorString;
	}
	
	public int getErrCode()	{
		return errorCode;
	}
}
