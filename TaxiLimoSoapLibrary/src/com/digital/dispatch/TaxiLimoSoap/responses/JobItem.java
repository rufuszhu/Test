package com.digital.dispatch.TaxiLimoSoap.responses;

public class JobItem {
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
	}
	
	public JobItem(String pName, String phoneNum, String phoneExt, String pStreetNum, String pStreetName, String pRegion,
				String pUnitNum, String pLandmark, String dStreetNum, String dStreetName, String dRegion, String dUnitNum,
				String dLandmark, String aCode, String aName, String pDate, String pTime, String repPTime, String repPDays,
				String repSt, String repEn, String trID, String bDateTime, String tStatus, String aArrival, String dCar,
				String dDriver, String cLongitude, String cLatitude, String tStatCode, String dStatCode, 
				String e1, String e2, String eDis, String jEndTime) {
		
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
	}
}