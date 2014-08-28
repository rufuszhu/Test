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
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyListResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

public class CompanyListRequest extends Request {
	private String regionName, state, country, latitude, longitude;
	private ICompResponseListener iResponseListener = null;
	private static String TAG = "Soap-GetCompanyList";
	
	public CompanyListRequest(ICompResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
	}
	
	public void setRegionName(String region) {
		regionName = region;
	}
	
	public void setStateProvince(String statOrProv) {
		state = statOrProv;
	}
	
	public void setCountry(String Coun) {
		country = Coun;
	}
	
	public void setLatitude(String lat) {
		latitude = lat;
	}
	
	public void setLongitude(String longi) {
		longitude = longi;
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
								CompanyListResponse gaRes = new CompanyListResponse(((SoapTypeWrapper)response).GetSoap());
								iResponseListener.onResponseReady(gaRes);
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
			}).execute(MethodEnum.CompanyList, RequestEnum.CompanyList, getArgumentList(), ns, url);
		}
	}
	
	public interface ICompResponseListener {
		public void onResponseReady(CompanyListResponse response);
		public void onErrorResponse(String errorString);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (regionName != null) { list.add(new DataParam("regionName", regionName)); }
		if (state != null) { list.add(new DataParam("state", state)); }
		if (country != null) { list.add(new DataParam("country", country)); }
		if (latitude != null) { list.add(new DataParam("latitude", latitude)); }
		if (longitude != null) { list.add(new DataParam("longitude", longitude)); }
		
		return list;
	}
}