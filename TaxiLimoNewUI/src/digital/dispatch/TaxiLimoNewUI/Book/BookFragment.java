package digital.dispatch.TaxiLimoNewUI.Book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.AddressDaoManager;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.ErrorDialogFragment;
import digital.dispatch.TaxiLimoNewUI.Utils.GecoderGoogle;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class BookFragment extends Fragment implements OnConnectionFailedListener, LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
		OnCameraChangeListener {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private boolean logEnabled = false; //set to true if need to debug GecoderGoogle code
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String TAG = "BookFragment";
	private static View view;

	private ImageView blue_pin;

	private LocationRequest mLocationRequest;
	private LocationClient mLocationClient;

	private GoogleMap mMap;
	private TextView address_bar_text;
	
	
	private boolean setPickupButtonClicked;
	
	private ArrayList<LatLng> carPos;
	private ArrayList<Marker> carMarkers;
	//private final static int INTERVAL = 1000 * 5; // 5 second
	private Runnable mHandlerTask;
	private Handler mHandler;
	private int counter = 0;

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
		Logger.d(TAG, "on CREATEVIEW");
		
		setPickupButtonClicked = false;

        //uncomment to enable (fake) car on map
//		mHandler = new Handler();
//		mHandlerTask = new Runnable() {
//			@Override
//			public void run() {
//				if(carPos!=null && carMarkers!=null &&carPos.size()==12 && carMarkers.size()==12){
//
//					moveCar(counter);
//					if(counter==11)
//						counter = 0;
//					else
//						counter++;
//				}
//				mHandler.postDelayed(mHandlerTask, (long) (4000*Math.random()));
//			}
//		};

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
		mLocationClient = new LocationClient(getActivity(), this, this);

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

		TextView add_fav_btn = (TextView) view.findViewById(R.id.add_fav_btn);
		Typeface fontFamily = Typeface.createFromAsset(getActivity().getAssets(), "fonts/fontawesome.ttf");
		add_fav_btn.setTypeface(fontFamily);
		add_fav_btn.setText(MBDefinition.icon_star_hollow);
		add_fav_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (Utils.mPickupAddress != null)
					setUpEnterNickNameDialog(Utils.mPickupAddress);
			}
		});

		address_bar_text = (TextView) view.findViewById(R.id.text_address);
		Typeface rionaFamily = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RionaSansRegular.otf");
		address_bar_text.setTypeface(rionaFamily);
        TextView tv_angle_right = (TextView) view.findViewById(R.id.tv_angle_right);

        tv_angle_right.setTypeface(fontFamily);
        tv_angle_right.setText(MBDefinition.ICON_ANGLE_RIGHT);

        TextView tv_pickup_location_lable = (TextView) view.findViewById(R.id.tv_pickup_location_lable);
        tv_pickup_location_lable.setTypeface(rionaFamily);


		TextView my_location_icon = (TextView) view.findViewById(R.id.my_location_icon);
		my_location_icon.setTypeface(fontFamily);
		my_location_icon.setText(MBDefinition.icon_current_location);

		FrameLayout current_location_btn = (FrameLayout) view.findViewById(R.id.my_location_btn);
		current_location_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onCurrentLocationClick();
			}
		});

        RelativeLayout addressBar = (RelativeLayout) view.findViewById(R.id.addressBar);
		addressBar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ModifyAddressActivity.class);
				intent.putExtra(MBDefinition.ADDRESSBAR_TEXT_EXTRA, address_bar_text.getText().toString());
				intent.putExtra(MBDefinition.IS_DESTINATION, false);
				((MainActivity) getActivity()).startActivityForAnim(intent);
			}
		});

		blue_pin = (ImageView) view.findViewById(R.id.blue_pin);
		blue_pin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (Utils.mPickupAddress == null || Utils.mPickupAddress.equals("")) {
					//Utils.showMessageDialog(getActivity().getString(R.string.err_no_pickup_address), getActivity());
					address_bar_text.setText(getActivity().getString(R.string.err_no_pickup_address));
					setPickupButtonClicked = true; 
				}

                else if(Utils.mPickupAddress.getLocality()==null){
                    Utils.showMessageDialog(getActivity().getString(R.string.invade_address), getActivity());
                }
				
				//TL-388 Allow landmark booking (no need to force user to enter in street number, if returned address does not have street number).
				// no house number in pickup address
//				else if (!validateHasHouseNumber()) {
//					showEnterHouseNumberDialog();
//				}
				// house number return by Google has a range
				else if (!validateHouseNumberHasNoRange()) {
					Address address = Utils.mPickupAddress;
					String[] houseNumberRange = TextUtils.split(AddressDaoManager.getHouseNumberFromAddress(address), "-");
					showHouseRangeDialog(houseNumberRange[0], houseNumberRange[1]);
				} 
				else {
					Intent intent = new Intent(getActivity(), BookActivity.class);
					((MainActivity) getActivity()).startActivityForAnim(intent);
				}
			}
		});

		return view;
	}

	@Override
	public void onStop() {

		// If the client is connected
		if (mLocationClient.isConnected()) {
			// stopPeriodicUpdates();
		}

		// After disconnect() is called, the client is considered "dead".
		mLocationClient.disconnect();

		super.onStop();
	}

	@Override
	public void onStart() {
		super.onStart();
		mLocationClient.connect();

	}

	/**
	 * Verify that Google Play services is available before making a request.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), 0);
			if (dialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(dialog);
				errorFragment.show(getActivity().getSupportFragmentManager(), LocationUtils.APPTAG);
			}
			return false;
		}
	}

	private boolean validateHasHouseNumber() {
		Address address = Utils.mPickupAddress;
		if (address == null)
			return false;
		String ad = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";
		String[] strArray = TextUtils.split(ad, " ");

		if (strArray.length>0 && !Utils.isNumeric(strArray[0]) && Utils.pickupHouseNumber.equals("")) {
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
		final TextView ok = (TextView) enterHouseNumDialog.getWindow().findViewById(R.id.ok);
		TextView cancel = (TextView) enterHouseNumDialog.getWindow().findViewById(R.id.cancel);
		et_house_num.setOnEditorActionListener(new OnEditorActionListener() {

	        @Override
	        public boolean onEditorAction(TextView v, int actionId,
	                KeyEvent event) {
	            if (actionId == EditorInfo.IME_ACTION_DONE) {
	            	ok.performClick();
	            }
	            return false;
	        }
	    });
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
					blue_pin.callOnClick();
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
		final TextView ok = (TextView) houseNumRangeDialog.getWindow().findViewById(R.id.ok);
		TextView cancel = (TextView) houseNumRangeDialog.getWindow().findViewById(R.id.cancel);
		et_house_num.setOnEditorActionListener(new OnEditorActionListener() {

	        @Override
	        public boolean onEditorAction(TextView v, int actionId,
	                KeyEvent event) {
	            if (actionId == EditorInfo.IME_ACTION_DONE) {
	            	ok.performClick();
	            }
	            return false;
	        }
	    });
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
					blue_pin.callOnClick();
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
		Logger.d(TAG, "on RESUME");
		checkGPSEnable();
		checkInternet();
		
	}

	@Override
	public void onPause() {
		super.onPause();
		Logger.d(TAG, "on PAUSE");
        //uncomment to enable (fake) car on map
		//stopRepeatingTask();
	}
    //uncomment to enable (fake) car on map
//	private void startRepeatingTask() {
//		mHandlerTask.run();
//	}
//
//	private void stopRepeatingTask() {
//		mHandler.removeCallbacks(mHandlerTask);
//	}
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
				mMap.setMyLocationEnabled(true);
				mMap.getUiSettings().setZoomControlsEnabled(false);
				mMap.getUiSettings().setMyLocationButtonEnabled(true);
				mMap.setOnCameraChangeListener(this);

			}
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		Location location = new Location("");
		location.setLatitude(cameraPosition.target.latitude);
		location.setLongitude(cameraPosition.target.longitude);
		//to avoid unnecessary address lookup
		if(location.getLatitude() != 0.0 && location.getLongitude() != 0.0){
			getAddress(location);
		}
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
		Location currentLocation = mLocationClient.getLastLocation();
		if (servicesConnected() && currentLocation != null) {

			LatLng currLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			// mCurrentLocation = currentLocation;

			CameraPosition currentPosition = new CameraPosition.Builder().target(currLatLng).zoom(MBDefinition.DEFAULT_ZOOM).bearing(0).tilt(0).build();
			CameraUpdate locatoinUpdate = CameraUpdateFactory.newCameraPosition(currentPosition);
			mMap.animateCamera(locatoinUpdate);
		} else {
			Toast.makeText(getActivity(), R.string.err_no_current_location, Toast.LENGTH_LONG).show();
		}

	}

	// For Eclipse with ADT, suppress warnings about Geocoder.isPresent()
	@SuppressLint("NewApi")
	public void getAddress(Location location) {

		// In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
			// No geocoder is present.
			//TL-286 use Google Maps Web Service Geocoding API 
			new GetAddressFromMapServerTask(getActivity()).execute(location); 
			
		}
		else{
			new GetAddressTask(getActivity()).execute(location);
			
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
			

			// Get the current location from the input parameter list
			Location location = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;

			// Try to get an address for the current location. Catch IO or network problems.
			try {

				/*
				 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform
				 * to address standards can also be used.
				 */
				Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());
				/*
				 * Call the synchronous getFromLocation() method with the latitude and longitude of the current location. Return at most 1 address.
				 */
				if(geocoder != null){
					addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				}

				// Catch network or other I/O problems.
			} catch (Exception e) {
				e.printStackTrace();
				// Log an error 
				Logger.e(TAG, "geocoder failed , moving on to HTTP");
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {

				// Get the first address
				Address address = addresses.get(0);
				Utils.mPickupAddress = address;
                Utils.pickupHouseNumber="";
				if(isAdded())
					return LocationUtils.addressToString(getActivity(), address);
				else
					return "";
				
			}else{	
				//try HTTP lookup to the maps API					
				GecoderGoogle mGecoderGoogle = new GecoderGoogle(localContext, Locale.getDefault(), logEnabled);
				// Create a list to contain the result address
				List<Address> addressList = null;	
				
				try{
					addressList = mGecoderGoogle.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
					
					if (addressList != null && addressList.size() > 0) {
					
							// Get the first address
							Address address = addressList.get(0);
							Utils.mPickupAddress = address;
                            Utils.pickupHouseNumber = "";
							if(isAdded())
								return LocationUtils.addressToString(getActivity(), address);
							else
								return "";
						
					} else {
						// If there aren't any addresses, post a message
						Utils.mPickupAddress = null;
                        Utils.pickupHouseNumber = "";

						if(isAdded())
							return getString(R.string.no_address_found);
						else
							return "";
					}
					
					
				}catch (IOException e) {
			
					// print the stack trace
					e.printStackTrace();
					// Return an error message
					if(isAdded())
						return (getString(R.string.IO_Exception_getFromLocation));
					else
						return "";
					
				} catch (IllegalArgumentException e) {
					
						// Construct a message containing the invalid arguments
						String errorString = getString(R.string.illegal_argument_exception, location.getLatitude(), location.getLongitude());
						// Log the error and print the stack trace
						Logger.e(TAG, errorString);
						e.printStackTrace();

						if(isAdded())
							return errorString;
						else
							return "";
					
				} catch (NullPointerException e) {
				
						// print the stack trace
						e.printStackTrace();
						// Return an error message
						if(isAdded())
							return (getString(R.string.IO_Exception_getFromLocation));
						else
							return "";
		
				}
				
				
			}
			
		}
				

		/**
		 * A method that's called once doInBackground() completes. Set the text of the UI element that displays the address. This method runs on the UI thread.
		 */
		@Override
		protected void onPostExecute(String address) {
			address_bar_text.setText(address);
			if(setPickupButtonClicked){
				setPickupButtonClicked=false;
				blue_pin.performClick();
			}
		}
	}
	
	
	/**
	 * Get address with Google Map Webservice Geocoding API asynchronously.
	 * 
	 * 
	 */
	protected class GetAddressFromMapServerTask extends AsyncTask<Location, Void, String> {
		
		// Store the context passed to the AsyncTask when the system instantiates it.
		Context localContext;

		// Constructor called by the system to instantiate the task
		public GetAddressFromMapServerTask(Context context) {

			// Required by the semantics of AsyncTask
			super();

			// Set a Context for the background task
			localContext = context;
		}
		
		
		@Override
		protected String doInBackground(Location... params) {
			GecoderGoogle mGecoderGoogle = new GecoderGoogle(localContext, Locale.getDefault(), logEnabled);

			// Get the current location from the input parameter list
			Location location = params[0];
			
			// Create a list to contain the result address
			List<Address> addressList = null;
			
			
			try{
				addressList = mGecoderGoogle.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				
				if (addressList != null && addressList.size() > 0) {
				
						// Get the first address
						Address address = addressList.get(0);
						Utils.mPickupAddress = address;
                        Utils.pickupHouseNumber="";
						if(isAdded())
							return LocationUtils.addressToString(getActivity(), address);
						else
							return "";
					// If there aren't any addresses, post a message
				} else {
					Utils.mPickupAddress = null;
                    Utils.pickupHouseNumber="";
					if(isAdded())
						return getString(R.string.no_address_found);
					else
						return "";
				}
				
				
			}catch (IOException e) {
		
				// print the stack trace
				e.printStackTrace();
				// Return an error message
				if(isAdded())
					return (getString(R.string.IO_Exception_getFromLocation));
				else
					return "";
				
			} catch (IllegalArgumentException e) {
				
					// Construct a message containing the invalid arguments
					String errorString = getString(R.string.illegal_argument_exception, location.getLatitude(), location.getLongitude());
					// Log the error and print the stack trace
					Logger.e(TAG, errorString);
					e.printStackTrace();

					if(isAdded())
						return errorString;
					else
						return "";
				
			} catch (NullPointerException e) {
			
					Logger.e(TAG, "NullPointer");
					return "";
	
			}
			
		}

		/**
		 * A method that's called once doInBackground() completes. Set the text of the UI element that displays the address. This method runs on the UI thread.
		 */
		@Override
		protected void onPostExecute(String address) {
			address_bar_text.setText(address);
			if(setPickupButtonClicked){
				setPickupButtonClicked=false;
				blue_pin.performClick();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		Logger.d(TAG, "onConnected");
		setUpMapIfNeeded();
		Address address = Utils.mPickupAddress;
		
		if (address != null) {
			address_bar_text.setText(LocationUtils.addressToString(getActivity(), address));
			if (checkReady()) {
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LocationUtils.addressToLatLng(address), MBDefinition.DEFAULT_ZOOM));
                //uncomment to enable (fake) car on map
//				cleanUP();
//				generate12CarsWithin5MilesRandom(address.getLatitude(),address.getLongitude());
//				startRepeatingTask();
			}
		} else {
			if (checkReady() && servicesConnected()) {
				// Get the current location
				Location currentLocation = mLocationClient.getLastLocation();
				if (currentLocation != null){
                    //uncomment to enable (fake) car on map
//					cleanUP();
//					generate12CarsWithin5MilesRandom(currentLocation.getLatitude(),currentLocation.getLongitude());
//                  startRepeatingTask();
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LocationUtils.locationToLatLng(currentLocation), MBDefinition.DEFAULT_ZOOM));

				}
				else
					Toast.makeText(getActivity(), R.string.err_no_current_location, Toast.LENGTH_LONG).show();
			}
			else{
				
			}
		}
	}
    //uncomment to enable (fake) car on map
//	private void moveCar(int carNum){
//		double MOVE_AMOUNT = 0.005;
//		LatLng pos = carPos.get(carNum);
//		Marker marker = carMarkers.get(carNum);
//		double sign = randomePosNegative();
//		LatLng newPos;
//
//
//		if(randomePosNegative()>0.5){
//			newPos = new LatLng( pos.latitude + MOVE_AMOUNT * sign, pos.longitude);
//			if(sign>0)
//				animateRotateAndMoveMarker(marker,0f,newPos);
//			else
//				animateRotateAndMoveMarker(marker,180f,newPos);
//			carPos.set(carNum, newPos);
//		}
//		else
//		{
//			newPos = new LatLng( pos.latitude , pos.longitude + MOVE_AMOUNT * sign);
//			if(sign>0){
//				animateRotateAndMoveMarker(marker,90f,newPos);
//				}
//			else
//				animateRotateAndMoveMarker(marker,270f,newPos);
//			carPos.set(carNum, newPos);
//
//		}
//
//	}
//
//	private void cleanUP(){
//		if(carPos!=null && carPos.size()>0 && carMarkers!=null && carMarkers.size()>0){
//			for(int i=0;i<carMarkers.size();i++){
//				carMarkers.get(i).remove();
//			}
//			carPos.clear();
//			carMarkers.clear();
//		}
//	}
//
//	private void generate12CarsWithin5MilesRandom(double mLat, double mLong){
//		double FIVE_MILES_IN_LAT = (double) 0.022346;
//		double FIVE_MILES_IN_LONG = (double) 0.033341;
//
//		carPos = new ArrayList<LatLng>();
//		carMarkers = new ArrayList<Marker>();
//
//		for(int i=0;i<12;i++){
//			double xLat = (double)Math.random() * FIVE_MILES_IN_LAT * randomePosNegative();
//			double xLong = (double)Math.random() * FIVE_MILES_IN_LONG * randomePosNegative();
//			double lat = xLat + mLat;
//			double longi = xLong + mLong;
//			LatLng a = new LatLng(lat,longi);
//			carPos.add(a);
//			Marker carMarker = mMap.addMarker(new MarkerOptions().position(a).draggable(false).icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi_on_map)).rotation(90*(i%4)));
//			carMarkers.add(carMarker);
//		}
//	}
//
//	private long randomePosNegative(){
//		if(Math.random()>0.5)
//			return (long) 1.0;
//		else
//			return (long) -1.0;
//	}
//
//
//	private void animateMarker(final Marker marker, final LatLng toPosition) {
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        Projection proj = mMap.getProjection();
//        Point startPoint = proj.toScreenLocation(marker.getPosition());
//        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//        final long duration = 5000;
//
//        final Interpolator interpolator = new LinearInterpolator();
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) elapsed
//                        / duration);
//                double lng = t * toPosition.longitude + (1 - t)
//                        * startLatLng.longitude;
//                double lat = t * toPosition.latitude + (1 - t)
//                        * startLatLng.latitude;
//                marker.setPosition(new LatLng(lat, lng));
//
//                if (t < 1.0) {
//                    // Post again 16ms later.
//                    handler.postDelayed(this, 16);
//                } else {
//                	marker.setVisible(true);
//                }
//            }
//        });
//    }
//	//animate rotation after, and move the marker after rotation
//	private void animateRotateAndMoveMarker(final Marker marker, final float endDegree,final LatLng newPos) {
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        final float startDegree = marker.getRotation();
//
//        final long duration = 1000;
//
//        final Interpolator interpolator = new LinearInterpolator();
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) elapsed
//                        / duration);
//                float degree = t * endDegree + (1 - t) * startDegree;
//                marker.setRotation(degree);
//
//                if (t < 1.0) {
//                    // Post again 16ms later.
//                    handler.postDelayed(this, 16);
//                } else {
//                	marker.setVisible(true);
//                	animateMarker(marker, newPos);
//                }
//            }
//        });
//    }

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	private void setUpEnterNickNameDialog(final Address address) {
		final EditText nickname_edit;
		TextView address_text;
		TextView cancel;
		TextView add;
		final Dialog nicknameDialog = new Dialog(getActivity());
		nicknameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		nicknameDialog.setContentView(R.layout.nickname_dialog);
		nicknameDialog.setCanceledOnTouchOutside(true);

		address_text = (TextView) nicknameDialog.getWindow().findViewById(R.id.addr);
		nickname_edit = (EditText) nicknameDialog.getWindow().findViewById(R.id.nickname);
		address_text.setText(LocationUtils.addressToString(getActivity(), address));

		cancel = (TextView) nicknameDialog.getWindow().findViewById(R.id.cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(nickname_edit.getWindowToken(), 0);
				nicknameDialog.dismiss();
			}
		});
		add = (TextView) nicknameDialog.getWindow().findViewById(R.id.add);
		add.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String nickname = nickname_edit.getText().toString();
				if (nickname.length() == 0) {
					nickname_edit.setError(getActivity().getString(R.string.nickName_cannot_be_empty));
				} else {
					DaoManager daoManager = DaoManager.getInstance(getActivity());
					DBAddressDao addressDao = daoManager.getAddressDao(DaoManager.TYPE_WRITE);
					DBAddress dbAddress = AddressDaoManager.addDaoAddressByAddress(address, "", nickname, true, addressDao);

					Toast.makeText(getActivity(), dbAddress.getNickName() + " is successfully added", Toast.LENGTH_SHORT).show();
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(nickname_edit.getWindowToken(), 0);
					nicknameDialog.dismiss();
				}
			}
		});
		nicknameDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		nicknameDialog.show();
	}
	
	
	
}