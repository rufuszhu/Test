package digital.dispatch.TaxiLimoNewUI.Task;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.requests.RegDevRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.RegDevRequest.IRegDevResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.RegDevResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

import digital.dispatch.TaxiLimoNewUI.Installation;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.RegisterActivity;
import digital.dispatch.TaxiLimoNewUI.Drawers.ProfileActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class RegisterDeviceTask extends AsyncTask<String, Integer, Boolean> implements IRegDevResponseListener, IRequestTimerListener {
	private static final String TAG = "RegisterDeviceTask";
	private RegDevRequest rdReq;
	private Context _context;
	private String GCMRegisterID;
	private boolean isFirstTime, sendVerifySMS, isUpdateGCM;
	private static final int RESPONSE_STATUS_INVALID_EMAIL = 2;
	private static final int RESPONSE_STATUS_BLACKLISTED = 3;

	public RegisterDeviceTask(Context context, String GCMRegisterID, boolean isFirstTime, boolean sendVerifySMS, boolean isUpdateGCM) {
		rdReq = new RegDevRequest(this, this);
		_context = context;
		this.GCMRegisterID = GCMRegisterID;
		this.isFirstTime = isFirstTime;
		this.sendVerifySMS = sendVerifySMS;
		//this is used only when old users to update the GCM id after they update the app 
		this.isUpdateGCM = isUpdateGCM;
	}

	// The code to be executed in a background thread.
	@Override
	protected Boolean doInBackground(String... params) {
		try {
			String versionName = "";
			try {
				versionName = _context.getPackageManager().getPackageInfo(_context.getPackageName(), 0).versionName;
			} catch (Exception e) {
				Logger.v(TAG, e.toString());
				e.printStackTrace();
			}

			Logger.e(TAG, _context.getString(R.string.reg_device_start));
			rdReq.setToken(GCMRegisterID);
			String userName = params[0];
			String userEmail = params[1];
			String phone = params[2];
			
			rdReq.setPhoneNum(phone);
			rdReq.setName(userName);
			rdReq.setEmail(userEmail); //TL-241

			if (Locale.getDefault().getLanguage().compareToIgnoreCase("de") == 0) {
				rdReq.setLocale("de-DE");
			} else {
				rdReq.setLocale("en-US");
			}
			
			if(sendVerifySMS)
				rdReq.setSmsverify("Y");
			else
				rdReq.setSmsverify("N");
			rdReq.setVersion(versionName);
			rdReq.setProtocol(_context.getString(R.string.protocol));
			rdReq.setHardwareID(Utils.getHardWareId(_context));
			rdReq.settype("Android " + Build.VERSION.RELEASE);
			rdReq.sendRequest(_context.getString(R.string.name_space), _context.getString(R.string.url));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public void onProgressUpdate(int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onErrorResponse(ResponseWrapper resWrapper) {
		Utils.stopProcessingDialog(_context);
		//TL-241 check API documents first MB has status beyond 0 or 1 but OSP only has status 0 or 1
		if(resWrapper.getStatus() == RESPONSE_STATUS_INVALID_EMAIL){
			new AlertDialog.Builder(_context).setTitle(R.string.err_error_response).setMessage(R.string.err_msg_reg_email).setCancelable(false).setPositiveButton(R.string.ok, null).show();
		}else if(resWrapper.getStatus() == RESPONSE_STATUS_BLACKLISTED){ //TL-248
			Utils.showUILockScreen(_context);
		}
		else{
			new AlertDialog.Builder(_context).setTitle(R.string.err_error_response).setMessage(R.string.err_msg_no_response).setCancelable(false).setPositiveButton(R.string.ok, null).show();
		}
		
		
		Logger.v(TAG, "RegDev: ResponseError - " + resWrapper.getErrorString());

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
	public void onResponseReady(RegDevResponse response) {
		Utils.stopProcessingDialog(_context);
		String str = "";
		str = response.getStatus() + " :: " + response.getErrorString();
		if(isUpdateGCM){
			//do nothing
		}
		else if(isFirstTime)
			((RegisterActivity)_context).showRegisterSuccessMessage();
		else
			((ProfileActivity)_context).showResendSuccessMessage();
		
		Logger.e(TAG, "RegDev: server-" + _context.getString(R.string.url) + ", response-" + str);

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

		/*
		 * State mobile = conMan.getNetworkInfo(0).getState(); State wifi = conMan.getNetworkInfo(1).getState();
		 * 
		 * if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) { //mobile } else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
		 * //wifi }
		 */
	}
	

	


}
