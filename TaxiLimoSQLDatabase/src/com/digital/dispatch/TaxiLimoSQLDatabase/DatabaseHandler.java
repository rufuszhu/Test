package com.digital.dispatch.TaxiLimoSQLDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MBDatabase";

// MBBooking table name
    public static final String TABLE_MBBOOKING = "MBBookingTable";
    public static final String KEY_MBBOOKING_ID                 = "id";
    public static final String KEY_MBBOOKING_ADVISEARRIVAL      = "adviseArrival";
    public static final String KEY_MBBOOKING_ASAP               = "asap";
    public static final String KEY_MBBOOKING_AUTHNUM            = "authNum";
    public static final String KEY_MBBOOKING_CABNUM             = "cabNum";
    public static final String KEY_MBBOOKING_CARLATITUDE        = "carLatitude";
    public static final String KEY_MBBOOKING_CARLONGITUDE       = "carLongitude";
    public static final String KEY_MBBOOKING_DISPATCHEDCAR      = "dispathedCar";
    public static final String KEY_MBBOOKING_DISPATCHEDTIME     = "dispathedTime";
    public static final String KEY_MBBOOKING_JOBID              = "jobID";
    public static final String KEY_MBBOOKING_NUMBEROFPASSENGERS = "numberOfPassengers";
    public static final String KEY_MBBOOKING_NUMTAXIS           = "numTaxis";
    public static final String KEY_MBBOOKING_PASSENGERNAME      = "passengerName";
    public static final String KEY_MBBOOKING_PHONEEXT           = "phoneExt";
    public static final String KEY_MBBOOKING_PHONENUMBER        = "phoneNumber";
    public static final String KEY_MBBOOKING_PICKUPTIME         = "pickupTime";
    public static final String KEY_MBBOOKING_PRIORITY           = "priority";
    public static final String KEY_MBBOOKING_PRIORITYREASON     = "priorityReason";
    public static final String KEY_MBBOOKING_REMARKS            = "remarks";    
    public static final String KEY_MBBOOKING_TAXIPIN            = "taxiPin";    
    public static final String KEY_MBBOOKING_TAXIRIDEID         = "taxiRideID"; 
    public static final String KEY_MBBOOKING_TRIPCANCELLEDTIME  = "tripCancelledTime";  
    public static final String KEY_MBBOOKING_TRIPCREATIONTIME   = "tripCreationTime";   
    public static final String KEY_MBBOOKING_TRIPMODIFICATIONTIME = "tripModificationTime"; 
    public static final String KEY_MBBOOKING_TRIPCOMPLETIONTIME	= "tripCompletionTime";
    public static final String KEY_MBBOOKING_TRIPSTATUS         = "tripStatus";
    public static final String KEY_MBBOOKING_TRIPTYPE           = "tripType";
    public static final String KEY_MBBOOKING_LINK_ROUTE         = "routeLink";
    public static final String KEY_MBBOOKING_BOOKING_TYPE       = "bookingType";
    public static final String KEY_MBBOOKING_DEST_ID       		= "destID";
    public static final String KEY_MBBOOKING_SYSTEM_ID      	= "systemID";
    public static final String KEY_MBBOOKING_ATTRIBUTE			= "attribute";
    public static final String KEY_MBBOOKING_ALREADY_PAID	    = "already_paid";
    public static final String KEY_MBBOOKING_MULTI_PAY_ALLOW	= "multi_pay_allow";    
    
    
    
//MBRoute table
    public static final String TABLE_ROUTE = "MBRouteTable";
    public static final String KEY_ROUTE_ID						="id";
    public static final String KEY_ROUTE_FAVORITE				="favorite";
    //link to booking table
    public static final String KEY_ROUTE_LINK_BOOKINGID			="routeBookingLink";
    public static final String KEY_ROUTE_LINK_PICKUPADDR		="pickupAddrLink";
    public static final String KEY_ROUTE_LINK_DROPOFFADDR		="dropOffAddrLink";

//MBAddress table
    public static final String TABLE_ADDRESS = "MBAddressTable";
    public static final String KEY_ADDRESS_ID					= "id";
    public static final String KEY_ADDRESS_ADDRTYPE				= "addressType";
    public static final String KEY_ADDRESS_COUNTRY 				= "country";
    public static final String KEY_ADDRESS_PROVINCE_STATE		= "provinceAndState";
    public static final String KEY_ADDRESS_DISTRICT 			= "district";
    public static final String KEY_ADDRESS_HOUSENUMBER 			= "houseNumber";
    public static final String KEY_ADDRESS_HOUSENUMBERFIRST 	= "houseNumberFirst";
    public static final String KEY_ADDRESS_LANDMARK 			= "landmark";
    public static final String KEY_ADDRESS_LATITUDE 			= "latitude";
    public static final String KEY_ADDRESS_LONGITUDE 			= "longitude";
    public static final String KEY_ADDRESS_ORGANIZATION 		= "organization";
    public static final String KEY_ADDRESS_STREETNAME 			= "streetName";
    public static final String KEY_ADDRESS_UINT 				= "unit";
    //link to route table
    public static final String KEY_ADDRESS_LINK_ROUTE			= "addrRouteLink";
    
//MBAttributs table 
    public static final String TABLE_ATTRIBUTES = "MBAttributes";
    public static final String KEY_ATTRIBUTES_ID				= "id";
    public static final String KEY_ATTRIBUTES_ATTRLONGNAME		= "attrLongName";
    public static final String KEY_ATTRIBUTES_ATTRSHORTNAME		= "attrShortName";
    //link to bookingjob table
    public static final String KEY_ATTRIBUTES_LINK_BOOKING		= "attrBookingLink";
    
//MBCreditCard table 
    public static final String TABLE_CREDIT_CARD = "MBCreditCard";
    public static final String KEY_CREDIT_CARD_ID				= "id";
    public static final String KEY_CREDIT_CARD_CARD_NUMBER		= "cardNum";
    public static final String KEY_CREDIT_CARD_FIRST4_CARD_NUM	= "first4CardNum";
    public static final String KEY_CREDIT_CARD_HOLDER_NAME		= "holderName";
    public static final String KEY_CREDIT_CARD_EXIPRY			= "expiry";
    public static final String KEY_CREDIT_CARD_ADDRESS			= "address";
    public static final String KEY_CREDIT_CARD_EMAIL			= "email";
    public static final String KEY_CREDIT_CARD_NICKNAME			= "nickname";
    public static final String KEY_CREDIT_CARD_TOKEN			= "token";
    public static final String KEY_CREDIT_CARD_BRAND			= "brand";
    
//MBCompany table 
    public static final String TABLE_COMPANY = "MBCompany";
    public static final String KEY_COMPANY_ID					= "id";
    public static final String KEY_COMPANY_NAME					= "companyName";
    public static final String KEY_COMPANY_DEST_ID				= "destID";
    public static final String KEY_COMPANY_LOGO_LOC				= "logoLocation";
    public static final String KEY_COMPANY_LOGO_VERSION			= "logoVersion";
    public static final String KEY_COMPANY_ATTRIBUTES			= "attributes";

    
    public static boolean dbDebug = false;
        
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public static void enableDebugMsg ( boolean debugEnable) {
    	dbDebug = debugEnable;
    }
        
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MBBooking_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MBBOOKING + 
                "(" +
                KEY_MBBOOKING_ID 					+ " INTEGER PRIMARY KEY," + 
                KEY_MBBOOKING_ADVISEARRIVAL         + " INTEGER," +
                KEY_MBBOOKING_ASAP                  + " INTEGER," +
                KEY_MBBOOKING_AUTHNUM               + " TEXT," +
                KEY_MBBOOKING_CABNUM                + " INTEGER," +
                KEY_MBBOOKING_CARLATITUDE           + " TEXT," +
                KEY_MBBOOKING_CARLONGITUDE          + " TEXT," +
                KEY_MBBOOKING_DISPATCHEDCAR         + " TEXT," +
                KEY_MBBOOKING_DISPATCHEDTIME        + " TEXT," +
                KEY_MBBOOKING_JOBID                 + " INTEGER," +
                KEY_MBBOOKING_NUMBEROFPASSENGERS    + " INTEGER," +
                KEY_MBBOOKING_NUMTAXIS              + " INTEGER," +
                KEY_MBBOOKING_PASSENGERNAME         + " TEXT," +
                KEY_MBBOOKING_PHONEEXT              + " TEXT," +
                KEY_MBBOOKING_PHONENUMBER           + " TEXT," +
                KEY_MBBOOKING_PICKUPTIME            + " TEXT," +
                KEY_MBBOOKING_PRIORITY              + " INTEGER," +
                KEY_MBBOOKING_PRIORITYREASON        + " TEXT," +
                KEY_MBBOOKING_REMARKS               + " TEXT," +
                KEY_MBBOOKING_TAXIPIN               + " INTEGER," +
                KEY_MBBOOKING_TAXIRIDEID            + " INTEGER," +
                KEY_MBBOOKING_TRIPCANCELLEDTIME     + " TEXT," +
                KEY_MBBOOKING_TRIPCREATIONTIME      + " TEXT," +
                KEY_MBBOOKING_TRIPMODIFICATIONTIME  + " TEXT," +
                KEY_MBBOOKING_TRIPCOMPLETIONTIME  	+ " TEXT," +
                KEY_MBBOOKING_TRIPSTATUS            + " INTEGER," +
                KEY_MBBOOKING_TRIPTYPE              + " INTEGER," +
                KEY_MBBOOKING_LINK_ROUTE            + " INTEGER," +
                KEY_MBBOOKING_BOOKING_TYPE          + " INTEGER," +
                KEY_MBBOOKING_DEST_ID     		    + " INTEGER," +
                KEY_MBBOOKING_SYSTEM_ID				+ " INTEGER," +
                KEY_MBBOOKING_ATTRIBUTE				+ " TEXT," +
                KEY_MBBOOKING_ALREADY_PAID          + " INTEGER," +
                KEY_MBBOOKING_MULTI_PAY_ALLOW		+ " INTEGER" +
                ")";
        db.execSQL(CREATE_MBBooking_TABLE);
        
        String CREATE_MBAttributes_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ATTRIBUTES + 
                "(" +
                KEY_ATTRIBUTES_ID + " INTEGER PRIMARY KEY," + 
                KEY_ATTRIBUTES_ATTRLONGNAME         + " TEXT," +
                KEY_ATTRIBUTES_ATTRSHORTNAME        + " TEXT," +
                KEY_ATTRIBUTES_LINK_BOOKING         + " INTEGER" +
                ")";
        db.execSQL(CREATE_MBAttributes_TABLE);      
        
        String CREATE_MBRoute_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTE + 
                "(" +
                KEY_ROUTE_ID 						+ " INTEGER PRIMARY KEY," + 
                KEY_ROUTE_FAVORITE         			+ " INTEGER," +
                KEY_ROUTE_LINK_BOOKINGID        	+ " INTEGER," +
                KEY_ROUTE_LINK_PICKUPADDR         	+ " INTEGER," +
                KEY_ROUTE_LINK_DROPOFFADDR        	+ " INTEGER" +
                ")";
        db.execSQL(CREATE_MBRoute_TABLE);    
        
        String CREATE_MBADDRESS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ADDRESS + 
                "(" +
                KEY_ADDRESS_ID 						+ " INTEGER PRIMARY KEY," + 
                KEY_ADDRESS_ADDRTYPE         		+ " INTEGER," +
                KEY_ADDRESS_COUNTRY                 + " TEXT," +
                KEY_ADDRESS_DISTRICT                + " TEXT," +
                KEY_ADDRESS_HOUSENUMBER           	+ " TEXT," +
                KEY_ADDRESS_HOUSENUMBERFIRST        + " INTEGER," +
                KEY_ADDRESS_LANDMARK         		+ " TEXT," +
                KEY_ADDRESS_LATITUDE        		+ " TEXT," +
                KEY_ADDRESS_LONGITUDE               + " TEXT," +
                KEY_ADDRESS_ORGANIZATION    		+ " TEXT," +
                KEY_ADDRESS_STREETNAME              + " TEXT," +
                KEY_ADDRESS_UINT         			+ " TEXT," +
                KEY_ADDRESS_LINK_ROUTE				+ " INTEGER," +
                KEY_ADDRESS_PROVINCE_STATE          + " TEXT" +
                ")";
        db.execSQL(CREATE_MBADDRESS_TABLE);
        
        String CREATE_MBCREDITCARD_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CREDIT_CARD + 
                "(" +
                KEY_CREDIT_CARD_ID 					+ " INTEGER PRIMARY KEY," + 
                KEY_CREDIT_CARD_CARD_NUMBER			+ " TEXT," +
                KEY_CREDIT_CARD_FIRST4_CARD_NUM		+ " TEXT," +
                KEY_CREDIT_CARD_HOLDER_NAME			+ " TEXT," +
                KEY_CREDIT_CARD_EXIPRY				+ " TEXT," +
                KEY_CREDIT_CARD_ADDRESS				+ " TEXT," +
                KEY_CREDIT_CARD_EMAIL				+ " TEXT," +
                KEY_CREDIT_CARD_NICKNAME			+ " TEXT," +
                KEY_CREDIT_CARD_TOKEN				+ " TEXT," +	
                KEY_CREDIT_CARD_BRAND				+ " TEXT" +
                ")";
        db.execSQL(CREATE_MBCREDITCARD_TABLE);
        
        String CREATE_MBCOMPANY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_COMPANY + 
                "(" +
                KEY_COMPANY_ID						+ " INTEGER PRIMARY KEY," + 
        		KEY_COMPANY_DEST_ID					+ " INTEGER," +
        		KEY_COMPANY_NAME					+ " TEXT," +
        		KEY_COMPANY_LOGO_LOC				+ " TEXT," +
        		KEY_COMPANY_LOGO_VERSION			+ " INTEGER," +
        		KEY_COMPANY_ATTRIBUTES				+ " TEXT" +
                ")";
        db.execSQL(CREATE_MBCOMPANY_TABLE); 
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	// keep the original data just add the column "destID" into booking table
    	if (newVersion > oldVersion) {
//    		try {
//    			db.execSQL("ALTER TABLE " + TABLE_ADDRESS + " ADD COLUMN " + KEY_ADDRESS_PROVINCE_STATE + " TEXT");
//    		}
//    		catch (Exception e) {
//    			if (DatabaseHandler.dbDebug) {
//    				Log.v("Database Upgrade", "Province and state exist: " + e.toString());
//    			}
//    		}
//    		
//    		try {
//    			db.execSQL("ALTER TABLE " + TABLE_MBBOOKING + " ADD COLUMN " + KEY_MBBOOKING_DEST_ID + " INTEGER DEFAULT 0");
//    		}
//    		catch (Exception e) {
//    			if (DatabaseHandler.dbDebug) {
//    				Log.v("Database Upgrade", "Dest ID exist: " + e.toString());
//    			}
//    		}
//    		
//    		try {
//    			db.execSQL("ALTER TABLE " + TABLE_MBBOOKING + " ADD COLUMN " + KEY_MBBOOKING_SYSTEM_ID + " INTEGER DEFAULT 0");
//    		}
//    		catch (Exception e) {
//    			if (DatabaseHandler.dbDebug) {
//    				Log.v("Database Upgrade", "System ID exist: " + e.toString());
//    			}
//    		}
//    		
//    		try {
//    			db.execSQL("ALTER TABLE " + TABLE_MBBOOKING + " ADD COLUMN " + KEY_MBBOOKING_ATTRIBUTE + " TEXT");
//    		}
//    		catch (Exception e) {
//    			if (DatabaseHandler.dbDebug) {
//    				Log.v("Database Upgrade", "Attribute exist: " + e.toString());
//    			}
//    		}
//    		
//    		try{
//    			db.execSQL("ALTER TABLE " + TABLE_MBBOOKING + " ADD COLUMN " + KEY_MBBOOKING_ALREADY_PAID + " INTEGER DEFAULT 0");
//			}
//			catch (Exception e){
//				if (DatabaseHandler.dbDebug)
//				Log.v("Database Upgrade", KEY_MBBOOKING_ALREADY_PAID + "column already exist");
//			}
//    		
//    		try{
//    			db.execSQL("ALTER TABLE " + TABLE_MBBOOKING + " ADD COLUMN " + KEY_MBBOOKING_MULTI_PAY_ALLOW + " INTEGER DEFAULT 0");
//			}
//			catch (Exception e){
//				if (DatabaseHandler.dbDebug)
//				Log.v("Database Upgrade", KEY_MBBOOKING_MULTI_PAY_ALLOW + "column already exist");
//			}  		

    	}

        // Create tables again
        onCreate(db);
    }
}
