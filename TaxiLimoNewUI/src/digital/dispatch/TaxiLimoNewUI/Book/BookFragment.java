package digital.dispatch.TaxiLimoNewUI.Book;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.AddressDaoManager;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadImageTask;
import digital.dispatch.TaxiLimoNewUI.Task.GetCompanyListTask;
import digital.dispatch.TaxiLimoNewUI.Utils.ErrorDialogFragment;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class BookFragment extends Fragment implements OnConnectionFailedListener, LocationListener, OnCameraChangeListener {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */

	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String TAG = "Book";
	private static View view;

	private GoogleMap mMap;


	private TextView address_bar_text;
	private TextView book_btn;

	private boolean noCompanyAvailable = false;

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	public static BookFragment newInstance() {
		BookFragment fragment = new BookFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, 1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// only show refresh if drawer is not open.
		if (!((MainActivity) getActivity()).getDrawerFragment().isDrawerOpen())
			inflater.inflate(R.menu.main, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.e(TAG, "on CREATEVIEW");

		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.fragment_book, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}

		address_bar_text = (TextView) view.findViewById(R.id.text_address);


		LinearLayout current_location_btn = (LinearLayout) view.findViewById(R.id.my_location_btn);
		current_location_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onCurrentLocationClick();
			}
		});

		LinearLayout addressBar = (LinearLayout) view.findViewById(R.id.addressBar);
		addressBar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ModifyAddressActivity.class);

				intent.putExtra(MBDefinition.ADDRESSBAR_TEXT_EXTRA, address_bar_text.getText().toString());

				intent.putExtra(MBDefinition.IS_DESTINATION, false);
				getActivity().startActivityForResult(intent, MBDefinition.REQUEST_PICKUPADDRESS_CODE);
			}
		});


		
		ImageView blue_pin =  (ImageView) view.findViewById(R.id.blue_pin);
		blue_pin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), BookActivity.class);
				getActivity().startActivity(intent);
			}
		});


		return view;
	}

	private boolean validateHasHouseNumber() {
		Address address = Utils.mPickupAddress;
		if (address == null)
			return false;
		String ad = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";
		String[] strArray = TextUtils.split(ad, " ");

		if (!Utils.isNumeric(strArray[0]) && Utils.pickupHouseNumber.equals("")) {

			return false;
		} else
			return true;
	}

	private boolean validateHouseNumberHasNoRange() {
		Address address = Utils.mPickupAddress;
		String ad = AddressDaoManager.getHouseNumberFromAddress(address);
		if (ad.contains("-") && Utils.pickupHouseNumber.equals("")) {
			return false;
		} else
			return true;
	}

	private void showEnterHouseNumberDialog() {
		final Dialog enterHouseNumDialog = new Dialog(getActivity());
		enterHouseNumDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		enterHouseNumDialog.setContentView(R.layout.dialog_enter_street_number);
		enterHouseNumDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		TextView tv_house_num = (TextView) enterHouseNumDialog.getWindow().findViewById(R.id.tv_street_address);
		final EditText et_house_num = (EditText) enterHouseNumDialog.getWindow().findViewById(R.id.et_street_number);
		TextView ok = (TextView) enterHouseNumDialog.getWindow().findViewById(R.id.ok);
		TextView cancel = (TextView) enterHouseNumDialog.getWindow().findViewById(R.id.cancel);

		tv_house_num.setText(LocationUtils.addressToString(getActivity(), Utils.mPickupAddress));
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (et_house_num.getText().toString().equals("")) {
					et_house_num.setError("Pleace enter a street number for pick up");
				} else if (et_house_num.getText().toString().length() > MBDefinition.STREET_NUMBER_MAX_LENGTH)
					et_house_num.setError("Street number too long");
				else {
					Utils.pickupHouseNumber = et_house_num.getText().toString();
					enterHouseNumDialog.dismiss();
					book_btn.callOnClick();
				}
			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				enterHouseNumDialog.dismiss();
			}
		});
		enterHouseNumDialog.setCanceledOnTouchOutside(true);
		enterHouseNumDialog.show();
	}

	private void showHouseRangeDialog(final String start, final String end) {
		final Dialog houseNumRangeDialog = new Dialog(getActivity());
		houseNumRangeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		houseNumRangeDialog.setContentView(R.layout.dialog_enter_street_number_range);
		houseNumRangeDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		TextView tv_range_message = (TextView) houseNumRangeDialog.getWindow().findViewById(R.id.tv_range_message);
		final EditText et_house_num = (EditText) houseNumRangeDialog.getWindow().findViewById(R.id.et_street_number);
		TextView ok = (TextView) houseNumRangeDialog.getWindow().findViewById(R.id.ok);
		TextView cancel = (TextView) houseNumRangeDialog.getWindow().findViewById(R.id.cancel);

		tv_range_message.setText(start + " and " + end);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (et_house_num.getText().toString().equals("")) {
					et_house_num.setError("Pleace enter a street number for pick up");
				} else if (Integer.parseInt(et_house_num.getText().toString()) > Integer.parseInt(end)
						|| Integer.parseInt(et_house_num.getText().toString()) < Integer.parseInt(start))
					et_house_num.setError("Street number not in range");
				else {
					Utils.pickupHouseNumber = et_house_num.getText().toString();
					houseNumRangeDialog.dismiss();
					book_btn.callOnClick();
				}
			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				houseNumRangeDialog.dismiss();
			}
		});
		houseNumRangeDialog.setCanceledOnTouchOutside(true);
		houseNumRangeDialog.show();
	}


	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
		checkGPSEnable();
		checkInternet();
		setUpMapIfNeeded();

		Location currentLocation = getBestLocation();
		Address address = Utils.mPickupAddress;
		if (address != null) {
			address_bar_text.setText(LocationUtils.addressToString(getActivity(), address));
			if (checkReady()) {
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LocationUtils.addressToLatLng(address), MBDefinition.DEFAULT_ZOOM));
			}
			// boolean isFromBooking = true;
			// new GetCompanyListTask(getActivity(), address, isFromBooking).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			// setupCompanyUI();
		} else {
			if (checkReady() && currentLocation != null) {
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LocationUtils.locationToLatLng(currentLocation), MBDefinition.DEFAULT_ZOOM));
			}
		}


	}

	@Override
	public void onPause() {
		super.onPause();
		Logger.e(TAG, "on PAUSE");
	}

	private void checkGPSEnable() {
		final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
		}
	}

	private void checkInternet() {
		Utils.isInternetAvailable(getActivity());
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
			mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(false);
				mMap.getUiSettings().setZoomControlsEnabled(false);
				mMap.setOnCameraChangeListener(this);
			}
		}
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

	// @Override
	// public void onDisconnected() {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		Location location = new Location("");
		location.setLatitude(cameraPosition.target.latitude);
		location.setLongitude(cameraPosition.target.longitude);

		getAddress(location);
		Utils.pickupHouseNumber = "";
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

	private void onCurrentLocationClick() {

		// Get the current location
		Location currentLocation = getBestLocation();
		if (currentLocation != null) {
			LatLng currLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			// mCurrentLocation = currentLocation;

			CameraPosition currentPosition = new CameraPosition.Builder().target(currLatLng).zoom(MBDefinition.DEFAULT_ZOOM).bearing(0).tilt(0).build();
			CameraUpdate locatoinUpdate = CameraUpdateFactory.newCameraPosition(currentPosition);
			mMap.animateCamera(locatoinUpdate);
		}
		else{
			Toast.makeText(getActivity(), R.string.err_no_current_location, Toast.LENGTH_LONG).show();
		}

	}

	// For Eclipse with ADT, suppress warnings about Geocoder.isPresent()
	@SuppressLint("NewApi")
	public void getAddress(Location location) {

		// In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
			// No geocoder is present. Issue an error message
			Toast.makeText(getActivity(), R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
			return;
		}

		new GetAddressTask(getActivity()).execute(location);
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

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);

			// Show the error dialog in the DialogFragment
			errorFragment.show(getActivity().getSupportFragmentManager(), LocationUtils.APPTAG);
		}
	}

	/**
	 * An AsyncTask that calls getFromLocation() in the background. The class uses the following generic types: Location - A {@link android.location.Location}
	 * object containing the current location, passed as the input parameter to doInBackground() Void - indicates that progress units are not used by this
	 * subclass String - An address passed to onPostExecute()
	 */
	protected class GetAddressTask extends AsyncTask<Location, Void, String> {

		// Store the context passed to the AsyncTask when the system instantiates it.
		Context localContext;

		// Constructor called by the system to instantiate the task
		public GetAddressTask(Context context) {

			// Required by the semantics of AsyncTask
			super();

			// Set a Context for the background task
			localContext = context;
		}

		/**
		 * Get a geocoding service instance, pass latitude and longitude to it, format the returned address, and return the address to the UI thread.
		 */
		@Override
		protected String doInBackground(Location... params) {
			/*
			 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform
			 * to address standards can also be used.
			 */
			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

			// Get the current location from the input parameter list
			Location location = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;

			// Try to get an address for the current location. Catch IO or network problems.
			try {

				/*
				 * Call the synchronous getFromLocation() method with the latitude and longitude of the current location. Return at most 1 address.
				 */
				addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

				// Catch network or other I/O problems.
			} catch (IOException exception1) {

				// Log an error and return an error message
				// Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));

				// print the stack trace
				exception1.printStackTrace();

				// Return an error message
				return (getString(R.string.IO_Exception_getFromLocation));

				// Catch incorrect latitude or longitude values
			} catch (IllegalArgumentException exception2) {

				// Construct a message containing the invalid arguments
				String errorString = getString(R.string.illegal_argument_exception, location.getLatitude(), location.getLongitude());
				// Log the error and print the stack trace
				Log.e(LocationUtils.APPTAG, errorString);
				exception2.printStackTrace();

				//
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {

				// Get the first address
				Address address = addresses.get(0);
				Utils.mPickupAddress = address;

				return LocationUtils.addressToString(getActivity(), address);

				// If there aren't any addresses, post a message
			} else {
				return getString(R.string.no_address_found);
			}
		}

		/**
		 * A method that's called once doInBackground() completes. Set the text of the UI element that displays the address. This method runs on the UI thread.
		 */
		@Override
		protected void onPostExecute(String address) {
			address_bar_text.setText(address);
		}
	}

}