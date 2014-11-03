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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	private static int id = 0;
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

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
		Logger.v(TAG, "Received message");
		String message = intent.getStringExtra("message");

		if (checkDisplayMsg(context, message)) {
			WakeLocker.acquire(context);
			displayMessage(context, gcmType.message, message, id);
			sendNotification(context, message);
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(500);
			WakeLocker.release();
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

	private boolean checkDisplayMsg(Context ctx, String msg) {
		boolean isJobCancelled = false;
		boolean isCancelJobBySupervisor = false;

		Logger.v(TAG, "GCM msg: " + msg);

		if (msg.contains("Trip")) {
			int trID = 0;
			String delims = "[ ]";
			String[] tokens = msg.split(delims);

			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].contains("Trip") && (i + 1) < tokens.length) {
					Pattern pattern = Pattern.compile("\\d+");
					Matcher matcher = pattern.matcher(tokens[i + 1]);

					if (matcher.find()) {
						trID = Integer.parseInt(matcher.group(0));
					}
				}

				if (tokens[i].contains("cancelled")) {
					isCancelJobBySupervisor = true;
				}
			}

			if (trID != 0) {
				if (msg.contains(getResources().getString(R.string.gcm_late_trip_format))) {
					Logger.e(TAG, "late trip:" + trID);
					SharedPreferences sharedPref = ctx.getSharedPreferences("mobile_booker", 0);
					SharedPreferences.Editor prefEditor = sharedPref.edit();
					String curLateTripTRID = trID + "";
					String prevLateTripTRID = sharedPref.getString(LATE_TRIP_TRID, "");

					if (!prevLateTripTRID.equalsIgnoreCase("")) {
						curLateTripTRID = prevLateTripTRID + "," + curLateTripTRID;
					}

					prefEditor.putString(LATE_TRIP_TRID, curLateTripTRID);
					prefEditor.commit();
				}

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
				}

			}
		}

		if (isJobCancelled) {
			return false;
		} else {
			return true;
		}
	}

//	/**
//	 * Issues a notification to inform the user that server has sent a message.
//	 */
//	@SuppressLint("NewApi")
//	private static void generateNotification(Context context, String message) {
//		if (id > 2000000000) {
//			id = 0;
//		}
//		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//		Intent notificationIntent = new Intent(context, MainActivity.class);
//		PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
//
//		Notification noti = new Notification.Builder(context).setContentTitle(context.getString(R.string.app_name)).setSmallIcon(R.drawable.ic_launcher).setDefaults(Notification.DEFAULT_SOUND)
//				.setAutoCancel(true).setContentIntent(intent).setContentText(context.getResources().getString(R.string.gcm_short_content_text))
//				// .setStyle(new
//				// Notification.BigTextStyle().bigText("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"))
//				// .setStyle(new
//				// Notification.BigTextStyle().bigText("Hello how are you doing today? I hope you're well. Can I schedule a meeting with you today. The weather is great today. Are you planning any vacation soon? How are you kids doing lately? I hope they'r"))
//				.setStyle(new Notification.BigTextStyle().bigText(message)).build();
//
//		notificationManager.notify(id, noti);
//
//		id++;
//	}
	
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