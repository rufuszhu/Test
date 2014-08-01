package digital.dispatch.TaxiLimoNewUI.Book;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Drawers.PreferenceActivity;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.R.menu;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;

public class BookFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String TAG = "Book";
	private static final int DRIVER_NOTE_MAX_LENGTH = 256;
	private static View view;

	private GoogleMap mMap;
	private LocationClient mLocationClient;
	
	private Dialog messageDialog;
	private String driverNoteString;
	private TextView textNote;
	
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

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		Logger.e(TAG, "on CREATE");
//		setHasOptionsMenu(true);
//	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// only show refresh if drawer is not open.
		if (!((MainActivity) getActivity()).getDrawerFragment().isDrawerOpen())
			inflater.inflate(R.menu.main, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.e(TAG, "on CREATEVIEW");
		//View rootView = inflater.inflate(R.layout.fragment_book, container, false);
		
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
	    
	    
	    ImageButton button = (ImageButton) view.findViewById(R.id.image_currentLocation);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onCurrentLocationClick();
			}
		});
		
		LinearLayout addressBar = (LinearLayout) view.findViewById(R.id.addressBar);
		addressBar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onAddressBarClick();
			}
		});
		
		LinearLayout driverNote = (LinearLayout) view.findViewById(R.id.driverNote);
		driverNote.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setUpMessageDialog();
			}
		});
		
	    return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		Logger.e(TAG, "on PAUSE");
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}
	
    /**
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
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
			}
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getActivity(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}
	
	private void setUpMessageDialog(){
		final EditText driverMessage;
		final TextView textRemaining;
		TextView ok;
		TextView clear;
		
		messageDialog = new Dialog(getActivity());
		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.message_dialog);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		messageDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		driverMessage = (EditText) messageDialog.getWindow().findViewById(R.id.message);
		textRemaining =  (TextView) messageDialog.getWindow().findViewById(R.id.text_remaining);
		driverMessage.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable note) {
				textRemaining.setText(DRIVER_NOTE_MAX_LENGTH-note.length() + " Charaters Left");
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
		});
		
		ok = (TextView) messageDialog.getWindow().findViewById(R.id.ok);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				driverNoteString = driverMessage.getText().toString();
				
				textNote.setText(driverNoteString.substring(0, Math.min(10, driverNoteString.length())));
				messageDialog.dismiss();
			}
		});
		clear = (TextView) messageDialog.getWindow().findViewById(R.id.clear);
		clear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				driverMessage.setText("");
			}
		});
		
		
		messageDialog.show();
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener

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
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}
	
	private void onAddressBarClick(){
		Intent intent = new Intent(getActivity(), ModifyAddressActivity.class);
		startActivity(intent);
	}

	private void onCurrentLocationClick() {
		LatLng userLocation;
		Location currentLocation = null;

		currentLocation = getBestLocation();
		if (currentLocation == null) {
			// show location not available
			// new AlertDialog.Builder(this).setTitle(R.string.GPS_location).setMessage(R.string.book_map_GPS_service_disabled).setCancelable(false)
			// .setNeutralButton(R.string.GPS_setting, mGPSSettingListener).setPositiveButton(R.string.positive_button, null).show();
		} else {
			userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			Logger.v(TAG, "currentLocation address " + userLocation.toString());
			// mCurrentLocation = currentLocation;

			CameraPosition currentPosition = new CameraPosition.Builder().target(userLocation).zoom(16.5f).bearing(0).tilt(0).build();
			CameraUpdate locatoinUpdate = CameraUpdateFactory.newCameraPosition(currentPosition);
			mMap.animateCamera(locatoinUpdate);
			// A few more markers for good measure.

			// queryAddrFromLoc();
		}
	}

	private Location getBestLocation() {
		Location gpsLocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
		Location networkLocation = getLocationByProvider(LocationManager.NETWORK_PROVIDER);
		Location mapLocation = null;
		Location tmpLocation;

		if (mMap != null) {
			if (mLocationClient != null && mLocationClient.isConnected())
				mapLocation = mLocationClient.getLastLocation();
		}

		if (mapLocation != null) {
			Logger.v(TAG, "getBestLocation map location accurate = " + mapLocation.getAccuracy());
			return mapLocation;
		}

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

		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

		try {
			if (locationManager.isProviderEnabled(provider)) {
				location = locationManager.getLastKnownLocation(provider);
			}
		} catch (IllegalArgumentException e) {
			Logger.e(TAG, "Cannot acces Provider " + provider);
		}

		return location;
	}

	// public void callService() {
	// getParam();
	// }

	// private void initDatabase(){
	// DatabaseHandler mDb = new DatabaseHandler(getActivity());
	//
	// }

	// @SuppressLint("NewApi")
	// private void getParam() {
	// if (Build.VERSION.SDK_INT >= 11) {
	// new GetMBParamTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	// }
	// else {
	// new GetMBParamTask().execute();
	// }
	// }
	//
	//
	//
	// private class GetMBParamTask extends AsyncTask<String, Integer, Void>
	// implements IMGParamResponseListener, IRequestTimerListener {
	// GetMBParamRequest gMBpReq;
	//
	// //The code to be executed in a background thread.
	// @Override
	// protected Void doInBackground(String... params)
	// {
	// gMBpReq = new GetMBParamRequest(this, this);
	//
	//
	// gMBpReq.sendRequest("http://iis.com.dds.osp.itaxi.interface/", "http://192.168.50.160:76/Service.asmx?wsdl");
	//
	// return null;
	// }
	//
	// @Override
	// public void onResponseReady(GetMBParamResponse response) {
	//
	// MGParam paramList = response.GetParams();
	// // TODO: single source, handle MB3.0 might have this param
	// Log.e(TAG, paramList.getCCPaymentEnabled()+"");
	// Log.e(TAG, paramList.getDropOffMand()+"");
	//
	// }
	//
	// @Override
	// public void onErrorResponse(String errorString) {
	//
	// Log.v(TAG, "error response: " + errorString);
	// }
	//
	// @Override
	// public void onError() {
	//
	// Log.v(TAG, "no response");
	// }
	//
	// @Override
	// public void onProgressUpdate(int progress) {}
	// }

}
