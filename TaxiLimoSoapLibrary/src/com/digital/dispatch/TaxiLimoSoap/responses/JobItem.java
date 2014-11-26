package com.digital.dispatch.TaxiLimoSoap.responses;

import android.util.Log;


public class JobItem {
	private static final String TAG = "JobItem";
	public String passengerName;
	public String phoneNumber;
	public String phoneExtension;
	public String pickupStreetNumber;
	public String pickupStreetName;
	public String pickupRegion;
	public String pickupUnitNumber;
	public String pickupLandmark;
	public String dropoffStreetNumber;
	public String dropoffStreetName;
	public String dropoffRegion;
	public String dropoffUnitNumber;
	public String dropffLandmark;
	public String accountCode;
	public String accountName;
	public String pickupDate;
	public String pickupTime;
	public String repPickupTime;
	public String repPickupDays;
	public String repStart;
	public String repEnd;
	public String taxi_ride_id;
	public String bookingDateTime;
	public String tripStatus;
	public String adviseArrival;
	public String dispatchedCar;
	public String dispatchedDriver;
	public String carLongitude ;
	public String carLatitude ;
	public String tripStatusUniformCode;
	public String detailTripStatusUniformCode;
	public String eta1;
	public String eta2;
	public String estimateDistance;
	public String jobEndDateTime;
	public String redispatchJobID;
	
	public JobItem() {
		passengerName = "";
		phoneNumber = "";
		phoneExtension = "";
		pickupStreetNumber = "";
		pickupStreetName = "";
		pickupRegion = "";
		pickupUnitNumber = "";
		pickupLandmark = "";
		dropoffStreetNumber = "";
		dropoffStreetName = "";
		dropoffRegion = "";
		dropoffUnitNumber = "";
		dropffLandmark = "";
		accountCode = "";
		accountName = "";
		pickupDate = "";
		pickupTime = "";
		repPickupTime = "";
		repPickupDays = "";
		repStart = "";
		repEnd = "";
		taxi_ride_id = "";
		bookingDateTime = "";
		tripStatus = "";
		adviseArrival = "";
		dispatchedCar = "";
		dispatchedDriver = "";
		carLongitude  = "";
		carLatitude  = "";
		tripStatusUniformCode = "";
		detailTripStatusUniformCode = "";
		eta1 = "";
		eta2 = "";
		estimateDistance = "";
		jobEndDateTime = "";
		redispatchJobID = "";
	}
	
	public JobItem(String pName, String phoneNum, String phoneExt, String pStreetNum, String pStreetName, String pRegion,
				String pUnitNum, String pLandmark, String dStreetNum, String dStreetName, String dRegion, String dUnitNum,
				String dLandmark, String aCode, String aName, String pDate, String pTime, String repPTime, String repPDays,
				String repSt, String repEn, String trID, String bDateTime, String tStatus, String aArrival, String dCar,
				String dDriver, String cLongitude, String cLatitude, String tStatCode, String dStatCode, 
				String e1, String e2, String eDis, String jEndTime, String redispatchJobID) {
		
		passengerName = pName;
		phoneNumber = phoneNum;
		phoneExtension = phoneExt;
		pickupStreetNumber = pStreetNum;
		pickupStreetName = pStreetName;
		pickupRegion = pRegion;
		pickupUnitNumber = pUnitNum;
		pickupLandmark = pLandmark;
		dropoffStreetNumber = dStreetNum;
		dropoffStreetName = dStreetName;
		dropoffRegion = dRegion;
		dropoffUnitNumber = dUnitNum;
		dropffLandmark = dLandmark;
		accountCode = aCode;
		accountName = aName;
		pickupDate = pDate;
		pickupTime = pTime;
		repPickupTime = repPTime;
		repPickupDays = repPDays;
		repStart = repSt;
		repEnd = repEn;
		taxi_ride_id = trID;
		bookingDateTime = bDateTime;
		tripStatus = tStatus;
		adviseArrival = aArrival;
		dispatchedCar = dCar;
		dispatchedDriver = dDriver;
		carLongitude  = cLongitude;
		carLatitude  = cLatitude;
		tripStatusUniformCode = tStatCode;
		detailTripStatusUniformCode = dStatCode;
		eta1 = e1;
		eta2 = e2;
		estimateDistance = eDis;
		jobEndDateTime = jEndTime;
		this.redispatchJobID = redispatchJobID;
	}

	
	public static void printJobItem(JobItem jobItem){
		
		Log.i(TAG,"passengerName: " +jobItem.passengerName);
		Log.i(TAG,"phoneNumber: " +jobItem.phoneNumber);
		Log.i(TAG,"phoneExtension: " + jobItem.phoneExtension);
		Log.i(TAG,"pickupStreetNumber: " + jobItem.pickupStreetNumber);
		Log.i(TAG,"pickupStreetName: " + jobItem.pickupStreetName);
		Log.i(TAG,"pickupRegion: " + jobItem.pickupRegion); 
		Log.i(TAG,"pickupUnitNumber: " + jobItem.pickupUnitNumber);
		Log.i(TAG,"dropoffStreetNumber: " + jobItem.dropoffStreetNumber);
		Log.i(TAG,"dropoffStreetName: " + jobItem.dropoffStreetName);
		Log.i(TAG,"dropoffRegion: " + jobItem.dropoffRegion);
		Log.i(TAG,"dropoffUnitNumber: " + jobItem.dropoffUnitNumber);
		Log.i(TAG,"dropffLandmark: " + jobItem.dropffLandmark);
		Log.i(TAG,"accountCode: " + jobItem.accountCode);
		Log.i(TAG,"accountName: " + jobItem.accountName);
		Log.i(TAG,"pickupDate: " + jobItem.pickupDate);
		Log.i(TAG,"pickupTime: " + jobItem.pickupTime);	
		Log.i(TAG,"repPickupTime: " + jobItem.repPickupTime);
		Log.i(TAG,"repPickupDays: " + jobItem.repPickupDays);
		Log.i(TAG,"repStart: " + jobItem.repStart);	
		Log.i(TAG,"repEnd: " + jobItem.repEnd);
		Log.i(TAG,"taxi_ride_id: " + jobItem.taxi_ride_id);
		Log.i(TAG,"bookingDateTime: " + jobItem.bookingDateTime);
		Log.i(TAG,"tripStatus: " + jobItem.tripStatus);
		Log.i(TAG,"adviseArrival: " + jobItem.adviseArrival);
		Log.i(TAG,"dispatchedCar: " + jobItem.dispatchedCar);
		Log.i(TAG,"dispatchedDriver: " + jobItem.dispatchedDriver);
		Log.i(TAG,"carLongitude: " + jobItem.carLongitude);
		Log.i(TAG,"carLatitude: " + jobItem.carLatitude);	
		Log.i(TAG,"tripStatusUniformCode: " + jobItem.tripStatusUniformCode);
		Log.i(TAG,"detailTripStatusUniformCode: " + jobItem.detailTripStatusUniformCode);	
		Log.i(TAG,"eta1: " + jobItem.eta1);
		Log.i(TAG,"eta2: " + jobItem.eta2);
		Log.i(TAG,"estimateDistance: " + jobItem.estimateDistance);
		Log.i(TAG,"jobEndDateTime: " + jobItem.jobEndDateTime);
		Log.i(TAG,"redispatchJobID: " + jobItem.redispatchJobID);
		Log.i(TAG, "----------------------------------------------");
	}
}