package com.digital.dispatch.TaxiLimoSQLDatabase;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.digital.dispatch.TaxiLimoSQLDatabase.DatabaseHandler;
import com.digital.dispatch.TaxiLimoSQLDatabase.MBAttribute;

public class MBAttributeDBHandling {
    private DatabaseHandler dbHdr;
    static private String TAG = "mbsqldatabasev2";
    public MBAttributeDBHandling (DatabaseHandler dbHandler)
    {
        dbHdr = dbHandler;
    }
    public int addAttribute(MBAttribute attribute) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        int rowId = 0;
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_ATTRIBUTES_ATTRLONGNAME, attribute.getAttrLongName()); 
        values.put(DatabaseHandler.KEY_ATTRIBUTES_ATTRSHORTNAME, attribute.getAttrShortName()); 
        values.put(DatabaseHandler.KEY_ATTRIBUTES_LINK_BOOKING, attribute.getAttrLinkBookin()); 
        // Inserting Row
        rowId = (int)db.insert(DatabaseHandler.TABLE_ATTRIBUTES, null, values);
        if ( rowId > 0)
        {
        	attribute.setId(rowId);
        }
        db.close(); // Closing database connection
        return rowId;
    }

    public MBAttribute getAttributeById(int id) {
        SQLiteDatabase db = dbHdr.getReadableDatabase();

		Cursor cursor = db.query(DatabaseHandler.TABLE_ATTRIBUTES, new String[] {
				DatabaseHandler.KEY_ATTRIBUTES_ID,
				DatabaseHandler.KEY_ATTRIBUTES_ATTRLONGNAME,
				DatabaseHandler.KEY_ATTRIBUTES_ATTRSHORTNAME,
				DatabaseHandler.KEY_ATTRIBUTES_LINK_BOOKING},
				DatabaseHandler.KEY_ATTRIBUTES_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		
        MBAttribute Attribute = null;
		try {
			if (cursor.moveToFirst()) {
				Attribute = new MBAttribute();
				Attribute.setId(Integer.parseInt(cursor.getString(0)));
				Attribute.setAttrLongName(cursor.getString(1));
				Attribute.setAttrShortName(cursor.getString(2));
				Attribute.setAttrLinkBooking(Integer.parseInt(cursor
						.getString(3)));
			} else {
				if (DatabaseHandler.dbDebug)
					Log.v(TAG, "getAttributeById, no attribute found");
			}
		} catch (Exception e) {
			if (DatabaseHandler.dbDebug)
				Log.e(TAG, "getAttributeById excetpion");
			e.printStackTrace();
		} finally {
			cursor.close();
		}
        db.close();
        return Attribute;
    }
    
    // Getting All MBAttribute
    public List<MBAttribute> getAllAttributes() {
        List<MBAttribute> AttributeList = new ArrayList<MBAttribute>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_ATTRIBUTES;

        SQLiteDatabase db = dbHdr.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
		try {
			if (cursor.moveToFirst()) {
				do {
					MBAttribute Attribute = new MBAttribute();
					Attribute.setId(Integer.parseInt(cursor.getString(0)));
					Attribute.setAttrLongName(cursor.getString(1));
					Attribute.setAttrShortName(cursor.getString(2));
					Attribute.setAttrLinkBooking(Integer.parseInt(cursor.getString(3)));	
					// Adding contact to list
					AttributeList.add(Attribute);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			if (DatabaseHandler.dbDebug)
				Log.e(TAG, "getAllAttributes excetpion");
			e.printStackTrace();
		} finally {
			cursor.close();
		}
        db.close();
        // return contact list
        return AttributeList;
    }
    
    // Getting All MBAttribute
    public List<MBAttribute> getRawAttributes() {
        List<MBAttribute> AttributeList = new ArrayList<MBAttribute>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_ATTRIBUTES
        	+ " WHERE " + DatabaseHandler.KEY_ATTRIBUTES_LINK_BOOKING + " = 0";
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
		try {
			if (cursor.moveToFirst()) {
				do {
					MBAttribute Attribute = new MBAttribute();
					Attribute.setId(Integer.parseInt(cursor.getString(0)));
					Attribute.setAttrLongName(cursor.getString(1));
					Attribute.setAttrShortName(cursor.getString(2));
					Attribute.setAttrLinkBooking(Integer.parseInt(cursor.getString(3)));
					// Adding contact to list
					AttributeList.add(Attribute);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			if (DatabaseHandler.dbDebug)
				Log.e(TAG, "getAllAttributes excetpion");
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		db.close();
        // return contact list
        return AttributeList;
    }

    // Updating single contact
    public boolean linkAttributeToBookingJob(MBAttribute Attribute, int bookingJobId) {
    	boolean result = false;
    	MBAttribute localAttribute;
    	if ( Attribute == null)
    	{
    		return result;
    	}
    	int id = Attribute.getId();
    	localAttribute = getAttributeById (id);
    	if ( localAttribute == null)
    	{
    		
    	}else {
			localAttribute.setId(0);
			localAttribute.setAttrLinkBooking(bookingJobId);
			addAttribute(localAttribute);
			result = true;
		}
        return result;
 
    }
    
	public boolean linkAttributeToBookingJob(int attrId, int bookingJobId) {
		boolean result = false;
		MBAttribute localAttribute;
		if (attrId > 0) {
			localAttribute = getAttributeById(attrId);
			if (localAttribute == null) {

			} else {
				localAttribute.setId(0);
				localAttribute.setAttrLinkBooking(bookingJobId);
				addAttribute(localAttribute);
				result = true;
			}
		}
		return result;

	}

    // Deleting single contact
    public void deleteAttribute(MBAttribute Attribute) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        db.delete(DatabaseHandler.TABLE_ATTRIBUTES, DatabaseHandler.KEY_ATTRIBUTES_ID + " = ?",
                new String[] { String.valueOf(Attribute.getId()) });
        db.close();
    }

    public boolean isAttributeExist(String longName, String shortName)
    {
    	boolean bResult = false;
        String selectQuery = "SELECT  * FROM MBAttributes WHERE attrLongName LIKE ? AND attrShortName LIKE ?s" ;

        SQLiteDatabase db = dbHdr.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{longName, shortName});
		try {
			if (cursor.moveToFirst()) {
				bResult = true;
			}
		} catch (Exception e) {
			if (DatabaseHandler.dbDebug)
				Log.e(TAG, "isAttributeExist excetpion");
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		db.close();
		return bResult;
	}
        
    public void deleteAttributeByContent (String longName, String shortName)
    {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        db.delete(DatabaseHandler.TABLE_ATTRIBUTES, "attrLongName = ? AND attrShortName = ?",
                new String[] { longName, shortName });
        db.close();
    }
    
    public List<MBAttribute> getAttributeByContent (String longName, String shortName)
    {
        List<MBAttribute> AttributeList = new ArrayList<MBAttribute>();
        String selectQuery = "SELECT  * FROM MBAttributes WHERE attrLongName LIKE ? AND attrShortName LIKE ?" ;

        SQLiteDatabase db = dbHdr.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{longName, shortName});

        // looping through all rows and adding to list
		try {
			if (cursor.moveToFirst()) {
				do {
					MBAttribute Attribute = new MBAttribute();
					Attribute.setId(Integer.parseInt(cursor.getString(0)));
					Attribute.setAttrLongName(cursor.getString(1));
					Attribute.setAttrShortName(cursor.getString(2));
					Attribute.setAttrLinkBooking(Integer.parseInt(cursor.getString(3)));
					// Adding contact to list
					AttributeList.add(Attribute);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			if (DatabaseHandler.dbDebug)
				Log.e(TAG, "getAttributeByContent excetpion");
			e.printStackTrace();
		} finally {
			cursor.close();
		}        
        db.close();
        return AttributeList;
    }
    
    public void deleteAttributeByBookingJobID (int bookingJobId)
    {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        db.delete(DatabaseHandler.TABLE_ATTRIBUTES, "attrBookingLink =?",
                new String[] { String.valueOf(bookingJobId) });
        db.close();
    }
    
    public List<MBAttribute> getAttributesByBookingJobID( int bookingJobId) {
        List<MBAttribute> AttributeList = new ArrayList<MBAttribute>();
        String selectQuery = "SELECT  * FROM MBAttributes WHERE attrBookingLink LIKE ?" ;

        SQLiteDatabase db = dbHdr.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{Integer.toString(bookingJobId)});

        // looping through all rows and adding to list
		try {
			if (cursor.moveToFirst()) {
				do {
					MBAttribute Attribute = new MBAttribute();
					Attribute.setId(Integer.parseInt(cursor.getString(0)));
					Attribute.setAttrLongName(cursor.getString(1));
					Attribute.setAttrShortName(cursor.getString(2));
					Attribute.setAttrLinkBooking(Integer.parseInt(cursor.getString(3)));
					// Adding contact to list
					AttributeList.add(Attribute);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			if (DatabaseHandler.dbDebug)
				Log.e(TAG, "getAttributesByBookingJobID excetpion");
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		db.close();
        // return contact list
        return AttributeList;
    }

    // Getting MBAttribute Count
    public int getAttributeCount() {
        String countQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_ATTRIBUTES;
        SQLiteDatabase db = dbHdr.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        db.close();
        // return count
        return cursor.getCount();
    }
    
    public void printAllAttributes()
    {
    	List<MBAttribute> attributeList =getAllAttributes();
    	for ( MBAttribute attr : attributeList)
    	{
    		attr.print();
    	}
    }
}
