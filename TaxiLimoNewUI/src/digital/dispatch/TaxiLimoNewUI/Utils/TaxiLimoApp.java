package digital.dispatch.TaxiLimoNewUI.Utils;

import com.digital.dispatch.TaxiLimoSQLDatabase.DatabaseHandler;

public class TaxiLimoApp {
	static private String TAG = "TaxiLimoApp";
	public DatabaseHandler mainDatabase;
	public DatabaseHandler getDatabase() {
		return mainDatabase;
	}

	public void setDatabase(DatabaseHandler mainDatabase) {
		this.mainDatabase = mainDatabase;
	}

	public void onCreate() 
	{
		Logger.v (TAG, "++MobileBookerApp: OnCreate");
	}

	public void onTerminate() 
	{
	    
	}
}
