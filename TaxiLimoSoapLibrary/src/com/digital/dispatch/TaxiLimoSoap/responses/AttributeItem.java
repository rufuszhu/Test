package com.digital.dispatch.TaxiLimoSoap.responses;

public class AttributeItem {
	public String attrSN;
	public String attrLN;
	public String attrWB;
	
	public AttributeItem() {
		attrSN = "";
		attrLN = "";
		attrWB = "";
	}
	
	public AttributeItem(String shortName, String longName, String webBook) {
		attrSN = shortName;
		attrLN = longName;
		attrWB = webBook;
	}
}
