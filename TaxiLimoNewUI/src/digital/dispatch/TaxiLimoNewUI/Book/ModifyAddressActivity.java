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
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
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
import digital.dispatch.TaxiLimoNewUI.Utils.ImageLoader;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class ModifyAddressActivity extends ActionBarActivity implements OnItemClickListener {
	private static final String TAG = "ModifyAddressActivity";
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, List<MyAddress>> listDataChild;
	private List<MyAddress> mContactList;
	private ContactExpandableListAdapter expListAdapter;
	private Context _activity;
	private boolean isDesitination;

	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private DBAddressDao addressDao;
	private Cursor cursor;

	private ImageLoader mImageLoader;

	TextView tv_streetNumber;
	TextView tv_unitNumber;
	AutoCompleteTextView autoCompView;
	LinearLayout favorite_btn;
	LinearLayout contact_favorite_btn;
	LinearLayout save_btn;
	LinearLayout delete_btn;
	LinearLayout select_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_address);

		findViews();

		DaoManager daoManager = DaoManager.getInstance(this);
		addressDao = daoManager.getAddressDao(DaoManager.TYPE_READ);

		_activity = this;
		isDesitination = getIntent().getBooleanExtra(MBDefinition.IS_DESTINATION, false);

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

		setUpExpendableListView();

		autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
		autoCompView.setOnItemClickListener(this);

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
					new ValidateAddressTask(_activity, isFromContact).execute(streetNumber + " " + streetName);
			}
		});

		favorite_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String streetName = autoCompView.getText().toString();
				String streetNumber = tv_streetNumber.getText().toString();
				if (validateNotEmpty()) {
					boolean isFromContact = false;
					new addFavoriteTask(_activity, isFromContact).execute(streetNumber + " " + streetName);
				}
			}
		});
	}

	private void findViews() {
		save_btn = (LinearLayout) findViewById(R.id.save_btn);
		favorite_btn = (LinearLayout) findViewById(R.id.favorite_btn);
		delete_btn = (LinearLayout) findViewById(R.id.delete_btn);
		select_btn = (LinearLayout) findViewById(R.id.select_btn);
		contact_favorite_btn = (LinearLayout) findViewById(R.id.contact_favorite_btn);
		tv_streetNumber = (TextView) findViewById(R.id.tv_streetNumber);
		tv_unitNumber = (TextView) findViewById(R.id.tv_unitNumber);
		autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
		expListView = (ExpandableListView) findViewById(R.id.lvExp);
	}

	private void setUpExpendableListView() {
		mImageLoader = new ImageLoader(this, getListPreferredItemHeight()) {
			@Override
			protected Bitmap processBitmap(Object data) {
				// This gets called in a background thread and passed the data from
				// ImageLoader.loadImage().
				return loadContactPhotoThumbnail((String) data, getImageSize());
			}
		};

		// Set a placeholder loading image for the image loader
		mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_holo_light);

		// Add a cache to the image loader
		mImageLoader.addImageCache(this.getSupportFragmentManager(), 0.1f);

		// preparing list data
		prepareListData();

		expListAdapter = new ContactExpandableListAdapter(this, listDataHeader, listDataChild, mImageLoader);
		queryFavList();
		// setting list adapter
		expListView.setAdapter(expListAdapter);

		expListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int scrollState) {
				// Pause image loader to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					mImageLoader.setPauseWork(true);
				} else {
					mImageLoader.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int i, int i1, int i2) {
			}
		});
		expListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
				view.setSelected(true);
				// favorite list can delete or select
				if (groupPosition == 0) {
					favorite_btn.setVisibility(View.GONE);
					save_btn.setVisibility(View.GONE);
					delete_btn.setVisibility(View.VISIBLE);
					select_btn.setVisibility(View.VISIBLE);
					contact_favorite_btn.setVisibility(View.GONE);
					final boolean isFromContact = true;

					final MyAddress ma = (MyAddress) expListAdapter.getChild(groupPosition, childPosition);
					delete_btn.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							addressDao.deleteByKey(ma.getId());
							queryFavList();
							expListAdapter.notifyDataSetChanged();
						}
					});

					select_btn.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							new ValidateAddressTask(_activity, isFromContact).execute(ma.getAddress());
						}
					});
				}
				// contact list can add contact_fav or select
				if (groupPosition == 1) {
					favorite_btn.setVisibility(View.GONE);
					contact_favorite_btn.setVisibility(View.VISIBLE);
					save_btn.setVisibility(View.GONE);
					delete_btn.setVisibility(View.GONE);
					select_btn.setVisibility(View.VISIBLE);
					final boolean isFromContact = true;
					final MyAddress ma = (MyAddress) expListAdapter.getChild(groupPosition, childPosition);

					contact_favorite_btn.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							new addFavoriteTask(_activity, isFromContact).execute(ma.getAddress());
						}
					});

					select_btn.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							new ValidateAddressTask(_activity, isFromContact).execute(ma.getAddress());
						}
					});

				}
				return true;

			}
		});

		expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// change the buttons on the buttom
				favorite_btn.setVisibility(View.VISIBLE);
				save_btn.setVisibility(View.VISIBLE);
				delete_btn.setVisibility(View.GONE);
				select_btn.setVisibility(View.GONE);
				contact_favorite_btn.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		// In the case onPause() is called during a fling the image loader is
		// un-paused to let any remaining background work complete.
		mImageLoader.setPauseWork(false);
	}

	@Override
	// autocomlete field listener, hide keyboard
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);
	}

	private int getListPreferredItemHeight() {
		final TypedValue typedValue = new TypedValue();

		// Resolve list item preferred height theme attribute into typedValue
		this.getTheme().resolveAttribute(android.R.attr.listPreferredItemHeight, typedValue, true);

		// Create a new DisplayMetrics object
		final DisplayMetrics metrics = new android.util.DisplayMetrics();

		// Populate the DisplayMetrics
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// Return theme value based on DisplayMetrics
		return (int) typedValue.getDimension(metrics);
	}

	/*
	 * Preparing the list data
	 */
	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<MyAddress>>();
		mContactList = new ArrayList<MyAddress>();
		// Adding child data
		listDataHeader.add("Favorites");
		listDataHeader.add("Contacts");

		readContacts();
		List<MyAddress> empty = new ArrayList<MyAddress>();
		listDataChild.put(listDataHeader.get(0), empty); // Header, Child data

		// if (!mContactList.isEmpty())
		listDataChild.put(listDataHeader.get(1), mContactList);
	}

	// update the fav list in expListAdapter
	private void queryFavList() {
		List<DBAddress> favList = addressDao.queryBuilder().where(Properties.IsFavoriate.eq(true)).list();
		ArrayList<MyAddress> maList = AddressDaoManager.dbAddressListToMyAddressList(favList);
		expListAdapter.updateFavlist(maList);
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

	public void readContacts() {

		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		if (cur.getCount() > 0) {

			while (cur.moveToNext()) {

				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String street = "";
				Uri img_uri;

				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

					System.out.println("name : " + name + ", ID : " + id);

					// get the phone number
					Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);
					while (pCur.moveToNext()) {
						String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						System.out.println("phone" + phone);
					}
					pCur.close();

					Uri postal_uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
					Cursor postal_cursor = getContentResolver().query(postal_uri, null, ContactsContract.Data.CONTACT_ID + "=" + id, null, null);
					while (postal_cursor.moveToNext()) {
						street = postal_cursor.getString(postal_cursor.getColumnIndex(StructuredPostal.STREET));
					}
					postal_cursor.close();

					// ImageView profile = (ImageView)findViewById(R.id.imageView1);
					img_uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id);

					Logger.e("uri: " + img_uri);

					if (street != null && !street.equalsIgnoreCase("")) {
						MyAddress maddr = new MyAddress(img_uri, name, street, (long) -1.0);
						mContactList.add(maddr);
					}
				}

			}
			cur.close();
		}

	}

	private Bitmap loadContactPhotoThumbnail(String photoData, int imageSize) {

		// Instantiates an AssetFileDescriptor. Given a content Uri pointing to an image file, the
		// ContentResolver can return an AssetFileDescriptor for the file.
		AssetFileDescriptor afd = null;

		// This "try" block catches an Exception if the file descriptor returned from the Contacts
		// Provider doesn't point to an existing file.
		try {
			Uri thumbUri;
			// If Android 3.0 or later, converts the Uri passed as a string to a Uri object.
			if (Utils.hasHoneycomb()) {
				thumbUri = Uri.parse(photoData);
			} else {
				// For versions prior to Android 3.0, appends the string argument to the content
				// Uri for the Contacts table.
				final Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, photoData);

				// Appends the content Uri for the Contacts.Photo table to the previously
				// constructed contact Uri to yield a content URI for the thumbnail image
				thumbUri = Uri.withAppendedPath(contactUri, Photo.CONTENT_DIRECTORY);
			}
			// Retrieves a file descriptor from the Contacts Provider. To learn more about this
			// feature, read the reference documentation for
			// ContentResolver#openAssetFileDescriptor.
			afd = this.getContentResolver().openAssetFileDescriptor(thumbUri, "r");

			// Gets a FileDescriptor from the AssetFileDescriptor. A BitmapFactory object can
			// decode the contents of a file pointed to by a FileDescriptor into a Bitmap.
			FileDescriptor fileDescriptor = afd.getFileDescriptor();

			if (fileDescriptor != null) {
				// Decodes a Bitmap from the image pointed to by the FileDescriptor, and scales it
				// to the specified width and height
				return ImageLoader.decodeSampledBitmapFromDescriptor(fileDescriptor, imageSize, imageSize);
			}
		} catch (FileNotFoundException e) {
			// If the file pointed to by the thumbnail URI doesn't exist, or the file can't be
			// opened in "read" mode, ContentResolver.openAssetFileDescriptor throws a
			// FileNotFoundException.
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Contact photo thumbnail not found for contact " + photoData + ": " + e.toString());
			}
		} finally {
			// If an AssetFileDescriptor was returned, try to close it
			if (afd != null) {
				try {
					afd.close();
				} catch (IOException e) {
					// Closing a file descriptor might cause an IOException if the file is
					// already closed. Nothing extra is needed to handle this.
				}
			}
		}

		// If the decoding failed, returns null
		return null;
	}

	protected class ValidateAddressTask extends AsyncTask<String, Void, List<Address>> {

		// Store the context passed to the AsyncTask when the system instantiates it.
		Context localContext;
		boolean isFromContact;

		// Constructor called by the system to instantiate the task
		public ValidateAddressTask(Context context, boolean isFromContact) {

			// Required by the semantics of AsyncTask
			super();

			// Set a Context for the background task
			localContext = context;
			this.isFromContact = isFromContact;
		}

		/**
		 * Get a geocoding service instance, pass latitude and longitude to it, format the returned address, and return the address to the UI thread.
		 */
		@Override
		protected List<Address> doInBackground(String... params) {
			/*
			 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform to address standards can also be used.
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
			if (addresses.size() > 1) {
				// pop up list
				setUpListDialog(_activity, LocationUtils.addressListToStringList(_activity, addresses), addresses);
			} else if (addresses.size() == 1) {
				if (Utils.isNumeric(AddressDaoManager.getHouseNumberFromAddress(addresses.get(0)))) {
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

	protected class addFavoriteTask extends AsyncTask<String, Void, List<Address>> {

		// Store the context passed to the AsyncTask when the system instantiates it.
		Context localContext;
		boolean isFromContext;

		// Constructor called by the system to instantiate the task
		public addFavoriteTask(Context context, boolean isFromContact) {

			// Required by the semantics of AsyncTask
			super();

			// Set a Context for the background task
			localContext = context;
			this.isFromContext = isFromContact;
		}

		/**
		 * Get a geocoding service instance, pass latitude and longitude to it, format the returned address, and return the address to the UI thread.
		 */
		@Override
		protected List<Address> doInBackground(String... params) {
			/*
			 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform to address standards can also be used.
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
				setUpListDialog(_activity, LocationUtils.addressListToStringList(_activity, addresses), addresses);
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
						}
					});
					add = (TextView) nicknameDialog.getWindow().findViewById(R.id.add);
					add.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							String nickname = nickname_edit.getText().toString();
							DBAddress dbAddress = AddressDaoManager.addDaoAddressByAddress(addresses.get(0), tv_unitNumber.getText().toString(), nickname, true, addressDao);
							queryFavList();
							Toast.makeText(_activity, dbAddress.getNickName() + " is successfully added", Toast.LENGTH_SHORT).show();
							nicknameDialog.dismiss();
						}
					});

					nicknameDialog.show();
				} else {
					// Toast.makeText(_activity, "invalid street number", Toast.LENGTH_SHORT).show();
					if (isFromContext) {
						Utils.showErrorDialog(_activity.getString(R.string.err_invalid_street_number), _activity);
					} else {
						tv_streetNumber.requestFocus();
						((EditText) tv_streetNumber).setError(_activity.getString(R.string.err_invalid_street_number));
					}
				}
			} else {
				// Toast.makeText(_activity, "invalid address", Toast.LENGTH_SHORT).show();
				if (isFromContext) {
					Utils.showErrorDialog(_activity.getString(R.string.err_invalid_street_name), _activity);
				} else {
					autoCompView.requestFocus();
					((AutoCompleteTextView) autoCompView).setError(_activity.getString(R.string.err_invalid_street_name));
				}
			}

		}
	}

	private void setUpListDialog(final Context context, ArrayList<String> addresses, final List<Address> addressesObj) {
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
				Intent returnIntent = new Intent();
				returnIntent.putExtra(MBDefinition.ADDRESS, addressesObj.get(which));
				setResult(RESULT_OK, returnIntent);
				finish();
			}
		});
		builderSingle.show();
	}

}
