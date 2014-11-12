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
	private Tree serviceList;
	private static String TAG = "Soap-GetMBParamRes";


	public GetServiceListResponse() {
		super();
		serviceList = new Tree("root");
		
		attributeList = new ArrayList<AttributeItem>();
	}

	public GetServiceListResponse(SoapObject soap) {
		super(soap);
		attributeList = new ArrayList<AttributeItem>();
		try {
			paramHM = new HashMap<String, String>();
			for (int i = 0; i < properties.size(); i++) {
				Object item = ((PropertyInfo) properties.elementAt(i)).getValue();
				String objName = ((PropertyInfo) properties.elementAt(i)).getName();

				if ((item instanceof SoapObject) && (objName.equalsIgnoreCase("listOfCountries"))) {
					
					Object states = ((SoapObject) item).getProperty("listOfStates");
					Log.e(TAG,"country name: " +((SoapObject) item).getProperty("name"));
					parseStates((SoapObject) states);
					Log.e(TAG, "--------------------------------");
				}
			}

		} catch (Exception e) {
			if (GlobalVar.logEnable) {
				Log.e(TAG, "ERROR: " + e.toString());
			}
		}
	}
	
	
	private void parseStates(SoapObject states){
		for (int i = 0; i < ((SoapObject) states).getPropertyCount(); i++){
			if(i==0)
				Log.e(TAG, "state name: " + ((SoapObject) states).getProperty(i).toString());
			else{
				Object regions = ((SoapObject) states).getProperty(i);
				parseRegions((SoapObject) regions);
			}
		}
	}
	
	private void parseRegions(SoapObject regions){
		for (int i = 0; i <  regions.getPropertyCount(); i++){
			if(i==0)
				Log.e(TAG, "region name: " + regions.getProperty(i).toString());
			else{
				Object companies = ((SoapObject) regions).getProperty(i);
				Log.e(TAG, "companies: " + companies.toString());
			}
		}

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

	public class Tree {
		private Node root;

		public Tree(String rootData) {
			root = new Node(rootData);
			root.setChildren(new ArrayList<Node>());
		}
		
		public Node getRoot(){
			return root;
		}

		public class Node {
			private String data;
			private Node parent;
			private List<Node> children;
			
			public Node(String data){
				this.setData(data);
			}

			public List<Node> getChildren() {
				return children;
			}

			public void setChildren(List<Node> children) {
				this.children = children;
			}

			public String getData() {
				return data;
			}

			public void setData(String data) {
				this.data = data;
			}

			public Node getParent() {
				return parent;
			}

			public void setParent(Node parent) {
				this.parent = parent;
			}
			
		}
	}
}
