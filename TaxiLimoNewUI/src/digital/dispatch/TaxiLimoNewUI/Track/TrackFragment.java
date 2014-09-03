package digital.dispatch.TaxiLimoNewUI.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.digital.dispatch.TaxiLimoSQLDatabase.MBBooking;
import com.digital.dispatch.TaxiLimoSoap.responses.JobItem;

import de.greenrobot.dao.query.CloseableListIterator;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAttributeDao;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.BookingListAdapter;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.R.menu;
import digital.dispatch.TaxiLimoNewUI.Task.CancelJobTask;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadImageTask;
import digital.dispatch.TaxiLimoNewUI.Task.RecallJobTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackFragment extends Fragment{

	private static final String ARG_SECTION_NUMBER = "section_number";
	private final String TAG = "Track";
	private DaoManager daoManager;
	private DBBookingDao bookingDao;
	private View rootView;

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
		// only show refresh if drawer is not open.
		if (!((MainActivity) getActivity()).getDrawerFragment().isDrawerOpen())
			inflater.inflate(R.menu.track, menu);
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
		daoManager = DaoManager.getInstance(getActivity());
		bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
		startRecallJobTask();
	}

	@Override
	public void onPause() {
		super.onPause();
		Logger.e(TAG, "on PAUSE");

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

	public void parseRecallJobResponse(JobItem[] jobArr) {
		Logger.i(TAG, "parseRecallJobResponse");
		JobItem job = jobArr[0];
		final DBBooking dbBook = bookingDao.queryBuilder().where(Properties.Taxi_ride_id.eq(job.taxi_ride_id)).list().get(0);
		
		View.OnClickListener cancelListener = new View.OnClickListener() {
			public void onClick(View v) {
				// change the status to cancelled in our database if no matter what
				Logger.e(TAG,"cancel btn clicked");
				dbBook.setTripStatus(MBDefinition.MB_STATUS_CANCELLED);
				dbBook.setTripCancelledTime(System.currentTimeMillis() + "");
				
				bookingDao.update(dbBook);
				new CancelJobTask(getActivity(), dbBook).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		};
		View.OnClickListener payListener = new View.OnClickListener() {
			public void onClick(View v) {
				
			}
		};
		View.OnClickListener tracklListener = new View.OnClickListener() {
			public void onClick(View v) {
				
			}
		};
		
		LinearLayout received_cancel_button = (LinearLayout) rootView.findViewById(R.id.received_cancel_button);
		LinearLayout dispatched_buttons = (LinearLayout) rootView.findViewById(R.id.dispatched_buttons);
		LinearLayout arrived_buttons = (LinearLayout) rootView.findViewById(R.id.arrived_buttons);
		
		LinearLayout dispatched_cancel_btn = (LinearLayout) rootView.findViewById(R.id.dispatched_cancel_btn);
		LinearLayout dispatched_track_btn = (LinearLayout) rootView.findViewById(R.id.dispatched_track_btn);
		LinearLayout arrived_track_btn = (LinearLayout) rootView.findViewById(R.id.arrived_track_btn);
		LinearLayout arrived_cancel_btn = (LinearLayout) rootView.findViewById(R.id.arrived_cancel_btn);
		LinearLayout arrived_pay_btn = (LinearLayout) rootView.findViewById(R.id.arrived_pay_btn);
		
		
		TableRow vehicle_row = (TableRow) rootView.findViewById(R.id.vehicle_row);
		TableRow driver_row = (TableRow) rootView.findViewById(R.id.driver_row);
		
		TextView tv_id = (TextView) rootView.findViewById(R.id.tv_id);
		TextView tv_receive = (TextView) rootView.findViewById(R.id.tv_receive);
		TextView tv_from = (TextView) rootView.findViewById(R.id.tv_pickup_address);
		TextView tv_to = (TextView) rootView.findViewById(R.id.tv_dropoff_address);
		TextView tv_vehicle = (TextView) rootView.findViewById(R.id.tv_vehicle);
		TextView tv_company_name = (TextView) rootView.findViewById(R.id.tv_company_name);
		TextView tv_company_description = (TextView) rootView.findViewById(R.id.tv_company_description);
		TextView tv_driver = (TextView) rootView.findViewById(R.id.tv_driver);
		
		Button call_btn = (Button) rootView.findViewById(R.id.call_btn);
		call_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dbBook.getCompany_phone_number()));
				startActivity(intent);
			}
			
		});
		
		ImageView iv_company_icon = (ImageView) rootView.findViewById(R.id.iv_company_icon);
		
		tv_id.setText(dbBook.getTaxi_ride_id()+"");
		tv_receive.setText(dbBook.getTripCreationTime());
		tv_from.setText(dbBook.getPickupAddress());
		
		if(dbBook.getDropoffAddress()!= null && dbBook.getDropoffAddress().length()>0)
			tv_to.setText(dbBook.getDropoffAddress());
		else
			tv_to.setText("Not Given");
		
		String prefixURL = getActivity().getString(R.string.url);
		prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
		new DownloadImageTask(iv_company_icon).execute(prefixURL + dbBook.getCompany_icon());

		tv_company_name.setText(dbBook.getCompany_name());
		tv_company_description.setText(dbBook.getCompany_description());

		LinearLayout ll_attr = (LinearLayout) rootView.findViewById(R.id.ll_attr);
		if(dbBook.getAttributeList()!=null)
			Utils.showOption(ll_attr,dbBook.getAttributeList().split(","), getActivity(), 0);

		switch (Integer.parseInt(job.tripStatusUniformCode)) {
			case MBDefinition.TRIP_STATUS_BOOKED:	
			case MBDefinition.TRIP_STATUS_DISPATCHING:
				received_cancel_button.setVisibility(View.VISIBLE);
				dispatched_buttons.setVisibility(View.GONE);
				arrived_buttons.setVisibility(View.GONE);
				
				received_cancel_button.setOnClickListener(cancelListener);
				
				vehicle_row.setVisibility(View.GONE);
				driver_row.setVisibility(View.GONE);
				break;
			case  MBDefinition.TRIP_STATUS_ACCEPTED:
				dbBook.setTripStatus(MBDefinition.MB_STATUS_ACCEPTED);
				received_cancel_button.setVisibility(View.GONE);
				dispatched_buttons.setVisibility(View.VISIBLE);
				arrived_buttons.setVisibility(View.GONE);	
				
				dispatched_cancel_btn.setOnClickListener(cancelListener);
				dispatched_track_btn.setOnClickListener(tracklListener);
				
				dbBook.setDispatchedCar(job.dispatchedCar);
				dbBook.setDispatchedDriver(job.dispatchedDriver);
				dbBook.setTripStatus(MBDefinition.MB_STATUS_ACCEPTED);
				bookingDao.update(dbBook);
				
				vehicle_row.setVisibility(View.VISIBLE);
				driver_row.setVisibility(View.VISIBLE);
				tv_vehicle.setText(dbBook.getDispatchedCar());
				tv_driver.setText(dbBook.getDispatchedDriver());
				Logger.e(dbBook.getDispatchedCar());
				break;
			case MBDefinition.TRIP_STATUS_ARRIVED:
				dbBook.setTripStatus(MBDefinition.MB_STATUS_ARRIVED);
				received_cancel_button.setVisibility(View.GONE);
				dispatched_buttons.setVisibility(View.GONE);
				arrived_buttons.setVisibility(View.VISIBLE);
				
				arrived_cancel_btn.setOnClickListener(cancelListener);
				arrived_track_btn.setOnClickListener(tracklListener);
				arrived_pay_btn.setOnClickListener(payListener);
				
				vehicle_row.setVisibility(View.VISIBLE);
				driver_row.setVisibility(View.VISIBLE);
				tv_vehicle.setText(dbBook.getDispatchedCar());
				tv_driver.setText(dbBook.getDispatchedDriver());
				break;
			case MBDefinition.TRIP_STATUS_COMPLETE:
				dbBook.setTripStatus(MBDefinition.MB_STATUS_COMPLETED);
				break;
			 default:
				 break;
		}
	}
	
	private void startRecallJobTask() {
		ArrayList<Pair<String, String>> pairList = getUniqueActiveJobDestIdList();
		for (int i = 0; i < pairList.size(); i++) {
			String jobList = getRideIdByDestId(pairList.get(i).first);
			String destId = pairList.get(i).first;
			String SysId = pairList.get(i).second;
			Logger.e("DestID: " + destId + " sysID: " + SysId + " JobList: " + jobList);
			new RecallJobTask(getActivity(), jobList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, destId, SysId);
		}
		if(pairList.size()==0){
			LinearLayout trip_detail_table = (LinearLayout) rootView.findViewById(R.id.trip_detail_table);
			trip_detail_table.setVisibility(View.GONE);
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
}