package com.digital.dispatch.TaxiLimoSoap.responses;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class KeyExchangeResponse extends ResponseWrapper {
	private String deviceID, payload, plStatus, dhPublicKey, e3DESKey, desTimestamp;
	private static String TAG = "Soap-KeyExchangeRes";
	
	
	public KeyExchangeResponse() {
		super();
	}
	
	public KeyExchangeResponse(SoapObject soap) {
		super(soap);
		
		try {
			deviceID = this.getProperty("deviceID").toString();
			payload = this.getProperty("payload").toString();
			
	    	String[] payloadInfo = payload.split(GlobalVar.SEPERATOR);
	    	plStatus = payloadInfo[0];
	    	dhPublicKey = payloadInfo[1];
	    	e3DESKey = payloadInfo[2];
	    	desTimestamp = payloadInfo[3];
		}
		catch (Exception e) {
			if (GlobalVar.logEnable) {
				Log.e(TAG, "ERROR: " + e.toString());
			}
			
			plStatus = "19";
		}
	}
	
	public String GetDeviceID() {
		return deviceID;
	}
	
	public String GetPayload() {
		return payload;
	}
	
	public String GetPayloadStatus() {
		return plStatus;
	}
	
	public String GetDHPublicKey() {
		return dhPublicKey;
	}
	
	public String GetEncryted3DESKey() {
		return e3DESKey;
	}
	
	public String Get3DESKeyTimeStamp() {
		return desTimestamp;
	}
}
