package digital.dispatch.TaxiLimoNewUI;

import java.util.ArrayList;
import java.util.List;

import com.digital.dispatch.TaxiLimoSQLDatabase.MBBooking;

import digital.dispatch.TaxiLimoNewUI.Adapters.ListAdapter;
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

		List<MBBooking> values = new ArrayList<MBBooking>();

		for (int i = 0; i < 10; i++) {
			MBBooking mb = new MBBooking();
			mb.setAttribute("11920 forge way");
			if (i % 3 == 0)
				mb.setDispatchedCar("Complete");
			else
				mb.setDispatchedCar("Canceled");
			values.add(mb);
		}

		ListAdapter adapter = new ListAdapter(getActivity(), values);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		MBBooking item = (MBBooking) getListAdapter().getItem(position);
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