package digital.dispatch.TaxiLimoNewUI.Task;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;

import com.digital.dispatch.TaxiLimoSoap.requests.RegDevRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.RegDevRequest.IRegDevResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.RegDevResponse;

import digital.dispatch.TaxiLimoNewUI.Installation;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;

public class RegisterDeviceTask extends AsyncTask<String, Integer, Boolean> implements IRegDevResponseListener, IRequestTimerListener {
	private static final String TAG = "RegisterDeviceTask";
	private RegDevRequest rdReq;
	private Context _context;
	private String GCMRegisterID;

	public RegisterDeviceTask(Context context, String GCMRegisterID) {
		rdReq = new RegDevRequest(this, this);
		_context = context;
		this.GCMRegisterID = GCMRegisterID;
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
			// rdReq.setPhoneNum(UserAccount.phoneNum(getApplicationContext()));
			// rdReq.setName(UserAccount.userName(getApplicationContext()));

			rdReq.setPhoneNum("7788594684");
			rdReq.setName("Rufus Zhu");

			if (Locale.getDefault().getLanguage().compareToIgnoreCase("de") == 0) {
				rdReq.setLocale("de-DE");
			} else {
				rdReq.setLocale("en-US");
			}

			rdReq.setVersion(versionName);
			rdReq.setProtocol(_context.getString(R.string.protocol));
			rdReq.setHardwareID(Installation.id(_context));
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
	public void onErrorResponse(String errorString) {

		new AlertDialog.Builder(_context).setTitle(R.string.err_error_response).setMessage(R.string.err_msg_reg_device).setCancelable(false).setPositiveButton(R.string.positive_button, null).show();

		Logger.v(TAG, "RegDev: ResponseError - " + errorString);

	}

	@Override
	public void onError() {

		if (checkConnection()) {
			new AlertDialog.Builder(_context).setTitle(R.string.err_no_response_error).setMessage(R.string.err_msg_no_response).setCancelable(false).setPositiveButton(R.string.positive_button, null)
					.show();
		} else {
			new AlertDialog.Builder(_context).setTitle(R.string.err_no_response_error).setMessage(R.string.err_msg_no_internet).setCancelable(false).setPositiveButton(R.string.positive_button, null)
					.show();
		}

		Logger.v(TAG, "RegDev: Error");
	}

	@Override
	public void onResponseReady(RegDevResponse response) {
		String str = "";
		str = response.getStatus() + " :: " + response.getErrorString();

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
