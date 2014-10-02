package digital.dispatch.TaxiLimoNewUI;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import digital.dispatch.TaxiLimoNewUI.Book.BookFragment;
import digital.dispatch.TaxiLimoNewUI.Drawers.PaymentActivity;
import digital.dispatch.TaxiLimoNewUI.Drawers.PreferenceActivity;
import digital.dispatch.TaxiLimoNewUI.Drawers.ProfileActivity;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities.gcmType;
import digital.dispatch.TaxiLimoNewUI.History.HistoryFragment;
import digital.dispatch.TaxiLimoNewUI.Task.RegisterDeviceTask;
import digital.dispatch.TaxiLimoNewUI.Track.TrackFragment;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "MainActivity";
	LocalBroadcastManager locBCManager;
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	String regid;
	Context _context;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	// Declare Tab Variable
	private FragmentTabHost mTabHost;
	View bookTabView;
	View trackTabView;
	View historyTabView;

	ImageView bookImageView;
	ImageView trackImageView;
	ImageView historyImageView;

	TextView booktab_indicator;
	TextView tracktab_indicator;
	TextView historytab_indicator;

	private final String BOOK_TAB = "book";
	private final String TRACK_TAB = "track";
	private final String HISTORY_TAB = "history";
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

		initView();

		setupTab();

		restoreActionBar();

		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(_context);
			Logger.e("GCM id: " + regid);
			if (regid.isEmpty()) {
				registerInBackground();
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}

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
		checkPlayServices();
		CommonUtilities.checkLateTrip(this, -1);
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

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it doesn't, display a dialog that allows users to download the APK from the Google Play Store or enable it in the device's
	 * system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logger.e("onActivityResult");
		Logger.e("requestCode: " + requestCode);
		if (requestCode == MBDefinition.REQUEST_PICKUPADDRESS_CODE) {
			if (resultCode == RESULT_OK) {
				// this address comes from modifyAddress activity, and it has
				// been validated
				if (data.getExtras().getParcelable(MBDefinition.ADDRESS) != null) {
					// Logger.e("dataBundle address: " +
					// dataBundle.getString(MBDefinition.ADDRESS));
					Utils.mPickupAddress = (Address) data.getExtras().getParcelable(MBDefinition.ADDRESS);
				}
			}
		} else if (requestCode == MBDefinition.REQUEST_DROPOFFADDRESS_CODE) {
			if (resultCode == RESULT_OK) {
				// this address comes from modifyAddress activity, and it has
				// been validated
				if (data.getExtras().getParcelable(MBDefinition.ADDRESS) != null) {
					// Logger.e("dataBundle address: " +
					// dataBundle.getString(MBDefinition.ADDRESS));
					Utils.mDropoffAddress = (Address) data.getExtras().getParcelable(MBDefinition.ADDRESS);
				}
			}
		} 
//		else if (requestCode == MBDefinition.REQUEST_COMPANYITEM_CODE) {
//			if (resultCode == RESULT_OK) {
//				if (data.getSerializableExtra(MBDefinition.COMPANY_ITEM) != null) {
//					Logger.e(TAG, "selected company: " + ((CompanyItem) data.getSerializableExtra(MBDefinition.COMPANY_ITEM)).name);
//					Utils.mSelectedCompany = (CompanyItem) data.getSerializableExtra(MBDefinition.COMPANY_ITEM);
//				}
//			}
//		} 
		else if (requestCode == MBDefinition.REQUEST_SELECT_COMPANY_TO_BOOK) {
			if (resultCode == RESULT_OK) {
				Utils.bookJob(Utils.mSelectedCompany, _context);
			}
		}
	}

	// public void setSelectedAttribute(ArrayList<Integer> arrayList) {
	// selected_attribute = arrayList;
	// Logger.e(TAG, "setSelectedAttribute");
	// }
	// public ArrayList<Integer> getSelectedAttribute(){
	// return selected_attribute;
	// }
	// public void setSelectedCompany(CompanyItem company) {
	// mSelectedCompany = company;
	// }
	//
	// public CompanyItem getSelectedCompany() {
	// return mSelectedCompany;
	// }
	private void initView() {
		bookTabView = LayoutInflater.from(this).inflate(R.layout.tab, null);
		trackTabView = LayoutInflater.from(this).inflate(R.layout.tab, null);
		historyTabView = LayoutInflater.from(this).inflate(R.layout.tab, null);
		bookImageView = (ImageView) bookTabView.findViewById(R.id.tab_icon);
		trackImageView = (ImageView) trackTabView.findViewById(R.id.tab_icon);
		historyImageView = (ImageView) historyTabView.findViewById(R.id.tab_icon);

		booktab_indicator = (TextView) bookTabView.findViewById(R.id.tab_indicator);
		tracktab_indicator = (TextView) trackTabView.findViewById(R.id.tab_indicator);
		historytab_indicator = (TextView) historyTabView.findViewById(R.id.tab_indicator);
	}

	private void setupTab() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		mTabHost.addTab(mTabHost.newTabSpec(BOOK_TAB).setIndicator(getTabIndicator(this, bookImageView, bookTabView, R.string.book, R.drawable.tab_book_icon_selected)), BookFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(TRACK_TAB).setIndicator(getTabIndicator(this, trackImageView, trackTabView, R.string.track, R.drawable.tab_track_icon)), TrackFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(HISTORY_TAB).setIndicator(getTabIndicator(this, historyImageView, historyTabView, R.string.history, R.drawable.tab_history_icon)), HistoryFragment.class,
				null);

		mTabHost.setOnTabChangedListener(new FragmentTabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				restoreActionBar();
				restoreTab();
			}
		});
		mTabHost.getTabWidget().setDividerDrawable(null);
		booktab_indicator.setVisibility(View.VISIBLE);

		mTabHost.setCurrentTab(Utils.currentTab);
	}
	
	public void switchToTrackTab(){
		Utils.currentTab = 1;
		mTabHost.setCurrentTab(1);
	}

	private View getTabIndicator(Context context, ImageView iv, View view, int title, int drawable) {
		TextView tv = (TextView) view.findViewById(R.id.tab_text);
		tv.setText(title);
		iv.setImageResource(drawable);
		return view;
	}

	public void restoreTab() {
		switch (mTabHost.getCurrentTab()) {
		case 0:
			bookImageView.setImageResource(R.drawable.tab_book_icon_selected);
			trackImageView.setImageResource(R.drawable.tab_track_icon);
			historyImageView.setImageResource(R.drawable.tab_history_icon);

			booktab_indicator.setVisibility(View.VISIBLE);
			tracktab_indicator.setVisibility(View.GONE);
			historytab_indicator.setVisibility(View.GONE);
			break;
		case 1:
			bookImageView.setImageResource(R.drawable.tab_book_icon);
			trackImageView.setImageResource(R.drawable.tab_track_icon_selected);
			historyImageView.setImageResource(R.drawable.tab_history_icon);

			booktab_indicator.setVisibility(View.GONE);
			tracktab_indicator.setVisibility(View.VISIBLE);
			historytab_indicator.setVisibility(View.GONE);
			break;
		case 2:
			bookImageView.setImageResource(R.drawable.tab_book_icon);
			trackImageView.setImageResource(R.drawable.tab_track_icon);
			historyImageView.setImageResource(R.drawable.tab_history_icon_selected);

			booktab_indicator.setVisibility(View.GONE);
			tracktab_indicator.setVisibility(View.GONE);
			historytab_indicator.setVisibility(View.VISIBLE);
			break;
		}
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
		switch (mTabHost.getCurrentTab()) {
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

	// public Address getPickupAddress() {
	// return mPickupAddress;
	// }
	// public Address getDropoffAddress() {
	// return mDropoffAddress;
	// }
	//
	// public void setPickupAddress(Address mAddress) {
	// this.mPickupAddress = mAddress;
	// }
	// public void setDropoffAddress(Address mAddress) {
	// this.mDropoffAddress = mAddress;
	// }
	// public ArrayList<AttributeItem> getAttributeList() {
	// return attributeList;
	// }
	// called from getMBParamTask

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // as you specify a parent activity in AndroidManifest.xml.
	// int id = item.getItemId();
	//
	// if (id == R.id.action_refresh) {
	// return true;
	// }
	//
	// return super.onOptionsItemSelected(item);
	// }
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.v(TAG, "GCM intent received");
			// getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
			// | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			final int gcmID = intent.getIntExtra(CommonUtilities.GCM_ID, -1);

			if (intent.filterEquals(new Intent(gcmType.register.toString()))) {
				String temp = getRegistrationId(_context);
				;

				if (temp.equalsIgnoreCase("")) {
					registerInBackground();
				}
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
									if(Utils.currentTab==1){
										TrackFragment fragment = (TrackFragment) getSupportFragmentManager().findFragmentByTag("track");
										fragment.startRecallJobTask();
									}
								}
							}).show();
				}
				// }
			} else if (intent.filterEquals(new Intent(gcmType.deletedMsg.toString()))) {
				Logger.v(TAG, "GCM Deleted Msg");
			} else if (intent.filterEquals(new Intent(gcmType.error.toString()))) {
				Logger.v(TAG, "GCM Error");
			} else if (intent.filterEquals(new Intent(gcmType.recoverableError.toString()))) {
				Logger.v(TAG, "GCM Recoverable Error");
			}
		}
	};

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Integer, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(_context);
					}
					regid = gcm.register(CommonUtilities.SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your app.
					// The request to your server should be authenticated if your app
					// is using accounts.
					new RegisterDeviceTask(_context, regid).execute();

					// For this demo: we don't need to send it because the device
					// will send upstream messages to a server that echo back the
					// message using the 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(_context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// mDisplay.append(msg + "\n");
				Logger.e(TAG, msg);
			}

		}.execute(null, null, null);
	}

	/**
	 * Stores the registration ID and app versionCode in the application's {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Logger.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Logger.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}

}
