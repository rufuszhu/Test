package digital.dispatch.TaxiLimoNewUI.GCM;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMReceiver extends GCMBroadcastReceiver {
	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		// change the GCMIntentService location if we are not using default main folder location
		return "digital.dispatch.TaxiLimoNewUI.GCM.GCMIntentService"; 
	} 	
}
