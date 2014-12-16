package digital.dispatch.TaxiLimoNewUI.Track;

import java.util.List;

import com.android.volley.toolbox.NetworkImageView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao.Properties;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.SendDriverMsgTask;
import digital.dispatch.TaxiLimoNewUI.Utils.AppController;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

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
		Logger.e(TAG, "onCreate");
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
		Typeface icon_pack = Typeface.createFromAsset(getActivity().getAssets(), "fonts/icon_pack.ttf");
		Typeface rionaSansMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RionaSansMedium.otf");
	
		textView.setTypeface(rionaSansMedium);
		icon.setTypeface(icon_pack);
		icon.setText(MBDefinition.ICON_FUNNEL);
		
		
		DBBooking book = ((TrackDetailActivity) getActivity()).getDBBook();
		
		if(book.getTripStatus()==MBDefinition.MB_STATUS_COMPLETED)
			textView.setText(getActivity().getString(R.string.map_not_available_complete));
		else if(book.getTripStatus()==MBDefinition.MB_STATUS_CANCELLED)
			textView.setText(getActivity().getString(R.string.map_not_available_cancel));
		else
			textView.setText(getActivity().getString(R.string.tracking_map_not_available));
	}
	


	public View mGetView() {
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
		
	}
	


}