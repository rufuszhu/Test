package com.digital.dispatch.TaxiLimoSoap.requests;

import java.util.ArrayList;
import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.DataParam;
import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.HandleReqResTask;
import com.digital.dispatch.TaxiLimoSoap.MethodEnum;
import com.digital.dispatch.TaxiLimoSoap.RequestEnum;
import com.digital.dispatch.TaxiLimoSoap.SoapTypeWrapper;
import com.digital.dispatch.TaxiLimoSoap.HandleReqResTask.ICustomResponseListener;
import com.digital.dispatch.TaxiLimoSoap.responses.RecallJobsResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

public class RecallJobsRequest extends Request {
	private String systemID, taxi_company_id, passengerName, phoneNumber, ospVersion;
	private String recallTripType, recallCriteria, fromTripRsvTime, taxi_ride_id, jobList;
	private IRecallJobsResponseListener iResponseListener = null;
	private static String TAG = "Soap-RecallJobsRequest";
	
	public RecallJobsRequest(IRecallJobsResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
	}
	
	public void setSysID(String sysID) {
		systemID = sysID;
	}
	
	public void setTaxiCompanyID(String compID) {
		taxi_company_id = compID;
	}
	
	public void setPassengerName(String name) {
		passengerName = name;
	}
	
	// use when recallCriteria = 8
	public void setPhoneNum(String num) {
		phoneNumber = num;
	}
	
	public void setTripType(String tType) {
		recallTripType = tType;
	}
	
	public void setCriteria(String crit) {
		recallCriteria = crit;
	}
	
	public void setFromTripRsvTime(String rsvTime) {
		fromTripRsvTime = rsvTime;
	}
	
	public void setOspVersion(String ospV) {
		ospVersion = ospV;
	}
	
	// use when recallCriteria = 1, single trID only
	public void setTRID(String trID) {
		taxi_ride_id = trID;
	}
	
	// use when recallCriteria = 10, concatenation of list of trID separated by ,
	public void setJobList(String jList) {
		jobList = jList;
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
							int status = rWrapper.getStatus();
							int errorCode = rWrapper.getErrCode();
							
							if (status != 0) {
								if (errorCode != 3) {
									iResponseListener.onErrorResponse(rWrapper);
										
									if (GlobalVar.logEnable) {
										Log.v(TAG, "Response Status: " + rWrapper.getErrorString());
									}
								} 
								else {
									RecallJobsResponse atRes = new RecallJobsResponse(((SoapTypeWrapper) response).GetSoap());
									iResponseListener.onResponseReady(atRes);
								}
							} 
							else {
								RecallJobsResponse atRes = new RecallJobsResponse(((SoapTypeWrapper) response).GetSoap());
								iResponseListener.onResponseReady(atRes);
							}
						}
						catch (Exception e) {
							iResponseListener.onError();
				    			
							if (GlobalVar.logEnable) {
								Log.v(TAG, "Response Error: " + e.toString());
								e.printStackTrace();
							}
						}
					}
				}

				@Override
				public void onError() {
					finishProgress();
					iResponseListener.onError();
				}
			}).execute(MethodEnum.RecallJobs, RequestEnum.RecallJobs, getArgumentList(), ns, url);
		}
	}
	
	public interface IRecallJobsResponseListener {
		public void onResponseReady(RecallJobsResponse response);
		public void onErrorResponse(ResponseWrapper resWrapper);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (systemID != null) { list.add(new DataParam("systemID", systemID)); }
		if (taxi_company_id != null) { list.add(new DataParam("taxi_company_id", taxi_company_id)); }
		if (passengerName != null) { list.add(new DataParam("passengerName", passengerName)); }
		if (phoneNumber != null) { list.add(new DataParam("phoneNumber", phoneNumber)); }
		if (recallTripType != null) { list.add(new DataParam("recallTripType", recallTripType)); }
		if (recallCriteria != null) { list.add(new DataParam("recallCriteria", recallCriteria)); }
		if (fromTripRsvTime != null) { list.add(new DataParam("fromTripRsvTime", fromTripRsvTime)); }
		if (taxi_ride_id != null) { list.add(new DataParam("taxi_ride_id", taxi_ride_id)); }
		if (ospVersion != null) { list.add(new DataParam("ospVersion", ospVersion)); }
		if (jobList != null) { list.add(new DataParam("jobList", jobList)); }
		
		return list;
	}
}