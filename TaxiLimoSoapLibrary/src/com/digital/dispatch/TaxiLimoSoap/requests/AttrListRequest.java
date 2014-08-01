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
import com.digital.dispatch.TaxiLimoSoap.responses.AttrListResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

public class AttrListRequest extends Request {
	private String systemID;
	private IAttrResponseListener iResponseListener = null;
	private static String TAG = "Soap-AttrList";
	
	public AttrListRequest(IAttrResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
	}
	
	public void setSysID(String sysID) {
		systemID = sysID;
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
								AttrListResponse atRes = new AttrListResponse(((SoapTypeWrapper)response).GetSoap());
								iResponseListener.onResponseReady(atRes);
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
			}).execute(MethodEnum.AttrList, RequestEnum.AttrList, getArgumentList(), ns, url);
		}
	}
	
	public interface IAttrResponseListener {
		public void onResponseReady(AttrListResponse response);
		public void onErrorResponse(String errorString);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (systemID != null) { list.add(new DataParam("systemID", systemID)); }
		
		return list;
	}
}