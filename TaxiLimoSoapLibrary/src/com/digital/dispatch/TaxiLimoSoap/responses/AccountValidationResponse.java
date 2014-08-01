package com.digital.dispatch.TaxiLimoSoap.responses;

import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class AccountValidationResponse extends ResponseWrapper {
	private static String TAG = "Soap-AccValidationRes";
	
	public AccountValidationResponse() {
		super();
	}
	
	public AccountValidationResponse(SoapObject soap) {
		super(soap);
	}
}