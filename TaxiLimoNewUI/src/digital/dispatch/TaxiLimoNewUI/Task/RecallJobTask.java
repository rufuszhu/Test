package digital.dispatch.TaxiLimoNewUI.Task;

import com.digital.dispatch.TaxiLimoSoap.requests.RecallJobsRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.RecallJobsRequest.IRecallJobsResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.JobItem;
import com.digital.dispatch.TaxiLimoSoap.responses.RecallJobsResponse;
import com.google.android.gms.maps.model.LatLng;

import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Book.AttributeActivity;
import digital.dispatch.TaxiLimoNewUI.Track.TrackDetailActivity;
import digital.dispatch.TaxiLimoNewUI.Track.TrackFragment;
import digital.dispatch.TaxiLimoNewUI.Track.TrackingMapActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

import android.content.Context;
import android.os.AsyncTask;

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
		JobItem[] jobArr= response.GetList();
		for(int i=0;i<jobArr.length;i++){
			JobItem.printJobItem(jobArr[i]);
		}
		if(which==MBDefinition.IS_FOR_MAP){
			LatLng carLatLng = new LatLng(Double.parseDouble(jobArr[0].carLatitude), Double.parseDouble(jobArr[0].carLongitude));
			((TrackingMapActivity)_context).updateCarMarker(carLatLng);
			((TrackingMapActivity)_context).updateStatus(jobArr);
		}
		else if(which==MBDefinition.IS_FOR_ONE_JOB){
			((TrackDetailActivity)_context).parseRecallJobResponse(jobArr); 
		}
		else if(which==MBDefinition.IS_FOR_LIST){
			TrackFragment fragment = (TrackFragment) ((MainActivity)_context).getSupportFragmentManager().findFragmentByTag("track"); 
			fragment.updateStatus(jobArr);
		}
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
		if(which==MBDefinition.IS_FOR_ONE_JOB){
			((TrackDetailActivity)_context).stopUpdateAnimation(); 
		}
		else if(which==MBDefinition.IS_FOR_LIST){
			TrackFragment fragment = (TrackFragment) ((MainActivity)_context).getSupportFragmentManager().findFragmentByTag("track"); 
			fragment.stopUpdateAnimation();
		}
		Utils.showMessageDialog(_context.getString(R.string.err_msg_no_response),_context);
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
		if(which==MBDefinition.IS_FOR_ONE_JOB){
			((TrackDetailActivity)_context).stopUpdateAnimation(); 
		}
		else if(which==MBDefinition.IS_FOR_LIST){
			TrackFragment fragment = (TrackFragment) ((MainActivity)_context).getSupportFragmentManager().findFragmentByTag("track"); 
			if(fragment!=null)
				fragment.stopUpdateAnimation();
		}
		Utils.showMessageDialog(_context.getString(R.string.err_msg_no_response),_context);
		Logger.v(TAG, "no response");
	}

	@Override
	public void onProgressUpdate(int progress) {

	}
}
