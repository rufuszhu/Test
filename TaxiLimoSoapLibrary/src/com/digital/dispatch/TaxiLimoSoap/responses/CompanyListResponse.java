package com.digital.dispatch.TaxiLimoSoap.responses;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.serialization.PropertyInfo;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class CompanyListResponse extends ResponseWrapper {
	private int numOfCompany = 0;
	private CompanyItem[] listOfCompany;
	private static String TAG = "Soap-CompanyListRes";
	
	public CompanyListResponse() {
		super();
	}
	
	public CompanyListResponse(SoapObject soap) {
		super(soap);
		
		try {
			numOfCompany = Integer.parseInt(this.getProperty("nrOfDestinations").toString());
			listOfCompany = new CompanyItem[numOfCompany];
			
			int j = 0; 
			for (int i = 0; i < properties.size(); i ++) {
				Object item = ((PropertyInfo)properties.elementAt(i)).getValue();
				String objName = ((PropertyInfo)properties.elementAt(i)).getName();
				String cDestID, cName, cLogo, cDesc, cAttr, multiPay, carFile, dupChkTime, ccPayEnable, phoneNr;
				int cLogoVersion, cBaseRate, cRatePerDist, cSysID;

				if (item instanceof SoapObject) {
					if (objName.equalsIgnoreCase("listOfDestinations")) {
						cDestID = ((SoapObject) item).getProperty("destID").toString();
						
						cName = ((SoapObject) item).getProperty("name").toString();
						cLogo = ((SoapObject) item).getProperty("logo").toString();
						cDesc = checkExistAndGet((SoapObject) item, "desc");
						cSysID = Integer.parseInt(((SoapObject) item).getProperty("mbSystemID").toString());
						multiPay = checkExistAndGet((SoapObject) item, "multiPay");
						
						cLogoVersion = Integer.parseInt(checkExistAndGet((SoapObject) item, "logoVersion"));
						cBaseRate = Integer.parseInt(checkExistAndGet((SoapObject) item, "baseRate"));
						cRatePerDist = Integer.parseInt(checkExistAndGet((SoapObject) item, "ratePerDistance"));
						cAttr = checkExistAndGet((SoapObject) item, "attributes");
						carFile = checkExistAndGet((SoapObject) item, "carFile");
						dupChkTime = checkExistAndGet((SoapObject) item, "dupChkTime");
						ccPayEnable = checkExistAndGet((SoapObject) item, "ccPayEnable");
						phoneNr = checkExistAndGet((SoapObject) item, "phoneNr");
						
						if (GlobalVar.logEnable) {
							Log.v(TAG, "destID=" + cDestID + ", SystemID=" + cSysID + ", name=" + cName + ", logo=" + cLogo 
									+ "\n attributes=" + cAttr + ", description=" + cDesc);
						}
						
						CompanyItem ci = new CompanyItem(cDestID, cName, cLogo, cLogoVersion, cDesc, cAttr, cSysID, cBaseRate, cRatePerDist, multiPay,carFile,dupChkTime,ccPayEnable, phoneNr);
						listOfCompany[j] = ci;
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
	
	public CompanyItem[] GetList() {
		return listOfCompany;
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