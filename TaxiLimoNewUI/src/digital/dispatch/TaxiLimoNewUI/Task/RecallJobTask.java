package digital.dispatch.TaxiLimoNewUI.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;

import com.digital.dispatch.TaxiLimoSoap.requests.RecallJobsRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.RecallJobsRequest.IRecallJobsResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.JobItem;
import com.digital.dispatch.TaxiLimoSoap.responses.RecallJobsResponse;
import com.google.android.gms.maps.model.LatLng;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Track.TrackDetailActivity;
import digital.dispatch.TaxiLimoNewUI.Track.TrackFragment;
import digital.dispatch.TaxiLimoNewUI.Track.TrackingMapActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class RecallJobTask extends AsyncTask<String, Integer, Boolean> implements IRecallJobsResponseListener, IRequestTimerListener {
	private static final String TAG = "RecallJobTask";
	private RecallJobsRequest rjReq;
	private Context _context;
	private String jobList;
	private int which;

	public RecallJobTask(Context context, String jobs, int which) {
		_context = context;
		jobList = jobs;
		this.which = which;
	}

	// Before running code in separate thread
	@Override
	protected void onPreExecute() {

	}

	// The code to be executed in a background thread.
	@Override
	protected Boolean doInBackground(String... params) {
		try {
			rjReq = new RecallJobsRequest(this, this);
			rjReq.setSysID(params[1]);
			rjReq.setOspVersion(MBDefinition.OSP_VERSION);
			rjReq.setCriteria("10"); // recall by TRID

			rjReq.setTaxiCompanyID(params[0]);

			rjReq.setJobList(jobList); // can just concatenate into one string
			rjReq.sendRequest(_context.getResources().getString(R.string.name_space), _context.getResources().getString(R.string.url));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean isReqSent) {

	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// set the current progress of the progress dialog
		// progressDialog.setProgress(values[0]);
	}

	@Override
	public void onResponseReady(RecallJobsResponse response) {
		JobItem[] jobArr = response.GetList();
		for (int i = 0; i < jobArr.length; i++) {
			JobItem.printJobItem(jobArr[i]);
		}

		List<DBBooking> dbBook = updateDBJobs(jobArr);

		if (which == MBDefinition.IS_FOR_MAP || which == MBDefinition.IS_FOR_ONE_JOB) {
			LatLng carLatLng = new LatLng(Double.parseDouble(jobArr[0].carLatitude), Double.parseDouble(jobArr[0].carLongitude));
			((TrackDetailActivity) _context).updateCarMarker(carLatLng, dbBook);
			((TrackDetailActivity) _context).parseRecallJobResponse(dbBook);
		} else if (which == MBDefinition.IS_FOR_LIST) {
			((MainActivity) _context).trackFragment.updateStatus(dbBook);
		}
	}

	private List<DBBooking> updateDBJobs(JobItem[] jobArr) {
		List<DBBooking> bookingList = new ArrayList<DBBooking>();
		DaoManager daoManager = DaoManager.getInstance(_context);
		DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
		DBBooking dbBook = new DBBooking();
		for (int i = 0; i < jobArr.length; i++) {
			dbBook = bookingDao.queryBuilder().where(Properties.Taxi_ride_id.eq(jobArr[i].taxi_ride_id)).list().get(0);
			JobItem job = jobArr[i];
			switch (Integer.parseInt(job.tripStatusUniformCode)) {
			case MBDefinition.TRIP_STATUS_BOOKED:
			case MBDefinition.TRIP_STATUS_DISPATCHING:
				break;
			case MBDefinition.TRIP_STATUS_ACCEPTED:
				dbBook.setTripStatus(MBDefinition.MB_STATUS_ACCEPTED);
				break;
			case MBDefinition.TRIP_STATUS_ARRIVED:
				dbBook.setTripStatus(MBDefinition.MB_STATUS_ARRIVED);
				break;
			case MBDefinition.TRIP_STATUS_COMPLETE:
				switch (Integer.parseInt(job.detailTripStatusUniformCode)) {
				case MBDefinition.DETAIL_STATUS_IN_SERVICE:
					dbBook.setTripStatus(MBDefinition.MB_STATUS_IN_SERVICE);
					break;
				case MBDefinition.DETAIL_STATUS_COMPLETE:
					dbBook.setTripStatus(MBDefinition.MB_STATUS_COMPLETED);
					break;
				case MBDefinition.DETAIL_STATUS_CANCEL:
					dbBook.setTripStatus(MBDefinition.MB_STATUS_CANCELLED);
					break;
				// special complete: no show, force complete etc. set as "Cancelled" to user
				case MBDefinition.DETAIL_STATUS_NO_SHOW:
				case MBDefinition.DETAIL_STATUS_FORCE_COMPLETE:
					if (dbBook.getTripCompletionTime().length() == 0) {
						Calendar cal = Calendar.getInstance();
						SimpleDateFormat pickupTimeFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.US);
						dbBook.setTripCompletionTime(pickupTimeFormat.format(cal.getTime()));
					}
					dbBook.setTripStatus(MBDefinition.MB_STATUS_CANCELLED);

					break;
				// other unimportant intermediate status, just ignore
				case MBDefinition.DETAIL_OTHER_IGNORE:
				default:
					break;
				}
				break;
			default:
				break;
			}
			dbBook.setCarLatitude(Double.parseDouble(job.carLatitude));
			dbBook.setCarLongitude(Double.parseDouble(job.carLongitude));
			dbBook.setDispatchedCar(job.dispatchedCar);
			dbBook.setDispatchedDriver(job.dispatchedDriver);

			if (!job.redispatchJobID.equals(""))
				dbBook.setTaxi_ride_id(Integer.parseInt(job.redispatchJobID));
			bookingDao.update(dbBook);
			bookingList.add(dbBook);
		}
		return bookingList;
	}

	@Override
	public void onErrorResponse(String errorString) {
		// if (!isDialogVisible) {
		// new AlertDialog.Builder(getActivity())
		// .setTitle(R.string.err_error_response)
		// .setMessage(R.string.err_msg_no_response) // TODO: change to more detail error msg when come up
		// .setPositiveButton(R.string.positive_button, null)
		// .show();
		// }
		if (which == MBDefinition.IS_FOR_ONE_JOB) {
			((TrackDetailActivity) _context).stopUpdateAnimation();
		} else if (which == MBDefinition.IS_FOR_LIST) {
			((MainActivity) _context).trackFragment.stopUpdateAnimation();
		}
		try {
			Utils.showMessageDialog(_context.getString(R.string.err_msg_no_response), _context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Logger.v(TAG, "error response: " + errorString);
	}

	@Override
	public void onError() {
		// if (!isDialogVisible) {
		// new AlertDialog.Builder(getActivity())
		// .setTitle(R.string.err_no_response_error)
		// .setMessage(R.string.err_msg_no_response)
		// .setPositiveButton(R.string.positive_button, null)
		// .show();
		// }
		if (which == MBDefinition.IS_FOR_ONE_JOB) {
			((TrackDetailActivity) _context).stopUpdateAnimation();
		} else if (which == MBDefinition.IS_FOR_LIST) {

			((MainActivity) _context).trackFragment.stopUpdateAnimation();
		} else {
			((TrackingMapActivity) _context).stopUpdateAnimation();
		}
		try {
			Utils.showMessageDialog(_context.getString(R.string.err_msg_no_response), _context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Logger.v(TAG, "no response");
	}

	@Override
	public void onProgressUpdate(int progress) {

	}
}
