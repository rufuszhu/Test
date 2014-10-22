package com.digital.dispatch.TaxiLimoSoap;

public enum RequestEnum {
	AccountValidation("AccountValidateRequest"), AttrList("GetAttrListRequest"), BookJob("BookJobReq"), CancelJob("CancelJobReq"), 
	CompanyList("GetCompanyListRequest"), GetMBParam("GetMBParamRequest"), GetVersion("GetVersionReq"), KeyExchange("KeyExchangeRequest"), 
	PayByAccount("AccountPaymentRequest"), PayByToken("TokenPaymentRequest"), RecallJobs("RecallJobsRequest"), RegDev("RegisterDeviceRequest"), 
	SendDriverMsg("SendDriverMessageReq"), Tokenization("TokenizationRequest"), VerifySMS("VerifyDeviceRequest");
	
	/* GetVersion is not in the spec just created to fit the 2nd layer*/
	
	private String reqName;
	
	private RequestEnum(String name) {
		this.reqName = name;
	}
	
	public String toString() {
		return this.reqName;
	}
}