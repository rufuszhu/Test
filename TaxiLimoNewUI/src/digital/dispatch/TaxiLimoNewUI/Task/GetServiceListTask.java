package digital.dispatch.TaxiLimoNewUI.Task;

import android.content.Context;
import android.os.AsyncTask;

import com.digital.dispatch.TaxiLimoSoap.requests.GetServiceListRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.GetServiceListRequest.IGetServiceListResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.GetServiceListResponse;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Drawers.AddPreferenceActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;

public class GetServiceListTask extends AsyncTask<String, Integer, Void> implements IGetServiceListResponseListener, IRequestTimerListener {
	private static final String TAG = "GetServiceListTask";
	GetServiceListRequest gMBpReq;
	Context _context;

	public GetServiceListTask(Context context) {
		_context = context;
	}

	// The code to be executed in a background thread.
	@Override
	protected Void doInBackground(String... params) {
		gMBpReq = new GetServiceListRequest(this, this);
		gMBpReq.sendRequest(_context.getString(R.string.name_space), _context.getString(R.string.url));
		return null;
	}

	@Override
	public void onResponseReady(GetServiceListResponse response) {
		((AddPreferenceActivity)_context).getData(response.getCountryList(), response.getStateList(), response.getRegionList(), response.getCompanyList());
	}

	@Override
	public void onErrorResponse(String errorString) {

		Logger.e(TAG, "error response: " + errorString);
	}

	@Override
	public void onError() {

		Logger.e(TAG, "no response");
	}

	@Override
	public void onProgressUpdate(int progress) {
	}
}
