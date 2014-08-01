package com.digital.dispatch.TaxiLimoSQLDatabase;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MBAddressDBHandling {
    private DatabaseHandler dbHdr;
    
    public MBAddressDBHandling (DatabaseHandler dbHandler)
    {
        dbHdr = dbHandler;
    }
   
    static private String TAG = "mbsqldatabase";
    
    public int addAddress(MBAddress address) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        ContentValues values = new ContentValues();
        int rowId;
        values.put(DatabaseHandler.KEY_ADDRESS_ADDRTYPE, address.getAddressType()); 
        values.put(DatabaseHandler.KEY_ADDRESS_COUNTRY, address.getCountry()); 
        values.put(DatabaseHandler.KEY_ADDRESS_DISTRICT, address.getDistrict()); 
        values.put(DatabaseHandler.KEY_ADDRESS_HOUSENUMBER, address.getHouseNumber()); 
        values.put(DatabaseHandler.KEY_ADDRESS_HOUSENUMBERFIRST, address.getHouseNumberFirst()); 
        values.put(DatabaseHandler.KEY_ADDRESS_LANDMARK, address.getLandmark()); 
        values.put(DatabaseHandler.KEY_ADDRESS_LATITUDE, address.getLatitude()); 
        values.put(DatabaseHandler.KEY_ADDRESS_LONGITUDE, address.getLongitude()); 
        values.put(DatabaseHandler.KEY_ADDRESS_ORGANIZATION, address.getOrganization()); 
        values.put(DatabaseHandler.KEY_ADDRESS_STREETNAME, address.getStreetName()); 
        values.put(DatabaseHandler.KEY_ADDRESS_UINT, address.getUnit()); 
        values.put(DatabaseHandler.KEY_ADDRESS_LINK_ROUTE, address.getAddrRouteLink());
        values.put(DatabaseHandler.KEY_ADDRESS_PROVINCE_STATE, address.getProvinceOrState());
        // Inserting Row
        rowId = (int)db.insert(DatabaseHandler.TABLE_ADDRESS, null, values);
        if ( rowId > 0)
        {
        	address.setId(rowId);
        }
        db.close(); // Closing database connection
        return rowId;
    }

    public MBAddress getAddressById(int id) {
        SQLiteDatabase db = dbHdr.getReadableDatabase();

		Cursor cursor = db.query(DatabaseHandler.TABLE_ADDRESS, null,
				DatabaseHandler.KEY_ADDRESS_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

        MBAddress address;
        
        if (cursor.moveToFirst()) {
            address = new MBAddress();
            address.setId(Integer.parseInt(cursor.getString(0)));
            address.setAddressType(Integer.parseInt(cursor.getString(1)));
            address.setCountry(cursor.getString(2));
            address.setDistrict(cursor.getString(3));
            address.setHouseNumber(cursor.getString(4));
            address.setHouseNumberFirst(Integer.parseInt(cursor.getString(5)));
            address.setLandmark(cursor.getString(6));
            address.setLatitude(cursor.getString(7));
            address.setLongitude(cursor.getString(8));
            address.setOrganization(cursor.getString(9));;
            address.setStreetName(cursor.getString(10));
            address.setUnit(cursor.getString(11));
            address.setAddrRouteLink(Integer.parseInt(cursor.getString(12)));
            address.setProvinceOrState(cursor.getString(13));
        }
        else {
        	cursor.close();
        	return null;
        }
        
        cursor.close();
        db.close();
        return address;
    }
    
    // Getting All MBAddress
    public List<MBAddress> getAllAddress() {
        List<MBAddress> addressList = new ArrayList<MBAddress>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_ADDRESS;

        SQLiteDatabase db = dbHdr.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MBAddress address = new MBAddress();
                address.setId(Integer.parseInt(cursor.getString(0)));
                address.setAddressType(Integer.parseInt(cursor.getString(1)));
                address.setCountry(cursor.getString(2));
                address.setDistrict(cursor.getString(3));
                address.setHouseNumber(cursor.getString(4));
                address.setHouseNumberFirst(Integer.parseInt(cursor.getString(5)));
                address.setLandmark(cursor.getString(6));
                address.setLatitude(cursor.getString(7));
                address.setLongitude(cursor.getString(8));
                address.setOrganization(cursor.getString(9));
                address.setStreetName(cursor.getString(10));
                address.setUnit(cursor.getString(11));
                address.setAddrRouteLink(Integer.parseInt(cursor.getString(12)));
                address.setProvinceOrState(cursor.getString(13));
                addressList.add(address);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return addressList;
    }

    // Updating single contact
    public int updateAddress(MBAddress address) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_ADDRESS_ADDRTYPE, address.getAddressType()); 
        values.put(DatabaseHandler.KEY_ADDRESS_COUNTRY, address.getCountry()); 
        values.put(DatabaseHandler.KEY_ADDRESS_DISTRICT, address.getDistrict()); 
        values.put(DatabaseHandler.KEY_ADDRESS_HOUSENUMBER, address.getHouseNumber()); 
        values.put(DatabaseHandler.KEY_ADDRESS_HOUSENUMBERFIRST, address.getHouseNumberFirst()); 
        values.put(DatabaseHandler.KEY_ADDRESS_LANDMARK, address.getLandmark()); 
        values.put(DatabaseHandler.KEY_ADDRESS_LATITUDE, address.getLatitude()); 
        values.put(DatabaseHandler.KEY_ADDRESS_LONGITUDE, address.getLongitude()); 
        values.put(DatabaseHandler.KEY_ADDRESS_ORGANIZATION, address.getOrganization()); 
        values.put(DatabaseHandler.KEY_ADDRESS_STREETNAME, address.getStreetName()); 
        values.put(DatabaseHandler.KEY_ADDRESS_UINT, address.getUnit()); 
        values.put(DatabaseHandler.KEY_ADDRESS_LINK_ROUTE, address.getAddrRouteLink());
        values.put(DatabaseHandler.KEY_ADDRESS_PROVINCE_STATE, address.getProvinceOrState()); 

        // updating row
        return db.update(DatabaseHandler.TABLE_ADDRESS, values, DatabaseHandler.KEY_ADDRESS_ID + " =?",
                new String[] { String.valueOf(address.getId()) });
    }

    // Deleting single contact
    public void deleteAddress(MBAddress address) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        db.delete(DatabaseHandler.TABLE_ADDRESS, DatabaseHandler.KEY_ADDRESS_ID + " =?",
                new String[] { String.valueOf(address.getId()) });
        db.close();
    }
    
    // Deleting single contact
    public void deleteAddressById(int addrId) {
    	MBAddress tmpAddr;
    	tmpAddr = getAddressById(addrId);
    	
    	if ( tmpAddr == null) {
    		if (DatabaseHandler.dbDebug)
    			Log.e(TAG, "deleteAddressById, addrId is inValid");
    		throw ( new RuntimeException());
    	}
    	
    	deleteAddress(tmpAddr);
    }

    // Getting MBAddress Count
    public int getAddressCount() {
        String countQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_ADDRESS;
        SQLiteDatabase db = dbHdr.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    
    public MBAddress copyAddress ( int srcAddrId)
    {
    	MBAddress desAddrObj = null;
    	if ( srcAddrId <= 0) {
    		return null;
    	}
    	
    	desAddrObj = getAddressById(srcAddrId);
    	
    	if ( desAddrObj != null) {
    		desAddrObj.setId(0);
    		addAddress(desAddrObj);
    		if (DatabaseHandler.dbDebug)
    			Log.v(TAG, "copyAddress desId =" + desAddrObj.getId());
    	}
    	
    	return desAddrObj;
    
    }
}
