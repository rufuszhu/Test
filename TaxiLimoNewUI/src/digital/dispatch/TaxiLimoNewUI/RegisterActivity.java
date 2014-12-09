package digital.dispatch.TaxiLimoNewUI;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import digital.dispatch.TaxiLimoNewUI.Drawers.ProfileActivity;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.Task.RegisterDeviceTask;
import digital.dispatch.TaxiLimoNewUI.Task.VerifyDeviceTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class RegisterActivity extends BaseActivity implements OnFocusChangeListener {
	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	
	protected static final String TAG = "RegisterActivity";
	GoogleCloudMessaging gcm;
	String regid;
	
	private EditText name, email, phone_number;
	private TextView next_btn, verify_btn;
	private LinearLayout ll_sms_verify;
	private Context _context;
	private EditText et_code;
	private TextView question_ic;
	private String curPhoneNum = "";
	private String curName = "";
	private String curEmail = "";
	boolean mBlockCompletion = false; //use this to bypass assigning existing value


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);
		getActionBar().setTitle(R.string.title_activity_register);
		
		findAndBindView();
		
		_context = this;
		
		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(_context);
			regid = getRegistrationId(_context);
			Logger.e("GCM id: " + regid);
			registerInBackground();	
			
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
			Toast.makeText(_context,"No valid Google Play Services APK found.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean alreadyRegister = SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, MBDefinition.SHARE_ALREADY_REGISTER, false);
		boolean alreadySMSVerify = SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, MBDefinition.SHARE_ALREADY_SMS_VERIFY, false);
		boolean startRegister = SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, MBDefinition.SHARE_START_REGISTER, false);
		
		if (alreadyRegister && alreadySMSVerify	) {
			Intent intent = new Intent(_context, MainActivity.class);
			startActivity(intent);
			finish();
		}
		else if (alreadyRegister && !alreadySMSVerify)
		{
			//show profile page if SMS is not verified
			Intent intent = new Intent(_context, ProfileActivity.class);
			startActivity(intent);
			finish();
			
		}else if (!alreadyRegister && alreadySMSVerify)
		{
			//show register confirmation page if SMS is verified but registration is not completed
			Intent intent = new Intent(_context, RegisterConfirmActivity.class);
			startActivity(intent);
			finish();
		}
		
		if(startRegister && !alreadySMSVerify)
		{		
			//loaded store info if user registered but not sms verified
			curPhoneNum = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER);
			mBlockCompletion = true;
			phone_number.setText(curPhoneNum);
			curName = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_NAME);
			name.setText(curName);
			curEmail = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_EMAIL);
			email.setText(curEmail);
			mBlockCompletion = false;
			
			ll_sms_verify.setVisibility(View.VISIBLE);
			next_btn.setVisibility(View.GONE);
			et_code.requestFocus();
		}
			
		
	}

	

	private void findAndBindView() {
		
		name = (EditText) findViewById(R.id.name);
		name.addTextChangedListener(new GenericTextWatcher(name));
		email = (EditText) findViewById(R.id.email);
		email.addTextChangedListener(new GenericTextWatcher(email));
		phone_number = (EditText) findViewById(R.id.phone_number);
		phone_number.addTextChangedListener(new GenericTextWatcher(phone_number));
		
		
		next_btn = (TextView) findViewById(R.id.next_btn);
		ll_sms_verify = (LinearLayout) findViewById(R.id.ll_sms_verify);
		
		
		Typeface icon_pack = Typeface.createFromAsset(getAssets(), "fonts/icon_pack.ttf");
        question_ic = (TextView) findViewById(R.id.question_circle);
        question_ic.setTypeface(icon_pack);
        question_ic.setText(MBDefinition.ICON_QUESTION_CIRCLE_CODE);


		name.setOnFocusChangeListener(this);
		email.setOnFocusChangeListener(this);
		phone_number.setOnFocusChangeListener(this);

		next_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				if (validate(null)) {
								
					Utils.showProcessingDialog(_context);
					
					boolean isFirstTime = true;
					boolean verifySMS = true;
					RegisterDeviceTask task = new RegisterDeviceTask(_context, regid, isFirstTime, verifySMS);
					String[] params = {name.getText().toString(), email.getText().toString(), phone_number.getText().toString()};
					task.execute(params);
				}
			}

		});
		
		et_code = (EditText) findViewById(R.id.et_code);
		et_code.addTextChangedListener(new GenericTextWatcher(et_code));
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
					RegisterDeviceTask task = new RegisterDeviceTask(_context, regid, isFirstTime, sendVerifySMS);
					String[] params = {name.getText().toString(), email.getText().toString(), phone_number.getText().toString()};
					task.execute(params);
					Utils.showProcessingDialog(_context);
			}
		}});

		verify_btn = (TextView) findViewById(R.id.verify_btn);
		verify_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isFirstTime = true; //set this parameter to false when called from profile page
				new VerifyDeviceTask(_context, isFirstTime, et_code.getText().toString()).execute();
				Utils.showProcessingDialog(_context);
			}
		});
	}
	
	//callback by RegisterDeviceTask
	public void showRegisterSuccessMessage(){
		storeInfo();
		//register info stored
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(_context);
		SharedPreferencesManager.savePreferences(sharedPreferences,
				MBDefinition.SHARE_START_REGISTER, true);
		
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
				et_code.requestFocus();
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
			Pattern ePattern = Pattern.compile(MBDefinition.EMAIL_PATTERN);
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
		String registrationId = prefs.getString(MBDefinition.PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Logger.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(MBDefinition.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = Utils.getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
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
		int appVersion = Utils.getAppVersion(context);
		Logger.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(MBDefinition.PROPERTY_REG_ID, regId);
		editor.putInt(MBDefinition.PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}
	

	//callback by RegisterDevice Task
	public void showVerifySuccessMessage() {
		//SMS Verified
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(_context);
		SharedPreferencesManager.savePreferences(sharedPreferences,
				MBDefinition.SHARE_ALREADY_SMS_VERIFY, true);
		
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
		et_code.setText("");
		Dialog messageDialog = new Dialog(_context);
		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.dialog_message);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView tv_message = (TextView) messageDialog.getWindow().findViewById(R.id.tv_message);
		tv_message.setText(_context.getString(R.string.verify_failed));

		messageDialog.show();
	}
	
	//inner class implements TextWatcher for show and hide button when user enter something
	private class GenericTextWatcher implements TextWatcher{

	    private View view;
	    private GenericTextWatcher(View view) {
	        this.view = view;
	    }

	    
	    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			if (mBlockCompletion)
				return;
		}
	    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

	    public void afterTextChanged(Editable editable) {
	    	   
	        switch(view.getId()){
	            case R.id.name:    
	            case R.id.email:    	
	            case R.id.phone_number:
	            	next_btn.setVisibility(View.VISIBLE);
	                break;
	            case R.id.et_code:
	            	verify_btn.setVisibility(View.VISIBLE);
	            default:
	            	break;
	        }
	    }
	}

}
