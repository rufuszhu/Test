package digital.dispatch.TaxiLimoNewUI.Utils;

import android.util.Log;

public class Logger {
	private final static boolean IS_OPEN = true;
    private final static String TAG = "TaxiLimo";
    
    public static void e(String tag, String log) {
        if(IS_OPEN) {
            Log.e(tag, log);
        }
    }
    
    
    public static void e(String log) {
        if(IS_OPEN) {
            Log.e(TAG, log);
        }
    }
    
    public static void i(String tag, String log) {
        if(IS_OPEN) {
            Log.i(tag, log);
        }
    }
    
    
    public static void i(String log) {
        if(IS_OPEN) {
            Log.i(TAG, log);
        }
    }
    
    public static void v(String tag, String log) {
        if(IS_OPEN) {
            Log.v(tag, log);
        }
    }
    
    
    public static void v(String log) {
        if(IS_OPEN) {
            Log.v(TAG, log);
        }
    }
}
