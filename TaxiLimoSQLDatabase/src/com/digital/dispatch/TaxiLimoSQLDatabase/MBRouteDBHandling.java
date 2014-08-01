package com.digital.dispatch.TaxiLimoSQLDatabase;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MBRouteDBHandling {
    private DatabaseHandler dbHdr;
    private static String TAG = "mbsqldatabasev2";
    
    public MBRouteDBHandling (DatabaseHandler dbHandler)
    {
        dbHdr = dbHandler;
    }
     
    public int addRoute(MBRoute Route) {
    	int rowId = 0;
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_ROUTE_FAVORITE, Route.getFavorite()); 
        values.put(DatabaseHandler.KEY_ROUTE_LINK_BOOKINGID, Route.getRouteBookingLink()); 
        values.put(DatabaseHandler.KEY_ROUTE_LINK_PICKUPADDR, Route.getPickupAddrLink()); 
        values.put(DatabaseHandler.KEY_ROUTE_LINK_DROPOFFADDR, Route.getDropoffAddrLink()); 
 
        // Inserting Row
        rowId = (int)db.insert(DatabaseHandler.TABLE_ROUTE, null, values);
        if (rowId > 0)
        {
        	Route.setId(rowId);
        }
        db.close(); // Closing database connection
        return rowId;
    }

	public MBRoute getRouteById(int id) {
		MBRoute Route = null;
		if (id > 0) {
			SQLiteDatabase db = dbHdr.getReadableDatabase();
			Cursor cursor = db
					.query(DatabaseHandler.TABLE_ROUTE, new String[] {
							DatabaseHandler.KEY_ROUTE_ID,
							DatabaseHandler.KEY_ROUTE_FAVORITE,
							DatabaseHandler.KEY_ROUTE_LINK_BOOKINGID,
							DatabaseHandler.KEY_ROUTE_LINK_PICKUPADDR,
							DatabaseHandler.KEY_ROUTE_LINK_DROPOFFADDR },
							DatabaseHandler.KEY_ROUTE_ID + "=?",
							new String[] { String.valueOf(id) }, null, null,
							null, null);

			try {
				if (cursor.moveToFirst()) {
					Route = new MBRoute();
					Route.setId(Integer.parseInt(cursor.getString(0)));
					Route.setFavorite(Integer.parseInt(cursor.getString(1)));
					Route.setRouteBookingLink(Integer.parseInt(cursor
							.getString(2)));
					Route.setPickupAddrLink(Integer.parseInt(cursor
							.getString(3)));
					Route.setDropoffAddrLink(Integer.parseInt(cursor
							.getString(4)));
				} else {
					if (DatabaseHandler.dbDebug)
						Log.e(TAG, "getRouteById: invalid Id = " + id);
					throw (new RuntimeException());
				}
			} catch (Exception e) {
				if (DatabaseHandler.dbDebug)
					Log.e(TAG, "getAllFavoriteRoutes excetpion");
				e.printStackTrace();
			} finally {
				cursor.close();
			}
			db.close();
		}
		return Route;
	}
    
    // Getting All MBRoute
    public List<MBRoute> getAllRoute() {
        List<MBRoute> RouteList = new ArrayList<MBRoute>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_ROUTE;

        SQLiteDatabase db = dbHdr.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					MBRoute Route = new MBRoute();
					Route.setId(Integer.parseInt(cursor.getString(0)));
					Route.setFavorite(Integer.parseInt(cursor.getString(1)));
					Route.setRouteBookingLink(Integer.parseInt(cursor
							.getString(2)));
					Route.setPickupAddrLink(Integer.parseInt(cursor
							.getString(3)));
					Route.setDropoffAddrLink(Integer.parseInt(cursor
							.getString(4)));
					// Adding contact to list
					RouteList.add(Route);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			if (DatabaseHandler.dbDebug)
				Log.e(TAG, "getAllFavoriteRoutes excetpion");
			e.printStackTrace();
		} finally {
			cursor.close();
		}
        db.close();
        return RouteList;
    }
    
    // Getting All MBRoute
    public List<MBRoute> getAllFavoriteRoutes() {
        List<MBRoute> RouteList = new ArrayList<MBRoute>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_ROUTE + " WHERE " +
        		DatabaseHandler.KEY_ROUTE_FAVORITE + "= 1";

        SQLiteDatabase db = dbHdr.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

		try {
			if (cursor.moveToFirst()) {
				do {
					MBRoute Route = new MBRoute();
					Route.setId(Integer.parseInt(cursor.getString(0)));
					Route.setFavorite(Integer.parseInt(cursor.getString(1)));
					Route.setRouteBookingLink(Integer.parseInt(cursor
							.getString(2)));
					Route.setPickupAddrLink(Integer.parseInt(cursor
							.getString(3)));
					Route.setDropoffAddrLink(Integer.parseInt(cursor
							.getString(4)));
					// Adding contact to list
					RouteList.add(Route);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			if (DatabaseHandler.dbDebug)
				Log.e(TAG, "getAllFavoriteRoutes excetpion");
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		db.close();
        return RouteList;
    }
    
    // Updating single contact
    public int updateRoute(MBRoute Route) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_ROUTE_FAVORITE, Route.getFavorite()); 
        values.put(DatabaseHandler.KEY_ROUTE_LINK_BOOKINGID, Route.getRouteBookingLink()); 
        values.put(DatabaseHandler.KEY_ROUTE_LINK_PICKUPADDR, Route.getPickupAddrLink()); 
        values.put(DatabaseHandler.KEY_ROUTE_LINK_DROPOFFADDR, Route.getDropoffAddrLink()); 
        // updating row
        return db.update(DatabaseHandler.TABLE_ROUTE, values, DatabaseHandler.KEY_ROUTE_ID + " =?",
                new String[] { String.valueOf(Route.getId()) });
    }

    // Deleting single contact
    private void deleteRoute(MBRoute Route) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        db.delete(DatabaseHandler.TABLE_ROUTE, DatabaseHandler.KEY_ROUTE_ID + " = ?",
                new String[] { String.valueOf(Route.getId()) });
        db.close();
    }

    // Deleting single contact
    public void deleteRouteById(int routeId) {
        int pickupAddrId;
        int dropoffAddrId; 
        MBAddressDBHandling tmpAddr;
    	MBRoute route= getRouteById (routeId);
    	if ( route == null)
    	{
    		if (DatabaseHandler.dbDebug)
    			Log.e(TAG, "deleteRouteById, id is invalid");
    		return;
    	}
    	pickupAddrId = route.getPickupAddrLink();
    	dropoffAddrId = route.getDropoffAddrLink();

    	tmpAddr = new MBAddressDBHandling(this.dbHdr);
		if (pickupAddrId > 0) {
			tmpAddr.deleteAddressById(pickupAddrId);
		}
		if (dropoffAddrId > 0) {
			tmpAddr.deleteAddressById(dropoffAddrId);
		}
    	deleteRoute (route);
       
    }

    // Getting MBRoute Count
    public int getRouteCount() {
        String countQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_ROUTE;
        SQLiteDatabase db = dbHdr.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int routeCount = cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return routeCount;

    }
    
    public MBRoute copyRoute (int srcRtId)
    {
    	MBRoute desRt = null;
    	MBAddressDBHandling addrDbHdl = new MBAddressDBHandling(dbHdr);
    	MBAddress pickAddrObj = null;
    	MBAddress dropAddrObj = null;
    	if ( srcRtId <= 0)
    	{
    		
    	}
    	else
    	{
    		desRt = getRouteById ( srcRtId);
    		if ( desRt != null)
    		{
    			desRt.setId(0);
    			if ( desRt.getPickupAddrLink() > 0)
    			{
    				pickAddrObj = addrDbHdl.copyAddress(desRt.getPickupAddrLink());
    				if ( pickAddrObj != null)
    				{
    					desRt.setPickupAddrLink(pickAddrObj.getId());
    				}
    				else
    				{
    					if (DatabaseHandler.dbDebug)
    						Log.e(TAG, "copyRoute: cannot copy pickup Addr id=" + desRt.getPickupAddrLink());
    					throw ( new RuntimeException());
    				}
    			}
    			if ( desRt.getDropoffAddrLink() > 0)
    			{
    				dropAddrObj =  addrDbHdl.copyAddress(desRt.getDropoffAddrLink());
    				if ( dropAddrObj != null)
    				{
    					desRt.setDropoffAddrLink(dropAddrObj.getId());
    				}
    				else
    				{
    					if (DatabaseHandler.dbDebug)
    						Log.e(TAG, "copyRoute: cannot copy dropoff Addr id=" + desRt.getPickupAddrLink());
    					throw ( new RuntimeException());
    				}
    			}
    			addRoute(desRt);
    		}
    	}
    	
    	return desRt;
    }
    
    private boolean isEqualString ( String str1, String str2)
    {
    	boolean bResult = false;
    	if (( str1== null) && (str2 == null))
    	{
    		bResult = true;
    	}
    	else if (( str1 == null) || (str2 == null))
    	{
    		bResult = false;
    	}
    	else
    	{
    		bResult = str1.equalsIgnoreCase(str2);
    	}
    	return bResult;
    }
    
    private boolean isAddressObjEqual ( MBAddress addrObj1, MBAddress addrObj2)
    {
    	boolean bResult = false;
    	if (( addrObj1 == null) && ( addrObj2 == null))
    	{
    		bResult = true;
    	}
    	else if (( addrObj1 == null) || (addrObj2 == null))
    	{
    		bResult = false;  		
    	}
    	else
    	{
    		String unitNum1 = addrObj1.getUnit();
    		String stNum1 = addrObj1.getHouseNumber();
    		String stName1 = addrObj1.getStreetName();
    		String stDistrict1 = addrObj1.getDistrict();
    		
    		String unitNum2 = addrObj2.getUnit();
    		String stNum2 = addrObj2.getHouseNumber();
    		String stName2 = addrObj2.getStreetName();
    		String stDistrict2 = addrObj2.getDistrict();
    		
    		if ( (isEqualString(unitNum1, unitNum2)) &&
    				(isEqualString ( stNum1, stNum2)) &&
    				(isEqualString(stName1, stName2)) &&
    				(isEqualString(stDistrict1, stDistrict2)))
    		{
    			bResult = true;
    		}	
    	}
 
    	return bResult;
    }
    
    public boolean isRouteDuplicateInFavorite ( MBRoute routeObj)
    {
    	boolean isDuplicate = false;
		List<MBRoute> favRtList = getAllFavoriteRoutes();   

		MBAddressDBHandling addrDbHdl = new MBAddressDBHandling(dbHdr);
		
		MBAddress pkAddrObj = null;
		MBAddress dpAddrObj = null;
		
    	if ( routeObj == null)
    	{
    		return isDuplicate;
    	}

		pkAddrObj = addrDbHdl.getAddressById(routeObj.getPickupAddrLink());
		dpAddrObj = addrDbHdl.getAddressById(routeObj.getDropoffAddrLink());
    	
		for ( MBRoute favRt : favRtList)
		{
			MBAddress favPkAddrObj = null;
			MBAddress favDpAddrObj = null;
			favPkAddrObj = addrDbHdl.getAddressById(favRt.getPickupAddrLink());
			favDpAddrObj = addrDbHdl.getAddressById(favRt.getDropoffAddrLink());		
			if (( isAddressObjEqual(pkAddrObj, favPkAddrObj)) && ( isAddressObjEqual(dpAddrObj, favDpAddrObj)))
			{
				isDuplicate = true;
				break;
			}
		}   		
		return isDuplicate;
    }        
}
