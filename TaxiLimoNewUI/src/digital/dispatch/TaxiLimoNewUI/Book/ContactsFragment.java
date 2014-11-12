package digital.dispatch.TaxiLimoNewUI.Book;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import digital.dispatch.TaxiLimoNewUI.BuildConfig;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao.Properties;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.AppController;
import digital.dispatch.TaxiLimoNewUI.Utils.ImageLoader;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class ContactsFragment extends ListFragment {

	private static final String TAG = "ContactsFragment";
	private View view;
	private ImageLoader mImageLoader;
	private List<MyContact> mContactList;
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
		Logger.e(TAG,"onCreate");
		mImageLoader = new ImageLoader(getActivity(), getListPreferredItemHeight()) {
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
		mImageLoader.addImageCache(getActivity().getSupportFragmentManager(), 0.1f);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_contacts, container, false);
		return view;
	}
	
	public View mGetView(){
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
		
		mContactList = new ArrayList<MyContact>();
		readContacts();
		
		ContactsAdapter adapter = new ContactsAdapter(getActivity(), mContactList);
		setListAdapter(adapter);
		
		Utils.isInternetAvailable(getActivity());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// In the case onPause() is called during a fling the image loader is
		// un-paused to let any remaining background work complete.
		mImageLoader.setPauseWork(false);
	}
	
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


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		((ModifyAddressActivity) getActivity()).updateAddress(mContactList.get(position).getAddress());
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
						Logger.e(TAG, contactId);
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
		}

		public class ViewHolder {
			public TextView address;
			public TextView tv_name;
			public ImageView profile_icon;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			// reuse views
			if (rowView == null) {

				rowView = LayoutInflater.from(getContext()).inflate(R.layout.contact_item, null);
				// configure view holder
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.profile_icon = (ImageView) rowView.findViewById(R.id.profile_icon);
				viewHolder.address = (TextView) rowView.findViewById(R.id.tv_address);
				viewHolder.tv_name = (TextView) rowView.findViewById(R.id.tv_name);
				rowView.setTag(viewHolder);
			}

			// fill data
			ViewHolder holder = (ViewHolder) rowView.getTag();
			mImageLoader.loadImage(values.get(position).getImg_URI().toString() + "/photo", holder.profile_icon);
			holder.address.setText(values.get(position).getAddress());
			holder.tv_name.setText(values.get(position).getName());

			return rowView;
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
			afd = getActivity().getContentResolver().openAssetFileDescriptor(thumbUri, "r");

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

}