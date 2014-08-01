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
import com.digital.dispatch.TaxiLimoSoap.responses.RegDevResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

public class RegDevRequest extends Request {
	private String deviceToken, phoneNr, name, locale, version, protocol, hardwareID, type;
	private IRegDevResponseListener iResponseListener = null;
	private static String TAG = "Soap-RegDev";
	
	public RegDevRequest(IRegDevResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
	}
	
	public void setToken(String dToken) {
		deviceToken = dToken;
	}
	
	public void setPhoneNum(String num) {
		phoneNr = num;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public void setLocale(String l) {
		locale = l;
	}
	
	public void setVersion(String v) {
		version = v;
	}
	
	public void setProtocol(String p) {
		protocol = p;
	}
	
	public void setHardwareID(String hID) {
		hardwareID = hID;
	}
	
	public void settype(String ty) {
		type = ty;
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
								RegDevResponse atRes = new RegDevResponse(((SoapTypeWrapper)response).GetSoap());
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
			}).execute(MethodEnum.RegDev, RequestEnum.RegDev, getArgumentList(), ns, url);
		}
	}
	
	public interface IRegDevResponseListener {
		public void onResponseReady(RegDevResponse response);
		public void onErrorResponse(String errorString);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (deviceToken != null) { list.add(new DataParam("deviceToken", deviceToken)); }
		if (phoneNr != null) { list.add(new DataParam("phoneNr", phoneNr)); }
		if (name != null) { list.add(new DataParam("name", name)); }
		if (locale != null) { list.add(new DataParam("locale", locale)); }
		if (version != null) { list.add(new DataParam("version", version)); }
		if (protocol != null) { list.add(new DataParam("protocol", protocol)); }
		if (hardwareID != null) { list.add(new DataParam("hardwareID", hardwareID)); }
		if (type != null) { list.add(new DataParam("type", type)); }
		
		return list;
	}
}