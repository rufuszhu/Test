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
import com.digital.dispatch.TaxiLimoSoap.responses.AccountValidationResponse;

public class AccountValidationRequest extends Request {
	private String systemID, accountCode, accountPassword, taxiCompID;
	private IAccValResponseListener iResponseListener = null;
	private static String TAG = "Soap-AccountValidation";
	
	public AccountValidationRequest(IAccValResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
	}
	
	public void setSysID(String sysID) {
		systemID = sysID;
	}
	
	public void setCompanyID(String compID) {
		taxiCompID = compID;
	}
	
	public void setAccNum(String accNum) {
		accountCode = accNum;
	}
	
	public void setAccPassword(String password) {
		accountPassword = password;
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
							AccountValidationResponse avRes = new AccountValidationResponse(((SoapTypeWrapper)response).GetSoap());
				    			
							if (avRes.getErrCode() != 0) {
								iResponseListener.onErrorResponse(avRes);
							}
							else {
								iResponseListener.onResponseReady(avRes);
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
			}).execute(MethodEnum.AccountValidation, RequestEnum.AccountValidation, getArgumentList(), ns, url);
		}
	}
	
	public interface IAccValResponseListener {
		public void onResponseReady(AccountValidationResponse response);
		public void onErrorResponse(AccountValidationResponse errRes);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (systemID != null) { list.add(new DataParam("systemID", systemID)); }
		if (accountCode != null) { list.add(new DataParam("account_code", accountCode)); }
		if (accountPassword != null) { list.add(new DataParam("account_password", accountPassword)); }
		if (taxiCompID != null) { list.add(new DataParam("taxi_co_id", taxiCompID)); }
		
		return list;
	}
}