package digital.dispatch.TaxiLimoNewUI.Utils;

import digital.dispatch.TaxiLimoNewUI.R;
import android.content.Context;
import android.content.SharedPreferences;

public class UserConfig {
	public static long lastCheckParamTime(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		return sharedPref.getLong(context.getResources().getString(R.string.store_const_last_check_param), 0);
	}
	
	public static long lastLoginTime(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		return sharedPref.getLong(context.getResources().getString(R.string.store_const_last_login), 0);
	}
	
	public static int lastCreditCard(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		return sharedPref.getInt(context.getResources().getString(R.string.store_const_last_credit_card), 0);
	}
	
	public static boolean isAmPM(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		return sharedPref.getBoolean(context.getResources().getString(R.string.store_const_is_am_pm), false);
	}
	
	public static boolean isFirstTimeMap(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		return sharedPref.getBoolean(context.getResources().getString(R.string.store_const_first_time_map), true);
	}
	
	public static int PreferredCompany(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		return sharedPref.getInt(context.getResources().getString(R.string.store_const_preferred_company), 0);
	}
	
	public static void setLastCheckParam(Context context, long lastCheck) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		SharedPreferences.Editor prefEditor = sharedPref.edit();
	    prefEditor.putLong(context.getResources().getString(R.string.store_const_last_check_param), lastCheck);
	    prefEditor.commit();
	}
	
	public static void setLastLogin(Context context, long lastLogin) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		SharedPreferences.Editor prefEditor = sharedPref.edit();
	    prefEditor.putLong(context.getResources().getString(R.string.store_const_last_login), lastLogin);
	    prefEditor.commit();
	}
	
	public static void setLastCreditCard(Context context, int lastCreditID) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		SharedPreferences.Editor prefEditor = sharedPref.edit();
	    prefEditor.putInt(context.getResources().getString(R.string.store_const_last_credit_card), lastCreditID);
	    prefEditor.commit();
	}
	
	public static void setIsAmPm(Context context, boolean isAmPm) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		SharedPreferences.Editor prefEditor = sharedPref.edit();
	    prefEditor.putBoolean(context.getResources().getString(R.string.store_const_is_am_pm), isAmPm);
	    prefEditor.commit();
	}
	
	public static void setFirstTimeMap(Context context, boolean isFirstTime) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		SharedPreferences.Editor prefEditor = sharedPref.edit();
	    prefEditor.putBoolean(context.getResources().getString(R.string.store_const_first_time_map), isFirstTime);
	    prefEditor.commit();
	}
	
	public static void setPreferredCompany(Context context, int preferCompDestID) {
		SharedPreferences sharedPref = context.getSharedPreferences("mobile_booker", 0);
		SharedPreferences.Editor prefEditor = sharedPref.edit();
	    prefEditor.putInt(context.getResources().getString(R.string.store_const_preferred_company), preferCompDestID);
	    prefEditor.commit();
	}
}
