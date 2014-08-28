package digital.dispatch.TaxiLimoNewUI.Utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesManager {

	public static boolean loadBooleanPreferences(SharedPreferences sharedPreferences, String key, Boolean defaultValue) {
		return sharedPreferences.getBoolean(key, defaultValue);
	}
	
	public static String loadStringPreferences(SharedPreferences sharedPreferences, String key) {
		return sharedPreferences.getString(key,null);
	}

    public static int loadIntPreferences(SharedPreferences sharedPreferences, String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

	public static void savePreferences(SharedPreferences sharedPreferences, String key, boolean value) {
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void savePreferences(SharedPreferences sharedPreferences, String key, String value) {
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void savePreferences(SharedPreferences sharedPreferences, String key, int value) {
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
