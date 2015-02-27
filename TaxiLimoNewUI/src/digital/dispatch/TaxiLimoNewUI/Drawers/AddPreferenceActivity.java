package digital.dispatch.TaxiLimoNewUI.Drawers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;
import com.digital.dispatch.TaxiLimoSoap.responses.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.DBPreference;
import digital.dispatch.TaxiLimoNewUI.DBPreferenceDao;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.GetServiceListTask;
import digital.dispatch.TaxiLimoNewUI.Utils.AppController;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import digital.dispatch.TaxiLimoNewUI.Widget.MySpinner;

public class AddPreferenceActivity extends BaseActivity {

	private static final String TAG = "AddPreferenceActivity";
	private ArrayList<Node> countryList;
	private ArrayList<Node> stateList;
	private ArrayList<Node> regionList;
	private ArrayList<Node> companyList;
	private MySpinner stateSpinner;
	private MySpinner countrySpinner;
	private MySpinner regionSpinner;

    private OnItemSelectedListener countryListener;
    private OnItemSelectedListener regionListener;
    private OnItemSelectedListener stateListener;

    private NetworkImageView icon;
    private TextView name;
    private TextView description, right_angle;
    private LinearLayout ll_attr;
    private FrameLayout icon_preferred;
    private RelativeLayout company_list_item;
    private TextView tv_choose_company;

    private Typeface OpenSansSemibold;
    private Typeface OpenSansRegular;

	private MyAdapter countryAdapter;
	private MyAdapter stateAdapter;
	private MyAdapter regionAdapter;

    private RelativeLayout rl_choose_prefer;

    private Context _context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        _context = this;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Typeface face = FontCache.getFont(_context, "fonts/Exo2-Light.ttf");
        TextView yourTextView = Utils.getToolbarTitleView(this, toolbar);
        yourTextView.setTypeface(face);

		// ArrayAdapter<Node> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, objects);

        OpenSansSemibold = FontCache.getFont(this, "fonts/OpenSansSemibold.ttf");
        OpenSansRegular = FontCache.getFont(this, "fonts/OpenSansRegular.ttf");
		findView();
		initAndBindEvent();
	}
    @Override
    public void onResume(){
        super.onResume();
        Logger.e(TAG, "onResume");
        new GetServiceListTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
	
	private void initAndBindEvent() {
		countryListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (countryAdapter.objects.size() > 0) {
					String countryName = countryAdapter.objects.get(position).getName();
					stateAdapter.clear();

					for (int i = 0; i < stateList.size(); i++) {
						if (stateList.get(i).getParent().equals(countryName)) {
							Logger.d("adding: " + stateList.get(i).getName());
							stateAdapter.objects.add(stateList.get(i));
						}
					}
					stateAdapter.notifyDataSetChanged();
                    stateSpinner.setSelection(0);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};

		stateListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				//if (stateAdapter.objects.size() > 0) {
					String stateName = stateAdapter.objects.get(position).getName();
					regionAdapter.clear();

					for (int i = 0; i < regionList.size(); i++) {
						if (regionList.get(i).getParent().equals(stateName))
							regionAdapter.objects.add(regionList.get(i));
					}
					regionAdapter.notifyDataSetChanged();
                    regionSpinner.setSelection(0);
				//}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};

		regionListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchPreference();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};


        rl_choose_prefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, CompanyPreferenceActivity.class);
                intent.putExtra(MBDefinition.EXTRA_CITY, regionAdapter.objects.get(regionSpinner.getSelectedItemPosition()).getName().toUpperCase());
                intent.putExtra(MBDefinition.EXTRA_COUNTRY, countryAdapter.objects.get(countrySpinner.getSelectedItemPosition()).getName().toUpperCase());
                intent.putExtra(MBDefinition.EXTRA_PROVINCE, stateAdapter.objects.get(stateSpinner.getSelectedItemPosition()).getName().toUpperCase());
                startActivityForAnim(intent);
            }
        });


        company_list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, CompanyPreferenceActivity.class);
                intent.putExtra(MBDefinition.EXTRA_CITY, regionAdapter.objects.get(regionSpinner.getSelectedItemPosition()).getName().toUpperCase());
                intent.putExtra(MBDefinition.EXTRA_COUNTRY, countryAdapter.objects.get(countrySpinner.getSelectedItemPosition()).getName().toUpperCase());
                intent.putExtra(MBDefinition.EXTRA_PROVINCE, stateAdapter.objects.get(stateSpinner.getSelectedItemPosition()).getName().toUpperCase());
                startActivityForAnim(intent);
            }
        });

        stateSpinner.setOnItemSelectedEvenIfUnchangedListener(stateListener);
        countrySpinner.setOnItemSelectedEvenIfUnchangedListener(countryListener);
        regionSpinner.setOnItemSelectedEvenIfUnchangedListener(regionListener);
	}

	private void findView() {
		stateSpinner = (MySpinner) findViewById(R.id.state_spinner);
		countrySpinner = (MySpinner) findViewById(R.id.country_spinner);
		regionSpinner = (MySpinner) findViewById(R.id.region_spinner);

        right_angle = (TextView) findViewById(R.id.right_angle);
        rl_choose_prefer = (RelativeLayout) findViewById(R.id.rl_choose_prefer);
        tv_choose_company = (TextView) findViewById(R.id.tv_choose_company);
        icon = (NetworkImageView) findViewById(R.id.company_icon);
        name = (TextView) findViewById(R.id.tv_name);
        description = (TextView) findViewById(R.id.tv_description);
        ll_attr = (LinearLayout) findViewById(R.id.ll_attr);
        icon_preferred = (FrameLayout) findViewById(R.id.icon_preferred);
        company_list_item = (RelativeLayout) findViewById(R.id.company_list_item);

        tv_choose_company.setTypeface(OpenSansRegular);
        Typeface icon_pack = FontCache.getFont(this, "fonts/icon_pack.ttf");
        right_angle.setTypeface(icon_pack);
        right_angle.setText(MBDefinition.ICON_RIGHT_TRIANGLE);
	}


	// response of GetServiceListTask
	public void getData(ArrayList<Node> countryList, ArrayList<Node> stateList, ArrayList<Node> regionList, ArrayList<Node> companyList) {

		this.companyList = companyList;
		this.countryList = countryList;
		this.stateList = stateList;
		this.regionList = regionList;

//        Logger.e(TAG, "companyList");
//		printList(companyList);
//        Logger.e(TAG, "countryList");
//		printList(countryList);
//        Logger.e(TAG, "stateList");
//		printList(stateList);
//        Logger.e(TAG, "regionList");
//		printList(regionList);

        countryAdapter = new MyAdapter(this, R.layout.spinner_item, countryList);
        stateAdapter = new MyAdapter(this, R.layout.spinner_item, new ArrayList<Node>());
        regionAdapter = new MyAdapter(this, R.layout.spinner_item, new ArrayList<Node>());

		countrySpinner.setAdapter(countryAdapter);
		stateSpinner.setAdapter(stateAdapter);
		regionSpinner.setAdapter(regionAdapter);

        if(countryList.size()>0)
            countrySpinner.setSelection(0);
        //stateSpinner.setOnItemSelectedEvenIfUnchangedListener(stateListener);
	}

    private void searchPreference(){
        if(null!= countryAdapter && null != regionAdapter&& null!= stateAdapter && null != regionSpinner.getSelectedItem()) {
//            Logger.e(TAG, "region getSelectedItem().toString(): " + ((Node) regionSpinner.getSelectedItem()).getName());
//            Logger.e(TAG, "country getSelectedItem().toString(): " + ((Node) countrySpinner.getSelectedItem()).getName());
//            Logger.e(TAG, "sate getSelectedItem().toString(): " + ((Node) stateSpinner.getSelectedItem()).getName());
            DaoManager daoManager = DaoManager.getInstance(this);
            DBPreferenceDao preferenceDao = daoManager.getDBPreferenceDao(DaoManager.TYPE_READ);
            List<DBPreference> preferedCompanyList = preferenceDao.queryBuilder().where(DBPreferenceDao.Properties.City.eq(((Node) regionSpinner.getSelectedItem()).getName().toUpperCase()),
                    DBPreferenceDao.Properties.Country.eq(((Node) countrySpinner.getSelectedItem()).getName().toUpperCase()),
                    DBPreferenceDao.Properties.State.eq(((Node) stateSpinner.getSelectedItem()).getName().toUpperCase())).list();

            if (preferedCompanyList.size() > 0) {
                //Logger.e(TAG, "Found company: " + preferedCompanyList.get(0).getCompanyName());
                setUpCompanyView(preferedCompanyList.get(0));

            } else {
                company_list_item.setVisibility(View.GONE);
            }
        }
        else {
            company_list_item.setVisibility(View.GONE);
        }
    }

	// Creating an Adapter Class
	public class MyAdapter extends ArrayAdapter<Node> {
		private ArrayList<Node> objects;

		public MyAdapter(Context context, int textViewResourceId, ArrayList<Node> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
		}

		public View getCustomView(int position, View convertView, ViewGroup parent) {

			// Inflating the layout for the custom Spinner
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.spinner_item, parent, false);

			// Declaring and Typecasting the textview in the inflated layout
			TextView tvLanguage = (TextView) layout.findViewById(R.id.tv);

			//it means this is country adapter
            if(objects.get(position).getParent()==null){
                if(LocationUtils.countrys.containsKey(objects.get(position).getName()))
                    tvLanguage.setText(LocationUtils.countrys.get(objects.get(position).getName()));
                else
                    tvLanguage.setText(objects.get(position).getName());
            }
            else{
                tvLanguage.setText(objects.get(position).getName());
            }

			// Setting the color of the text
			tvLanguage.setTypeface(OpenSansRegular);


			return layout;
		}

		// It gets a View that displays in the drop down popup the data at the specified position
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		// It gets a View that displays the data at the specified position
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}
	}
	
	private void printList(ArrayList<Node> list){
		for(int i = 0; i< list.size();i++){
			Logger.i(TAG, "name: " + list.get(i).getName() + " parent: " + list.get(i).getParent());
		}
	}

    private void setUpCompanyView(DBPreference preference){
        company_list_item.setVisibility(View.VISIBLE);
        company_list_item.setBackground(getResources().getDrawable(R.drawable.shape_textview_border));
        name.setTypeface(OpenSansSemibold);
        //viewHolder.tv_round_btn.setTypeface(exoBold);
        description.setTypeface(OpenSansRegular);

		/*
		if (bookRightAfter)
			//viewHolder.tv_round_btn.setText(context.getString(R.string.book));
		else
			//viewHolder.tv_round_btn.setText(context.getString(R.string.select));
        */

        name.setText(preference.getCompanyName());
        String prefixURL = getResources().getString(R.string.url);
        prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
        icon.setDefaultImageResId(R.drawable.launcher);
        icon.setImageUrl(prefixURL + preference.getImg(), AppController.getInstance().getImageLoader());
        description.setText(preference.getDescription());

        String[] attrs = preference.getAttributeList().split(",");
        int marginRight = 10;
        Utils.showOption(ll_attr, attrs, this, marginRight);
    }



}
