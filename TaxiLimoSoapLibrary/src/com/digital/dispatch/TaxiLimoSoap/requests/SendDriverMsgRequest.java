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
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;
import com.digital.dispatch.TaxiLimoSoap.responses.SendDriverMsgResponse;

public class SendDriverMsgRequest extends Request {
	private String systemID, systemPassword, sessionID, message, mgVersion, mgDestId;
	private String deliveryTime, priority, destination, destinationTypeID;
	private ISendDriverMsgResponseListener iResponseListener = null;
	private static String TAG = "Soap-SendDriverMsg";
	
	public SendDriverMsgRequest(ISendDriverMsgResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
	}
	
	public void setSysID(String sysID) {
		systemID = sysID;
	}
	
	public void setDestID(String dID) {
		mgDestId = dID;
	}
	
	public void setMGVersion(String mgV) {
		mgVersion = mgV;
	}
	
	public void setSystemPassword(String sysPass) {
		systemPassword = sysPass;
	}
	
	public void setSessionID(String sID) {
		sessionID = sID;
	}
	
	public void setMessage(String msg) {
		message = msg;
	}
	
	public void setDeliveryTime(String dTime) {
		deliveryTime = dTime;
	}
	
	public void setPriority(String p) {
		priority = p;
	}
	
	public void setDestination(String des) {
		destination = des;
	}
	
	public void setDestinationTypeID(String dtID) {
		destinationTypeID = dtID;
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
							if (status != 0) {
								iResponseListener.onErrorResponse(rWrapper.getErrorString());
										
								if (GlobalVar.logEnable) {
									Log.v(TAG, "Response Status: " + rWrapper.getErrorString());
								}
							} 
							else {
								SendDriverMsgResponse atRes = new SendDriverMsgResponse(((SoapTypeWrapper) response).GetSoap());
								iResponseListener.onResponseReady(atRes);
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
			}).execute(MethodEnum.SendDriverMsg, RequestEnum.SendDriverMsg, getArgumentList(), ns, url);
		}
	}
	
	public interface ISendDriverMsgResponseListener {
		public void onResponseReady(SendDriverMsgResponse response);
		public void onErrorResponse(String errorString);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (systemID != null) { list.add(new DataParam("systemID", systemID)); }
		if (systemPassword != null) { list.add(new DataParam("systemPassword", systemPassword)); }
		if (mgDestId != null) { list.add(new DataParam("mgDestId", mgDestId)); }
		if (mgVersion != null) { list.add(new DataParam("mgVersion", mgVersion)); }
		if (sessionID != null) { list.add(new DataParam("sessionID", sessionID)); }
		if (message != null) { list.add(new DataParam("message", message)); }
		if (deliveryTime != null) { list.add(new DataParam("deliveryTime", deliveryTime)); }
		if (priority != null) { list.add(new DataParam("priority", priority)); }
		if (destination != null) { list.add(new DataParam("destination", destination)); }
		if (destinationTypeID != null) { list.add(new DataParam("destinationTypeID", destinationTypeID)); }
		
		return list;
	}
}
