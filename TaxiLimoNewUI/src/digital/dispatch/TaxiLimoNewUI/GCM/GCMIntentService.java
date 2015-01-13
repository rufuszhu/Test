/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package digital.dispatch.TaxiLimoNewUI.GCM;

import static digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities.*;


import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.android.gcm.GCMBaseIntentService;



/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService 
 {

	private static final String TAG = "GCMIntentService";
	private static int id = 0;
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static final int MAX_CAR_USER_DISTANCE = 1000; //meters TL-194
 
    
    double mobileLatitude = 0;
    double mobileLongitude = 0;
    private Context c;

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Logger.v(TAG, "Device registered: regId = " + registrationId);

		displayMessage(context, gcmType.register, registrationId, id);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Logger.v(TAG, "Device unregistered");
		// displayMessage(context, gcmType.unregister, getString(R.string.gcm_unregistered));
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		
		String message = intent.getStringExtra("message");
		c = context;
		Logger.v(TAG, "Received message " + message);
		if(!message.isEmpty()){
		
			if (checkDisplayMsg(context, message, intent)) {
				WakeLocker.acquire(context);
				displayMessage(context, gcmType.message, message, id);
				sendNotification(context, message);
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(500);
				WakeLocker.release();
			}
			
			
		}
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Logger.v(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		displayMessage(context, gcmType.deletedMsg, message, id);
		// notifies user
		sendNotification(context, message);
		
	}

	@Override
	public void onError(Context context, String errorId) {
		Logger.v(TAG, "Received error: " + errorId);
		// displayMessage(context, gcmType.error, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Logger.v(TAG, "Received recoverable error: " + errorId);
		// displayMessage(context, gcmType.recoverableError, getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}



	   
	private boolean checkDisplayMsg(Context ctx, String msg, Intent intent) {
		boolean isJobCancelled = false;
		boolean isCancelJobBySupervisor = false;
		boolean isJobPaid = false;
		boolean isPayable = true;
		boolean isMeterOn = false;
		String amt = "";
		String Lat = "";
		String Long = "";
		int trID = 0;
		int eventID = 0;

		Logger.v(TAG, "GCM msg: " + msg);
		
		trID = Integer.parseInt(intent.getStringExtra("tId"));
		eventID = Integer.parseInt(intent.getStringExtra("eId"));
		
		switch(eventID){
			//TL-194 added driver initiated payment support
			case MBDefinition.FARE_EVENT:
				amt = intent.getStringExtra("amt");
				Lat = intent.getStringExtra("Lat");
				Long = intent.getStringExtra("Long");
				Logger.v(TAG, "trip fare:" + trID + " amount: " + amt + " Lat: " + Lat + " Long: " + Long);
							
				if(Lat != null && Long != null){
					
					double carLat = Double.parseDouble(Lat);
					double carLong = Double.parseDouble(Long);
								
					//measure the car vs mobile's gps to make sure not too far off
					isPayable = isRightPayTrip(trID, carLat, carLong);
				
				}
				
				break;
			case MBDefinition.LATE_TRIP_EVENT:
				Logger.d(TAG, "late trip:" + trID);
				SharedPreferences sharedPref = ctx.getSharedPreferences("mobile_booker", 0);
				SharedPreferences.Editor prefEditor = sharedPref.edit();
				String curLateTripTRID = trID + "";
				String prevLateTripTRID = sharedPref.getString(LATE_TRIP_TRID, "");

				if (!prevLateTripTRID.equalsIgnoreCase("")) {
					curLateTripTRID = prevLateTripTRID + "," + curLateTripTRID;
				}

				prefEditor.putString(LATE_TRIP_TRID, curLateTripTRID);
				prefEditor.commit();
				break;
			case MBDefinition.CANCELLED_EVENT:
			case MBDefinition.FORCED_COMPLETE_EVENT:
			
				isCancelJobBySupervisor = true;
				break;
			case MBDefinition.METER_ON_EVENT:
				isMeterOn = true;
				Logger.v(TAG, "Meter On:" + trID);
				break;
			default:
				break;
				
				
		}
		
		if(trID != 0){
			DaoManager daoManager = DaoManager.getInstance(ctx);
			DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
			final DBBooking dbBook = bookingDao.queryBuilder().where(Properties.Taxi_ride_id.eq(trID)).list().get(0);
	
			if (dbBook != null) {
				if (dbBook.getTripStatus() == MBDefinition.MB_STATUS_CANCELLED) {
					isJobCancelled = true;
				}
	
				if (isCancelJobBySupervisor) {
					dbBook.setTripStatus(MBDefinition.MB_STATUS_CANCELLED);
					bookingDao.update(dbBook);
				}
				//TL-200 use paid amount field to store driver requested fare when already_paid is set to false
				if(isPayable && !dbBook.getAlready_paid() && !amt.isEmpty()){
					
					dbBook.setPaidAmount(amt);
					bookingDao.update(dbBook);
					
				}else if (dbBook.getAlready_paid() && !amt.isEmpty()){
					isJobPaid = true;
				}
				//TL-255
				if(isMeterOn){
					dbBook.setTripStatus(MBDefinition.MB_STATUS_IN_SERVICE);
					bookingDao.update(dbBook);
				}
			}
		}

		if (isJobCancelled ) {
			return false;
		} else if(isJobPaid){
			return false;
		} else if (!isPayable){
			return false;
		}else if (isMeterOn){
			return false;
		}
		else {
			return true;
		}
	}
	//TL-194 helper function to check the distance between car and mobile based on on GPS
	private boolean isRightPayTrip(int tripId, double carLatitude, double carLongitude){
		
		boolean res = false;
		//no car GPS allow pay
		if(carLatitude == 0 || carLongitude == 0){
			res = true;
		}
		
		Location locationMobile = getBestLocation();
		//no mobile GPS allow pay
		if(locationMobile.getLatitude() == 0 || locationMobile.getLongitude() == 0){
			res = true;
		}
		
		// mobile GPS and Car GPS within 100 meters allow pay
		Location locationCar = new Location("Car");
		locationCar.setLatitude(carLatitude);
		locationCar.setLongitude(carLongitude);
		
		
		Logger.v(TAG, "mobile GPS: " + locationMobile.getLatitude() + "," + locationMobile.getLongitude());
		double distanceMeters = locationCar.distanceTo(locationMobile);
		Logger.v(TAG, "distance:" + distanceMeters);
		if(distanceMeters < MAX_CAR_USER_DISTANCE){ //this meter should be configurable later
			res = true;
		}
		
		
		return res;
	}
	
	//TL-194 added this method to get mobile gps 
	//should move to some utility later if used by more than one location
	private Location getBestLocation() {
		Location gpsLocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
		Location networkLocation = getLocationByProvider(LocationManager.NETWORK_PROVIDER);
		Location tmpLocation;

		if (gpsLocation == null) {
			Logger.v(TAG, "No GPS Location available.");
			return networkLocation;
		}

		if (networkLocation == null) {
			Logger.v(TAG, "No Network Location available");
			return gpsLocation;
		}
	
		Logger.v(TAG, "GPS location:");
		Logger.v(TAG, " accurate=" + gpsLocation.getAccuracy() + " time=" + gpsLocation.getTime());
		Logger.v(TAG, "Netowrk location:");
		Logger.v(TAG, " accurate=" + networkLocation.getAccuracy() + " time=" + networkLocation.getTime());
		
		if (gpsLocation.getAccuracy() < networkLocation.getAccuracy()) {
				Logger.v(TAG, "use GPS location");
				tmpLocation = gpsLocation;

		} else {
			Logger.v(TAG, "use networkLocation");
			tmpLocation = networkLocation;
		}
		return tmpLocation;

	}

	
	private Location getLocationByProvider(String provider) {
		Location location = null;

		LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

		try {
			if (locationManager.isProviderEnabled(provider)) {
				location = locationManager.getLastKnownLocation(provider);
			}
		} catch (IllegalArgumentException e) {
			Logger.d(TAG, "Cannot acces Provider " + provider);
		}

		return location;
	}
	
	

	
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Context context, String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_notification).setAutoCancel(true)
        .setContentTitle(context.getString(R.string.app_name))
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}