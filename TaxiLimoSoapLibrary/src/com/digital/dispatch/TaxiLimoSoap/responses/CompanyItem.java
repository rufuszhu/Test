package com.digital.dispatch.TaxiLimoSoap.responses;

public class CompanyItem {
	public String destID;
	public String name;
	public String logo;
	public int logoVersion;
	public String description;
	public String attributes;
	public int systemID;
	public int baseRate;
	public int ratePerDistance;
	public String multiPay;
	
	public CompanyItem() {
		destID = "";
		name = "";
		logo = "";
		logoVersion = 0;
		description = "";
		attributes = "";
		systemID = 0;
		baseRate = 0;
		ratePerDistance = 0;
		multiPay = "";
	}
	
	public CompanyItem(String dID, String cName, String cLogo, int cLogoVer, String desc, String attr, int sysID, int bRate, int rPerDist, String cmultiPay) {
		destID = dID;
		name = cName;
		logo = cLogo;
		logoVersion = cLogoVer;
		description = desc;
		attributes = attr;
		systemID = sysID;
		baseRate = bRate;
		ratePerDistance = rPerDist;
		multiPay = cmultiPay;
		
	}
}
