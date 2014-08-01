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
import com.digital.dispatch.TaxiLimoSoap.responses.KeyExchangeResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

public class KeyExchangeRequest extends Request {
	private String deviceID, DHKey;
	private IKeyExchangeResponseListener iResponseListener = null;
	private static String TAG = "Soap-KeyExchange";
	
	public KeyExchangeRequest(IKeyExchangeResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
	}
	
	public void setDeviceID(String dID) {
		deviceID = dID;
	}
	
	public void setDHKey(String key) {
		DHKey = key;
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
								iResponseListener.onErrorResponse(rWrapper.getErrorString());
						    		
								if (GlobalVar.logEnable) {
									Log.v(TAG, "Response Status: " + rWrapper.getErrorString());
								}
							}
							else {
								KeyExchangeResponse atRes = new KeyExchangeResponse(((SoapTypeWrapper)response).GetSoap());
								iResponseListener.onResponseReady(atRes);
							}
						}
						catch (Exception e) {
							iResponseListener.onError();
				    			
							if (GlobalVar.logEnable) {
								Log.v(TAG, "Response ERROR: " + e.toString());
							}
						}
					}
				}

				@Override
				public void onError() {
					finishProgress();
					iResponseListener.onError();
				}
			}).execute(MethodEnum.KeyExchange, RequestEnum.KeyExchange, getArgumentList(), ns, url);
		}
	}
	
	public interface IKeyExchangeResponseListener {
		public void onResponseReady(KeyExchangeResponse response);
		public void onErrorResponse(String errorString);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (deviceID != null) { list.add(new DataParam("deviceID", deviceID)); }
		if (DHKey != null) { list.add(new DataParam("payload", DHKey)); }
		
		return list;
	}
}