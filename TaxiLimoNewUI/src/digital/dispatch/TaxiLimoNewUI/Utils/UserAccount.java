package digital.dispatch.TaxiLimoNewUI.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import digital.dispatch.TaxiLimoNewUI.R;

public class UserAccount {
	public static String userName(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		return sharedPref.getString(context.getResources().getString(R.string.store_const_user_name), "");
	}
	
	public static String phoneNum(Context context) {
		String phoneNumer;
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		phoneNumer = sharedPref.getString(context.getResources().getString(R.string.store_const_phone_num), "");
		return phoneNumer;
	}
	
	public static String accountNum(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		return sharedPref.getString(context.getResources().getString(R.string.store_const_account_num), "");
	}
	
	public static String password(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		return sharedPref.getString(context.getResources().getString(R.string.store_const_password), "");
	}
	
	public static String ccPIN(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		return sharedPref.getString(context.getResources().getString(R.string.store_const_pin), "");
	}
	
	public static void setUserName(Context context, String userName) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		SharedPreferences.Editor prefEditor = sharedPref.edit();
	    prefEditor.putString(context.getResources().getString(R.string.store_const_user_name), userName);
	    prefEditor.commit();
	}
	
	public static void setPhoneNum(Context context, String phoneNum) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		SharedPreferences.Editor prefEditor = sharedPref.edit();
	    prefEditor.putString(context.getResources().getString(R.string.store_const_phone_num), phoneNum);
	    prefEditor.commit();
	}
	
	public static void setAccountNum(Context context, String userName) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		SharedPreferences.Editor prefEditor = sharedPref.edit();
	    prefEditor.putString(context.getResources().getString(R.string.store_const_account_num), userName);
	    prefEditor.commit();
	}
	
	public static void setPassword(Context context, String password) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		SharedPreferences.Editor prefEditor = sharedPref.edit();
	    prefEditor.putString(context.getResources().getString(R.string.store_const_password), password);
	    prefEditor.commit();
	}
	
	public static void setCreditCardPIN(Context context, String password) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		SharedPreferences.Editor prefEditor = sharedPref.edit();
	    prefEditor.putString(context.getResources().getString(R.string.store_const_pin), password);
	    prefEditor.commit();
	}
}
