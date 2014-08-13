package digital.dispatch.TaxiLimoNewUI.DaoManager;

import java.text.DateFormat;
import java.util.Date;
import android.text.TextUtils;

import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;

import android.location.Address;
import android.util.Log;

public class AddressDaoManager {
	public static void addDaoAddressByAddress(Address address,String unit, boolean isFavoriate, DBAddressDao addressDao) {

		String streetName = getStreetNameFromAddress(address);
		String houseNumber = getHouseNumberFromAddress(address);
		String district = address.getLocality();
		String province = address.getAdminArea();
		String country = address.getCountryName();
		Double latitude = address.getLatitude();
		Double longitude = address.getLongitude();
		DBAddress dbaddress = new DBAddress(null, unit, streetName, houseNumber, district, province, country, latitude, longitude
				,isFavoriate);
		addressDao.insert(dbaddress);
		Log.d("DaoExample", "Inserted new note, ID: " + dbaddress.getId());

	}

	public static String getHouseNumberFromAddress(Address address) {
		// If there's a street address, add it
		String ad = address.getMaxAddressLineIndex() > 0 ? address
				.getAddressLine(0) : "";
		String[] strArray = TextUtils.split(ad, " ");
		if (strArray.length > 1) {
			return strArray[0];
		} else
			return "";
	}

	public static String getStreetNameFromAddress(Address address) {
		// If there's a street address, add it
		String ad = address.getMaxAddressLineIndex() > 0 ? address
				.getAddressLine(0) : "";
		String[] strArray = TextUtils.split(ad, " ");
		if (strArray.length > 1) {
			return strArray[1];
		} else
			return "";
	}



}
