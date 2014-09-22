package digital.dispatch.TaxiLimoNewUI.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.digital.dispatch.TaxiLimoSoap.responses.JobItem;

import de.greenrobot.dao.query.CloseableListIterator;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.Adapters.BookingListAdapter;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.History.TripDetailActivity;
import digital.dispatch.TaxiLimoNewUI.Task.CancelJobTask;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadImageTask;
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
	private DBBookingDao bookingDao;
	private View rootView;
	private MenuItem refresh_icon;
	private BookingListAdapter adapter;

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
		Logger.d(TAG, "onCreateOptionsMenu");
		// only show refresh if drawer is not open.
		if (!((MainActivity) getActivity()).getDrawerFragment().isDrawerOpen()) {
			inflater.inflate(R.menu.track, menu);
			refresh_icon = menu.findItem(R.id.action_refresh);
			stopUpdateAnimation();
			startRecallJobTask();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
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
		Logger.e(TAG, "on RESUME");
		DaoManager daoManager = DaoManager.getInstance(getActivity());
		bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_READ);
		// List<DBBooking> values = bookingDao.queryBuilder().where(Properties.TripStatus.notEq(MBDefinition.MB_STATUS_CANCELLED),
		// Properties.TripStatus.notEq(MBDefinition.MB_STATUS_COMPLETED)).list();
		// adapter = new BookingListAdapter(getActivity(), values);
		// setListAdapter(adapter);

		if (refresh_icon != null)
			startRecallJobTask();
	}

	private void startUpdateAnimation(MenuItem item) {
		// Do animation start
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView) inflater.inflate(R.layout.iv_refresh, null);
		Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_refresh);
		rotation.setRepeatCount(Animation.INFINITE);
		iv.startAnimation(rotation);
		item.setActionView(iv);
	}

	public void stopUpdateAnimation() {
		// Get our refresh item from the menu
		if (refresh_icon.getActionView() != null) {
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
		Toast.makeText(getActivity(), position + " selected", Toast.LENGTH_LONG).show();
	}

	private void startRecallJobTask() {

		ArrayList<Pair<String, String>> pairList = getUniqueActiveJobDestIdList();
		Logger.d(TAG, "size of pairlist: " + pairList.size());
		for (int i = 0; i < pairList.size(); i++) {
			String jobList = getRideIdByDestId(pairList.get(i).first);
			String destId = pairList.get(i).first;
			String SysId = pairList.get(i).second;
			Logger.d("DestID: " + destId + " sysID: " + SysId + " JobList: " + jobList);
			startUpdateAnimation(refresh_icon);
			new RecallJobTask(getActivity(), jobList, MBDefinition.IS_FOR_LIST).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, destId, SysId);
		}

		if (pairList.size() == 0) {
			if (adapter != null)
				adapter.clear();
		}
	}

	private ArrayList<Pair<String, String>> getUniqueActiveJobDestIdList() {
		CloseableListIterator<DBBooking> iterator = bookingDao.queryBuilder()
				.where(Properties.TripStatus.notEq(MBDefinition.MB_STATUS_CANCELLED), Properties.TripStatus.notEq(MBDefinition.MB_STATUS_COMPLETED)).orderAsc(Properties.DestID).listIterator();
		ArrayList<Pair<String, String>> activeDestIdSysIdPairList = new ArrayList<Pair<String, String>>();
		String oldId = "";
		while (iterator.hasNext()) {
			DBBooking book = iterator.next();
			if (!book.getDestID().equalsIgnoreCase(oldId)) {
				Pair<String, String> tempPair = new Pair<String, String>(book.getDestID(), book.getSysId());
				activeDestIdSysIdPairList.add(tempPair);
			}
			oldId = book.getDestID();
		}
		try {
			iterator.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return activeDestIdSysIdPairList;
	}

	private String getRideIdByDestId(String destId) {
		CloseableListIterator<DBBooking> iterator = bookingDao.queryBuilder()
				.where(Properties.TripStatus.notEq(MBDefinition.MB_STATUS_CANCELLED), Properties.TripStatus.notEq(MBDefinition.MB_STATUS_COMPLETED), Properties.DestID.eq(destId)).listIterator();
		String rideIdList = "";
		while (iterator.hasNext()) {
			DBBooking book = iterator.next();
			rideIdList += book.getTaxi_ride_id() + ",";
		}
		// get rid off the last ","
		if (rideIdList.endsWith(","))
			rideIdList = rideIdList.substring(0, rideIdList.length() - 1);
		return rideIdList;
	}

	public void updateStatus(JobItem[] jobArr) {
		stopUpdateAnimation();
		List<DBBooking> values = new ArrayList<DBBooking>();
		for (int i = 0; i < jobArr.length; i++) {
			DBBooking dbBook = bookingDao.queryBuilder().where(Properties.Taxi_ride_id.eq(jobArr[i].taxi_ride_id)).list().get(0);
			JobItem job = jobArr[i];
			switch (Integer.parseInt(job.tripStatusUniformCode)) {
			case MBDefinition.TRIP_STATUS_BOOKED:
			case MBDefinition.TRIP_STATUS_DISPATCHING:
				values.add(dbBook);
				break;
			case MBDefinition.TRIP_STATUS_ACCEPTED:
				dbBook.setTripStatus(MBDefinition.MB_STATUS_ACCEPTED);
				bookingDao.update(dbBook);
				values.add(dbBook);
				break;
			case MBDefinition.TRIP_STATUS_ARRIVED:
				dbBook.setTripStatus(MBDefinition.MB_STATUS_ARRIVED);
				bookingDao.update(dbBook);
				values.add(dbBook);
				break;
			case MBDefinition.TRIP_STATUS_COMPLETE:
				switch (Integer.parseInt(job.detailTripStatusUniformCode)) {

				case MBDefinition.DETAIL_STATUS_IN_SERVICE:
					dbBook.setTripStatus(MBDefinition.MB_STATUS_IN_SERVICE);
					bookingDao.update(dbBook);
					values.add(dbBook);
					break;
				case MBDefinition.DETAIL_STATUS_COMPLETE:

					dbBook.setTripStatus(MBDefinition.MB_STATUS_COMPLETED);
					bookingDao.update(dbBook);

					break;
				case MBDefinition.DETAIL_STATUS_CANCEL:

					dbBook.setTripStatus(MBDefinition.MB_STATUS_CANCELLED);
					bookingDao.update(dbBook);
					break;
				// special complete: no show, force complete etc. set as "Cancelled" to user
				case MBDefinition.DETAIL_STATUS_NO_SHOW:
				case MBDefinition.DETAIL_STATUS_FORCE_COMPLETE:

					dbBook.setTripStatus(MBDefinition.MB_STATUS_CANCELLED);
					bookingDao.update(dbBook);
					break;
				// other unimportant intermediate status, just ignore
				case MBDefinition.DETAIL_OTHER_IGNORE:

				default:
					break;

				}
				break;
			default:
				break;
			}

		}
		if (adapter != null)
			adapter.clear();
		adapter = new BookingListAdapter(getActivity(), values);
		setListAdapter(adapter);
	}

}