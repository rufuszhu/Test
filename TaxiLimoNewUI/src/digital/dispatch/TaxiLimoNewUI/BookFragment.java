package digital.dispatch.TaxiLimoNewUI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class BookFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";


	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static BookFragment newInstance() {
		BookFragment fragment = new BookFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, 1);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//only show refresh if drawer is not open.
		if(!((MainActivity) getActivity()).getDrawerFragment().isDrawerOpen())
			inflater.inflate(R.menu.main, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_book, container, false);
		return rootView;
	}
	
}
