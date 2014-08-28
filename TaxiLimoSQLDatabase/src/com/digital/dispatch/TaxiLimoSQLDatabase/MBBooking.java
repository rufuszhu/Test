package com.digital.dispatch.TaxiLimoSQLDatabase;

import android.util.Log;
//import com.digital.dispatch.mbsqldatabasev2.*;

public class MBBooking {
	private final String TAG = "MBDatabase";
	
    private int id;
    private int adviseArrival;
    private int asap;
    private int cabNum;
    private String authNum;
    private String carLatitude;
    private String carLongitude;
    private String dispatchedCar;
    private String dispatchedTime;
    private int jobID;
    private int numberOfPassengers;
    private int numTaxis;
    private String passengerName;
    private String phoneNumber;
    private String phoneExt;
    private String pickupTime;
    private int priority;
    private String priorityReason;
    private String remarks;
    private int taxiPIN;
    private int taxiRideID;
    private String tripCancelledTime;
    private String tripCreationTime;
    private String tripModificationTime;
    private String tripCompletionTime;
    private int tripStatus;
    private int tripType;
    private String paymentType;
    private int routeLink;
    private int bookingType;
    private int destID;
    private int sysID;
    private String attribute; // for TaxiLimo static attribute, save here instead attribute table
    private int already_paid;
    private int multi_pay_allow;
    
    public MBBooking()
    {
    	id = 0;
        adviseArrival = 0;
        asap = 1;
        cabNum = 1;
        authNum = "";
        carLatitude = "0";
        carLongitude = "0";
        dispatchedCar = "";
        dispatchedTime = "";
        jobID = 0;
        numberOfPassengers = 1;
        numTaxis = 1;
        passengerName = "";
        phoneNumber = "";
        phoneExt = "";
        pickupTime = "";
        priority = 0;
        priorityReason ="";
        remarks = null;
        taxiPIN = 0;
        taxiRideID = 0;
        tripCancelledTime = "";
        tripCreationTime = "";
        tripModificationTime = "";
        tripCompletionTime = "";
        tripStatus = 0;
        paymentType = "";
        tripType = 0;
        routeLink = 0;  
        bookingType = 0;
        destID = 0;
        sysID = 0;
        attribute = "";
        already_paid = 0;
        multi_pay_allow=0;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDestId() {
        return destID;
    }

    public void setDestId(int dID) {
        this.destID = dID;
    }
    
    public int getSysId() {
        return sysID;
    }

    public void setSysId(int sid) {
        this.sysID = sid;
    }
    
    // Static attribute String
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attr) {
        this.attribute = attr;
    }
    /* ------------------------- */
    
    public int getAdviseArrival() {
        return adviseArrival;
    }

    public void setAdviseArrival(int adviseArrival) {
        this.adviseArrival = adviseArrival;
    }

    public int getAsap() {
        return asap;
    }

    public void setAsap(int asap) {
        this.asap = asap;
    }

    public int getCabNum() {
        return cabNum;
    }

    public void setCabNum(int cabNum) {
        this.cabNum = cabNum;
    }

    public String getAuthNum() {
        return authNum;
    }

    public void setAuthNum(String authNum) {
        this.authNum = authNum;
    }

    public String getCarLatitude() {
        return carLatitude;
    }

    public void setCarLatitude(String carLatitude) {
        this.carLatitude = carLatitude;
    }

    public String getCarLongitude() {
        return carLongitude;
    }

    public void setCarLongitude(String carLongitude) {
        this.carLongitude = carLongitude;
    }

    public String getDispatchedCar() {
        return dispatchedCar;
    }

    public void setDispatchedCar(String dispatchedCar) {
        this.dispatchedCar = dispatchedCar;
    }

    public String getDispatchedTime() {
        return dispatchedTime;
    }

    public void setDispatchedTime(String dispatchedTime) {
        this.dispatchedTime = dispatchedTime;
    }

    public int getJobID() {
        return jobID;
    }

    public void setJobID(int jobID) {
        this.jobID = jobID;
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }

    public int getNumTaxis() {
        return numTaxis;
    }

    public void setNumTaxis(int numTaxis) {
        this.numTaxis = numTaxis;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneExt() {
        return phoneExt;
    }

    public void setPhoneExt(String phoneExt) {
        this.phoneExt = phoneExt;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getPriorityReason() {
        return priorityReason;
    }

    public void setPriorityReason(String priorityReason) {
        this.priorityReason = priorityReason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getTaxiPIN() {
        return taxiPIN;
    }

    public void setTaxiPIN(int taxiPIN) {
        this.taxiPIN = taxiPIN;
    }

    public int getTaxiRideID() {
        return taxiRideID;
    }

    public void setTaxiRideID(int taxiRideID) {
        this.taxiRideID = taxiRideID;
    }

    public String getTripCancelledTime() {
        return tripCancelledTime;
    }

    public void setTripCancelledTime(String tripCancelledTime) {
        this.tripCancelledTime = tripCancelledTime;
    }

    public String getTripCreationTime() {
        return tripCreationTime;
    }

    public void setTripCreationTime(String tripCreationTime) {
        this.tripCreationTime = tripCreationTime;
    }

    public String getTripModificationTime() {
        return tripModificationTime;
    }

    public void setTripModificationTime(String tripModificationTime) {
        this.tripModificationTime = tripModificationTime;
    }
    
    public String getTripCompletionTime() {
        return tripCompletionTime;
    }

    public void setTripCompletionTime(String tripCompTime) {
        this.tripCompletionTime = tripCompTime;
    }
    
    public int getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(int tripStatus) {
        this.tripStatus = tripStatus;
    }

    public int getTripType() {
        return tripType;
    }

    public void setTripType(int tripType) {
        this.tripType = tripType;
    }

    public int getRouteLink() {
        return routeLink;
    }

    public void setRouteLink(int routeLink) {
        this.routeLink = routeLink;
    }
    
    public String getPaymentType() {
		return paymentType;
	}
    
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	
	public int getBookingType() {
		return bookingType;
	}
	
	public void setBookingType(int bookingType) {
		this.bookingType = bookingType;
	}
		
	public void print()
	{		
		if (DatabaseHandler.dbDebug) {
			Log.v(TAG, "----MBBookingJob =" + id + "----");
			Log.v(TAG, "   adviseArrival =" + adviseArrival);
			Log.v(TAG, "   asap =" + asap);
			Log.v(TAG, "   cabNum =" + cabNum);
			Log.v(TAG, "   authNum =" + authNum);
			Log.v(TAG, "   carLatitude =" + carLatitude);
			Log.v(TAG, "   carLongitude =" + carLongitude);
			Log.v(TAG, "   dispatchedCar =" + dispatchedCar);
			Log.v(TAG, "   dispatchedTime =" + dispatchedTime);
			Log.v(TAG, "   jobID =" + jobID);
			Log.v(TAG, "   numberOfPassengers =" + numberOfPassengers);
			Log.v(TAG, "   numTaxis =" + numTaxis);
			Log.v(TAG, "   passengerName =" + passengerName);
			Log.v(TAG, "   adviseArrival =" + adviseArrival);
			Log.v(TAG, "   phoneNumber =" + phoneNumber);
			Log.v(TAG, "   phoneExt =" + phoneExt);
			Log.v(TAG, "   pickupTime =" + pickupTime);
			Log.v(TAG, "   priority =" + priority);
			Log.v(TAG, "   priorityReason =" + priorityReason);
			Log.v(TAG, "   remarks =" + remarks);
			Log.v(TAG, "   taxiPIN =" + taxiPIN);
			Log.v(TAG, "   taxiRideID =" + taxiRideID);
			Log.v(TAG, "   tripCancelledTime =" + tripCancelledTime);
			Log.v(TAG, "   tripCreationTime =" + tripCreationTime);
			Log.v(TAG, "   tripModificationTime =" + tripModificationTime);
			Log.v(TAG, "   tripCompletionTime =" + tripCompletionTime);
			Log.v(TAG, "   tripStatus =" + tripStatus);
			Log.v(TAG, "   tripType =" + tripType);
			Log.v(TAG, "   routeLink =" + routeLink);
			Log.v(TAG, "   bookingType =" + bookingType);
			Log.v(TAG, "   destID =" + destID);
			Log.v(TAG, "   sysID =" + sysID);
			Log.v(TAG, "   attributeList =" + attribute);
			Log.v(TAG, "   already_paid =" + already_paid);
			Log.v(TAG, "   multi_pay_allow =" + multi_pay_allow);
			Log.v(TAG, "-------------------------------");
		}
	}

	public int getAlready_paid() {
		return  already_paid;
	}

	public void setAlready_paid(int already_paid) {
		this.already_paid = already_paid;
	}

	public int getMulti_pay_allow() {
		return multi_pay_allow;
	}

	public void setMulti_pay_allow(int multi_pay_allow) {
		this.multi_pay_allow = multi_pay_allow;
	}

}
