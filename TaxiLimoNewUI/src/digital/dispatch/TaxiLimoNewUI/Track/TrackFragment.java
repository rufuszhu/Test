package digital.dispatch.TaxiLimoNewUI.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.digital.dispatch.TaxiLimoSoap.responses.JobItem;

import de.greenrobot.dao.query.CloseableListIterator;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.BookingListAdapter;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.RecallJobTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackFragment extends ListFragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
	private final String TAG = "TrackFragment";
	private View rootView;
	private MenuItem refresh_icon;
	private boolean isRefreshing;

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
		isRefreshing = false;
		
		BookingListAdapter adapter = new BookingListAdapter(getActivity(), getActiveJobs());
		setListAdapter(adapter);
	}
	
	private List<DBBooking> getActiveJobs(){
		DaoManager daoManager = DaoManager.getInstance(getActivity());
		DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_READ);
		return bookingDao.queryBuilder()
				.where(Properties.TripStatus.notEq(MBDefinition.MB_STATUS_CANCELLED), Properties.TripStatus.notEq(MBDefinition.MB_STATUS_COMPLETED))
				.orderDesc(Properties.TripCreationTime).list();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Logger.d(TAG, "onActivityCreated");
		setHasOptionsMenu(true);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Logger.d(TAG, "onCreateOptionsMenu");
		// only show refresh if drawer is not open.
		if (!((MainActivity) getActivity()).getDrawerFragment().isDrawerOpen()) {
			
			if (!isRefreshing) {
				inflater.inflate(R.menu.track, menu);
				refresh_icon = menu.findItem(R.id.action_refresh);
				startRecallJobTask();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Logger.d(TAG, "onOptionsItemSelected");
		int id = item.getItemId();
		if (id == R.id.action_refresh && !isRefreshing) {
			startRecallJobTask();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_track, container, false);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.d(TAG, "on RESUME");
		BookingListAdapter adapter = new BookingListAdapter(getActivity(), getActiveJobs());
		setListAdapter(adapter);
		Utils.isInternetAvailable(getActivity());
	}

	private void startUpdateAnimation(MenuItem item) {
		// Do animation start
		isRefreshing = true;
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView) inflater.inflate(R.layout.iv_refresh, null);
		Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_refresh);
		rotation.setRepeatCount(Animation.INFINITE);
		iv.startAnimation(rotation);
		item.setActionView(iv);
	}

	public void stopUpdateAnimation() {
		isRefreshing = false;
		// Get our refresh item from the menu
		if (refresh_icon != null && refresh_icon.getActionView() != null) {
			// Remove the animation.
			refresh_icon.getActionView().clearAnimation();
			refresh_icon.setActionView(null);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		DBBooking item = (DBBooking) getListAdapter().getItem(position);
		Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
		intent.putExtra(MBDefinition.DBBOOKING_EXTRA, item);
		startActivity(intent);
	}

	public void startRecallJobTask() {
		
		ArrayList<Pair<String, String>> pairList = getUniqueActiveJobDestIdList();
		if (!isRefreshing && refresh_icon != null && pairList.size()>0) {
			startUpdateAnimation(refresh_icon);
		}
		Logger.d(TAG, "size of pairlist: " + pairList.size());
		for (int i = 0; i < pairList.size(); i++) {
			String jobList = getRideIdByDestId(pairList.get(i).first);
			String destId = pairList.get(i).first;
			String SysId = pairList.get(i).second;
			Logger.d("DestID: " + destId + " sysID: " + SysId + " JobList: " + jobList);
			
			new RecallJobTask(getActivity(), jobList, MBDefinition.IS_FOR_LIST).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, destId, SysId);
		}
	}

	private ArrayList<Pair<String, String>> getUniqueActiveJobDestIdList() {
		List<DBBooking> values = getActiveJobs();
		if(values.size()==0){
			if((BookingListAdapter)getListView().getAdapter()!=null)
				((BookingListAdapter)getListView().getAdapter()).clear();
		}
		
		ArrayList<Pair<String, String>> activeDestIdSysIdPairList = new ArrayList<Pair<String, String>>();
		String oldId = "";
		for (int i = 0; i < values.size(); i++) {
			DBBooking book = values.get(i);
			if (!book.getDestID().equalsIgnoreCase(oldId)) {
				Pair<String, String> tempPair = new Pair<String, String>(book.getDestID(), book.getSysId());
				activeDestIdSysIdPairList.add(tempPair);
			}
			oldId = book.getDestID();
		}

		return activeDestIdSysIdPairList;
	}

	private String getRideIdByDestId(String destId) {
		DaoManager daoManager = DaoManager.getInstance(getActivity());
		DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_READ);
		CloseableListIterator<DBBooking> iterator = bookingDao
				.queryBuilder()
				.where(Properties.TripStatus.notEq(MBDefinition.MB_STATUS_CANCELLED), Properties.TripStatus.notEq(MBDefinition.MB_STATUS_COMPLETED),
						Properties.DestID.eq(destId)).listIterator();
		String rideIdList = "";
		while (iterator.hasNext()) {
			DBBooking book = iterator.next();
			rideIdList += book.getTaxi_ride_id() + ",";
		}
		try {
			iterator.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// get rid off the last ","
		if (rideIdList.endsWith(","))
			rideIdList = rideIdList.substring(0, rideIdList.length() - 1);
		return rideIdList;
	}

	public void updateStatus(List<DBBooking> bookingList) {
		stopUpdateAnimation();
		setListAdapter(null);
		List<DBBooking> activeJobs = getActiveJobs();
		if( activeJobs.size() > 0){	
			BookingListAdapter adapter = new BookingListAdapter(getActivity(), activeJobs);
			if(adapter.getCount()>0)
			Logger.e(TAG,"trip status: " + adapter.getItem(0).getTripStatus());
			setListAdapter(adapter);
		}
	}

}