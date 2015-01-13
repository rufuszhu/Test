package com.digital.dispatch.TaxiLimoSoap.responses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.serialization.PropertyInfo;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;

public class GetServiceListResponse extends ResponseWrapper {
	private int errCode;
	private MGParam params;
	private HashMap<String, String> paramHM;
	private ArrayList<AttributeItem> attributeList;
	private ArrayList<Node> countryList;
	private ArrayList<Node> stateList;
	private ArrayList<Node> regionList;
	private ArrayList<Node> companyList;
	private static String TAG = "GetServiceListResponse";


	public GetServiceListResponse() {
		super();
	}

	public GetServiceListResponse(SoapObject soap) {
		super(soap);
		countryList = new ArrayList<Node>();
		stateList = new ArrayList<Node>();
		regionList = new ArrayList<Node>();
		companyList = new ArrayList<Node>();
		try {
			paramHM = new HashMap<String, String>();
			for (int i = 0; i < properties.size(); i++) {
				Object item = ((PropertyInfo) properties.elementAt(i)).getValue();
				String objName = ((PropertyInfo) properties.elementAt(i)).getName();
				String countryName = "";
				if ((item instanceof SoapObject) && (objName.equalsIgnoreCase("listOfCountries"))) {
					for (int j = 0; j < ((SoapObject) item).getPropertyCount(); j++){
						if(j==0){
							countryName = ((SoapObject) item).getProperty(j).toString();
							Node country = new Node(countryName,"");
							countryList.add(country);
						}
						else{
							Object states = ((SoapObject) item).getProperty(j);
							parseStates((SoapObject) states, countryName);
						}
					}
				}
			}

		} catch (Exception e) {
			if (GlobalVar.logEnable) {
				Log.e(TAG, "ERROR: " + e.toString());
				e.printStackTrace();
			}
		}
	}
	
	private void parseStates(SoapObject states, String countryName){
		String stateName = "";
		for (int i = 0; i < ((SoapObject) states).getPropertyCount(); i++){
			if(i==0){
				stateName = ((SoapObject) states).getProperty(i).toString();
				Node state = new Node(stateName,countryName);
				stateList.add(state);
			}
			else{
				Object regions = ((SoapObject) states).getProperty(i);
				parseRegions((SoapObject) regions, stateName);
			}
		}
	}
	
	private void parseRegions(SoapObject regions, String stateName){
		String regionName="";
		for (int i = 0; i <  regions.getPropertyCount(); i++){
			if(i==0){
				regionName = ((SoapObject) regions).getProperty(i).toString();
				Node region = new Node(regionName,stateName);
				regionList.add(region);
			}
			else{
				Object companies = ((SoapObject) regions).getProperty(i);
				String companyString = companies.toString();
				Node company = new Node(companyString,regionName);
				companyList.add(company);
			}
		}
	}
	
	public ArrayList<Node> getCountryList() {
		return countryList;
	}

	public void setCountryList(ArrayList<Node> countryList) {
		this.countryList = countryList;
	}

	public ArrayList<Node> getStateList() {
		return stateList;
	}

	public void setStateList(ArrayList<Node> stateList) {
		this.stateList = stateList;
	}

	public ArrayList<Node> getRegionList() {
		return regionList;
	}

	public void setRegionList(ArrayList<Node> regionList) {
		this.regionList = regionList;
	}

	public ArrayList<Node> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(ArrayList<Node> companyList) {
		this.companyList = companyList;
	}
	public int GetErrCode() {
		return errCode;
	}

	public HashMap<String, String> GetParamList() {
		return paramHM;
	}

	public MGParam GetParams() {
		return params;
	}

	public ArrayList<AttributeItem> getAttributeList() {
		return attributeList;
	}

	private String checkExistAndGet(SoapObject so, String propertyName) {
		if (so.hasProperty(propertyName)) {
			String temp = so.getProperty(propertyName).toString();
			if (temp.equalsIgnoreCase("anyType{}")) {
				return "";
			} else {
				return temp;
			}
		} else {
			return "";
		}
	}

	
	
}
