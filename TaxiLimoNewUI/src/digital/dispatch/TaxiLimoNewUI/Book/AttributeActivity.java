package digital.dispatch.TaxiLimoNewUI.Book;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.TextView;

import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.DBAttribute;
import digital.dispatch.TaxiLimoNewUI.DBAttributeDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.AttributeItemAdapter;
import digital.dispatch.TaxiLimoNewUI.Adapters.CompanyListAdapter;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.GetCompanyListTask;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class AttributeActivity extends BaseActivity {
	private static final String TAG = "AttributeActivity";
	private CompanyItem[] companyArr;
	private ListView lv_company;
	private CompanyListAdapter cp_adapter;
	// private Address mAddress;
	// private ArrayList<Integer> selected_attributes;
	private MenuItem refresh_icon;
	private boolean refreshing;
	private boolean shouldBookRightAfter;
    private TextView tv_company404_text;
    private View line;
	private Context _context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attribute);
		_context = this;
		// mAddress = getIntent().getParcelableExtra(MBDefinition.ADDRESS);


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
		Typeface rionaSansBold = FontCache.getFont(this, "fonts/RionaSansBold.otf");
        Typeface rionaSansMedium = FontCache.getFont(this, "fonts/RionaSansMedium.otf");
		TextView textView1 = (TextView) findViewById(R.id.textView1);
		TextView textView2 = (TextView) findViewById(R.id.textView2);
        tv_company404_text = (TextView) findViewById(R.id.tv_company404_text);
        line = findViewById(R.id.line);

		textView1.setTypeface(rionaSansBold);
		textView2.setTypeface(rionaSansBold);
        tv_company404_text.setTypeface(rionaSansMedium);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		refreshing = true;
		boolean isFromBooking = false;
		new GetCompanyListTask(this, Utils.mPickupAddress, isFromBooking).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        /*
        int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
        String usedMegsString = String.format(" - Memory Used: %d MB", usedMegs);
        Logger.e(TAG, usedMegsString);
        */
	}

    @Override
    protected void onDestroy(){
        Logger.d(TAG, "onDestroy");
        super.onDestroy();
        //TL-369
        if(findViewById(R.id.RootView) != null) {
            unbindDrawables(findViewById(R.id.RootView));
            System.gc();
        }

    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
            clearSelectedCompanyIfNotAvailable();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		Logger.d(TAG, "onCreateOptionsMenu");
//		getMenuInflater().inflate(R.menu.attribute, menu);
//		refresh_icon = menu.findItem(R.id.action_refresh);
//		if (refreshing) {
//			boolean isFromBooking = false;
//			new GetCompanyListTask(this, Utils.mPickupAddress, isFromBooking).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//			startUpdateAnimation(refresh_icon);
//		} else {
//			refresh_icon.setVisible(false);
//		}
//		return true;
//	}

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
            //TL-359
            clearSelectedCompanyIfNotAvailable();

			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    private void clearSelectedCompanyIfNotAvailable(){
        boolean companyAvailable = false;
        if(Utils.mSelectedCompany==null)
            return;

        if(cp_adapter==null) {
            Utils.mSelectedCompany = null;
            return;
        }

        for(int i = 0; i< cp_adapter.getCount(); i++){
            if(cp_adapter.getCompanyItem(i).destID.equals(Utils.mSelectedCompany.destID))
                companyAvailable = true;
        }

        if(!companyAvailable)
            Utils.mSelectedCompany=null;
    }

	// call from AttributeItemAdapter
	public void filterCompany(ArrayList<Integer> positive_attribute_IDs) {
		// selected_attributes = positive_attribute_IDs;
		Utils.selected_attribute = positive_attribute_IDs;
		if (positive_attribute_IDs.size() == 0 && companyArr!=null) {
			cp_adapter = new CompanyListAdapter(this, companyArr, shouldBookRightAfter);
			lv_company.setAdapter(cp_adapter);
            if(cp_adapter.getCount()==0){
                lv_company.setVisibility(View.GONE);
                tv_company404_text.setVisibility(View.VISIBLE);
                line.setVisibility(View.GONE);
                if(Utils.mPickupAddress!=null) {
                    String string = "No Fleets available ";
                    string += Utils.mPickupAddress.getLocality() != null ? ("in " + Utils.mPickupAddress.getLocality()) : "";
                    tv_company404_text.setText(string );
                }
                else
                    tv_company404_text.setText("Please select a pickup address first");
            }
            else{
                lv_company.setVisibility(View.VISIBLE);
                tv_company404_text.setVisibility(View.GONE);
                line.setVisibility(View.VISIBLE);
            }
		} else {
			if (companyArr!=null && companyArr.length > 0) {
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
                if(cp_adapter.getCount()==0){
                    lv_company.setVisibility(View.GONE);
                    tv_company404_text.setVisibility(View.VISIBLE);
                    line.setVisibility(View.GONE);
                }
                else{
                    lv_company.setVisibility(View.VISIBLE);
                    tv_company404_text.setVisibility(View.GONE);
                    line.setVisibility(View.VISIBLE);
                }
			}
		}
	}

	// called from getCompanyListResponse, filter attribute grid here to make sure app not showing unused attributes
	public void loadCompanyList(CompanyItem[] tempCompList) {
		//stopUpdateAnimation();
		companyArr = tempCompList;

        //TL-376 Only show applicable attributes on OPTIONS page.
        setUpAttributeGridBaseOnCompanyAttribut(tempCompList);

        if(Utils.selected_attribute!=null) {
            filterCompany(Utils.selected_attribute);
        }
        else {
            filterCompany(new ArrayList<Integer>());
        }
	}

    private void setUpAttributeGridBaseOnCompanyAttribut(CompanyItem[] tempCompList){
        DaoManager daoManager = DaoManager.getInstance(this);
        DBAttributeDao attributeDao = daoManager.getDBAttributeDao(DaoManager.TYPE_READ);
        List<DBAttribute> attrList = attributeDao.queryBuilder().list();

        String companyAttrList = "";
        for(int i = 0 ; i< tempCompList.length; i++){
            companyAttrList += tempCompList[i].attributes + " ";
        }

        List<DBAttribute> temp = new ArrayList<DBAttribute>();
        for(int i = attrList.size()-1; i >= 0; i--){
            if(!companyAttrList.contains(attrList.get(i).getAttributeId())) {
                temp.add(attrList.get(i));
            }
        }

        for(int i = 0; i < temp.size(); i++){
            attrList.remove(temp.get(i));
        }

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new AttributeItemAdapter(this, attrList));
    }

}
