package digital.dispatch.TaxiLimoNewUI.Book;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Widget.NonSwipeableViewPager;

public class ModifyAddressActivity extends FragmentActivity {
	private static final String TAG = "ModifyAddressActivity";


	private Context _activity;
	private boolean isDesitination;

	private DBAddressDao addressDao;

	AutoCompleteTextView autoCompView;

	public static FragmentManager fragmentManager;
	private RelativeLayout tab0, tab1, tab2;
	private PagerAdapter mAdapter;
	private NonSwipeableViewPager mPager;
	private OnPageChangeListener pageChangeListener;
	private FavoritesFragment favoritesFragment;
	public ContactsFragment contactsFragment;
	private SearchFragment searchFragment;

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

		setUpTab();

		// autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
		// autoCompView.setOnItemClickListener(this);

		bindEvents();
	}
	
	public boolean getIsDesitination(){
		return isDesitination;
	}

	private void setupActionBarTitle() {
		ActionBar ab = getActionBar();
		if (isDesitination) {
			ab.setTitle(getString(R.string.title_activity_destination));
		} else {
			ab.setTitle(getString(R.string.title_activity_pick_up));
		}
	}

	private void setTabListener() {
		tab0 = (RelativeLayout) findViewById(R.id.tab0);
		tab1 = (RelativeLayout) findViewById(R.id.tab1);
		tab2 = (RelativeLayout) findViewById(R.id.tab2);

		tab0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(0);
				resetAllTabColor();
				tab0.setBackgroundColor(getResources().getColor(R.color.background_tab_selected));

			}
		});
		tab1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(1);
				resetAllTabColor();
				tab1.setBackgroundColor(getResources().getColor(R.color.background_tab_selected));
			}
		});
		tab2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(2);
				resetAllTabColor();
				tab2.setBackgroundColor(getResources().getColor(R.color.background_tab_selected));
			}
		});
	}

	private void setUpTab() {
		mPager = (NonSwipeableViewPager) findViewById(R.id.pager);
		setTabListener();
		favoritesFragment = FavoritesFragment.newInstance();
		contactsFragment = new ContactsFragment();
		searchFragment = SearchFragment.newInstance();

		mAdapter = new PagerAdapter(getSupportFragmentManager());
		fragmentManager = getSupportFragmentManager();
		pageChangeListener = new OnPageChangeListener() {

			@Override
			public void onPageSelected(int selected) {
				Log.e(TAG, "onPageSelected: " + selected);
				if(selected==0)
					searchFragment.getData();

			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {
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

	private void resetAllTabColor() {
		tab0.setBackgroundColor(getResources().getColor(R.color.background_tab));
		tab1.setBackgroundColor(getResources().getColor(R.color.background_tab));
		tab2.setBackgroundColor(getResources().getColor(R.color.background_tab));
	}

	public void notifyFavoriteDataChange(DBAddress value){
		favoritesFragment.notifyDataChange(value);
	}

//	public static class TextFragment extends Fragment {
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
//			return inflater.inflate(R.layout.text_fragment, container, false);
//		}
//	}

	private class PagerAdapter extends FragmentPagerAdapter {
		private final String[] TITLES = { "SEARCH", "FAVORITES", "CONTACTS" };

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return searchFragment;
			case 1:
				return favoritesFragment;
			case 2:
				return contactsFragment;
			default:
				return null;
			}
		}

	}

	private void bindEvents() {

	}

	private void findViews() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		// InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// inputMethodManager.toggleSoftInputFromWindow(tv_streetNumber.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
		// tv_streetNumber.requestFocus();
	}

	@Override
	public void onPause() {
		super.onPause();
	}



	public void hideKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}





//	protected class ValidateAddressTask extends AsyncTask<String, Void, List<Address>> {
//
//		// Store the context passed to the AsyncTask when the system instantiates it.
//		Context localContext;
//		boolean isFromContact;
//		String unit;
//
//		// Constructor called by the system to instantiate the task
//		public ValidateAddressTask(Context context, boolean isFromContact, String unit) {
//
//			// Required by the semantics of AsyncTask
//			super();
//
//			// Set a Context for the background task
//			localContext = context;
//			this.isFromContact = isFromContact;
//			this.unit = unit;
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
//
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
//
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
//				Utils.showMessageDialog(_activity.getString(R.string.cannot_get_address_from_google), _activity);
//			} else if (addresses.size() > 1) {
//				// pop up list
//				boolean isSave = true;
//				setUpListDialog(_activity, LocationUtils.addressListToStringList(_activity, addresses), addresses, unit, isSave);
//			} else if (addresses.size() == 1) {
//				if (Utils.isNumeric(AddressDaoManager.getHouseNumberFromAddress(addresses.get(0)))) {
//					addUnitNumber(unit);
//
//					Intent returnIntent = new Intent();
//					returnIntent.putExtra(MBDefinition.ADDRESS, addresses.get(0));
//					setResult(RESULT_OK, returnIntent);
//					finish();
//				} else {
//					if (isFromContact) {
//						Utils.showErrorDialog(_activity.getString(R.string.err_invalid_street_number), _activity);
//					} else {
//						tv_streetNumber.requestFocus();
//						((EditText) tv_streetNumber).setError(_activity.getString(R.string.err_invalid_street_number));
//					}
//				}
//			} else {
//				if (isFromContact) {
//					Utils.showErrorDialog(_activity.getString(R.string.err_invalid_street_name), _activity);
//				} else {
//					autoCompView.requestFocus();
//					((AutoCompleteTextView) autoCompView).setError(_activity.getString(R.string.err_invalid_street_name));
//				}
//				// Toast.makeText(_activity, "invalid address", Toast.LENGTH_SHORT).show();
//			}
//		}
//	}
//
//	private void addUnitNumber(String unit) {
//		if (unit != null && unit.length() > 0) {
//			if (isDesitination)
//				Utils.dropoff_unit_number = unit;
//			else
//				Utils.pickup_unit_number = unit;
//		}
//	}
//
//	protected class addFavoriteTask extends AsyncTask<String, Void, List<Address>> {
//
//		// Store the context passed to the AsyncTask when the system instantiates it.
//		Context localContext;
//
//		// Constructor called by the system to instantiate the task
//		public addFavoriteTask(Context context) {
//			// Required by the semantics of AsyncTask
//			super();
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
//
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
//
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
//		protected void onPostExecute(final List<Address> addresses) {
//			if (addresses.size() > 1) {
//				// pop up list
//				boolean isSave = false;
//				setUpListDialog(_activity, LocationUtils.addressListToStringList(_activity, addresses), addresses, tv_unitNumber.getText().toString(), isSave);
//			} else if (addresses.size() == 1) {
//				if (Utils.isNumeric(AddressDaoManager.getHouseNumberFromAddress(addresses.get(0)))) {
//					final EditText nickname_edit;
//					TextView address_text;
//					TextView cancel;
//					TextView add;
//					final Dialog nicknameDialog = new Dialog(_activity);
//					nicknameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//					nicknameDialog.setContentView(R.layout.nickname_dialog);
//					nicknameDialog.setCanceledOnTouchOutside(true);
//
//					address_text = (TextView) nicknameDialog.getWindow().findViewById(R.id.addr);
//					nickname_edit = (EditText) nicknameDialog.getWindow().findViewById(R.id.nickname);
//					address_text.setText(LocationUtils.addressToString(_activity, addresses.get(0)));
//
//					cancel = (TextView) nicknameDialog.getWindow().findViewById(R.id.cancel);
//					cancel.setOnClickListener(new View.OnClickListener() {
//						public void onClick(View v) {
//							nicknameDialog.dismiss();
//							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//							imm.hideSoftInputFromWindow(nickname_edit.getWindowToken(), 0);
//						}
//					});
//					add = (TextView) nicknameDialog.getWindow().findViewById(R.id.add);
//					add.setOnClickListener(new View.OnClickListener() {
//						public void onClick(View v) {
//							String nickname = nickname_edit.getText().toString();
//							DBAddress dbAddress = AddressDaoManager.addDaoAddressByAddress(addresses.get(0), tv_unitNumber.getText().toString(), nickname,
//									true, addressDao);
//							favoritesFragment.notifyChange();
//							Toast.makeText(_activity, dbAddress.getNickName() + " is successfully added", Toast.LENGTH_SHORT).show();
//							nicknameDialog.dismiss();
//							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//							imm.hideSoftInputFromWindow(nickname_edit.getWindowToken(), 0);
//						}
//					});
//					nicknameDialog.show();
//				} else {
//					tv_streetNumber.requestFocus();
//					((EditText) tv_streetNumber).setError(_activity.getString(R.string.err_invalid_street_number));
//				}
//			} else {
//				autoCompView.requestFocus();
//				((AutoCompleteTextView) autoCompView).setError(_activity.getString(R.string.err_invalid_street_name));
//			}
//		}
//	}
//
//	private void setUpListDialog(final Context context, final ArrayList<String> addresses, final List<Address> addressesObj, final String unit,
//			final boolean isSave) {
//		AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
//		// builderSingle.setIcon(R.drawable.ic_launcher);
//		builderSingle.setTitle("Please be more specific");
//		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.autocomplete_list_item);
//		arrayAdapter.addAll(addresses);
//		builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
//
//		builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (isSave) {
//					addUnitNumber(unit);
//					Intent returnIntent = new Intent();
//					returnIntent.putExtra(MBDefinition.ADDRESS, addressesObj.get(which));
//					setResult(RESULT_OK, returnIntent);
//					finish();
//				} else {
//					new addFavoriteTask(_activity).execute(addresses.get(which));
//					Logger.e(TAG, "calling add fav task with address: " + addresses.get(which));
//				}
//			}
//		});
//		builderSingle.show();
//	}


}
