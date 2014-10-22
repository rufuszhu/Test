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
import com.digital.dispatch.TaxiLimoSoap.responses.VerifySMSResponse;

public class VerifySMSRequest extends Request {
	private String smsCode, phoneNr,  protocol, hardwareID;
	private IVerifySMSResponseListener iResponseListener = null;
	private static String TAG = "Soap-RegDev";
	
	public VerifySMSRequest(IVerifySMSResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
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
								VerifySMSResponse atRes = new VerifySMSResponse(((SoapTypeWrapper)response).GetSoap());
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
			}).execute(MethodEnum.VerifySMS, RequestEnum.VerifySMS, getArgumentList(), ns, url);
		}
	}
	
	public interface IVerifySMSResponseListener {
		public void onResponseReady(VerifySMSResponse atRes);
		public void onErrorResponse(String errorString);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (phoneNr != null) { list.add(new DataParam("phoneNr", phoneNr)); }
		if (smsCode != null) { list.add(new DataParam("SMSCode", smsCode)); }
		if (protocol != null) { list.add(new DataParam("protocol", protocol)); }
		if (hardwareID != null) { list.add(new DataParam("hardwareID", hardwareID)); }
		
		return list;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public String getPhoneNr() {
		return phoneNr;
	}

	public void setPhoneNr(String phoneNr) {
		this.phoneNr = phoneNr;
	}



	public String getProtocol() {
		return protocol;
	}



	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}



	public String getHardwareID() {
		return hardwareID;
	}



	public void setHardwareID(String hardwareID) {
		this.hardwareID = hardwareID;
	}



	public IVerifySMSResponseListener getiResponseListener() {
		return iResponseListener;
	}



	public void setiResponseListener(IVerifySMSResponseListener iResponseListener) {
		this.iResponseListener = iResponseListener;
	}



	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}
}