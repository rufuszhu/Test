package digital.dispatch.TaxiLimoNewUI.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import digital.dispatch.TaxiLimoNewUI.Book.ModifyAddressActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.AddFavoriteTask;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import digital.dispatch.TaxiLimoNewUI.Widget.SwipableListItem;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	private ArrayList<String> resultList;
	private static final String LOG_TAG = "PlacesAutoCompleteAdapter";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static Context _context;
	private Typeface OpenSansRegular;
	private Typeface fontFamily;
	private Typeface icon_pack;
	private static final String API_KEY = "AIzaSyBZqkyqueJbnKhaUyudF6P6HwGTh1PtHVk";
	private static final String TAG = "PlacesAutoCompleteAdapter";
	
	private boolean mSwiping = false;
	private boolean mItemPressed = false;

	// private TextView _streetNumber;

	// public PlacesAutoCompleteAdapter(Context context, int textViewResourceId, TextView streetNumber) {
	public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		_context = context;
		resultList = new ArrayList<>();
        OpenSansRegular = FontCache.getFont(context, "fonts/OpenSansRegular.ttf");
		fontFamily = FontCache.getFont(context, "fonts/fontawesome.ttf");
		icon_pack = FontCache.getFont(context, "fonts/icon_pack.ttf");
	}

	@Override
	public int getCount() {
		if (resultList == null)
			return 0;
		return resultList.size();
	}

	@Override
	public String getItem(int index) {
		if (resultList == null)
			return "";
		return resultList.get(index);
	}

	private class ViewHolder {
		public TextView icon;
		public TextView bold;
		public TextView notBold;
		public RelativeLayout contact_option;
		public LinearLayout viewHeader;
		public ViewGroup swipeContactView;
		public TextView green_circle;
		public TextView add_fav_btn;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		// reuse views
		if (rowView == null) {
			rowView = LayoutInflater.from(getContext()).inflate(R.layout.search_list_item, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.icon = (TextView) rowView.findViewById(R.id.icon);
			viewHolder.bold = (TextView) rowView.findViewById(R.id.bold);
			viewHolder.notBold = (TextView) rowView.findViewById(R.id.notBold);
			viewHolder.contact_option = (RelativeLayout) rowView.findViewById(R.id.contact_option);
			viewHolder.viewHeader = (LinearLayout) rowView.findViewById(R.id.viewHeader);
			viewHolder.swipeContactView = (ViewGroup) rowView.findViewById(R.id.swipeContactView);
			viewHolder.green_circle = (TextView) rowView.findViewById(R.id.green_circle);
			viewHolder.add_fav_btn = (TextView) rowView.findViewById(R.id.add_fav_btn);
			rowView.setTag(viewHolder);
		}

		// fill data
		final ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.add_fav_btn.setTypeface(fontFamily);
		holder.add_fav_btn.setText(MBDefinition.icon_tab_fav);
		
		
		if (position % 2 == 1) {
			holder.viewHeader.setBackgroundResource(R.drawable.list_background2_selector);
		}
		
		final TextView green_circle = holder.green_circle;
		holder.contact_option.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Animation pop = AnimationUtils.loadAnimation(_context, R.anim.pop);
				pop.setFillAfter(true);
				pop.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						((SwipableListItem) (holder.swipeContactView.findViewById(R.id.swipeContactView))).maximize();
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}
				});

				green_circle.setVisibility(View.VISIBLE);
				green_circle.startAnimation(pop);

				new AddFavoriteTask(_context).execute(resultList.get(position));

			}
		});
		
		holder.icon.setTypeface(icon_pack);
		holder.icon.setText(MBDefinition.icon_location);

		holder.notBold.setVisibility(View.GONE);

		if (position < resultList.size()) {
			holder.bold.setTypeface(OpenSansRegular, Typeface.NORMAL);
			holder.bold.setText(resultList.get(position));
		}
		
		holder.viewHeader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((ModifyAddressActivity) _context).searchFragment.callValidateAddressTask(resultList.get(position));
			}
		});

		holder.swipeContactView.setOnTouchListener(mTouchListener);

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
					// Logger.d(LOG_TAG, "Street number: " + _streetNumber.getText().toString());
					// resultList = autocomplete(_streetNumber.getText().toString() + " " + constraint.toString());
					queryResults = autocomplete(constraint.toString());
					// Assign the data to the FilterResults
					filterResults.values = queryResults;
					if (queryResults != null)
						filterResults.count = queryResults.size();
					else
						filterResults.count = 0;
				}

				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<String> temp = new ArrayList<>();
                final Object myListObj = results.values;
                if(myListObj instanceof List<?>) {
                    for (int i=0; i<((List) myListObj).size(); i++) {
                        if (((List) myListObj).get(i) instanceof String) {
                            temp.add((String)((List) myListObj).get(i));
                        }
                    }
                }

				resultList = temp;
				if (results.count > 0) {
					((ModifyAddressActivity) _context).searchFragment.resetGoogleListViewHight();
					((ModifyAddressActivity) _context).searchFragment.hideNoResultFoundLayout();
					notifyDataSetChanged();
				} else {
					((ModifyAddressActivity) _context).searchFragment.displayNoResultFoundIfNoResultFound();
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
			String RADIUS = "150000";// 150 KM
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			// sb.append("&components=country:" + _context.getString(R.string.default_country_code));
			if (Utils.mPickupAddress != null) {
				sb.append("&location=" + Utils.mPickupAddress.getLatitude() + "," + Utils.mPickupAddress.getLongitude());
				sb.append("&radius=" + RADIUS);
			}
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
			resultList = new ArrayList<>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
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
				mSwipeSlop = ViewConfiguration.get(_context).getScaledTouchSlop();
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
//						mSwiping = true;
//						mListView.requestDisallowInterceptTouchEvent(true);
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
}