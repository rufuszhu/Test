package digital.dispatch.TaxiLimoNewUI.Book;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.Toast;

import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import digital.dispatch.TaxiLimoNewUI.Adapters.AttributeItemAdapter;
import digital.dispatch.TaxiLimoNewUI.Adapters.CompanyListAdapter;
import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.DBAttribute;
import digital.dispatch.TaxiLimoNewUI.DBAttributeDao;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
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
import digital.dispatch.TaxiLimoNewUI.Widget.SwipableListItem;

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
    private DBPreferenceDao preferenceDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribute);
        _context = this;

        setToolBar();
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

        lv_company.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (null != cp_adapter) {
                    //if it is already preferred company, delete it
                    if (view.findViewById(R.id.icon_preferred).getVisibility() == View.VISIBLE) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                        builder.setMessage(_context.getString(R.string.delete_preference_confirmation) + " " + Utils.mPickupAddress.getLocality() + "?");
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePreferedByCity();
                                cp_adapter.notifyDataSetChanged();
                            }
                        });

                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();

                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                        builder.setMessage(_context.getString(R.string.save_preference_confirmation) + " " + Utils.mPickupAddress.getLocality() + "?");
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete previous preferred company
                                deletePreferedByCity();
                                //add preferred company with empty attrList
                                addPreference(cp_adapter.getItem(position));
                                cp_adapter.notifyDataSetChanged();
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
                    return true;
                }
                return false;
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

    private void deletePreferedByCity() {
//        Logger.e(TAG, "before delete");
//        printPreferCompany();
        Address address = Utils.mPickupAddress;
        QueryBuilder qb = preferenceDao.queryBuilder();

        qb.where(DBPreferenceDao.Properties.City.eq(address.getLocality().toUpperCase()),
                DBPreferenceDao.Properties.Country.eq(address.getCountryCode().toUpperCase()),
                DBPreferenceDao.Properties.State.eq(LocationUtils.states.get(address.getAdminArea())));

        qb.buildDelete().executeDeleteWithoutDetachingEntities();

//        Logger.e(TAG, "after delete");
//        printPreferCompany();
    }

    private void addPreference(final CompanyItem item) {

        Address address = Utils.mPickupAddress;
        DBPreference newPreference = new DBPreference();
        newPreference.setAttributeList(Utils.setupAttributeIdList(Utils.selected_attribute));
        newPreference.setCity(address.getLocality().toUpperCase());
        newPreference.setCountry(address.getCountryCode().toUpperCase());
        newPreference.setCompanyName(item.name);
        newPreference.setDestId(item.destID);
        newPreference.setImg(item.logo);
        newPreference.setDescription(item.description);
        newPreference.setState(LocationUtils.states.get(address.getAdminArea()));
        preferenceDao.insert(newPreference);


    }

    public boolean printPreferCompany() {
        DaoManager daoManager = DaoManager.getInstance(this);
        preferenceDao = daoManager.getDBPreferenceDao(DaoManager.TYPE_WRITE);
        List<DBPreference> preferenceList = preferenceDao.queryBuilder().list();
        for (int i = 0; i < preferenceList.size(); i++) {
            Logger.d(TAG, "prefered: " + preferenceList.get(i).getCompanyName() + ", country: " + preferenceList.get(i).getCountry() + ", city: " + preferenceList.get(i).getCity() + ", state: " + preferenceList.get(i).getState());
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
    }

    @Override
    protected void onResume() {
        super.onResume();
//		refreshing = true;
        boolean isFromBooking = false;
        boolean isPreference = false;
        new GetCompanyListTask(this, Utils.mPickupAddress, isFromBooking, isPreference, "", "", "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (canOpenTutorial(AttributeActivity.class.getSimpleName())) {
            showToolTip(getString(R.string.tooltip_attribute), AttributeActivity.class.getSimpleName());
            //stopShowingToolTip(AttributeActivity.class.getSimpleName());
        }

        DaoManager daoManager = DaoManager.getInstance(this);
        preferenceDao = daoManager.getDBPreferenceDao(DaoManager.TYPE_WRITE);
        companyArr = new CompanyItem[0];
        boolean isFromPreference = false;
        cp_adapter = new CompanyListAdapter(this, companyArr, isFromPreference);
        lv_company.setAdapter(cp_adapter);

        /*
        int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
        String usedMegsString = String.format(" - Memory Used: %d MB", usedMegs);
        Logger.e(TAG, usedMegsString);
        */
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
            clearSelectedCompanyIfNotAvailable();
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
            clearSelectedCompanyIfNotAvailable();

            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void clearSelectedCompanyIfNotAvailable() {
        boolean companyAvailable = false;
        if (Utils.mSelectedCompany == null)
            return;

        if (cp_adapter == null) {
            Utils.mSelectedCompany = null;
            return;
        }

        for (int i = 0; i < cp_adapter.getCount(); i++) {
            if (cp_adapter.getCompanyItem(i).destID.equals(Utils.mSelectedCompany.destID))
                companyAvailable = true;
        }

        if (!companyAvailable)
            Utils.mSelectedCompany = null;
    }

    // call from AttributeItemAdapter
    public void filterCompany(ArrayList<Integer> positive_attribute_IDs) {
        // selected_attributes = positive_attribute_IDs;
        Utils.selected_attribute = positive_attribute_IDs;
        if (positive_attribute_IDs.size() == 0 && companyArr != null) {
            //cp_adapter = new CompanyListAdapter(this, companyArr, shouldBookRightAfter);
            //lv_company.setAdapter(cp_adapter);
            cp_adapter.items = companyArr;
            cp_adapter.notifyDataSetChanged();

            if (cp_adapter.getCount() == 0) {
                lv_company.setVisibility(View.GONE);
                tv_company404_text.setVisibility(View.VISIBLE);
                line.setVisibility(View.GONE);
                if (Utils.mPickupAddress != null) {
                    String string = "No Fleets available ";
                    string += Utils.mPickupAddress.getLocality() != null ? ("in " + Utils.mPickupAddress.getLocality()) : "";
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

//                cp_adapter = new CompanyListAdapter(this, compArr, shouldBookRightAfter);
//                lv_company.setAdapter(cp_adapter);
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

        //TL-376 Only show applicable attributes on OPTIONS page.
        setUpAttributeGridBaseOnCompanyAttribut(tempCompList);

        if (Utils.selected_attribute != null) {
            filterCompany(Utils.selected_attribute);
        } else {
            filterCompany(new ArrayList<Integer>());
        }
    }

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
        boolean isFromPreference = false;
        gridview.setAdapter(new AttributeItemAdapter(this, attrList, isFromPreference, null));
    }

}
