package com.digital.dispatch.TaxiLimoSoap.requests;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.DataParam;
import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.HandleReqResTask;
import com.digital.dispatch.TaxiLimoSoap.MethodEnum;
import com.digital.dispatch.TaxiLimoSoap.RequestEnum;
import com.digital.dispatch.TaxiLimoSoap.SoapTypeWrapper;
import com.digital.dispatch.TaxiLimoSoap.HandleReqResTask.ICustomResponseListener;
import com.digital.dispatch.TaxiLimoSoap.responses.BookJobResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

public class BookJobRequest extends Request {
	private String systemID, deviceID, taxi_company_id, taxi_ride_id, request_type, phonenum, phoneext, passenger_name;
	private String number_of_passenger, numTaxis, adviseArrival, email, type, priority, pickup_house_number;
	private String pickup_street_name, pickup_district, pickup_latitude, pickup_longitude, pickup_time;
	private String pickup_unit,	pickup_landmark, dropoff_house_number, dropoff_street_name, dropoff_district;
	private String dropoff_unit, dropoff_landmark, dropoff_longitude, dropoff_latitude, accountCode, accountPassword;
	private String authNum, remarks, priorityReason, cabNum, flatRate, rep_pickup_time;
	private String repSuspendStart, repSuspendEnd, repStart, repEnd, forcedAddressFlag, ospVersion;
	private String zipCode;
	private List<String> attributeList;
	private IBookJobResponseListener iResponseListener = null;
	private static String TAG = "Soap-BookJob";
	
	public BookJobRequest(IBookJobResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
	}
	
	public void setSysID(String sysID) {
		systemID = sysID;
	}
	
	public void setTaxiCompanyID(String compID) {
		taxi_company_id = compID;
	}
	
	public void setHardwareID(String hID) {
		deviceID = hID;
	}
	
	public void setTaxiRideID(String trID) {
		taxi_ride_id = trID;
	}
	
	public void setReqType(String trID) {
		request_type = trID;
	}
	
	public void setPassengerName(String name) {
		passenger_name = name;
	}
	
	public void setPhoneNum(String num) {
		phonenum = num;
	}
	
	public void setPhoneExt(String ext) {
		phoneext = ext;
	}
	
	public void setNumOfPassenger(String pNum) {
		number_of_passenger = pNum;
	}
	
	public void setNumOfTaxi(String tNum) {
		numTaxis = tNum;
	}
	
	public void setAdviseArrival(String aa) {
		adviseArrival = aa;
	}
	
	public void setEmail(String e) {
		email = e;
	}
	
	public void setType(String t) {
		type = t;
	}
	
	public void setPriority(String p) {
		priority = p;
	}
	
	public void setPickupHouseNum(String pHouseNum) {
		pickup_house_number = pHouseNum;
	}
	
	public void setPickUpStreetName(String pStreetName) {
		pickup_street_name = pStreetName;
	}
	
	public void setPickUpDistrict(String pDistrict) {
		pickup_district = pDistrict;
	}
	
	public void setPickUpLongitude(String plongitude) {
		pickup_longitude = plongitude;
	}
	
	public void setPickUpLatitude(String pLatitude) {
		pickup_latitude = pLatitude;
	}
	
	public void setPickUpTime(String pTime) {
		pickup_time = pTime;
	}
	
	public void setPickUpUnit(String pUnit) {
		pickup_unit = pUnit;
	}
	
	public void setPickUpLandmark(String pLandmark) {
		pickup_landmark = pLandmark;
	}
	
	public void setDropOffHouseNum(String dHouseNum) {
		dropoff_house_number = dHouseNum;
	}
	
	public void setDropOffStreetName(String dStreetName) {
		dropoff_street_name = dStreetName;
	}
	
	public void setDropOffDistrict(String dDistrict) {
		dropoff_district = dDistrict;
	}
	
	public void setDropOffUnit(String dUnit) {
		dropoff_unit = dUnit;
	}
	
	public void setDropOffLandmark(String dLandmark) {
		dropoff_landmark = dLandmark;
	}
	
	public void setDropOffLongitude(String dLongitude) {
		dropoff_longitude = dLongitude;
	}
	
	public void setDropOffLatitude(String dLatitude) {
		dropoff_latitude = dLatitude;		
	}
	
	public void setAccountCode(String ac) {
		accountCode = ac;
	}
	
	public void setAccountPassword(String ap) {
		accountPassword = ap;
	}
	
	public void setAuthorizationNumber(String aNum) {
		authNum = aNum;
	}
	
	public void setRemark(String rk) {
		remarks = rk;
	}
	
	public void setPriorityReason(String pReason) {
		priorityReason = pReason;
	}
	
	public void setCabNum(String cNum) {
		cabNum = cNum;
	}
	
	public void setFlatRate(String fRate) {
		flatRate = fRate;
	}
	
	public void setAttributeList(List<String> aList) {
		attributeList = aList;
	}
	
	public void setRepetitivePickUpTime(String rpTime) {
		rep_pickup_time = rpTime;
	}
	
	public void setRepetitiveSuspendStartDate(String rsStart) {
		repSuspendStart = rsStart;
	}
	
	public void setRepetitiveSuspendEndDate(String rsEnd) {
		repSuspendEnd = rsEnd;
	}
	
	public void setRepetitiveStartDate(String rStart) {
		repStart = rStart;
	}
	
	public void setRepetitiveEndDate(String rEnd) {
		repEnd = rEnd;
	}
	
	public void setForcedAddressFlag(String flag) {
		forcedAddressFlag = flag;
	}
	
	public void setOSPVersion(String ospV) {
		ospVersion = ospV;
	}
	
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void sendRequest(String ns, String url) {
		startProgress(); // start sending UI time check progress
		
		if (!isReqCancelled) {
			new HandleReqResTask(new ICustomResponseListener() {
				@Override
				public void onResponseReady(Object response) {
					if (!isReqCancelled) {
						finishProgress(); // response is back, can tell UI to finish progress bar
						
						try {
							ResponseWrapper rWrapper = new ResponseWrapper(((SoapTypeWrapper)response).GetSoap());
						    	
							if (rWrapper.getStatus() != 0) {
								iResponseListener.onErrorResponse(rWrapper);
						    		
								if (GlobalVar.logEnable) {
									Log.v(TAG, "Response Status: " + rWrapper.getErrorString());
								}
							}
							else {
								BookJobResponse cjRes = new BookJobResponse(((SoapTypeWrapper)response).GetSoap());
								iResponseListener.onResponseReady(cjRes);
						    	}
						}
						catch (Exception e) {
							iResponseListener.onError();
				    			
							if (GlobalVar.logEnable) {
								Log.e(TAG, "Response Error: " + e.toString());
							}	    		
				    	}
					}
				}

				@Override
				public void onError() {
					finishProgress();
					iResponseListener.onError();
				}
			}).execute(MethodEnum.BookJob, RequestEnum.BookJob, getArgumentList(), ns, url);
		}
	}
	
	public interface IBookJobResponseListener {
		public void onResponseReady(BookJobResponse response);
		public void onErrorResponse(ResponseWrapper response);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (systemID != null) { list.add(new DataParam("systemID", systemID)); }
		if (taxi_company_id != null) { list.add(new DataParam("taxi_company_id", taxi_company_id)); }
		if (deviceID != null) { list.add(new DataParam("deviceID", deviceID)); }
		if (taxi_ride_id != null) { list.add(new DataParam("taxi_ride_id", taxi_ride_id)); }
		if (request_type != null) { list.add(new DataParam("request_type", request_type)); }
		if (phonenum != null) { list.add(new DataParam("phonenum", phonenum)); }
		if (phoneext != null) { list.add(new DataParam("phoneext", phoneext)); }
		if (passenger_name != null) { list.add(new DataParam("passenger_name", passenger_name)); }
		if (number_of_passenger != null) { list.add(new DataParam("number_of_passenger", number_of_passenger)); }
		if (numTaxis != null) { list.add(new DataParam("numTaxis", numTaxis)); }
		if (adviseArrival != null) { list.add(new DataParam("adviseArrival", adviseArrival)); }
		if (email != null) { list.add(new DataParam("email", email)); }
		if (type != null) { list.add(new DataParam("type", type)); }
		if (priority != null) { list.add(new DataParam("priority", priority)); }
		if (pickup_house_number != null) { list.add(new DataParam("pickup_house_number", pickup_house_number)); }
		if (pickup_street_name != null) { list.add(new DataParam("pickup_street_name", pickup_street_name)); }
		if (pickup_district != null) { list.add(new DataParam("pickup_district", pickup_district)); }
		if (pickup_latitude != null) { list.add(new DataParam("pickup_latitude", pickup_latitude)); }
		if (pickup_longitude != null) { list.add(new DataParam("pickup_longitude", pickup_longitude)); }
		if (pickup_time != null) { list.add(new DataParam("pickup_time", pickup_time)); }
		if (pickup_unit != null) { list.add(new DataParam("pickup_unit", pickup_unit)); }
		if (pickup_landmark != null) { list.add(new DataParam("pickup_landmark", pickup_landmark)); }
		if (dropoff_house_number != null) { list.add(new DataParam("dropoff_house_number", dropoff_house_number)); }
		if (dropoff_street_name != null) { list.add(new DataParam("dropoff_street_name", dropoff_street_name)); }
		if (dropoff_district != null) { list.add(new DataParam("dropoff_district", dropoff_district)); }
		if (dropoff_unit != null) { list.add(new DataParam("dropoff_unit", dropoff_unit)); }
		if (dropoff_landmark != null) { list.add(new DataParam("dropoff_landmark", dropoff_landmark)); }
		if (dropoff_longitude != null) { list.add(new DataParam("dropoff_longitude", dropoff_longitude)); }
		if (dropoff_latitude != null) { list.add(new DataParam("dropoff_latitude", dropoff_latitude)); }
		if (accountCode != null) { list.add(new DataParam("accountCode", accountCode)); }
		if (accountPassword != null) { list.add(new DataParam("accountPassword", accountPassword)); }
		if (authNum != null) { list.add(new DataParam("authNum", authNum)); }
		if (remarks != null) { list.add(new DataParam("remarks", remarks)); }
		if (priorityReason != null) { list.add(new DataParam("priorityReason", priorityReason)); }
		if (cabNum != null) { list.add(new DataParam("cabNum", cabNum)); }
		if (flatRate != null) { list.add(new DataParam("flatRate", flatRate)); }
		if (rep_pickup_time != null) { list.add(new DataParam("rep_pickup_time", rep_pickup_time)); }
		if (repSuspendStart != null) { list.add(new DataParam("repSuspendStart", repSuspendStart)); }
		if (repSuspendEnd != null) { list.add(new DataParam("repSuspendEnd", repSuspendEnd)); }
		if (repStart != null) { list.add(new DataParam("repStart", repStart)); }
		if (repEnd != null) { list.add(new DataParam("repEnd", repEnd)); }
		if (forcedAddressFlag != null) { list.add(new DataParam("forcedAddressFlag", forcedAddressFlag)); }
		if (ospVersion != null) { list.add(new DataParam("ospVersion", ospVersion)); }
		if (zipCode != null) { list.add(new DataParam("zipCode", zipCode)); }
		if (attributeList != null) {
			String str = "", newStr = "";
			
			for (int i = 0; i < attributeList.size(); i ++) {
				str += attributeList.get(i) + ",";
			}
			
			if (!str.equalsIgnoreCase("")) {
				newStr = str.substring(0, str.length() - 1);
			}
			
			list.add(new DataParam("attributelist", newStr)); 
		}
		
		return list;
	}

	
}