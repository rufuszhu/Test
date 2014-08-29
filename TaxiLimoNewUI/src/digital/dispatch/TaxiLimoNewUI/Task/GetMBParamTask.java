package digital.dispatch.TaxiLimoNewUI.Task;

import java.util.ArrayList;

import com.digital.dispatch.TaxiLimoSoap.requests.GetMBParamRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.GetMBParamRequest.IMGParamResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.AttributeItem;
import com.digital.dispatch.TaxiLimoSoap.responses.GetMBParamResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.MGParam;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class GetMBParamTask extends AsyncTask<String, Integer, Void> 
implements IMGParamResponseListener, IRequestTimerListener {
private static final String TAG = "GetMBParamTask";
GetMBParamRequest gMBpReq;
Context _context;

public GetMBParamTask(Context context) {
	_context = context;
}

//The code to be executed in a background thread.  
@Override  
protected Void doInBackground(String... params)  
{
	gMBpReq = new GetMBParamRequest(this, this);
		
	gMBpReq.sendRequest(_context.getString(R.string.name_space), _context.getString(R.string.url));
	
	return null;  
}

@Override
public void onResponseReady(GetMBParamResponse response) {
	
	MGParam paramList = response.GetParams();
	ArrayList<AttributeItem> attributeList = response.getAttributeList();
	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
	SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_DROP_OFF_MANDATORY, paramList.getDropOffMand());
	SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_MULTI_BOOK_ALLOWED, paramList.getMultiBookAllowed());
	SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_PAYMENT_TMOUT, paramList.getPaymentTimeOut());
	SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_SAME_LOG_BOOK_ALLOWED, paramList.getSameLocBookAllowed());
	SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_SND_MSG_DRV, paramList.getMsgToDriver());
	SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_TIP_BUTTON1, paramList.getTip1Btn());
	
	for(int i=0;i<attributeList.size();i++){
		AttributeItem item = attributeList.get(i);
		Logger.i("id: " + item.getId() + ", icon: " + item.getAppIcon() + ", name: " + item.getName());
	}
}

@Override
public void onErrorResponse(String errorString) {
//	if (isForeGround && !haveAlert) {
//		haveAlert = true;
//		noParamRes = true;
//		
//		new AlertDialog.Builder(MainActivity.this)
//    		.setTitle(R.string.err_error_response)
//    		.setMessage(R.string.err_msg_get_param)
//    		.setCancelable(false)
//    		.setPositiveButton(R.string.positive_button, alertOffListener)
//    		.show();
//	}
	Utils.showErrorDialog(_context.getString(R.string.err_msg_get_param), _context);

	Logger.e(TAG, "error response: " + errorString);
}

@Override
public void onError() {
//	if (isForeGround && !haveAlert) {
//		haveAlert = true;
//		noParamRes = true; 
//		
//		new AlertDialog.Builder(MainActivity.this)
//        	.setTitle(R.string.err_no_response_error)
//        	.setMessage(R.string.err_msg_get_param)
//        	.setCancelable(false)
//        	.setPositiveButton(R.string.positive_button, alertOffListener)
//        	.show();
//	}
		
	Utils.showErrorDialog(_context.getString(R.string.err_msg_get_param), _context);
	Logger.e(TAG, "no response");
}

@Override
public void onProgressUpdate(int progress) {}
}

