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
import com.digital.dispatch.TaxiLimoSoap.responses.TokenizationResponse;

public class TokenizationRequest extends Request {
	private String deviceID, sNum, desTimeStamp, reqType, token, cardInfo, chName, zip, email;
	private ITokenizationResponseListener iResponseListener = null;
	private static String TAG = "Soap-Tokenization";
	
	public TokenizationRequest(ITokenizationResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
		desTimeStamp = "";
	}
	
	public void setDeviceID(String dID) {
		deviceID = dID;
	}
	
	public void setSequenceNum(String sequenceNum) {
		// make sure max length is 3
		if (sequenceNum.length() > 3) {
			sNum = sequenceNum.substring(sequenceNum.length() - 3, sequenceNum.length());
		}
		else {
			sNum = sequenceNum;
		}
	}
	
	public void setDESTimeStamp(String ts) {
		desTimeStamp = ts;
	}
	
	public void setReqType(String type) {
		reqType = type;
	}
	
	public void setToken(String t) {
		token = t;
	}
	
	public void setCardNumAndExDate(String cNumAndExDate) {
		cardInfo = cNumAndExDate;
	}
	
	public void setCardHolderName(String cardHolderName) {
		chName = cardHolderName;
	}
	
	public void setZip(String z) {
		zip = z;
	}
	
	public void setEmail(String e) {
		email = e;
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
							TokenizationResponse tnRes = new TokenizationResponse(((SoapTypeWrapper)response).GetSoap());
				    			
							if (tnRes.GetResponseCode() == 1) {
								iResponseListener.onResponseReady(tnRes);
							}
							else {
								iResponseListener.onErrorResponse(tnRes);
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
			}).execute(MethodEnum.Tokenization, RequestEnum.Tokenization, getArgumentList(), ns, url);
		}
	}
	
	public interface ITokenizationResponseListener {
		public void onResponseReady(TokenizationResponse response);
		public void onErrorResponse(TokenizationResponse res);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
		
		if (deviceID != null) { list.add(new DataParam("deviceID", deviceID)); }
		
		String tmpPayload = sNum + GlobalVar.SEPERATOR + desTimeStamp + GlobalVar.SEPERATOR + reqType + GlobalVar.SEPERATOR;
		
		if (token != null) { tmpPayload += token; }
		tmpPayload += GlobalVar.SEPERATOR;
		if (cardInfo != null) { tmpPayload += cardInfo; }
		tmpPayload += GlobalVar.SEPERATOR;
		if (chName != null) { tmpPayload += chName; }
		tmpPayload += GlobalVar.SEPERATOR;
		if (zip != null) { tmpPayload += zip; }
		tmpPayload += GlobalVar.SEPERATOR;
		if (email != null) { tmpPayload += email; }
		
		list.add(new DataParam("payload", tmpPayload));
		
		return list;
	}
}