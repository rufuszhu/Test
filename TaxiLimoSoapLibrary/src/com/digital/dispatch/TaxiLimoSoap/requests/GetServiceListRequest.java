package com.digital.dispatch.TaxiLimoSoap.requests;

import java.util.ArrayList;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.DataParam;
import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.HandleReqResTask;
import com.digital.dispatch.TaxiLimoSoap.HandleReqResTask.ICustomResponseListener;
import com.digital.dispatch.TaxiLimoSoap.MethodEnum;
import com.digital.dispatch.TaxiLimoSoap.RequestEnum;
import com.digital.dispatch.TaxiLimoSoap.SoapTypeWrapper;
import com.digital.dispatch.TaxiLimoSoap.responses.GetServiceListResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

public class GetServiceListRequest extends Request {
	private IGetServiceListResponseListener iResponseListener = null;
	private static String TAG = "GetServiceListRequest";

	public GetServiceListRequest(IGetServiceListResponseListener resListener, IRequestTimerListener timeListener) {
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

					try {
						ResponseWrapper rWrapper = new ResponseWrapper(((SoapTypeWrapper) response).GetSoap());

						if (rWrapper.getStatus() != 0) {
							iResponseListener.onErrorResponse(rWrapper.getErrorString());

							if (GlobalVar.logEnable) {
								Log.v(TAG, "Response Status: " + rWrapper.getErrorString());
							}
						} else {
							GetServiceListResponse gMBpRes = new GetServiceListResponse(((SoapTypeWrapper) response).GetSoap());
							iResponseListener.onResponseReady(gMBpRes);
						}
					} catch (Exception e) {
						iResponseListener.onError();

						if (GlobalVar.logEnable) {
							Log.e(TAG, "Response Error: " + e.toString());
                            e.printStackTrace();
						}
					}

				}

				@Override
				public void onError() {
					finishProgress();
					iResponseListener.onError();
				}
			}).execute(MethodEnum.GetServiceList, RequestEnum.GetServiceList, getArgumentList(), ns, url);
		}
	}

	public interface IGetServiceListResponseListener {
		public void onResponseReady(GetServiceListResponse response);

		public void onErrorResponse(String errRes);

		public void onError();
	}

	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();

		return list;
	}
}