package com.digital.dispatch.TaxiLimoSoap.responses;

import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class PayByAccountResponse extends ResponseWrapper {
	private static String TAG = "Soap-PayByAccountRes";
	
	public PayByAccountResponse() {
		super();
	}
	
	public PayByAccountResponse(SoapObject soap) {
		super(soap);
	}
}