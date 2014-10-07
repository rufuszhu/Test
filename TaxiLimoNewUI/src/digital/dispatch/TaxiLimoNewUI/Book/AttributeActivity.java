package digital.dispatch.TaxiLimoNewUI.Book;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import digital.dispatch.TaxiLimoNewUI.DBAttribute;
import digital.dispatch.TaxiLimoNewUI.DBAttributeDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.AttributeItemAdapter;
import digital.dispatch.TaxiLimoNewUI.Adapters.CompanyListAdapter;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.GetCompanyListTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class AttributeActivity extends Activity {
	private static final String TAG = "AttributeActivity";
	private CompanyItem[] companyArr;
	private ListView lv_company;
	private CompanyListAdapter cp_adapter;
	// private Address mAddress;
	// private ArrayList<Integer> selected_attributes;
	private MenuItem refresh_icon;
	private boolean refreshing;
	private boolean shouldBookRightAfter;
	private Context _context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attribute);
		_context = this;
		// mAddress = getIntent().getParcelableExtra(MBDefinition.ADDRESS);
		DaoManager daoManager = DaoManager.getInstance(this);
		DBAttributeDao attributeDao = daoManager.getDBAttributeDao(DaoManager.TYPE_READ);
		List<DBAttribute> attrList = attributeDao.queryBuilder().list();

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new AttributeItemAdapter(this, attrList));
		
		shouldBookRightAfter = getIntent().getExtras().getBoolean(MBDefinition.EXTRA_SHOULD_BOOK_RIGHT_AFTER);
		lv_company = (ListView) findViewById(R.id.lv_company);
		lv_company.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (shouldBookRightAfter) {
					Utils.mSelectedCompany = cp_adapter.getCompanyItem(position);
					Intent returnIntent = new Intent();
					setResult(RESULT_OK, returnIntent);
					finish();
				} else {
					Utils.mSelectedCompany = cp_adapter.getCompanyItem(position);
					finish();
				}
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshing = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Logger.e(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.attribute, menu);
		refresh_icon = menu.findItem(R.id.action_refresh);
		if (refreshing) {
			boolean isFromBooking = false;
			new GetCompanyListTask(this, Utils.mPickupAddress, isFromBooking).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			startUpdateAnimation(refresh_icon);
		} else {
			refresh_icon.setVisible(false);
		}
		return true;
	}

	// override actionbar back button
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
		}
		return true;
	}

	public void startUpdateAnimation(MenuItem item) {
		// Do animation start
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView) inflater.inflate(R.layout.iv_refresh, null);
		Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
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
			refresh_icon.setVisible(false);
			refreshing = false;
			this.invalidateOptionsMenu();
		}
	}

	// override android back button
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// call from AttributeItemAdapter
	public void filterCompany(ArrayList<Integer> positive_attribute_IDs) {
		// selected_attributes = positive_attribute_IDs;
		Utils.selected_attribute = positive_attribute_IDs;
		if (positive_attribute_IDs.size() == 0) {
			cp_adapter = new CompanyListAdapter(this, companyArr, shouldBookRightAfter);
			lv_company.setAdapter(cp_adapter);
		} else {
			ArrayList<CompanyItem> temp = new ArrayList<CompanyItem>();

			for (int i = 0; i < companyArr.length; i++) {
				boolean hasEverything = true;
				for (int j = 0; j < positive_attribute_IDs.size(); j++) {
					if (!companyArr[i].attributes.contains(positive_attribute_IDs.get(j) + "")) {
						hasEverything = false;
						break;
					}
				}
				if (hasEverything)
					temp.add(companyArr[i]);
			}

			// arrayList to array
			CompanyItem[] compArr = new CompanyItem[temp.size()];
			for (int i = 0; i < temp.size(); i++) {
				compArr[i] = temp.get(i);
			}

			cp_adapter = new CompanyListAdapter(this, compArr, shouldBookRightAfter);
			lv_company.setAdapter(cp_adapter);
		}
	}

	// called from getCompanyListResponse, load attribute grid here to prevent user filter before get company request is done
	public void loadCompanyList(CompanyItem[] tempCompList) {
		stopUpdateAnimation();
		companyArr = tempCompList;

		for (int i = 0; i < tempCompList.length; i++) {
			CompanyItem.printCompanyItem(tempCompList[i]);
		}

		filterCompany(new ArrayList<Integer>());
	}

}
