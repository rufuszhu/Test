package digital.dispatch.TaxiLimoNewUI.Task;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.requests.VerifySMSRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.VerifySMSRequest.IVerifySMSResponseListener;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;
import com.digital.dispatch.TaxiLimoSoap.responses.VerifySMSResponse;

import digital.dispatch.TaxiLimoNewUI.Drawers.ProfileActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.RegisterActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class VerifyDeviceTask extends AsyncTask<String, Integer, Boolean> implements IVerifySMSResponseListener, IRequestTimerListener {
	private static final String TAG = "VerifyDeviceTask";
	private VerifySMSRequest rdReq;
	private Context _context;
	private String smsCode;
	private boolean isFirstTime;
	private static final int VERIFY_DEVICE_RESP_STATUS_BLACKLISTED = 2;
	private static final int VERIFY_DEVICE_RESP_STATUS_SMS_NOT_MATCH = 3;

	//TL-170  added flag isFirstTime to differentiate the call from register or profile page
	public VerifyDeviceTask(Context context, boolean isFirstTime, String smsCode) {
		rdReq = new VerifySMSRequest(this, this);
		_context = context;
		this.smsCode = smsCode;
		this.isFirstTime = isFirstTime;
	}

	// The code to be executed in a background thread.
	@Override
	protected Boolean doInBackground(String... params) {
		try {
			Logger.d(TAG, _context.getString(R.string.reg_device_start));
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
			String phone = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER);
			rdReq.setPhoneNr(phone);
			rdReq.setProtocol(_context.getString(R.string.protocol));
			rdReq.setHardwareID(Utils.getHardWareId(_context));
			rdReq.setSmsCode(smsCode);
			rdReq.sendRequest(_context.getString(R.string.name_space), _context.getString(R.string.url));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	
	@Override
	public void onProgressUpdate(int progress) {
		
	}

	public void onErrorResponse(ResponseWrapper resWrapper) {
		Utils.stopProcessingDialog(_context);
		//TL-248
		if(resWrapper.getStatus() == VERIFY_DEVICE_RESP_STATUS_BLACKLISTED){
			//TODO display UI Lock screen
			Utils.showUILockScreen(_context);
		}
		else if (resWrapper.getStatus() == VERIFY_DEVICE_RESP_STATUS_SMS_NOT_MATCH){
			
			if(isFirstTime)
				((RegisterActivity)_context).showVerifyFailedMessage();
			else
				((ProfileActivity)_context).showProfileVerifyFailedMessage();
		}else{
			//default error code
			new AlertDialog.Builder(_context).setTitle(R.string.err_error_response).setMessage(R.string.err_msg_no_response).setCancelable(false).setPositiveButton(R.string.ok, null)
			.show();
			
		}
		Logger.v(TAG, "RegDev: ResponseError - " + resWrapper.getErrCode());

	}

	@Override
	public void onError() {
		Utils.stopProcessingDialog(_context);
		if (checkConnection()) {
			new AlertDialog.Builder(_context).setTitle(R.string.err_no_response_error).setMessage(R.string.err_msg_no_response).setCancelable(false).setPositiveButton(R.string.ok, null)
					.show();
		} else {
			new AlertDialog.Builder(_context).setTitle(R.string.err_no_response_error).setMessage(R.string.err_msg_no_internet).setCancelable(false).setPositiveButton(R.string.ok, null)
			.show();
		}

		Logger.v(TAG, "RegDev: Error");
	}

	@Override
	public void onResponseReady(VerifySMSResponse response) {
		Utils.stopProcessingDialog(_context);
		String str = "";
		str = response.getStatus() + " :: " + response.getErrorString();
		
		if(isFirstTime)
			((RegisterActivity)_context).showVerifySuccessMessage();
		else
			((ProfileActivity)_context).showProfileVerifySuccessMessage();
		
		Logger.d(TAG, "VerifyDev: server-" + _context.getString(R.string.url) + ", response-" + str);
	}

	public boolean checkConnection() {
		ConnectivityManager conMan = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = conMan.getActiveNetworkInfo();

		if (ni == null) {
			return false;
		} else if (ni.isConnected()) {
			return true;
		} else {
			return false;
		}

		
	}

	

}
