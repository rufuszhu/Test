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

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.CancelJobTask;
import digital.dispatch.TaxiLimoNewUI.Track.TrackDetailActivity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {
	/**
     * Type of intent used to send message to main activity.
     */
	public static enum gcmType {
	    register, unregister, message, deletedMsg, error, recoverableError, refreshTracking
	}

    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "594999606916";

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "GCM";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";

    /**
     * Intent's extra that contains the name of the Application.
     */
    public static final String APP_NAME = "appName";
    
    /**
     * Intent's extra that contains the ID for GCM notification.
     */
    public static final String GCM_ID = "gcmID";
    
    /**
     * Intent's extra that contains the taxi ride ID of the late trips.
     */
    public static final String LATE_TRIP_TRID = "lateTripTRID";
    
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, gcmType type, String message, int gcmID) {
        Intent intent = new Intent(type.toString());
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(GCM_ID, gcmID);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    
    public static void lateTripNotification(final Context context, final String trID, final int gcmID) {
    	String msg = context.getResources().getString(R.string.late_trip_msg) + " (Trip " + trID + ")";
    	new AlertDialog.Builder(context)
    	.setTitle(R.string.late_trip)
    	.setMessage(msg)
    	.setCancelable(false)
    	.setPositiveButton(R.string.late_trip_wait, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (gcmID != -1) {
					removeNotification(gcmID, context);
				}
			}
    	})
    	.setNegativeButton(R.string.late_trip_no_wait, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DaoManager daoManager = DaoManager.getInstance(context);
    			DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
    			final DBBooking dbBook = bookingDao.queryBuilder().where(Properties.Taxi_ride_id.eq(trID)).list().get(0);
				new CancelJobTask(context,dbBook).execute();
				
				if (gcmID != -1) {
					removeNotification(gcmID, context);
				}
			}
    	})
    	.show();
	}
    
    public static boolean checkLateTrip(Context ctx, int gcmID) {
    	SharedPreferences sharedPref = ctx.getSharedPreferences("mobile_booker", 0);
		SharedPreferences.Editor prefEditor = sharedPref.edit();
		String LateTripTRID = sharedPref.getString(LATE_TRIP_TRID, "");
		
		if (!LateTripTRID.equalsIgnoreCase("")) {
			String[] lateTRIDList = LateTripTRID.split(",");
			
			for (int i = 0; i < lateTRIDList.length; i ++) {
				lateTripNotification(ctx, lateTRIDList[i], gcmID);
			}
			
			prefEditor.putString(LATE_TRIP_TRID, "");
		    prefEditor.commit();
			return true;
		}
		
		return false;
    }
    
    public static BroadcastReceiver getGenericReceiver(final Context ctx, final boolean isTrackDetail) {
	    final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	        	if (intent.filterEquals(new Intent(gcmType.message.toString()))) {
	        		// get gcm notification ID to remove notification once read by user
	        		String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
	        		final int gcmID = intent.getIntExtra(GCM_ID, -1);
	        		
	        		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
	                builder.setTitle(R.string.gcm);
			        builder.setMessage(newMessage);
			        builder.setCancelable(false);
			        
	                // Create the AlertDialog object and return it
	                 builder.create();
	        		
	        		// clear late trip notification list once displayed
	        		SharedPreferences sharedPref = ctx.getSharedPreferences("mobile_booker", 0);
	        		SharedPreferences.Editor prefEditor = sharedPref.edit();
	        		final String lateID = sharedPref.getString(LATE_TRIP_TRID, "");
	        		
	        		if (!lateID.equalsIgnoreCase("")) {
	        			DaoManager daoManager = DaoManager.getInstance(ctx);
	        			DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
	        			final DBBooking dbBook = bookingDao.queryBuilder().where(Properties.Taxi_ride_id.eq(lateID)).list().get(0);
	        			
	        			builder.setPositiveButton(R.string.late_trip_wait, new DialogInterface.OnClickListener() {
	        				@Override
	        				public void onClick(DialogInterface dialog, int which) {
	        					removeNotification(gcmID, ctx);
	        								
	        				}
	        	    	})
	        	    	.setNegativeButton(R.string.late_trip_no_wait, new DialogInterface.OnClickListener() {
	        				@Override
	        				public void onClick(DialogInterface dialog, int which) {
	        					new CancelJobTask(ctx,dbBook).execute();
	        					removeNotification(gcmID, ctx);
	        				}
	        	    	});
	        			
	        			prefEditor.putString(LATE_TRIP_TRID, "");
		        	    prefEditor.commit();
	        		}
	        		else {
	        			builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								removeNotification(gcmID, ctx);
								if(isTrackDetail)
	        						((TrackDetailActivity)ctx).startRecallJobTask(); 
							}
		    			});
	        		}
	        		
	        		builder.create().show();
	        	}
	        }
	    };
	    
	    return mHandleMessageReceiver;
    }
    
    private static void removeNotification(int gcmID, Context ctx) {
    	NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		
		if (gcmID != -1) {
			notificationManager.cancel(gcmID);
		}
    }
}