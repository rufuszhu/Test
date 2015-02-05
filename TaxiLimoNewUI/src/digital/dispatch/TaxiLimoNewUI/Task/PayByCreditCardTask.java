package digital.dispatch.TaxiLimoNewUI.Task;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.digital.dispatch.TaxiLimoSoap.requests.PayByTokenRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.PayByTokenRequest.IPayByTokenResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.PayByTokenResponse;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBCreditCard;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Track.PayActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.UserConfig;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class PayByCreditCardTask extends AsyncTask<Void, Integer, Void> implements IPayByTokenResponseListener, IRequestTimerListener {
	private static final String TAG = "PayByCreditCardTask";
	PayByTokenRequest pbtReq;
	Context _context;
	private DBBooking dbBook;
	private String totalAmount;
	private DBCreditCard cCard;
	private String finalTipAmount;

	public PayByCreditCardTask(Context context, DBBooking dbBook, String amount, DBCreditCard cCard, String finalTipAmount) {
		_context = context;
		this.dbBook =dbBook;
		this.totalAmount = amount;
		this.cCard = cCard;
		this.finalTipAmount = finalTipAmount;
	}

	// Before running code in separate thread
	@Override
	protected void onPreExecute() {
		Utils.showProcessingDialog(_context);
	}

	// The code to be executed in a background thread.
	@Override
	protected Void doInBackground(Void... params) {
		try {
			Integer sequenceNum = Integer.valueOf((int) (Math.random() * (MBDefinition.MDT_MAX_SEQUENCE_NUM + 1)));

			pbtReq = new PayByTokenRequest(this, this);
			pbtReq.setSysPassword(MBDefinition.SYSTEM_PASSWORD);
			pbtReq.setSysID(dbBook.getSysId());
			pbtReq.setDestID(dbBook.getDestID());
			pbtReq.setDeviceID(Utils.getHardWareId(_context));
			pbtReq.setSeqenceNum(sequenceNum.toString());
			pbtReq.setReqType("1");
			pbtReq.setToken(cCard.getToken());
			pbtReq.setAmount(totalAmount);
			pbtReq.setJobID(dbBook.getTaxi_ride_id()+"");
			pbtReq.setCardNum(cCard.getLast4CardNum());
			pbtReq.setCardBrand(cCard.getCardBrand());

			if (!finalTipAmount.equalsIgnoreCase("")) {
				pbtReq.setTip(finalTipAmount);
			}

			pbtReq.sendRequest(_context.getString(R.string.name_space), _context.getString(R.string.url));
		} catch (Exception e) {
			Logger.e(TAG, e.toString());
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// set the current progress of the progress dialog
		// progressDialog.setProgress(values[0]);
	}

	// Approved, all other will go to error response
	@Override
	public void onResponseReady(PayByTokenResponse response) {
		Utils.stopProcessingDialog(_context);
		// record last used credit card for next payment default
		UserConfig.setLastCreditCard(_context, cCard.getId());

			String msg = _context.getString(R.string.payment_approve_line_1_1) + totalAmount + " " + _context.getString(R.string.payment_approve_line_1_2) + "\n"
					+ _context.getString(R.string.payment_cc_approve_line_2_card_num) + "\n" + "*" + cCard.getLast4CardNum() + "\n"
					+ _context.getString(R.string.payment_cc_approve_line_3_auth_code) + " " + response.GetAuthorizationCode();

			if (response.GetEmailSent()) {
				msg += "\n" + _context.getString(R.string.payment_cc_approve_email_sent) + "\n" + response.GetEmail();
			}
			DaoManager daoManager = DaoManager.getInstance(_context);
			DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_READ);
			dbBook.setAlready_paid(true);
			dbBook.setPaidAmount(totalAmount);
			dbBook.setAuthCode(response.GetAuthorizationCode());
			bookingDao.update(dbBook);
			((PayActivity)_context).showPaySuccessDialog(msg); 
	}

	@Override
	public void onErrorResponse(PayByTokenResponse res) {
		Utils.stopProcessingDialog(_context);
		int errCode = res.GetResponseCode();

		// Default generic error, unknown error, retry may succeed
		if (errCode == 0 || errCode == 7 || errCode == 25) {
			notApproveAlert(R.string.payment_cc_err_generic_unknown);
		}
		// Duplicate transaction detected
		else if (errCode == 12 || errCode == 23) {
			notApproveAlert(R.string.payment_cc_err_duplicate);
		}
		// No such card
		else if (errCode == 19) {
			notApproveAlert(R.string.payment_cc_err_no_such_card);
		}
		// Connection problem/time out (PG, Global, Moneris etc.)
		else if (errCode == 20 || errCode == 21 || errCode == 28) {
			notApproveAlert(R.string.payment_cc_err_connection_tout);
		}
		// Declined due to fraud alert
		else if (errCode == 24) {
			notApproveAlert(R.string.payment_cc_err_fraud_alert);
		}
		// Problem with server/merchant configuration (Merchant account problem, setup problem)
		else if (errCode == 22 || errCode == 26 || errCode == 27) {
			notApproveAlert(R.string.payment_cc_err_server_config);
		}
		// Card is invalid
		else if (errCode == 29) {
			notApproveAlert(R.string.payment_cc_err_card_invalid);
		}
		// Card not authorized
		else if (errCode == 30) {
			notApproveAlert(R.string.payment_cc_err_unauthorized);
		}
		// Card limit problem
		else if (errCode == 31) {
			notApproveAlert(R.string.payment_cc_err_limit);
		}

	}

	@Override
	public void onError() {
		Utils.stopProcessingDialog(_context);
		new AlertDialog.Builder(_context).setTitle(R.string.err_no_response_error).setMessage(R.string.err_msg_no_response).setPositiveButton(R.string.ok, null).show();

		Logger.e(TAG, "no response");
	}

	@Override
	public void onProgressUpdate(int progress) {
	}

	private void notApproveAlert(int msgID) {
		new AlertDialog.Builder(_context).setTitle(R.string.payment_not_approve).setMessage(msgID).setCancelable(false).setPositiveButton(R.string.ok, null).show();
	}
}
