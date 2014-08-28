package com.digital.dispatch.TaxiLimoSoap.requests;

import java.util.ArrayList;

import com.digital.dispatch.TaxiLimoSoap.DataParam;
import com.digital.dispatch.TaxiLimoSoap.HandleReqResTask;
import com.digital.dispatch.TaxiLimoSoap.MethodEnum;
import com.digital.dispatch.TaxiLimoSoap.RequestEnum;
import com.digital.dispatch.TaxiLimoSoap.HandleReqResTask.ICustomResponseListener;
import com.digital.dispatch.TaxiLimoSoap.responses.GetVersionResponse;

public class GetVersionRequest extends Request {
	private IGetVersionResponseListener iResponseListener = null;
	private static String TAG = "Soap-GetVersion";
	
	public GetVersionRequest(IGetVersionResponseListener resListener, IRequestTimerListener timeListener) {
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
						
						GetVersionResponse atRes = new GetVersionResponse(response.toString());
			    		iResponseListener.onResponseReady(atRes);
					}
				}

				@Override
				public void onError() {
					finishProgress();
					iResponseListener.onError();
				}
			}).execute(MethodEnum.GetVersion, RequestEnum.GetVersion, getArgumentList(), ns, url);
		}
	}
	
	public interface IGetVersionResponseListener {
		public void onResponseReady(GetVersionResponse response);
		public void onErrorResponse(String errRes);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		return list;
	}
}