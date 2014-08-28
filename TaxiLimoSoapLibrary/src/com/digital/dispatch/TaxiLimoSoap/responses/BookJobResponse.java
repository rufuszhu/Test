package com.digital.dispatch.TaxiLimoSoap.responses;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.serialization.PropertyInfo;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class BookJobResponse extends ResponseWrapper {
	private int taxi_ride_id = 0;
	private int nofJobs = 0;
	private int fareEstimate = 0;
	private String confirmationNum = "";
	private String eta1 = "";
	private String eta2 = "";
	private JobIDListItem[] taxiJobIDList;
	private static String TAG = "Soap-BookJobRes";
	
	public BookJobResponse() {
		super();
	}
	
	public BookJobResponse(SoapObject soap) {
		super(soap);
		
		try {
			taxi_ride_id = Integer.parseInt(checkExistAndGet(this, "taxi_ride_id", true));
			fareEstimate = Integer.parseInt(checkExistAndGet(this, "fareEstimate", true));
			nofJobs = Integer.parseInt(checkExistAndGet(this, "nofJobs", true));
			confirmationNum = checkExistAndGet(this, "confirmationNum", true);
			eta1 = checkExistAndGet(this, "eta1", false);
			eta2 = checkExistAndGet(this, "eta2", false);
			if ( nofJobs == -1)
			{
				nofJobs = 1;
			}
			taxiJobIDList = new JobIDListItem[nofJobs];
			
			int j = 0;
			for (int i = 0; i < properties.size(); i ++) {
				Object item = ((PropertyInfo)properties.elementAt(i)).getValue();
				String objName = ((PropertyInfo)properties.elementAt(i)).getName();
				String e1, e2;
				int tfID, fareEst;
				
				if (GlobalVar.logEnable) {
					Log.v(TAG, "objName=" + objName + "  i="+i);
				}
				
				if (item instanceof SoapObject) {
					e1 = checkExistAndGet((SoapObject) item, "eta1", false);
					e2 = checkExistAndGet((SoapObject) item, "eta2", false);
					tfID = Integer.parseInt(checkExistAndGet((SoapObject) item, "taxi_ride_id", true));
					fareEst = Integer.parseInt(checkExistAndGet((SoapObject) item, "fareEstimate", true));
					
					JobIDListItem jli = new JobIDListItem(tfID, fareEst, e1, e2);
					taxiJobIDList[j] = jli;
					j++;
				}
			}
		}
		catch (Exception e) {
			if (GlobalVar.logEnable) {
				Log.e(TAG, "ERROR: " + e.toString());
			}
		}
	}
	
	public JobIDListItem[] GetList() {
		return taxiJobIDList;
	}
	
	public int getTRID() {
		return taxi_ride_id;
	}
	
	public int getFareEst() {
		return fareEstimate;
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