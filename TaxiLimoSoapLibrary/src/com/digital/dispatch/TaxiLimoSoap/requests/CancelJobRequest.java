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
import com.digital.dispatch.TaxiLimoSoap.responses.CancelJobResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

public class CancelJobRequest extends Request {
	private String systemID, taxi_ride_id, destID, request_type, mgVersion;
	private ICancelResponseListener iResponseListener = null;
	private static String TAG = "Soap-CancelJob";
	
	public CancelJobRequest(ICancelResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
	}
	
	public void setSysID(String sysID) {
		systemID = sysID;
	}
	
	public void setMGVersion(String mgV) {
		mgVersion = mgV;
	}
	
	public void setTaxiRideID(String trID) {
		taxi_ride_id = trID;
	}
	
	public void setDestID(String dID) {
		destID = dID;
	}
	
	public void setReqType(String reqType) {
		request_type = reqType;
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
								String errorString = rWrapper.getErrorString();
								
								if (errorString.contains("already cancelled")) {
									CancelJobResponse cjRes = new CancelJobResponse(((SoapTypeWrapper) response).GetSoap());
									iResponseListener.onResponseReady(cjRes);
								}
								else {
									iResponseListener.onErrorResponse(rWrapper.getErrorString());
										
									if (GlobalVar.logEnable) {
										Log.v(TAG, "Response Status: " + rWrapper.getErrorString());
									}
								}
							}
							else {
								CancelJobResponse cjRes = new CancelJobResponse(((SoapTypeWrapper)response).GetSoap());
								iResponseListener.onResponseReady(cjRes);
							}
						}
						catch (Exception e) {
							iResponseListener.onError();
				    			
							if (GlobalVar.logEnable) {
								Log.v(TAG, "Response Error: " + e.toString());
							}
						}
					}
				}

				@Override
				public void onError() {
					finishProgress();
					iResponseListener.onError();
				}
			}).execute(MethodEnum.CancelJob, RequestEnum.CancelJob, getArgumentList(), ns, url);
		}
	}
	
	public interface ICancelResponseListener {
		public void onResponseReady(CancelJobResponse response);
		public void onErrorResponse(String errorString);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (systemID != null) { list.add(new DataParam("systemID", systemID)); }
		if (taxi_ride_id != null) { list.add(new DataParam("taxi_ride_id", taxi_ride_id)); }
		if (destID != null) { list.add(new DataParam("mgDestId", destID)); }
		if (mgVersion != null) { list.add(new DataParam("mgVersion", mgVersion)); }
		if (request_type != null) { list.add(new DataParam("request_type", request_type)); }
		
		return list;
	}
}