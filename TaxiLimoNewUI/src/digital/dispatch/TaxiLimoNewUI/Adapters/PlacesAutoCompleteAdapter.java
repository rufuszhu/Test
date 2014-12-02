package digital.dispatch.TaxiLimoNewUI.Adapters;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Book.ModifyAddressActivity;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	private ArrayList<String> resultList;
	private static final String LOG_TAG = "PlacesAutoCompleteAdapter";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static Context _context;
	private Typeface RionaSansMedium;
	private static final String API_KEY = "AIzaSyB-yx9i6UXvIObombR7xr1gQutmCBye2no";
	private static final String TAG = "PlacesAutoCompleteAdapter";

	// private TextView _streetNumber;

	// public PlacesAutoCompleteAdapter(Context context, int textViewResourceId, TextView streetNumber) {
	public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		_context = context;
		resultList = new ArrayList<String>();
		RionaSansMedium = Typeface.createFromAsset(_context.getAssets(), "fonts/RionaSansMedium.otf");
	}

	@Override
	public int getCount() {
		if(resultList==null)
			return 0;
		return resultList.size();
	}

	@Override
	public String getItem(int index) {
		if(resultList==null)
			return "";
		return resultList.get(index);
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
			rowView.setBackgroundColor(_context.getResources().getColor(R.color.list_background2));
		}
		Typeface fontFamily = Typeface.createFromAsset(_context.getAssets(), "fonts/fontawesome.ttf");
		holder.icon.setTypeface(fontFamily);
		holder.icon.setText(MBDefinition.icon_location);

		holder.notBold.setVisibility(View.GONE);
		
		if (position < resultList.size()) {
			holder.bold.setTypeface(RionaSansMedium, Typeface.NORMAL);
			holder.bold.setText(resultList.get(position));
		}

		return rowView;
	}
	


	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				ArrayList<String> queryResults;
				if (constraint != null) {
					// Retrieve the autocomplete results.
					// Logger.e(LOG_TAG, "Street number: " + _streetNumber.getText().toString());
					// resultList = autocomplete(_streetNumber.getText().toString() + " " + constraint.toString());
					queryResults = autocomplete(constraint.toString());
					// Assign the data to the FilterResults
					filterResults.values = queryResults;
					filterResults.count = queryResults.size();
				}
				
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				resultList = (ArrayList<String>)results.values;
				if (results != null && results.count > 0) {
					((ModifyAddressActivity) _context).searchFragment.resetGoogleListViewHight();
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
			
			
		};
		return filter;
	}

	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			//sb.append("&components=country:" + _context.getString(R.string.default_country_code));
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

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
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}
}