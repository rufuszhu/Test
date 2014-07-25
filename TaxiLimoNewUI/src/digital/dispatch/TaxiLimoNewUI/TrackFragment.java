package digital.dispatch.TaxiLimoNewUI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
	private final String TAG = "Track";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static TrackFragment newInstance() {
		TrackFragment fragment = new TrackFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, 2);
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
			inflater.inflate(R.menu.track, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_track, container, false);
		
		return rootView;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (id == R.id.action_refresh) {
            Toast.makeText(getActivity(), "You clicked refreash in tacking fragment", Toast.LENGTH_SHORT).show();
            return true;
        }
		
		return super.onOptionsItemSelected(item);
	}
}