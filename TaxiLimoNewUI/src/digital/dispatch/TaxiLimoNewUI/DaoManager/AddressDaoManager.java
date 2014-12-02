package digital.dispatch.TaxiLimoNewUI.DaoManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.text.TextUtils;

import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import digital.dispatch.TaxiLimoNewUI.Book.MyContact;

import android.content.Context;
import android.location.Address;
import android.util.Log;

public class AddressDaoManager {
	private final static String TAG = "AddressDaoManager";
	public static DBAddress addDaoAddressByAddress(Address address,String unit, String nickName, boolean isFavoriate, DBAddressDao addressDao) {

		String streetName = getStreetNameFromAddress(address);
		String houseNumber = getHouseNumberFromAddress(address);
		String district = address.getLocality();
		String province = address.getAdminArea();
		String country = address.getCountryName();
		Double latitude = address.getLatitude();
		Double longitude = address.getLongitude();
		DBAddress dbaddress = new DBAddress(null, unit, streetName, houseNumber, district, province, country, nickName, latitude, longitude
				,isFavoriate,"");
		dbaddress.setFullAddress(dbAddressToString(dbaddress));
		addressDao.insert(dbaddress);
		
		Logger.d(TAG , "Inserted new note, ID: " + dbaddress.getId());
		return dbaddress;

	}

	public static String getHouseNumberFromAddress(Address address) {
		// If there's a street address, add it
		String ad = address.getMaxAddressLineIndex() > 0 ? address
				.getAddressLine(0) : "";
		String[] strArray = TextUtils.split(ad, " ");
		if (strArray.length>0 && Utils.isNumeric(strArray[0])) {
			return strArray[0];
		} else
			return "";
	}
	
	public static String getHouseNumberFromAddress(String address) {
		// If there's a street address, add it
		String[] strArray = TextUtils.split(address, " ");
		if (Utils.isNumeric(strArray[0])) {
			return strArray[0];
		} else
			return "";
	}

	public static String getStreetNameFromAddress(Address address) {
		// If there's a street address, add it
		String ad = address.getMaxAddressLineIndex() > 0 ? address
				.getAddressLine(0) : "";
		String[] strArray = TextUtils.split(ad, " ");
		//if no street number, return the original string
		if(!Utils.isNumeric(strArray[0]))
			return ad;
		//has street number, chop it
		if (strArray.length > 1) {
			String temp="";
			for(int i=1;i< strArray.length;i++){
				temp += strArray[i] + " ";
			}
			return temp;
		} 
		else if(strArray.length==1)
			return strArray[0];
		else
			return "";
	}
	
	public static String getStreetNameFromAddress(String address) {
		// If there's a street address, add it
		String[] strArray = TextUtils.split(address, " ");
		//if no street number, return the original string
		if(!Utils.isNumeric(strArray[0]))
			return address;
		
		if (strArray.length > 1) {
			String temp="";
			for(int i=1;i< strArray.length;i++){
				temp += strArray[i] + " ";
			}
			return temp;
		} 
		else if(strArray.length==1)
			return strArray[0];
		else
			return "";
	}
	
	
	
	public static ArrayList<MyContact> dbAddressListToMyAddressList(List<DBAddress> dbAddresses){
		ArrayList<MyContact> temp = new ArrayList<MyContact>();
		for(int i=0 ; i< dbAddresses.size(); i++){
			DBAddress dba = dbAddresses.get(i);
			MyContact ma = new MyContact(null,dba.getNickName(),dba.getFullAddress(), dba.getId());
			temp.add(ma);
		}
		return temp;
	}
	
	public static String dbAddressToString(DBAddress address){
		String str = new String("");
		if(address.getUnit()!=null && address.getUnit().length()>0)
			str += address.getUnit() + "-";
		
		if(address.getHouseNumber()!=null && address.getHouseNumber().length()>0)
			str += address.getHouseNumber() + " ";
		
		if(address.getStreetName()!=null  && address.getStreetName().length()>0)
			str += address.getStreetName() + ", ";
		
		if(address.getDistrict()!=null  && address.getDistrict().length()>0)
			str += address.getDistrict() + ", ";
		
		if(address.getProvince()!=null  && address.getProvince().length()>0)
			str += address.getProvince() + ", ";
		
		if(address.getCountry()!=null  && address.getCountry().length()>0)
			str += address.getCountry();

		return str;
	}



}
