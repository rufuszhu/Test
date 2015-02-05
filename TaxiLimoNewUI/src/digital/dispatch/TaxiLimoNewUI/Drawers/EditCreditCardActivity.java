package digital.dispatch.TaxiLimoNewUI.Drawers;

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
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.digital.dispatch.TaxiLimoSoap.requests.KeyExchangeRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.KeyExchangeRequest.IKeyExchangeResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.requests.TokenizationRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.TokenizationRequest.ITokenizationResponseListener;
import com.digital.dispatch.TaxiLimoSoap.responses.KeyExchangeResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.TokenizationResponse;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBCreditCard;
import digital.dispatch.TaxiLimoNewUI.DBCreditCardDao;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.DeleteCreditCardTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition.ccRequestType;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class EditCreditCardActivity extends BaseActivity implements TextWatcher, OnFocusChangeListener {
	private EditText edtCardNum, edtHolderName, edtExpiryMonth, edtExpiryYear, edtZip, edtNickname;
	private TextView btnSave;
	private final String prime = "11684510167982206765990851851544155388252758257075719142008344429621708996" + "506416490173644860944227986735266303324043629151480087265000441195390936848807913";
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
	private String keyTimeStamp, ccToken, paymentTripID, paymentSysID, paymentDestID;
	private boolean notInScreen = false; // check if this activity screen still live

	private DaoManager daoManager;
	private DBCreditCardDao creditCardDao;
	private Context context;

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
	private static final String TAG = "EditCreditCardActivity";
	private static final int MAX_CARD_NUM_LEN = 19;
	private static final int MIN_CARD_NUM_LEN = 13;
	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	private DBCreditCard cardToEdit;
	private DBBooking dbBook;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_credit_card);
		
		if(getIntent().getExtras()!=null)
			dbBook = (DBBooking) getIntent().getSerializableExtra(MBDefinition.EXTRA_BOOKING);
		else
			dbBook = null;
		
		findView();
		cardToEdit = (DBCreditCard) getIntent().getSerializableExtra(MBDefinition.EXTRA_CREDIT_CARD);
		if (cardToEdit == null)
			getActionBar().setTitle(getResources().getString(R.string.title_activity_register_credit_card));
		else{
			getActionBar().setTitle(getResources().getString(R.string.title_activity_edit_credit_card));
			setupEditView(cardToEdit);
		}
		context = this;
		

	}
	
	

	private void findView() {
		edtCardNum = (EditText) findViewById(R.id.et_card_number);
		edtHolderName = (EditText) findViewById(R.id.et_holder_name);
		edtExpiryMonth = (EditText) findViewById(R.id.et_mm);
		edtExpiryYear = (EditText) findViewById(R.id.et_yy);
		edtZip = (EditText) findViewById(R.id.et_zip);
		edtNickname = (EditText) findViewById(R.id.et_nickname);
		btnSave = (TextView) findViewById(R.id.save_btn);

		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (cardToEdit == null)
					addCreditCard();
				else {
					cardToEdit.setNickName(edtNickname.getText().toString());
					creditCardDao.update(cardToEdit);
				}
			}
		});
	}

	@Override
	public void onResume() {
		edtCardNum.setOnFocusChangeListener(this);
		edtHolderName.setOnFocusChangeListener(this);
		edtExpiryMonth.setOnFocusChangeListener(this);
		edtExpiryYear.setOnFocusChangeListener(this);
		edtZip.setOnFocusChangeListener(this);

		edtCardNum.addTextChangedListener(this);

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if (cardToEdit != null)
			getMenuInflater().inflate(R.menu.edit_credit_card, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_delete_credit_card) {
			new AlertDialog.Builder(context).setTitle("Confirm Delete Credit Card").setMessage("Are you sure you want to delete?")
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new DeleteCreditCardTask(context).execute(cardToEdit.getToken());
							creditCardDao.delete(cardToEdit);
							finish();
						}
					}).setNegativeButton(R.string.cancel, null).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			validate((EditText) v);
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		if (arg0 == edtCardNum.getEditableText()) {
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
									tempCardNum = tempCardNum.substring(0, CARD_FORMAT[i][j] + occurrenceCount) + CARD_NUM_SEPARATOR + tempCardNum.substring(CARD_FORMAT[i][j] + occurrenceCount);
								}
							}

							isAddingSpace = true;
							edtCardNum.setText(tempCardNum);
							edtCardNum.setSelection(edtCardNum.length());

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
	
	private void setupEditView(DBCreditCard card) {
		edtCardNum.setText("..." + card.getLast4CardNum());
		edtHolderName.setText(card.getHolderName());
		edtZip.setText(card.getZip());
		edtNickname.setText(card.getNickName());
		
		
		edtExpiryMonth.setText(card.getExpiryMonth());
		edtExpiryYear.setText(card.getExpiryYear());
		
		ccToken = card.getToken();
		
		setUnEditable(edtCardNum);
		setUnEditable(edtHolderName);
		setUnEditable(edtZip);
		setUnEditable(edtExpiryMonth);
		setUnEditable(edtExpiryYear);
	}
	
	private void setUnEditable(EditText target) {
		target.setFocusable(false);
		target.setTextColor(getResources().getColor(R.color.gray_light));
	}

	private boolean validate(EditText target) {
		boolean isValid = true;

		if (target != null) {
			if (target == edtCardNum) {
				return validateCardNum();
			} else if (target == edtHolderName) {
				return validateHolderName();
			} else if (target == edtExpiryMonth) {
				return validateExMonth();
			} else if (target == edtExpiryYear) {
				return validateExYear();
			} else if (target == edtZip) {
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
		String editCardNum = edtCardNum.getText().toString().trim();
		int cardNumLen = editCardNum.length();

		if (cardNumLen < MIN_CARD_NUM_LEN) {
			edtCardNum.setError(getResources().getString(R.string.credit_card_ccnum_too_short));
			isValid = false;
		} else if (cardNumLen > MAX_CARD_NUM_LEN) {
			edtCardNum.setError(getResources().getString(R.string.credit_card_ccnum_too_long));
			isValid = false;
		} else {
			edtCardNum.setError(null);
		}

		return isValid;
	}

	// Card Holder Name
	private boolean validateHolderName() {
		boolean isValid = true;

		if (edtHolderName.getText().toString().equalsIgnoreCase("")) {
			edtHolderName.setError(getResources().getString(R.string.credit_card_empty_holder_name));
			isValid = false;
		} else {
			edtHolderName.setError(null);
		}

		return isValid;
	}

	// Expiry Month
	private boolean validateExMonth() {
		boolean isValid = true;

		if (edtExpiryMonth.getText().toString().equalsIgnoreCase("")) {
			edtExpiryMonth.setError(getResources().getString(R.string.credit_card_month_invalid));
			isValid = false;
		} else if (edtExpiryMonth.getText().toString().length() != 2) {
			edtExpiryMonth.setError(getResources().getString(R.string.credit_card_month_too_short));
			isValid = false;
		} else {
			int exMonth = Integer.parseInt(edtExpiryMonth.getText().toString());

			if (exMonth <= 12 && exMonth >= 1) {
				edtExpiryMonth.setError(null);
			} else {
				edtExpiryMonth.setError(getResources().getString(R.string.credit_card_month_invalid));
				isValid = false;
			}
		}

		return isValid;
	}

	// Expiry Year
	private boolean validateExYear() {
		boolean isValid = true;

		if (edtExpiryYear.getText().toString().equalsIgnoreCase("")) {
			edtExpiryYear.setError(getResources().getString(R.string.credit_card_year_invalid));
			isValid = false;
		} else if (edtExpiryYear.getText().toString().length() != 2) {
			edtExpiryYear.setError(getResources().getString(R.string.credit_card_year_too_short));
			isValid = false;
		} else {
			int exYear = Integer.parseInt(edtExpiryYear.getText().toString());
			int curYear = Integer.parseInt(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2));

			// compare if the entered year is greater or equal to current year
			if (exYear >= curYear) {
				edtExpiryYear.setError(null);
			} else {
				edtExpiryYear.setError(getResources().getString(R.string.credit_card_year_invalid));
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
		int exYear = Integer.parseInt(edtExpiryYear.getText().toString()) + curYear - (curYear % 100);
		int exMonth = Integer.parseInt(edtExpiryMonth.getText().toString()) - 1;

		// set the expiring date to year and month on the card with today's day
		Calendar exCal = Calendar.getInstance();
		exCal.set(exYear, exMonth, curDay);

		if (exCal.compareTo(Calendar.getInstance()) >= 0) {
			edtExpiryYear.setError(null);
		} else {
			edtExpiryMonth.setError(getResources().getString(R.string.credit_card_expired_date));
			isValid = false;
		}

		return isValid;
	}

	// Billing Address
	private boolean validateAddress() {
		boolean isValid = true;

		if (edtZip.getText().toString().equalsIgnoreCase("")) {
			edtZip.setError(getResources().getString(R.string.credit_card_empty_zip));
			isValid = false;
		} else {
			edtZip.setError(null);
		}

		return isValid;
	}

	private void addCreditCard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		if (getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}

		if (validate(null)) {
			if (registerCardCheck()) {
				if (tripleDESKey != null) {
					new RegisterCreditCardTask().execute(ccRequestType.AddCard.toString());
				} else {
					new GetKeyTask(true).execute(ccRequestType.AddCard.toString());
				}
			} else {
				new AlertDialog.Builder(EditCreditCardActivity.this).setTitle(R.string.err).setMessage(R.string.err_msg_duplicated_card).setPositiveButton(R.string.ok, null).show();
			}
		}
	}

	private void saveEditedCC() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		if (getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}

		new RegisterCreditCardTask().execute(ccRequestType.EditCard.toString());

	}

	private boolean registerCardCheck() {

		List<DBCreditCard> cardList = creditCardDao.queryBuilder().list();
		for (DBCreditCard card : cardList) {
			String cardNum = edtCardNum.getText().toString();
			// check no duplicated card
			if (card.getLast4CardNum().equalsIgnoreCase(cardNum.substring(cardNum.length() - 4, cardNum.length())) && card.getFirst4CardNum().equalsIgnoreCase(cardNum.substring(0, 4))) {
				return false;
			}
		}

		return true;
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
			progressDialog = new Dialog(EditCreditCardActivity.this);
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
				kExReq.setDeviceID(Utils.getHardWareId(EditCreditCardActivity.this));

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
				new AlertDialog.Builder(EditCreditCardActivity.this).setTitle(R.string.err_error_response).setMessage(R.string.err_msg_no_response).setPositiveButton(R.string.ok, null)
						.show();
			}

			Logger.v(TAG, "error response: " + errorString);
		}

		@Override
		public void onError() {
			progressDialog.dismiss();
			if (!notInScreen) {
				new AlertDialog.Builder(EditCreditCardActivity.this).setTitle(R.string.err_no_response_error).setMessage(R.string.err_msg_no_response)
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
			progressDialog = new Dialog(EditCreditCardActivity.this);
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
				tokenReq.setDeviceID(Utils.getHardWareId(EditCreditCardActivity.this));
				tokenReq.setSequenceNum(sequenceNum.toString());
				tokenReq.setReqType(params[0]);

				String cardNumHex = stringAsHex(edtCardNum.getText().toString().replace(CARD_NUM_SEPARATOR, ""));
				String expiryHex = stringAsHex(edtExpiryMonth.getText().toString() + edtExpiryYear.getText().toString());

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
					String zipHex = stringAsHex(edtZip.getText().toString());

					for (int i = zipHex.length(); i < ZIP_PADDING_LENGTH; i++) {
						zipHex += CARD_INFO_PADDING_CHAR;
					}

					tokenReq.setDESTimeStamp(keyTimeStamp);
					tokenReq.setCardNumAndExDate(encryptedCardInfo);
					tokenReq.setCardHolderName(edtHolderName.getText().toString());
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
			// MBCreditCard card = new MBCreditCard(cardNum, edtHolderName.getText().toString(), edtExpiryMonth.getText().toString() + "/" + edtExpiryYear.getText().toString(), edtZip.getText()
			// .toString(), edtEmail.getText().toString(), edtNickname.getText().toString(), token, brand, edtCardNum.getText().toString().substring(0, 4));
			DBCreditCard card = new DBCreditCard();
			card.setCardBrand(brand);
			card.setExpiryMonth(edtExpiryMonth.getText().toString());
			card.setExpiryYear(edtExpiryYear.getText().toString());
			card.setFirst4CardNum(edtCardNum.getText().toString().substring(0, 4));
			card.setHolderName(edtHolderName.getText().toString());
			card.setLast4CardNum(cardNum);
			card.setNickName(edtNickname.getText().toString());
			card.setToken(token);
			card.setZip(edtZip.getText().toString());
			creditCardDao.insert(card);

			Logger.v(TAG, "Done cc - Approve");

			new AlertDialog.Builder(EditCreditCardActivity.this).setTitle(R.string.register_success).setMessage(R.string.credit_card_registration_approved)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							 if (dbBook!=null) {
							 Intent ccIntent = new Intent();
							 	ccIntent.putExtra(MBDefinition.EXTRA_BOOKING, dbBook);
							 	setResult(RESULT_OK, ccIntent);
							 }

							finish();
						}
					}).show();
		}

		@Override
		public void onErrorResponse(TokenizationResponse res) {
			progressDialog.dismiss();
			int errCode = res.GetResponseCode();

			if (!notInScreen) {
				// Default generic error, unknown error, retry may succeed
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
				new AlertDialog.Builder(EditCreditCardActivity.this).setTitle(R.string.err_no_response_error).setMessage(R.string.err_msg_no_response)
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
		new AlertDialog.Builder(EditCreditCardActivity.this).setTitle(R.string.register_unsuccess).setMessage(msgID).setCancelable(false).setPositiveButton(R.string.ok, null).show();
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

}
