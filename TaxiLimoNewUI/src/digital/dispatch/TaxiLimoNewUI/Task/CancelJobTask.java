package digital.dispatch.TaxiLimoNewUI.Task;

import android.content.Context;
import android.os.AsyncTask;

import com.digital.dispatch.TaxiLimoSoap.requests.CancelJobRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.CancelJobRequest.ICancelResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.CancelJobResponse;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Track.TrackDetailActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

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
		Utils.showProcessingDialog(_context);
	}

	// The code to be executed in a background thread.
	@Override
	protected Void doInBackground(Void... params) {
		//set job's status in database to canceled 
		dbBook.setTripStatus(MBDefinition.MB_STATUS_CANCELLED);
		dbBook.setTripCancelledTime(System.currentTimeMillis() + "");
		DaoManager daoManager = DaoManager.getInstance(_context);
		DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
		bookingDao.update(dbBook);
		
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
		
		Utils.stopProcessingDialog(_context);
		if(_context instanceof TrackDetailActivity)
			((TrackDetailActivity)_context).showCancelDialog();
		else
			Utils.showMessageDialog(_context.getString(R.string.message_cancel_successful), _context);
				
		
		Logger.v(TAG, "Cancel Job: " + response.getStatus() + " :: " + response.getErrorString());
	}

	@Override
	public void onErrorResponse(String errorString) {
		Utils.stopProcessingDialog(_context);
		//we force cancel the job even if the request fail
		if(_context instanceof TrackDetailActivity)
			((TrackDetailActivity)_context).showCancelDialog();
		else
			Utils.showMessageDialog(_context.getString(R.string.message_cancel_successful), _context);
		
		Logger.e(TAG, "cancelJob: ResponseError - " + errorString);
	}

	@Override
	public void onError() {
		Utils.stopProcessingDialog(_context);
		if(_context instanceof TrackDetailActivity)
			((TrackDetailActivity)_context).showCancelDialog();
		else
			Utils.showMessageDialog(_context.getString(R.string.message_cancel_successful), _context);
		
		Logger.v(TAG, "cancelJob: Error");
	}

	@Override
	public void onProgressUpdate(int progress) {

	}
	

}