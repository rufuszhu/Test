package com.digital.dispatch.TaxiLimoSQLDatabase;

import android.util.Log;

public class MBAttribute {
	private final String TAG = "MBDatabase";
		
	private int id;
	private String attrLongName;
	private String attrShortName;
	private int attrLinkBooking;
	
	public MBAttribute() {
		attrLongName = "";
		attrShortName = "";
		attrLinkBooking = 0;
	}
	
	public MBAttribute(String longName, String shortName) {
		attrLongName = longName;
		attrShortName = shortName;
		attrLinkBooking = 0;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getAttrLongName() {
		return attrLongName;
	}
	
	public void setAttrLongName(String attrLongName) {
		this.attrLongName = attrLongName;
	}
	
	public String getAttrShortName() {
		return attrShortName;
	}
	
	public void setAttrShortName(String attrShortName) {
		this.attrShortName = attrShortName;
	}
	
	public int getAttrLinkBookin() {
		return this.attrLinkBooking;
	}
	
	public void setAttrLinkBooking(int bookingJobId) {
		this.attrLinkBooking = bookingJobId;
	}
	
	public void print()
	{	
		if (DatabaseHandler.dbDebug) {
			Log.v(TAG, "----MBAttribute =" + id);
			Log.v(TAG, "   attrLongName = " + attrLongName);
			Log.v(TAG, "   attrShortName = " + attrShortName);
			Log.v(TAG, "   attrLinkBooking = " + attrLinkBooking);
			Log.v(TAG, "-------------------------------");
			Log.v(TAG, " ");
		}
	}
}
