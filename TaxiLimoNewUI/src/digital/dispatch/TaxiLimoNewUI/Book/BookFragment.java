package digital.dispatch.TaxiLimoNewUI.Book;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kankan.wheel.widget.WheelView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import digital.dispatch.TaxiLimoNewUI.DBAttributeDao;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.DaoManager.AddressDaoManager;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.BookJobTask;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadImageTask;
import digital.dispatch.TaxiLimoNewUI.Task.GetCompanyListTask;
import digital.dispatch.TaxiLimoNewUI.Utils.ErrorDialogFragment;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class BookFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnCameraChangeListener {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */

	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String TAG = "Book";
	private static View view;

	private GoogleMap mMap;
	private LocationClient mLocationClient;

	private TextView textNote;
	private TextView text_pickup;
	private TextView address_bar_text;
	private TextView tv_empty_comany_message;
	private TextView book_btn;

	private String oldDistrict;

	// These settings are the same as the settings for the map. They will in fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
		// View rootView = inflater.inflate(R.layout.fragment_book, container, false);
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

		textNote = (TextView) view.findViewById(R.id.text_note);
		address_bar_text = (TextView) view.findViewById(R.id.text_address);
		tv_empty_comany_message = (TextView) view.findViewById(R.id.tv_empty_comany_message);
		// setupCompanyUI();
		Utils.setNoteIndication(getActivity(), textNote);

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
				if (servicesConnected()) {
					intent.putExtra(MBDefinition.ADDRESSBAR_TEXT_EXTRA, address_bar_text.getText().toString());
				}
				intent.putExtra(MBDefinition.IS_DESTINATION, false);
				getActivity().startActivityForResult(intent, MBDefinition.REQUEST_PICKUPADDRESS_CODE);
			}
		});

		text_pickup = (TextView) view.findViewById(R.id.text_pickup);
		setTimeText(text_pickup);
		LinearLayout pickTime = (LinearLayout) view.findViewById(R.id.pickupTime);
		pickTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TimePickerDialog.timepick(getActivity(), 1, 20);
				// TimePickerDialog tpd = new TimePickerDialog(getActivity(), null, 10, 10, true);
				// tpd.show();
				Utils.setUpTimeDialog(getActivity(), text_pickup);
			}
		});

		LinearLayout driverNote = (LinearLayout) view.findViewById(R.id.driverNote);
		driverNote.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// setUpMessageDialog();
				Dialog messageDialog = new Dialog(getActivity());

				Utils.setUpDriverNoteDialog(getActivity(), messageDialog, textNote);

			}
		});

		LinearLayout destination = (LinearLayout) view.findViewById(R.id.destination_button);
		destination.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Intent intent = new Intent(getActivity(), ModifyAddressActivity.class);
				// if(servicesConnected()){
				// intent.putExtra("currentLocation", mLocationClient.getLastLocation());
				// }
				// startActivityForResult(intent,REQUEST_CODE);
				Intent intent = new Intent(getActivity(), ModifyAddressActivity.class);
				intent.putExtra(MBDefinition.IS_DESTINATION, true);
				getActivity().startActivityForResult(intent, MBDefinition.REQUEST_DROPOFFADDRESS_CODE);
			}
		});

		book_btn = (TextView) view.findViewById(R.id.book_button);
		book_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DaoManager daoManager = DaoManager.getInstance(getActivity());
				DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
				CompanyItem selectedCompany = Utils.mSelectedCompany;
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
				if (!validateHasHouseNumber()) {
					showEnterHouseNumberDialog();
				}
				else if(!validateHouseNumberHasNoRange()){
					Address address = Utils.mPickupAddress;
					String[] houseNumberRange = TextUtils.split(AddressDaoManager.getHouseNumberFromAddress(address), "-");
					showHouseRangeDialog(houseNumberRange[0], houseNumberRange[1]);
				}
				// check if allow multiple booking
				else if (bookingDao.queryBuilder().where(Properties.TripStatus.notEq(MBDefinition.MB_STATUS_CANCELLED), Properties.TripStatus.notEq(MBDefinition.MB_STATUS_COMPLETED)).list().size() > 0
						&& !SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, MBDefinition.SHARE_MULTI_BOOK_ALLOWED, false)) {
					Utils.showErrorDialog(getActivity().getString(R.string.err_no_multiple_booking), getActivity());

				} else if (selectedCompany == null) {
					// get into select company page
					Intent intent = new Intent(getActivity(), AttributeActivity.class);
					intent.putExtra(MBDefinition.EXTRA_SHOULD_BOOK_RIGHT_AFTER, true);
					getActivity().startActivityForResult(intent, MBDefinition.REQUEST_SELECT_COMPANY_TO_BOOK);
				} else {
					Utils.bookJob(selectedCompany, getActivity());
				}
			}
		});
		return view;
	}

	private boolean validateHasHouseNumber() {
		Address address = Utils.mPickupAddress;

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
				}
				else if(et_house_num.getText().toString().length() > MBDefinition.STREET_NUMBER_MAX_LENGTH)
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
			}
			else if(Integer.parseInt(et_house_num.getText().toString()) > Integer.parseInt(end)
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

	private void setupCompanyUI() {
		LinearLayout attribute = (LinearLayout) view.findViewById(R.id.attribute);
		LinearLayout ll_no_company_selected = (LinearLayout) view.findViewById(R.id.ll_no_company_selected);
		if (Utils.mSelectedCompany != null) {
			CompanyItem item = Utils.mSelectedCompany;
			attribute.setVisibility(View.VISIBLE);
			ll_no_company_selected.setVisibility(View.GONE);
			attribute.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (Utils.mPickupAddress != null) {
						Intent intent = new Intent(getActivity(), AttributeActivity.class);
						intent.putExtra(MBDefinition.EXTRA_SHOULD_BOOK_RIGHT_AFTER, false);
						startActivity(intent);
						// getActivity().startActivityForResult(intent, MBDefinition.REQUEST_COMPANYITEM_CODE);
					}
				}
			});

			ImageView iv_company_icon = (ImageView) view.findViewById(R.id.iv_company_icon);
			TextView tv_company_name = (TextView) view.findViewById(R.id.tv_company_name);
			tv_company_name.setText(item.name);
			String prefixURL = getActivity().getString(R.string.url);
			prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
			// String[] locArray = item.logo.split("/");
			// new DownloadLogoTask(prefixURL + item.logo, locArray[locArray.length - 1], viewHolder.icon, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new DownloadImageTask(iv_company_icon).execute(prefixURL + item.logo);
		}
		// if no company selected
		else {
			attribute.setVisibility(View.GONE);
			ll_no_company_selected.setVisibility(View.VISIBLE);
			ll_no_company_selected.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (Utils.mPickupAddress != null) {
						Intent intent = new Intent(getActivity(), AttributeActivity.class);
						intent.putExtra(MBDefinition.EXTRA_SHOULD_BOOK_RIGHT_AFTER, false);
						// getActivity().startActivityForResult(intent, MBDefinition.REQUEST_COMPANYITEM_CODE);
						startActivity(intent);
					}
				}
			});
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();

		setupCompanyUI();
	}

	@Override
	public void onPause() {
		super.onPause();
		Logger.e(TAG, "on PAUSE");
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setTimeText(TextView tv_time) {
		if (Utils.pickupDate == null || Utils.pickupTime == null) {
			tv_time.setText(getActivity().getResources().getString(R.string.now));
			tv_time.setTextSize(20);
			tv_time.setTextColor(getActivity().getResources().getColor(R.color.gray_light));
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd", Locale.US);
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh: mm a", Locale.US);
			String date = dateFormat.format(Utils.pickupDate);
			String time = timeFormat.format(Utils.pickupTime);
			Calendar cal = Calendar.getInstance();

			if (Utils.pickupDate.getDate() == cal.get(Calendar.DATE)) {
				tv_time.setText("Today" + "\n" + time);
			} else
				tv_time.setText(date + "\n" + time);
			tv_time.setTextSize(13);
			tv_time.setTextColor(getActivity().getResources().getColor(R.color.black));
		}
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

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getActivity(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener

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
			if (checkReady()) {
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LocationUtils.locationToLatLng(mLocationClient.getLastLocation()), MBDefinition.DEFAULT_ZOOM));
			}
		}

		TextView tv_destination = (TextView) view.findViewById(R.id.tv_destination);
		if (Utils.mDropoffAddress != null) {
			tv_destination.setTextColor(getActivity().getResources().getColor(R.color.black));
			tv_destination.setText(LocationUtils.addressToString(getActivity(), Utils.mDropoffAddress));
		} else {
			tv_destination.setTextColor(getActivity().getResources().getColor(R.color.gray_light));
			tv_destination.setText(getActivity().getResources().getString(R.string.enter_for_fare));
		}
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
		Location location = new Location("");
		location.setLatitude(cameraPosition.target.latitude);
		location.setLongitude(cameraPosition.target.longitude);

		getAddress(location);
		Utils.pickupHouseNumber="";
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error has a resolution, try sending an Intent to start a Google Play services activity that can resolve error.
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
		if (servicesConnected()) {

			// Get the current location
			Location currentLocation = mLocationClient.getLastLocation();
			LatLng currLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			// mCurrentLocation = currentLocation;

			CameraPosition currentPosition = new CameraPosition.Builder().target(currLatLng).zoom(MBDefinition.DEFAULT_ZOOM).bearing(0).tilt(0).build();
			CameraUpdate locatoinUpdate = CameraUpdateFactory.newCameraPosition(currentPosition);
			mMap.animateCamera(locatoinUpdate);
			// A few more markers for good measure.

			// queryAddrFromLoc();
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
	 * An AsyncTask that calls getFromLocation() in the background. The class uses the following generic types: Location - A {@link android.location.Location} object containing the current location,
	 * passed as the input parameter to doInBackground() Void - indicates that progress units are not used by this subclass String - An address passed to onPostExecute()
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
			 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform to address standards can also be used.
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
				Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));

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
				if (oldDistrict == null) {
					oldDistrict = address.getLocality();
					boolean isFromBooking = true;
					new GetCompanyListTask(getActivity(), address, isFromBooking).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					// only call if district changes
					if (!oldDistrict.equalsIgnoreCase(address.getLocality())) {
						Utils.mSelectedCompany = null;
						oldDistrict = address.getLocality();
						boolean isFromBooking = true;
						new GetCompanyListTask(getActivity(), address, isFromBooking).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				}
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

	public void handleGetCompanyListResponse(CompanyItem[] tempCompList, String city) {

		if (tempCompList.length == 0) {
			// show no available company;
			Logger.e("No company available in " + city);
			tv_empty_comany_message.setText("No company available in " + city);
			setupCompanyUI();
		} else if (tempCompList.length == 1) {
			Utils.mSelectedCompany = tempCompList[0];
			setupCompanyUI();
		} else {
			// show please choose a company
			Logger.e(tempCompList.length + " companies available in " + city);
			tv_empty_comany_message.setText(tempCompList.length + " companies available in " + city);
			setupCompanyUI();
		}
	}
}