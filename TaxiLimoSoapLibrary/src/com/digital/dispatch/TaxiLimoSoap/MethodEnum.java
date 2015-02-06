package com.digital.dispatch.TaxiLimoSoap;

public enum MethodEnum {
	AccountValidation("TLValidateAccount"), AttrList("TLGetAttributeList"), BookJob("TLBookJob"), CancelJob("TLCancelJob"), 
	CompanyList("TLGetCompanyList"), GetMBParam("TLGetMBParameter"), GetVersion("TLGetVersion"), KeyExchange("TLKeyExchange"), 
	PayByAccount("TLAccountPayment"), PayByToken("TLTokenPayment"), RecallJobs("TLRecallJobs"), RegDev("TLRegisterDevice"), 
	SendDriverMsg("TLSendDriverMessage"), Tokenization("TLTokenization"), VerifySMS("TLVerifyDevice"), GetServiceList("TLGetServiceList"),
    AvailableCars("TLAvailableCars");
	
	
	private String methodName;
	
	private MethodEnum(String name) {
		this.methodName = name;
	}
	
	public String toString() {
		return this.methodName;
	}
}