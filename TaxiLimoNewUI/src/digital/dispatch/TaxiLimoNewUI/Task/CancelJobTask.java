package digital.dispatch.TaxiLimoNewUI.Task;

import com.digital.dispatch.TaxiLimoSoap.requests.CancelJobRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.CancelJobRequest.ICancelResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.CancelJobResponse;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

import android.content.Context;
import android.os.AsyncTask;

public class CancelJobTask extends AsyncTask<Void, Integer, Void> implements ICancelResponseListener, IRequestTimerListener {
	private static final String TAG = "CancelJobTask";
	private CancelJobRequest cjReq;
	private DBBooking dbBook;
	private Context _context;

	public CancelJobTask(Context context, DBBooking dbBook) {
		this.dbBook = dbBook;
		_context = context;
	}

	@Override
	protected void onPreExecute() {

	}

	// The code to be executed in a background thread.
	@Override
	protected Void doInBackground(Void... params) {
		try {
			cjReq = new CancelJobRequest(this, this);
			cjReq.setSysID(dbBook.getSysId() + "");
			cjReq.setDestID(dbBook.getDestID() + "");
			cjReq.setMGVersion(MBDefinition.OSP_VERSION);
			cjReq.setTaxiRideID(dbBook.getTaxi_ride_id() + "");
			cjReq.setReqType("2");
			cjReq.sendRequest(_context.getResources().getString(R.string.name_space), _context.getResources().getString(R.string.url));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// Update the progress
	@Override
	protected void onProgressUpdate(Integer... values) {
		// set the current progress of the progress dialog
		// progressDialog.setProgress(values[0]);
	}

	@Override
	public void onResponseReady(CancelJobResponse response) {

		// String msg = getResources().getString(R.string.track_cancel_success) + " (Trip " + trID + ")";
		//
		// // no need to check response.getStatus() == 0, since if it's not it will be in onErrorResponse
		// new AlertDialog.Builder(getActivity())
		// .setTitle(R.string.cancel_job)
		// .setMessage(msg)
		// .setCancelable(false)
		// .setPositiveButton(R.string.positive_button, null)
		// .show();
		//
		// startGetTrackingInfo();
		Utils.showErrorDialog(_context.getString(R.string.message_cancel_successful), _context);
		Logger.v(TAG, "Cancel Job: " + response.getStatus() + " :: " + response.getErrorString());
	}

	@Override
	public void onErrorResponse(String errorString) {
		// if (!noProgress) {
		// isDialogVisible = true;
		//
		// new AlertDialog.Builder(getActivity())
		// .setTitle(R.string.cancel_job)
		// .setMessage(R.string.track_cancel_unsuccess)
		// .setCancelable(false)
		// .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// isDialogVisible = false;
		// }
		// })
		// .show();
		//
		// startGetTrackingInfo();
		// isRefreshing = false;
		// }
		Utils.showErrorDialog(_context.getString(R.string.message_cancel_successful), _context);
		Logger.e(TAG, "cancelJob: ResponseError - " + errorString);
	}

	@Override
	public void onError() {
		// if (!noProgress) {
		// isDialogVisible = true;
		//
		// new AlertDialog.Builder(getActivity())
		// .setTitle(R.string.err_no_response_error)
		// .setMessage(R.string.track_cancel_unsuccess)
		// .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// isDialogVisible = false;
		// }
		// })
		// .show();
		//
		// startGetTrackingInfo();
		// isRefreshing = false;
		// }
		Utils.showErrorDialog(_context.getString(R.string.message_cancel_successful), _context);
		Logger.v(TAG, "cancelJob: Error");
	}

	@Override
	public void onProgressUpdate(int progress) {

	}
}