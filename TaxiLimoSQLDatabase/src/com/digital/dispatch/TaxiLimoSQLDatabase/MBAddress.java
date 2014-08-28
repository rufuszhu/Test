package com.digital.dispatch.TaxiLimoSQLDatabase;

import android.util.Log;

public class MBAddress {
	static final String TAG="MBDatabase";
	
	private int id;
	private int addressType;
    private String country;
    private String provinceOrState;
    private String district;
    private String houseNumber;
    private int houseNumberFirst;
    private String landmark;
    private String latitude;
    private String longitude;
    private String organization;
    private String streetName;
    private String unit;
    //link to route table
    private int addrRouteLink;
    private boolean mChanged;
    
    private MBAddressDBHandling mDbHandle;
    
    public MBAddress ()
    {
    	id = 0;
    	addressType = 0;
    	country = null;
    	provinceOrState = null;
    	district = null;
    	houseNumber = null;
    	houseNumberFirst = 1;
    	landmark = null;
        latitude = null;
        longitude = null;
        organization = null;
        streetName = null;
        unit = null;
    	addrRouteLink = 0;
    	mDbHandle = null;
    	mChanged = false;
    }
    
    public MBAddress (MBAddressDBHandling dbHdl)
    {
    	id = 0;
    	addressType = 0;
    	country = null;
    	provinceOrState = null;
    	district = null;
    	houseNumber = null;
    	houseNumberFirst = 1;
    	landmark = null;
        latitude = null;
        longitude = null;
        organization = null;
        streetName = null;
        unit = null;
    	addrRouteLink = 0;
    	mDbHandle = dbHdl;
    	mChanged = false;
    }
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAddressType() {
		return addressType;
	}
	public void setAddressType(int addressType) {
		this.addressType = addressType;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		mChanged = true;
		this.country = country;
	}
	public String getProvinceOrState() {
		return provinceOrState;
	}
	public void setProvinceOrState(String pOrS) {
		mChanged = true;
		this.provinceOrState = pOrS;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		mChanged = true;
		this.district = district;
	}
	public String getHouseNumber() {
		return houseNumber;
	}
	public void setHouseNumber(String houseNumber) {
		mChanged = true;
		this.houseNumber = houseNumber;
	}
	public int getHouseNumberFirst() {
		return houseNumberFirst;
	}
	public void setHouseNumberFirst(int houseNumberFirst) {
		mChanged = true;
		this.houseNumberFirst = houseNumberFirst;
	}
	public String getLandmark() {
		return landmark;
	}
	public void setLandmark(String landmark) {
		mChanged = true;
		this.landmark = landmark;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		mChanged = true;
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		mChanged = true;
		this.longitude = longitude;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		mChanged = true;
		this.organization = organization;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		mChanged = true;
		this.streetName = streetName;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		mChanged = true;
		this.unit = unit;
	}
	public int getAddrRouteLink() {
		return addrRouteLink;
	}
	public void setAddrRouteLink(int addrRouteLink) {
		mChanged = true;
		this.addrRouteLink = addrRouteLink;
	}
	

	public boolean compareAddr(MBAddress addr) {
		if (district != null && addr.district != null) {
			if (!district.equalsIgnoreCase(addr.district)) { return false; }
		}
		
		if (houseNumber != null && addr.houseNumber != null) {
			if (!houseNumber.equalsIgnoreCase(addr.houseNumber)) { return false; }
		}
		
		if (houseNumberFirst != addr.houseNumberFirst) { return false; }
		
		if (streetName != null && addr.streetName != null) {
			if (!streetName.equalsIgnoreCase(addr.streetName)) { return false; }
		}
	
		return true;
	}
	
	public String toString() {
	    if (unit != null) {
	    	return unit + " - " + houseNumber + " " + streetName + ", " + district;
	    }
	    else {
	    	return houseNumber + " " + streetName + ", " + district;
	    }
	}
	
	public void print()
	{
		if (DatabaseHandler.dbDebug) {
			Log.v(TAG, "----MBAddress =" + id + "----");
			Log.v(TAG, "   addressType =" + addressType);
			Log.v(TAG, "   country =" + country);
			Log.v(TAG, "   pOrState =" + provinceOrState);
			Log.v(TAG, "   district =" + district);
			Log.v(TAG, "   houseNumber =" + houseNumber);
			Log.v(TAG, "   houseNumberFirst =" + houseNumberFirst);
			Log.v(TAG, "   landmark =" + landmark);
			Log.v(TAG, "   latitude =" + latitude);
			Log.v(TAG, "   longitude =" + longitude);
			Log.v(TAG, "   organization =" + organization);
			Log.v(TAG, "   streetName =" + streetName);
			Log.v(TAG, "   unit =" + unit);
			Log.v(TAG, "   addrRouteLink =" + addrRouteLink);
			Log.v(TAG, "-------------------------------");
		}
	}
    
}
