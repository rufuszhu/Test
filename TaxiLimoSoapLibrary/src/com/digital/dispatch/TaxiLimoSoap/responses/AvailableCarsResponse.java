package com.digital.dispatch.TaxiLimoSoap.responses;


import android.util.Log;

import com.digital.dispatch.TaxiLimoSoap.GlobalVar;
import com.digital.dispatch.TaxiLimoSoap.serialization.PropertyInfo;
import com.digital.dispatch.TaxiLimoSoap.serialization.SoapObject;
/**
 * Created by ezhang on 2/2/2015.
 */
public class AvailableCarsResponse extends ResponseWrapper {
    private int numOfCars = 0;
    private CarGPSItem[] listOfCars = null;
    private static String TAG = "Soap-AvailableCarsRes";

    public AvailableCarsResponse() {
        super();
    }

    public AvailableCarsResponse(SoapObject soap) {
        super(soap);

        try {
            numOfCars = Integer.parseInt(this.getProperty("nrOfCars").toString());
            if (numOfCars > 0) {
                listOfCars = new CarGPSItem[numOfCars];

                int j = 0;
                for (int i = 0; i < properties.size(); i++) {
                    Object item = ((PropertyInfo) properties.elementAt(i)).getValue();
                    String objName = ((PropertyInfo) properties.elementAt(i)).getName();
                    String carFile, carLat, carLong;


                    if (item instanceof SoapObject) {
                        if (objName.equalsIgnoreCase("listOfCars")) {
                            carFile = checkExistAndGet((SoapObject) item, "carFile");
                            carLat = ((SoapObject) item).getProperty("latitude").toString();
                            carLong = ((SoapObject) item).getProperty("longitude").toString();


                            if (GlobalVar.logEnable) {
                                Log.v(TAG, "carFile=" + carFile + ", carLat=" + carLat + ", carLong=" + carLong);
                            }

                            CarGPSItem ci = new CarGPSItem(carFile, carLat, carLong);
                            if (j < numOfCars) {

                                listOfCars[j] = ci;
                                j++;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            if (GlobalVar.logEnable) {
                Log.e(TAG, "ERROR: " + e.toString());
            }
        }
    }

    public CarGPSItem[] GetList() {
        return listOfCars;
    }

    public int GetNbrOfCars() {
        return numOfCars;
    }

    private String checkExistAndGet(SoapObject so, String propertyName) {
        if (so.hasProperty(propertyName)) {
            String temp = so.getProperty(propertyName).toString();
            if (temp.equalsIgnoreCase("anyType{}")) {
                return "";
            }
            else {
                return temp;
            }
        }
        else {
            return "";
        }
    }
}
