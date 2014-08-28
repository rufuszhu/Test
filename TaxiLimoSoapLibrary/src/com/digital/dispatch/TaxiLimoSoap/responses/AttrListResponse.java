package com.digital.dispatch.TaxiLimoSoap.responses;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.serialization.PropertyInfo;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class AttrListResponse extends ResponseWrapper{
	private int numOfAttr = 0;
	private AttributeItem[] listOfAttr;
	private static String TAG = "Soap-AttrListRes";
	
	public AttrListResponse() {
		super();
	}
	
	public AttrListResponse(SoapObject soap) {
		super(soap);
		
		try {
			numOfAttr = Integer.parseInt(this.getProperty("nofAttributes").toString());
			listOfAttr = new AttributeItem[numOfAttr];
			
			int j = 0; 
			for (int i = 0; i < properties.size(); i ++) {
				Object item = ((PropertyInfo)properties.elementAt(i)).getValue();
				String objName = ((PropertyInfo)properties.elementAt(i)).getName();
				String aSN, aLN, aWB;

				if (item instanceof SoapObject) {
					if (objName.equalsIgnoreCase("listOfAttributes")) {
						aSN = ((SoapObject) item).getProperty("attrShortName").toString();
						aLN = ((SoapObject) item).getProperty("attrLongName").toString();
						aWB = ((SoapObject) item).getProperty("attrWebBooker").toString();
						
						if (GlobalVar.logEnable) {
							//Log.d(TAG, "aSN=" + aLN + " aLN=" + aLN + "aWB=" + aWB);
						}
						
						AttributeItem ai = new AttributeItem(aSN, aLN, aWB);
						listOfAttr[j] = ai;
						j++;
					}
				}
			}
		}
		catch (Exception e) {
			if (GlobalVar.logEnable) {
				Log.e(TAG, "ERROR: " + e.toString());
			}
		}
	}
	
	public AttributeItem[] GetList() {
		return listOfAttr;
	}
}