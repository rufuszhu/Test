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
import android.graphics.Typeface;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import digital.dispatch.TaxiLimoNewUI.Book.BookFragment;
import digital.dispatch.TaxiLimoNewUI.Drawers.AboutActivity;
import digital.dispatch.TaxiLimoNewUI.Drawers.PaymentActivity;
import digital.dispatch.TaxiLimoNewUI.Drawers.PreferenceActivity;
import digital.dispatch.TaxiLimoNewUI.Drawers.ProfileActivity;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities.gcmType;
import digital.dispatch.TaxiLimoNewUI.History.HistoryFragment;
import digital.dispatch.TaxiLimoNewUI.Track.TrackFragment;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class MainActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
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
	
	private BroadcastReceiver bcReceiver;

	private TextView tab0_icon;
	private TextView tab1_icon;
	private TextView tab2_icon;
	private TextView tab0_text;
	private TextView tab1_text;
	private TextView tab2_text;

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

		// initView();

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
		//TL-235
		boolean isTrackDetail = false;
		bcReceiver = CommonUtilities.getGenericReceiver(this, isTrackDetail);
		LocalBroadcastManager.getInstance(this).registerReceiver(bcReceiver, new IntentFilter(gcmType.message.toString()));
		super.onResume();
		Logger.d(TAG, "onResume");
		CommonUtilities.checkLateTrip(this, -1);
		mPager.setCurrentItem(Utils.currentTab);

        /*
        int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
        String usedMegsString = String.format(" - Memory Used: %d MB", usedMegs);
        Logger.e(TAG,  usedMegsString);
        */
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(bcReceiver);
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
				if (selected == 0) {
					selectTab0();
				} else if (selected == 1) {
					selectTab1();
				} else if (selected == 2) {
					selectTab2();
				}
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
		selectTab0();
		// This is required to avoid a black flash when the map is loaded. The flash is due
		// to the use of a SurfaceView as the underlying view of the map.
		mPager.requestTransparentRegion(mPager);
		mPager.setOnPageChangeListener(pageChangeListener);
	}

	private void setTabListener() {
		tab0 = (RelativeLayout) findViewById(R.id.tab0);
		tab1 = (RelativeLayout) findViewById(R.id.tab1);
		tab2 = (RelativeLayout) findViewById(R.id.tab2);

		tab0_text = (TextView) findViewById(R.id.tab0_text);
		tab1_text = (TextView) findViewById(R.id.tab1_text);
		tab2_text = (TextView) findViewById(R.id.tab2_text);

        Typeface exoFamily = FontCache.getFont(this, "fonts/Exo2-SemiBold.ttf");
		tab0_text.setTypeface(exoFamily);
		tab1_text.setTypeface(exoFamily);
		tab2_text.setTypeface(exoFamily);

        Typeface fontFamily = FontCache.getFont(this, "fonts/icon_pack.ttf");
		tab0_icon = (TextView) findViewById(R.id.tab0_icon);
		tab0_icon.setTypeface(fontFamily);
		tab0_icon.setText(MBDefinition.icon_tab_calendar);

		tab1_icon = (TextView) findViewById(R.id.tab1_icon);
		tab1_icon.setTypeface(fontFamily);
		tab1_icon.setText(MBDefinition.icon_tab_track);

		tab2_icon = (TextView) findViewById(R.id.tab2_icon);
		tab2_icon.setTypeface(fontFamily);
		tab2_icon.setText(MBDefinition.icon_tab_clock);

		tab0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(0);
				selectTab0();
			}
		});
		tab1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(1);
				selectTab1();
			}
		});
		tab2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(2);
				selectTab2();
			}
		});
	}

	private void selectTab0() {
		final int textColor = getResources().getColor(R.color.tab_text);
		final int textColorSelected = getResources().getColor(R.color.tab_text_selected);
		tab0_text.setTextColor(textColorSelected);
		tab1_text.setTextColor(textColor);
		tab2_text.setTextColor(textColor);
		tab0_icon.setTextColor(textColorSelected);
		tab1_icon.setTextColor(textColor);
		tab2_icon.setTextColor(textColor);
	}

	private void selectTab1() {
		final int textColor = getResources().getColor(R.color.tab_text);
		final int textColorSelected = getResources().getColor(R.color.tab_text_selected);
		tab0_text.setTextColor(textColor);
		tab1_text.setTextColor(textColorSelected);
		tab2_text.setTextColor(textColor);
		tab0_icon.setTextColor(textColor);
		tab1_icon.setTextColor(textColorSelected);
		tab2_icon.setTextColor(textColor);
	}

	private void selectTab2() {
		final int textColor = getResources().getColor(R.color.tab_text);
		final int textColorSelected = getResources().getColor(R.color.tab_text_selected);
		tab0_text.setTextColor(textColor);
		tab1_text.setTextColor(textColor);
		tab2_text.setTextColor(textColorSelected);
		tab0_icon.setTextColor(textColor);
		tab1_icon.setTextColor(textColor);
		tab2_icon.setTextColor(textColorSelected);
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
			startActivityForAnim(intent);
			break;
		// case 1:
		// intent = new Intent(this, PaymentActivity.class);
		// startActivityForAnim(intent);
		// break;
		// case 2:
		// intent = new Intent(this, PreferenceActivity.class);
		// startActivityForAnim(intent);
		// break;
		case 1:
			intent = new Intent(this, AboutActivity.class);
			startActivityForAnim(intent);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setIcon(R.color.transparent);
		actionBar.setIcon(null);
		int titleId = getResources().getIdentifier("action_bar_title", "id",
	            "android");
        Typeface face = FontCache.getFont(_context, "fonts/Exo2-Light.ttf");
	    TextView yourTextView = (TextView) findViewById(titleId);
	    yourTextView.setTypeface(face);
		// if we want customize action bar
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		// actionBar.setCustomView(R.layout.actionbar);
		// actionBar.setDisplayShowTitleEnabled(true);
		// actionBar.setDisplayUseLogoEnabled(false);
		// getActionBar().setHomeButtonEnabled(true);
		// getActionBar().setDisplayHomeAsUpEnabled(true);
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
										trackFragment.startRecallJobTask();
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
