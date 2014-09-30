package digital.dispatch.TaxiLimoNewUI;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.PayByCreditCardTask;
import digital.dispatch.TaxiLimoNewUI.Track.TrackDetailActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
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

public class PayActivity extends Activity {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		context = this;
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

		et_tip_amount.setFocusable(false);
		pay_btn = (TextView) findViewById(R.id.pay_btn);
		pay_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new PayByCreditCardTask(context, dbBook, tv_total.getText().toString(), selectedCard, final_tip_amount).execute();
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
				Logger.e(TAG, "enteredFare: " + enteredFare + "tipPercentage: " + tipPercentage);
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
				Logger.e(TAG, "amount_radio clicked");
				percent_radio.setChecked(false);
				et_tip_amount.setFocusableInTouchMode(true);
				et_tip_amount.setFocusable(true);
				et_tip_amount.setTextColor(context.getResources().getColor(R.color.black));
			}
			break;
		case R.id.percent_radio:
			if (checked) {
				Logger.e(TAG, "percent_radio clicked");
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pay, menu);
		return true;
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
		new AlertDialog.Builder(context).setTitle(R.string.payment_approve).setMessage(msg).setCancelable(false).setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
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
}