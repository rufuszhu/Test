package digital.dispatch.TaxiLimoNewUI.Book;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import digital.dispatch.TaxiLimoNewUI.BuildConfig;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao.Properties;
import digital.dispatch.TaxiLimoNewUI.DaoMaster;
import digital.dispatch.TaxiLimoNewUI.DaoSession;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.ContactExpandableListAdapter;
import digital.dispatch.TaxiLimoNewUI.Adapters.PlacesAutoCompleteAdapter;
import digital.dispatch.TaxiLimoNewUI.DaoManager.AddressDaoManager;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Track.TrackFragment;
import digital.dispatch.TaxiLimoNewUI.Utils.ImageLoader;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class ModifyAddressActivity extends FragmentActivity implements OnItemClickListener {
	private static final String TAG = "ModifyAddressActivity";
	private static final int RED_TOTAL_DIFF = 194 - 107;
	private static final int GREEN_TOTAL_DIFF = 194 - 120;
	private static final int BLUE_TOTAL_DIFF = 194 - 131;

	private static final int RED = 197;
	private static final int GREEN = 194;
	private static final int BLUE = 194;

	private static final int RED_SELECTED = 107;
	private static final int GREEN_SELECTED = 120;
	private static final int BLUE_SELECTED = 131;

	private Context _activity;
	private boolean isDesitination;

	private DBAddressDao addressDao;

	EditText tv_streetNumber;
	EditText tv_unitNumber;
	AutoCompleteTextView autoCompView;
	LinearLayout favorite_btn;
	private ImageView blue_pin;
	// LinearLayout contact_favorite_btn;
	LinearLayout save_btn;
	LinearLayout delete_btn;
	

	public static FragmentManager fragmentManager;
	private static MapFragment mapFragment;
	private RelativeLayout tab0, tab1, tab2, tab3;
	private PagerAdapter mAdapter;
	private ViewPager mPager;
	private OnPageChangeListener pageChangeListener;
	private boolean isScolling = false;
	private FavoritesFragment favoritesFragment;
	private ContactsFragment contactsFragment;
	private LandmarksFragment landmarksFragment;

	// LinearLayout select_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_address);

		findViews();

		DaoManager daoManager = DaoManager.getInstance(this);
		addressDao = daoManager.getAddressDao(DaoManager.TYPE_READ);

		_activity = this;
		isDesitination = getIntent().getBooleanExtra(MBDefinition.IS_DESTINATION, false);

		setupActionBarTitle();

		// setUpExpendableListView();
		setUpTab();

		autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
		autoCompView.setOnItemClickListener(this);

		bindEvents();
	}


	private void setTabListener() {
		tab0 = (RelativeLayout) findViewById(R.id.tab1);
		tab1 = (RelativeLayout) findViewById(R.id.tab2);
		tab2 = (RelativeLayout) findViewById(R.id.tab3);
		tab3 = (RelativeLayout) findViewById(R.id.tab4);

		tab0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isScolling = true;
				resetAllAlpha();
				mPager.setCurrentItem(0);
				resetAllTabColor();
				tab0.setBackgroundColor(getResources().getColor(R.color.background_tab_selected));

			}
		});
		tab1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isScolling = true;
				resetAllAlpha();
				mPager.setCurrentItem(1);
				resetAllTabColor();
				tab1.setBackgroundColor(getResources().getColor(R.color.background_tab_selected));
			}
		});
		tab2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isScolling = true;
				resetAllAlpha();
				mPager.setCurrentItem(2);
				resetAllTabColor();
				tab2.setBackgroundColor(getResources().getColor(R.color.background_tab_selected));
			}
		});
		tab3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isScolling = true;
				resetAllAlpha();
				mPager.setCurrentItem(3);
				resetAllTabColor();
				tab3.setBackgroundColor(getResources().getColor(R.color.background_tab_selected));
			}
		});
	}

	private void setUpTab() {
		mPager = (ViewPager) findViewById(R.id.pager);
		setTabListener();
		mapFragment = MapFragment.newInstance();
		favoritesFragment = FavoritesFragment.newInstance();
		contactsFragment = new ContactsFragment();
		landmarksFragment = new LandmarksFragment();

		mAdapter = new PagerAdapter(getSupportFragmentManager());
		fragmentManager = getSupportFragmentManager();
		pageChangeListener = new OnPageChangeListener() {

			@Override
			public void onPageSelected(int selected) {
				Log.e(TAG, "onPageSelected: " + selected);
				if(selected==0){
					mapFragment.callGetLatLngTask();
				}
				else
					blue_pin.setVisibility(View.GONE);
				hideKeyBoard();
				hideDeleteBtn();
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				Log.e(TAG, "position: " + position + " positionOffset: " + positionOffset + " positionOffsetPixels: " + positionOffsetPixels);
				// do not animate tab color transition for clicking on tab
				if (!isScolling) {
					if (position == 0) {
						Log.e(TAG, "setting alpha");
						favoritesFragment.mGetView().setAlpha(positionOffset);
						mapFragment.mGetView().setAlpha(1 - positionOffset);

						if (mPager.getCurrentItem() == 0) {
							animateSelect(tab1, positionOffset);
							animateUnSelect(tab0, positionOffset);
						} else {
							animateSelect(tab0, 1f - positionOffset);
							animateUnSelect(getTab(mPager.getCurrentItem()), 1f - positionOffset);
						}
					} else if (position == 1) {
						if (contactsFragment.mGetView() != null)
							contactsFragment.mGetView().setAlpha(positionOffset);
						if (favoritesFragment.mGetView() != null)
							favoritesFragment.mGetView().setAlpha(1 - positionOffset);

						if (mPager.getCurrentItem() == 1) {
							animateSelect(tab2, positionOffset);
							animateUnSelect(tab1, positionOffset);
						} else {
							animateSelect(tab1, 1f - positionOffset);
							animateUnSelect(getTab(mPager.getCurrentItem()), 1f - positionOffset);
						}
					} else if (position == 2) {
						if (landmarksFragment.mGetView() != null)
							landmarksFragment.mGetView().setAlpha(positionOffset);
						if (contactsFragment.mGetView() != null)
							contactsFragment.mGetView().setAlpha(1 - positionOffset);

						if (mPager.getCurrentItem() == 2) {
							animateSelect(tab3, positionOffset);
							animateUnSelect(tab2, positionOffset);
						} else {
							animateSelect(tab2, 1f - positionOffset);
							animateUnSelect(getTab(mPager.getCurrentItem()), 1f - positionOffset);
						}
					}
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_DRAGGING) {
					blue_pin.setVisibility(View.GONE);
				}
				if (state == ViewPager.SCROLL_STATE_IDLE) {
					isScolling = false;
					Log.e(TAG, "SCROLL_STATE_IDLE");
					if(mPager.getCurrentItem() == 0)
						blue_pin.setVisibility(View.VISIBLE);
					// Log.e(TAG,"previousTab: " + previousTab + " mPager.getCurrentItem(): " + mPager.getCurrentItem());
				}
			}
		};
		// set second page back to default Alpha
		// favoritesFragment.mGetView().setAlpha(1f);

		mPager.setAdapter(mAdapter);

		// This is required to avoid a black flash when the map is loaded. The flash is due
		// to the use of a SurfaceView as the underlying view of the map.
		mPager.requestTransparentRegion(mPager);
		mPager.setOnPageChangeListener(pageChangeListener);
	}

	private void animateUnSelect(RelativeLayout tab, float positionOffset) {
		tab.setBackgroundColor(Color.rgb(RED_SELECTED + (int) (positionOffset * RED_TOTAL_DIFF), GREEN_SELECTED + (int) (positionOffset * GREEN_TOTAL_DIFF),
				BLUE_SELECTED + (int) (positionOffset * BLUE_TOTAL_DIFF)));
	}

	private void animateSelect(RelativeLayout tab, float positionOffset) {
		tab.setBackgroundColor(Color.rgb(RED - (int) (positionOffset * RED_TOTAL_DIFF), GREEN - (int) (positionOffset * GREEN_TOTAL_DIFF), BLUE
				- (int) (positionOffset * BLUE_TOTAL_DIFF)));
	}

	private void resetAllTabColor() {
		tab0.setBackgroundColor(getResources().getColor(R.color.background_tab));
		tab1.setBackgroundColor(getResources().getColor(R.color.background_tab));
		tab2.setBackgroundColor(getResources().getColor(R.color.background_tab));
		tab3.setBackgroundColor(getResources().getColor(R.color.background_tab));
	}

	private void resetAllAlpha() {
		if (mapFragment.mGetView() != null)
			mapFragment.mGetView().setAlpha(1);
		if (favoritesFragment.mGetView() != null)
			favoritesFragment.mGetView().setAlpha(1);
		if (contactsFragment.mGetView() != null)
			contactsFragment.mGetView().setAlpha(1);
		if (landmarksFragment.mGetView() != null)
			landmarksFragment.mGetView().setAlpha(1);
	}

	private RelativeLayout getTab(int i) {
		if (i == 0)
			return tab0;
		else if (i == 1)
			return tab1;
		else if (i == 2)
			return tab2;
		else if (i == 3)
			return tab3;
		else
			return null;
	}

	private class PagerAdapter extends FragmentPagerAdapter {
		private final String[] TITLES = { "MAP", "FAVORITES", "CONTACTS", "REMARK" };

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return mapFragment;
			case 1:
				return favoritesFragment;
			case 2:
				return contactsFragment;
			case 3:
				return landmarksFragment;
			default:
				return null;
			}
		}

	}

	private void bindEvents() {
		ImageView clear = (ImageView) findViewById(R.id.clear_autocomplete);
		clear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				autoCompView.setText("");
			}
		});

		TextView driver_note_btn = (TextView) findViewById(R.id.driver_note_btn);
		driver_note_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Dialog messageDialog = new Dialog(_activity);
				Utils.setUpDriverNoteDialog(_activity, messageDialog, null);
			}
		});

		save_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String streetName = autoCompView.getText().toString();
				String streetNumber = tv_streetNumber.getText().toString();
				boolean isFromContact = false;
				if (validateNotEmpty())
					new ValidateAddressTask(_activity, isFromContact, tv_unitNumber.getText().toString()).execute(streetNumber + " " + streetName);
			}
		});

		favorite_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String streetName = autoCompView.getText().toString();
				String streetNumber = tv_streetNumber.getText().toString();
				if (validateNotEmpty()) {
					new addFavoriteTask(_activity).execute(streetNumber + " " + streetName);
				}
			}
		});
		
		

	}

	//this is called by the tab fragments to update address textViews
	public void updateAddress(String address) {
		if (address.contains(" ")) {
			String firstPart = address.split(" ")[0];

			if (Utils.isNumeric(firstPart)) {
				tv_unitNumber.setText("");
				tv_streetNumber.setText(firstPart);
				autoCompView.setText(address.substring(firstPart.length() + 1));
			} else {
				tv_unitNumber.setText("");
				tv_streetNumber.setText("");
				autoCompView.setText(address);
			}

		} else {
			tv_unitNumber.setText("");
			tv_streetNumber.setText("");
			autoCompView.setText(address);
		}
	}

	private void setupActionBarTitle() {
		ActionBar ab = getActionBar();
		if (isDesitination) {
			Address destination = Utils.mDropoffAddress;
			if (destination != null) {
				tv_streetNumber.setText(AddressDaoManager.getHouseNumberFromAddress(destination));
				autoCompView.setText(AddressDaoManager.getStreetNameFromAddress(destination) + " " + destination.getLocality());
			}
			ab.setTitle(getString(R.string.title_activity_destination));
		} else {
			String addressExtra = getIntent().getStringExtra(MBDefinition.ADDRESSBAR_TEXT_EXTRA);
			if (addressExtra != null && addressExtra.length() > 0) {
				tv_streetNumber.setText(AddressDaoManager.getHouseNumberFromAddress(addressExtra));
				autoCompView.setText(AddressDaoManager.getStreetNameFromAddress(addressExtra));
			}
			ab.setTitle(getString(R.string.title_activity_pick_up));
		}
	}

	// override action bar back button to clear destination
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (isDesitination) {
				if (isEmpty())
					Utils.mDropoffAddress = null;
				Utils.dropoff_unit_number = null;
			}
			super.onBackPressed();
		}
		return true;
	}

	private void findViews() {
		save_btn = (LinearLayout) findViewById(R.id.save_btn);
		favorite_btn = (LinearLayout) findViewById(R.id.favorite_btn);
		delete_btn = (LinearLayout) findViewById(R.id.delete_btn);

		tv_streetNumber = (EditText) findViewById(R.id.tv_streetNumber);
		tv_unitNumber = (EditText) findViewById(R.id.tv_unitNumber);
		autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
		
		blue_pin = (ImageView) findViewById(R.id.blue_pin);
	}

	@Override
	protected void onResume() {
		super.onResume();
		hideKeyBoard();
//		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		inputMethodManager.toggleSoftInputFromWindow(tv_streetNumber.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
//		tv_streetNumber.requestFocus();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	

	@Override
	// autocomlete field listener, hide keyboard
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		hideKeyBoard();
	}
	
	public void hideKeyBoard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(tv_streetNumber.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(tv_unitNumber.getWindowToken(), 0);
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	private boolean isEmpty() {
		String streetName = autoCompView.getText().toString();
		String streetNumber = tv_streetNumber.getText().toString();

		return streetNumber.equalsIgnoreCase("") && streetName.equalsIgnoreCase("");
	}

	private boolean validateNotEmpty() {
		String streetName = autoCompView.getText().toString();
		String streetNumber = tv_streetNumber.getText().toString();

		if (streetNumber.equalsIgnoreCase("")) {
			tv_streetNumber.requestFocus();
			((EditText) tv_streetNumber).setError(_activity.getString(R.string.err_empty_street_number));
			if (streetName.equalsIgnoreCase("")) {
				autoCompView.requestFocus();
				((AutoCompleteTextView) autoCompView).setError(_activity.getString(R.string.err_empty_street_name));
				return false;
			}
			return false;
		}
		return true;
	}

	protected class ValidateAddressTask extends AsyncTask<String, Void, List<Address>> {

		// Store the context passed to the AsyncTask when the system instantiates it.
		Context localContext;
		boolean isFromContact;
		String unit;

		// Constructor called by the system to instantiate the task
		public ValidateAddressTask(Context context, boolean isFromContact, String unit) {

			// Required by the semantics of AsyncTask
			super();

			// Set a Context for the background task
			localContext = context;
			this.isFromContact = isFromContact;
			this.unit = unit;
		}

		/**
		 * Get a geocoding service instance, pass latitude and longitude to it, format the returned address, and return the address to the UI thread.
		 */
		@Override
		protected List<Address> doInBackground(String... params) {
			/*
			 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform
			 * to address standards can also be used.
			 */
			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

			// Get the current location from the input parameter list
			String locationName = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;

			// Try to get an address for the current location. Catch IO or network problems.
			try {

				/*
				 * Call the synchronous getFromLocation() method with the latitude and longitude of the current location. Return at most 1 address.
				 */
				addresses = geocoder.getFromLocationName(locationName, 10);

				// Catch network or other I/O problems.
			} catch (IOException exception1) {

				// Log an error and return an error message
				Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));

				// print the stack trace
				exception1.printStackTrace();

				// Return an error message
				// return (getString(R.string.IO_Exception_getFromLocation));

				// Catch incorrect latitude or longitude values
			} catch (IllegalArgumentException exception2) {

				// Construct a message containing the invalid arguments
				String errorString = getString(R.string.illegal_argument_exception, locationName);
				// Log the error and print the stack trace
				Log.e(LocationUtils.APPTAG, errorString);
				exception2.printStackTrace();

				//
				// return errorString;
			}
			return addresses;
		}

		/**
		 * A method that's called once doInBackground() completes. Set the text of the UI element that displays the address. This method runs on the UI thread.
		 */
		@Override
		protected void onPostExecute(List<Address> addresses) {
			if (addresses == null) {
				Utils.showMessageDialog(_activity.getString(R.string.cannot_get_address_from_google), _activity);
			} else if (addresses.size() > 1) {
				// pop up list
				boolean isSave = true;
				setUpListDialog(_activity, LocationUtils.addressListToStringList(_activity, addresses), addresses, unit, isSave);
			} else if (addresses.size() == 1) {
				if (Utils.isNumeric(AddressDaoManager.getHouseNumberFromAddress(addresses.get(0)))) {
					addUnitNumber(unit);

					Intent returnIntent = new Intent();
					returnIntent.putExtra(MBDefinition.ADDRESS, addresses.get(0));
					setResult(RESULT_OK, returnIntent);
					finish();
				} else {
					if (isFromContact) {
						Utils.showErrorDialog(_activity.getString(R.string.err_invalid_street_number), _activity);
					} else {
						tv_streetNumber.requestFocus();
						((EditText) tv_streetNumber).setError(_activity.getString(R.string.err_invalid_street_number));
					}
				}
			} else {
				if (isFromContact) {
					Utils.showErrorDialog(_activity.getString(R.string.err_invalid_street_name), _activity);
				} else {
					autoCompView.requestFocus();
					((AutoCompleteTextView) autoCompView).setError(_activity.getString(R.string.err_invalid_street_name));
				}
				// Toast.makeText(_activity, "invalid address", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void addUnitNumber(String unit) {
		if (unit != null && unit.length() > 0) {
			if (isDesitination)
				Utils.dropoff_unit_number = unit;
			else
				Utils.pickup_unit_number = unit;
		}
	}

	protected class addFavoriteTask extends AsyncTask<String, Void, List<Address>> {

		// Store the context passed to the AsyncTask when the system instantiates it.
		Context localContext;

		// Constructor called by the system to instantiate the task
		public addFavoriteTask(Context context) {
			// Required by the semantics of AsyncTask
			super();
			// Set a Context for the background task
			localContext = context;
		}

		/**
		 * Get a geocoding service instance, pass latitude and longitude to it, format the returned address, and return the address to the UI thread.
		 */
		@Override
		protected List<Address> doInBackground(String... params) {
			/*
			 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform
			 * to address standards can also be used.
			 */
			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

			// Get the current location from the input parameter list
			String locationName = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;

			// Try to get an address for the current location. Catch IO or network problems.
			try {

				/*
				 * Call the synchronous getFromLocation() method with the latitude and longitude of the current location. Return at most 1 address.
				 */
				addresses = geocoder.getFromLocationName(locationName, 10);

				// Catch network or other I/O problems.
			} catch (IOException exception1) {

				// Log an error and return an error message
				Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));

				// print the stack trace
				exception1.printStackTrace();

				// Return an error message
				// return (getString(R.string.IO_Exception_getFromLocation));

				// Catch incorrect latitude or longitude values
			} catch (IllegalArgumentException exception2) {

				// Construct a message containing the invalid arguments
				String errorString = getString(R.string.illegal_argument_exception, locationName);
				// Log the error and print the stack trace
				Log.e(LocationUtils.APPTAG, errorString);
				exception2.printStackTrace();

				//
				// return errorString;
			}
			return addresses;
		}

		/**
		 * A method that's called once doInBackground() completes. Set the text of the UI element that displays the address. This method runs on the UI thread.
		 */
		@Override
		protected void onPostExecute(final List<Address> addresses) {
			if (addresses.size() > 1) {
				// pop up list
				boolean isSave = false;
				setUpListDialog(_activity, LocationUtils.addressListToStringList(_activity, addresses), addresses, tv_unitNumber.getText().toString(), isSave);
			} else if (addresses.size() == 1) {
				if (Utils.isNumeric(AddressDaoManager.getHouseNumberFromAddress(addresses.get(0)))) {
					final EditText nickname_edit;
					TextView address_text;
					TextView cancel;
					TextView add;
					final Dialog nicknameDialog = new Dialog(_activity);
					nicknameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					nicknameDialog.setContentView(R.layout.nickname_dialog);
					nicknameDialog.setCanceledOnTouchOutside(true);

					address_text = (TextView) nicknameDialog.getWindow().findViewById(R.id.addr);
					nickname_edit = (EditText) nicknameDialog.getWindow().findViewById(R.id.nickname);
					address_text.setText(LocationUtils.addressToString(_activity, addresses.get(0)));

					cancel = (TextView) nicknameDialog.getWindow().findViewById(R.id.cancel);
					cancel.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							nicknameDialog.dismiss();
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(nickname_edit.getWindowToken(), 0);
						}
					});
					add = (TextView) nicknameDialog.getWindow().findViewById(R.id.add);
					add.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							String nickname = nickname_edit.getText().toString();
							DBAddress dbAddress = AddressDaoManager.addDaoAddressByAddress(addresses.get(0), tv_unitNumber.getText().toString(), nickname,
									true, addressDao);
							favoritesFragment.notifyChange();
							Toast.makeText(_activity, dbAddress.getNickName() + " is successfully added", Toast.LENGTH_SHORT).show();
							nicknameDialog.dismiss();
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(nickname_edit.getWindowToken(), 0);
						}
					});
					nicknameDialog.show();
				} else {
					tv_streetNumber.requestFocus();
					((EditText) tv_streetNumber).setError(_activity.getString(R.string.err_invalid_street_number));
				}
			} else {
				autoCompView.requestFocus();
				((AutoCompleteTextView) autoCompView).setError(_activity.getString(R.string.err_invalid_street_name));
			}
		}
	}

	private void setUpListDialog(final Context context, final ArrayList<String> addresses, final List<Address> addressesObj, final String unit,
			final boolean isSave) {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
		// builderSingle.setIcon(R.drawable.ic_launcher);
		builderSingle.setTitle("Please be more specific");
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.autocomplete_list_item);
		arrayAdapter.addAll(addresses);
		builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (isSave) {
					addUnitNumber(unit);
					Intent returnIntent = new Intent();
					returnIntent.putExtra(MBDefinition.ADDRESS, addressesObj.get(which));
					setResult(RESULT_OK, returnIntent);
					finish();
				} else {
					new addFavoriteTask(_activity).execute(addresses.get(which));
					Logger.e(TAG, "calling add fav task with address: " + addresses.get(which));
				}
			}
		});
		builderSingle.show();
	}


	public void showDeleteBtn(final DBAddress dbAddress) {
		favorite_btn.setVisibility(View.GONE);
		delete_btn.setVisibility(View.VISIBLE);
		delete_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
				builder.setTitle("Confirm Delete");
				builder.setMessage("Are you sure you want to delete?");
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						addressDao.delete(dbAddress);
						favoritesFragment.notifyChange();
						Toast.makeText(_activity, dbAddress.getNickName() + "is successfully deleted", Toast.LENGTH_SHORT).show();
					}
				});
				builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.show();
			}
		});
	}
	
	private void hideDeleteBtn(){
		favorite_btn.setVisibility(View.VISIBLE);
		delete_btn.setVisibility(View.GONE);
	}
}
