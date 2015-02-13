package digital.dispatch.TaxiLimoNewUI.Track;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBCreditCard;
import digital.dispatch.TaxiLimoNewUI.DBCreditCardDao;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.PayByCreditCardTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.PIN;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.UserAccount;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class PayActivity extends BaseActivity {

	private static final String TAG = "PayActivity";
	private static final CharSequence DEFAULT_TOTAL = "0.00";
	private DBCreditCard selectedCard;
	private DaoManager daoManager;
	private DBCreditCardDao creditCardDao;
	private List<DBCreditCard> creditCardList;
	private RadioButton amount_radio;
	private RadioButton percent_radio;
	private EditText et_tip_amount;
	private EditText et_fare_amount;
	private TextView tv_total;
	private TextView pay_btn;
	private TextView cancel_btn;
	private DBBooking dbBook;
	private Context context;
	private String final_tip_amount;
	private BigDecimal tipPercentage;
	private String storePassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);

		context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		storePassword = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_CC_PIN);
		
		
		daoManager = DaoManager.getInstance(this);
		creditCardDao = daoManager.getDBCreditCardDao(DaoManager.TYPE_READ);
		creditCardList = creditCardDao.queryBuilder().list();
		// TODO: add default card
		selectedCard = creditCardList.get(0);
		dbBook = (DBBooking) getIntent().getExtras().get(MBDefinition.DBBOOKING_EXTRA);
		setUpSpinners();

		amount_radio = (RadioButton) findViewById(R.id.amount_radio);
		percent_radio = (RadioButton) findViewById(R.id.percent_radio);
		et_tip_amount = (EditText) findViewById(R.id.et_tip_amount);
		et_fare_amount = (EditText) findViewById(R.id.et_fare_amount);
		tv_total = (TextView) findViewById(R.id.tv_total);

		Logger.v(TAG, "trip fare:" + dbBook.getTaxi_ride_id() + " amount: " + dbBook.getPaidAmount());
		
		//TL-194 driver initiated payment flow payment amount in from notification
		//if the trip has not been paid yet and paid amount has value then display the value as driver request amount
		if(!dbBook.getAlready_paid() && dbBook.getPaidAmount() != null){
			et_fare_amount.setText(dbBook.getPaidAmount());
			
		}
		
		et_tip_amount.setFocusable(false);
		pay_btn = (TextView) findViewById(R.id.pay_btn);
		pay_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String total = tv_total.getText().toString();
				if (total.equals("") || total.equals(DEFAULT_TOTAL)) {
					Utils.showMessageDialog("Cannot pay with 0.00 amount", context);
				} else {
					//if (UserAccount.ccPIN(PayActivity.this) != null && !UserAccount.ccPIN(PayActivity.this).equals("")) {
					if(storePassword!=null && !storePassword.equals("")){	
						showEnterPinDialog();
					} else {
						new PayByCreditCardTask(context, dbBook, tv_total.getText().toString(), selectedCard, final_tip_amount).execute();
					}
				}
			}
		});
		cancel_btn = (TextView) findViewById(R.id.cancel_btn);

		cancel_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(context, TrackDetailActivity.class);
				intent.putExtra(MBDefinition.DBBOOKING_EXTRA, dbBook);
				startActivity(intent);
				finish();
			}
		});

		setupTextLinstner();
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.d(TAG, "on RESUME");
		//show keyboard
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInputFromWindow(et_fare_amount.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
		et_fare_amount.requestFocus();
	}

	@Override
	public void onPause() {
		super.onPause();
		//hide keyboard
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void showEnterPinDialog() {
		final String msg = getResources().getString(R.string.total_amount) + tv_total.getText().toString() + "\n"
				+ getResources().getString(R.string.payment_cc_enter_pin);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final EditText input = (EditText) inflater.inflate(R.layout.layout_pin_edittext, null);

		InputMethodManager imm = (InputMethodManager) getSystemService(PayActivity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		new AlertDialog.Builder(PayActivity.this).setTitle(R.string.cc_pin).setMessage(msg).setView(input).setNegativeButton(R.string.cancel, null)
				.setPositiveButton(R.string.pay_confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//hide keyboard
						InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
						if(storePassword.equals(input.getText().toString())){
							new PayByCreditCardTask(context, dbBook, tv_total.getText().toString(), selectedCard, final_tip_amount).execute();
						} else {
							new AlertDialog.Builder(PayActivity.this).setTitle(R.string.err).setMessage(R.string.pin_not_correct).setCancelable(false)
									.setPositiveButton(R.string.ok, null).show();
						}
						//new CheckPINTask().execute(input.getText().toString());
					}
				}).show();
	}

	private void setupTextLinstner() {
		et_fare_amount.addTextChangedListener(new TextWatcher() {

			private String current = "";
			DecimalFormat dec = new DecimalFormat("0.00");

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!s.toString().equals(current)) {
					double price;
					// get rid of "."
					String cleanString = s.toString().replaceAll("[^\\d]", "");

					try {
						price = Double.parseDouble(cleanString);
					} catch (java.lang.NumberFormatException e) {
						price = 0;
					}

					int shrink = 100;

					// Reformat the number
					String formated = dec.format(price / shrink);

					current = formated;
					et_fare_amount.removeTextChangedListener(this);
					if (formated.length() <= 8) {
						et_fare_amount.setText(formated);
						et_fare_amount.setSelection(formated.length());
					} else {
						et_fare_amount.setText("0.00");
						et_fare_amount.setSelection(4);
					}

					et_fare_amount.addTextChangedListener(this);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setTotalAmount();
			}
		});

		et_tip_amount.addTextChangedListener(new TextWatcher() {

			private String current = "";
			DecimalFormat dec = new DecimalFormat("0.00");

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!s.toString().equals(current)) {
					double price;
					// get rid of "."
					String cleanString = s.toString().replaceAll("[^\\d]", "");
					try {
						price = Double.parseDouble(cleanString);
					} catch (java.lang.NumberFormatException e) {
						price = 0;
					}

					int shrink = 100;

					// Reformat the number
					String formated = dec.format(price / shrink);

					current = formated;
					et_tip_amount.removeTextChangedListener(this);
					if (formated.length() <= 8) {
						et_tip_amount.setText(formated);
						et_tip_amount.setSelection(formated.length());
					} else {
						et_tip_amount.setText("0.00");
						et_tip_amount.setSelection(4);
					}

					et_tip_amount.addTextChangedListener(this);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setTotalAmount();
			}
		});

	}

	private void setTotalAmount() {
		if (Utils.isNumeric(et_fare_amount.getText().toString()) || Utils.isNumeric(et_tip_amount.getText().toString())) {
			BigDecimal enteredFare;
			if (et_fare_amount.getText() == null || et_fare_amount.getText().toString().equalsIgnoreCase(""))
				enteredFare = new BigDecimal("0.00");
			else
				enteredFare = new BigDecimal(et_fare_amount.getText().toString());

			BigDecimal totalFare;

			if (percent_radio.isChecked()) {
				Logger.d(TAG, "enteredFare: " + enteredFare + "tipPercentage: " + tipPercentage);
				totalFare = enteredFare.add(enteredFare.multiply(tipPercentage));
				final_tip_amount = enteredFare.multiply(tipPercentage).setScale(2, RoundingMode.CEILING).toString();
			} else if (amount_radio.isChecked()) {
				if (Utils.isNumeric(et_tip_amount.getText().toString())) {
					BigDecimal enteredTip = new BigDecimal(et_tip_amount.getText().toString());
					totalFare = enteredFare.add(enteredTip);
					final_tip_amount = et_tip_amount.getText().toString();
				} else {
					totalFare = enteredFare;
					final_tip_amount = "";
				}
			} else {
				totalFare = BigDecimal.valueOf(0.00);
				final_tip_amount = "";
			}

			tv_total.setText(totalFare.setScale(2, RoundingMode.CEILING).toString());
		} else {
			tv_total.setText(DEFAULT_TOTAL);
		}
	}

	@Override
	public void onBackPressed() {
		Logger.e(TAG, "onBackPressed Called");
		Intent intent = new Intent(context, TrackDetailActivity.class);
		intent.putExtra(MBDefinition.DBBOOKING_EXTRA, dbBook);
		startActivity(intent);
		finish();
	}

	private void setUpSpinners() {
		Spinner tip_spinner = (Spinner) findViewById(R.id.tips_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tips_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tip_spinner.setAdapter(adapter);

		tip_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, android.view.View arg1, int arg2, long arg3) {
				String[] percentList = getResources().getStringArray(R.array.tips_array);

				tipPercentage = new BigDecimal(Integer.parseInt(percentList[arg2].substring(0, percentList[arg2].length() - 1))).divide(BigDecimal.valueOf(100));

				setTotalAmount();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		// TODO: remember tip amount of last payment and apply it here
		tip_spinner.setSelection(4);

		String[] cardArr = createCCStrArr(creditCardList);
		Spinner credit_card_spinner = (Spinner) findViewById(R.id.credit_card_spinner);
		ArrayAdapter<String> cc_spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cardArr);
		cc_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		credit_card_spinner.setAdapter(cc_spinner_adapter);

		credit_card_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, android.view.View arg1, int arg2, long arg3) {
				selectedCard = creditCardList.get(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	private String[] createCCStrArr(List<DBCreditCard> ccList) {
		String[] arr = new String[ccList.size()];
		for (int i = 0; i < ccList.size(); i++) {
			DBCreditCard cc = ccList.get(i);
			arr[i] = cc.getNickName() + " *" + cc.getLast4CardNum();
		}
		return arr;
	}

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch (view.getId()) {
		case R.id.amount_radio:
			if (checked) {
				percent_radio.setChecked(false);
				et_tip_amount.setFocusableInTouchMode(true);
				et_tip_amount.setFocusable(true);
				et_tip_amount.setTextColor(context.getResources().getColor(R.color.black));

				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.toggleSoftInputFromWindow(et_tip_amount.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
				et_tip_amount.requestFocus();
			}
			break;
		case R.id.percent_radio:
			if (checked) {
				amount_radio.setChecked(false);
				et_tip_amount.setFocusable(false);
				et_tip_amount.setText("0.00");
				et_tip_amount.setTextColor(context.getResources().getColor(R.color.gray_line));
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == android.R.id.home) {
			Intent intent = new Intent(context, TrackDetailActivity.class);
			intent.putExtra(MBDefinition.DBBOOKING_EXTRA, dbBook);
			startActivity(intent);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public void showPaySuccessDialog(String msg) {
		new AlertDialog.Builder(context).setTitle(R.string.payment_approve).setMessage(msg).setCancelable(false)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbBook.setAlready_paid(true);
						DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_READ);
						bookingDao.update(dbBook);
						Intent intent = new Intent(context, TrackDetailActivity.class);
						intent.putExtra(MBDefinition.DBBOOKING_EXTRA, dbBook);
						startActivity(intent);
						finish();
					}
				}).show();

	}

	private class CheckPINTask extends AsyncTask<String, Integer, Boolean> {
		// The code to be executed in a background thread.
		@Override
		protected Boolean doInBackground(String... params) {
			boolean isPass = false;

			try {
				isPass = PIN.check(params[0], UserAccount.ccPIN(PayActivity.this));
			} catch (Exception e) {
				Logger.e(TAG, e.toString());
				e.printStackTrace();
			}

			return isPass;
		}

		@Override
		protected void onPostExecute(Boolean isPass) {
			if (isPass) {
				//hide keyboard
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				new PayByCreditCardTask(context, dbBook, tv_total.getText().toString(), selectedCard, final_tip_amount).execute();
			} else {
				new AlertDialog.Builder(PayActivity.this).setTitle(R.string.err).setMessage(R.string.pin_not_correct).setCancelable(false)
						.setPositiveButton(R.string.ok, null).show();
			}
		}
	}
}
