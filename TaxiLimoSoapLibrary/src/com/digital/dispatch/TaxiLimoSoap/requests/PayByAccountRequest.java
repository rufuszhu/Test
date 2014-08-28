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
import com.digital.dispatch.TaxiLimoSoap.responses.PayByAccountResponse;

public class PayByAccountRequest extends Request {
	private String systemID, systemPassword, taxiCompanyID, deviceID, taxiRideID, accountCode, amount, tip;
	private IPayByAccountResponseListener iResponseListener = null;
	private static String TAG = "Soap-PayByAccount";
	
	public PayByAccountRequest(IPayByAccountResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
	}
	
	public void setSysID(String sysID) {
		systemID = sysID;
	}
	
	public void setSysPassword(String sysPW) {
		systemPassword = sysPW;
	}

	public void setTaxiCompanyID(String tcID) {
		taxiCompanyID = tcID;
	}

	public void setDeviceID(String dID) {
		deviceID = dID;
	}
	
	public void setTRID(String trID) {
		taxiRideID = trID;
	}
	
	public void setAccountCode(String aCode) {
		accountCode = aCode;
	}

	public void setTotalAmount(String aTotal) {
		amount = aTotal;
	}
	
	public void setTip(String aTip) {
		tip = aTip;
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
							PayByAccountResponse atRes = new PayByAccountResponse(((SoapTypeWrapper)response).GetSoap());
				    			
							if (atRes.getErrCode() == 0) {
								iResponseListener.onResponseReady(atRes);
							}
							else {
								iResponseListener.onErrorResponse(atRes);
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
			}).execute(MethodEnum.PayByAccount, RequestEnum.PayByAccount, getArgumentList(), ns, url);
		}
	}
	
	public interface IPayByAccountResponseListener {
		public void onResponseReady(PayByAccountResponse response);
		public void onErrorResponse(PayByAccountResponse response);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (systemID != null) { list.add(new DataParam("systemID", systemID)); }
		if (systemPassword != null) { list.add(new DataParam("systemPassword", systemPassword)); }
		if (taxiCompanyID != null) { list.add(new DataParam("taxi_company_id", taxiCompanyID)); }
		if (deviceID != null) { list.add(new DataParam("deviceID", deviceID)); }
		if (taxiRideID != null) { list.add(new DataParam("taxi_ride_id", taxiRideID)); }
		if (accountCode != null) { list.add(new DataParam("account_code", accountCode)); }
		if (amount != null) { list.add(new DataParam("amount", amount)); }
		if (tip != null) { list.add(new DataParam("tip", tip)); }
		
		return list;
	}
}