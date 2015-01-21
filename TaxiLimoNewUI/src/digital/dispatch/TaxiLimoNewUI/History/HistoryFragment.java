package digital.dispatch.TaxiLimoNewUI.History;

import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.greenrobot.dao.query.QueryBuilder;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.BookingListAdapter;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class HistoryFragment extends ListFragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String TAG = "HistoryFragment";
	// Maximun nubmer of jobs can be saved in Database
	private static final int MAX_HISTORY_CAP = 20;
	private QueryBuilder<DBBooking> qb;
	private DBBookingDao bookingDao;

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
		bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
		qb = bookingDao.queryBuilder()
				.whereOr(Properties.TripStatus.eq(MBDefinition.MB_STATUS_CANCELLED), 
						Properties.TripStatus.eq(MBDefinition.MB_STATUS_COMPLETED),
						Properties.TripStatus.eq(MBDefinition.MB_STATUS_UNKNOWN)) //TL-264
				.orderDesc(Properties.TripCreationTime).limit(MAX_HISTORY_CAP);
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.d(TAG, "on RESUME");

		List<DBBooking> values = qb.list();

		// delete all older jobs
		if (values.size() > 0) {
			long smallestId = values.get(values.size() - 1).getId();
			bookingDao.queryBuilder().where(Properties.Id.lt(smallestId)).buildDelete().executeDeleteWithoutDetachingEntities();
		}

        if(values.size()>0) {
            getListView().setVisibility(View.VISIBLE);
            BookingListAdapter adapter = new BookingListAdapter(getActivity(), values);
            setListAdapter(adapter);
        }
        else
            getListView().setVisibility(View.GONE);

		Utils.isInternetAvailable(getActivity());
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		DBBooking item = (DBBooking) getListAdapter().getItem(position);
		Intent intent = new Intent(getActivity(), TripDetailActivity.class);
		intent.putExtra(MBDefinition.DBBOOKING_EXTRA, item);
		startActivity(intent);
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
        TextView tv_history_404 = (TextView) rootView.findViewById(R.id.tv_history_404);
        Typeface rionaSansMedium = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RionaSansMedium.otf");
        tv_history_404.setTypeface(rionaSansMedium);
		return rootView;
	}

}