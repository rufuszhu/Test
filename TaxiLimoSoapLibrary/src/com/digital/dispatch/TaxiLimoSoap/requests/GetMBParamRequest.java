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
import com.digital.dispatch.TaxiLimoSoap.responses.GetMBParamResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

public class GetMBParamRequest extends Request {
	private String systemID, systemPassword, taxiCompID;
	private IMGParamResponseListener iResponseListener = null;
	private static String TAG = "Soap-GetMBParams";
	
	public GetMBParamRequest(IMGParamResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
	}
	
	public void setSysID(String sysID) {
		systemID = sysID;
	}
	
	public void setSysPassword(String sysPass) {
		systemPassword = sysPass;
	}
	
	public void setTaxiCompanyID(String TCID) {
		taxiCompID = TCID;
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
				    			GetMBParamResponse gMBpRes = new GetMBParamResponse(((SoapTypeWrapper)response).GetSoap());
				    			iResponseListener.onResponseReady(gMBpRes);
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
			}).execute(MethodEnum.GetMBParam, RequestEnum.GetMBParam, getArgumentList(), ns, url);
		}
	}
	
	public interface IMGParamResponseListener {
		public void onResponseReady(GetMBParamResponse response);
		public void onErrorResponse(String errorString);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (systemID != null) { list.add(new DataParam("systemID", systemID)); }
		if (systemPassword != null) { list.add(new DataParam("systemPassword", systemPassword)); }
		if (taxiCompID != null) { list.add(new DataParam("taxi_company_id", taxiCompID)); }
		
		return list;
	}
}