package digital.dispatch.TaxiLimoNewUI.Book;

import java.util.ArrayList;
import java.util.List;

import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import digital.dispatch.TaxiLimoNewUI.DBAttribute;
import digital.dispatch.TaxiLimoNewUI.DBAttributeDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.R.menu;
import digital.dispatch.TaxiLimoNewUI.Adapters.AttributeItemAdapter;
import digital.dispatch.TaxiLimoNewUI.Adapters.CompanyListAdapter;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.GetCompanyListTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

public class AttributeActivity extends ActionBarActivity {
	private CompanyItem[] companyArr;
	private ListView lv_company;
	private CompanyListAdapter cp_adapter;
	//private Address mAddress;
	private ArrayList<Integer> selected_attributes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attribute);
		
		//mAddress = getIntent().getParcelableExtra(MBDefinition.ADDRESS);

		

		lv_company = (ListView) findViewById(R.id.lv_company);
		lv_company.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Intent returnIntent = new Intent();
//				Logger.e("selecting company: " + cp_adapter.getCompanyItem(position).name);
//				returnIntent.putExtra(MBDefinition.COMPANY_ITEM, cp_adapter.getCompanyItem(position));
//				returnIntent.putExtra(MBDefinition.ADDRESS, mAddress);
//				setResult(RESULT_OK, returnIntent);
//				finish();
				finishWithData(position);
			}
		});
		

//		gridview.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//				Toast.makeText(AttributeActivity.this, "aabbcc" + position, Toast.LENGTH_SHORT).show();
//			}
//		});
		boolean isFromBooking = false;
		new GetCompanyListTask(this, Utils.mPickupAddress, isFromBooking).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	//override actionbar back button
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
            case android.R.id.home:
            	finishWithData(null);
            }
            return true;
    }
	//override android back button
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	finishWithData(null);
	    	return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	private void finishWithData(Integer position){
		Intent returnIntent = new Intent();
		if(position!=null)
			returnIntent.putExtra(MBDefinition.COMPANY_ITEM, cp_adapter.getCompanyItem(position));
		returnIntent.putExtra(MBDefinition.SELECTED_ATTRIBUTE, selected_attributes);
		//returnIntent.putExtra(MBDefinition.ADDRESS, mAddress);
		setResult(RESULT_OK, returnIntent);
		finish();
	}
	
	// call from AttributeItemAdapter
	public void filterCompany(ArrayList<Integer> positive_attribute_IDs) {
		selected_attributes = positive_attribute_IDs;
		if (positive_attribute_IDs.size() == 0) {
			cp_adapter = new CompanyListAdapter(this, companyArr);
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

			cp_adapter = new CompanyListAdapter(this, compArr);
			lv_company.setAdapter(cp_adapter);
		}
	}
	//called from getCompanyListResponse, load attribute grid here to prevent user filter before get company request is done
	public void loadCompanyList(CompanyItem[] tempCompList) {
		companyArr = tempCompList;

		for (int i = 0; i < tempCompList.length; i++) {
			CompanyItem.printCompanyItem(tempCompList[i]);
		}
		
		DaoManager daoManager = DaoManager.getInstance(this);
		DBAttributeDao attributeDao = daoManager.getDBAttributeDao(DaoManager.TYPE_READ);
		List<DBAttribute> attrList = attributeDao.queryBuilder().list();
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new AttributeItemAdapter(this, attrList));
		
		filterCompany(new ArrayList<Integer>());
	}

}
