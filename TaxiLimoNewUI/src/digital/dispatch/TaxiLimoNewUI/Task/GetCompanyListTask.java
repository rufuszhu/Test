package digital.dispatch.TaxiLimoNewUI.Task;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.requests.CompanyListRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.CompanyListRequest.ICompResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyListResponse;

import java.util.Locale;

import digital.dispatch.TaxiLimoNewUI.Book.AttributeActivity;
import digital.dispatch.TaxiLimoNewUI.Book.BookActivity;
import digital.dispatch.TaxiLimoNewUI.Drawers.CompanyPreferenceActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class GetCompanyListTask extends AsyncTask<String, Integer, Void> implements ICompResponseListener, IRequestTimerListener {
	private static final String TAG = "GetCompanyListTask";
	CompanyListRequest clReq;
	Context _context;
	private Address mAddress;
	private boolean isFromBooking;
    private boolean isFromPreference;
    private String city;
    private String province;
    private String country;

	public GetCompanyListTask(Context context, Address mAddress, Boolean isFromBooking, boolean isFromPreference, String city, String province, String country) {
		_context = context;
		this.mAddress = mAddress;
		this.isFromBooking = isFromBooking;
        this.isFromPreference = isFromPreference;

        this.city = city;
        this.country = country;
        this.province = province;
	}

	// Before running code in separate thread
	@Override
	protected void onPreExecute() {
		if (!isFromBooking) {
            if(isFromPreference)
                Utils.showProcessingDialogWithMessage(_context, "Getting available companies in " + city);
            else
                Utils.showProcessingDialogWithMessage(_context, "Getting available companies in " + mAddress.getLocality());
        }
	}

	// The code to be executed in a background thread.
	@Override
	protected Void doInBackground(String... params) {
		try {
			clReq = new CompanyListRequest(this, this);
            if(isFromPreference){
                clReq.setRegionName(city.toUpperCase(Locale.US));
                clReq.setStateProvince(LocationUtils.states.get(province));
                clReq.setCountry(country);
            }
            else{
                clReq.setRegionName(mAddress.getLocality().toUpperCase(Locale.US));
                clReq.setStateProvince(LocationUtils.states.get(mAddress.getAdminArea()));
                clReq.setCountry(mAddress.getCountryCode().toUpperCase(Locale.US));
            }

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
		if (_context instanceof Activity) {
			Activity activity = (Activity) _context;
			if (activity.isFinishing()) {
				return;
			}
		}

		CompanyItem[] tempCompList = response.GetList();
		if (isFromBooking) {
			((BookActivity) _context).handleGetCompanyListResponse(tempCompList, mAddress.getLocality());
		}
        else if(isFromPreference){
            Utils.stopProcessingDialogWithMessage(_context);
            ((CompanyPreferenceActivity) _context).loadCompanyList(tempCompList);
        }
        else {
			Utils.stopProcessingDialogWithMessage(_context);
			((AttributeActivity) _context).loadCompanyList(tempCompList);
		}
	}

	@Override
	public void onErrorResponse(String errorString) {
		if (_context instanceof Activity) {
			Activity activity = (Activity) _context;
			if (activity.isFinishing()) {
				return;
			}
		}

		Log.v(TAG, "ResponseError - " + errorString);
		if (!isFromBooking)
			Utils.stopProcessingDialogWithMessage(_context);
		try {
			Utils.showMessageDialog(_context.getString(R.string.err_msg_no_response), _context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError() {
		Log.v(TAG, "Error");
		
		if ( _context instanceof Activity ) {
		    Activity activity = (Activity)_context;
		    if ( activity.isFinishing() ) {
		        return;
		    }
		}
		
		if (!isFromBooking)
			Utils.stopProcessingDialogWithMessage(_context);
		try {
			Utils.showMessageDialog(_context.getString(R.string.err_msg_no_response), _context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProgressUpdate(int progress) {
	}

}
