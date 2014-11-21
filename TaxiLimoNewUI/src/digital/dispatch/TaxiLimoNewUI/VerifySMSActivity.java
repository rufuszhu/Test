package digital.dispatch.TaxiLimoNewUI;

import digital.dispatch.TaxiLimoNewUI.Task.RegisterDeviceTask;
import digital.dispatch.TaxiLimoNewUI.Task.VerifyDeviceTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class VerifySMSActivity extends Activity {
	private static final String TAG = "VerifySMSActivity";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private EditText et_code;
	private Context _context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_sms);
		findViewAndBindEvent();
		_context=this;

	}

	private void findViewAndBindEvent() {
		et_code = (EditText) findViewById(R.id.et_code);
		TextView request_new_btn = (TextView) findViewById(R.id.request_new_btn);
		request_new_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isFirstTime = false;
				boolean sendVerifySMS = true;
				String regid = getRegistrationId(_context);
				new RegisterDeviceTask(_context, regid, isFirstTime, sendVerifySMS).execute();
				Utils.showProcessingDialog(_context);
			}
		});

		TextView verify_btn = (TextView) findViewById(R.id.verify_btn);
		verify_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new VerifyDeviceTask(_context, et_code.getText().toString()).execute();
				Utils.showProcessingDialog(_context);
			}
		});
	}

	public void showResendSuccessMessage() {
		Dialog messageDialog = new Dialog(_context);
		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.dialog_message);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView tv_message = (TextView) messageDialog.getWindow().findViewById(R.id.tv_message);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);

		String phone = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER);
		tv_message.setText(_context.getString(R.string.verify_dialog_text, phone));


		messageDialog.show();
	}

	public void showVerifySuccessMessage() {
		Dialog messageDialog = new Dialog(_context);
		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.dialog_message);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView tv_message = (TextView) messageDialog.getWindow().findViewById(R.id.tv_message);
		tv_message.setText(_context.getString(R.string.verify_success));
		messageDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				Intent intent = new Intent(_context, RegisterCreditCardActivity.class);
				startActivity(intent);
			}
		});
		messageDialog.show();
	}
	
	public void showVerifyFailedMessage() {
		Dialog messageDialog = new Dialog(_context);
		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.dialog_message);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView tv_message = (TextView) messageDialog.getWindow().findViewById(R.id.tv_message);
		tv_message.setText(_context.getString(R.string.verify_failed));

		messageDialog.show();
	}
	

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Logger.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}

}
