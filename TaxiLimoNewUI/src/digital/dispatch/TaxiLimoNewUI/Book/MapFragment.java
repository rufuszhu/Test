//package digital.dispatch.TaxiLimoNewUI.Book;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Locale;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AutoCompleteTextView;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import digital.dispatch.TaxiLimoNewUI.R;
//import digital.dispatch.TaxiLimoNewUI.Book.BookFragment.GetAddressTask;
//import digital.dispatch.TaxiLimoNewUI.Book.ModifyAddressActivity.ValidateAddressTask;
//import digital.dispatch.TaxiLimoNewUI.DaoManager.AddressDaoManager;
//import digital.dispatch.TaxiLimoNewUI.Task.GetCompanyListTask;
//import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
//import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
//import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
//import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
//
//public class MapFragment extends Fragment implements OnCameraChangeListener {
//	private static final String TAG = "MapFragment";
//	private SupportMapFragment fragment;
//	private GoogleMap map;
//	private View view;
//
//	public static MapFragment newInstance() {
//		MapFragment fragment = new MapFragment();
//		return fragment;
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		view = inflater.inflate(R.layout.fragment_map, container, false);
//		return view;
//	}
//
//	public View mGetView() {
//		return view;
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		FragmentManager fm = getChildFragmentManager();
//		fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
//		if (fragment == null) {
//			fragment = SupportMapFragment.newInstance();
//			fm.beginTransaction().replace(R.id.map, fragment).commit();
//		}
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		if (map == null) {
//			map = fragment.getMap();
//			map.setOnCameraChangeListener(this);
//		}
//		
//		callGetLatLngTask();
//	}
//	
//	private Location getBestLocation() {
//		Location gpsLocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
//		Location networkLocation = getLocationByProvider(LocationManager.NETWORK_PROVIDER);
//		Location tmpLocation;
//
//		if (gpsLocation == null) {
//			Logger.v(TAG, "No GPS Location available.");
//			return networkLocation;
//		}
//
//		if (networkLocation == null) {
//			Logger.v(TAG, "No Network Location available");
//			return gpsLocation;
//		}
//
//		Logger.v(TAG, "GPS location:");
//		Logger.v(TAG, "   accurate=" + gpsLocation.getAccuracy() + " time=" + gpsLocation.getTime());
//		Logger.v(TAG, "Netowrk location:");
//		Logger.v(TAG, "   accurate=" + networkLocation.getAccuracy() + " time=" + networkLocation.getTime());
//
//		if (gpsLocation.getAccuracy() < networkLocation.getAccuracy()) {
//			Logger.v(TAG, "use GPS location");
//			tmpLocation = gpsLocation;
//
//		} else {
//			Logger.v(TAG, "use networkLocation");
//			tmpLocation = networkLocation;
//		}
//		return tmpLocation;
//
//	}
//
//	private Location getLocationByProvider(String provider) {
//		Location location = null;
//
//		LocationManager locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//
//		try {
//			if (locationManager.isProviderEnabled(provider)) {
//				location = locationManager.getLastKnownLocation(provider);
//			}
//		} catch (IllegalArgumentException e) {
//			Logger.d(TAG, "Cannot acces Provider " + provider);
//		}
//
//		return location;
//	}
//
//	@Override
//	public void onCameraChange(CameraPosition cameraPosition) {
//		Location location = new Location("");
//		location.setLatitude(cameraPosition.target.latitude);
//		location.setLongitude(cameraPosition.target.longitude);
//
//		getAddress(location);
//		Utils.pickupHouseNumber = "";
//	}
//
//	// For Eclipse with ADT, suppress warnings about Geocoder.isPresent()
//	@SuppressLint("NewApi")
//	public void getAddress(Location location) {
//
//		// In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
//			// No geocoder is present. Issue an error message
//			Toast.makeText(getActivity(), R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
//			return;
//		}
//
//		new GetAddressTask(getActivity()).execute(location);
//	}
//	
//	public void callGetLatLngTask(){
//		if(getStreetNumberText().length()>0)
//			new GetLatLngTask(getActivity()).execute(getStreetNumberText() + " " + getStreetNameText());
//		else
//			new GetLatLngTask(getActivity()).execute(getStreetNameText());
//	}
//	
//	private String getStreetNumberText(){
//		return ((ModifyAddressActivity) getActivity()).tv_streetNumber.getText().toString();
//	}
//	
//	private String getStreetNameText(){
//		return ((ModifyAddressActivity) getActivity()).autoCompView.getText().toString();
//	}
//
//	/**
//	 * An AsyncTask that calls getFromLocation() in the background. The class uses the following generic types: Location - A {@link android.location.Location}
//	 * object containing the current location, passed as the input parameter to doInBackground() Void - indicates that progress units are not used by this
//	 * subclass String - An address passed to onPostExecute()
//	 */
//	protected class GetAddressTask extends AsyncTask<Location, Void, String> {
//
//		// Store the context passed to the AsyncTask when the system instantiates it.
//		Context localContext;
//
//		// Constructor called by the system to instantiate the task
//		public GetAddressTask(Context context) {
//
//			// Required by the semantics of AsyncTask
//			super();
//
//			// Set a Context for the background task
//			localContext = context;
//		}
//
//		/**
//		 * Get a geocoding service instance, pass latitude and longitude to it, format the returned address, and return the address to the UI thread.
//		 */
//		@Override
//		protected String doInBackground(Location... params) {
//			/*
//			 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform
//			 * to address standards can also be used.
//			 */
//			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());
//
//			// Get the current location from the input parameter list
//			Location location = params[0];
//
//			// Create a list to contain the result address
//			List<Address> addresses = null;
//
//			// Try to get an address for the current location. Catch IO or network problems.
//			try {
//
//				/*
//				 * Call the synchronous getFromLocation() method with the latitude and longitude of the current location. Return at most 1 address.
//				 */
//				addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//
//				// Catch network or other I/O problems.
//			} catch (IOException exception1) {
//
//				// Log an error and return an error message
//				// Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));
//
//				// print the stack trace
//				exception1.printStackTrace();
//
//				// Return an error message
//				return (getString(R.string.IO_Exception_getFromLocation));
//
//				// Catch incorrect latitude or longitude values
//			} catch (IllegalArgumentException exception2) {
//
//				// Construct a message containing the invalid arguments
//				String errorString = getString(R.string.illegal_argument_exception, location.getLatitude(), location.getLongitude());
//				// Log the error and print the stack trace
//				Log.e(LocationUtils.APPTAG, errorString);
//				exception2.printStackTrace();
//
//				//
//				return errorString;
//			}
//			// If the reverse geocode returned an address
//			if (addresses != null && addresses.size() > 0) {
//
//				// Get the first address
//				Address address = addresses.get(0);
//				return LocationUtils.addressToString(getActivity(), address);
//
//				// If there aren't any addresses, post a message
//			} else {
//				return getString(R.string.no_address_found);
//			}
//		}
//
//		/**
//		 * A method that's called once doInBackground() completes. Set the text of the UI element that displays the address. This method runs on the UI thread.
//		 */
//		@Override
//		protected void onPostExecute(String address) {
//			((ModifyAddressActivity) getActivity()).updateAddress(address);
//		}
//	}
//
//	protected class GetLatLngTask extends AsyncTask<String, Void, List<Address>> {
//
//		// Store the context passed to the AsyncTask when the system instantiates it.
//		Context localContext;
//
//		// Constructor called by the system to instantiate the task
//		public GetLatLngTask(Context context) {
//
//			// Required by the semantics of AsyncTask
//			super();
//
//			// Set a Context for the background task
//			localContext = context;
//		}
//
//		/**
//		 * Get a geocoding service instance, pass latitude and longitude to it, format the returned address, and return the address to the UI thread.
//		 */
//		@Override
//		protected List<Address> doInBackground(String... params) {
//			/*
//			 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform
//			 * to address standards can also be used.
//			 */
//			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());
//
//			// Get the current location from the input parameter list
//			String locationName = params[0];
//
//			// Create a list to contain the result address
//			List<Address> addresses = null;
//
//			// Try to get an address for the current location. Catch IO or network problems.
//			try {
//				/*
//				 * Call the synchronous getFromLocation() method with the latitude and longitude of the current location. Return at most 1 address.
//				 */
//				addresses = geocoder.getFromLocationName(locationName, 10);
//
//				// Catch network or other I/O problems.
//			} catch (IOException exception1) {
//
//				// Log an error and return an error message
//				Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));
//
//				// print the stack trace
//				exception1.printStackTrace();
//
//				// Return an error message
//				// return (getString(R.string.IO_Exception_getFromLocation));
//
//				// Catch incorrect latitude or longitude values
//			} catch (IllegalArgumentException exception2) {
//
//				// Construct a message containing the invalid arguments
//				String errorString = getString(R.string.illegal_argument_exception, locationName);
//				// Log the error and print the stack trace
//				Log.e(LocationUtils.APPTAG, errorString);
//				exception2.printStackTrace();
//				//
//				// return errorString;
//			}
//			return addresses;
//		}
//
//		/**
//		 * A method that's called once doInBackground() completes. Set the text of the UI element that displays the address. This method runs on the UI thread.
//		 */
//		@Override
//		protected void onPostExecute(List<Address> addresses) {
//			if (addresses == null) {
//				Utils.showMessageDialog(getActivity().getString(R.string.cannot_get_address_from_google), getActivity());
//			} else if (addresses.size() > 0) {
//				if(map!=null){
//					map.moveCamera(CameraUpdateFactory.newLatLngZoom(LocationUtils.addressToLatLng(addresses.get(0)), MBDefinition.DEFAULT_ZOOM));
//				}
//			} else {
////				autoCompView.requestFocus();
////				((AutoCompleteTextView) autoCompView).setError(_activity.getString(R.string.err_invalid_street_name));
//			}
//		}
//	}
//
//}
