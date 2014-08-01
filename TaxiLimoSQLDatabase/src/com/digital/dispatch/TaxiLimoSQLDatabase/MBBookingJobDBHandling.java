package com.digital.dispatch.TaxiLimoSQLDatabase;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.digital.dispatch.TaxiLimoSQLDatabase.DatabaseHandler;
import com.digital.dispatch.TaxiLimoSQLDatabase.MBBooking;

public class MBBookingJobDBHandling {
	static private String TAG = "mbsqldatabasev2";
	static private String ORDER = DatabaseHandler.KEY_MBBOOKING_TRIPCREATIONTIME + " DESC";
    private DatabaseHandler dbHdr;
    
    public MBBookingJobDBHandling (DatabaseHandler dbHandler)
    {
        dbHdr = dbHandler;
    }

    public int addBookingJob(MBBooking bookingJob) {
    	int RowId = 0;
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_MBBOOKING_ADVISEARRIVAL, bookingJob.getAdviseArrival()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_ASAP, bookingJob.getAsap()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_AUTHNUM, bookingJob.getAuthNum()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_CABNUM, bookingJob.getCabNum()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_CARLATITUDE, bookingJob.getCarLatitude()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_CARLONGITUDE, bookingJob.getCarLongitude()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_DISPATCHEDCAR, bookingJob.getDispatchedCar()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_DISPATCHEDTIME, bookingJob.getDispatchedTime()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_JOBID, bookingJob.getJobID()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_NUMBEROFPASSENGERS, bookingJob.getNumberOfPassengers()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_NUMTAXIS, bookingJob.getNumTaxis()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_PASSENGERNAME, bookingJob.getPassengerName()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_PHONEEXT, bookingJob.getPhoneExt()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_PHONENUMBER, bookingJob.getPhoneNumber()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_PICKUPTIME, bookingJob.getPickupTime()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_PRIORITY, bookingJob.getPriority()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_PRIORITYREASON, bookingJob.getPriorityReason()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_REMARKS, bookingJob.getRemarks()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TAXIPIN, bookingJob.getTaxiPIN()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TAXIRIDEID, bookingJob.getTaxiRideID()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TRIPCANCELLEDTIME, bookingJob.getTripCancelledTime()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TRIPCREATIONTIME, bookingJob.getTripCreationTime()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TRIPMODIFICATIONTIME, bookingJob.getTripModificationTime());
        values.put(DatabaseHandler.KEY_MBBOOKING_TRIPCOMPLETIONTIME, bookingJob.getTripCompletionTime()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TRIPSTATUS, bookingJob.getTripStatus()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TRIPTYPE, bookingJob.getTripType()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_LINK_ROUTE, bookingJob.getRouteLink()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_BOOKING_TYPE, bookingJob.getBookingType());
        values.put(DatabaseHandler.KEY_MBBOOKING_DEST_ID, bookingJob.getDestId());
        values.put(DatabaseHandler.KEY_MBBOOKING_SYSTEM_ID, bookingJob.getSysId());
        values.put(DatabaseHandler.KEY_MBBOOKING_ATTRIBUTE, bookingJob.getAttribute());
        values.put(DatabaseHandler.KEY_MBBOOKING_ALREADY_PAID, bookingJob.getAlready_paid());
        values.put(DatabaseHandler.KEY_MBBOOKING_MULTI_PAY_ALLOW, bookingJob.getMulti_pay_allow());
        
      
        // Inserting Row
        RowId =(int)db.insert(DatabaseHandler.TABLE_MBBOOKING, null, values);
        if ( RowId > 0)
        {
        	bookingJob.setId(RowId);
        }
        db.close(); // Closing database connection
        return RowId;
    }

    public MBBooking getBookingJobById(int id) {
        SQLiteDatabase db = dbHdr.getReadableDatabase();
        Cursor cursor = null;
		cursor = db.query(DatabaseHandler.TABLE_MBBOOKING, null,
				DatabaseHandler.KEY_MBBOOKING_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, ORDER, null);
		
        MBBooking bookingJob;
        if ( cursor.getCount() >1)
        {
        	if (DatabaseHandler.dbDebug)
        		Log.e(TAG, "getBookingJobById, duplicated primary key"+cursor.getCount());
        }
        
        if (cursor.moveToFirst())
        {           
            bookingJob = new MBBooking();
            bookingJob.setId(Integer.parseInt(cursor.getString(0)));
            bookingJob.setAdviseArrival(Integer.parseInt(cursor.getString(1)));
            bookingJob.setAsap(Integer.parseInt(cursor.getString(2)));
            bookingJob.setAuthNum(cursor.getString(3));
            bookingJob.setCabNum(Integer.parseInt(cursor.getString(4)));
            bookingJob.setCarLatitude(cursor.getString(5));
            bookingJob.setCarLongitude(cursor.getString(6));
            bookingJob.setDispatchedCar(cursor.getString(7));
            bookingJob.setDispatchedTime(cursor.getString(8));
            bookingJob.setJobID(Integer.parseInt(cursor.getString(9)));
            bookingJob.setNumberOfPassengers(Integer.parseInt(cursor.getString(10)));
            bookingJob.setNumTaxis(Integer.parseInt(cursor.getString(11)));
            bookingJob.setPassengerName(cursor.getString(12));
            bookingJob.setPhoneExt((cursor.getString(13)));
            bookingJob.setPhoneNumber((cursor.getString(14)));
            bookingJob.setPickupTime((cursor.getString(15)));
            bookingJob.setPriority(Integer.parseInt(cursor.getString(16)));
            bookingJob.setPriorityReason((cursor.getString(17)));
            bookingJob.setRemarks((cursor.getString(18)));
            bookingJob.setTaxiPIN(Integer.parseInt(cursor.getString(19)));
            bookingJob.setTaxiRideID(Integer.parseInt(cursor.getString(20)));
            bookingJob.setTripCancelledTime((cursor.getString(21)));
            bookingJob.setTripCreationTime((cursor.getString(22)));
            bookingJob.setTripModificationTime((cursor.getString(23)));
            bookingJob.setTripCreationTime((cursor.getString(24)));
            bookingJob.setTripStatus(Integer.parseInt(cursor.getString(25)));
            bookingJob.setTripType(Integer.parseInt(cursor.getString(26)));
            bookingJob.setRouteLink(Integer.parseInt(cursor.getString(27)));
            bookingJob.setBookingType(Integer.parseInt(cursor.getString(28)));
            bookingJob.setDestId(Integer.parseInt(cursor.getString(29)));
            bookingJob.setSysId(Integer.parseInt(cursor.getString(30)));
            bookingJob.setAttribute(cursor.getString(31));
            bookingJob.setAlready_paid(Integer.parseInt(cursor.getString(32)));
            bookingJob.setMulti_pay_allow(Integer.parseInt(cursor.getString(33)));
        }
        else
        {
        	cursor.close();
        	return null;
        }
        
        cursor.close();
        return bookingJob;
    }
    
    
    public MBBooking getBookingJobByTripID(int tripID) {
        SQLiteDatabase db = dbHdr.getReadableDatabase();

		Cursor cursor = db.query(DatabaseHandler.TABLE_MBBOOKING, new String[] {
				DatabaseHandler.KEY_MBBOOKING_ID,
				DatabaseHandler.KEY_MBBOOKING_ADVISEARRIVAL,
				DatabaseHandler.KEY_MBBOOKING_ASAP,
				DatabaseHandler.KEY_MBBOOKING_AUTHNUM,
				DatabaseHandler.KEY_MBBOOKING_CABNUM,
				DatabaseHandler.KEY_MBBOOKING_CARLATITUDE,
				DatabaseHandler.KEY_MBBOOKING_CARLONGITUDE,
				DatabaseHandler.KEY_MBBOOKING_DISPATCHEDCAR,
				DatabaseHandler.KEY_MBBOOKING_DISPATCHEDTIME,
				DatabaseHandler.KEY_MBBOOKING_JOBID,
				DatabaseHandler.KEY_MBBOOKING_NUMBEROFPASSENGERS,
				DatabaseHandler.KEY_MBBOOKING_NUMTAXIS,
				DatabaseHandler.KEY_MBBOOKING_PASSENGERNAME,
				DatabaseHandler.KEY_MBBOOKING_PHONEEXT,
				DatabaseHandler.KEY_MBBOOKING_PHONENUMBER,
				DatabaseHandler.KEY_MBBOOKING_PICKUPTIME,
				DatabaseHandler.KEY_MBBOOKING_PRIORITY,
				DatabaseHandler.KEY_MBBOOKING_PRIORITYREASON,
				DatabaseHandler.KEY_MBBOOKING_REMARKS,
				DatabaseHandler.KEY_MBBOOKING_TAXIPIN,
				DatabaseHandler.KEY_MBBOOKING_TAXIRIDEID,
				DatabaseHandler.KEY_MBBOOKING_TRIPCANCELLEDTIME,
				DatabaseHandler.KEY_MBBOOKING_TRIPCREATIONTIME,
				DatabaseHandler.KEY_MBBOOKING_TRIPMODIFICATIONTIME,
				DatabaseHandler.KEY_MBBOOKING_TRIPCOMPLETIONTIME,
				DatabaseHandler.KEY_MBBOOKING_TRIPSTATUS,
				DatabaseHandler.KEY_MBBOOKING_TRIPTYPE,
				DatabaseHandler.KEY_MBBOOKING_LINK_ROUTE,
				DatabaseHandler.KEY_MBBOOKING_BOOKING_TYPE,
				DatabaseHandler.KEY_MBBOOKING_DEST_ID,
				DatabaseHandler.KEY_MBBOOKING_SYSTEM_ID,
				DatabaseHandler.KEY_MBBOOKING_ATTRIBUTE,
				DatabaseHandler.KEY_MBBOOKING_ALREADY_PAID,
				DatabaseHandler.KEY_MBBOOKING_MULTI_PAY_ALLOW},
				DatabaseHandler.KEY_MBBOOKING_TAXIRIDEID + "=?",
				new String[] { String.valueOf(tripID) }, null, null, ORDER, null);
		
        MBBooking bookingJob;
        if (cursor.moveToFirst())
        {
			do {
				bookingJob = new MBBooking();
				bookingJob.setId(Integer.parseInt(cursor.getString(0)));
				bookingJob.setAdviseArrival(Integer.parseInt(cursor.getString(1)));
				bookingJob.setAsap(Integer.parseInt(cursor.getString(2)));
				bookingJob.setAuthNum(cursor.getString(3));
				bookingJob.setCabNum(Integer.parseInt(cursor.getString(4)));
				bookingJob.setCarLatitude(cursor.getString(5));
				bookingJob.setCarLongitude(cursor.getString(6));
				bookingJob.setDispatchedCar(cursor.getString(7));
				bookingJob.setDispatchedTime(cursor.getString(8));
				bookingJob.setJobID(Integer.parseInt(cursor.getString(9)));
				bookingJob.setNumberOfPassengers(Integer.parseInt(cursor.getString(10)));
				bookingJob.setNumTaxis(Integer.parseInt(cursor.getString(11)));
				bookingJob.setPassengerName(cursor.getString(12));
				bookingJob.setPhoneExt((cursor.getString(13)));
				bookingJob.setPhoneNumber((cursor.getString(14)));
				bookingJob.setPickupTime((cursor.getString(15)));
				bookingJob.setPriority(Integer.parseInt(cursor.getString(16)));
				bookingJob.setPriorityReason((cursor.getString(17)));
				bookingJob.setRemarks((cursor.getString(18)));
				bookingJob.setTaxiPIN(Integer.parseInt(cursor.getString(19)));
				bookingJob.setTaxiRideID(Integer.parseInt(cursor.getString(20)));
				bookingJob.setTripCancelledTime((cursor.getString(21)));
				bookingJob.setTripCreationTime((cursor.getString(22)));
				bookingJob.setTripModificationTime((cursor.getString(23)));
				bookingJob.setTripCompletionTime((cursor.getString(24)));
				bookingJob.setTripStatus(Integer.parseInt(cursor.getString(25)));
				bookingJob.setTripType(Integer.parseInt(cursor.getString(26)));
				bookingJob.setRouteLink(Integer.parseInt(cursor.getString(27)));
				bookingJob.setBookingType(Integer.parseInt(cursor.getString(28)));
				bookingJob.setDestId(Integer.parseInt(cursor.getString(29)));
				bookingJob.setSysId(Integer.parseInt(cursor.getString(30)));
				bookingJob.setAttribute(cursor.getString(31));
				bookingJob.setAlready_paid(Integer.parseInt(cursor.getString(32)));
				bookingJob.setMulti_pay_allow(Integer.parseInt(cursor.getString(33)));
			} while (cursor.moveToNext());
        }
        else
        {
        	cursor.close();
        	return null;
        }
        
        cursor.close();
        return bookingJob;
    }    
    
    public List<MBBooking> getBookingJobByBookingType(int bookingType) {
        SQLiteDatabase db = dbHdr.getReadableDatabase();
        Cursor cursor = null;
        List<MBBooking> bookingJobList = new ArrayList<MBBooking>();
        if (DatabaseHandler.dbDebug)
        	Log.v(TAG, "++getBookingJobByBookingType");        
        
		cursor = db.query(DatabaseHandler.TABLE_MBBOOKING, null,
				DatabaseHandler.KEY_MBBOOKING_BOOKING_TYPE + "=?",
				new String[] { String.valueOf(bookingType)}, null, 
				null, ORDER, null);
		
		if (DatabaseHandler.dbDebug)
			Log.v(TAG, "getBookingJobByBookingType, raw query search all coulmns" + cursor.getCount());

		
        MBBooking bookingJob;
        if (cursor.moveToFirst())
        {
			do {
				if (DatabaseHandler.dbDebug)
					Log.v(TAG, "getBookingJobByBookingType:cursor != null");
				bookingJob = new MBBooking();
				bookingJob.setId(Integer.parseInt(cursor.getString(0)));
				bookingJob.setAdviseArrival(Integer.parseInt(cursor.getString(1)));
				bookingJob.setAsap(Integer.parseInt(cursor.getString(2)));
				bookingJob.setAuthNum(cursor.getString(3));
				bookingJob.setCabNum(Integer.parseInt(cursor.getString(4)));
				bookingJob.setCarLatitude(cursor.getString(5));
				bookingJob.setCarLongitude(cursor.getString(6));
				bookingJob.setDispatchedCar(cursor.getString(7));
				bookingJob.setDispatchedTime(cursor.getString(8));
				bookingJob.setJobID(Integer.parseInt(cursor.getString(9)));
				bookingJob.setNumberOfPassengers(Integer.parseInt(cursor.getString(10)));
				bookingJob.setNumTaxis(Integer.parseInt(cursor.getString(11)));
				bookingJob.setPassengerName(cursor.getString(12));
				bookingJob.setPhoneExt((cursor.getString(13)));
				bookingJob.setPhoneNumber((cursor.getString(14)));
				bookingJob.setPickupTime((cursor.getString(15)));
				bookingJob.setPriority(Integer.parseInt(cursor.getString(16)));
				bookingJob.setPriorityReason((cursor.getString(17)));
				bookingJob.setRemarks((cursor.getString(18)));
				bookingJob.setTaxiPIN(Integer.parseInt(cursor.getString(19)));
				bookingJob.setTaxiRideID(Integer.parseInt(cursor.getString(20)));
				bookingJob.setTripCancelledTime((cursor.getString(21)));
				bookingJob.setTripCreationTime((cursor.getString(22)));
				bookingJob.setTripModificationTime((cursor.getString(23)));
				bookingJob.setTripCompletionTime((cursor.getString(24)));
				bookingJob.setTripStatus(Integer.parseInt(cursor.getString(25)));
				bookingJob.setTripType(Integer.parseInt(cursor.getString(26)));
				bookingJob.setRouteLink(Integer.parseInt(cursor.getString(27)));
				bookingJob.setBookingType(Integer.parseInt(cursor.getString(28)));
				bookingJob.setDestId(Integer.parseInt(cursor.getString(29)));
				bookingJob.setSysId(Integer.parseInt(cursor.getString(30)));
				bookingJob.setAttribute(cursor.getString(31));
				bookingJob.setAlready_paid(Integer.parseInt(cursor.getString(32)));
				bookingJob.setMulti_pay_allow(Integer.parseInt(cursor.getString(33)));
				bookingJobList.add(bookingJob);
			} while (cursor.moveToNext());
        }
        else {
        	if (DatabaseHandler.dbDebug)
        		Log.v(TAG, "getBookingJobByBookingType:cursor = null");
        }       
        
        cursor.close();
        return bookingJobList;
    }      
    
    public List<MBBooking> getBookingJobByBookingTypeName(int bookingType, String name, String phoneNum) {
        SQLiteDatabase db = dbHdr.getReadableDatabase();
        Cursor cursor = null;
        List<MBBooking> bookingJobList = new ArrayList<MBBooking>();
		cursor = db.query(DatabaseHandler.TABLE_MBBOOKING, null,
				DatabaseHandler.KEY_MBBOOKING_BOOKING_TYPE + "=?" + " AND " +
				DatabaseHandler.KEY_MBBOOKING_PASSENGERNAME + "=?" + " AND " +
			    DatabaseHandler.KEY_MBBOOKING_PHONENUMBER + "=?",new String[] { String.valueOf(bookingType), name, phoneNum }, 
			    null, null, ORDER, null);
		
        MBBooking bookingJob;
        if (cursor.moveToFirst()) {
			do {
				bookingJob = new MBBooking();
				bookingJob.setId(Integer.parseInt(cursor.getString(0)));
				bookingJob.setAdviseArrival(Integer.parseInt(cursor.getString(1)));
				bookingJob.setAsap(Integer.parseInt(cursor.getString(2)));
				bookingJob.setAuthNum(cursor.getString(3));
				bookingJob.setCabNum(Integer.parseInt(cursor.getString(4)));
				bookingJob.setCarLatitude(cursor.getString(5));
				bookingJob.setCarLongitude(cursor.getString(6));
				bookingJob.setDispatchedCar(cursor.getString(7));
				bookingJob.setDispatchedTime(cursor.getString(8));
				bookingJob.setJobID(Integer.parseInt(cursor.getString(9)));
				bookingJob.setNumberOfPassengers(Integer.parseInt(cursor.getString(10)));
				bookingJob.setNumTaxis(Integer.parseInt(cursor.getString(11)));
				bookingJob.setPassengerName(cursor.getString(12));
				bookingJob.setPhoneExt((cursor.getString(13)));
				bookingJob.setPhoneNumber((cursor.getString(14)));
				bookingJob.setPickupTime((cursor.getString(15)));
				bookingJob.setPriority(Integer.parseInt(cursor.getString(16)));
				bookingJob.setPriorityReason((cursor.getString(17)));
				bookingJob.setRemarks((cursor.getString(18)));
				bookingJob.setTaxiPIN(Integer.parseInt(cursor.getString(19)));
				bookingJob.setTaxiRideID(Integer.parseInt(cursor.getString(20)));
				bookingJob.setTripCancelledTime((cursor.getString(21)));
				bookingJob.setTripCreationTime((cursor.getString(22)));
				bookingJob.setTripModificationTime((cursor.getString(23)));
				bookingJob.setTripCompletionTime((cursor.getString(24)));
				bookingJob.setTripStatus(Integer.parseInt(cursor.getString(25)));
				bookingJob.setTripType(Integer.parseInt(cursor.getString(26)));
				bookingJob.setRouteLink(Integer.parseInt(cursor.getString(27)));
				bookingJob.setBookingType(Integer.parseInt(cursor.getString(28)));
				bookingJob.setDestId(Integer.parseInt(cursor.getString(29)));
				bookingJob.setSysId(Integer.parseInt(cursor.getString(30)));
				bookingJob.setAttribute(cursor.getString(31));
				bookingJob.setAlready_paid(Integer.parseInt(cursor.getString(32)));
				bookingJob.setMulti_pay_allow(Integer.parseInt(cursor.getString(33)));
				bookingJobList.add(bookingJob);
			} while (cursor.moveToNext());
        }
        
        cursor.close();
        return bookingJobList;
    }    

    public List<MBBooking> getBookingJobByBookingTypeNameAndDestIDAndUserInfo(int bookingType, String name, String phoneNum, String destID) {
        SQLiteDatabase db = dbHdr.getReadableDatabase();
        Cursor cursor = null;
        List<MBBooking> bookingJobList = new ArrayList<MBBooking>();
		cursor = db.query(DatabaseHandler.TABLE_MBBOOKING, null,
				DatabaseHandler.KEY_MBBOOKING_BOOKING_TYPE + "=?" + " AND " +
				DatabaseHandler.KEY_MBBOOKING_PASSENGERNAME + "=?" + " AND " +
			    DatabaseHandler.KEY_MBBOOKING_PHONENUMBER + "=?" + " AND " +
			    DatabaseHandler.KEY_MBBOOKING_DEST_ID + "=?",new String[] { String.valueOf(bookingType), name, phoneNum, destID }, 
			    null, null, ORDER, null);
		
        MBBooking bookingJob;
        if (cursor.moveToFirst()) {
			do {
				bookingJob = new MBBooking();
				bookingJob.setId(Integer.parseInt(cursor.getString(0)));
				bookingJob.setAdviseArrival(Integer.parseInt(cursor.getString(1)));
				bookingJob.setAsap(Integer.parseInt(cursor.getString(2)));
				bookingJob.setAuthNum(cursor.getString(3));
				bookingJob.setCabNum(Integer.parseInt(cursor.getString(4)));
				bookingJob.setCarLatitude(cursor.getString(5));
				bookingJob.setCarLongitude(cursor.getString(6));
				bookingJob.setDispatchedCar(cursor.getString(7));
				bookingJob.setDispatchedTime(cursor.getString(8));
				bookingJob.setJobID(Integer.parseInt(cursor.getString(9)));
				bookingJob.setNumberOfPassengers(Integer.parseInt(cursor.getString(10)));
				bookingJob.setNumTaxis(Integer.parseInt(cursor.getString(11)));
				bookingJob.setPassengerName(cursor.getString(12));
				bookingJob.setPhoneExt((cursor.getString(13)));
				bookingJob.setPhoneNumber((cursor.getString(14)));
				bookingJob.setPickupTime((cursor.getString(15)));
				bookingJob.setPriority(Integer.parseInt(cursor.getString(16)));
				bookingJob.setPriorityReason((cursor.getString(17)));
				bookingJob.setRemarks((cursor.getString(18)));
				bookingJob.setTaxiPIN(Integer.parseInt(cursor.getString(19)));
				bookingJob.setTaxiRideID(Integer.parseInt(cursor.getString(20)));
				bookingJob.setTripCancelledTime((cursor.getString(21)));
				bookingJob.setTripCreationTime((cursor.getString(22)));
				bookingJob.setTripModificationTime((cursor.getString(23)));
				bookingJob.setTripCompletionTime((cursor.getString(24)));
				bookingJob.setTripStatus(Integer.parseInt(cursor.getString(25)));
				bookingJob.setTripType(Integer.parseInt(cursor.getString(26)));
				bookingJob.setRouteLink(Integer.parseInt(cursor.getString(27)));
				bookingJob.setBookingType(Integer.parseInt(cursor.getString(28)));
				bookingJob.setDestId(Integer.parseInt(cursor.getString(29)));
				bookingJob.setSysId(Integer.parseInt(cursor.getString(30)));
				bookingJob.setAttribute(cursor.getString(31));
				bookingJob.setAlready_paid(Integer.parseInt(cursor.getString(32)));
				bookingJob.setMulti_pay_allow(Integer.parseInt(cursor.getString(33)));
				bookingJobList.add(bookingJob);
			} while (cursor.moveToNext());
        }
        
        cursor.close();
        return bookingJobList;
    } 
    
    public List<MBBooking> getBookingJobByBookingTypeNameAndDestID(int bookingType, String destID) {
        SQLiteDatabase db = dbHdr.getReadableDatabase();
        Cursor cursor = null;
        List<MBBooking> bookingJobList = new ArrayList<MBBooking>();
		cursor = db.query(DatabaseHandler.TABLE_MBBOOKING, null,
				DatabaseHandler.KEY_MBBOOKING_BOOKING_TYPE + "=?" + " AND " +
			    DatabaseHandler.KEY_MBBOOKING_DEST_ID + "=?",new String[] { String.valueOf(bookingType), destID }, 
			    null, null, ORDER, null);
		
        MBBooking bookingJob;
        if (cursor.moveToFirst()) {
			do {
				bookingJob = new MBBooking();
				bookingJob.setId(Integer.parseInt(cursor.getString(0)));
				bookingJob.setAdviseArrival(Integer.parseInt(cursor.getString(1)));
				bookingJob.setAsap(Integer.parseInt(cursor.getString(2)));
				bookingJob.setAuthNum(cursor.getString(3));
				bookingJob.setCabNum(Integer.parseInt(cursor.getString(4)));
				bookingJob.setCarLatitude(cursor.getString(5));
				bookingJob.setCarLongitude(cursor.getString(6));
				bookingJob.setDispatchedCar(cursor.getString(7));
				bookingJob.setDispatchedTime(cursor.getString(8));
				bookingJob.setJobID(Integer.parseInt(cursor.getString(9)));
				bookingJob.setNumberOfPassengers(Integer.parseInt(cursor.getString(10)));
				bookingJob.setNumTaxis(Integer.parseInt(cursor.getString(11)));
				bookingJob.setPassengerName(cursor.getString(12));
				bookingJob.setPhoneExt((cursor.getString(13)));
				bookingJob.setPhoneNumber((cursor.getString(14)));
				bookingJob.setPickupTime((cursor.getString(15)));
				bookingJob.setPriority(Integer.parseInt(cursor.getString(16)));
				bookingJob.setPriorityReason((cursor.getString(17)));
				bookingJob.setRemarks((cursor.getString(18)));
				bookingJob.setTaxiPIN(Integer.parseInt(cursor.getString(19)));
				bookingJob.setTaxiRideID(Integer.parseInt(cursor.getString(20)));
				bookingJob.setTripCancelledTime((cursor.getString(21)));
				bookingJob.setTripCreationTime((cursor.getString(22)));
				bookingJob.setTripModificationTime((cursor.getString(23)));
				bookingJob.setTripCompletionTime((cursor.getString(24)));
				bookingJob.setTripStatus(Integer.parseInt(cursor.getString(25)));
				bookingJob.setTripType(Integer.parseInt(cursor.getString(26)));
				bookingJob.setRouteLink(Integer.parseInt(cursor.getString(27)));
				bookingJob.setBookingType(Integer.parseInt(cursor.getString(28)));
				bookingJob.setDestId(Integer.parseInt(cursor.getString(29)));
				bookingJob.setSysId(Integer.parseInt(cursor.getString(30)));
				bookingJob.setAttribute(cursor.getString(31));
				bookingJob.setAlready_paid(Integer.parseInt(cursor.getString(32)));
				bookingJob.setMulti_pay_allow(Integer.parseInt(cursor.getString(33)));
				bookingJobList.add(bookingJob);
			} while (cursor.moveToNext());
        }
        
        cursor.close();
        return bookingJobList;
    } 
    
    // Getting All MBBooking
    public List<MBBooking> getAllBookingJob() {
        List<MBBooking> bookingJobList = new ArrayList<MBBooking>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_MBBOOKING;

        SQLiteDatabase db = dbHdr.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MBBooking bookingJob = new MBBooking();
                bookingJob.setId(Integer.parseInt(cursor.getString(0)));
                bookingJob.setAdviseArrival(Integer.parseInt(cursor.getString(1)));
                bookingJob.setAsap(Integer.parseInt(cursor.getString(2)));
                bookingJob.setAuthNum(cursor.getString(3));
                bookingJob.setCabNum(Integer.parseInt(cursor.getString(4)));
                bookingJob.setCarLatitude(cursor.getString(5));
                bookingJob.setCarLongitude(cursor.getString(6));
                bookingJob.setDispatchedCar(cursor.getString(7));
                bookingJob.setDispatchedTime(cursor.getString(8));
                bookingJob.setJobID(Integer.parseInt(cursor.getString(9)));
                bookingJob.setNumberOfPassengers(Integer.parseInt(cursor.getString(10)));
                bookingJob.setNumTaxis(Integer.parseInt(cursor.getString(11)));
                bookingJob.setPassengerName(cursor.getString(12));
                bookingJob.setPhoneExt((cursor.getString(13)));
                bookingJob.setPhoneNumber((cursor.getString(14)));
                bookingJob.setPickupTime((cursor.getString(15)));
                bookingJob.setPriority(Integer.parseInt(cursor.getString(16)));
                bookingJob.setPriorityReason((cursor.getString(17)));
                bookingJob.setRemarks((cursor.getString(18)));
                bookingJob.setTaxiPIN(Integer.parseInt(cursor.getString(19)));
                bookingJob.setTaxiRideID(Integer.parseInt(cursor.getString(20)));
                bookingJob.setTripCancelledTime((cursor.getString(21)));
                bookingJob.setTripCreationTime((cursor.getString(22)));
                bookingJob.setTripModificationTime((cursor.getString(23)));
                bookingJob.setTripCompletionTime((cursor.getString(24)));
                bookingJob.setTripStatus(Integer.parseInt(cursor.getString(25)));
                bookingJob.setTripType(Integer.parseInt(cursor.getString(26)));
                bookingJob.setRouteLink(Integer.parseInt(cursor.getString(27)));
                bookingJob.setBookingType(Integer.parseInt(cursor.getString(28)));
                bookingJob.setDestId(Integer.parseInt(cursor.getString(29)));
                bookingJob.setSysId(Integer.parseInt(cursor.getString(30)));
                bookingJob.setAttribute(cursor.getString(31));
                bookingJob.setAlready_paid(Integer.parseInt(cursor.getString(32)));
                bookingJob.setMulti_pay_allow(Integer.parseInt(cursor.getString(33)));
                // Adding contact to list
                bookingJobList.add(bookingJob);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return bookingJobList;
    }

    // Updating single contact
    public int updateBookingJob(MBBooking bookingJob) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_MBBOOKING_ADVISEARRIVAL, bookingJob.getAdviseArrival()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_ASAP, bookingJob.getAsap()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_AUTHNUM, bookingJob.getAuthNum()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_CABNUM, bookingJob.getCabNum()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_CARLATITUDE, bookingJob.getCarLatitude()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_CARLONGITUDE, bookingJob.getCarLongitude()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_DISPATCHEDCAR, bookingJob.getDispatchedCar()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_DISPATCHEDTIME, bookingJob.getDispatchedTime()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_JOBID, bookingJob.getJobID()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_NUMBEROFPASSENGERS, bookingJob.getNumberOfPassengers()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_NUMTAXIS, bookingJob.getNumTaxis()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_PASSENGERNAME, bookingJob.getPassengerName()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_PHONEEXT, bookingJob.getPhoneExt()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_PHONENUMBER, bookingJob.getPhoneNumber()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_PICKUPTIME, bookingJob.getPickupTime()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_PRIORITY, bookingJob.getPriority()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_PRIORITYREASON, bookingJob.getPriorityReason()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_REMARKS, bookingJob.getRemarks()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TAXIPIN, bookingJob.getTaxiPIN()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TAXIRIDEID, bookingJob.getTaxiRideID()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TRIPCANCELLEDTIME, bookingJob.getTripCancelledTime()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TRIPCREATIONTIME, bookingJob.getTripCreationTime()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TRIPMODIFICATIONTIME, bookingJob.getTripModificationTime());
        values.put(DatabaseHandler.KEY_MBBOOKING_TRIPCOMPLETIONTIME, bookingJob.getTripCompletionTime());
        values.put(DatabaseHandler.KEY_MBBOOKING_TRIPSTATUS, bookingJob.getTripStatus()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_TRIPTYPE, bookingJob.getTripType()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_LINK_ROUTE, bookingJob.getRouteLink()); 
        values.put(DatabaseHandler.KEY_MBBOOKING_BOOKING_TYPE, bookingJob.getBookingType());
        values.put(DatabaseHandler.KEY_MBBOOKING_DEST_ID, bookingJob.getDestId());
        values.put(DatabaseHandler.KEY_MBBOOKING_SYSTEM_ID, bookingJob.getSysId());
        values.put(DatabaseHandler.KEY_MBBOOKING_ATTRIBUTE, bookingJob.getAttribute());
        values.put(DatabaseHandler.KEY_MBBOOKING_ALREADY_PAID, bookingJob.getAlready_paid());
        values.put(DatabaseHandler.KEY_MBBOOKING_MULTI_PAY_ALLOW, bookingJob.getMulti_pay_allow());
        // updating row
        return db.update(DatabaseHandler.TABLE_MBBOOKING, values, DatabaseHandler.KEY_MBBOOKING_ID + " =?",
                new String[] { String.valueOf(bookingJob.getId()) });
    }

    // Deleting single contact
    private void deleteBookingJob(MBBooking bookingJob) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        db.delete(DatabaseHandler.TABLE_MBBOOKING, DatabaseHandler.KEY_MBBOOKING_ID + " =?",
                new String[] { String.valueOf(bookingJob.getId()) });
        db.close();
    }

    // Deleting single contact
    public void deleteBookingJobById(int bookingId) {
    	MBRouteDBHandling tmpRouteHdr;
    	MBAttributeDBHandling tmpAttrHdr;
    	MBBooking tmpBooking;
    	int routeId;
    	tmpBooking = getBookingJobById(bookingId);
    	
    	if (tmpBooking == null) {
    		if (DatabaseHandler.dbDebug)
    			Log.e(TAG, "deleteBookingJobById, bookingId invalid");
    		return;
    	}
    	
    	routeId = tmpBooking.getRouteLink();
    	
    	if (routeId > 0) {
        	tmpRouteHdr = new MBRouteDBHandling (dbHdr);    		
    		tmpRouteHdr.deleteRouteById(routeId);
    	}
    	
    	tmpAttrHdr = new MBAttributeDBHandling (dbHdr);
    	tmpAttrHdr.deleteAttributeByBookingJobID(tmpBooking.getId());
    	deleteBookingJob(tmpBooking);
    }
    
    public void deleteBookingJobByTripId(int tripId) {
    	MBRouteDBHandling tmpRouteHdr;
    	MBAttributeDBHandling tmpAttrHdr;
    	MBBooking tmpBooking;
    	int routeId;
    	tmpBooking = getBookingJobByTripID(tripId);
    	
    	if (tmpBooking == null) {
    		if (DatabaseHandler.dbDebug)
    			Log.e(TAG, "deleteBookingJobByTripId, TripId invalid");
    		return;
    	}
    	
    	routeId = tmpBooking.getRouteLink();
    	
    	if (routeId > 0) {
        	tmpRouteHdr = new MBRouteDBHandling(dbHdr);    		
    		tmpRouteHdr.deleteRouteById(routeId);
    	}
    	
    	tmpAttrHdr = new MBAttributeDBHandling(dbHdr);
    	tmpAttrHdr.deleteAttributeByBookingJobID(tmpBooking.getId());
    	deleteBookingJob(tmpBooking);
    }
    
    // Getting MBBooking Count
    public int getBookingJobCount() {
        String countQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_MBBOOKING;
        SQLiteDatabase db = dbHdr.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
     
    public void linkRouteToBookingJob(MBRoute route, MBBooking booking)
    {
    	if ((booking.getId() == 0) || ( route.getId() == 0)) {
    		if (DatabaseHandler.dbDebug)
    			Log.e(TAG, "invalid booking or route ID");
    		return;
    	}
    	
    	if ((booking.getRouteLink() != 0))	{
    		if (DatabaseHandler.dbDebug)
    			Log.e(TAG, "booking route link to another link");
    		throw(new RuntimeException());
    	}
    	
    	booking.setRouteLink(route.getId());
    	updateBookingJob(booking);
    	return;
    }
    
    public MBBooking copyBookingJob(int srcBookingId, boolean isCustomized) {
    	MBBooking desBooking = null;
    	MBRouteDBHandling rtDb = new MBRouteDBHandling(dbHdr);
    	MBRoute rtObj = null;
    	MBAttributeDBHandling attrHdr;
    	List<MBAttribute> attrList;
    	attrHdr = new MBAttributeDBHandling (dbHdr);
    	
    	if (srcBookingId <= 0)	{
    		
    	}
    	else {
    		desBooking = getBookingJobById(srcBookingId);
    		
    		if (desBooking != null) {
    			desBooking.setId(0);
    			
    			if (desBooking.getRouteLink() > 0)	{
    				rtObj = rtDb.copyRoute(desBooking.getRouteLink());
    				
    				if (rtObj != null)	{
    					desBooking.setRouteLink(rtObj.getId());
    				}
    				else {
    					if (DatabaseHandler.dbDebug)
    						Log.e(TAG, "copyBookingJob, cannot copy rtObj id=" + desBooking.getRouteLink());
    					throw (new RuntimeException());
    				}
    			}
    			
    			addBookingJob(desBooking);
				rtObj.setRouteBookingLink(desBooking.getId());
				rtDb.updateRoute(rtObj);
				
				// TODO: customized might use this code or not, remove or comment out depends on situation
				if (isCustomized) {
					attrList = attrHdr.getAttributesByBookingJobID(srcBookingId);
					
					if ((attrList != null) && (attrList.size() > 0)) {
						for (MBAttribute attr : attrList) {
							attr.setId(0);
							attr.setAttrLinkBooking(desBooking.getId());
							attrHdr.addAttribute(attr);
						}
					}
				}
    		}
    	}
    	
    	return desBooking;
    }
    
    public MBBooking copyBookingJobByTripID(int srcTripId, boolean isCustomized) {
    	MBBooking desBooking = null;
    	MBBooking srcBooking = null;
    	MBRouteDBHandling rtDb = new MBRouteDBHandling(dbHdr);
    	MBRoute rtObj = null;
    	int srcId = 0;
    	MBAttributeDBHandling attrHdr;
    	List<MBAttribute> attrList;
    	attrHdr = new MBAttributeDBHandling(dbHdr);
    	
    	if ( srcTripId <= 0) {
    		
    	}
    	else {
    		srcBooking = getBookingJobByTripID(srcTripId);
    		
    		if (srcBooking != null) {
    			srcId =srcBooking.getId();
    			if (DatabaseHandler.dbDebug)
    				Log.v(TAG, "copyBookingJob, srcID = " + srcId);
    			desBooking = new MBBooking();
    			desBooking.setId(0);
    			desBooking.setRemarks(srcBooking.getRemarks());
    			desBooking.setNumberOfPassengers(srcBooking.getNumberOfPassengers());
    			desBooking.setDestId(srcBooking.getDestId());
    			desBooking.setSysId(srcBooking.getSysId());
    			
    			// not customized is using static attribute
    			if (!isCustomized) {
    				desBooking.setAttribute(srcBooking.getAttribute());
    			}
    			
    			if (srcBooking.getRouteLink() > 0) {
    				rtObj = rtDb.copyRoute(srcBooking.getRouteLink());
    				
    				if (rtObj != null)	{
    					desBooking.setRouteLink(rtObj.getId());
    				}
    				else {
    					if (DatabaseHandler.dbDebug)
    						Log.e(TAG, "copyBookingJob, cannot copy rtObj id=" + desBooking.getRouteLink());
    					throw ( new RuntimeException());
    				}
    			}
    			
    			addBookingJob(desBooking);
				rtObj.setRouteBookingLink(desBooking.getId());
				rtDb.updateRoute(rtObj);
				
				// TODO: customized might use this code or not, remove or comment out depends on situation
				if (isCustomized) {
					attrList = attrHdr.getAttributesByBookingJobID(srcId);
					
					if (( attrList != null) && ( attrList.size() > 0)) {
						for ( MBAttribute attr : attrList) {
							attr.setId(0);
							attr.setAttrLinkBooking(desBooking.getId());
							attrHdr.addAttribute(attr);
						}
					}
				}
    		}
    	}
    	
    	return desBooking;
    }
    
    
    public void printAttrByJobId(int id) {
    	MBAttributeDBHandling attrHdr;
    	attrHdr = new MBAttributeDBHandling (dbHdr);
		List<MBAttribute> attrUpdatedList = attrHdr.getAttributesByBookingJobID(id);	
		if (DatabaseHandler.dbDebug)
			Log.v(TAG, "printAttrByJobId::attribut num =" + attrUpdatedList.size() + " jobId=" + id);
		for (MBAttribute ab : attrUpdatedList) {
			ab.print();
		}
    }
}
