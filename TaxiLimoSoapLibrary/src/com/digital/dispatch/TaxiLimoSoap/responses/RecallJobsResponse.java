package com.digital.dispatch.TaxiLimoSoap.responses;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.serialization.PropertyInfo;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class RecallJobsResponse extends ResponseWrapper {
	private int numOfJobs = 0;
	private JobItem[] listOfJobs;
	private static String TAG = "Soap-RecallJobsRes";

	public RecallJobsResponse() {
		super();
	}

	public RecallJobsResponse(SoapObject soap) {
		super(soap);

		try {
			numOfJobs = Integer.parseInt(this.getProperty("nofJobs").toString());
			listOfJobs = new JobItem[numOfJobs];

			int j = 0;
			for (int i = 0; i < properties.size(); i++) {
				Object item = ((PropertyInfo) properties.elementAt(i)).getValue();
				String objName = ((PropertyInfo) properties.elementAt(i)).getName();
				String pName, phoneNum, phoneExt, pStreetName, pRegion, pLandmark, dStreetName, dRegion, dLandmark;
				String aCode, aName, pDate, pTime, repPTime, repPDays, repSt, repEn, trID, bDateTime, tStatus;
				String aArrival, dCar, dDriver, cLongitude, cLatitude, tStatCode, dStatCode, e1, e2, eDis;
				String pStreetNum, pUnitNum, dStreetNum, dUnitNum, jobEndDateTime;

				if ((item instanceof SoapObject) && (objName.equalsIgnoreCase("listOfJobs"))) {
					pUnitNum = checkExistAndGet((SoapObject) item, "pickupUnitNumber");
					pStreetNum = checkExistAndGet((SoapObject) item, "pickupStreetNumber");
					dUnitNum = checkExistAndGet((SoapObject) item, "dropoffUnitNumber");
					dStreetNum = checkExistAndGet((SoapObject) item, "dropoffStreetNumber");
					pName = checkExistAndGet((SoapObject) item, "passengerName");
					phoneNum = checkExistAndGet((SoapObject) item, "phoneNumber");
					phoneExt = checkExistAndGet((SoapObject) item, "phoneExtension");
					pStreetName = checkExistAndGet((SoapObject) item, "pickupStreetName");
					pRegion = checkExistAndGet((SoapObject) item, "pickupRegion");
					pLandmark = checkExistAndGet((SoapObject) item, "pickupLandmark");
					dStreetName = checkExistAndGet((SoapObject) item, "dropoffStreetName");
					dRegion = checkExistAndGet((SoapObject) item, "dropoffRegion");
					dLandmark = checkExistAndGet((SoapObject) item, "dropffLandmark");
					aName = checkExistAndGet((SoapObject) item, "accountName");
					aCode = checkExistAndGet((SoapObject) item, "accountCode");
					pDate = checkExistAndGet((SoapObject) item, "pickupDate");
					pTime = checkExistAndGet((SoapObject) item, "pickupTime");
					repPTime = checkExistAndGet((SoapObject) item, "repPickupTime");
					repPDays = checkExistAndGet((SoapObject) item, "repPickupDays");
					repSt = checkExistAndGet((SoapObject) item, "repStart");
					repEn = checkExistAndGet((SoapObject) item, "repEnd");
					trID = checkExistAndGet((SoapObject) item, "taxiRideID");
					bDateTime = checkExistAndGet((SoapObject) item, "bookingDateTime");
					tStatus = checkExistAndGet((SoapObject) item, "tripStatus");
					aArrival = checkExistAndGet((SoapObject) item, "adviseArrival");
					dCar = checkExistAndGet((SoapObject) item, "dispatchedCar");
					dDriver = checkExistAndGet((SoapObject) item, "dispatchedDriver");
					cLongitude = checkExistAndGet((SoapObject) item, "carLongitude");
					cLatitude = checkExistAndGet((SoapObject) item, "carLatitude");
					tStatCode = checkExistAndGet((SoapObject) item, "tripStatusUniformCode");
					dStatCode = checkExistAndGet((SoapObject) item, "detailTripStatusUniformCode");
					e1 = checkExistAndGet((SoapObject) item, "eta1");
					e2 = checkExistAndGet((SoapObject) item, "eta2");
					eDis = checkExistAndGet((SoapObject) item, "estimateDistance");
					jobEndDateTime = checkExistAndGet((SoapObject) item, "jobEndDateTime");

					JobItem ji = new JobItem(pName, phoneNum, phoneExt, pStreetNum, pStreetName, pRegion, pUnitNum, pLandmark, dStreetNum, dStreetName, dRegion, dUnitNum, dLandmark, aCode, aName,
							pDate, pTime, repPTime, repPDays, repSt, repEn, trID, bDateTime, tStatus, aArrival, dCar, dDriver, cLongitude, cLatitude, tStatCode, dStatCode, e1, e2, eDis,
							jobEndDateTime);
					listOfJobs[j] = ji;
					j++;
				}
			}
		} catch (Exception e) {
			if (GlobalVar.logEnable) {
				Log.e(TAG, "ERROR: " + e.toString());
				e.printStackTrace();
			}
		}
	}

	public JobItem[] GetList() {
		return listOfJobs;
	}

	private String checkExistAndGet(SoapObject so, String propertyName) {
		if (so.hasProperty(propertyName)) {
			String temp = so.getProperty(propertyName).toString();
			if (temp.equalsIgnoreCase("anyType{}")) {
				return "";
			} else {
				return temp;
			}
		} else {
			return "";
		}
	}
}