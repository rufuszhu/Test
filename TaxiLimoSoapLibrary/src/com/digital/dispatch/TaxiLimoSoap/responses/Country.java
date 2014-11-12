package com.digital.dispatch.TaxiLimoSoap.responses;

import java.util.ArrayList;

public class Country {
	private String countryName;
	private ArrayList<State> states;
	
	public class State{
		private String stateName;
		private ArrayList<Regions> states;
		
		public State(String name, ArrayList<Regions> states){
			this.stateName = name;
			this.states = states;
		}
		
		public String getStateName() {
			return stateName;
		}
		public void setStateName(String stateName) {
			this.stateName = stateName;
		}
		public ArrayList<Regions> getStates() {
			return states;
		}
		public void setStates(ArrayList<Regions> states) {
			this.states = states;
		}
	}
	
	public class Regions{
		private String regionName;
		private ArrayList<String> companies;
		public String getRegionName() {
			return regionName;
		}
		public void setRegionName(String regionName) {
			this.regionName = regionName;
		}
		public ArrayList<String> getCompanies() {
			return companies;
		}
		public void setCompanies(ArrayList<String> companies) {
			this.companies = companies;
		}
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public ArrayList<State> getStates() {
		return states;
	}

	public void setStates(ArrayList<State> states) {
		this.states = states;
	}
	
}


