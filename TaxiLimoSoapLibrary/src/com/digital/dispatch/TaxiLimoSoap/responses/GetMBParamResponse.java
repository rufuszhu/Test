package com.digital.dispatch.TaxiLimoSoap.responses;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.serialization.PropertyInfo;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class GetMBParamResponse extends ResponseWrapper {
	private int errCode;
	private MGParam params;
	private HashMap<String, String> paramHM;
	private ArrayList<AttributeItem> attributeList;
	private static String TAG = "Soap-GetMBParamRes";
	
	
	public static String BASE_RATE = "C_MB_BASE_RATE";
	public static String RATE_PER_DIST = "C_MB_RATE_PER_DIST";
	public static String SND_MSG_DRV = "C_MB_SND_MSG_DRV"; 
	public static String DROP_OFF_MANDATORY = "C_MB_DRP_OFF_MAND";
	public static String MULTI_BOOK_ALLOWED = "C_MB_MLT_BOOK_ALWD";
	public static String SAME_LOG_BOOK_ALLOWED = "C_MB_SAME_LOC_BK_ALW";
	public static String USE_ACCT_PW = "C_MB_USE_ACCPW"; 
	public static String CC_PAYMENT = "C_MB_CC_PAMNT_ENABLE"; 
	public static String TIP_BUTTON1 = "C_MB_TIP_BUTTON1";
	public static String PAYMENT_TMOUT = "C_MB_PAYMNT_TMOUT";

	
	public GetMBParamResponse() {
		super();
		attributeList = new ArrayList<AttributeItem>();
	}
	
	public GetMBParamResponse(SoapObject soap) {
		super(soap);
		attributeList = new ArrayList<AttributeItem>();
		try {
			paramHM = new HashMap<String, String>();
			//errCode = Integer.parseInt(this.getProperty("errorCode").toString());
			for (int i = 0; i < properties.size(); i ++) {
				Object item = ((PropertyInfo)properties.elementAt(i)).getValue();
				String objName = ((PropertyInfo)properties.elementAt(i)).getName();
				
				if ((item instanceof SoapObject) && (objName.equalsIgnoreCase("listOfParameters"))) {
					paramHM.put(checkExistAndGet((SoapObject)item, "parameterName"), 
							checkExistAndGet((SoapObject)item, "parameterValue"));
					
					if (GlobalVar.logEnable) {
						Log.e(TAG, "name: " + checkExistAndGet((SoapObject)item, "parameterName") + 
								", value: " + checkExistAndGet((SoapObject)item, "parameterValue"));
					}
				}
				
				if ((item instanceof SoapObject) && (objName.equalsIgnoreCase("listOfAttributes"))) {
					AttributeItem attr = new AttributeItem(checkExistAndGet((SoapObject)item, "Id"),
							checkExistAndGet((SoapObject)item, "name"),
							checkExistAndGet((SoapObject)item, "appIconId"));
					
					attributeList.add(attr);
					if (GlobalVar.logEnable) {
						Log.e(TAG, "id: "+ checkExistAndGet((SoapObject)item, "Id") + 
									", name: " + checkExistAndGet((SoapObject)item, "name") + 
									", value: " + checkExistAndGet((SoapObject)item, "appIconId"));
					}
				}
			}
			
			
			params = new MGParam(null, null, paramHM.get(SND_MSG_DRV), 
					paramHM.get(DROP_OFF_MANDATORY), paramHM.get(MULTI_BOOK_ALLOWED), paramHM.get(SAME_LOG_BOOK_ALLOWED),
					null, null, paramHM.get(TIP_BUTTON1), paramHM.get(PAYMENT_TMOUT));	

		}
		catch (Exception e) {
			if (GlobalVar.logEnable) {
				Log.e(TAG, "ERROR: " + e.toString());
			}
		}
	}
	
	public int GetErrCode() {
		return errCode;
	}
	
	public HashMap<String, String> GetParamList() {
		return paramHM;
	}
	
	public MGParam GetParams() {
		return params;
	}
	
	public ArrayList<AttributeItem> getAttributeList(){
		return attributeList;
	}
	
	private String checkExistAndGet(SoapObject so, String propertyName) {
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
			return "";
		}
	}
}
