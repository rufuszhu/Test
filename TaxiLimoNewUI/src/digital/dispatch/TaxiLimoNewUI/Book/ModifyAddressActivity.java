package digital.dispatch.TaxiLimoNewUI.Book;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import digital.dispatch.TaxiLimoNewUI.Widget.NonSwipeableViewPager;

public class ModifyAddressActivity extends BaseActivity {
	private static final String TAG = "ModifyAddressActivity";
	private boolean isDesitination;

	public static FragmentManager fragmentManager;
	private RelativeLayout tab0, tab1, tab2;
	private PagerAdapter mAdapter;
	private NonSwipeableViewPager mPager;
	private OnPageChangeListener pageChangeListener;
	private FavoritesFragment favoritesFragment;
	public ContactsFragment contactsFragment;
	private SearchFragment searchFragment;

	// LinearLayout select_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_address);

		findViews();

		isDesitination = getIntent().getBooleanExtra(MBDefinition.IS_DESTINATION, false);

		setupActionBarTitle();
		setUpTab();
		bindEvents();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id==android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public boolean getIsDesitination(){
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

	private void setUpTab() {
		mPager = (NonSwipeableViewPager) findViewById(R.id.pager);
		setTabListener();
		favoritesFragment = FavoritesFragment.newInstance();
		contactsFragment = new ContactsFragment();
		searchFragment = SearchFragment.newInstance();

		mAdapter = new PagerAdapter(getSupportFragmentManager());
		fragmentManager = getSupportFragmentManager();
		pageChangeListener = new OnPageChangeListener() {

			@Override
			public void onPageSelected(int selected) {
				Log.e(TAG, "onPageSelected: " + selected);
				if(selected==0)
					searchFragment.getData();

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

	private void resetAllTabColor() {
		tab0.setBackgroundColor(getResources().getColor(R.color.background_tab));
		tab1.setBackgroundColor(getResources().getColor(R.color.background_tab));
		tab2.setBackgroundColor(getResources().getColor(R.color.background_tab));
	}

	public void notifyFavoriteDataChange(DBAddress value){
		favoritesFragment.notifyDataChange(value);
	}

//	public static class TextFragment extends Fragment {
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
//			return inflater.inflate(R.layout.text_fragment, container, false);
//		}
//	}

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
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

}
