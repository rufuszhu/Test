package digital.dispatch.TaxiLimoNewUI;


import java.util.concurrent.atomic.AtomicInteger;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import digital.dispatch.TaxiLimoNewUI.Book.BookFragment;
import digital.dispatch.TaxiLimoNewUI.Drawers.AboutActivity;
import digital.dispatch.TaxiLimoNewUI.Drawers.PaymentActivity;
import digital.dispatch.TaxiLimoNewUI.Drawers.PreferenceActivity;
import digital.dispatch.TaxiLimoNewUI.Drawers.ProfileActivity;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities.gcmType;
import digital.dispatch.TaxiLimoNewUI.History.HistoryFragment;
import digital.dispatch.TaxiLimoNewUI.Track.TrackFragment;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	public static final String EXTRA_MESSAGE = "message";


	private static final String TAG = "MainActivity";
	LocalBroadcastManager locBCManager;

	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	String regid;
	Context _context;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;


	private ViewPager mPager;
	private RelativeLayout tab0, tab1, tab2;
	private PagerAdapter mAdapter;
	private OnPageChangeListener pageChangeListener;
	private BookFragment bookFragment;
	public TrackFragment trackFragment;
	private HistoryFragment historyFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.base_back_activity_enter, R.anim.base_back_activity_exit);
		setContentView(R.layout.activity_main);
		Logger.d(TAG, "onCreate");

		_context = this;

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

		//initView();

		setUpTab();

		restoreActionBar();

		registerGCMReceiver();
	}

	private void registerGCMReceiver() {
		// Register GCM listener
		locBCManager = LocalBroadcastManager.getInstance(this);
		locBCManager.registerReceiver(mHandleMessageReceiver, new IntentFilter(gcmType.deletedMsg.toString()));
		locBCManager.registerReceiver(mHandleMessageReceiver, new IntentFilter(gcmType.message.toString()));
		locBCManager.registerReceiver(mHandleMessageReceiver, new IntentFilter(gcmType.register.toString()));
		locBCManager.registerReceiver(mHandleMessageReceiver, new IntentFilter(gcmType.unregister.toString()));
		locBCManager.registerReceiver(mHandleMessageReceiver, new IntentFilter(gcmType.recoverableError.toString()));
		locBCManager.registerReceiver(mHandleMessageReceiver, new IntentFilter(gcmType.error.toString()));
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.d(TAG, "onReusme");
		CommonUtilities.checkLateTrip(this, -1);
		mPager.setCurrentItem(Utils.currentTab);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Logger.d(TAG, "onPause");
		if (locBCManager != null) {
			locBCManager.unregisterReceiver(mHandleMessageReceiver);
		}
	}

	@Override
	protected void onDestroy() {
		Logger.d(TAG, "onDestroy");
		super.onDestroy();
	}


	private void setUpTab() {
		mPager = (ViewPager) findViewById(R.id.pager);
		setTabListener();
		bookFragment = BookFragment.newInstance();
		trackFragment = TrackFragment.newInstance();
		historyFragment = HistoryFragment.newInstance();

		mAdapter = new PagerAdapter(getSupportFragmentManager());
		pageChangeListener = new OnPageChangeListener() {

			@Override
			public void onPageSelected(int selected) {
				Log.e(TAG, "onPageSelected: " + selected);
				//Utils.currentTab=selected;
				restoreActionBar();
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		};
		// set second page back to default Alpha
		// favoritesFragment.mGetView().setAlpha(1f);

		mPager.setAdapter(mAdapter);

		// This is required to avoid a black flash when the map is loaded. The flash is due
		// to the use of a SurfaceView as the underlying view of the map.
		mPager.requestTransparentRegion(mPager);
		mPager.setOnPageChangeListener(pageChangeListener);
	}
	
	private void setTabListener() {
		tab0 = (RelativeLayout) findViewById(R.id.tab0);
		tab1 = (RelativeLayout) findViewById(R.id.tab1);
		tab2 = (RelativeLayout) findViewById(R.id.tab2);

		tab0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(0);
				resetAllTabColor();
				tab0.setBackgroundColor(getResources().getColor(R.color.background_tab_selected));

			}
		});
		tab1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(1);
				resetAllTabColor();
				tab1.setBackgroundColor(getResources().getColor(R.color.background_tab_selected));
			}
		});
		tab2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(2);
				resetAllTabColor();
				tab2.setBackgroundColor(getResources().getColor(R.color.background_tab_selected));
			}
		});
	}
	
	private void resetAllTabColor() {
		tab0.setBackgroundColor(getResources().getColor(R.color.background_tab));
		tab1.setBackgroundColor(getResources().getColor(R.color.background_tab));
		tab2.setBackgroundColor(getResources().getColor(R.color.background_tab));
	}
	
	private class PagerAdapter extends FragmentPagerAdapter {
		private final String[] TITLES = { "SEARCH", "FAVORITES", "CONTACTS" };

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return bookFragment;
			case 1:
				return trackFragment;
			case 2:
				return historyFragment;
			default:
				return null;
			}
		}

	}

	public void switchToTrackTab() {
		Utils.currentTab = 1;
		mPager.setCurrentItem(1);
	}

	public void switchToBookTab() {
		Utils.currentTab = 0;
		mPager.setCurrentItem(0);
	}




	@Override
	public void onNavigationDrawerItemSelected(int position) {
		Intent intent;
		switch (position) {
		case 0:
			intent = new Intent(this, ProfileActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.base_back_activity_enter, R.anim.base_back_activity_exit);
			break;
		case 1:
			intent = new Intent(this, PaymentActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.base_back_activity_enter, R.anim.base_back_activity_exit);
			break;
		case 2:
			intent = new Intent(this, PreferenceActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.base_back_activity_enter, R.anim.base_back_activity_exit);
			break;
		case 3:
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.base_back_activity_enter, R.anim.base_back_activity_exit);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);

		// if we want customize action bar
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM |
		// ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		// actionBar.setCustomView(R.layout.actionbar);
		// actionBar.setDisplayUseLogoEnabled(false);
		// actionBar.setIcon(R.color.transparent);
		// actionBar.setIcon(null);
		switch (mPager.getCurrentItem()) {
		case 0:
			Utils.currentTab = 0;
			mTitle = getString(R.string.book_title);
			break;
		case 1:
			Utils.currentTab = 1;
			mTitle = getString(R.string.track_title);
			break;
		case 2:
			Utils.currentTab = 2;
			mTitle = getString(R.string.history_title);
			break;
		}

		// TextView tv = (TextView) findViewById(R.id.actionbar_title);
		// tv.setText(mTitle);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.

			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	public NavigationDrawerFragment getDrawerFragment() {
		return mNavigationDrawerFragment;
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.v(TAG, "GCM intent received");
			// getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
			// | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			final int gcmID = intent.getIntExtra(CommonUtilities.GCM_ID, -1);

			if (intent.filterEquals(new Intent(gcmType.register.toString()))) {
				// String temp = getRegistrationId(_context);

				// if (temp.equalsIgnoreCase("")) {
				// registerInBackground();
				// }
				Log.v(TAG, "GCM Registered");
			} else if (intent.filterEquals(new Intent(gcmType.unregister.toString()))) {
				Logger.v(TAG, "GCM Unregistered");
			} else if (intent.filterEquals(new Intent(gcmType.message.toString()))) {
				Logger.v(TAG, "GCM message - " + newMessage);

				// if (isForeGround) {
				if (!CommonUtilities.checkLateTrip(MainActivity.this, gcmID)) {
					new AlertDialog.Builder(MainActivity.this).setTitle(R.string.gcm).setMessage(newMessage).setCancelable(false)
							.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
									if (gcmID != -1) {
										notificationManager.cancel(gcmID);
									}
									if (Utils.currentTab == 1) {
										TrackFragment fragment = (TrackFragment) getSupportFragmentManager().findFragmentByTag("track");
										fragment.startRecallJobTask();
									}
								}
							}).show();
				}
			} else if (intent.filterEquals(new Intent(gcmType.deletedMsg.toString()))) {
				Logger.v(TAG, "GCM Deleted Msg");
			} else if (intent.filterEquals(new Intent(gcmType.error.toString()))) {
				Logger.v(TAG, "GCM Error");
			} else if (intent.filterEquals(new Intent(gcmType.recoverableError.toString()))) {
				Logger.v(TAG, "GCM Recoverable Error");
			}
		}
	};

	public void showMessageDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage(getResources().getString(id)).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		}).setTitle(R.string.err_error_response);

		builder.show();
	}

}
