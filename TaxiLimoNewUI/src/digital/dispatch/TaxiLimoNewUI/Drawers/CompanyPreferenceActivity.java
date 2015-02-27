package digital.dispatch.TaxiLimoNewUI.Drawers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import java.util.ArrayList;
import java.util.List;

import digital.dispatch.TaxiLimoNewUI.Adapters.AttributeItemAdapter;
import digital.dispatch.TaxiLimoNewUI.Adapters.CompanyListAdapter;
import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.DBAttribute;
import digital.dispatch.TaxiLimoNewUI.DBAttributeDao;
import digital.dispatch.TaxiLimoNewUI.DBPreference;
import digital.dispatch.TaxiLimoNewUI.DBPreferenceDao;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.GetCompanyListTask;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;


public class CompanyPreferenceActivity extends BaseActivity {

    private static final String TAG = "CompanyPreferenceActivity";
    private CompanyItem[] companyArr;
    private ListView lv_company;
    private CompanyListAdapter cp_adapter;
    // private Address mAddress;
    // private ArrayList<Integer> selected_attributes;

    private TextView tv_company404_text;
    private View line;
    private Context _context;
    private DBPreferenceDao preferenceDao;

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    private String city;
    private String province;
    private String country;
    private String preferedAttrList;
    private ArrayList<Integer> selectedAttrList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_preference);
        _context = this;

        city = getIntent().getExtras().getString(MBDefinition.EXTRA_CITY);
        province = getIntent().getExtras().getString(MBDefinition.EXTRA_PROVINCE);
        country = getIntent().getExtras().getString(MBDefinition.EXTRA_COUNTRY);

        setToolBar();

        lv_company = (ListView) findViewById(R.id.lv_company);
        lv_company.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                builder.setMessage(_context.getString(R.string.save_preference_confirmation) + " " + city + "?");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete previous preferred company
                        deletePreferedByCity();
                        //add preferred company with empty attrList
                        addPreference(cp_adapter.getCompanyItem(position));
                        finish();
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
        });

        Typeface OpenSansBold = FontCache.getFont(this, "fonts/OpenSansBold.ttf");
        Typeface OpenSansRegular = FontCache.getFont(this, "fonts/OpenSansRegular.ttf");
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        tv_company404_text = (TextView) findViewById(R.id.tv_company404_text);
        line = findViewById(R.id.line);

        textView1.setTypeface(OpenSansBold);
        textView2.setTypeface(OpenSansBold);
        tv_company404_text.setTypeface(OpenSansRegular);
    }

    @Override
    protected void onResume() {
        super.onResume();
//		refreshing = true;
        boolean isFromBooking = false;
        boolean isFromPrefernece = true;
        new GetCompanyListTask(this, null, isFromBooking, isFromPrefernece, city, province, country).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (canOpenTutorial(CompanyPreferenceActivity.class.getSimpleName())) {
            showToolTip(getString(R.string.tooltip_preference), CompanyPreferenceActivity.class.getSimpleName());
        }

        DaoManager daoManager = DaoManager.getInstance(this);
        preferenceDao = daoManager.getDBPreferenceDao(DaoManager.TYPE_WRITE);

        loadSavedAttrList();

        companyArr = new CompanyItem[0];
        boolean isFromPreference = true;
        cp_adapter = new CompanyListAdapter(this, companyArr, isFromPreference);
        lv_company.setAdapter(cp_adapter);

        /*
        int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
        String usedMegsString = String.format(" - Memory Used: %d MB", usedMegs);
        Logger.e(TAG, usedMegsString);
        */
    }

    private void loadSavedAttrList(){
        List<DBPreference> preferedCompanyInThisCity = preferenceDao.queryBuilder().where(DBPreferenceDao.Properties.City.eq(city.toUpperCase()),
                DBPreferenceDao.Properties.Country.eq(country),
                DBPreferenceDao.Properties.State.eq(province)).list();

        if(preferedCompanyInThisCity.size()==0){
            preferedAttrList = "";
        }
        else
        {
            preferedAttrList = preferedCompanyInThisCity.get(0).getAttributeList();
        }
    }

    private void deletePreferedByCity(){
        preferenceDao.queryBuilder().where(DBPreferenceDao.Properties.City.eq(city.toUpperCase()),
                DBPreferenceDao.Properties.Country.eq(country),
                DBPreferenceDao.Properties.State.eq(province)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    private void addPreference(CompanyItem item){
        Address address = Utils.mPickupAddress;

        //over write company for this trip if changing preference for the same city
        if(address!=null && province.equalsIgnoreCase(LocationUtils.states.get(address.getAdminArea()))
                && city.equalsIgnoreCase(address.getLocality())
                && country.equalsIgnoreCase(address.getCountryCode())){
            Utils.mSelectedCompany = item;
            Utils.selected_attribute = selectedAttrList;
        }

        DBPreference newPreference = new DBPreference();
        newPreference.setAttributeList(Utils.setupAttributeIdList(selectedAttrList));
        newPreference.setCity(city.toUpperCase());
        newPreference.setCountry(country.toUpperCase());
        newPreference.setCompanyName(item.name);
        newPreference.setDestId(item.destID);
        newPreference.setImg(item.logo);
        newPreference.setDescription(item.description);
        //Logger.e(TAG,"saving province: " + province + ", city: " + city + ", country: " + country);
        newPreference.setState(province.toUpperCase());
        preferenceDao.insert(newPreference);
    }

    public boolean printPreferCompany(){
        List<DBPreference> preferenceList = preferenceDao.queryBuilder().list();
        for(int i=0; i<preferenceList.size(); i++){
            Logger.d(TAG, "prefered: " + preferenceList.get(i).getCompanyName());
        }
        return false;
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        Typeface face = FontCache.getFont(_context, "fonts/Exo2-Light.ttf");
        TextView yourTextView = Utils.getToolbarTitleView(this, toolbar);
        yourTextView.setTypeface(face);
        getSupportActionBar().setTitle(province + " | " + city);
    }

    @Override
    protected void onDestroy() {
        Logger.d(TAG, "onDestroy");
        super.onDestroy();
        //TL-369
        if (findViewById(R.id.RootView) != null) {
            unbindDrawables(findViewById(R.id.RootView));
            System.gc();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //clearSelectedCompanyIfNotAvailable();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // override android back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //TL-359
            //clearSelectedCompanyIfNotAvailable();

            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
//
//    private void clearSelectedCompanyIfNotAvailable() {
//        boolean companyAvailable = false;
//        if (Utils.mSelectedCompany == null)
//            return;
//
//        if (cp_adapter == null) {
//            Utils.mSelectedCompany = null;
//            return;
//        }
//
//        for (int i = 0; i < cp_adapter.getCount(); i++) {
//            if (cp_adapter.getCompanyItem(i).destID.equals(Utils.mSelectedCompany.destID))
//                companyAvailable = true;
//        }
//
//        if (!companyAvailable)
//            Utils.mSelectedCompany = null;
//    }

    // call from AttributeItemAdapter
    public void filterCompany(ArrayList<Integer> positive_attribute_IDs) {
        selectedAttrList = positive_attribute_IDs;
        if (positive_attribute_IDs.size() == 0 && companyArr != null) {
            //cp_adapter = new CompanyListAdapter(this, companyArr, shouldBookRightAfter);
            //lv_company.setAdapter(cp_adapter);
            cp_adapter.items = companyArr;
            cp_adapter.notifyDataSetChanged();

            if (cp_adapter.getCount() == 0) {
                lv_company.setVisibility(View.GONE);
                tv_company404_text.setVisibility(View.VISIBLE);
                line.setVisibility(View.GONE);
                if (city != null) {
                    String string = "No Fleets available ";
                    string += city != null ? ("in " + city) : "";
                    tv_company404_text.setText(string);
                } else
                    tv_company404_text.setText("Please select a pickup address first");
            } else {
                lv_company.setVisibility(View.VISIBLE);
                tv_company404_text.setVisibility(View.GONE);
                line.setVisibility(View.VISIBLE);
            }
        } else {
            if (companyArr != null && companyArr.length > 0) {
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

                cp_adapter.items = compArr;
                cp_adapter.notifyDataSetChanged();

                if (cp_adapter.getCount() == 0) {
                    lv_company.setVisibility(View.GONE);
                    tv_company404_text.setVisibility(View.VISIBLE);
                    line.setVisibility(View.GONE);
                } else {
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

        selectedAttrList = attrStringToList(preferedAttrList);

        //TL-376 Only show applicable attributes on OPTIONS page.
        setUpAttributeGridBaseOnCompanyAttribut(tempCompList);

        if (selectedAttrList != null) {
            filterCompany(selectedAttrList);
        } else {
            filterCompany(new ArrayList<Integer>());
        }
    }

    private ArrayList<Integer> attrStringToList(String list){
        String[] temp = list.split(",");
        ArrayList<Integer> result = new ArrayList<>();
        for(int i = 0; i < temp.length; i++){
            if(temp[i].length()>0)
            result.add(Integer.valueOf(temp[i]));
        }
        return result;
    }

    //only shows attributes exist in the cities' companies
    private void setUpAttributeGridBaseOnCompanyAttribut(CompanyItem[] tempCompList) {
        DaoManager daoManager = DaoManager.getInstance(this);
        DBAttributeDao attributeDao = daoManager.getDBAttributeDao(DaoManager.TYPE_READ);
        List<DBAttribute> attrList = attributeDao.queryBuilder().list();

        String companyAttrList = "";
        for (int i = 0; i < tempCompList.length; i++) {
            companyAttrList += tempCompList[i].attributes + " ";
        }

        List<DBAttribute> temp = new ArrayList<DBAttribute>();
        for (int i = attrList.size() - 1; i >= 0; i--) {
            if (!companyAttrList.contains(attrList.get(i).getAttributeId())) {
                temp.add(attrList.get(i));
            }
        }

        for (int i = 0; i < temp.size(); i++) {
            attrList.remove(temp.get(i));
        }

        GridView gridview = (GridView) findViewById(R.id.gridview);
        boolean isFromPreference = true;
        gridview.setAdapter(new AttributeItemAdapter(this, attrList, isFromPreference, selectedAttrList));
    }

}
