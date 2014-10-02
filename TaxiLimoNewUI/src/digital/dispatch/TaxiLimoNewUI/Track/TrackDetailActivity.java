package digital.dispatch.TaxiLimoNewUI.Track;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.digital.dispatch.TaxiLimoSoap.responses.JobItem;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBCreditCardDao;
import digital.dispatch.TaxiLimoNewUI.EditCreditCardActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.RegisterCreditCardActivity;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities.gcmType;
import digital.dispatch.TaxiLimoNewUI.Task.CancelJobTask;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadImageTask;
import digital.dispatch.TaxiLimoNewUI.Task.RecallJobTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class TrackDetailActivity extends Activity {

	private static final String TAG = "TrackDetailActivity";
	private DaoManager daoManager;
	private DBBookingDao bookingDao;
	private Context _context;
	private DBBooking dbBook;
	private MenuItem refresh_icon;

	private View.OnClickListener cancelListener;
	private View.OnClickListener payListener;
	private View.OnClickListener tracklListener;
	private LinearLayout received_cancel_button;
	private LinearLayout dispatched_buttons;
	private LinearLayout arrived_buttons;
	private LinearLayout inService_buttons;
	private LinearLayout dispatched_cancel_btn;
	private LinearLayout dispatched_track_btn;
	private LinearLayout arrived_track_btn;
	private LinearLayout arrived_cancel_btn;
	private LinearLayout arrived_pay_btn;
	private LinearLayout inService_pay_btn;
	private LinearLayout inService_track_btn;
	private LinearLayout completed_pay_btn;
	private TableRow vehicle_row;
	private TableRow driver_row;
	private TextView tv_id;
	private TextView tv_receive;
	private TextView tv_from;
	private TextView tv_to;
	private TextView tv_vehicle;
	private TextView tv_company_name;
	private TextView tv_company_description;
	private TextView tv_driver;
	private Button call_btn;
	private ImageView iv_company_icon;
	private LinearLayout ll_attr;

	private TextView tv_arrived_circle;
	private TextView tv_completed_circle;
	private TextView tv_inservice_circle;
	private TextView tv_dispatched_circle;

	private ImageView arrived_circle;
	private ImageView completed_circle;
	private ImageView inservice_circle;
	private ImageView dispatched_circle;
	
	private BroadcastReceiver bcReceiver;
	
	private boolean isRefreshing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.e(TAG, "onCreate");
		setContentView(R.layout.activity_track_detail);
		_context = this;
		dbBook = (DBBooking) getIntent().getSerializableExtra(MBDefinition.DBBOOKING_EXTRA);
		
		findView();
		fillTable();
		initListener();
		isRefreshing = false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
		daoManager = DaoManager.getInstance(_context);
		bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
		boolean isTrackDetail=true;
		bcReceiver = CommonUtilities.getGenericReceiver(_context,isTrackDetail);
		LocalBroadcastManager.getInstance(this).registerReceiver(bcReceiver, new IntentFilter(gcmType.message.toString()));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logger.e("onActivityResult");
		Logger.e("requestCode: " + requestCode);
		if (requestCode == MBDefinition.REQUEST_REGISTER_CC) {
			if (resultCode == RESULT_OK) {
				if (data.getExtras().getParcelable(MBDefinition.EXTRA_BOOKING) != null) {
					dbBook = (DBBooking) data.getExtras().getSerializable(MBDefinition.EXTRA_BOOKING);
					arrived_pay_btn.callOnClick();
				}
			}
		}
	}


	@Override
	public void onPause() {
		super.onPause();
		Logger.e(TAG, "on PAUSE");
	}
	
	
	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(bcReceiver);
		super.onDestroy();
	}
	
	

	private void findView() {
		received_cancel_button = (LinearLayout) findViewById(R.id.received_cancel_button);
		dispatched_buttons = (LinearLayout) findViewById(R.id.dispatched_buttons);
		arrived_buttons = (LinearLayout) findViewById(R.id.arrived_buttons);
		inService_buttons = (LinearLayout) findViewById(R.id.inService_buttons);
		completed_pay_btn = (LinearLayout) findViewById(R.id.completed_pay_button);
		
		dispatched_cancel_btn = (LinearLayout) findViewById(R.id.dispatched_cancel_btn);
		dispatched_track_btn = (LinearLayout) findViewById(R.id.dispatched_track_btn);
		arrived_track_btn = (LinearLayout) findViewById(R.id.arrived_track_btn);
		arrived_cancel_btn = (LinearLayout) findViewById(R.id.arrived_cancel_btn);
		arrived_pay_btn = (LinearLayout) findViewById(R.id.arrived_pay_btn);
		inService_pay_btn = (LinearLayout) findViewById(R.id.inService_pay_btn);
		inService_track_btn = (LinearLayout) findViewById(R.id.inService_track_btn);
		

		vehicle_row = (TableRow) findViewById(R.id.vehicle_row);
		driver_row = (TableRow) findViewById(R.id.driver_row);

		tv_id = (TextView) findViewById(R.id.tv_id);
		tv_receive = (TextView) findViewById(R.id.tv_receive);
		tv_from = (TextView) findViewById(R.id.tv_pickup_address);
		tv_to = (TextView) findViewById(R.id.tv_dropoff_address);
		tv_vehicle = (TextView) findViewById(R.id.tv_vehicle);
		tv_company_name = (TextView) findViewById(R.id.tv_company_name);
		tv_company_description = (TextView) findViewById(R.id.tv_company_description);
		tv_driver = (TextView) findViewById(R.id.tv_driver);

		call_btn = (Button) findViewById(R.id.call_btn);
		iv_company_icon = (ImageView) findViewById(R.id.iv_tracking_company_icon);
		ll_attr = (LinearLayout) findViewById(R.id.ll_attr);

		tv_arrived_circle = (TextView) findViewById(R.id.tv_arrived_circle);
		tv_completed_circle = (TextView) findViewById(R.id.tv_completed_circle);
		tv_inservice_circle = (TextView) findViewById(R.id.tv_inservice_circle);
		tv_dispatched_circle = (TextView) findViewById(R.id.tv_dispatched_circle);

		arrived_circle = (ImageView) findViewById(R.id.arrived_circle);
		completed_circle = (ImageView) findViewById(R.id.completed_circle);
		inservice_circle = (ImageView) findViewById(R.id.inservice_circle);
		dispatched_circle = (ImageView) findViewById(R.id.dispatched_circle);
	}

	private void fillTable() {
		tv_id.setText(dbBook.getTaxi_ride_id() + "");
		tv_receive.setText(dbBook.getTripCreationTime());
		tv_from.setText(dbBook.getPickupAddress());

		if (dbBook.getDropoffAddress() != null && dbBook.getDropoffAddress().length() > 0)
			tv_to.setText(dbBook.getDropoffAddress());
		else
			tv_to.setText("Not Given");

		String prefixURL = _context.getString(R.string.url);
		prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
		new DownloadImageTask(iv_company_icon).execute(prefixURL + dbBook.getCompany_icon());

		tv_company_name.setText(dbBook.getCompany_name());
		tv_company_description.setText(dbBook.getCompany_description());

		if (dbBook.getAttributeList() != null){
			int margin_right = 10;
			Utils.showOption(ll_attr, dbBook.getAttributeList().split(","), _context, margin_right);
		}

	}

	private void initListener() {

		cancelListener = new View.OnClickListener() {
			public void onClick(View v) {
				// change the status to cancelled in our database if no matter what
				AlertDialog.Builder builder = new AlertDialog.Builder(_context);
				builder.setTitle("Confirm Cancel");
				builder.setMessage("Are you sure you want to cancel?");
				builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbBook.setTripStatus(MBDefinition.MB_STATUS_CANCELLED);
						dbBook.setTripCancelledTime(System.currentTimeMillis() + "");
						bookingDao.update(dbBook);
						new CancelJobTask(_context, dbBook).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.show();
			}
		};

		payListener = new View.OnClickListener() {
			public void onClick(View v) {
				DBCreditCardDao creditCardDao = daoManager.getDBCreditCardDao(DaoManager.TYPE_READ);
				if(creditCardDao.queryBuilder().list().size()==0){
					AlertDialog.Builder builder = new AlertDialog.Builder(_context);
					builder.setMessage(R.string.ask_register_cc).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(_context, EditCreditCardActivity.class);
							intent.putExtra(MBDefinition.EXTRA_BOOKING, dbBook);
							startActivityForResult(intent,MBDefinition.REQUEST_REGISTER_CC);
						}
					}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss(); 
						}
					});
					builder.show();
				}
				else{
					Intent intent = new Intent(_context, PayActivity.class);
					intent.putExtra(MBDefinition.DBBOOKING_EXTRA, dbBook);
					startActivity(intent);
					overridePendingTransition(R.anim.base_back_activity_enter, R.anim.base_back_activity_exit);
					finish();
				}
			}
		};

		tracklListener = new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(_context, TrackingMapActivity.class);
				intent.putExtra(MBDefinition.DBBOOKING_EXTRA, dbBook);
				startActivity(intent);
			}
		};

		call_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dbBook.getCompany_phone_number()));
				startActivity(intent);
			}
		});

		dispatched_cancel_btn.setOnClickListener(cancelListener);
		dispatched_track_btn.setOnClickListener(tracklListener);
		received_cancel_button.setOnClickListener(cancelListener);
		arrived_cancel_btn.setOnClickListener(cancelListener);
		arrived_track_btn.setOnClickListener(tracklListener);
		arrived_pay_btn.setOnClickListener(payListener);
		inService_pay_btn.setOnClickListener(payListener);
		inService_track_btn.setOnClickListener(tracklListener);
		completed_pay_btn.setOnClickListener(payListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Logger.e(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.track_detail, menu);
		refresh_icon = menu.findItem(R.id.action_refresh);
		startRecallJobTask();
		
		return true;
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


	
	

	public void startUpdateAnimation(MenuItem item) {
		// Do animation start
		isRefreshing=true;
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView) inflater.inflate(R.layout.iv_refresh, null);
		Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
		rotation.setRepeatCount(Animation.INFINITE);
		iv.startAnimation(rotation);
		item.setActionView(iv);
	}

	public void stopUpdateAnimation() {
		isRefreshing=false;
		// Get our refresh item from the menu
		if (refresh_icon.getActionView() != null) {
			// Remove the animation.
			refresh_icon.getActionView().clearAnimation();
			refresh_icon.setActionView(null);
		}
	}

	public void parseRecallJobResponse(JobItem[] jobArr) {
		Logger.i(TAG, "parseRecallJobResponse");
		stopUpdateAnimation();
		JobItem job = jobArr[0];

		// final DBBooking dbBook = bookingDao.queryBuilder().where(Properties.Taxi_ride_id.eq(job.taxi_ride_id)).list().get(0);

		switch (Integer.parseInt(job.tripStatusUniformCode)) {
		case MBDefinition.TRIP_STATUS_BOOKED:
		case MBDefinition.TRIP_STATUS_DISPATCHING:
			setupBookedUI();
			break;
		case MBDefinition.TRIP_STATUS_ACCEPTED:
			dbBook.setDispatchedCar(job.dispatchedCar);
			dbBook.setDispatchedDriver(job.dispatchedDriver);
			dbBook.setTripStatus(MBDefinition.MB_STATUS_ACCEPTED);
			setUpAcceptedUI();
			break;
		case MBDefinition.TRIP_STATUS_ARRIVED:
			dbBook.setTripStatus(MBDefinition.MB_STATUS_ARRIVED);
			setUpArrivedUI();
			break;
		case MBDefinition.TRIP_STATUS_COMPLETE:
			switch (Integer.parseInt(job.detailTripStatusUniformCode)) {
			case MBDefinition.DETAIL_STATUS_IN_SERVICE:
				setUpInServiceUI();
				dbBook.setTripStatus(MBDefinition.MB_STATUS_IN_SERVICE);
				break;
			case MBDefinition.DETAIL_STATUS_COMPLETE:
				setUpInCompletedUI();
				dbBook.setTripStatus(MBDefinition.MB_STATUS_COMPLETED);
				break;
			case MBDefinition.DETAIL_STATUS_CANCEL:
				setUpCanceledUI();
				dbBook.setTripStatus(MBDefinition.MB_STATUS_CANCELLED);
				break;
			// special complete: no show, force complete etc. set as "Cancelled" to user
			case MBDefinition.DETAIL_STATUS_NO_SHOW:
			case MBDefinition.DETAIL_STATUS_FORCE_COMPLETE:
				dbBook.setTripStatus(MBDefinition.MB_STATUS_CANCELLED);
				setUpCanceledUI();
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
		bookingDao.update(dbBook);
	}

	private void setUpCanceledUI() {
		received_cancel_button.setVisibility(View.GONE);
		dispatched_buttons.setVisibility(View.GONE);
		arrived_buttons.setVisibility(View.GONE);
		inService_buttons.setVisibility(View.GONE);
		completed_pay_btn.setVisibility(View.GONE);

		vehicle_row.setVisibility(View.GONE);
		driver_row.setVisibility(View.GONE);

		tv_dispatched_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_arrived_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_inservice_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_completed_circle.setTextColor(getResources().getColor(R.color.gray_line));

		tv_dispatched_circle.setTypeface(null, Typeface.NORMAL);
		tv_arrived_circle.setTypeface(null, Typeface.NORMAL);
		tv_inservice_circle.setTypeface(null, Typeface.NORMAL);
		tv_completed_circle.setTypeface(null, Typeface.NORMAL);

		dispatched_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
		arrived_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
		inservice_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
		completed_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
	}

	private void setupBookedUI() {
		received_cancel_button.setVisibility(View.VISIBLE);
		dispatched_buttons.setVisibility(View.GONE);
		arrived_buttons.setVisibility(View.GONE);
		inService_buttons.setVisibility(View.GONE);
		completed_pay_btn.setVisibility(View.GONE);

		vehicle_row.setVisibility(View.GONE);
		driver_row.setVisibility(View.GONE);

		tv_dispatched_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_arrived_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_inservice_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_completed_circle.setTextColor(getResources().getColor(R.color.gray_line));

		tv_dispatched_circle.setTypeface(null, Typeface.NORMAL);
		tv_arrived_circle.setTypeface(null, Typeface.NORMAL);
		tv_inservice_circle.setTypeface(null, Typeface.NORMAL);
		tv_completed_circle.setTypeface(null, Typeface.NORMAL);

		dispatched_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
		arrived_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
		inservice_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
		completed_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
	}

	private void setUpAcceptedUI() {
		received_cancel_button.setVisibility(View.GONE);
		dispatched_buttons.setVisibility(View.VISIBLE);
		arrived_buttons.setVisibility(View.GONE);
		inService_buttons.setVisibility(View.GONE);
		completed_pay_btn.setVisibility(View.GONE);

		vehicle_row.setVisibility(View.VISIBLE);
		driver_row.setVisibility(View.VISIBLE);

		tv_vehicle.setText(dbBook.getDispatchedCar());
		tv_driver.setText(dbBook.getDispatchedDriver());

		tv_dispatched_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_arrived_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_inservice_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_completed_circle.setTextColor(getResources().getColor(R.color.gray_line));

		tv_dispatched_circle.setTypeface(null, Typeface.BOLD);
		tv_arrived_circle.setTypeface(null, Typeface.NORMAL);
		tv_inservice_circle.setTypeface(null, Typeface.NORMAL);
		tv_completed_circle.setTypeface(null, Typeface.NORMAL);

		dispatched_circle.setImageResource(R.drawable.shape_solid_circle);
		arrived_circle.setImageResource(R.drawable.shape_holo_circle);
		inservice_circle.setImageResource(R.drawable.shape_holo_circle);
		completed_circle.setImageResource(R.drawable.shape_holo_circle);

	}

	private void setUpArrivedUI() {
		received_cancel_button.setVisibility(View.GONE);
		dispatched_buttons.setVisibility(View.GONE);
		arrived_buttons.setVisibility(View.VISIBLE);
		inService_buttons.setVisibility(View.GONE);
		completed_pay_btn.setVisibility(View.GONE);

		vehicle_row.setVisibility(View.VISIBLE);
		driver_row.setVisibility(View.VISIBLE);
		
		tv_vehicle.setText(dbBook.getDispatchedCar());
		tv_driver.setText(dbBook.getDispatchedDriver());

		tv_dispatched_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_arrived_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_inservice_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_completed_circle.setTextColor(getResources().getColor(R.color.gray_line));

		tv_dispatched_circle.setTypeface(null, Typeface.NORMAL);
		tv_arrived_circle.setTypeface(null, Typeface.BOLD);
		tv_inservice_circle.setTypeface(null, Typeface.NORMAL);
		tv_completed_circle.setTypeface(null, Typeface.NORMAL);

		dispatched_circle.setImageResource(R.drawable.shape_solid_circle);
		arrived_circle.setImageResource(R.drawable.shape_solid_circle);
		inservice_circle.setImageResource(R.drawable.shape_holo_circle);
		completed_circle.setImageResource(R.drawable.shape_holo_circle);
	}

	private void setUpInServiceUI() {
		received_cancel_button.setVisibility(View.GONE);
		dispatched_buttons.setVisibility(View.GONE);
		arrived_buttons.setVisibility(View.GONE);
		inService_buttons.setVisibility(View.VISIBLE);
		completed_pay_btn.setVisibility(View.GONE);
		
		vehicle_row.setVisibility(View.VISIBLE);
		driver_row.setVisibility(View.VISIBLE);
		
		tv_vehicle.setText(dbBook.getDispatchedCar());
		tv_driver.setText(dbBook.getDispatchedDriver());

		arrived_cancel_btn.setOnClickListener(cancelListener);
		arrived_track_btn.setOnClickListener(tracklListener);
		arrived_pay_btn.setOnClickListener(payListener);

		vehicle_row.setVisibility(View.VISIBLE);
		driver_row.setVisibility(View.VISIBLE);
		tv_vehicle.setText(dbBook.getDispatchedCar());
		tv_driver.setText(dbBook.getDispatchedDriver());
		
		tv_dispatched_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_arrived_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_inservice_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_completed_circle.setTextColor(getResources().getColor(R.color.gray_line));

		tv_dispatched_circle.setTypeface(null, Typeface.NORMAL);
		tv_arrived_circle.setTypeface(null, Typeface.NORMAL);
		tv_inservice_circle.setTypeface(null, Typeface.BOLD);
		tv_completed_circle.setTypeface(null, Typeface.NORMAL);

		dispatched_circle.setImageResource(R.drawable.shape_solid_circle);
		arrived_circle.setImageResource(R.drawable.shape_solid_circle);
		inservice_circle.setImageResource(R.drawable.shape_solid_circle);
		completed_circle.setImageResource(R.drawable.shape_holo_circle);
	}
	
	private void setUpInCompletedUI() {
		received_cancel_button.setVisibility(View.GONE);
		dispatched_buttons.setVisibility(View.GONE);
		arrived_buttons.setVisibility(View.GONE);
		inService_buttons.setVisibility(View.GONE);
		completed_pay_btn.setVisibility(View.VISIBLE);
		
		vehicle_row.setVisibility(View.VISIBLE);
		driver_row.setVisibility(View.VISIBLE);
		
		tv_vehicle.setText(dbBook.getDispatchedCar());
		tv_driver.setText(dbBook.getDispatchedDriver());
		
		tv_dispatched_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_arrived_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_inservice_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_completed_circle.setTextColor(getResources().getColor(R.color.gray_circle));

		tv_dispatched_circle.setTypeface(null, Typeface.NORMAL);
		tv_arrived_circle.setTypeface(null, Typeface.NORMAL);
		tv_inservice_circle.setTypeface(null, Typeface.NORMAL);
		tv_completed_circle.setTypeface(null, Typeface.BOLD);

		dispatched_circle.setImageResource(R.drawable.shape_solid_circle);
		arrived_circle.setImageResource(R.drawable.shape_solid_circle);
		inservice_circle.setImageResource(R.drawable.shape_solid_circle);
		completed_circle.setImageResource(R.drawable.shape_solid_circle);
		
	}

	public void startRecallJobTask() {
		if(refresh_icon!=null && !isRefreshing)
			startUpdateAnimation(refresh_icon);
		new RecallJobTask(_context, dbBook.getTaxi_ride_id().toString(), MBDefinition.IS_FOR_ONE_JOB).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dbBook.getDestID(), dbBook.getSysId());
	}

	public void showCancelDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(_context);
		builder.setMessage("Cancel Successful!").setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				finish();
			}
		});
		builder.show();
	}

}
