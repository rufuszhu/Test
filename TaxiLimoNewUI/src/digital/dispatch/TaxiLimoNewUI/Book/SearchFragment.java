package digital.dispatch.TaxiLimoNewUI.Book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter.FilterListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao.Properties;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.PlacesAutoCompleteAdapter;
import digital.dispatch.TaxiLimoNewUI.Book.FavoritesFragment.ValidateAddressTask;
import digital.dispatch.TaxiLimoNewUI.DaoManager.AddressDaoManager;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class SearchFragment extends Fragment implements OnItemClickListener {

	private static final String TAG = "SearchFragment";
	private View view;
	private AutoCompleteTextView autoCompView;
	private DaoManager daoManager;
	private DBAddressDao addressDao;

	private ListView listView_favorite;
	private ListView listView_contact;
	private ListView listView_google;

	private List<ListItem> contactResults;
	private List<ListItem> favoriteResults;
	private List<MyContact> allContacts;
	private List<DBAddress> allFavs;
	private ContactResultAdapter contactAdapter;
	private FavoriteResultAdapter favAdapter;
	private PlacesAutoCompleteAdapter googleAdapter;
	
	private RelativeLayout rl_no_result;
	private TextView no_result_icon, tv_no_result, tv_street, power_by_google;
	private Typeface fontFamily,rionaSansMedium,rionaRegularFamily,fontAwesome;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static SearchFragment newInstance() {
		SearchFragment fragment = new SearchFragment();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.e(TAG, "onCreate");
		getData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_search, container, false);
		Logger.e(TAG, "onCreateView");
		findViewAndBindEvent();
		
		fontFamily = Typeface.createFromAsset(getActivity().getAssets(), "fonts/icon_pack.ttf");
		rionaSansMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RionaSansMedium.otf");
		rionaRegularFamily = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RionaSansRegular.otf");
		fontAwesome = Typeface.createFromAsset(getActivity().getAssets(), "fonts/fontawesome.ttf");

		contactResults = new ArrayList<ListItem>();
		favoriteResults = new ArrayList<ListItem>();

		contactAdapter = new ContactResultAdapter(getActivity());
		favAdapter = new FavoriteResultAdapter(getActivity());
		listView_contact.setAdapter(contactAdapter);
		listView_favorite.setAdapter(favAdapter);

		OnTouchListener dismissScrollListener = new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				hideKeyBoard();
				return (event.getAction() == MotionEvent.ACTION_MOVE);
			}
		};
		listView_contact.setOnTouchListener(dismissScrollListener);
		listView_google.setOnTouchListener(dismissScrollListener);
		listView_favorite.setOnTouchListener(dismissScrollListener);

		return view;
	}

	public void getData() {
		allContacts = new ArrayList<MyContact>();
		readContacts();
		daoManager = DaoManager.getInstance(getActivity());
		addressDao = daoManager.getAddressDao(DaoManager.TYPE_READ);
		allFavs = addressDao.queryBuilder().where(Properties.IsFavoriate.eq(true)).list();
	}

	public void resetGoogleListViewHight() {
		setListViewHeightBasedOnChildren(listView_google);
	}

	private class ListItem {
		public String bold;
		public String notBold;

		public ListItem(String bold, String notBold) {
			this.bold = bold;
			this.notBold = notBold;
		}
	}

	private void findViewAndBindEvent() {
		listView_contact = (ListView) view.findViewById(R.id.listView_contact);
		listView_favorite = (ListView) view.findViewById(R.id.listView_favorite);
		listView_google = (ListView) view.findViewById(R.id.listView_google);
		autoCompView = (AutoCompleteTextView) view.findViewById(R.id.autocomplete);
		googleAdapter = new PlacesAutoCompleteAdapter(getActivity(), R.layout.search_list_item);
		
		rl_no_result = (RelativeLayout) view.findViewById(R.id.rl_no_result);
		no_result_icon = (TextView) view.findViewById(R.id.no_result_icon);
		tv_no_result = (TextView) view.findViewById(R.id.tv_no_result);
		tv_street = (TextView) view.findViewById(R.id.tv_street);
		power_by_google = (TextView) view.findViewById(R.id.power_by_google);
		
		listView_google.setAdapter(googleAdapter);
		// autoCompView.setOnItemClickListener(this);

		no_result_icon.setTypeface(fontFamily);
		no_result_icon.setText(MBDefinition.icon_tab_search);
		

		tv_no_result.setTypeface(rionaSansMedium);
		tv_street.setTypeface(rionaSansMedium);
		
		
		autoCompView.setTypeface(rionaRegularFamily);
		if (((ModifyAddressActivity) getActivity()).getIsDesitination())
			autoCompView.setHint(getActivity().getString(R.string.enter_dropoff_address));
		else
			autoCompView.setHint(getActivity().getString(R.string.enter_pickup_address));
		autoCompView.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				googleAdapter.getFilter().filter(s.toString());
				contactResults.clear();
				favoriteResults.clear();
				if (s.toString().length() > 0) {
					searchForContact(s.toString());
					searchForFavorite(s.toString());
				}
//				googleAdapter.notifyDataSetChanged();
//				googleAdapter.getFilter().filter(s.toString(), new FilterListener(){
//					@Override
//					public void onFilterComplete(int count) {
//						googleAdapter.notifyDataSetChanged();
//					}});
				contactAdapter.notifyDataSetChanged();
				favAdapter.notifyDataSetChanged();
				setListViewHeightBasedOnChildren(listView_contact);
				setListViewHeightBasedOnChildren(listView_favorite);
				
				//listView_google.requestLayout();
				googleAdapter.notifyDataSetChanged();
				
				
			}
		});

		ImageView clear = (ImageView) view.findViewById(R.id.clear_autocomplete);
		clear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				autoCompView.setText("");
			}
		});
		
		listView_contact.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				new ValidateAddressTask(getActivity()).execute(contactResults.get(position).notBold);
			}
		});
		
		listView_favorite.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				new ValidateAddressTask(getActivity()).execute(favoriteResults.get(position).notBold);
			}
		});
		
		listView_google.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				new ValidateAddressTask(getActivity()).execute((String)listView_google.getItemAtPosition(position));
			}
		});
	}
	
	//this function gets called in PlacesAutoCompleteAdapter when no result returned by google
	public void displayNoResultFoundIfNoResultFound(){
		if(checkNoLocalResultFound() && autoCompView.getText().toString().length()>0){
			rl_no_result.setVisibility(View.VISIBLE);
			tv_street.setText("\"" + autoCompView.getText().toString() + "\"");
			power_by_google.setVisibility(View.GONE);
		}
		else{
			hideNoResultFoundLayout();
		}
	}
	
	public void hideNoResultFoundLayout(){
		rl_no_result.setVisibility(View.GONE);
		power_by_google.setVisibility(View.VISIBLE);
	}
	
	private boolean checkNoLocalResultFound(){
		return (contactResults.size()==0 && favoriteResults.size()==0);
	}

	private void searchForContact(String string) {
		for (int i = 0; i < allContacts.size(); i++) {
			MyContact contact = allContacts.get(i);
			if (contact.getAddress().toLowerCase().contains(string.toLowerCase()) || contact.getName().toLowerCase().contains(string.toLowerCase()))
				contactResults.add(new ListItem(contact.getName(), contact.getAddress()));
		}
	}

	private void searchForFavorite(String string) {
		for (int i = 0; i < allFavs.size(); i++) {
			DBAddress address = allFavs.get(i);
			if (address.getFullAddress().toLowerCase().contains(string.toLowerCase()) || address.getNickName().toLowerCase().contains(string.toLowerCase()))
				favoriteResults.add(new ListItem(address.getNickName(), address.getFullAddress()));
		}
	}

	@Override
	// autocomlete field listener, hide keyboard
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		hideKeyBoard();
	}

	public void hideKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);
	}

	public View mGetView() {
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");

	}

	@Override
	public void onPause() {
		super.onPause();

	}

	private class ContactResultAdapter extends ArrayAdapter<ListItem> {

		public ContactResultAdapter(Context context) {
			super(context, R.layout.search_list_item, contactResults);
		}

		private class ViewHolder {
			public TextView icon;
			public TextView bold;
			public TextView notBold;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			// reuse views
			if (rowView == null) {
				rowView = LayoutInflater.from(getContext()).inflate(R.layout.search_list_item, null);
				// configure view holder
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.icon = (TextView) rowView.findViewById(R.id.icon);
				viewHolder.bold = (TextView) rowView.findViewById(R.id.bold);
				viewHolder.notBold = (TextView) rowView.findViewById(R.id.notBold);
				rowView.setTag(viewHolder);
			}

			// fill data
			ViewHolder holder = (ViewHolder) rowView.getTag();
			if(position%2==1){
				rowView.setBackgroundResource(R.drawable.list_background2_selector);
			}
			
			holder.icon.setTypeface(fontFamily);
			holder.icon.setText(MBDefinition.icon_phone);

			String a = contactResults.get(position).bold;
			
			holder.bold.setTypeface(rionaSansMedium, Typeface.BOLD);
			holder.bold.setText(a);
			
			holder.notBold.setTypeface(rionaSansMedium, Typeface.NORMAL);
			holder.notBold.setText(contactResults.get(position).notBold);

			return rowView;
		}
	}

	private class FavoriteResultAdapter extends ArrayAdapter<ListItem> {

		public FavoriteResultAdapter(Context context) {
			super(context, R.layout.search_list_item, favoriteResults);
		}

		private class ViewHolder {
			public TextView icon;
			public TextView bold;
			public TextView notBold;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			// reuse views
			if (rowView == null) {
				rowView = LayoutInflater.from(getContext()).inflate(R.layout.search_list_item, null);
				// configure view holder
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.icon = (TextView) rowView.findViewById(R.id.icon);
				viewHolder.bold = (TextView) rowView.findViewById(R.id.bold);
				viewHolder.notBold = (TextView) rowView.findViewById(R.id.notBold);
				rowView.setTag(viewHolder);
			}

			// fill data
			ViewHolder holder = (ViewHolder) rowView.getTag();
			if(position%2==1){
				rowView.setBackgroundResource(R.drawable.list_background2_selector);
			}
			
			holder.icon.setTypeface(fontAwesome);
			holder.icon.setText(MBDefinition.icon_tab_fav);

			String a = favoriteResults.get(position).bold;
			
			holder.bold.setTypeface(rionaSansMedium, Typeface.BOLD);
			holder.bold.setText(a);
			
			holder.notBold.setTypeface(rionaSansMedium, Typeface.NORMAL);
			holder.notBold.setText(favoriteResults.get(position).notBold);

			return rowView;
		}
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
						allContacts.add(maddr);
					}
				}
			} finally {
				cursor.close();
			}
		}
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = listAdapter.getCount() * dpToPx(55);
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight;
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	private int dpToPx(int dp) {
		float density = getActivity().getApplicationContext().getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}
	
	protected class ValidateAddressTask extends AsyncTask<String, Void, List<Address>> {

		// Store the context passed to the AsyncTask when the system instantiates it.
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
				Utils.showMessageDialog(getActivity().getString(R.string.cannot_get_address_from_google), getActivity());
			} else if (addresses.size() > 1) {
				// pop up list
				setUpListDialog(getActivity(), LocationUtils.addressListToStringList(getActivity(), addresses), addresses);
			} else if (addresses.size() == 1) {
				if (Utils.isNumeric(AddressDaoManager.getHouseNumberFromAddress(addresses.get(0)))) {
					if (((ModifyAddressActivity) getActivity()).getIsDesitination())
						Utils.mDropoffAddress = addresses.get(0);
					else
						Utils.mPickupAddress = addresses.get(0);
					getActivity().finish();
				} else {
					Utils.showErrorDialog(getActivity().getString(R.string.err_invalid_street_number), getActivity());

				}
			} else {
				Utils.showErrorDialog(getActivity().getString(R.string.err_invalid_street_name), getActivity());

			}
		}
	}

	private void setUpListDialog(final Context context, final ArrayList<String> addresses, final List<Address> addressesObj) {
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

				if (((ModifyAddressActivity) getActivity()).getIsDesitination())
					Utils.mDropoffAddress = addressesObj.get(which);
				else
					Utils.mPickupAddress = addressesObj.get(which);
				getActivity().finish();
			}
		});
		builderSingle.show();
	}

}