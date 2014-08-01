package com.digital.dispatch.TaxiLimoSQLDatabase;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.digital.dispatch.TaxiLimoSQLDatabase.DatabaseHandler;

public class MBCompanyDBHandling {
    private DatabaseHandler dbHdr;
    static private String TAG = "mbsqldatabasev2";
    
    public MBCompanyDBHandling (DatabaseHandler dbHandler) {
        dbHdr = dbHandler;
    }
    
    public int addCompany(MBCompany comp) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        int rowId = 0;
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_COMPANY_DEST_ID, comp.getDestID());
        values.put(DatabaseHandler.KEY_COMPANY_NAME, comp.getCompanyName());
        values.put(DatabaseHandler.KEY_COMPANY_LOGO_LOC, comp.getLogoLocation());
        values.put(DatabaseHandler.KEY_COMPANY_LOGO_VERSION, comp.getLogoVersion()); 
        values.put(DatabaseHandler.KEY_COMPANY_ATTRIBUTES, comp.getAttributes()); 
        // Inserting Row
        rowId = (int)db.insert(DatabaseHandler.TABLE_COMPANY, null, values);
        if (rowId > 0) {
        	comp.setId(rowId);
        }
        db.close(); // Closing database connection
        return rowId;
    }

    public MBCompany getCompanyByDestId(int destID) {
        SQLiteDatabase db = dbHdr.getReadableDatabase();
        
		Cursor cursor = db.query(DatabaseHandler.TABLE_COMPANY, new String[] {
				DatabaseHandler.KEY_COMPANY_ID,
				DatabaseHandler.KEY_COMPANY_NAME,
				DatabaseHandler.KEY_COMPANY_DEST_ID,
				DatabaseHandler.KEY_COMPANY_LOGO_LOC,
				DatabaseHandler.KEY_COMPANY_LOGO_VERSION,
				DatabaseHandler.KEY_COMPANY_ATTRIBUTES},
				DatabaseHandler.KEY_COMPANY_DEST_ID + "=?",
				new String[] { String.valueOf(destID) }, null, null, null, null);
		
        MBCompany comp = null;
		try {
			if (cursor.moveToFirst()) {
				comp = new MBCompany();
				comp.setId(Integer.parseInt(cursor.getString(0)));
				comp.setCompanyName(cursor.getString(1));
				comp.setDestID(cursor.getInt(2));
				comp.setLogoLocation(cursor.getString(3));
				comp.setLogoVersion(cursor.getInt(4));
				comp.setAttributes(cursor.getString(5));
			} else {
				if (DatabaseHandler.dbDebug)
					Log.v(TAG, "getCompanyByID, no company found");
			}
		} 
		catch (Exception e) {
			if (DatabaseHandler.dbDebug)
				Log.e(TAG, "getCompanyByID excetpion");
			e.printStackTrace();
		} 
		finally {
			cursor.close();
		}
		
        db.close();
        
        return comp;
    }
    
    // Getting All MBCompanies
    public List<MBCompany> getAllCompanies() {
        List<MBCompany> CompanyList = new ArrayList<MBCompany>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_COMPANY;

        SQLiteDatabase db = dbHdr.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // looping through all rows and adding to list
		try {
			if (cursor.moveToFirst()) {
				do {
					MBCompany comp = new MBCompany();
					comp.setId(cursor.getInt(0));
					comp.setDestID(cursor.getInt(1));
					comp.setCompanyName(cursor.getString(2));
					comp.setLogoLocation(cursor.getString(3));
					comp.setLogoVersion(cursor.getInt(4));
					comp.setAttributes(cursor.getString(5));
					// Adding contact to list
					CompanyList.add(comp);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			if (DatabaseHandler.dbDebug)
				Log.e(TAG, "getAllCompanies excetpion");
			e.printStackTrace();
		} finally {
			cursor.close();
		}
        db.close();
        // return contact list
        return CompanyList;
    }

    // Deleting single contact
    public void deleteCompanyByID(int id) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        db.delete(DatabaseHandler.TABLE_COMPANY, DatabaseHandler.KEY_COMPANY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
    
    // Updating single contact
    public int updateCompany(MBCompany comp) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();

        // Only logo location and logo version is editable because it will change from time to time
        // destID will always stays the same for same company
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_COMPANY_NAME, comp.getCompanyName());
        values.put(DatabaseHandler.KEY_COMPANY_LOGO_LOC, comp.getLogoLocation()); 
        values.put(DatabaseHandler.KEY_COMPANY_LOGO_VERSION, comp.getLogoVersion());
        values.put(DatabaseHandler.KEY_COMPANY_ATTRIBUTES, comp.getAttributes());

        // updating row
        int status = db.update(DatabaseHandler.TABLE_COMPANY, values, DatabaseHandler.KEY_COMPANY_ID + " =?",
                new String[] { String.valueOf(comp.getId()) });
        db.close();
        
        return status;
    }

    // Getting MBCompany Count
    public int getCompanyCount() {
        String countQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_COMPANY;
        SQLiteDatabase db = dbHdr.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;
    }
    
    public void printAllCompanies() {
    	List<MBCompany> companyList = getAllCompanies();
    	
    	for (MBCompany comp : companyList) {
    		comp.print();
    	}
    }
}