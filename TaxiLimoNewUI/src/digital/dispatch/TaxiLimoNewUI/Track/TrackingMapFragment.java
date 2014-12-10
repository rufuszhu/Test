package digital.dispatch.TaxiLimoNewUI.Track;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.RecallJobTask;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;

public class TrackingMapFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	private static final String TAG = "TrackingMapFragment";
	private SupportMapFragment fragment;
	private GoogleMap map;
	private LocationClient mLocationClient;
	private View view;

	private DBBooking dbBook;
	private LatLng pickupLatLng;
	private LatLng carLatLng;
	private Marker pickupMarker;
	private Marker carMarker;

	private Runnable mHandlerTask;
	private Handler mHandler;
	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	private final static int INTERVAL = 1000 * 30; // 30 second

	public static TrackingMapFragment newInstance() {
		TrackingMapFragment fragment = new TrackingMapFragment();

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_map, container, false);
		dbBook = ((TrackDetailActivity) getActivity()).getDBBook();
		pickupLatLng = new LatLng(dbBook.getPickup_latitude(), dbBook.getPickup_longitude());

		mHandler = new Handler();
		mHandlerTask = new Runnable() {
			@Override
			public void run() {
				// Toast.makeText(_context, "getting location", Toast.LENGTH_SHORT).show();
				startRecallJobTask();
				mHandler.postDelayed(mHandlerTask, INTERVAL);
			}
		};

		return view;
	}

	private void startRecallJobTask() {
		new RecallJobTask(getActivity(), dbBook.getTaxi_ride_id().toString(), MBDefinition.IS_FOR_MAP).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
				dbBook.getDestID(), dbBook.getSysId());
	}

	public View mGetView() {
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map, fragment).commit();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (map == null) {
			map = fragment.getMap();
			map.setMyLocationEnabled(true);
		}
		setUpLocationClientIfNeeded();
		mLocationClient.connect();

		startRepeatingTask();
	}

	@Override
	public void onPause() {
		super.onPause();
		Logger.e(TAG, "on PAUSE");
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
		stopRepeatingTask();
	}

	private void startRepeatingTask() {
		mHandlerTask.run();
	}

	private void stopRepeatingTask() {
		mHandler.removeCallbacks(mHandlerTask);
	}

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
		Logger.v(TAG, "   accurate=" + gpsLocation.getAccuracy() + " time=" + gpsLocation.getTime());
		Logger.v(TAG, "Netowrk location:");
		Logger.v(TAG, "   accurate=" + networkLocation.getAccuracy() + " time=" + networkLocation.getTime());

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

		LocationManager locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

		try {
			if (locationManager.isProviderEnabled(provider)) {
				location = locationManager.getLastKnownLocation(provider);
			}
		} catch (IllegalArgumentException e) {
			Logger.d(TAG, "Cannot acces Provider " + provider);
		}

		return location;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getActivity(), this, this); // OnConnectionFailedListener
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(LocationUtils.locationToLatLng(mLocationClient.getLastLocation()), MBDefinition.DEFAULT_ZOOM));
		pickupMarker = map.addMarker(new MarkerOptions().position(pickupLatLng).draggable(false));
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error has a resolution, try sending an Intent to start a Google Play services
		 * activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(getActivity(), LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original PendingIntent
				 */

			} catch (IntentSender.SendIntentException e) {

				// Log the error
				e.printStackTrace();
			}
		} else {

			// If no resolution is available, display a dialog to the user with the error.
			showErrorDialog(connectionResult.getErrorCode());
		}

	}

	public void toggleCamera() {
		// toggle between focus on taxi and fit both taxi location and pickup location
		if (map != null && map.getCameraPosition().zoom != MBDefinition.DEFAULT_ZOOM && carLatLng!=null) {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(carLatLng, MBDefinition.DEFAULT_ZOOM));
		} else if (map != null && carMarker != null && pickupMarker != null) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();

			builder.include(carMarker.getPosition());
			builder.include(pickupMarker.getPosition());

			LatLngBounds bounds = builder.build();

			int padding = 150; // offset from edges of the map in pixels
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
			map.moveCamera(cu);
		}
	}

	// get called from recall job task
	public void updateCarMarker(LatLng carLatLng, DBBooking book) {
		if (isAdded()) {
			this.dbBook = book;
			if (carLatLng == null || (dbBook.getCarLatitude() == 0 && dbBook.getCarLongitude() == 0)) {
				if (((TrackDetailActivity) getActivity()).mPager.getCurrentItem() == 1)
					Toast.makeText(getActivity(), "Car Location not availabe", Toast.LENGTH_LONG).show();
				return;
			}

			this.carLatLng = carLatLng;
			if (carMarker != null)
				carMarker.remove();
			if (dbBook.getTripStatus() == MBDefinition.MB_STATUS_ARRIVED || dbBook.getTripStatus() == MBDefinition.MB_STATUS_ACCEPTED
					|| dbBook.getTripStatus() == MBDefinition.MB_STATUS_IN_SERVICE) {

				// TL-222 load car icon based on company car file color
				String carFile = dbBook.getCompany_car_file();
				BitmapDescriptor icon = null;
				if (carFile == null || carFile.isEmpty()) {
					icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_track_taxi_yellow); // default
				} else if (MBDefinition.ICON_TRACK_TAXI_BLUE.equalsIgnoreCase(carFile)) {
					icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_track_taxi_blue);
				} else if (MBDefinition.ICON_TRACK_TAXI_RED.equalsIgnoreCase(carFile)) {
					icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_track_taxi_red);
				} else if (MBDefinition.ICON_TRACK_TAXI_GREEN.equalsIgnoreCase(carFile)) {
					icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_track_taxi_green);
				} else if (MBDefinition.ICON_TRACK_TAXI_ORANGE.equalsIgnoreCase(carFile)) {
					icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_track_taxi_orange);
				} else {
					icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_track_taxi_yellow);
				}

				carMarker = map.addMarker(new MarkerOptions().position(carLatLng).draggable(false).icon(icon));
			}
		}
	}

	/**
	 * Show a dialog returned by Google Play services for the connection error code
	 * 
	 * @param errorCode
	 *            An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(), LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {
			errorDialog.show();
		}
	}

}
