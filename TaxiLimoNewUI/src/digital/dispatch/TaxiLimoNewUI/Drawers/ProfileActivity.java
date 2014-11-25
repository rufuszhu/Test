package digital.dispatch.TaxiLimoNewUI.Drawers;

//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import digital.dispatch.TaxiLimoNewUI.R;
//import digital.dispatch.TaxiLimoNewUI.R.id;
//import digital.dispatch.TaxiLimoNewUI.R.layout;
//import digital.dispatch.TaxiLimoNewUI.R.menu;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
//import android.text.TextWatcher;
//import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ProfileActivity extends ActionBarActivity {

	private EditText edtPhone, et_firstName, et_lastName, edtUEmail;
	private LinearLayout save_btn;
	private Context _activity;
	private MenuItem edit_icon;
	private final String TAG = "ProfileActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		edtPhone = (EditText) findViewById(R.id.edt_phoneNum);
		et_firstName = (EditText) findViewById(R.id.et_firstName);
		et_lastName = (EditText) findViewById(R.id.et_lastName);
		edtUEmail = (EditText) findViewById(R.id.edt_userEmail);
		save_btn = (LinearLayout) findViewById(R.id.profile_save_btn);
		_activity = this;
		// name, Phone number and email is not editable at this point
		et_firstName.setFocusable(false);
		et_lastName.setFocusable(false);
		edtPhone.setFocusable(false);
		edtUEmail.setFocusable(false);

		save_btn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (validateName()) {
					// update name
					storeInfo();
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
		if (id == R.id.action_edit) {
			Logger.d(TAG, "onOptionsItemSelected");
			et_firstName.setFocusable(true);
			et_firstName.setFocusableInTouchMode(true);
			et_firstName.setBackground(getResources().getDrawable(R.drawable.shape_textview_border));
			
			et_lastName.setFocusable(true);
			et_lastName.setFocusableInTouchMode(true);
			et_lastName.setBackground(getResources().getDrawable(R.drawable.shape_textview_border));
			save_btn.setVisibility(View.VISIBLE);
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	public void onResume() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_activity);
		if (sharedPreferences != null) {
			String phone = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER);

			edtPhone.setText(phone);

			String userName = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_NAME);


			et_firstName.setText(userName);
			et_lastName.setText(userName);
			String email = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_EMAIL);
			edtUEmail.setText(email);
		}
		super.onResume();
	}

	private boolean validateName() {
		String first_name = et_firstName.getText().toString();
		String last_name = et_lastName.getText().toString();
		if (first_name.length() == 0) {
			et_firstName.setError(getResources().getString(R.string.profile_empty_name));
			return false;
		}
		if (last_name.length() == 0) {
			et_lastName.setError(getResources().getString(R.string.profile_empty_name));
			return false;
		}

		return true;

	}

	private void storeInfo() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((_activity));
		String userName = et_firstName.getText().toString();
	

		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_NAME, userName);
		// SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_EMAIL, edtUEmail.getText().toString());
		// SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER, edtPhone.getText().toString());
		AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
		builder.setMessage(R.string.save_successful).setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				finish();
			}
		});

		builder.show();
	}

}
