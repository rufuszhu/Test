package digital.dispatch.TaxiLimoNewUI;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import digital.dispatch.TaxiLimoNewUI.Book.BookFragment;
import digital.dispatch.TaxiLimoNewUI.Drawers.PaymentActivity;
import digital.dispatch.TaxiLimoNewUI.Drawers.PreferenceActivity;
import digital.dispatch.TaxiLimoNewUI.Drawers.ProfileActivity;
import digital.dispatch.TaxiLimoNewUI.History.HistoryFragment;
import digital.dispatch.TaxiLimoNewUI.Task.GetMBParamTask;
import digital.dispatch.TaxiLimoNewUI.Track.TrackFragment;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static final String TAG = "MainActivity";

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private Address mPickupAddress;
	private Address mDropoffAddress;

	// Declare Tab Variable
	private FragmentTabHost mTabHost;
	View bookTabView;
	View trackTabView;
	View historyTabView;

	ImageView bookImageView;
	ImageView trackImageView;
	ImageView historyImageView;

	private final String BOOK_TAB = "book";
	private final String TRACK_TAB = "track";
	private final String HISTORY_TAB = "history";
	private static int currentTab = 0;
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Logger.d(TAG, "onCreate");
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		initView();

		setupTab();

		restoreActionBar();
		
		new GetMBParamTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}
	@Override
	protected void onResume(){
		super.onResume();
		Logger.d(TAG, "onReusme");
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
					setPickupAddress((Address) data.getExtras().getParcelable(
							MBDefinition.ADDRESS));
					
				}
			}
		}
		else if(requestCode == MBDefinition.REQUEST_DROPOFFADDRESS_CODE){
			if (resultCode == RESULT_OK) {
				// this address comes from modifyAddress activity, and it has
				// been validated
				if (data.getExtras().getParcelable(MBDefinition.ADDRESS) != null) {
					// Logger.e("dataBundle address: " +
					// dataBundle.getString(MBDefinition.ADDRESS));
					setDropoffAddress((Address) data.getExtras().getParcelable(
							MBDefinition.ADDRESS));
					
				}
			}
		}
	}

	private void initView() {
		bookTabView = LayoutInflater.from(this).inflate(R.layout.tab, null);
		trackTabView = LayoutInflater.from(this).inflate(R.layout.tab, null);
		historyTabView = LayoutInflater.from(this).inflate(R.layout.tab, null);
		bookImageView = (ImageView) bookTabView.findViewById(R.id.tab_icon);
		trackImageView = (ImageView) trackTabView.findViewById(R.id.tab_icon);
		historyImageView = (ImageView) historyTabView
				.findViewById(R.id.tab_icon);
	}

	private void setupTab() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		mTabHost.addTab(
				mTabHost.newTabSpec(BOOK_TAB).setIndicator(
						getTabIndicator(this, bookImageView, bookTabView,
								R.string.book, R.drawable.ic_action_refresh)),
				BookFragment.class, null);
		mTabHost.addTab(
				mTabHost.newTabSpec(TRACK_TAB).setIndicator(
						getTabIndicator(this, trackImageView, trackTabView,
								R.string.track, R.drawable.ic_action_event)),
				TrackFragment.class, null);
		mTabHost.addTab(
				mTabHost.newTabSpec(HISTORY_TAB).setIndicator(
						getTabIndicator(this, historyImageView, historyTabView,
								R.string.history, R.drawable.ic_action_event)),
				HistoryFragment.class, null);
		// mTabHost.setCurrentTab(currentTab);
		mTabHost.setOnTabChangedListener(new FragmentTabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				restoreActionBar();
				restoreTab();
			}
		});
		mTabHost.getTabWidget().setDividerDrawable(null);
	}

	private View getTabIndicator(Context context, ImageView iv, View view,
			int title, int drawable) {
		TextView tv = (TextView) view.findViewById(R.id.tab_text);
		tv.setText(title);
		iv.setImageResource(drawable);
		return view;
	}

	public void restoreTab() {
		switch (mTabHost.getCurrentTab()) {
		case 0:
			bookImageView.setImageResource(R.drawable.ic_action_refresh);
			trackImageView.setImageResource(R.drawable.ic_action_event);
			historyImageView.setImageResource(R.drawable.ic_action_event);
			break;
		case 1:
			bookImageView.setImageResource(R.drawable.ic_action_event);
			trackImageView.setImageResource(R.drawable.ic_action_refresh);
			historyImageView.setImageResource(R.drawable.ic_action_event);
			break;
		case 2:
			bookImageView.setImageResource(R.drawable.ic_action_event);
			trackImageView.setImageResource(R.drawable.ic_action_event);
			historyImageView.setImageResource(R.drawable.ic_action_refresh);
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
			break;
		case 1:
			intent = new Intent(this, PaymentActivity.class);
			startActivity(intent);
			break;
		case 2:
			intent = new Intent(this, PreferenceActivity.class);
			startActivity(intent);
			break;
		case 3:
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
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
			currentTab = 0;
			mTitle = getString(R.string.book_title);
			break;
		case 1:
			currentTab = 1;
			mTitle = getString(R.string.track_title);
			break;
		case 2:
			currentTab = 2;
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

	public Address getPickupAddress() {
		return mPickupAddress;
	}
	public Address getDropoffAddress() {
		return mDropoffAddress;
	}

	public void setPickupAddress(Address mAddress) {
		this.mPickupAddress = mAddress;
	}
	public void setDropoffAddress(Address mAddress) {
		this.mDropoffAddress = mAddress;
	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // as you specify a parent activity in AndroidManifest.xml.
	// int id = item.getItemId();
	//
	// if (id == R.id.action_refresh) {
	// Toast.makeText(this, "Refreshingasdfasf", Toast.LENGTH_SHORT).show();
	// return true;
	// }
	//
	// return super.onOptionsItemSelected(item);
	// }

}
