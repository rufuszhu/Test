package digital.dispatch.TaxiLimoNewUI.Track;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBCreditCardDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Drawers.EditCreditCardActivity;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities.gcmType;
import digital.dispatch.TaxiLimoNewUI.Task.CancelJobTask;
import digital.dispatch.TaxiLimoNewUI.Task.RecallJobTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class TrackDetailActivity extends BaseActivity {

	private static final String TAG = "TrackDetailActivity";
	private static final int RED_TOTAL_DIFF = 194 - 107;
	private static final int GREEN_TOTAL_DIFF = 194 - 120;
	private static final int BLUE_TOTAL_DIFF = 194 - 131;

	private static final int RED = 197;
	private static final int GREEN = 194;
	private static final int BLUE = 194;

	private static final int RED_SELECTED = 107;
	private static final int GREEN_SELECTED = 120;
	private static final int BLUE_SELECTED = 131;

	public static FragmentManager fragmentManager;
	private static TrackingMapFragment trackingMapFragment;
	private static MapNotAvailableFragment map404Fragment;
	private RelativeLayout tab0, tab1;
	private PagerAdapter mAdapter;
	public ViewPager mPager;
	private OnPageChangeListener pageChangeListener;
	private boolean isScolling = false;
	private InfoFragment infoFragment;

	private DaoManager daoManager;
	private DBBookingDao bookingDao;
	private Context _context;
	private DBBooking dbBook;
	private MenuItem refresh_icon;

	private TextView tv_arrived_circle;
	private TextView tv_completed_circle;
	private TextView tv_inservice_circle;
	private TextView tv_dispatched_circle;
	private TextView tv_book_time;
	private TextView tv_id;
	private TextView arrow_right;

	private ImageView arrived_circle;
	private ImageView completed_circle;
	private ImageView inservice_circle;
	private ImageView dispatched_circle;

	private BroadcastReceiver bcReceiver;

	//private LinearLayout ll_btn_group, ll_cancel_btn_small, ll_pay_btn;
	private LinearLayout ll_cancel_btn, ll_pay_btn;
	private ImageView zoom_btn;
	private Typeface icon_pack, rionaSansMedium, rionaSansBold, exo2SemiBold, fontawesome, exo2Bold;

	private View cancel_cover;

	private boolean isRefreshing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d(TAG, "onCreate");
		setContentView(R.layout.activity_track_detail);
		_context = this;
		dbBook = (DBBooking) getIntent().getSerializableExtra(MBDefinition.DBBOOKING_EXTRA);
		icon_pack = Typeface.createFromAsset(getAssets(), "fonts/icon_pack.ttf");
		rionaSansMedium = Typeface.createFromAsset(getAssets(), "fonts/RionaSansMedium.otf");
		rionaSansBold = Typeface.createFromAsset(getAssets(), "fonts/RionaSansBold.otf");
		exo2SemiBold = Typeface.createFromAsset(getAssets(), "fonts/Exo2-SemiBold.ttf");
		exo2Bold = Typeface.createFromAsset(getAssets(), "fonts/Exo2-Bold.ttf");
		fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        findView();
        initListener();
		setUpTab();
		setTab0Text();
		isRefreshing = false;
	}

	public DBBooking getDBBook() {
		return dbBook;
	}

	private void setTab0Text() {

		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
			Date date = format.parse(dbBook.getTripCreationTime());
			SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a MMM dd, yyyy", Locale.US);
			String dateString = dateFormat.format(date);
			tv_book_time.setText("Received at " + dateString);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		tv_book_time.setTextColor(getResources().getColor(R.color.background_tab));
		arrow_right.setVisibility(View.GONE);

		tv_id.setText("Trip ID " + dbBook.getTaxi_ride_id());
	}

	private void setTab1Text() {
		tv_id.setText(dbBook.getPickupAddress());
		if (dbBook.getDropoffAddress() == null || dbBook.getDropoffAddress().length() == 0)
			tv_book_time.setText("Destination Not Given");
		else
			tv_book_time.setText(dbBook.getDropoffAddress());
		tv_book_time.setTextColor(getResources().getColor(R.color.background_tab_selected));
		arrow_right.setVisibility(View.VISIBLE);
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.d(TAG, "on RESUME");
		daoManager = DaoManager.getInstance(_context);
		bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
		boolean isTrackDetail = true;
		bcReceiver = CommonUtilities.getGenericReceiver(_context, isTrackDetail);
		LocalBroadcastManager.getInstance(this).registerReceiver(bcReceiver, new IntentFilter(gcmType.message.toString()));
	}

	@Override
	// this is user from user click pay but no registered credit card. And got directed to the cc register page and finished registraion
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logger.d("onActivityResult");
		Logger.v("requestCode: " + requestCode);
		if (requestCode == MBDefinition.REQUEST_REGISTER_CC) {
			if (resultCode == RESULT_OK) {
				if (data.getExtras().getParcelable(MBDefinition.EXTRA_BOOKING) != null) {

					dbBook = (DBBooking) data.getExtras().getSerializable(MBDefinition.EXTRA_BOOKING);
					// arrived_pay_btn.callOnClick();
				}
			}
		}
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(bcReceiver);
		super.onPause();
		Logger.d(TAG, "on PAUSE");
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	private void setTabListener() {
		tab0 = (RelativeLayout) findViewById(R.id.tab1);
		tab1 = (RelativeLayout) findViewById(R.id.tab2);

		tab0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isScolling = true;
				resetAllAlpha();
				mPager.setCurrentItem(0);
				resetAllTabColor();
				tab0.setBackgroundColor(getResources().getColor(R.color.background_tab_selected));

			}
		});
		tab1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isScolling = true;
				resetAllAlpha();
				mPager.setCurrentItem(1);
				resetAllTabColor();
				tab1.setBackgroundColor(getResources().getColor(R.color.background_tab_selected));
			}
		});
	}

	private void setUpTab() {
		mPager = (ViewPager) findViewById(R.id.pager);
		setTabListener();
		TextView icon_info = (TextView) findViewById(R.id.icon_info);
		TextView tv_info = (TextView) findViewById(R.id.tv_info);
		//TextView icon_track = (TextView) findViewById(R.id.icon_track);
		TextView tv_track = (TextView) findViewById(R.id.tv_track);

		icon_info.setTypeface(icon_pack);
		//icon_track.setTypeface(icon_pack);

		icon_info.setText(MBDefinition.ICON_INFO);
		//icon_track.setText(MBDefinition.icon_tab_track);

		tv_info.setTypeface(rionaSansMedium, Typeface.NORMAL);
		tv_track.setTypeface(rionaSansMedium, Typeface.NORMAL);

		trackingMapFragment = TrackingMapFragment.newInstance();
		infoFragment = new InfoFragment();
		map404Fragment = new MapNotAvailableFragment();
		mAdapter = new PagerAdapter(getSupportFragmentManager());

		fragmentManager = getSupportFragmentManager();
		pageChangeListener = new OnPageChangeListener() {

			@Override
			public void onPageSelected(int selected) {
				if (selected == 0) {
					setTab0Text();
					zoom_btn.setVisibility(View.GONE);
				}
				if (selected == 1) {
					setTab1Text();
					if (shouldShowCar()) {
						zoom_btn.setVisibility(View.VISIBLE);
					}
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// do not animate tab color transition for clicking on tab
				if (!isScolling) {
					if (position == 0) {
						if (trackingMapFragment != null && trackingMapFragment.mGetView() != null)
							trackingMapFragment.mGetView().setAlpha(positionOffset);

						infoFragment.mGetView().setAlpha(1 - positionOffset);

						if (mPager.getCurrentItem() == 0) {
							animateSelect(tab1, positionOffset);
							animateUnSelect(tab0, positionOffset);
						} else {
							animateSelect(tab0, 1f - positionOffset);
							animateUnSelect(getTab(mPager.getCurrentItem()), 1f - positionOffset);
						}
					}
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_DRAGGING) {

				}
				if (state == ViewPager.SCROLL_STATE_IDLE) {
					isScolling = false;

				}
			}
		};
		// set second page back to default Alpha
		// favoritesFragment.mGetView().setAlpha(1f);

		mPager.setAdapter(mAdapter);



		// This is required to avoid a black flash when the map is loaded. The flash is due
		// to the use of a SurfaceView as the underlying view of the map.
		mPager.requestTransparentRegion(mPager);
		mPager.setOnPageChangeListener(pageChangeListener);

        //TL-347 When "map" is available, set "TRACK CAR" as the default tab, instead of INFO
        if(dbBook.getTripStatus() == MBDefinition.MB_STATUS_ACCEPTED || dbBook.getTripStatus() == MBDefinition.MB_STATUS_ARRIVED || dbBook.getTripStatus() == MBDefinition.MB_STATUS_IN_SERVICE)
            tab1.performClick();
	}

	private void animateUnSelect(RelativeLayout tab, float positionOffset) {
		tab.setBackgroundColor(Color.rgb(RED_SELECTED + (int) (positionOffset * RED_TOTAL_DIFF), GREEN_SELECTED + (int) (positionOffset * GREEN_TOTAL_DIFF),
				BLUE_SELECTED + (int) (positionOffset * BLUE_TOTAL_DIFF)));
	}

	private void animateSelect(RelativeLayout tab, float positionOffset) {
		tab.setBackgroundColor(Color.rgb(RED - (int) (positionOffset * RED_TOTAL_DIFF), GREEN - (int) (positionOffset * GREEN_TOTAL_DIFF), BLUE
				- (int) (positionOffset * BLUE_TOTAL_DIFF)));
	}

	private void resetAllTabColor() {
		tab0.setBackgroundColor(getResources().getColor(R.color.background_tab));
		tab1.setBackgroundColor(getResources().getColor(R.color.background_tab));
	}

	private void resetAllAlpha() {
		if (trackingMapFragment.mGetView() != null)
			trackingMapFragment.mGetView().setAlpha(1);
		if (infoFragment.mGetView() != null)
			infoFragment.mGetView().setAlpha(1);
	}

	private RelativeLayout getTab(int i) {
		if (i == 0)
			return tab0;
		else if (i == 1)
			return tab1;
		else
			return null;
	}

	private class PagerAdapter extends FragmentPagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);

		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return infoFragment;
			case 1:
				// if (dbBook.getTripStatus() == MBDefinition.MB_STATUS_ARRIVED || dbBook.getTripStatus() == MBDefinition.MB_STATUS_ACCEPTED
				// || dbBook.getTripStatus() == MBDefinition.MB_STATUS_IN_SERVICE)
				// return trackingMapFragment;
				// else
				// return map404Fragment;
				return new RootFragment();
			default:
				return null;
			}
		}

	}

	private void findView() {
		tv_arrived_circle = (TextView) findViewById(R.id.tv_arrived_circle);
		tv_completed_circle = (TextView) findViewById(R.id.tv_completed_circle);
		tv_inservice_circle = (TextView) findViewById(R.id.tv_inservice_circle);
		tv_dispatched_circle = (TextView) findViewById(R.id.tv_dispatched_circle);
		
		
		
		TextView cancel_icon = (TextView) findViewById(R.id.cancel_icon); 
		TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel); 
		
//		TextView pay_icon = (TextView) findViewById(R.id.pay_icon); 
//		TextView tv_pay = (TextView) findViewById(R.id.tv_pay); 

		arrow_right = (TextView) findViewById(R.id.arrow_right);
		arrow_right.setTypeface(fontawesome);
		arrow_right.setText(MBDefinition.ICON_ARROW_RIGHT);
		cancel_icon.setTypeface(icon_pack);
		cancel_icon.setText(MBDefinition.ICON_CROSS);
		tv_cancel.setTypeface(exo2Bold);
		
//		pay_icon.setTypeface(icon_pack);
//		pay_icon.setText("V");
//		tv_pay.setTypeface(exo2Bold);
		
		
		tv_dispatched_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_arrived_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_inservice_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_completed_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		
		

		arrived_circle = (ImageView) findViewById(R.id.arrived_circle);
		completed_circle = (ImageView) findViewById(R.id.completed_circle);
		inservice_circle = (ImageView) findViewById(R.id.inservice_circle);
		dispatched_circle = (ImageView) findViewById(R.id.dispatched_circle);

		tv_id = (TextView) findViewById(R.id.tv_id);
		tv_book_time = (TextView) findViewById(R.id.tv_book_time);

		tv_id.setTypeface(exo2SemiBold);
		tv_book_time.setTypeface(exo2SemiBold);

		ll_cancel_btn = (LinearLayout) findViewById(R.id.ll_cancel_btn);
		//ll_pay_btn = (LinearLayout) findViewById(R.id.ll_pay_btn);
		cancel_cover = findViewById(R.id.cancel_cover); 
	}

	private void checkAndDisablePayBtns() {
		if (dbBook.getAlready_paid() & !dbBook.getMulti_pay_allow()) {
			// completed_pay_btn.setAlpha((float) 0.4);
			// arrived_pay_btn.setAlpha((float) 0.4);
			// inService_pay_btn.setAlpha((float) 0.4);
			//
			// completed_pay_btn.setClickable(false);
			// arrived_pay_btn.setClickable(false);
			// inService_pay_btn.setClickable(false);
		}
	}

	private void initListener() {

		OnClickListener cancelListener = new View.OnClickListener() {
			public void onClick(View v) {
				// change the status to cancelled in our database if no matter what
				AlertDialog.Builder builder = new AlertDialog.Builder(_context);
				builder.setTitle("Confirm Cancel");
				builder.setMessage("Are you sure you want to cancel?");
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						new CancelJobTask(_context, dbBook).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				});
				builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.show();
			}
		};

		OnClickListener payListener = new View.OnClickListener() {
			public void onClick(View v) {
				DBCreditCardDao creditCardDao = daoManager.getDBCreditCardDao(DaoManager.TYPE_READ);
				if (creditCardDao.queryBuilder().list().size() == 0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(_context);
					builder.setMessage(R.string.ask_register_cc).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(_context, EditCreditCardActivity.class);
							intent.putExtra(MBDefinition.EXTRA_BOOKING, dbBook);
							startActivityForResult(intent, MBDefinition.REQUEST_REGISTER_CC);
						}
					}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
					builder.show();
				} else {
					Intent intent = new Intent(_context, PayActivity.class);
					intent.putExtra(MBDefinition.DBBOOKING_EXTRA, dbBook);
					startActivity(intent);
					overridePendingTransition(R.anim.base_back_activity_enter, R.anim.base_back_activity_exit);
					finish();
				}
			}
		};

		ll_cancel_btn.setOnClickListener(cancelListener);
//		ll_cancel_btn_small.setOnClickListener(cancelListener);
//		ll_pay_btn.setOnClickListener(payListener);

		zoom_btn = (ImageView) findViewById(R.id.zoom_btn);
		zoom_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				trackingMapFragment.toggleCamera();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Logger.d(TAG, "onCreateOptionsMenu");
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
		if (id == android.R.id.home) {
			Utils.currentTab = 1;
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void startUpdateAnimation(MenuItem item) {
		// Do animation start
		isRefreshing = true;
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView) inflater.inflate(R.layout.iv_refresh, null);
		Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
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

	public void parseRecallJobResponse(List<DBBooking> dbBook1, LatLng carLatLng) {
		stopUpdateAnimation();
		this.dbBook = dbBook1.get(0);

		if (dbBook.getTripStatus() == MBDefinition.MB_STATUS_ACCEPTED || dbBook.getTripStatus() == MBDefinition.MB_STATUS_ARRIVED
				|| dbBook.getTripStatus() == MBDefinition.MB_STATUS_IN_SERVICE) {
			FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
			/*
			 * IMPORTANT: We use the "root frame" defined in "root_fragment.xml" as the reference to replace fragment
			 */
			trans.replace(R.id.root_frame, trackingMapFragment);

			/*
			 * IMPORTANT: The following lines allow us to add the fragment to the stack and return to it later, by pressing back
			 */
			// trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			// trans.addToBackStack(null);

			trans.commit();
			trackingMapFragment.updateCarMarker(carLatLng, dbBook);
		} else {
			FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
			/*
			 * IMPORTANT: We use the "root frame" defined in "root_fragment.xml" as the reference to replace fragment
			 */
			trans.replace(R.id.root_frame, map404Fragment);
			trans.commit();
		}

		if (mPager.getCurrentItem() == 1) {
            if(shouldShowCar())
			    zoom_btn.setVisibility(View.VISIBLE);
            else
                zoom_btn.setVisibility(View.GONE);
			setTab1Text();
		} else {
			zoom_btn.setVisibility(View.GONE);
			setTab0Text();
		}

		checkAndDisablePayBtns();
		switch (dbBook.getTripStatus()) {
		case MBDefinition.MB_STATUS_BOOKED:
			setupBookedUI();
			break;
		case MBDefinition.MB_STATUS_ACCEPTED:
			setUpAcceptedUI();
			break;
		case MBDefinition.MB_STATUS_ARRIVED:
			setUpArrivedUI();
			break;
		case MBDefinition.MB_STATUS_IN_SERVICE:
			setUpInServiceUI();
			break;
		case MBDefinition.MB_STATUS_COMPLETED:
		case MBDefinition.MB_STATUS_UNKNOWN: //TL-264
			setUpInCompletedUI();
			break;
		case MBDefinition.MB_STATUS_CANCELLED:
			setUpCanceledUI();
			break;
		default:
			break;
		}
	}

	private void disableCancelBtn() {
		ll_cancel_btn.setVisibility(View.VISIBLE);
		cancel_cover.setVisibility(View.VISIBLE);
	}

	private void enableCancelBtn() {
        //TL-344 Grey out Cancel Trip button for jobs that are in Unknown state.
        if(dbBook.getShouldForceDisableCancel())
            disableCancelBtn();
        else {
            ll_cancel_btn.setVisibility(View.VISIBLE);
            cancel_cover.setVisibility(View.GONE);
        }
	}
	
	private boolean shouldShowCar(){
		return (dbBook.getTripStatus()==MBDefinition.MB_STATUS_ARRIVED 
				|| dbBook.getTripStatus()==MBDefinition.MB_STATUS_ACCEPTED
				|| dbBook.getTripStatus()==MBDefinition.MB_STATUS_IN_SERVICE) &&
				dbBook.getCarLatitude() != 0 && dbBook.getCarLongitude() != 0;
	}

	private void setUpCanceledUI() {
		disableCancelBtn();
		//ll_cancel_btn.setVisibility(View.GONE);
		//ll_pay_btn.setVisibility(View.GONE);
		
		infoFragment.updateDriverAndVehicle();

		tv_dispatched_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_arrived_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_inservice_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_completed_circle.setTextColor(getResources().getColor(R.color.gray_line));

		tv_dispatched_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_arrived_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_inservice_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_completed_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);

		dispatched_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
		arrived_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
		inservice_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
		completed_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
	}

	private void setupBookedUI() {
		enableCancelBtn();
		//ll_cancel_btn.setVisibility(View.VISIBLE);
		//ll_pay_btn.setVisibility(View.VISIBLE);
		
		infoFragment.updateDriverAndVehicle();

		tv_dispatched_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_arrived_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_inservice_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_completed_circle.setTextColor(getResources().getColor(R.color.gray_line));

		tv_dispatched_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_arrived_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_inservice_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_completed_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);

		dispatched_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
		arrived_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
		inservice_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
		completed_circle.setBackground(getResources().getDrawable(R.drawable.shape_holo_circle));
	}

	private void setUpAcceptedUI() {
		enableCancelBtn();
//		ll_cancel_btn.setVisibility(View.VISIBLE);
//		ll_pay_btn.setVisibility(View.VISIBLE);
		
		infoFragment.updateDriverAndVehicle();

		tv_dispatched_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_arrived_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_inservice_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_completed_circle.setTextColor(getResources().getColor(R.color.gray_line));

		tv_dispatched_circle.setTypeface(exo2SemiBold, Typeface.BOLD);
		tv_arrived_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_inservice_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_completed_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);

		dispatched_circle.setImageResource(R.drawable.shape_solid_circle);
		arrived_circle.setImageResource(R.drawable.shape_holo_circle);
		inservice_circle.setImageResource(R.drawable.shape_holo_circle);
		completed_circle.setImageResource(R.drawable.shape_holo_circle);

	}

	private void setUpArrivedUI() {
		enableCancelBtn();
//		ll_cancel_btn.setVisibility(View.VISIBLE);
//		ll_pay_btn.setVisibility(View.VISIBLE);
		
		infoFragment.updateDriverAndVehicle();

		tv_dispatched_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_arrived_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_inservice_circle.setTextColor(getResources().getColor(R.color.gray_line));
		tv_completed_circle.setTextColor(getResources().getColor(R.color.gray_line));

		tv_dispatched_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_arrived_circle.setTypeface(exo2SemiBold, Typeface.BOLD);
		tv_inservice_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_completed_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);

		dispatched_circle.setImageResource(R.drawable.shape_solid_circle);
		arrived_circle.setImageResource(R.drawable.shape_solid_circle);
		inservice_circle.setImageResource(R.drawable.shape_holo_circle);
		completed_circle.setImageResource(R.drawable.shape_holo_circle);
	}

	private void setUpInServiceUI() {
		disableCancelBtn();
//		ll_cancel_btn.setVisibility(View.VISIBLE);
//		ll_pay_btn.setVisibility(View.VISIBLE);
		
		infoFragment.updateDriverAndVehicle();

		tv_dispatched_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_arrived_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_inservice_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_completed_circle.setTextColor(getResources().getColor(R.color.gray_line));

		tv_dispatched_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_arrived_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_inservice_circle.setTypeface(exo2SemiBold, Typeface.BOLD);
		tv_completed_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);

		dispatched_circle.setImageResource(R.drawable.shape_solid_circle);
		arrived_circle.setImageResource(R.drawable.shape_solid_circle);
		inservice_circle.setImageResource(R.drawable.shape_solid_circle);
		completed_circle.setImageResource(R.drawable.shape_holo_circle);
	}

	private void setUpInCompletedUI() {
		disableCancelBtn();
		
//		ll_cancel_btn.setVisibility(View.VISIBLE);
//		ll_pay_btn.setVisibility(View.VISIBLE);
		
		infoFragment.updateDriverAndVehicle();

		tv_dispatched_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_arrived_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_inservice_circle.setTextColor(getResources().getColor(R.color.gray_circle));
		tv_completed_circle.setTextColor(getResources().getColor(R.color.gray_circle));

		tv_dispatched_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_arrived_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_inservice_circle.setTypeface(exo2SemiBold, Typeface.NORMAL);
		tv_completed_circle.setTypeface(exo2SemiBold, Typeface.BOLD);

		dispatched_circle.setImageResource(R.drawable.shape_solid_circle);
		arrived_circle.setImageResource(R.drawable.shape_solid_circle);
		inservice_circle.setImageResource(R.drawable.shape_solid_circle);
		completed_circle.setImageResource(R.drawable.shape_solid_circle);

	}

	public void startRecallJobTask() {
		if (refresh_icon != null && !isRefreshing) {
			startUpdateAnimation(refresh_icon);
			new RecallJobTask(_context, dbBook.getTaxi_ride_id().toString(), MBDefinition.IS_FOR_ONE_JOB).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					dbBook.getDestID(), dbBook.getSysId());
		}
	}

	public void showCancelDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(_context);
		builder.setMessage(R.string.message_cancel_successful).setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				finish();
			}
		});
		builder.show();
	}

}
