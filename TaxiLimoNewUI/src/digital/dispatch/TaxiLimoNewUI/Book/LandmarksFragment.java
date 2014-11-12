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
import android.widget.Toast;
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

public class LandmarksFragment extends ListFragment {

	private static final String TAG = "LandmarksFragment";
	private View view;

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_NEARBY_SEARCH = "/nearbysearch";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyB-yx9i6UXvIObombR7xr1gQutmCBye2no";
	
	private LandmarksAdapter adapter;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static LandmarksFragment newInstance() {
		LandmarksFragment fragment = new LandmarksFragment();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.e(TAG, "onCreate");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_contacts, container, false);
		return view;
	}

	public View mGetView() {
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
		searchLandmarks();
	}

	@Override
	public void onPause() {
		super.onPause();

	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		((ModifyAddressActivity) getActivity()).updateAddress(adapter.getValues().get(position).getAddress());
	}

	private void searchLandmarks() {

		new AsyncTask<URL, Integer, ArrayList<MyLandMark>>() {
			protected ArrayList<MyLandMark> doInBackground(URL... urls) {
				ArrayList<MyLandMark> resultList = null;
				HttpURLConnection conn = null;
				StringBuilder jsonResults = new StringBuilder();
				try {
					StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_NEARBY_SEARCH + OUT_JSON);
					sb.append("?key=" + API_KEY);
					sb.append("&location=49.127205,-123.093256&radius=500");
					// sb.append("&input=" + URLEncoder.encode(input, "utf8"));

					URL url = new URL(sb.toString());
					conn = (HttpURLConnection) url.openConnection();
					InputStreamReader in = new InputStreamReader(conn.getInputStream());

					// Load the results into a StringBuilder
					int read;
					char[] buff = new char[1024];
					while ((read = in.read(buff)) != -1) {
						jsonResults.append(buff, 0, read);
					}
				} catch (MalformedURLException e) {
					Log.e(TAG, "Error processing Places API URL", e);

				} catch (IOException e) {
					Log.e(TAG, "Error connecting to Places API", e);
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
				}

				try {
					// Create a JSON object hierarchy from the results
					JSONObject jsonObj = new JSONObject(jsonResults.toString());
					JSONArray predsJsonArray = jsonObj.getJSONArray("results");

					// Extract the Place descriptions from the results
					resultList = new ArrayList<MyLandMark>(predsJsonArray.length());
					for (int i = 0; i < predsJsonArray.length(); i++) {
						Log.e(TAG, "name: " + predsJsonArray.getJSONObject(i).getString("name"));
						Log.e(TAG, "vicinity: " + predsJsonArray.getJSONObject(i).getString("vicinity"));
						
						MyLandMark maddr = new MyLandMark("", predsJsonArray.getJSONObject(i).getString("name"), predsJsonArray.getJSONObject(i).getString("vicinity"), (long) -1.0);
						resultList.add(maddr);
					}
				} catch (JSONException e) {
					Log.e(TAG, "Cannot process JSON results", e);
				}
				return resultList;

			}

			protected void onPostExecute(ArrayList<MyLandMark> resultList) {
				adapter = new LandmarksAdapter(resultList);
				setListAdapter(adapter);
			}
		}.execute();
	}

	private class LandmarksAdapter extends ArrayAdapter<MyLandMark> {

		private List<MyLandMark> values;

		public List<MyLandMark> getValues() {
			return values;
		}

		public void setValues(List<MyLandMark> values) {
			this.values = values;
		}

		public LandmarksAdapter(List<MyLandMark> values) {
			super(getActivity(), R.layout.contact_item, values);
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
			// mImageLoader.loadImage(values.get(position).getImg_URI().toString() + "/photo", holder.profile_icon);
			holder.address.setText(values.get(position).getAddress());
			holder.tv_name.setText(values.get(position).getName());

			return rowView;
		}
	}

}