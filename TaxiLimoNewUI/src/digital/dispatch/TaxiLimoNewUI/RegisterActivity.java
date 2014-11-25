package digital.dispatch.TaxiLimoNewUI;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;


import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.Task.RegisterDeviceTask;
import digital.dispatch.TaxiLimoNewUI.Task.VerifyDeviceTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class RegisterActivity extends BaseActivity implements OnFocusChangeListener {
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	
	protected static final String TAG = "RegisterActivity";
	GoogleCloudMessaging gcm;
	String regid;
	
	private EditText name, email, phone_number;
	private TextView next_btn, register_btn;
	private LinearLayout ll_sms_verify;
	
	private Context _context;
	private EditText et_code;
	private TextView question_ic;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);
		getActionBar().setTitle(R.string.title_activity_register);
		Typeface fontFamily = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        question_ic = (TextView) findViewById(R.id.question_circle);
        question_ic.setTypeface(fontFamily);
        question_ic.setText("\uf059");
		findAndBindView();
		
		_context = this;
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, MBDefinition.SHARE_ALREADY_REGISTER, false)) {
			Intent intent = new Intent(_context, MainActivity.class);
			startActivity(intent);
			finish();
		}*/
	}

	

	private void findAndBindView() {
		name = (EditText) findViewById(R.id.name);	
		email = (EditText) findViewById(R.id.email);
		phone_number = (EditText) findViewById(R.id.phone_number);
		next_btn = (TextView) findViewById(R.id.next_btn);
		ll_sms_verify = (LinearLayout) findViewById(R.id.ll_sms_verify);
		


		name.setOnFocusChangeListener(this);
		email.setOnFocusChangeListener(this);
		phone_number.setOnFocusChangeListener(this);

		next_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				if (validate(null)) {
					storeInfo();
					
					// Check device for Play Services APK. If check succeeds, proceed with
					// GCM registration.
					if (checkPlayServices()) {
						gcm = GoogleCloudMessaging.getInstance(_context);
						regid = getRegistrationId(_context);
						Logger.e("GCM id: " + regid);
						
						Utils.showProcessingDialog(_context);
						registerInBackground();
						
					} else {
						Log.i(TAG, "No valid Google Play Services APK found.");
						Toast.makeText(_context,"No valid Google Play Services APK found.", Toast.LENGTH_LONG).show();
					}	
				}
			}

		});
		
		et_code = (EditText) findViewById(R.id.et_code);
		TextView request_new_btn = (TextView) findViewById(R.id.request_new_btn);
		//request new verification code, here need to refresh the phone number in case number is updated
		request_new_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validate(null)) {
					storeInfo();
					boolean isFirstTime = true; //set this parameter to false when called from profile page
					boolean sendVerifySMS = true;
					String regid = getRegistrationId(_context);
					new RegisterDeviceTask(_context, regid, isFirstTime, sendVerifySMS).execute();
					Utils.showProcessingDialog(_context);
			}
		}});

		register_btn = (TextView) findViewById(R.id.register_btn);
		register_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new VerifyDeviceTask(_context, et_code.getText().toString()).execute();
				Utils.showProcessingDialog(_context);
			}
		});
	}
	
	public void showRegisterSuccessMessage(){
		Dialog messageDialog = new Dialog(_context);
		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.dialog_message);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView tv_message = (TextView) messageDialog.getWindow().findViewById(R.id.tv_message);
		tv_message.setText(_context.getString(R.string.verify_dialog_text,phone_number.getText().toString()));
		messageDialog.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface dialog) {
				ll_sms_verify.setVisibility(View.VISIBLE);
				next_btn.setVisibility(View.GONE);
				register_btn.setVisibility(View.VISIBLE);
			}});
		messageDialog.show();
	}
	
	private void storeInfo() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_NAME, name.getText().toString());
		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_EMAIL, email.getText().toString());
		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER, phone_number.getText().toString());
		
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			validate((EditText) v);
		}
	}

	private boolean validate(EditText target) {

		if (target != null) {
			if (target == name) {
				return validateName();
			} else if (target == email) {
				return validateEmail();
			} else if (target == phone_number) {
				return validatePhone();
			} else
				return false;
		}
		// validate all if not from afterTextChanged
		else {
			return validateName()  && validateEmail()  && validatePhone();
		}

	}

	private boolean validateName() {
		String userName = name.getText().toString();
		if (userName.length() == 0) {
			name.setError(getResources().getString(R.string.empty_name));
			return false;
		} else {
			return true;
		}
	}

	private boolean validatePhone() {
		String name = phone_number.getText().toString();
		if (name.length() == 0) {
			phone_number.setError(getResources().getString(R.string.empty_phone_number));
			return false;
		} else if (name.length() > 13) {
			phone_number.setError(getResources().getString(R.string.phone_number_too_long));
			return false;
		} else
			return true;
	}

	

	private boolean validateEmail() {
		boolean isValid = true;

		if (email.getText().toString().equalsIgnoreCase("")) {
			email.setError(getResources().getString(R.string.credit_card_empty_email));
			isValid = false;
		} else {
			// check if the entered email is a valid pattern
			Pattern ePattern = Pattern.compile(EMAIL_PATTERN);
			Matcher matcher = ePattern.matcher(email.getText().toString());

			if (matcher.matches()) {
				email.setError(null);
			} else {
				email.setError(getResources().getString(R.string.credit_card_email_invalid));
				isValid = false;
			}
		}
		return isValid;
	}

	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If it doesn't, display a dialog that allows users to download the APK from the Google Play Store or enable it in the device's
	 * system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
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
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Integer, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(_context);
					}
					regid = gcm.register(CommonUtilities.SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your app.
					// The request to your server should be authenticated if your app
					// is using accounts.
					boolean isFirstTime = true;
					boolean verifySMS = true;
					new RegisterDeviceTask(_context, regid, isFirstTime, verifySMS).execute();

					// For this demo: we don't need to send it because the device
					// will send upstream messages to a server that echo back the
					// message using the 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(_context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// mDisplay.append(msg + "\n");
				Logger.e(TAG, msg);
			}

		}.execute(null, null, null);
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Logger.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
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

	//callback by RegisterDevice Task
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
				//flow to register confirmation page
				Intent intent = new Intent(_context, RegisterConfirmActivity.class);
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

}
