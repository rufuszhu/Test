package digital.dispatch.TaxiLimoNewUI.Book;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.ListFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import digital.dispatch.TaxiLimoNewUI.BuildConfig;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.BookingListAdapter.ViewHolder;
import digital.dispatch.TaxiLimoNewUI.DaoManager.AddressDaoManager;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.ImageLoader;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import digital.dispatch.TaxiLimoNewUI.Widget.SwipableListItem;

public class ContactsFragment extends ListFragment {

	private static final String TAG = "ContactsFragment";
	private View view;
	private ImageLoader mImageLoader;
	private List<MyContact> mContactList;
	private DBAddressDao addressDao;
	private ListView mListView;
	private boolean mSwiping = false;
	private boolean mItemPressed = false;
	private ContactsAdapter adapter;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static ContactsFragment newInstance() {
		ContactsFragment fragment = new ContactsFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.e(TAG, "onCreate");
		mImageLoader = new ImageLoader(getActivity(), getListPreferredItemHeight()) {
			@Override
			protected Bitmap processBitmap(Object data) {
				// This gets called in a background thread and passed the data
				// from
				// ImageLoader.loadImage().
				return loadContactPhotoThumbnail((String) data, getImageSize());
			}
		};

		// Set a placeholder loading image for the image loader
		mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_holo_light);

		// Add a cache to the image loader
		mImageLoader.addImageCache(getActivity().getSupportFragmentManager(), 0.1f);

		DaoManager daoManager = DaoManager.getInstance(getActivity());
		addressDao = daoManager.getAddressDao(DaoManager.TYPE_READ);
		mContactList = new ArrayList<MyContact>();
		readContacts();

		adapter = new ContactsAdapter(getActivity(), mContactList);
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.e(TAG, "onCreateView");
		view = inflater.inflate(R.layout.fragment_contacts, container, false);
		mListView = (ListView) view.findViewById(android.R.id.list);
		return view;
	}

	public View mGetView() {
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
		//Utils.isInternetAvailable(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		// In the case onPause() is called during a fling the image loader is
		// un-paused to let any remaining background work complete.
		mImageLoader.setPauseWork(false);
	}

	/**
	 * Handle touch events to lock list view swiping during swipe and block multiple swipe
	 */
	private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

		float mDownX;
		private int mSwipeSlop = -1;

		@Override
		public boolean onTouch(final View v, MotionEvent event) {
			if (mSwipeSlop < 0) {
				mSwipeSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
			}

			// ((SwipableListItem)
			// (v.findViewById(R.id.swipeContactView))).processDragEvent(event);
			((SwipableListItem) (v)).processDragEvent(event);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mItemPressed) {
					// Multi-item swipes not handled
					return false;
				}
				mItemPressed = true;
				mDownX = event.getX();
				break;
			case MotionEvent.ACTION_CANCEL:
				mSwiping = false;
				mItemPressed = false;
				break;
			case MotionEvent.ACTION_MOVE: {
				float x = event.getX() + v.getTranslationX();
				float deltaX = x - mDownX;
				float deltaXAbs = Math.abs(deltaX);
				if (!mSwiping) {
					if (deltaXAbs > mSwipeSlop) {
						mSwiping = true;
						mListView.requestDisallowInterceptTouchEvent(true);
					}
				}
				// if (mSwiping) {
				// //
				// ((SwipableListItem)(v.findViewById(R.id.swipeContactView))).processDragEvent(event);
				// }
			}
				break;
			case MotionEvent.ACTION_UP: {
				mSwiping = false;
				mItemPressed = false;
				break;
			}
			default:
				return false;
			}
			return true;
		}
	};

	private int getListPreferredItemHeight() {
		final TypedValue typedValue = new TypedValue();

		// Resolve list item preferred height theme attribute into typedValue
		getActivity().getTheme().resolveAttribute(android.R.attr.listPreferredItemHeight, typedValue, true);

		// Create a new DisplayMetrics object
		final DisplayMetrics metrics = new android.util.DisplayMetrics();

		// Populate the DisplayMetrics
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// Return theme value based on DisplayMetrics
		return (int) typedValue.getDimension(metrics);
	}

	private void readContacts() {
		String[] PROJECTION = new String[] { ContactsContract.Data.CONTACT_ID, ContactsContract.Contacts.DISPLAY_NAME, StructuredPostal.STREET };

		ContentResolver cr = getActivity().getContentResolver();
		Cursor cursor = cr.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, PROJECTION, null, null, ContactsContract.Contacts.DISPLAY_NAME);

		if (cursor != null) {
			try {
				final int contactIdIndex = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
				final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				final int streetIndex = cursor.getColumnIndex(StructuredPostal.STREET);
				String contactId;
				String displayName, street;
				while (cursor.moveToNext()) {
					street = cursor.getString(streetIndex);
					if (street != null && !street.equalsIgnoreCase("")) {
						contactId = cursor.getString(contactIdIndex);
						displayName = cursor.getString(displayNameIndex);
						Uri img_uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);

						MyContact maddr = new MyContact(img_uri, displayName, street, (long) -1.0);
						mContactList.add(maddr);
					}
				}
			} finally {
				cursor.close();
			}
		}
	}

	private class ContactsAdapter extends ArrayAdapter<MyContact> {

		private final Context context;
		private List<MyContact> values;
		private Typeface fontFamily;
		private Typeface RionaSansMedium;
		private Typeface RionaSansRegular;
		
		public List<MyContact> getValues() {
			return values;
		}

		public void setValues(List<MyContact> values) {
			this.values = values;
		}

		public ContactsAdapter(Context context, List<MyContact> values) {
			super(context, R.layout.contact_item, values);
			this.context = context;
			this.values = values;
			fontFamily = Typeface.createFromAsset(getActivity().getAssets(), "fonts/fontawesome.ttf");
			RionaSansMedium = Typeface.createFromAsset(context.getAssets(), "fonts/RionaSansMedium.otf");
			RionaSansRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RionaSansRegular.otf");
		}

		public class ViewHolder {
			public TextView address;
			public TextView tv_name;
			public TextView add_fav_btn;
			public ImageView profile_icon;
			public RelativeLayout contact_option;
			public LinearLayout viewHeader;
			public ViewGroup swipeContactView;
			public TextView green_circle;
			

		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			// reuse views
			if (convertView == null) {

				convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_item, null);
				// configure view holder
				viewHolder = new ViewHolder();
				viewHolder.profile_icon = (ImageView) convertView.findViewById(R.id.profile_icon);
				viewHolder.address = (TextView) convertView.findViewById(R.id.tv_address);
				viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				viewHolder.contact_option = (RelativeLayout) convertView.findViewById(R.id.contact_option);
				viewHolder.add_fav_btn = (TextView) convertView.findViewById(R.id.add_fav_btn);
				viewHolder.viewHeader = (LinearLayout) convertView.findViewById(R.id.viewHeader);
				viewHolder.swipeContactView = (ViewGroup) convertView.findViewById(R.id.swipeContactView);
				viewHolder.green_circle = (TextView) convertView.findViewById(R.id.green_circle);
				convertView.setTag(viewHolder);
			}
			else{
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final View temp = convertView;

			// fill data

			viewHolder.add_fav_btn.setTypeface(fontFamily);
			viewHolder.add_fav_btn.setText(MBDefinition.icon_tab_fav);
			
			mImageLoader.loadImage(values.get(position).getImg_URI().toString() + "/photo", viewHolder.profile_icon);

			
			viewHolder.tv_name.setTypeface(RionaSansMedium);
			viewHolder.tv_name.setText(values.get(position).getName());
			
			
			viewHolder.address.setTypeface(RionaSansRegular);
			viewHolder.address.setText(values.get(position).getAddress());
			final TextView green_circle = viewHolder.green_circle;
			viewHolder.contact_option.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Animation pop = AnimationUtils.loadAnimation(context, R.anim.pop);
					pop.setFillAfter(true);
					pop.setAnimationListener(new AnimationListener(){

						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							((SwipableListItem) (temp.findViewById(R.id.swipeContactView))).maximize();	
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}});
					
					green_circle.setVisibility(View.VISIBLE);
					green_circle.startAnimation(pop);
					
					new addFavoriteTask(getActivity()).execute(values.get(position).getAddress());
					
				}
			});
			
			if(position%2==1){
				viewHolder.viewHeader.setBackgroundResource(R.drawable.list_background2_selector);
			}
			viewHolder.viewHeader.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new ValidateAddressTask(getActivity()).execute(adapter.getValues().get(position).getAddress());
				}
			});

			viewHolder.swipeContactView.setOnTouchListener(mTouchListener);
			return convertView;
		}
	}

	private Bitmap loadContactPhotoThumbnail(String photoData, int imageSize) {

		// Instantiates an AssetFileDescriptor. Given a content Uri pointing to
		// an image file, the
		// ContentResolver can return an AssetFileDescriptor for the file.
		AssetFileDescriptor afd = null;

		// This "try" block catches an Exception if the file descriptor returned
		// from the Contacts
		// Provider doesn't point to an existing file.
		try {
			Uri thumbUri;
			// If Android 3.0 or later, converts the Uri passed as a string to a
			// Uri object.
			if (Utils.hasHoneycomb()) {
				thumbUri = Uri.parse(photoData);
			} else {
				// For versions prior to Android 3.0, appends the string
				// argument to the content
				// Uri for the Contacts table.
				final Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, photoData);

				// Appends the content Uri for the Contacts.Photo table to the
				// previously
				// constructed contact Uri to yield a content URI for the
				// thumbnail image
				thumbUri = Uri.withAppendedPath(contactUri, Photo.CONTENT_DIRECTORY);
			}
			// Retrieves a file descriptor from the Contacts Provider. To learn
			// more about this
			// feature, read the reference documentation for
			// ContentResolver#openAssetFileDescriptor.
			afd = getActivity().getContentResolver().openAssetFileDescriptor(thumbUri, "r");

			// Gets a FileDescriptor from the AssetFileDescriptor. A
			// BitmapFactory object can
			// decode the contents of a file pointed to by a FileDescriptor into
			// a Bitmap.
			FileDescriptor fileDescriptor = afd.getFileDescriptor();

			if (fileDescriptor != null) {
				// Decodes a Bitmap from the image pointed to by the
				// FileDescriptor, and scales it
				// to the specified width and height
				return ImageLoader.decodeSampledBitmapFromDescriptor(fileDescriptor, imageSize, imageSize);
			}
		} catch (FileNotFoundException e) {
			// If the file pointed to by the thumbnail URI doesn't exist, or the
			// file can't be
			// opened in "read" mode, ContentResolver.openAssetFileDescriptor
			// throws a
			// FileNotFoundException.
			if (BuildConfig.DEBUG) {
				// Log.d(TAG, "Contact photo thumbnail not found for contact " +
				// photoData + ": " + e.toString());
			}
		} finally {
			// If an AssetFileDescriptor was returned, try to close it
			if (afd != null) {
				try {
					afd.close();
				} catch (IOException e) {
					// Closing a file descriptor might cause an IOException if
					// the file is
					// already closed. Nothing extra is needed to handle this.
				}
			}
		}

		// If the decoding failed, returns null
		return null;
	}

	protected class addFavoriteTask extends AsyncTask<String, Void, List<Address>> {

		// Store the context passed to the AsyncTask when the system
		// instantiates it.
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

			// Try to get an address for the current location. Catch IO or
			// network problems.
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
				setUpListDialog(getActivity(), LocationUtils.addressListToStringList(getActivity(), addresses), addresses, isSave);
			} else if (addresses.size() == 1) {
				if (Utils.isNumeric(AddressDaoManager.getHouseNumberFromAddress(addresses.get(0)))) {
					setUpEnterNickNameDialog(addresses.get(0));
				} else {
					// street number is invalid
				}
			} else {
				// R.string.err_invalid_street_name
			}
		}
	}

	private void setUpListDialog(final Context context, final ArrayList<String> addresses, final List<Address> addressesObj, final boolean isSave) {
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
					if (((ModifyAddressActivity) getActivity()).getIsDesitination())
						Utils.mDropoffAddress = addressesObj.get(0);
					else
						Utils.mPickupAddress = addressesObj.get(0);
					getActivity().finish();
				} else
					new addFavoriteTask(getActivity()).execute(addresses.get(which));
			}
		});
		builderSingle.show();
	}

	protected class ValidateAddressTask extends AsyncTask<String, Void, List<Address>> {

		// Store the context passed to the AsyncTask when the system
		// instantiates it.
		Context localContext;

		// Constructor called by the system to instantiate the task
		public ValidateAddressTask(Context context) {

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

			// Try to get an address for the current location. Catch IO or
			// network problems.
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
				Utils.showMessageDialog(getActivity().getString(R.string.cannot_get_address_from_google), getActivity());
			} else if (addresses.size() > 1) {
				// pop up list
				boolean isSave = true;
				setUpListDialog(getActivity(), LocationUtils.addressListToStringList(getActivity(), addresses), addresses, isSave);
			} else if (addresses.size() == 1) {
				if (Utils.isNumeric(AddressDaoManager.getHouseNumberFromAddress(addresses.get(0)))) {
					if (((ModifyAddressActivity) getActivity()).getIsDesitination())
						Utils.mDropoffAddress = addresses.get(0);
					else
						Utils.mPickupAddress = addresses.get(0);
					getActivity().finish();
				} else {
					setUpEnterNickNameDialog(addresses.get(0));
				}
			} else {
				Utils.showErrorDialog(getActivity().getString(R.string.err_invalid_street_name), getActivity());

			}
		}
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
					((ModifyAddressActivity)getActivity()).favoritesFragment.notifyChange();
					nicknameDialog.dismiss();
				}
			}
		});
		nicknameDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		nicknameDialog.show();
	}
}