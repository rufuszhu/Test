package digital.dispatch.TaxiLimoNewUI;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digital.dispatch.TaxiLimoSoap.requests.KeyExchangeRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.KeyExchangeRequest.IKeyExchangeResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.requests.TokenizationRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.TokenizationRequest.ITokenizationResponseListener;
import com.digital.dispatch.TaxiLimoSoap.responses.KeyExchangeResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.TokenizationResponse;

import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Track.PayActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.PIN;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition.ccRequestType;
import digital.dispatch.TaxiLimoNewUI.Utils.UserAccount;

public class RegisterCreditCardActivity extends BaseActivity implements TextWatcher, OnFocusChangeListener {

	private EditText cc_number, cc_holder_name, cc_mm, cc_yy, cc_zip, cc_nick_name, enter_pin, re_enter_pin;
	private TextView next_btn_pay, skip_btn, next_btn_pin;
	private final String prime = "11684510167982206765990851851544155388252758257075719142008344429621708996"
			+ "506416490173644860944227986735266303324043629151480087265000441195390936848807913";
	private final String base = "5";
	private final String CARD_INFO_SEPARATOR = " ";
	private final String CARD_INFO_PADDING_CHAR = "0";
	private final int ZIP_PADDING_LENGTH = 32;
	private final int CARD_NUM_PADDING_LENGTH = 20;
	private final int CARD_INFO_PADDING_LENGTH = 32;
	private final int PUBLIC_KEY_LENGTH = 64;
	private final int MAX_SECRET_KEY_LENGTH = 64;
	private final int MIN_SECRET_KEY_LENGTH = 24;
	private boolean isAddingSpace = false;
	private byte[] tripleDESKey = null;
	private String keyTimeStamp, ccToken;
	private CheckBox check_box;
	private boolean notInScreen = false; // check if this activity screen still live

	private DaoManager daoManager;
	private DBCreditCardDao creditCardDao;
	private Context context;

	private LinearLayout ll_enter_pin, ll_payment_form, ll_pay_btns;

	// data format {total_length, insert_space_number, first_space_position, second...}
	private static final int[][] CARD_FORMAT = { { 16, 3, 4, 8, 12, 0, 0 }, // VISA
			{ 16, 3, 4, 8, 12, 0, 0 }, // Master (MC)
			{ 15, 2, 4, 10, 0, 0, 0 }, // American Express (AMEX)
			{ 14, 2, 4, 10, 0, 0, 0 }, // Diner Club (DCI)
			{ 16, 3, 4, 8, 12, 0, 0 }, // Discover
			{ 16, 3, 4, 8, 12, 0, 0 }, // JCB
	};

	private static final String[] CARD_TYPE_PATTERN = { "^4[0-9]{3}", // VISA
			"^5[1-5][0-9]{2}", // Master
			"^3[47][0-9]{2}", // American Express
			"^3(?:0[0-5]|[68][0-9])[0-9]", // Diner club
			"^6(?:011|5[0-9]{2})[0-9]", // Discover
			"^(?:2131|1800|35[0-9]{2})" // JCB
	};

	private static final String CARD_NUM_SEPARATOR = " ";
	private static final String TAG = "RegisterCreditCardActivity";
	private static final int MAX_CARD_NUM_LEN = 19;
	private static final int MIN_CARD_NUM_LEN = 13;
	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	private boolean isSaving = false;
	private boolean isEdit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_credit_card);

		context = this;
		findView();
		ll_payment_form.setVisibility(View.VISIBLE);
		ll_pay_btns.setVisibility(View.VISIBLE);
		ll_enter_pin.setVisibility(View.GONE);
		next_btn_pin.setVisibility(View.GONE);

	}

	private void findView() {
		ll_enter_pin = (LinearLayout) findViewById(R.id.ll_enter_pin);
		ll_payment_form = (LinearLayout) findViewById(R.id.ll_payment_form);
		ll_pay_btns = (LinearLayout) findViewById(R.id.ll_pay_btns);

		cc_number = (EditText) findViewById(R.id.cc_number);
		cc_holder_name = (EditText) findViewById(R.id.cc_holder_name);
		cc_mm = (EditText) findViewById(R.id.cc_mm);
		cc_yy = (EditText) findViewById(R.id.cc_yy);
		cc_zip = (EditText) findViewById(R.id.cc_zip);
		cc_nick_name = (EditText) findViewById(R.id.cc_nick_name);
		next_btn_pay = (TextView) findViewById(R.id.next_btn_pay);
		skip_btn = (TextView) findViewById(R.id.skip_btn);
		next_btn_pin = (TextView) findViewById(R.id.next_btn_pin);

		check_box = (CheckBox) findViewById(R.id.check_box);
		enter_pin = (EditText) findViewById(R.id.enter_pin);
		re_enter_pin = (EditText) findViewById(R.id.re_enter_pin);

		next_btn_pay.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				addCreditCard();
			}
		});

		skip_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO:go to eula, but for demo we just go to main activity
				showWelcomeDialog();

			}
		});

		next_btn_pin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO:go to eula, but for demo we just go to main activity
				if (!check_box.isChecked()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage(R.string.pin_warning).setTitle(R.string.warning)
							.setPositiveButton(R.string.string_continue, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									showWelcomeDialog();
								}
							}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.dismiss();
								}
							});

					builder.show();
				} else if (validatePin() && validateReEnterPin()) {
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
					SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_CC_PIN, enter_pin.getText().toString());
					showWelcomeDialog();
					//new SavePINTask().execute(enter_pin.getText().toString(), "");
				}
			}
		});

		check_box.setChecked(true);

		check_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked) {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage(R.string.pin_warning).setTitle(R.string.warning).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							disalbePinEditText();
						}
					}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							check_box.setChecked(true);
						}
					});

					builder.show();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle(R.string.important).setMessage(R.string.pin_cannot_reset).setCancelable(true);
					builder.show();
					enablePinEditText();
				}
			}
		});

	}

	private void disalbePinEditText() {
		enter_pin.setFocusable(false);
		re_enter_pin.setFocusable(false);
		enter_pin.setText("");
		re_enter_pin.setText("");
	}

	private void enablePinEditText() {
		enter_pin.setFocusableInTouchMode(true);
		enter_pin.setFocusable(true);
		re_enter_pin.setFocusableInTouchMode(true);
		re_enter_pin.setFocusable(true);
	}

	@Override
	public void onResume() {
		cc_number.setOnFocusChangeListener(this);
		cc_holder_name.setOnFocusChangeListener(this);
		cc_mm.setOnFocusChangeListener(this);
		cc_yy.setOnFocusChangeListener(this);
		cc_zip.setOnFocusChangeListener(this);
		enter_pin.setOnFocusChangeListener(this);
		re_enter_pin.setOnFocusChangeListener(this);

		cc_number.addTextChangedListener(this);

		daoManager = DaoManager.getInstance(this);
		creditCardDao = daoManager.getDBCreditCardDao(DaoManager.TYPE_WRITE);
		super.onResume();
	}

	@Override
	public void onDestroy() {
		notInScreen = true; // don't display anything anymore
		super.onDestroy();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		EditText target = (EditText) v;
		if (!hasFocus) {

			if (target == enter_pin) {
				validatePin();
			} else if (target == re_enter_pin) {
				validateReEnterPin();
			} else {
				validate((EditText) v);
			}
		}
	}

	private boolean validatePin() {
		if (enter_pin.getText().toString().length() == 0) {
			enter_pin.setError(context.getString(R.string.empty_pin));
			return false;
		} else if (enter_pin.getText().toString().length() == 4) {
			return true;
		} else {
			enter_pin.setError(context.getString(R.string.invalid_pin_length));
			return false;
		}
	}

	private boolean validateReEnterPin() {
		if (!re_enter_pin.getText().toString().equals(enter_pin.getText().toString())) {
			re_enter_pin.setError(context.getString(R.string.pin_not_matching));
			return false;
		}
		return true;
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		if (arg0 == cc_number.getEditableText()) {
			// set space for card #
			if (!isAddingSpace) {
				if (arg0.toString().length() > 4) {
					for (int i = 0; i < CARD_TYPE_PATTERN.length; i++) {
						Pattern cPattern = Pattern.compile(CARD_TYPE_PATTERN[i]);
						Matcher matcher;
						/*
						 * - MC - VISA - AMEX - DCI - DISCOVER - JCB
						 */

						if (i == 4 && arg0.toString().length() > 5) {
							matcher = cPattern.matcher(arg0.toString().replace(CARD_NUM_SEPARATOR, "").substring(0, 5));
						} else {
							matcher = cPattern.matcher(arg0.toString().substring(0, 4));
						}

						if (matcher.matches()) {
							String noSpaceCardNum = arg0.toString().replace(CARD_NUM_SEPARATOR, "");
							String tempCardNum = noSpaceCardNum;

							for (int j = 2; j < 2 + CARD_FORMAT[i][1]; j++) {
								if (noSpaceCardNum.length() > CARD_FORMAT[i][j]) {
									int occurrenceCount = tempCardNum.toString().length() - tempCardNum.toString().replace(CARD_NUM_SEPARATOR, "").length();
									tempCardNum = tempCardNum.substring(0, CARD_FORMAT[i][j] + occurrenceCount) + CARD_NUM_SEPARATOR
											+ tempCardNum.substring(CARD_FORMAT[i][j] + occurrenceCount);
								}
							}

							isAddingSpace = true;
							cc_number.setText(tempCardNum);
							cc_number.setSelection(cc_number.length());

							break;
						}
					}
				}
			} else {
				isAddingSpace = false;
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	private boolean validate(EditText target) {
		boolean isValid = true;

		if (target != null) {
			if (target == cc_number) {
				return validateCardNum();
			} else if (target == cc_holder_name) {
				return validateHolderName();
			} else if (target == cc_mm) {
				return validateExMonth();
			} else if (target == cc_yy) {
				return validateExYear();
			} else if (target == cc_zip) {
				return validateAddress();
			} else {
				Logger.e(TAG, "target doesn't fit any category");
			}
		}
		// validate all if not from afterTextChanged
		else {
			if (validateExMonth() && validateExYear()) {
				if (!validateExpiry()) {
					isValid = false;
				}
			} else {
				isValid = false;
			}

			if (!validateCardNum()) {
				isValid = false;
			}
			if (!validateHolderName()) {
				isValid = false;
			}
			if (!validateAddress()) {
				isValid = false;
			}
		}

		return isValid;
	}

	// Card Number
	private boolean validateCardNum() {
		boolean isValid = true;
		String editCardNum = cc_number.getText().toString().trim();
		int cardNumLen = editCardNum.length();

		if (cardNumLen < MIN_CARD_NUM_LEN) {
			cc_number.setError(getResources().getString(R.string.credit_card_ccnum_too_short));
			isValid = false;
		} else if (cardNumLen > MAX_CARD_NUM_LEN) {
			cc_number.setError(getResources().getString(R.string.credit_card_ccnum_too_long));
			isValid = false;
		} else {
			cc_number.setError(null);
		}
		return isValid;
	}

	// Card Holder Name
	private boolean validateHolderName() {
		boolean isValid = true;

		if (cc_holder_name.getText().toString().equalsIgnoreCase("")) {
			cc_holder_name.setError(getResources().getString(R.string.credit_card_empty_holder_name));
			isValid = false;
		} else {
			cc_holder_name.setError(null);
		}

		return isValid;
	}

	// Expiry Month
	private boolean validateExMonth() {
		boolean isValid = true;

		if (cc_mm.getText().toString().equalsIgnoreCase("")) {
			cc_mm.setError(getResources().getString(R.string.credit_card_month_invalid));
			isValid = false;
		} else if (cc_mm.getText().toString().length() != 2) {
			cc_mm.setError(getResources().getString(R.string.credit_card_month_too_short));
			isValid = false;
		} else {
			int exMonth = Integer.parseInt(cc_mm.getText().toString());

			if (exMonth <= 12 && exMonth >= 1) {
				cc_mm.setError(null);
			} else {
				cc_mm.setError(getResources().getString(R.string.credit_card_month_invalid));
				isValid = false;
			}
		}

		return isValid;
	}

	// Expiry Year
	private boolean validateExYear() {
		boolean isValid = true;

		if (cc_yy.getText().toString().equalsIgnoreCase("")) {
			cc_yy.setError(getResources().getString(R.string.credit_card_year_invalid));
			isValid = false;
		} else if (cc_yy.getText().toString().length() != 2) {
			cc_yy.setError(getResources().getString(R.string.credit_card_year_too_short));
			isValid = false;
		} else {
			int exYear = Integer.parseInt(cc_yy.getText().toString());
			int curYear = Integer.parseInt(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2));

			// compare if the entered year is greater or equal to current year
			if (exYear >= curYear) {
				cc_yy.setError(null);
			} else {
				cc_yy.setError(getResources().getString(R.string.credit_card_year_invalid));
				isValid = false;
			}
		}

		return isValid;
	}

	// Expiry as a whole
	private boolean validateExpiry() {
		boolean isValid = true;
		int curYear = Calendar.getInstance().get(Calendar.YEAR);
		int curDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int exYear = Integer.parseInt(cc_yy.getText().toString()) + curYear - (curYear % 100);
		int exMonth = Integer.parseInt(cc_mm.getText().toString()) - 1;

		// set the expiring date to year and month on the card with today's day
		Calendar exCal = Calendar.getInstance();
		exCal.set(exYear, exMonth, curDay);

		if (exCal.compareTo(Calendar.getInstance()) >= 0) {
			cc_yy.setError(null);
		} else {
			cc_mm.setError(getResources().getString(R.string.credit_card_expired_date));
			isValid = false;
		}

		return isValid;
	}

	// Billing Address
	private boolean validateAddress() {
		boolean isValid = true;

		if (cc_zip.getText().toString().equalsIgnoreCase("")) {
			cc_zip.setError(getResources().getString(R.string.credit_card_empty_zip));
			isValid = false;
		} else {
			cc_zip.setError(null);
		}
		return isValid;
	}

	private void addCreditCard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		if (getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}

		if (validate(null)) {
			if (tripleDESKey != null) {
				new RegisterCreditCardTask().execute(ccRequestType.AddCard.toString());
			} else {
				new GetKeyTask(true).execute(ccRequestType.AddCard.toString());
			}

		}
	}

	private class GetKeyTask extends AsyncTask<String, Integer, Void> implements IKeyExchangeResponseListener, IRequestTimerListener {
		KeyExchangeRequest kExReq;
		PrivateKey priKey;
		BigInteger p;
		boolean reqTokenAfter = false;
		String ccOpType;
		Dialog progressDialog;

		public GetKeyTask(boolean registerCreditCardAfter) {
			reqTokenAfter = registerCreditCardAfter;
		}

		// Before running code in separate thread
		@Override
		protected void onPreExecute() {
			progressDialog = new Dialog(context);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			Drawable d = new ColorDrawable(Color.BLACK);
			d.setAlpha(0);
			progressDialog.getWindow().setBackgroundDrawable(d);
			progressDialog.setContentView(R.layout.custom_dialog);
			// This dialog can't be cancelled by pressing the back key
			progressDialog.setCancelable(false);
			DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					dialog.dismiss();

					if (kExReq != null) {
						kExReq.cancelProgress();
					}
				}
			};
			progressDialog.setOnCancelListener(cancelListener);
			progressDialog.setCanceledOnTouchOutside(true);
			if (reqTokenAfter) {
				progressDialog.show();
			}
		}

		// The code to be executed in a background thread.
		@Override
		protected Void doInBackground(String... params) {
			try {
				if (reqTokenAfter) {
					ccOpType = params[0];
				}

				kExReq = new KeyExchangeRequest(this, this);
				kExReq.setDeviceID(Utils.getHardWareId(context));

				PublicKey pubKey;
				byte[] tmpMyPublicKeyBytes = null;
				BigInteger g = new BigInteger(base);
				p = new BigInteger(prime);
				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
				DHParameterSpec dhSpec = new DHParameterSpec(p, g, 31);
				keyGen.initialize(dhSpec);

				// Try to get a public key with 64 bytes
				do {
					KeyPair keypair = keyGen.generateKeyPair();
					pubKey = keypair.getPublic();
					priKey = keypair.getPrivate();
					tmpMyPublicKeyBytes = ((DHPublicKey) pubKey).getY().toByteArray();
				} while (tmpMyPublicKeyBytes.length != PUBLIC_KEY_LENGTH || tmpMyPublicKeyBytes[0] == 0);

				kExReq.setDHKey(byteToHex(tmpMyPublicKeyBytes));
				kExReq.sendRequest(getResources().getString(R.string.name_space), getResources().getString(R.string.url));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// set the current progress of the progress dialog
			// progressDialog.setProgress(values[0]);
		}

		@Override
		public void onResponseReady(KeyExchangeResponse response) {
			progressDialog.dismiss();
			String payload = response.GetPayload();
			String[] payloadInfo = payload.split(";");

			byte[] bytes = hexToByte(payloadInfo[1]);
			BigInteger serverPublicKey = new BigInteger(1, bytes);
			BigInteger sharedSecretKey = serverPublicKey.modPow(((DHPrivateKey) priKey).getX(), p);
			byte[] secretKeyBytes = sharedSecretKey.toByteArray();

			// Get rid of the first bytes of '0'which is the sign of the BigInteger
			if (secretKeyBytes[0] == 0) {
				byte finalserectKeyBytes[] = new byte[secretKeyBytes.length - 1];
				System.arraycopy(secretKeyBytes, 1, finalserectKeyBytes, 0, secretKeyBytes.length - 1);
				secretKeyBytes = finalserectKeyBytes;
			}

			// Secret key should not longer than 64 bytes
			// We need secret key not shorter than 24 bytes so we can get
			// a DES key from it
			if (secretKeyBytes.length > MAX_SECRET_KEY_LENGTH || secretKeyBytes.length < MIN_SECRET_KEY_LENGTH) {
				Logger.e(TAG, "secret key not right length");
			} else {
				// get first 24 byte because it's key for 3DES
				byte keyExchangeDESKey[] = new byte[MIN_SECRET_KEY_LENGTH];
				for (int i = 0; i < keyExchangeDESKey.length; i++) {
					keyExchangeDESKey[i] = secretKeyBytes[i];
				}

				try {
					String decMsg = decrypt3DES(keyExchangeDESKey, payloadInfo[2]);

					Logger.v(TAG, "Decrypted 3DES Key: " + decMsg);

					tripleDESKey = hexToByte(decMsg);
					keyTimeStamp = payloadInfo[3];

					if (tripleDESKey != null && (reqTokenAfter && !notInScreen)) {
						new RegisterCreditCardTask().execute(ccOpType);
					}
				} catch (Exception e) {
					Logger.e(TAG, e.toString());
					e.printStackTrace();
				}
			}
		}

		// status code 2~6 will be in here as well
		@Override
		public void onErrorResponse(String errorString) {
			progressDialog.dismiss();
			if (!notInScreen) {
				new AlertDialog.Builder(context).setTitle(R.string.err_error_response).setMessage(R.string.err_msg_no_response)
						.setPositiveButton(R.string.ok, null).show();
			}

			Logger.v(TAG, "error response: " + errorString);
		}

		@Override
		public void onError() {
			progressDialog.dismiss();
			if (!notInScreen) {
				new AlertDialog.Builder(context).setTitle(R.string.err_no_response_error).setMessage(R.string.err_msg_no_response)
						.setPositiveButton(R.string.ok, null).show();
			}

			Logger.v(TAG, "no response");
		}

		@Override
		public void onProgressUpdate(int progress) {
			publishProgress(progress);

			if (progress >= 100) {
				progressDialog.dismiss();
			}
		}
	}

	private class RegisterCreditCardTask extends AsyncTask<String, Integer, Void> implements ITokenizationResponseListener, IRequestTimerListener {
		TokenizationRequest tokenReq;
		Dialog progressDialog;

		// Before running code in separate thread
		@Override
		protected void onPreExecute() {
			progressDialog = new Dialog(context);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			Drawable d = new ColorDrawable(Color.BLACK);
			d.setAlpha(0);
			progressDialog.getWindow().setBackgroundDrawable(d);
			progressDialog.setContentView(R.layout.custom_dialog);
			// This dialog can't be cancelled by pressing the back key
			progressDialog.setCancelable(false);
			DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					dialog.dismiss();

					if (tokenReq != null) {
						tokenReq.cancelProgress();
					}
				}
			};
			progressDialog.setOnCancelListener(cancelListener);
			progressDialog.setCanceledOnTouchOutside(true);
			progressDialog.show();
		}

		// The code to be executed in a background thread.
		@Override
		protected Void doInBackground(String... params) {
			try {
				Integer sequenceNum = Integer.valueOf((int) (Math.random() * (MBDefinition.MDT_MAX_SEQUENCE_NUM + 1)));

				tokenReq = new TokenizationRequest(this, this);
				tokenReq.setDeviceID(Utils.getHardWareId(context));
				tokenReq.setSequenceNum(sequenceNum.toString());
				tokenReq.setReqType(params[0]);

				String cardNumHex = stringAsHex(cc_number.getText().toString().replace(CARD_NUM_SEPARATOR, ""));
				String expiryHex = stringAsHex(cc_mm.getText().toString() + cc_yy.getText().toString());

				for (int i = cardNumHex.length(); i < CARD_NUM_PADDING_LENGTH * 2; i++) {
					cardNumHex += CARD_INFO_PADDING_CHAR;
				}

				String cardInfo = cardNumHex + stringAsHex(CARD_INFO_SEPARATOR) + expiryHex;

				for (int i = cardInfo.length(); i < CARD_INFO_PADDING_LENGTH * 2; i++) {
					cardInfo += CARD_INFO_PADDING_CHAR;
				}

				String encryptedCardInfo = encrypt3DES(cardInfo);

				// Add Card
				if (params[0].equalsIgnoreCase(ccRequestType.AddCard.toString())) {
					String zipHex = stringAsHex(cc_zip.getText().toString());

					for (int i = zipHex.length(); i < ZIP_PADDING_LENGTH; i++) {
						zipHex += CARD_INFO_PADDING_CHAR;
					}

					tokenReq.setDESTimeStamp(keyTimeStamp);
					tokenReq.setCardNumAndExDate(encryptedCardInfo);
					tokenReq.setCardHolderName(cc_holder_name.getText().toString());
					tokenReq.setZip(encrypt3DES(zipHex));
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
					String email = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_EMAIL);
					tokenReq.setEmail(email);
				}
				// Edit Card
				else if (params[0].equalsIgnoreCase(ccRequestType.EditCard.toString())) {
					tokenReq.setToken(ccToken);
				} else {
					Logger.v(TAG, "some unrecognized request type");
				}

				tokenReq.sendRequest(getResources().getString(R.string.name_space), getResources().getString(R.string.url));
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// set the current progress of the progress dialog
			// progressDialog.setProgress(values[0]);
		}

		// Only Approved, resCode = 1 will be here
		@Override
		public void onResponseReady(TokenizationResponse response) {
			progressDialog.dismiss();

			String cardNum = response.GetLast4Digit();
			String token = response.GetToken();
			String brand = response.GetCardBrand();
			// MBCreditCard card = new MBCreditCard(cardNum, cc_holder_name.getText().toString(), cc_mm.getText().toString() + "/" + cc_yy.getText().toString(),
			// cc_zip.getText()
			// .toString(), edtEmail.getText().toString(), cc_nick_name.getText().toString(), token, brand, cc_number.getText().toString().substring(0, 4));
			DBCreditCard card = new DBCreditCard();
			card.setCardBrand(brand);
			card.setExpiryMonth(cc_mm.getText().toString());
			card.setExpiryYear(cc_yy.getText().toString());
			card.setFirst4CardNum(cc_number.getText().toString().substring(0, 4));
			card.setHolderName(cc_holder_name.getText().toString());
			card.setLast4CardNum(cardNum);
			card.setNickName(cc_nick_name.getText().toString());
			card.setToken(token);
			card.setZip(cc_zip.getText().toString());
			creditCardDao.insert(card);

			Logger.v(TAG, "Done cc - Approve");

			new AlertDialog.Builder(context).setTitle(R.string.register_success).setMessage(R.string.credit_card_registration_approved)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ll_payment_form.setVisibility(View.GONE);
							ll_pay_btns.setVisibility(View.GONE);
							ll_enter_pin.setVisibility(View.VISIBLE);
							next_btn_pin.setVisibility(View.VISIBLE);
						}

					}).show();
		}

		@Override
		public void onErrorResponse(TokenizationResponse res) {
			progressDialog.dismiss();
			int errCode = res.GetResponseCode();

			if (!notInScreen) {
				// Default generic error, unknown error – retry may succeed
				if (errCode == 0 || errCode == 7 || errCode == 25) {
					notApproveAlert(R.string.credit_card_err_unknown);
				}
				// Duplicate transaction detected
				else if (errCode == 12 || errCode == 23) {
					notApproveAlert(R.string.credit_card_err_duplicate);
				}
				// Invalid Key or key too old, run keyExchange again, only happen in AddCard since EditCard don't need Key
				else if (errCode == 14) {
					new GetKeyTask(true).execute(ccRequestType.AddCard.toString());
				}
				// This should not happen since we restrict card to max = 3 at client side and re-install will have new hardward ID
				else if (errCode == 18) {
					Logger.e(TAG, "max number of cards reached - THIS SHOULD NEVER HAPPENED!!");
				}
				// No such card
				else if (errCode == 19) {
					notApproveAlert(R.string.credit_card_err_no_such_card);
				}
				// Connection problem/time out (PG, Global, Moneris etc.)
				else if (errCode == 20 || errCode == 21 || errCode == 28) {
					notApproveAlert(R.string.credit_card_err_no_connect_processor);
				}
				// Declined due to fraud alert
				else if (errCode == 24) {
					notApproveAlert(R.string.credit_card_err_fraud_alert);
				}
				// Problem with server/merchant configuration (Merchant account problem, setup problem)
				else if (errCode == 22 || errCode == 26 || errCode == 27) {
					notApproveAlert(R.string.credit_card_err_merchant_account);
				}
				// Card is invalid
				else if (errCode == 29) {
					notApproveAlert(R.string.credit_card_err_invalid_card);
				}
				// Card not authorized
				else if (errCode == 30) {
					notApproveAlert(R.string.credit_card_err_not_authorized);
				} else if (errCode == 32) {
					notApproveAlert(R.string.credit_card_err_incorrect_avs);
				}
			}
		}

		@Override
		public void onError() {
			progressDialog.dismiss();
			if (!notInScreen) {
				new AlertDialog.Builder(context).setTitle(R.string.err_no_response_error).setMessage(R.string.err_msg_no_response)
						.setPositiveButton(R.string.ok, null).show();
			}

			Logger.v(TAG, "no response");
		}

		@Override
		public void onProgressUpdate(int progress) {
			publishProgress(progress);

			if (progress >= 100) {
				progressDialog.dismiss();
			}
		}
	}

	private void notApproveAlert(int msgID) {
		new AlertDialog.Builder(context).setTitle(R.string.register_unsuccess).setMessage(msgID).setCancelable(false).setPositiveButton(R.string.ok, null)
				.show();
	}

	public String encrypt3DES(String input) throws Exception {
		if (tripleDESKey != null) {
			Cipher nCipher = Cipher.getInstance("DESede/ECB/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(tripleDESKey, "DESede");
			nCipher.init(Cipher.ENCRYPT_MODE, keySpec);

			byte[] cipherbyte = nCipher.doFinal(hexToByte(input));

			return byteToHex(cipherbyte);
		} else {
			return null;
		}
	}

	public String decrypt3DES(byte[] key, String input) throws Exception {
		byte[] keyInByte = new byte[24];
		System.arraycopy(key, 0, keyInByte, 0, 24);
		Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyInByte, "DESede"));
		byte[] plaintext = cipher.doFinal(hexToByte(input));

		return byteToHex(plaintext);
	}

	private static byte[] hexToByte(String hex) {
		if ((hex.length() & 0x01) == 0x01) {
			throw new IllegalArgumentException();
		}

		byte[] bytes = new byte[hex.length() / 2];

		for (int idx = 0; idx < bytes.length; ++idx) {
			int hi = Character.digit((int) hex.charAt(idx * 2), 16);
			int lo = Character.digit((int) hex.charAt(idx * 2 + 1), 16);

			if ((hi < 0) || (lo < 0)) {
				throw new IllegalArgumentException();
			}

			bytes[idx] = (byte) ((hi << 4) | lo);
		}

		return bytes;
	}

	private static String byteToHex(byte[] bytes) {
		char[] hex = new char[bytes.length * 2];

		for (int idx = 0; idx < bytes.length; ++idx) {
			int hi = (bytes[idx] & 0xF0) >>> 4;
			int lo = (bytes[idx] & 0x0F);
			hex[idx * 2] = (char) (hi < 10 ? '0' + hi : 'A' - 10 + hi);
			hex[idx * 2 + 1] = (char) (lo < 10 ? '0' + lo : 'A' - 10 + lo);
		}

		return new String(hex);
	}

	private static String stringAsHex(String str) {
		byte[] buf = str.getBytes();
		char[] chars = new char[2 * buf.length];

		for (int i = 0; i < buf.length; ++i) {
			chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
		}

		return new String(chars);
	}

	private class SavePINTask extends AsyncTask<String, Integer, Integer> {
		// The code to be executed in a background thread.
		@Override
		protected Integer doInBackground(String... params) {
			if (isSaving) {
				return 3;
			} else {
				isSaving = true;
				Integer isSuccess = 1;

				if (isEdit) {
					if (params[1] != null) {
						try {
							boolean isPass = PIN.check(params[1], UserAccount.ccPIN(context));

							if (!isPass) {
								return 2;
							}
						} catch (Exception e) {
							Logger.e(TAG, "Check PIN error: " + e.toString());
							return 1;
						}
					}
				}
				if (params[0].length() == 0) {
					UserAccount.setCreditCardPIN(context, "");
					isSuccess = 0;
				} else {
					try {
						String passwordHash = PIN.getSaltedHash(params[0]);
						UserAccount.setCreditCardPIN(context, passwordHash);
						isSuccess = 0;
					} catch (Exception e) {
						Logger.e(TAG, "Save PIN error: " + e.toString());
					}
				}
				return isSuccess;
			}
		}

		// 0 = success, 1 = saving fail, 2 = old PIN is wrong, 3 = no action taken
		@Override
		protected void onPostExecute(Integer flag) {
			if (!notInScreen) {
				if (flag == 1) {
					new AlertDialog.Builder(context).setTitle(R.string.err).setMessage(R.string.pin_not_save).setCancelable(false)
							.setPositiveButton(R.string.ok, null).show();

					isSaving = false;
				} else if (flag == 2) {
					new AlertDialog.Builder(context).setTitle(R.string.err).setMessage(R.string.pin_not_correct).setCancelable(false)
							.setPositiveButton(R.string.ok, null).show();

					isSaving = false;
				} else if (flag == 0) {
					showWelcomeDialog();
				}
			}
		}
	}

	private void showWelcomeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage(R.string.welcome_message).setCancelable(true);
		final AlertDialog dialog = builder.create();
		dialog.show();
		new CountDownTimer(2000, 2000) {
			public void onTick(long millisUntilFinished) {
				
			}

			public void onFinish() {
				dialog.dismiss();
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferencesManager.savePreferences(sharedPreferences, MBDefinition.SHARE_ALREADY_REGISTER, true);
				Intent intent = new Intent(context, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}.start();

	}

}
