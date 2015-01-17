package digital.dispatch.TaxiLimoNewUI.Task;

import java.util.ArrayList;

import com.digital.dispatch.TaxiLimoSoap.requests.GetMBParamRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.GetMBParamRequest.IMGParamResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.AttributeItem;
import com.digital.dispatch.TaxiLimoSoap.responses.GetMBParamResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.MGParam;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

import digital.dispatch.TaxiLimoNewUI.DBAttribute;
import digital.dispatch.TaxiLimoNewUI.DBAttributeDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
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
    SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_SUPPORT_EMAIL, paramList.getSupportEmail()); //TL-379
    SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_SUPPORT_PHONE, paramList.getSupportPhone());

	
//	for(int i=0;i<attributeList.size();i++){
//		AttributeItem item = attributeList.get(i);
//		Logger.i("id: " + item.getId() + ", icon: " + item.getAppIcon() + ", name: " + item.getName());
//	}
	saveAttributeListToDB(attributeList);
}

public void saveAttributeListToDB(ArrayList<AttributeItem> attributeList) {
	DaoManager daoManager = DaoManager.getInstance(_context);
	DBAttributeDao attributeDao = daoManager.getDBAttributeDao(DaoManager.TYPE_WRITE);
	attributeDao.deleteAll();
	for(int i=0; i < attributeList.size();i++){
		AttributeItem item = attributeList.get(i);
		DBAttribute dbattr = new DBAttribute(null, item.getId(), item.getName(), item.getAppIcon());
		attributeDao.insert(dbattr);
	}
}

@Override
public void onErrorResponse(ResponseWrapper resWrapper) {
	//TL-248
	Utils.showErrorDialog(_context.getString(R.string.err_msg_get_param), _context);
	Logger.e(TAG, "error response: " + resWrapper.getErrorString());
}

@Override
public void onError() {
	Utils.showErrorDialog(_context.getString(R.string.err_msg_get_param), _context);
	Logger.e(TAG, "no response");
}

@Override
public void onProgressUpdate(int progress) {}
}

