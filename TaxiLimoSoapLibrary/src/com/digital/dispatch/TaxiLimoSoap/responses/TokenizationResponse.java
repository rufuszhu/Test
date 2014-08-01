package com.digital.dispatch.TaxiLimoSoap.responses;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class TokenizationResponse extends ResponseWrapper {
	private String payload, rsMsg, token, cardLast4Digit, cardBrand;
	private int sNum, rsCode, keFlag;
	private static String TAG = "Soap-TokenizationRes";
	
	public TokenizationResponse() {
		super();
	}
	
	public TokenizationResponse(SoapObject soap) {
		super(soap);
		
		try {
			payload = this.getProperty("payload").toString();
			
			String[] payloadInfo = payload.split(GlobalVar.SEPERATOR);
			sNum = Integer.parseInt(payloadInfo[0]);
			rsCode = Integer.parseInt(payloadInfo[1]);
			rsMsg = payloadInfo[2];
			token = payloadInfo[3];
			cardLast4Digit = payloadInfo[4];
			cardBrand = payloadInfo[5];
	    	keFlag = Integer.parseInt(payloadInfo[6]);
		}
		catch (Exception e) {
			rsCode = 7; // use 7 to be the generic fail msg code
			
			if (GlobalVar.logEnable) {
				Log.e(TAG, "ERROR: " + e.toString());
			}
		}
	}
	
	public String GetPayload() {
		return payload;
	}
	
	public int GetSequenceNum() {
		return sNum;
	}
	
	public int GetResponseCode() {
		return rsCode;
	}
	
	public String GetResponseMsg() {
		return rsMsg;
	}
	
	public String GetToken() {
		return token;
	}
	
	public String GetLast4Digit() {
		return cardLast4Digit;
	}
	
	public String GetCardBrand() {
		return cardBrand;
	}
	
	public int GetKeyExFlag() {
		return keFlag;
	}
}
