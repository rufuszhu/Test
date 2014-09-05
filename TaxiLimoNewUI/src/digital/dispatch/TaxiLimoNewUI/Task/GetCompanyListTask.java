package digital.dispatch.TaxiLimoNewUI.Task;

import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.requests.CompanyListRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.CompanyListRequest.ICompResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyListResponse;
import digital.dispatch.TaxiLimoNewUI.Book.AttributeActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;

import digital.dispatch.TaxiLimoNewUI.R;

public class GetCompanyListTask extends AsyncTask<String, Integer, Void> implements ICompResponseListener, IRequestTimerListener {
	private static final String TAG = "GetCompanyListTask";
	CompanyListRequest clReq;
	Context _context;
	private Address mAddress;
	private Boolean isFromBooking;

	public GetCompanyListTask(Context context, Address mAddress, Boolean isFromBooking) {
		_context = context;
		this.mAddress = mAddress;
		this.isFromBooking = isFromBooking;
	}

	// Before running code in separate thread
	@Override
	protected void onPreExecute() {
	}

	// The code to be executed in a background thread.
	@Override
	protected Void doInBackground(String... params) {
		try {
			clReq = new CompanyListRequest(this, this);
			clReq.setRegionName(mAddress.getLocality().toUpperCase(Locale.US));
			clReq.setStateProvince(LocationUtils.states.get(mAddress.getAdminArea()));
			clReq.setCountry(mAddress.getCountryName().toUpperCase(Locale.US));
			clReq.sendRequest(_context.getString(R.string.name_space), _context.getString(R.string.url));
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
	public void onResponseReady(CompanyListResponse response) {
		if(isFromBooking){
			
		}else{
			CompanyItem[] tempCompList = response.GetList();
			((AttributeActivity)_context).loadCompanyList(tempCompList);	
		}
	}

	@Override
	public void onErrorResponse(String errorString) {
			Log.v(TAG, "ResponseError - " + errorString);
	}

	@Override
	public void onError() {
			Log.v(TAG, "Error");
	}

	@Override
	public void onProgressUpdate(int progress) {
	}


}
