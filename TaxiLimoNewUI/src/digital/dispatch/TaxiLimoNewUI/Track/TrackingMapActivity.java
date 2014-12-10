package digital.dispatch.TaxiLimoNewUI.Track;

import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities.gcmType;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadImageTask;
import digital.dispatch.TaxiLimoNewUI.Task.RecallJobTask;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;

public class TrackingMapActivity extends android.support.v4.app.FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnCameraChangeListener {
	private GoogleMap mMap;
	private LocationClient mLocationClient;

	private ImageView iv_company_logo;

	private TextView tv_car_num;
	private TextView tv_driver_id;
	private TextView tv_status;
	private ImageView zoom_btn;

	private DBBooking dbBook;
	private LatLng pickupLatLng;
	private LatLng carLatLng;
	private Marker pickupMarker;
	private Marker carMarker;

	private Runnable mHandlerTask;
	private Handler mHandler;
	private Context _context;
	private DBBookingDao bookingDao;
	private boolean isRefreshing;
	private MenuItem refresh_icon;
	private BroadcastReceiver bcReceiver;
	// These settings are the same as the settings for the map. They will in fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	private static final String TAG = "TrackingMapActivity";
	private final static int INTERVAL = 1000 * 30; // 30 second

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracking_map);
		_context = this;
		isRefreshing = false;
		findView();
		bindView();
		bindEvent();
		pickupLatLng = new LatLng(dbBook.getPickup_latitude(), dbBook.getPickup_longitude());
		mHandler = new Handler();
		mHandlerTask = new Runnable() {
			@Override
			public void run() {
				//Toast.makeText(_context, "getting location", Toast.LENGTH_SHORT).show();
				startRecallJobTask();
				mHandler.postDelayed(mHandlerTask, INTERVAL);
			}
		};
		DaoManager daoManager = DaoManager.getInstance(_context);
		bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);

	}

	private void findView() {
		iv_company_logo = (ImageView) findViewById(R.id.iv_company_logo);
		// tv_company_name = (TextView) findViewById(R.id.tv_company_name);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_car_num = (TextView) findViewById(R.id.tv_car_num);
		tv_driver_id = (TextView) findViewById(R.id.tv_driver_id);
		zoom_btn = (ImageView) findViewById(R.id.zoom_btn);
	}

	private void bindView() {
		dbBook = (DBBooking) getIntent().getSerializableExtra(MBDefinition.DBBOOKING_EXTRA);
		// download company logo
		String prefixURL = this.getString(R.string.url);
		prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
		new DownloadImageTask(iv_company_logo).execute(prefixURL + dbBook.getCompany_icon());

		// tv_company_name.setText(dbBook.getCompany_name());
		tv_car_num.setText(dbBook.getDispatchedCar());
		tv_driver_id.setText(dbBook.getDispatchedDriver());
		tv_status.setText("Status: " + dbBook.getTripStatus());
	}

	private void bindEvent() {
		zoom_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (checkReady() && mMap.getCameraPosition().zoom != MBDefinition.DEFAULT_ZOOM) {
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(carLatLng, MBDefinition.DEFAULT_ZOOM));
				} else if (checkReady() && carMarker != null && pickupMarker != null) {
					LatLngBounds.Builder builder = new LatLngBounds.Builder();

					builder.include(carMarker.getPosition());
					builder.include(pickupMarker.getPosition());

					LatLngBounds bounds = builder.build();

					int padding = 150; // offset from edges of the map in pixels
					CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
					mMap.moveCamera(cu);
				}
			}
		});

	}

	private void startRepeatingTask() {
		mHandlerTask.run();
	}

	private void stopRepeatingTask() {
		mHandler.removeCallbacks(mHandlerTask);
	}

	@Override
	public void onResume() {
		//TL-235
		boolean isTrackDetail = false;
		bcReceiver = CommonUtilities.getGenericReceiver(_context, isTrackDetail);
		LocalBroadcastManager.getInstance(this).registerReceiver(bcReceiver, new IntentFilter(gcmType.message.toString()));
	super.onResume();
		Logger.e(TAG, "on RESUME");
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();

		startRepeatingTask();
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(bcReceiver);
		super.onPause();
		Logger.e(TAG, "on PAUSE");
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
		stopRepeatingTask();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Logger.e(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.track_detail, menu);
		refresh_icon = menu.findItem(R.id.action_refresh);
		startRecallJobTask();

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			startRecallJobTask();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void startRecallJobTask() {
		if (refresh_icon != null && !isRefreshing)
			startUpdateAnimation(refresh_icon);
		new RecallJobTask(_context, dbBook.getTaxi_ride_id().toString(), MBDefinition.IS_FOR_MAP).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
				dbBook.getDestID(), dbBook.getSysId());
	}

	/**
	 * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on all entry points that call methods on the Google Maps API.
	 */
	private boolean checkReady() {
		if (mMap == null) {
			return false;
		}
		return true;
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(false);
				mMap.getUiSettings().setZoomControlsEnabled(false);
				mMap.setOnCameraChangeListener(this);
			}
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(this, this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener
		pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).draggable(false));
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {

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
				connectionResult.startResolutionForResult(this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

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

	/**
	 * Show a dialog returned by Google Play services for the connection error code
	 * 
	 * @param errorCode
	 *            An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {
			errorDialog.show();
		}
	}

	// get called from recall job task
	public void updateCarMarker(LatLng carLatLng, List<DBBooking> book) {
		stopUpdateAnimation();
		this.dbBook = book.get(0);
		if (carLatLng == null) {
			Toast.makeText(_context, "Car Location not availabe", Toast.LENGTH_LONG).show();
		}
		// first time, set up zoom level and camera location
		if (checkReady() && this.carLatLng == null) {
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(carLatLng, MBDefinition.DEFAULT_ZOOM));
		}

		this.carLatLng = carLatLng;
		if (carMarker != null)
			carMarker.remove();
		if (dbBook.getTripStatus() == MBDefinition.MB_STATUS_ARRIVED || dbBook.getTripStatus() == MBDefinition.MB_STATUS_ACCEPTED
				|| dbBook.getTripStatus() == MBDefinition.MB_STATUS_IN_SERVICE) {
			BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_track_taxi);
			carMarker = mMap.addMarker(new MarkerOptions().position(carLatLng).draggable(false).icon(icon));
		}
		updateStatus();
	}

	private void updateStatus() {

		if (dbBook.getTripStatus() == MBDefinition.MB_STATUS_COMPLETED) {
			tv_status.setText("Reached destination");
		} else if (dbBook.getTripStatus() == MBDefinition.MB_STATUS_CANCELLED) {
			tv_status.setText("Trip is Canceled");
		} else if (dbBook.getTripStatus() == MBDefinition.MB_STATUS_ACCEPTED) {
			tv_status.setText("Enroute to pickup");
		} else if (dbBook.getTripStatus() == MBDefinition.MB_STATUS_ARRIVED) {
			tv_status.setText("At Pickup");
		} else if (dbBook.getTripStatus() == MBDefinition.MB_STATUS_BOOKED) {
			tv_status.setText("Booked");
		} else if (dbBook.getTripStatus() == MBDefinition.MB_STATUS_IN_SERVICE) {
			tv_status.setText("Trip in Progress");
		}

	}

	private void startUpdateAnimation(MenuItem item) {
		// Do animation start
		isRefreshing = true;
		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView) inflater.inflate(R.layout.iv_refresh, null);
		Animation rotation = AnimationUtils.loadAnimation(_context, R.anim.rotate_refresh);
		rotation.setRepeatCount(Animation.INFINITE);
		iv.startAnimation(rotation);
		item.setActionView(iv);
	}

	public void stopUpdateAnimation() {
		isRefreshing = false;
		// Get our refresh item from the menu
		if (refresh_icon!= null && refresh_icon.getActionView() != null) {
			// Remove the animation.
			refresh_icon.getActionView().clearAnimation();
			refresh_icon.setActionView(null);
		}
	}
}
