package digital.dispatch.TaxiLimoNewUI.Book;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao.Properties;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.PlacesAutoCompleteAdapter;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;

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
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_search, container, false);
		findViewAndBindEvent();
		getData();
		
		contactResults = new ArrayList<ListItem>();
		favoriteResults = new ArrayList<ListItem>();
		

		
		contactAdapter = new ContactResultAdapter(getActivity());
		favAdapter = new FavoriteResultAdapter(getActivity());
		listView_contact.setAdapter(contactAdapter);
		listView_favorite.setAdapter(favAdapter);
		return view;
	}
	
	public void getData(){
		allContacts = new ArrayList<MyContact>();
		readContacts();
		daoManager = DaoManager.getInstance(getActivity());
		addressDao = daoManager.getAddressDao(DaoManager.TYPE_READ);
		allFavs = addressDao.queryBuilder().where(Properties.IsFavoriate.eq(true)).list();
	}
	


	private class ListItem{
		public String bold;
		public String notBold;
		
		public ListItem(String bold, String notBold){
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
		listView_google.setAdapter(googleAdapter);
		//autoCompView.setOnItemClickListener(this);
		
		autoCompView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            	contactResults.clear();
            	favoriteResults.clear();
            	if(s.toString().length()>0){
            		searchForContact(s.toString());
            		searchForFavorite(s.toString());
            		googleAdapter.getFilter().filter(s.toString());
            	}
            	contactAdapter.notifyDataSetChanged();
            	favAdapter.notifyDataSetChanged();
            }
        });
		
		ImageView clear = (ImageView) view.findViewById(R.id.clear_autocomplete);
		clear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				autoCompView.setText("");
			}
		});
	}

	private void searchForContact(String string) {
		for(int i = 0; i<allContacts.size();i++){
			MyContact contact = allContacts.get(i);
			if(contact.getAddress().toLowerCase().contains(string.toLowerCase()) || contact.getName().toLowerCase().contains(string.toLowerCase()))
				contactResults.add(new ListItem(contact.getName(),contact.getAddress()));
		}
	}
	
	private void searchForFavorite(String string) {
		for(int i = 0; i<allFavs.size();i++){
			DBAddress address = allFavs.get(i);
			if(address.getFullAddress().toLowerCase().contains(string.toLowerCase()) || address.getNickName().toLowerCase().contains(string.toLowerCase()))
				favoriteResults.add(new ListItem(address.getNickName(),address.getFullAddress()));
		}
	}
	@Override
	// autocomlete field listener, hide keyboard
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		hideKeyBoard();
	}
	
	public void hideKeyBoard(){
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
	
	private class ContactResultAdapter extends ArrayAdapter<ListItem>{
		
		public ContactResultAdapter(Context context) {
			super(context, R.layout.search_list_item, contactResults);
		}

		private class ViewHolder {
			public ImageView icon;
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

				viewHolder.bold = (TextView) rowView.findViewById(R.id.bold);
				viewHolder.notBold = (TextView) rowView.findViewById(R.id.notBold);
				rowView.setTag(viewHolder);
			}
			
			// fill data
			ViewHolder holder = (ViewHolder) rowView.getTag();
			String a = contactResults.get(position).bold;
			holder.bold.setText(a);
			holder.notBold.setText(contactResults.get(position).notBold);
			
			return rowView;
		}
	}
	
	private class FavoriteResultAdapter extends ArrayAdapter<ListItem>{
		
		public FavoriteResultAdapter(Context context) {
			super(context, R.layout.search_list_item, favoriteResults);
		}

		private class ViewHolder {
			public ImageView icon;
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

				viewHolder.bold = (TextView) rowView.findViewById(R.id.bold);
				viewHolder.notBold = (TextView) rowView.findViewById(R.id.notBold);
				rowView.setTag(viewHolder);
			}
			
			// fill data
			ViewHolder holder = (ViewHolder) rowView.getTag();
			String a = favoriteResults.get(position).bold;
			holder.bold.setText(a);
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


}