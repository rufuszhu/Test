package com.digital.dispatch.TaxiLimoSoap.responses;

import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class GetVersionResponse {
	private String response;
	
	public GetVersionResponse(String res) {
		response = res;
	}
	
	public String getRes() {
		return response;
	}
}
