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
import com.digital.dispatch.TaxiLimoSoap.responses.PayByTokenResponse;

public class PayByTokenRequest extends Request {
	private String systemID, systemPassword, destID, deviceID, seqNum, transDTM, reqType, token, amount;
	private String driverID, vehicleID, jobID, pickUpAddr, dropOffAddr, cardNum, cardBrand, tip;
	private IPayByTokenResponseListener iResponseListener = null;
	private static String TAG = "Soap-PayByToken";
	
	public PayByTokenRequest(IPayByTokenResponseListener resListener, IRequestTimerListener timeListener) {
		super(timeListener);
		iResponseListener = resListener;
	}
	
	// required
	public void setSysID(String sysID) {
		systemID = sysID;
	}
	
	public void setSysPassword(String sysPW) {
		systemPassword = sysPW;
	}

	public void setDestID(String tcID) {
		destID = tcID;
	}

	public void setDeviceID(String dID) {
		deviceID = dID;
	}
	
	public void setSeqenceNum(String sNum) {
		seqNum = sNum;
	}
	
	public void setReqType(String rType) {
		reqType = rType;
	}

	public void setToken(String t) {
		token = t;
	}

	public void setAmount(String a) {
		amount = a;
	}
	
	public void setJobID(String jID) {
		jobID = jID;
	}
	// end

	// Not required items
	public void setTransTime(String time) {
		transDTM = time;
	}
	
	public void setTip(String t) {
		tip = t;
	}
	
	public void setDriverID(String dID) {
		driverID = dID;
	}
	
	public void setVehicleID(String vID) {
		vehicleID = vID;
	}

	public void setPickUpAddr(String puAddr) {
		pickUpAddr = puAddr;
	}

	public void setDropOffAddr(String doAddr) {
		dropOffAddr = doAddr;
	}
	
	public void setCardNum(String cn) {
		cardNum = cn;
	}
	
	public void setCardBrand(String cb) {
		cardBrand = cb;
	}
	// end
	
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
							PayByTokenResponse pbtRes = new PayByTokenResponse(((SoapTypeWrapper)response).GetSoap());
				    			
							if (pbtRes.GetResponseCode() == 1) {
								iResponseListener.onResponseReady(pbtRes);
							}
							else {
								iResponseListener.onErrorResponse(pbtRes);
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
			}).execute(MethodEnum.PayByToken, RequestEnum.PayByToken, getArgumentList(), ns, url);
		}
	}
	
	public interface IPayByTokenResponseListener {
		public void onResponseReady(PayByTokenResponse response);
		public void onErrorResponse(PayByTokenResponse errorString);
		public void onError();
	}
	
	private ArrayList<DataParam> getArgumentList() {
		ArrayList<DataParam> list = new ArrayList<DataParam>();
        
		if (systemID != null) { list.add(new DataParam("systemID", systemID)); }
		if (systemPassword != null) { list.add(new DataParam("systemPassword", systemPassword)); }
		if (destID != null) { list.add(new DataParam("mgDestId", destID)); }
		if (deviceID != null) { list.add(new DataParam("deviceID", deviceID)); }
		if (seqNum != null) { list.add(new DataParam("seqNo", seqNum)); }
		if (transDTM != null) { list.add(new DataParam("transDTM", transDTM)); }
		if (reqType != null) { list.add(new DataParam("reqType", reqType)); }
		if (token != null) { list.add(new DataParam("token", token)); }
		if (amount != null) { list.add(new DataParam("amount", amount)); }
		if (tip != null) { list.add(new DataParam("tip", tip)); }
		if (driverID != null) { list.add(new DataParam("info_1", driverID)); }
		if (vehicleID != null) { list.add(new DataParam("info_2", vehicleID)); }
		if (jobID != null) { list.add(new DataParam("info_3", jobID)); }
		if (pickUpAddr != null) { list.add(new DataParam("info_4", pickUpAddr)); }
		if (dropOffAddr != null) { list.add(new DataParam("info_5", dropOffAddr)); }
		if (cardNum != null) { list.add(new DataParam("cardNr", cardNum)); }
		if (cardBrand != null) { list.add(new DataParam("cardBrand", cardBrand)); }
		
		return list;
	}
}