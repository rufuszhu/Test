package digital.dispatch.TaxiLimoNewUI.Book;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities.gcmType;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Widget.NonSwipeableViewPager;

public class ModifyAddressActivity extends BaseActivity {
	private static final String TAG = "ModifyAddressActivity";
	private boolean isDesitination;

	public static FragmentManager fragmentManager;
	private RelativeLayout tab0, tab1, tab2;
	private PagerAdapter mAdapter;
	private NonSwipeableViewPager mPager;
	private OnPageChangeListener pageChangeListener;
	public FavoritesFragment favoritesFragment;
	public ContactsFragment contactsFragment;
	public SearchFragment searchFragment;
	
	private BroadcastReceiver bcReceiver;
	private Context _context;

	private TextView tab0_icon;
	private TextView tab1_icon;
	private TextView tab2_icon;
	private TextView tab0_text;
	private TextView tab1_text;
	private TextView tab2_text;

	// LinearLayout select_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_address);
		_context = this;
		findViews();

		isDesitination = getIntent().getBooleanExtra(MBDefinition.IS_DESTINATION, false);

		setupActionBarTitle();
		setUpTab();
		bindEvents();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean getIsDesitination() {
		return isDesitination;
	}

	private void setupActionBarTitle() {
		ActionBar ab = getActionBar();
		if (isDesitination) {
			ab.setTitle(getString(R.string.title_activity_destination));
		} else {
			ab.setTitle(getString(R.string.title_activity_pick_up));
		}
	}

	private void setTabListener() {
		tab0 = (RelativeLayout) findViewById(R.id.tab0);
		tab1 = (RelativeLayout) findViewById(R.id.tab1);
		tab2 = (RelativeLayout) findViewById(R.id.tab2);

		tab0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(0);
			}
		});
		tab1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(1);
			}
		});
		tab2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(2);
			}
		});
	}

	private void setUpTab() {
		mPager = (NonSwipeableViewPager) findViewById(R.id.pager);
		setTabListener();
		favoritesFragment = FavoritesFragment.newInstance();
		contactsFragment = new ContactsFragment();
		searchFragment = SearchFragment.newInstance();

		tab0 = (RelativeLayout) findViewById(R.id.tab0);
		tab1 = (RelativeLayout) findViewById(R.id.tab1);
		tab2 = (RelativeLayout) findViewById(R.id.tab2);

		tab0_text = (TextView) findViewById(R.id.tab0_text);
		tab1_text = (TextView) findViewById(R.id.tab1_text);
		tab2_text = (TextView) findViewById(R.id.tab2_text);
		
		Typeface exoFamily = Typeface.createFromAsset(getAssets(), "fonts/Exo2-SemiBold.ttf");
		tab0_text.setTypeface(exoFamily);
		tab1_text.setTypeface(exoFamily);
		tab2_text.setTypeface(exoFamily);

		Typeface fontFamily = Typeface.createFromAsset(getAssets(), "fonts/icon_pack.ttf");
		tab0_icon = (TextView) findViewById(R.id.tab0_icon);
		tab0_icon.setTypeface(fontFamily);
		tab0_icon.setText(MBDefinition.icon_tab_search);

		Typeface fontAwesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
		tab1_icon = (TextView) findViewById(R.id.tab1_icon);
		tab1_icon.setTypeface(fontAwesome);
		tab1_icon.setText(MBDefinition.icon_tab_fav);

		tab2_icon = (TextView) findViewById(R.id.tab2_icon);
		tab2_icon.setTypeface(fontFamily);
		tab2_icon.setText(MBDefinition.icon_tab_contact);

		mAdapter = new PagerAdapter(getSupportFragmentManager());
		fragmentManager = getSupportFragmentManager();
		pageChangeListener = new OnPageChangeListener() {

			@Override
			public void onPageSelected(int selected) {
				Log.e(TAG, "onPageSelected: " + selected);

				if (selected == 0) {
					selectTab0();
					//searchFragment.getData();
				} else if (selected == 1) {
					selectTab1();
				} else if (selected == 2) {
					selectTab2();
				}

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



	public void notifyFavoriteDataChange(DBAddress value) {
		favoritesFragment.notifyDataChange(value);
	}

	// public static class TextFragment extends Fragment {
	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
	// return inflater.inflate(R.layout.text_fragment, container, false);
	// }
	// }

	private class PagerAdapter extends FragmentPagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 3;
		}


		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return searchFragment;
			case 1:
				return favoritesFragment;
			case 2:
				return contactsFragment;
			default:
				return null;
			}
		}

	}

	private void bindEvents() {

	}

	private void findViews() {

	}

	@Override
	protected void onResume() {
		//TL-235
		boolean isTrackDetail = false;
		bcReceiver = CommonUtilities.getGenericReceiver(_context, isTrackDetail);
		LocalBroadcastManager.getInstance(this).registerReceiver(bcReceiver, new IntentFilter(gcmType.message.toString()));
		super.onResume();
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(bcReceiver);
		super.onPause();
	}

}
