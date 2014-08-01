package com.digital.dispatch.TaxiLimoSoap.responses;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class CancelJobResponse extends ResponseWrapper {
	public int taxiRideID;
	private static String TAG = "Soap-CancelJobRes";
	
	public CancelJobResponse() {
		super();
		taxiRideID = 0;
	}
	
	public CancelJobResponse(SoapObject soap) {
		super(soap);
		
		try {
			if (this.hasProperty("taxi_ride_id")) {
				taxiRideID = Integer.parseInt(this.getProperty("taxi_ride_id").toString());
			}
		}
		catch (Exception e) {
			if (GlobalVar.logEnable) {
				Log.e(TAG, "ERROR: " + e.toString());
			}
		}
	}
}