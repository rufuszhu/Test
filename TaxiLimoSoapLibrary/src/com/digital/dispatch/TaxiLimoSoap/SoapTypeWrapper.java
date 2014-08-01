package com.digital.dispatch.TaxiLimoSoap;

import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class SoapTypeWrapper {
	private SoapObject soap;
	private MethodEnum type;
	
	public SoapTypeWrapper(SoapObject so, MethodEnum me) {
		soap = so;
		type = me;
	}
	
	public SoapObject GetSoap() {
		return soap;
	}
	
	public MethodEnum getType() {
		return type;
	}
}
