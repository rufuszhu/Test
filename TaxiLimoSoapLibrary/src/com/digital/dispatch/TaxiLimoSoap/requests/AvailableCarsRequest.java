package com.digital.dispatch.TaxiLimoSoap.requests;

import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.DataParam;
import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.HandleReqResTask;
import com.digital.dispatch.TaxiLimoSoap.MethodEnum;
import com.digital.dispatch.TaxiLimoSoap.RequestEnum;
import com.digital.dispatch.TaxiLimoSoap.SoapTypeWrapper;
import com.digital.dispatch.TaxiLimoSoap.HandleReqResTask.ICustomResponseListener;
import com.digital.dispatch.TaxiLimoSoap.responses.AvailableCarsResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyListResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

import java.util.ArrayList;

/**
 * Created by ezhang on 2/2/2015.
 */
public class AvailableCarsRequest extends Request{
    private String regionName, state, country;
    private String latitude, longitude;
    private ICarsResponseListener iResponseListener = null;
    private static String TAG = "Soap-AvailableCars";

    public AvailableCarsRequest(ICarsResponseListener resListener, IRequestTimerListener timeListener) {
        super(timeListener);
        iResponseListener = resListener;
    }

    public void setRegionName(String region) {
        regionName = region;
    }

    public void setStateProvince(String statOrProv) {
        state = statOrProv;
    }

    public void setCountry(String Country) {
        this.country = Country;
    }

    public void setLatitude(String lat) {
        latitude = lat;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sendRequest(String ns, String url) {
        startProgress(); // start sending UI time check progress

        if (!isReqCancelled) {
            new HandleReqResTask(new ICustomResponseListener() {
                @Override
                public void onResponseReady(Object response) {
                    if (!isReqCancelled) {
                        finishProgress(); // response is back, can tell UI to finish progress bar

                        try {
                            ResponseWrapper rWrapper = new ResponseWrapper(((SoapTypeWrapper)response).GetSoap());

                            if (rWrapper.getStatus() != 0) {
                                iResponseListener.onErrorResponse(rWrapper);

                                if (GlobalVar.logEnable) {
                                    Log.v(TAG, "Response Status: " + rWrapper.getErrorString());
                                }
                            }
                            else {
                                AvailableCarsResponse acRes = new AvailableCarsResponse(((SoapTypeWrapper)response).GetSoap());
                                iResponseListener.onResponseReady(acRes);
                            }
                        }
                        catch (Exception e) {
                            iResponseListener.onError();

                            if (GlobalVar.logEnable) {
                                Log.e(TAG, "Response Error: " + e.toString());
                            }
                        }
                    }
                }

                @Override
                public void onError() {
                    finishProgress();
                    iResponseListener.onError();
                }
            }).execute(MethodEnum.AvailableCars, RequestEnum.AvailableCars, getArgumentList(), ns, url);
        }
    }

    public interface ICarsResponseListener {
        public void onResponseReady(AvailableCarsResponse response);
        public void onErrorResponse(ResponseWrapper rWrapper);
        public void onError();

    }

    private ArrayList<DataParam> getArgumentList() {
        ArrayList<DataParam> list = new ArrayList<DataParam>();

        if (regionName != null) { list.add(new DataParam("regionName", regionName)); }
        if (state != null) { list.add(new DataParam("state", state)); }
        if (country != null) { list.add(new DataParam("country", country)); }
        if (latitude != null) { list.add(new DataParam("latitude", latitude)); }
        if (longitude != null) { list.add(new DataParam("longitude", longitude)); }

        return list;
    }
}
