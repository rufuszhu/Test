package digital.dispatch.TaxiLimoNewUI.Drawers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.RegisterDeviceTask;
import digital.dispatch.TaxiLimoNewUI.Task.VerifyDeviceTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
//import digital.dispatch.TaxiLimoNewUI.R.id;
//import digital.dispatch.TaxiLimoNewUI.R.layout;
//import digital.dispatch.TaxiLimoNewUI.R.menu;
//import android.text.TextWatcher;
//import android.app.AlertDialog;

public class ProfileActivity extends ActionBarActivity implements OnFocusChangeListener {

	private EditText edtPhone, edtName, edtUEmail, et_code;
	private LinearLayout save_btn, cancel_btn;
	private TextView verify_btn;
	private LinearLayout ll_sms_verify, button_groups;
	private TextView question_ic, cancel_ic, save_ic;
	private String curPhoneNum = "";
	private String curName = "";
	private String curEmail = "";
	private final String TAG = "ProfileActivity";
	private Context _context;
	boolean sendVerifySMS = false;
	boolean isChanged = false;
	boolean mBlockCompletion = false; // use this to bypass assigning existing value

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		findAndBindView();

	}

	@Override
	public void onResume() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
		if (sharedPreferences != null) {
			curPhoneNum = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER);
			mBlockCompletion = true;
			edtPhone.setText(curPhoneNum);
			curName = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_NAME);
			edtName.setText(curName);
			curEmail = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_EMAIL);
			edtUEmail.setText(curEmail);
			mBlockCompletion = false;

		}
		super.onResume();
	}

	private void findAndBindView() {

		edtPhone = (EditText) findViewById(R.id.edt_phoneNum);
		edtPhone.addTextChangedListener(new GenericTextWatcher(edtPhone));
		edtName = (EditText) findViewById(R.id.edt_name);
		edtName.addTextChangedListener(new GenericTextWatcher(edtName));
		edtUEmail = (EditText) findViewById(R.id.edt_userEmail);
		edtUEmail.addTextChangedListener(new GenericTextWatcher(edtUEmail));
		save_btn = (LinearLayout) findViewById(R.id.profile_save_btn);
		cancel_btn = (LinearLayout) findViewById(R.id.profile_cancel_btn);
		verify_btn = (TextView) findViewById(R.id.profile_verify_btn);
		_context = this;

		ll_sms_verify = (LinearLayout) findViewById(R.id.ll_sms_verify);

		button_groups = (LinearLayout) findViewById(R.id.profile_btn_group);
		// set up fontawesome icons
		Typeface fontFamily = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
		question_ic = (TextView) findViewById(R.id.question_circle);
		question_ic.setTypeface(fontFamily);
		question_ic.setText(MBDefinition.ICON_QUESTION_CIRCLE_CODE);

		cancel_ic = (TextView) findViewById(R.id.cancel_close_icon);
		cancel_ic.setTypeface(fontFamily);
		cancel_ic.setText(MBDefinition.ICON_TIMES_CODE);
		save_ic = (TextView) findViewById(R.id.save_check_icon);
		save_ic.setText(MBDefinition.ICON_CHECK_CODE);
		save_ic.setTypeface(fontFamily);

		edtName.setOnFocusChangeListener(this);
		edtUEmail.setOnFocusChangeListener(this);
		edtPhone.setOnFocusChangeListener(this);
		/*
		 * edtName.addTextChangedListener(new TextWatcher() {
		 * 
		 * @Override public void afterTextChanged(Editable note) {
		 * 
		 * if(!curName.equals(note.toString())){ LinearLayout buttons = (LinearLayout) findViewById(R.id.profile_btn_group);
		 * buttons.setVisibility(View.VISIBLE); }
		 * 
		 * }
		 * 
		 * @Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { if(mBlockCompletion) return; }
		 * 
		 * @Override public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { } });
		 * 
		 * edtUEmail.addTextChangedListener(new TextWatcher() {
		 * 
		 * @Override public void afterTextChanged(Editable note) { if(!curEmail.equals(note.toString())){ LinearLayout buttons = (LinearLayout)
		 * findViewById(R.id.profile_btn_group); buttons.setVisibility(View.VISIBLE); } }
		 * 
		 * @Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { if(mBlockCompletion) return; }
		 * 
		 * @Override public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { } });
		 * 
		 * edtPhone.addTextChangedListener(new TextWatcher() {
		 * 
		 * @Override public void afterTextChanged(Editable note) { if(!curPhoneNum.equals(note.toString())){ LinearLayout buttons = (LinearLayout)
		 * findViewById(R.id.profile_btn_group); buttons.setVisibility(View.VISIBLE); }
		 * 
		 * }
		 * 
		 * @Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { if(mBlockCompletion) return; }
		 * 
		 * @Override public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { } });
		 */

		save_btn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (validate(null)) {

					storeInfo();
					// send register device again
					boolean isFirstTime = false; // set this parameter to false when called from profile page
					String regid = getRegistrationId(_context);
					new RegisterDeviceTask(_context, regid, isFirstTime, sendVerifySMS).execute();
					Utils.showProcessingDialog(_context);

				}
			}
		});
		cancel_btn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				finish();
			}
		});

		verify_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// send VerifySMSRequest
				boolean isFirstTime = false; // set this parameter to false when called from profile page
				new VerifyDeviceTask(_context, isFirstTime, et_code.getText().toString()).execute();
				Utils.showProcessingDialog(_context);
			}
		});

		et_code = (EditText) findViewById(R.id.et_code);
		TextView request_new_btn = (TextView) findViewById(R.id.request_new_btn);
		// request new verification code, here need to refresh the phone number in case number is updated
		request_new_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validate(null)) {
					storeInfo();
					boolean isFirstTime = false; // set this parameter to false when called from profile page
					sendVerifySMS = true;
					String regid = getRegistrationId(_context);
					new RegisterDeviceTask(_context, regid, isFirstTime, sendVerifySMS).execute();
					Utils.showProcessingDialog(_context);
				}
			}
		});

	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		// edit_icon = menu.findItem(R.id.action_edit);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

		

	private boolean validate(EditText target) {

		if (target != null) {
			if (target == edtName) {
				return validateName();
			} else if (target == edtUEmail) {
				return validateEmail();
			} else if (target == edtPhone) {
				return validatePhone();
			} else
				return false;
		}
		// validate all if not from afterTextChanged
		else {
			return validateName() && validateEmail() && validatePhone();
		}

	}

	private boolean validateName() {
		String userName = edtName.getText().toString();

		if (userName.length() == 0) {
			edtName.setError(getResources().getString(R.string.empty_name));
			return false;
		} else {
			return true;
		}

	}

	private boolean validatePhone() {
		String phone = edtPhone.getText().toString();
		if (phone.length() == 0) {
			edtPhone.setError(getResources().getString(R.string.empty_phone_number));
			return false;
		} else if (phone.length() > 13) {
			edtPhone.setError(getResources().getString(R.string.phone_number_too_long));
			return false;
		} else {
			// if phone number changed we need show sms verification
			if (!phone.equalsIgnoreCase(curPhoneNum)) {
				sendVerifySMS = true;

			}
			return true;
		}
	}

	private boolean validateEmail() {
		boolean isValid = true;

		String newEmail = edtUEmail.getText().toString();

		if (newEmail.equalsIgnoreCase("")) {
			edtUEmail.setError(getResources().getString(R.string.credit_card_empty_email));
			isValid = false;
		} else {
			// check if the entered email is a valid pattern
			Pattern ePattern = Pattern.compile(MBDefinition.EMAIL_PATTERN);
			Matcher matcher = ePattern.matcher(newEmail);

			if (matcher.matches()) {
				edtUEmail.setError(null);
			} else {
				edtUEmail.setError(getResources().getString(R.string.credit_card_email_invalid));
				isValid = false;
			}
		}

		return isValid;
	}

	private void storeInfo() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((_context));
		String userName = edtName.getText().toString();

		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_NAME, userName);
		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_EMAIL, edtUEmail.getText().toString());
		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER, edtPhone.getText().toString());

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

		if (!hasFocus) {

			validate((EditText) v);

		}

	}

	private String getRegistrationId(Context context) {
		// getGCMPreferences
		final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
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

	// callback by VerifyDeviceTask
	public void showProfileVerifySuccessMessage() {
		Dialog messageDialog = new Dialog(_context);
		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.dialog_message);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView tv_message = (TextView) messageDialog.getWindow().findViewById(R.id.tv_message);
		tv_message.setText(_context.getString(R.string.verify_success));

		messageDialog.show();
		// restore original profile layout
		ll_sms_verify.setVisibility(View.GONE);
		verify_btn.setVisibility(View.GONE);
		button_groups.setVisibility(View.GONE);

	}

	public void showProfileVerifyFailedMessage() {
		et_code.setText("");
		verify_btn.setVisibility(View.GONE);
		Dialog messageDialog = new Dialog(_context);
		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.dialog_message);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView tv_message = (TextView) messageDialog.getWindow().findViewById(R.id.tv_message);
		tv_message.setText(_context.getString(R.string.verify_failed));

		messageDialog.show();
	}

	// callback by RegisterDeviceTask for successful update
	public void showResendSuccessMessage() {
		Dialog messageDialog = new Dialog(_context);
		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.dialog_message);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView tv_message = (TextView) messageDialog.getWindow().findViewById(R.id.tv_message);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);

		String phone = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER);

		if (sendVerifySMS == true) {
			tv_message.setText(_context.getString(R.string.verify_dialog_text, phone));
		} else {
			tv_message.setText(_context.getString(R.string.profile_update_text));

		}

		messageDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (sendVerifySMS == true) {
					ll_sms_verify.setVisibility(View.VISIBLE);
					save_btn.setVisibility(View.GONE);
					et_code.requestFocus();

				} else {
					// save is done hide all buttons
					button_groups.setVisibility(View.GONE);
				}

			}
		});
		messageDialog.show();
	}

	// inner class implements TextWatcher for show and hide button when user enter something
	private class GenericTextWatcher implements TextWatcher {

		private View view;

		private GenericTextWatcher(View view) {
			this.view = view;
		}

		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			if (mBlockCompletion)
				return;
		}

		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		public void afterTextChanged(Editable editable) {
			String text = editable.toString();
			switch (view.getId()) {
			case R.id.edt_name:
				if (!curName.equals(text)) {
					button_groups.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.edt_userEmail:
				if (!curEmail.equals(text)) {
					button_groups.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.edt_phoneNum:
				if (!curPhoneNum.equalsIgnoreCase(text)) {
					button_groups.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.et_code:
				verify_btn.setVisibility(View.VISIBLE);
			default:
				break;
			}
		}
	}

}
