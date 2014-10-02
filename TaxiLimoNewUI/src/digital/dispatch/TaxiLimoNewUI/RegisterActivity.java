package digital.dispatch.TaxiLimoNewUI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import digital.dispatch.TaxiLimoNewUI.Book.ModifyAddressActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegisterActivity extends BaseActivity implements OnFocusChangeListener {
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private EditText first_name, last_name, email, re_enter_email, phone_number;
	private TextView next_btn, register_title;
	private LinearLayout ll_info_form, ll_pay_btns;
	private Animation infoFormLeaveAnimation;
	private Context _context;

	// private EditText cc_nick_name,cc_number,cc_holder_name,cc_yy,cc_mm,cc_zip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);
		getActionBar().setTitle(R.string.title_activity_register);
		findAndBindView();
		// setAnimation();
		_context = this;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, MBDefinition.SHARE_ALREADY_REGISTER, false)) {
			Intent intent = new Intent(_context, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void setAnimation() {
		infoFormLeaveAnimation = AnimationUtils.loadAnimation(_context, R.anim.base_activity_exit);

		infoFormLeaveAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation anim) {
			};

			public void onAnimationRepeat(Animation anim) {
			};

			public void onAnimationEnd(Animation anim) {
				ll_info_form.setVisibility(View.GONE);

			};
		});

	}

	private void findAndBindView() {
		first_name = (EditText) findViewById(R.id.first_name);
		last_name = (EditText) findViewById(R.id.last_name);
		email = (EditText) findViewById(R.id.email);
		re_enter_email = (EditText) findViewById(R.id.re_enter_email);
		phone_number = (EditText) findViewById(R.id.phone_number);
		next_btn = (TextView) findViewById(R.id.next_btn);
		ll_info_form = (LinearLayout) findViewById(R.id.ll_info_form);
		register_title = (TextView) findViewById(R.id.register_title);

		// cc_nick_name = (EditText) findViewById(R.id.cc_nick_name);
		// cc_holder_name = (EditText) findViewById(R.id.cc_holder_name);
		// cc_number = (EditText) findViewById(R.id.cc_number);
		// cc_mm = (EditText) findViewById(R.id.cc_mm);
		// cc_yy = (EditText) findViewById(R.id.cc_yy);
		// cc_zip = (EditText) findViewById(R.id.cc_zip);

		first_name.setOnFocusChangeListener(this);
		last_name.setOnFocusChangeListener(this);
		email.setOnFocusChangeListener(this);
		re_enter_email.setOnFocusChangeListener(this);
		phone_number.setOnFocusChangeListener(this);

		next_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// ll_info_form.startAnimation(infoFormLeaveAnimation);
				if (validate(null)) {
					storeInfo();
					Intent intent = new Intent(_context, RegisterCreditCardActivity.class);
					startActivity(intent);
				}
			}

		});
	}

	private void storeInfo() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_FIRST_NAME, first_name.getText().toString());
		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_LAST_NAME, last_name.getText().toString());
		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_EMAIL, email.getText().toString());
		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER, phone_number.getText().toString());
		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_ALREADY_REGISTER, true);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			validate((EditText) v);
		}
	}

	private boolean validate(EditText target) {

		if (target != null) {
			if (target == first_name) {
				return validateFirstName();
			} else if (target == last_name) {
				return validateLastName();
			} else if (target == email) {
				return validateEmail();
			} else if (target == re_enter_email) {
				return validateReEnterEmail();
			} else if (target == phone_number) {
				return validatePhone();
			} else
				return false;
		}
		// validate all if not from afterTextChanged
		else {
			return validateFirstName() && validateLastName() && validateEmail() && validateReEnterEmail() && validatePhone();
		}

	}

	private boolean validateLastName() {
		String name = last_name.getText().toString();
		if (name.length() == 0) {
			last_name.setError(getResources().getString(R.string.empty_last_name));
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

	private boolean validateReEnterEmail() {
		String emailToMatch = email.getText().toString();
		String match = re_enter_email.getText().toString();
		if (!emailToMatch.equals(match)) {
			re_enter_email.setError(getResources().getString(R.string.email_not_matching));
			return false;
		} else {
			return true;
		}
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

	private boolean validateFirstName() {
		String name = first_name.getText().toString();
		if (name.length() == 0) {
			first_name.setError(getResources().getString(R.string.empty_first_name));
			return false;
		} else {
			return true;
		}
	}

}
