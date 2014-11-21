package digital.dispatch.TaxiLimoNewUI.Book;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.Toast;
import digital.dispatch.TaxiLimoNewUI.BuildConfig;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao.Properties;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.PlacesAutoCompleteAdapter;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.AppController;
import digital.dispatch.TaxiLimoNewUI.Utils.ImageLoader;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class SearchFragment extends Fragment implements OnItemClickListener {

	private static final String TAG = "SearchFragment";
	private View view;
	private AutoCompleteTextView autoCompView;

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
		return view;
	}

	private void findViewAndBindEvent() {
		autoCompView = (AutoCompleteTextView) view.findViewById(R.id.autocomplete);
		autoCompView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));
		autoCompView.setOnItemClickListener(this);
		
		ImageView clear = (ImageView) view.findViewById(R.id.clear_autocomplete);
		clear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				autoCompView.setText("");
			}
		});
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



}