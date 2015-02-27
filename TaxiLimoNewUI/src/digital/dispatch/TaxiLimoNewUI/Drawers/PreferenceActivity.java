package digital.dispatch.TaxiLimoNewUI.Drawers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.Book.ModifyAddressActivity;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBPreference;
import digital.dispatch.TaxiLimoNewUI.DBPreferenceDao;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.AppController;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import digital.dispatch.TaxiLimoNewUI.Widget.SwipableListItem;

public class PreferenceActivity extends BaseActivity {

	private static final String TAG = "PreferenceActivity";
    private List<DBPreference> dbPreferences;
    private Context _context;
    private PreferenceAdapter adapter;
    private ListView mListView;
    private boolean mSwiping = false;
    private boolean mItemPressed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Typeface face = FontCache.getFont(this, "fonts/Exo2-Light.ttf");
        TextView yourTextView = Utils.getToolbarTitleView(this, toolbar);
        yourTextView.setTypeface(face);
        _context = this;
        mListView = (ListView)findViewById(R.id.lv_preference_list);

//        DaoManager daoManager = DaoManager.getInstance(_context);
//        DBPreferenceDao preferenceDao = daoManager.getDBPreferenceDao(DaoManager.TYPE_WRITE);
//        DBPreference newPreference = new DBPreference();
//        newPreference.setAttributeList("");
//        newPreference.setCity("Calgary");
//        newPreference.setCountry("CA");
//        newPreference.setCompanyName("DEF Taxi");
//        newPreference.setDestId("1");
//        newPreference.setImg("");
//        newPreference.setState("Alberta");
//        preferenceDao.insert(newPreference);
//
//        DBPreference new2Preference = new DBPreference();
//        new2Preference.setAttributeList("");
//        new2Preference.setCity("Beijing");
//        new2Preference.setCountry("China");
//        new2Preference.setCompanyName("Beijing Taxi");
//        new2Preference.setDestId("12234");
//        new2Preference.setImg("");
//        new2Preference.setState("Beijing");
//        preferenceDao.insert(new2Preference);
	}

    @Override
    protected void onResume(){
        super.onResume();
        getPreferenceList();

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preference, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_add) {
			Intent intent = new Intent(this, AddPreferenceActivity.class);
			startActivityForAnim(intent);
			return true;
		}
		if(id==android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    public void getPreferenceList() {

        AsyncTask<Void, Void, List<DBPreference>> asyncTask = new AsyncTask<Void, Void, List<DBPreference>>() {
            @Override
            protected List<DBPreference> doInBackground(Void... voids) {
                DaoManager daoManager = DaoManager.getInstance(_context);
                DBPreferenceDao preferenceDao = daoManager.getDBPreferenceDao(DaoManager.TYPE_READ);
                dbPreferences = preferenceDao.queryBuilder().orderAsc(DBPreferenceDao.Properties.Country).list();
                return dbPreferences;
            }

            @Override
            protected void onPostExecute(List<DBPreference> dbPreferences) {
                super.onPostExecute(dbPreferences);
                adapter = new PreferenceAdapter(_context,dbPreferences);
                mListView.setAdapter(adapter);
            }
        };
        asyncTask.execute();
    }




    private class PreferenceAdapter extends ArrayAdapter<DBPreference> {

        private final Context context;
        private List<DBPreference> values;
        private Typeface fontFamily;
        private Typeface OpenSansSemibold;
        private Typeface OpenSansRegular;

        private final int TYPE_HAS_HEADER = 0;
        private final int TYPE_NO_HEADER = 1;


        public List<DBPreference> getValues() {
            return values;
        }

        public void addValues(DBPreference value) {
            this.values.add(value);
        }

        public PreferenceAdapter(Context context, List<DBPreference> values) {
            super(context, R.layout.preference_list_item, values);
            this.context = context;
            this.values = values;
            fontFamily = FontCache.getFont(_context, "fonts/icon_pack.ttf");
            OpenSansSemibold = FontCache.getFont(_context, "fonts/OpenSansSemibold.ttf");
            OpenSansRegular = FontCache.getFont(_context, "fonts/OpenSansRegular.ttf");
        }

        @Override
        public int getCount() {
            int count = 0;
            if (values != null) {
                count = values.size();
            }
            return count;
        }

        @Override
        public DBPreference getItem(int i) {
            return values.get(i);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }


        @Override
        public int getItemViewType(int position) {
            if(position==0)
                return TYPE_HAS_HEADER;
            else if(values.get(position).getCountry().equals(values.get(position - 1).getCountry()))
                return TYPE_NO_HEADER;
            else
                return TYPE_HAS_HEADER;
        }

        public class ViewHolder {
            public TextView tv_province;
            public TextView tv_city;
            public TextView delete_btn;
            public TextView tv_header;
            public TextView tv_company_name;
            public RelativeLayout viewHeader;
            public ViewGroup swipeContactView;
            public TextView green_circle_delete;
            public NetworkImageView icon;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            int type = getItemViewType(position);
            // reuse views
            if (convertView == null) {

                viewHolder = new ViewHolder();

                if (type == TYPE_HAS_HEADER) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.preference_list_item_with_header, null);
                    viewHolder.tv_header = (TextView) convertView.findViewById(R.id.tv_header);
                }
                else{
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.preference_list_item, null);
                }

                viewHolder.icon = (NetworkImageView) convertView.findViewById(R.id.company_icon);
                viewHolder.tv_province = (TextView) convertView.findViewById(R.id.tv_province);
                viewHolder.tv_city = (TextView) convertView.findViewById(R.id.tv_city);
                viewHolder.delete_btn = (TextView) convertView.findViewById(R.id.delete_btn);
                viewHolder.tv_company_name = (TextView) convertView.findViewById(R.id.tv_company_name);
                viewHolder.swipeContactView = (ViewGroup) convertView.findViewById(R.id.swipeContactView);
                viewHolder.viewHeader = (RelativeLayout) convertView.findViewById(R.id.viewHeader);

                viewHolder.green_circle_delete = (TextView) convertView.findViewById(R.id.green_circle_delete);
                convertView.setTag(viewHolder);
            }

            // fill data

            final ViewHolder holder = (ViewHolder) convertView.getTag();

            if(type==TYPE_HAS_HEADER){
                String country = values.get(position).getCountry();
                if(LocationUtils.countrys.containsKey(country)){
                    country = LocationUtils.countrys.get(country);
                }
                holder.tv_header.setTypeface(OpenSansSemibold);
                holder.tv_header.setText(country);
            }

            String prefixURL = context.getResources().getString(R.string.url);
            prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
            holder.icon.setDefaultImageResId(R.drawable.launcher);
            holder.icon.setImageUrl(prefixURL + values.get(position).getImg(), AppController.getInstance().getImageLoader());

            holder.tv_province.setTypeface(OpenSansRegular);
            holder.tv_province.setText(values.get(position).getState());

            holder.tv_city.setTypeface(OpenSansRegular);
            holder.tv_city.setText(values.get(position).getCity());

            holder.tv_company_name.setTypeface(OpenSansRegular);
            holder.tv_company_name.setText(values.get(position).getCompanyName());

            final View temp = convertView;

            holder.delete_btn.setTypeface(fontFamily);
            holder.delete_btn.setText(MBDefinition.icon_delete);

            holder.swipeContactView.setOnTouchListener(mTouchListener);
            holder.viewHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(_context, CompanyPreferenceActivity.class);
                    intent.putExtra(MBDefinition.EXTRA_CITY, values.get(position).getCity());
                    intent.putExtra(MBDefinition.EXTRA_COUNTRY, values.get(position).getCountry());
                    intent.putExtra(MBDefinition.EXTRA_PROVINCE, values.get(position).getState());
                    startActivityForAnim(intent);
                }
            });

            holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Animation pop = AnimationUtils.loadAnimation(context, R.anim.pop);
                    pop.setFillAfter(true);
                    pop.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                            builder.setTitle(_context.getString(R.string.warning));
                            builder.setMessage(_context.getString(R.string.delete_confirmation));
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DBPreference tobeDeleted = values.get(position);
                                    DaoManager daoManager = DaoManager.getInstance(_context);
                                    DBPreferenceDao preferenceDao = daoManager.getDBPreferenceDao(DaoManager.TYPE_WRITE);
                                    preferenceDao.delete(tobeDeleted);
                                    Toast.makeText(_context, tobeDeleted.getCompanyName() + _context.getString(R.string.delete_successful),
                                            Toast.LENGTH_SHORT).show();
                                    ((SwipableListItem) (temp.findViewById(R.id.swipeContactView))).maximize();
                                    values.remove(tobeDeleted);
                                    adapter.notifyDataSetChanged();
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

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    holder.green_circle_delete.setVisibility(View.VISIBLE);
                    holder.green_circle_delete.startAnimation(pop);

                }
            });
            return convertView;
        }
    }

    /**
     * Handle touch events to lock list view scrolling during swipe and block multiple swipe
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        float mDownX;
        private int mSwipeSlop = -1;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(_context).getScaledTouchSlop();
            }

            ((SwipableListItem) v).processDragEvent(event);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE: {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            mListView.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (mSwiping) {
                        // ((SwipableListItem)(v.findViewById(R.id.swipeContactView))).processDragEvent(event);
                    }
                }
                break;
                case MotionEvent.ACTION_UP: {
                    mSwiping = false;
                    mItemPressed = false;
                    break;
                }
                default:
                    return false;
            }
            return true;
        }
    };

}
