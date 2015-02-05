package com.digital.dispatch.TaxiLimoSoap.responses;

import java.io.Serializable;
import android.util.Log;

/**
 * Created by ezhang on 2/2/2015.
 */
public class CarGPSItem implements Serializable {
    private static final String TAG = "CarGPSItem";

    public String carFile;
    public String carLongitude ;
    public String carLatitude ;

    public CarGPSItem() {

        this.carFile = "";
        this.carLongitude = "";
        this.carLatitude = "";

    }

    public CarGPSItem(String carFile, String carLatitude, String carLongitude) {

        this.carFile = carFile;
        this.carLatitude = carLatitude;
        this.carLongitude = carLongitude;
    }

    public static void printCarGPSItem(CarGPSItem item){
        Log.d(TAG, "carFile: " +  item.carFile);
        Log.d(TAG, "carLat: " +  item.carLatitude);
        Log.d(TAG, "carLong: " +  item.carLongitude);
        Log.d(TAG, "--------------------------------");
    }

}
