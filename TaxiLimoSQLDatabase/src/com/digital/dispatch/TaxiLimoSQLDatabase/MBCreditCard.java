package com.digital.dispatch.TaxiLimoSQLDatabase;

import android.util.Log;

public class MBCreditCard {
	private final String TAG = "MBDatabase";
		
	private int id;
	private String cardNum;
	private String first4CardNum;
	private String holderName;
	private String expiry;
	private String zip;
	private String email;
	private String nickname;
	private String token;
	private String cardBrand;
	
	public MBCreditCard() {
		cardNum = "";
		first4CardNum = "";
		holderName = "";
		expiry = "";
		zip = "";
		email = "";
		nickname = "";
		token = "";
		cardBrand = "";
	}
	
	public MBCreditCard(String cardNumber, String hName, String ex, String z, String userEmail, String customName, 
			String t, String brand, String first4Num) {
		cardNum = cardNumber;
		first4CardNum = first4Num;
		holderName = hName;
		expiry = ex;
		zip = z;
		email = userEmail;
		nickname = customName;
		token = t;
		cardBrand = brand;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCardNumber() {
		return cardNum;
	}
	
	public void setCardNumber(String cardNumber) {
		this.cardNum = cardNumber;
	}
	
	public String getFirst4CardNumber() {
		return first4CardNum;
	}
	
	public void setFirst4CardNumber(String cardNumber) {
		this.first4CardNum = cardNumber;
	}
	
	public String getCardHolderName() {
		return holderName;
	}
	
	public void setCardHolderName(String hName) {
		this.holderName = hName;
	}
	
	public String getExpiry() {
		return this.expiry;
	}
	
	public void setExpiry(String ex) {
		this.expiry = ex;
	}
	
	public String getZip() {
		return this.zip;
	}
	
	public void setZip(String z) {
		zip = z;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String em) {
		this.email = em;
	}
	
	public String getNickname() {
		return this.nickname;
	}
	
	public void setNickname(String name) {
		this.nickname = name;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getCardBrand() {
		return cardBrand;
	}
	
	public void setCardBrand(String brand) {
		this.cardBrand = brand;
	}
	
	public void print() {	
		if (DatabaseHandler.dbDebug) {
			Log.v(TAG, "----MBCreditCard =" + id);
			Log.v(TAG, "   cardNum = " + cardNum);
			Log.v(TAG, "   first4cardNum = " + first4CardNum);
			Log.v(TAG, "   holderName = " + holderName);
			Log.v(TAG, "   expiry = " + expiry);
			Log.v(TAG, "   zip = " + zip);
			Log.v(TAG, "   email = " + email);
			Log.v(TAG, "   nickname = " + nickname);
			Log.v(TAG, "   token = " + token);
			Log.v(TAG, "   brand = " + cardBrand);
			Log.v(TAG, "-------------------------------");
			Log.v(TAG, " ");
		}
	}
}
