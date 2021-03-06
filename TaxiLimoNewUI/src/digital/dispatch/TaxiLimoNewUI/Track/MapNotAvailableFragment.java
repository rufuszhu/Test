package digital.dispatch.TaxiLimoNewUI.Track;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;

public class MapNotAvailableFragment extends Fragment {

	private static final String TAG = "MapNotAvailabeFragment";
	private View view;
	private TextView icon;
	private TextView textView;
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static MapNotAvailableFragment newInstance() {
		MapNotAvailableFragment fragment = new MapNotAvailableFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d(TAG, "onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_map_not_available, container, false);
		findView();
		fillValue();
		
		return view;
	}

	private void findView() {
		icon = (TextView) view.findViewById(R.id.icon);
		textView = (TextView) view.findViewById(R.id.textView);
	}
	
	private void fillValue() {
		Typeface icon_pack = FontCache.getFont(getActivity(), "fonts/icon_pack.ttf");
		Typeface OpenSansSemibold = FontCache.getFont(getActivity(), "fonts/OpenSansSemibold.ttf");
	
		textView.setTypeface(OpenSansSemibold);
		icon.setTypeface(icon_pack);
		icon.setText(MBDefinition.ICON_FUNNEL);
		
		
		DBBooking book = ((TrackDetailActivity) getActivity()).getDBBook();
		
		if(book.getTripStatus()==MBDefinition.MB_STATUS_COMPLETED)
			textView.setText(getActivity().getString(R.string.map_not_available_complete));
		else if(book.getTripStatus()==MBDefinition.MB_STATUS_CANCELLED)
			textView.setText(getActivity().getString(R.string.map_not_available_cancel));
//		else if(book.getTripStatus()==MBDefinition.MB_STATUS_UNKNOWN) //TL-264
//			textView.setText(getActivity().getString(R.string.map_not_available_complete));
		else
			textView.setText(getActivity().getString(R.string.tracking_map_not_available));
	}
	


	public View mGetView() {
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.d(TAG, "on RESUME");
		
	}
	


}