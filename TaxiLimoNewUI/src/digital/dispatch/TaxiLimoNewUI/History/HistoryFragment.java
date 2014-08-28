package digital.dispatch.TaxiLimoNewUI.History;

import java.util.ArrayList;
import java.util.List;

import com.digital.dispatch.TaxiLimoSQLDatabase.MBBooking;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.BookingListAdapter;
import digital.dispatch.TaxiLimoNewUI.Book.ModifyAddressActivity;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.R.menu;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class HistoryFragment extends ListFragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static HistoryFragment newInstance() {
		HistoryFragment fragment = new HistoryFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, 3);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		DaoManager daoManager = DaoManager.getInstance(getActivity());
		DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
		List<DBBooking> values = bookingDao.queryBuilder()
				.whereOr(Properties.TripStatus.eq(MBDefinition.MB_STATUS_CANCELLED), 
						 Properties.TripStatus.notEq(MBDefinition.MB_STATUS_COMPLETED)).list();
		
		



		BookingListAdapter adapter = new BookingListAdapter(getActivity(), values);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		DBBooking item = (DBBooking) getListAdapter().getItem(position);
		Intent intent = new Intent(getActivity(), TripDetailActivity.class);
		intent.putExtra(MBDefinition.DBBOOKING_EXTRA, item);
		startActivity(intent);
		Toast.makeText(getActivity(), position + " selected", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// only show refresh if drawer is not open.
		if (!((MainActivity) getActivity()).getDrawerFragment().isDrawerOpen())
			inflater.inflate(R.menu.main, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_history, container, false);
		return rootView;
	}

}