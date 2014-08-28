package com.digital.dispatch.TaxiLimoSQLDatabase;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.digital.dispatch.TaxiLimoSQLDatabase.DatabaseHandler;

public class MBCreditCardDBHandling {
    private DatabaseHandler dbHdr;
    static private String TAG = "mbsqldatabasev2";
    
    public MBCreditCardDBHandling (DatabaseHandler dbHandler) {
        dbHdr = dbHandler;
    }
    
    public int addCreditCard(MBCreditCard card) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        int rowId = 0;
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_CREDIT_CARD_CARD_NUMBER, card.getCardNumber());
        values.put(DatabaseHandler.KEY_CREDIT_CARD_FIRST4_CARD_NUM, card.getFirst4CardNumber());
        values.put(DatabaseHandler.KEY_CREDIT_CARD_HOLDER_NAME, card.getCardHolderName()); 
        values.put(DatabaseHandler.KEY_CREDIT_CARD_EXIPRY, card.getExpiry());
        values.put(DatabaseHandler.KEY_CREDIT_CARD_ADDRESS, card.getZip());
        values.put(DatabaseHandler.KEY_CREDIT_CARD_EMAIL, card.getEmail()); 
        values.put(DatabaseHandler.KEY_CREDIT_CARD_NICKNAME, card.getNickname());
        values.put(DatabaseHandler.KEY_CREDIT_CARD_TOKEN, card.getToken());
        values.put(DatabaseHandler.KEY_CREDIT_CARD_BRAND, card.getCardBrand());
        // Inserting Row
        rowId = (int)db.insert(DatabaseHandler.TABLE_CREDIT_CARD, null, values);
        if ( rowId > 0) {
        	card.setId(rowId);
        }
        db.close(); // Closing database connection
        return rowId;
    }

    public MBCreditCard getCreditCardById(int id) {
        SQLiteDatabase db = dbHdr.getReadableDatabase();

		Cursor cursor = db.query(DatabaseHandler.TABLE_CREDIT_CARD, new String[] {
				DatabaseHandler.KEY_CREDIT_CARD_ID,
				DatabaseHandler.KEY_CREDIT_CARD_CARD_NUMBER,
				DatabaseHandler.KEY_CREDIT_CARD_FIRST4_CARD_NUM,
				DatabaseHandler.KEY_CREDIT_CARD_HOLDER_NAME,
				DatabaseHandler.KEY_CREDIT_CARD_EXIPRY,
				DatabaseHandler.KEY_CREDIT_CARD_ADDRESS,
				DatabaseHandler.KEY_CREDIT_CARD_EMAIL,
				DatabaseHandler.KEY_CREDIT_CARD_NICKNAME,
				DatabaseHandler.KEY_CREDIT_CARD_TOKEN,
				DatabaseHandler.KEY_CREDIT_CARD_BRAND},
				DatabaseHandler.KEY_CREDIT_CARD_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		
        MBCreditCard card = null;
		try {
			if (cursor.moveToFirst()) {
				card = new MBCreditCard();
				card.setId(Integer.parseInt(cursor.getString(0)));
				card.setCardNumber(cursor.getString(1));
				card.setFirst4CardNumber(cursor.getString(2));
				card.setCardHolderName(cursor.getString(3));
				card.setExpiry(cursor.getString(4));
				card.setZip(cursor.getString(5));
				card.setEmail(cursor.getString(6));
				card.setNickname(cursor.getString(7));
				card.setToken(cursor.getString(8));
				card.setCardBrand(cursor.getString(9));
			} else {
				if (DatabaseHandler.dbDebug)
					Log.v(TAG, "getCreditCardById, no card found");
			}
		} 
		catch (Exception e) {
			if (DatabaseHandler.dbDebug)
				Log.e(TAG, "getCreditCardById excetpion");
			e.printStackTrace();
		} 
		finally {
			cursor.close();
		}
		
        db.close();
        
        return card;
    }
    
    // Getting All MBCreditCards
    public List<MBCreditCard> getAllCreditCards() {
        List<MBCreditCard> CreditCardList = new ArrayList<MBCreditCard>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_CREDIT_CARD;

        SQLiteDatabase db = dbHdr.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
		try {
			if (cursor.moveToFirst()) {
				do {
					MBCreditCard card = new MBCreditCard();
					card.setId(Integer.parseInt(cursor.getString(0)));
					card.setCardNumber(cursor.getString(1));
					card.setFirst4CardNumber(cursor.getString(2));
					card.setCardHolderName(cursor.getString(3));
					card.setExpiry(cursor.getString(4));
					card.setZip(cursor.getString(5));
					card.setEmail(cursor.getString(6));
					card.setNickname(cursor.getString(7));
					card.setToken(cursor.getString(8));
					card.setCardBrand(cursor.getString(9));
					// Adding contact to list
					CreditCardList.add(card);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			if (DatabaseHandler.dbDebug)
				Log.e(TAG, "getAllCreditCards excetpion");
			e.printStackTrace();
		} finally {
			cursor.close();
		}
        db.close();
        // return contact list
        return CreditCardList;
    }

    // Deleting single contact
    public void deleteCreditCardByID(int id) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();
        db.delete(DatabaseHandler.TABLE_CREDIT_CARD, DatabaseHandler.KEY_CREDIT_CARD_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
    
    // Updating single contact
    public int updateCreditCard(MBCreditCard card) {
        SQLiteDatabase db = dbHdr.getWritableDatabase();

        // Only email and nickname is editable,
        // token, card number, expiry and name are final once created
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_CREDIT_CARD_EMAIL, card.getEmail()); 
        values.put(DatabaseHandler.KEY_CREDIT_CARD_NICKNAME, card.getNickname());

        // updating row
        return db.update(DatabaseHandler.TABLE_CREDIT_CARD, values, DatabaseHandler.KEY_CREDIT_CARD_ID + " =?",
                new String[] { String.valueOf(card.getId()) });
    }

    // Getting MBCreditCard Count
    public int getCreditCardCount() {
        String countQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_CREDIT_CARD;
        SQLiteDatabase db = dbHdr.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }
    
    public void printAllCreditCards() {
    	List<MBCreditCard> creditCardList = getAllCreditCards();
    	
    	for (MBCreditCard card : creditCardList) {
    		card.print();
    	}
    }
}
