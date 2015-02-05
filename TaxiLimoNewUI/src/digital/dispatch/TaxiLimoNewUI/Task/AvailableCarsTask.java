package digital.dispatch.TaxiLimoNewUI.Task;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;

import com.digital.dispatch.TaxiLimoSoap.requests.AvailableCarsRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.Request;
import com.digital.dispatch.TaxiLimoSoap.responses.AvailableCarsResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.CarGPSItem;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

import java.util.Locale;

import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.R;

/**
 * Created by ezhang on 2/2/2015.
 */
public class AvailableCarsTask extends AsyncTask<String, Integer, Void> implements AvailableCarsRequest.ICarsResponseListener, Request.IRequestTimerListener {
    private static final String TAG = "AvailableCarsTask";
    AvailableCarsRequest acReq;
    Context _context;


    private Address mAddress;

    public AvailableCarsTask(Context context, Address mAddress) {
        _context = context;
        this.mAddress = mAddress;

    }



    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            acReq = new AvailableCarsRequest(this, this);
            acReq.setRegionName(mAddress.getLocality().toUpperCase(Locale.US));
            acReq.setStateProvince(LocationUtils.states.get(mAddress.getAdminArea()));
            acReq.setCountry(mAddress.getCountryCode().toUpperCase(Locale.US));
            acReq.setLatitude(Double.toString(mAddress.getLatitude()));
            acReq.setLongitude(Double.toString(mAddress.getLongitude()));
            acReq.sendRequest(_context.getString(R.string.name_space), _context.getString(R.string.url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onResponseReady(AvailableCarsResponse response) {
        int carNumbers = response.GetNbrOfCars();
        CarGPSItem[] tempCarList = null;
        if(carNumbers > 0) {
            tempCarList = response.GetList();
            /*
            for (int i = 0; i < tempCarList.length; i++) {
                CarGPSItem.printCarGPSItem(tempCarList[i]);
            }*/
        }
        if (_context instanceof Activity && (((MainActivity) _context).bookFragment.isAdded())) {
            ((MainActivity) _context).bookFragment.handleAvailableCarsResponse(tempCarList, carNumbers);
        }

    }

    @Override
    public void onError() {
        Logger.e(TAG, "no response");

    }

    @Override
    public void onErrorResponse(ResponseWrapper rWrapper) {
        Logger.e(TAG, "error response: " + rWrapper.getErrorString());
    }


    @Override
    public void onProgressUpdate(int progress) {

    }
}
