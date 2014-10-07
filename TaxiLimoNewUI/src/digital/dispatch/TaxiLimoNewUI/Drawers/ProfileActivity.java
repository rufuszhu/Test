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
import android.support.v7.app.ActionBarActivity;
//import android.text.TextWatcher;
//import android.app.AlertDialog;
import android.content.Context;
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
	
	private EditText edtPhone, edtUName, edtUEmail;
	private LinearLayout save_btn;
	private Context _activity;
	private MenuItem edit_icon;
	private final String TAG = "ProfileActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		edtPhone = (EditText)findViewById(R.id.edt_phoneNum);
        edtUName = (EditText)findViewById(R.id.edt_userName);
        edtUEmail = (EditText)findViewById(R.id.edt_userEmail);
        save_btn = (LinearLayout) findViewById(R.id.profile_save_btn);
		_activity = this;
		//name, Phone number and email is not editable at this point
		edtUName.setFocusable(false);
		edtUName.setTextColor(getResources().getColor(R.color.gray_light));
		
		edtPhone.setFocusable(false);
		edtPhone.setTextColor(getResources().getColor(R.color.gray_light));
		
		edtUEmail.setFocusable(false);
		edtUEmail.setTextColor(getResources().getColor(R.color.gray_light));
		
		
		save_btn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(validateName()){
					//update name
					storeInfo();
					
				}
				
			}
			
		});
		
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		//edit_icon = menu.findItem(R.id.action_edit);
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
			edtUName.setFocusable(true);
			edtUName.setFocusableInTouchMode(true);
			
			edtUName.setTextColor(getResources().getColor(R.color.black));
			return true;
		}
		return super.onOptionsItemSelected(item);
		
		
	}
	
	
	@Override
    public void onResume() { 
		
	     
	
	    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_activity);
	    if(sharedPreferences != null){
			String phone = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER);
			
			edtPhone.setText(phone);
			
			String firstName = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_FIRST_NAME);
			String lastName = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_LAST_NAME);
	
			
			edtUName.setText(firstName + " " + lastName);
			
			String email = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_EMAIL);
			edtUEmail.setText(email);
			
			
	    }
    	super.onResume();
    }
    
	/*
	private boolean validateEmail() {
		boolean isValid = true;

		if (edtUEmail.getText().toString().equalsIgnoreCase("")) {
			edtUEmail.setError(getResources().getString(R.string.profile_empty_email));
			isValid = false;
		} else {
			// check if the entered email is a valid pattern
			Pattern ePattern = Pattern.compile(MBDefinition.EMAIL_PATTERN);
			Matcher matcher = ePattern.matcher(edtUEmail.getText().toString());

			if (matcher.matches()) {
				edtUEmail.setError(null);
			} else {
				edtUEmail.setError(getResources().getString(R.string.profile_email_invalid));
				isValid = false;
			}
		}
		return isValid;
	}
	
	private boolean validatePhone() {
		String name = edtPhone.getText().toString();
		if (name.length() == 0) {
			edtPhone.setError(getResources().getString(R.string.empty_phone_number));
			return false;
		} else if (name.length() > 13) {
			edtPhone.setError(getResources().getString(R.string.phone_number_too_long));
			return false;
		} else
			return true;
	}
	*/
	private boolean validateName() {
		String name = edtUName.getText().toString();
		if (name.length() == 0) {
			edtUName.setError(getResources().getString(R.string.profile_empty_name));
			return false;
		} else {
			return true;
		}
	}
	
	private void storeInfo() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences((_activity));
		String name = edtUName.getText().toString();
		
		String[] names = name.split(" ");
		//assume that last name is the name after the last white space and the rest of it are first name
		String lastName = names[names.length-1];
		
		String firstName = name.substring(0, (name.length() - lastName.length() -1));
		
		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_FIRST_NAME, firstName);
		SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_LAST_NAME, lastName);
		//SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_EMAIL, edtUEmail.getText().toString());
		//SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER, edtPhone.getText().toString());
		edtUName.setFocusable(false);
		edtUName.setTextColor(getResources().getColor(R.color.gray_light));
	
	}
	
}
