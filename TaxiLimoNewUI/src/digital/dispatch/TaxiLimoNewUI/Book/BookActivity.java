package digital.dispatch.TaxiLimoNewUI.Book;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.DBPreference;
import digital.dispatch.TaxiLimoNewUI.DBPreferenceDao;
import digital.dispatch.TaxiLimoNewUI.DaoManager.AddressDaoManager;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities.gcmType;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.GetCompanyListTask;
import digital.dispatch.TaxiLimoNewUI.Task.GetEstimateFareTask;
import digital.dispatch.TaxiLimoNewUI.Track.TrackDetailActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.AppController;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class BookActivity extends BaseActivity {
    private static final String TAG = "BookActivity";
    private Context _this;
    private RelativeLayout rl_pick_up, rl_drop_off, rl_date, rl_driver_note, rl_company;
    private TextView tv_pick_up, tv_drop_off, tv_date, tv_driver_note, tv_company, book_btn;
    private TextView icon_pickup, icon_dropoff, icon_date, icon_note, icon_company, tv_date_title, tv_note_title, tv_company_title, angle_right3, angle_right2,
            angle_right1, clear_dropoff;
    private BroadcastReceiver bcReceiver;
    private boolean noCompanyAvailable;
    private boolean bookAnyway;// if this flag is true, let user to book job without driver instruction even if the pickup address has no street number
    private CompanyItem[] cachedCompanyArray;
    private DBPreference preferedForThisCity;
    private NetworkImageView company_icon;
    private FrameLayout icon_preferred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        _this = this;
        setToolBar();
        findView();
        styleView();
        bindEvent();
    }

    private void setToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        Typeface face = FontCache.getFont(this, "fonts/Exo2-Light.ttf");
        TextView yourTextView = Utils.getToolbarTitleView(this,toolbar);
        yourTextView.setTypeface(face);
    }

    private void styleView() {
        Typeface OpenSansRegular = FontCache.getFont(this, "fonts/OpenSansRegular.ttf");
        Typeface icon_pack = FontCache.getFont(this, "fonts/icon_pack.ttf");
        Typeface fontAwesome = FontCache.getFont(this, "fonts/fontawesome.ttf");
        Typeface OpenSansBold = FontCache.getFont(this, "fonts/OpenSansBold.ttf");

        icon_pickup.setTypeface(fontAwesome);
        icon_dropoff.setTypeface(fontAwesome);
        icon_date.setTypeface(icon_pack);
        icon_note.setTypeface(icon_pack);
        icon_company.setTypeface(fontAwesome);
        angle_right1.setTypeface(fontAwesome);
        angle_right2.setTypeface(fontAwesome);
        angle_right3.setTypeface(fontAwesome);
        clear_dropoff.setTypeface(icon_pack);

        clear_dropoff.setText(MBDefinition.ICON_CROSS_SMALL);

        icon_pickup.setText(MBDefinition.ICON_PERSON);
        icon_dropoff.setText(MBDefinition.ICON_DROPOFF);
        icon_date.setText(MBDefinition.icon_tab_calendar);
        icon_note.setText(MBDefinition.ICON_COMMENT);
        icon_company.setText(MBDefinition.ICON_COMPANY);

        angle_right1.setText(MBDefinition.ICON_ANGLE_RIGHT);
        angle_right2.setText(MBDefinition.ICON_ANGLE_RIGHT);
        angle_right3.setText(MBDefinition.ICON_ANGLE_RIGHT);

        tv_date_title.setTypeface(OpenSansRegular);
        tv_note_title.setTypeface(OpenSansRegular);
        tv_company_title.setTypeface(OpenSansRegular);
        tv_pick_up.setTypeface(OpenSansRegular);
        tv_drop_off.setTypeface(OpenSansRegular);
        tv_date.setTypeface(OpenSansRegular);
        tv_driver_note.setTypeface(OpenSansRegular);
        tv_company.setTypeface(OpenSansRegular);
        book_btn.setTypeface(OpenSansBold);
    }

    @Override
    public void onResume() {
        // TL-235
        boolean isTrackDetail = false;
        bcReceiver = CommonUtilities.getGenericReceiver(_this, isTrackDetail);
        LocalBroadcastManager.getInstance(this).registerReceiver(bcReceiver, new IntentFilter(gcmType.message.toString()));
        super.onResume();
        Logger.d(TAG, "on RESUME");
        checkInternet();
        setEstFareIfExist();
        setPickupIfExist();
        setDropOffIfExist();
        setTimeIfExist();
        setDriverNoteIfExist();

        DaoManager daoManager = DaoManager.getInstance(this);
        DBPreferenceDao preferenceDao = daoManager.getDBPreferenceDao(DaoManager.TYPE_WRITE);
        List<DBPreference> preferedCompanyList = preferenceDao.queryBuilder().where(DBPreferenceDao.Properties.City.eq(Utils.mPickupAddress.getLocality().toUpperCase(Locale.US)),
                DBPreferenceDao.Properties.Country.eq(Utils.mPickupAddress.getCountryCode()),
                DBPreferenceDao.Properties.State.eq(LocationUtils.states.get(Utils.mPickupAddress.getAdminArea()))).list();
        Logger.i(TAG, "searching city: " + Utils.mPickupAddress.getLocality().toUpperCase(Locale.US) + ", country: " + Utils.mPickupAddress.getCountryCode() + ", state: " + LocationUtils.states.get(Utils.mPickupAddress.getAdminArea()));

        if(preferedCompanyList.size()>0){
            preferedForThisCity = preferedCompanyList.get(0);
            Logger.i(TAG,"Found preferred company: " + preferedForThisCity.getCompanyName());
            Logger.i(TAG,"Preferred company attr list: " + preferedForThisCity.getAttributeList());
        }
        else{
            Logger.i(TAG,"Preferred company not found");
            preferedForThisCity = null;
        }

        setCompanyIfExist();
        loadSavedAttrList();

        bookAnyway = false;
        noCompanyAvailable=false;
    }

    private void loadSavedAttrList(){
        if(preferedForThisCity!=null) {
            //if user has not manually modified attribute for this trip, use the saved preferred attribute
            if (Utils.selected_attribute == null) {
                Utils.selected_attribute = attrStringToList(preferedForThisCity.getAttributeList());
            }
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

    @Override
    public void onPause() {
        // TL-235
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bcReceiver);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MBDefinition.REQUEST_SELECT_COMPANY_TO_BOOK) {
            if (resultCode == RESULT_OK) {
                Utils.bookJob(Utils.mSelectedCompany, this);
            }
        }
    }

    private void setEstFareIfExist() {
        // TL-88 add fare estimate if drop off address is set and baseRate set up
        RelativeLayout rl_fare = (RelativeLayout) findViewById(R.id.rl_fare);
        TextView tv_est_fare = (TextView) findViewById(R.id.tv_est_fare);
        rl_fare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(_this);
                builder.setMessage(getString(R.string.estimate_disclaimer));
                builder.setTitle(getString(R.string.estimate_disclaimer_title));
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        //if there is a selected company
        if (Utils.mDropoffAddress != null && Utils.mPickupAddress != null && Utils.mSelectedCompany != null && Utils.mSelectedCompany.baseRate != 0
                && Utils.mSelectedCompany.ratePerDistance != 0 ) {
            Typeface exo2Regular = FontCache.getFont(this, "fonts/Exo2-Regular.ttf");
            Typeface exo2SemiBold = FontCache.getFont(this, "fonts/Exo2-SemiBold.ttf");
            Typeface icon_pack = FontCache.getFont(this, "fonts/icon_pack.ttf");
            TextView tv_est = (TextView) findViewById(R.id.tv_est);
            TextView tv_mark = (TextView) findViewById(R.id.tv_mark);

            tv_est_fare.setTypeface(exo2Regular);
            tv_est.setTypeface(exo2SemiBold);
            tv_mark.setTypeface(icon_pack);
            tv_mark.setText(MBDefinition.ICON_EXCLAMATION_CIRCLE_CODE);

            rl_fare.setVisibility(View.VISIBLE);
            rl_drop_off.setBackground(getResources().getDrawable(R.drawable.dropoff_with_fare_selector));
            rl_pick_up.setBackground(getResources().getDrawable(R.drawable.pickup_with_fare_selector));

            tv_est_fare.setText("");

            // --post GB use serial executor by default --
            new GetEstimateFareTask(this, rl_fare, tv_est_fare, Utils.mSelectedCompany.baseRate, Utils.mSelectedCompany.ratePerDistance).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, Utils.mPickupAddress.getLatitude() + "," + Utils.mPickupAddress.getLongitude(),
                    Utils.mDropoffAddress.getLatitude() + "," + Utils.mDropoffAddress.getLongitude(), "driving");

        } else if (cachedCompanyArray != null && cachedCompanyArray.length > 0 && cachedCompanyArray[0].baseRate != 0 && cachedCompanyArray[0].ratePerDistance != 0 && Utils.mPickupAddress != null && Utils.mDropoffAddress != null) {
            Logger.d(TAG, cachedCompanyArray[0].destID + " " + cachedCompanyArray[0].baseRate + " " + cachedCompanyArray[0].ratePerDistance);
            Typeface exo2Regular = Typeface.createFromAsset(getAssets(), "fonts/Exo2-Regular.ttf");
            Typeface exo2SemiBold = Typeface.createFromAsset(getAssets(), "fonts/Exo2-SemiBold.ttf");
            Typeface icon_pack = Typeface.createFromAsset(getAssets(), "fonts/icon_pack.ttf");

            TextView tv_est = (TextView) findViewById(R.id.tv_est);
            TextView tv_mark = (TextView) findViewById(R.id.tv_mark);

            tv_est_fare.setTypeface(exo2Regular);
            tv_est.setTypeface(exo2SemiBold);
            tv_mark.setTypeface(icon_pack);
            tv_mark.setText(MBDefinition.ICON_EXCLAMATION_CIRCLE_CODE);

            rl_fare.setVisibility(View.VISIBLE);
            rl_drop_off.setBackground(getResources().getDrawable(R.drawable.dropoff_with_fare_selector));
            rl_pick_up.setBackground(getResources().getDrawable(R.drawable.pickup_with_fare_selector));

            tv_est_fare.setText("");
            // --post GB use serial executor by default --
            new GetEstimateFareTask(this, rl_fare, tv_est_fare, cachedCompanyArray[0].baseRate, cachedCompanyArray[0].ratePerDistance).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, Utils.mPickupAddress.getLatitude() + "," + Utils.mPickupAddress.getLongitude(),
                    Utils.mDropoffAddress.getLatitude() + "," + Utils.mDropoffAddress.getLongitude(), "driving");
        } else {
            tv_est_fare.setText("");
            rl_fare.setVisibility(View.GONE);
            rl_drop_off.setBackground(getResources().getDrawable(R.drawable.dropoff_selector));
            rl_pick_up.setBackground(getResources().getDrawable(R.drawable.pickup_selector));
        }
    }

    private void setCompanyIfExist() {
        tv_company.setTextColor(_this.getResources().getColor(R.color.gray_light));
        tv_company.setText(getString(R.string.choose));
        company_icon.setVisibility(View.GONE);
        icon_preferred.setVisibility(View.GONE);
        // no need to get company list again if pick up city is not changed and user already selected a company
        if (Utils.last_city.equals(Utils.mPickupAddress.getLocality())) {
            if(Utils.mSelectedCompany != null) {
                tv_company.setText(Utils.mSelectedCompany.name);
                String prefixURL = getResources().getString(R.string.url);
                prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
                company_icon.setVisibility(View.VISIBLE);
                company_icon.setDefaultImageResId(R.drawable.launcher);
                company_icon.setImageUrl(prefixURL + Utils.mSelectedCompany.logo, AppController.getInstance().getImageLoader());
                if(preferedForThisCity!=null && preferedForThisCity.getDestId().equalsIgnoreCase(Utils.mSelectedCompany.destID)){
                    icon_preferred.setVisibility(View.VISIBLE);
                }
                else{
                    icon_preferred.setVisibility(View.GONE);
                }

            }
        } else {
            boolean isFromBooking = true;
            boolean isPreference = false;
            Utils.selected_attribute=null;
            new GetCompanyListTask(this, Utils.mPickupAddress, isFromBooking, isPreference, "", "", "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void setDriverNoteIfExist() {
        if (Utils.driverNoteString.length() > 20) {
            tv_driver_note.setTextColor(getResources().getColor(R.color.black));
            tv_driver_note.setText(Utils.driverNoteString.substring(0, 20) + "...");
        } else if (Utils.driverNoteString.length() == 0) {
            tv_driver_note.setTextColor(getResources().getColor(R.color.gray_light));
            tv_driver_note.setText(getString(R.string.empty_note));
        } else {
            tv_driver_note.setTextColor(getResources().getColor(R.color.black));
            tv_driver_note.setText(Utils.driverNoteString);
        }

    }

    private void setTimeIfExist() {
        if (Utils.pickupDate == null || Utils.pickupTime == null) {
            tv_date.setText(_this.getResources().getString(R.string.now));
            tv_date.setTextColor(_this.getResources().getColor(R.color.gray_light));
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.US);
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma", Locale.US);
            String date = dateFormat.format(Utils.pickupDate);
            String time = timeFormat.format(Utils.pickupTime);
            Calendar cal = Calendar.getInstance();

            if (Utils.pickupDate.getDate() == cal.get(Calendar.DATE)) {
                tv_date.setText(time + ", Today");
            } else
                tv_date.setText(time + ", " + date);
            tv_date.setTextColor(_this.getResources().getColor(R.color.black));
        }
    }

    private void setDropOffIfExist() {
        if (Utils.mDropoffAddress != null) {
            tv_drop_off.setTextColor(_this.getResources().getColor(R.color.black));
            tv_drop_off.setText(LocationUtils.addressToString(_this, Utils.mDropoffAddress));
        } else {
            tv_drop_off.setTextColor(_this.getResources().getColor(R.color.gray_light));
            tv_drop_off.setText(_this.getResources().getString(R.string.enter_for_est_fare));
        }
    }

    private void setPickupIfExist() {
        if (Utils.mPickupAddress != null) {
            if (Utils.pickupHouseNumber != null && !Utils.pickupHouseNumber.equals(""))
                tv_pick_up.setText(Utils.pickupHouseNumber + " " + AddressDaoManager.getStreetNameFromAddress(Utils.mPickupAddress) + " "
                        + Utils.mPickupAddress.getLocality());
            else
                tv_pick_up.setText(LocationUtils.addressToString(_this, Utils.mPickupAddress));
        }
    }

    private void bindEvent() {
        clear_dropoff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(Utils.mDropoffAddress!=null) {
                    tv_drop_off.setText(_this.getString(R.string.enter_for_est_fare));
                    tv_drop_off.setTextColor(_this.getResources().getColor(R.color.list_gray_text));
                    Utils.mDropoffAddress = null;
                    setEstFareIfExist();
                }
            }
        });

        rl_pick_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(_this, ModifyAddressActivity.class);
                intent.putExtra(MBDefinition.ADDRESSBAR_TEXT_EXTRA, tv_pick_up.getText().toString());
                intent.putExtra(MBDefinition.IS_DESTINATION, false);
                ((BookActivity) _this).startActivityForAnim(intent);
            }
        });

        rl_drop_off.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(_this, ModifyAddressActivity.class);
                intent.putExtra(MBDefinition.IS_DESTINATION, true);
                if (!tv_drop_off.getText().toString().startsWith("Enter your")) {
                    intent.putExtra(MBDefinition.ADDRESSBAR_TEXT_EXTRA, tv_drop_off.getText().toString());
                }
                ((BookActivity) _this).startActivityForAnim(intent);
            }
        });

        rl_driver_note.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(_this, DriverNoteActivity.class);
                ((BookActivity) _this).startActivityForAnim(intent);
            }
        });

        rl_date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(_this, SetTimeActivity.class);
                ((BookActivity) _this).startActivityForAnim(intent);
            }
        });

        rl_company.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Utils.mPickupAddress != null && !noCompanyAvailable) {
                    Intent intent = new Intent(_this, AttributeActivity.class);
                    intent.putExtra(MBDefinition.EXTRA_SHOULD_BOOK_RIGHT_AFTER, false);
                    ((BookActivity) _this).startActivityForAnim(intent);
                    // _this.startActivityForResult(intent, MBDefinition.REQUEST_COMPANYITEM_CODE);
                }
            }
        });

        book_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkAllowBooking()) {
                    Utils.bookJob(Utils.mSelectedCompany, _this);
                }
            }
        });

    }

    private void findView() {
        rl_pick_up = (RelativeLayout) findViewById(R.id.rl_pickup);

        rl_drop_off = (RelativeLayout) findViewById(R.id.rl_drop_off);
        rl_date = (RelativeLayout) findViewById(R.id.rl_date);
        rl_driver_note = (RelativeLayout) findViewById(R.id.rl_driver_note);
        rl_company = (RelativeLayout) findViewById(R.id.rl_company);

        clear_dropoff = (TextView) findViewById(R.id.clear_dropoff);

        tv_pick_up = (TextView) findViewById(R.id.tv_pickup);
        tv_drop_off = (TextView) findViewById(R.id.tv_drop_off);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_driver_note = (TextView) findViewById(R.id.tv_driver_note);
        tv_company = (TextView) findViewById(R.id.tv_company);
        book_btn = (TextView) findViewById(R.id.book_button);

        icon_pickup = (TextView) findViewById(R.id.icon_pickup);
        icon_dropoff = (TextView) findViewById(R.id.icon_dropoff);
        icon_date = (TextView) findViewById(R.id.icon_date);
        icon_note = (TextView) findViewById(R.id.icon_note);
        icon_company = (TextView) findViewById(R.id.icon_company);
        tv_date_title = (TextView) findViewById(R.id.tv_date_title);
        tv_note_title = (TextView) findViewById(R.id.tv_note_title);
        tv_company_title = (TextView) findViewById(R.id.tv_company_title);
        angle_right3 = (TextView) findViewById(R.id.angle_right3);
        angle_right2 = (TextView) findViewById(R.id.angle_right2);
        angle_right1 = (TextView) findViewById(R.id.angle_right1);

        company_icon = (NetworkImageView) findViewById(R.id.company_icon);
        icon_preferred = (FrameLayout) findViewById(R.id.icon_preferred);
    }

	/*
     * private void buildAlertMessageNoGps() { final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 * builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false) .setPositiveButton(R.string.yes, new
	 * DialogInterface.OnClickListener() { public void onClick(final DialogInterface dialog, final int id) { startActivity(new
	 * Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); } }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { public
	 * void onClick(final DialogInterface dialog, int which) { dialog.cancel(); } }); final AlertDialog alert = builder.create(); alert.show(); }
	 * 
	 * 
	 * 
	 * private void checkGPSEnable() { final LocationManager manager = (LocationManager) _this.getSystemService(Context.LOCATION_SERVICE);
	 * 
	 * if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { buildAlertMessageNoGps(); } }
	 */

    private void checkInternet() {
        Utils.isInternetAvailable(_this);
    }

    // helper function before booking
    private boolean checkAllowBooking() {

        DaoManager daoManager = DaoManager.getInstance(_this);
        DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
        CompanyItem selectedCompany = Utils.mSelectedCompany;
        List<DBBooking> activeBookingList = bookingDao.queryBuilder()
                .where(Properties.TripStatus.notEq(MBDefinition.MB_STATUS_CANCELLED),
                        Properties.TripStatus.notEq(MBDefinition.MB_STATUS_COMPLETED),
                        Properties.ShouldForceDisableCancel.eq(false)).list(); //ShouldForceDisableCancel means the job is Unknown, if a job is unknown we dont block future booking TL-442

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_this);

        if (Utils.mPickupAddress == null || Utils.mPickupAddress.equals("")) {
            Utils.showMessageDialog(_this.getString(R.string.err_no_pickup_address), _this);
            return false;
        }
        // no taxi company found in current city
        // if (noCompanyAvailable) {
        // Utils.showMessageDialog(_this.getString(R.string.err_no_company_found) + " " + Utils.mPickupAddress.getLocality(), _this);
        // return false;
        // }
        //TL-388 Popup dialog to hint user to enter in detail information in “Message to Driver”, eg. which mall entrance, which aiport terminal, etc.
        // no house number in pickup address
        if (!validateHasHouseNumber() && !bookAnyway && Utils.driverNoteString.length()==0) {
            //showEnterHouseNumberDialog();

            AlertDialog.Builder builder = new AlertDialog.Builder(_this);
            builder.setMessage(getString(R.string.msg_suggest_to_enter_driver_note));
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(_this, DriverNoteActivity.class);
                    ((BookActivity) _this).startActivityForAnim(intent);
                }
            });

            builder.setNegativeButton(R.string.book_anyway, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    bookAnyway = true;
                    book_btn.performClick();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
        // house number return by Google has a range
        if (!validateHouseNumberHasNoRange()) {
            Address address = Utils.mPickupAddress;
            String[] houseNumberRange = TextUtils.split(AddressDaoManager.getHouseNumberFromAddress(address), "-");
            showHouseRangeDialog(houseNumberRange[0], houseNumberRange[1]);
            return false;
        }
        // check if allow multiple booking
        if (activeBookingList.size() > 0 && !SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, MBDefinition.SHARE_MULTI_BOOK_ALLOWED, false)) {
            Utils.showErrorDialog(_this.getString(R.string.err_no_multiple_booking), _this);
            return false;

        }

        // check if allow duplicate booking TL-23
        if (activeBookingList.size() > 0
                && !SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, MBDefinition.SHARE_SAME_LOG_BOOK_ALLOWED, false)) {

            for (int i = 0; i < activeBookingList.size(); i++) {
                DBBooking booking = activeBookingList.get(i);

                if (Utils.compareAddr(booking)) {
                    // both ASAP JOB then it's duplicate
                    if (booking.getPickup_time() == null && (Utils.pickupDate == null || Utils.pickupTime == null)) {

                        Utils.showErrorDialog(_this.getString(R.string.err_msg_no_same_loc), _this);
                        return false;

                    }
                    // both future job then need to check how far apart
                    else if (booking.getPickup_time() != null && Utils.pickupDate != null && Utils.pickupTime != null) {

                        if (checkDuplicatePickupTime(booking)) {
                            Utils.showErrorDialog(_this.getString(R.string.err_msg_no_same_loc), _this);
                            return false;
                        }
                    }
                }
            }
        }
        // check if drop off MANDATORY
        if (SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, MBDefinition.SHARE_DROP_OFF_MANDATORY, false) && Utils.mDropoffAddress == null) {
            Utils.showErrorDialog(_this.getString(R.string.err_dropoff_mandatory), _this);
            return false;
        }
        // no selected company
        if (selectedCompany == null) {
            // get into select company page
            Intent intent = new Intent(_this, AttributeActivity.class);
            intent.putExtra(MBDefinition.EXTRA_SHOULD_BOOK_RIGHT_AFTER, true);
            startActivityForResult(intent, MBDefinition.REQUEST_SELECT_COMPANY_TO_BOOK);
            return false;
        }
        return true;
    }

    // return true if the book job does not fall within the duplicate job time frame with another active job.
    private boolean checkDuplicatePickupTime(DBBooking booking) {

        try {
            SimpleDateFormat checkFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.US);
            Date checkDate = (Date) checkFormat.parse(booking.getPickup_time());
            long checkingTime = checkDate.getTime();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm:ss", Locale.US);

            String date = dateFormat.format(Utils.pickupDate);
            String time = timeFormat.format(Utils.pickupTime);

            Date bookingDate = (Date) checkFormat.parse(date + " " + time);
            long bookingTime = bookingDate.getTime();

            long bufferTime = 0;
            if (Utils.mSelectedCompany.dupChkTime.isEmpty() || Utils.mSelectedCompany.dupChkTime.equalsIgnoreCase("0")) {
                bufferTime = Long.parseLong(_this.getString(R.string.duplicate_check_buffer_time)); // default
            } else {
                bufferTime = Long.parseLong(Utils.mSelectedCompany.dupChkTime);
            }
            // within the duplicate job time frame then consider duplicate job
            if (Math.abs(bookingTime - checkingTime) > 60000 * bufferTime) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean validateHasHouseNumber() {
        Address address = Utils.mPickupAddress;
        if (address == null)
            return false;
        String ad = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";
        String[] strArray = TextUtils.split(ad, " ");

        if (!Utils.isNumeric(strArray[0]) && Utils.pickupHouseNumber.equals("")) {

            return false;
        } else
            return true;
    }

    private boolean validateHouseNumberHasNoRange() {
        Address address = Utils.mPickupAddress;
        String ad = AddressDaoManager.getHouseNumberFromAddress(address);
        if (ad.contains("-") && Utils.pickupHouseNumber.equals("")) {
            return false;
        } else
            return true;
    }

    private void showEnterHouseNumberDialog() {
        final Dialog enterHouseNumDialog = new Dialog(_this);
        enterHouseNumDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        enterHouseNumDialog.setContentView(R.layout.dialog_enter_street_number);
        enterHouseNumDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        TextView tv_house_num = (TextView) enterHouseNumDialog.getWindow().findViewById(R.id.tv_street_address);
        final EditText et_house_num = (EditText) enterHouseNumDialog.getWindow().findViewById(R.id.et_street_number);
        TextView ok = (TextView) enterHouseNumDialog.getWindow().findViewById(R.id.ok);
        TextView cancel = (TextView) enterHouseNumDialog.getWindow().findViewById(R.id.cancel);

        tv_house_num.setText(LocationUtils.addressToString(_this, Utils.mPickupAddress));
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (et_house_num.getText().toString().equals("")) {
                    et_house_num.setError("Pleace enter a street number for pick up");
                } else if (et_house_num.getText().toString().length() > MBDefinition.STREET_NUMBER_MAX_LENGTH)
                    et_house_num.setError("Street number too long");
                else {
                    Utils.pickupHouseNumber = et_house_num.getText().toString();
                    enterHouseNumDialog.dismiss();
                    book_btn.callOnClick();
                }
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                enterHouseNumDialog.dismiss();
            }
        });
        enterHouseNumDialog.setCanceledOnTouchOutside(true);
        enterHouseNumDialog.show();
    }

    private void showHouseRangeDialog(final String start, final String end) {
        final Dialog houseNumRangeDialog = new Dialog(_this);
        houseNumRangeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        houseNumRangeDialog.setContentView(R.layout.dialog_enter_street_number_range);
        houseNumRangeDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        TextView tv_range_message = (TextView) houseNumRangeDialog.getWindow().findViewById(R.id.tv_range_message);
        final EditText et_house_num = (EditText) houseNumRangeDialog.getWindow().findViewById(R.id.et_street_number);
        TextView ok = (TextView) houseNumRangeDialog.getWindow().findViewById(R.id.ok);
        TextView cancel = (TextView) houseNumRangeDialog.getWindow().findViewById(R.id.cancel);

        tv_range_message.setText(start + " and " + end);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (et_house_num.getText().toString().equals("")) {
                    et_house_num.setError("Pleace enter a street number for pick up");
                } else if (Integer.parseInt(et_house_num.getText().toString()) > Integer.parseInt(end)
                        || Integer.parseInt(et_house_num.getText().toString()) < Integer.parseInt(start))
                    et_house_num.setError("Street number not in range");
                else {
                    Utils.pickupHouseNumber = et_house_num.getText().toString();
                    houseNumRangeDialog.dismiss();
                    book_btn.callOnClick();
                }
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                houseNumRangeDialog.dismiss();
            }
        });
        houseNumRangeDialog.setCanceledOnTouchOutside(true);
        houseNumRangeDialog.show();
    }

    public void showBookSuccessDialog(final DBBooking dbBooking) {

        AlertDialog.Builder builder = new AlertDialog.Builder(_this);
        builder.setTitle(R.string.booking_confirmed);
        //is ASAP job
        if(dbBooking.getPickup_time()==null || dbBooking.getPickup_time().length()==0)
            builder.setMessage(getString(R.string.message_book_successful));
        else
            builder.setMessage(getString(R.string.message_book_successful_future_job));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(_this, TrackDetailActivity.class);
                intent.putExtra(MBDefinition.DBBOOKING_EXTRA, dbBooking);
                startActivity(intent);
                finish();
            }
        });
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Intent intent = new Intent(_this, TrackDetailActivity.class);
                intent.putExtra(MBDefinition.DBBOOKING_EXTRA, dbBooking);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void handleGetCompanyListResponse(CompanyItem[] tempCompList, String locality) {
        Utils.last_city = locality;
        cachedCompanyArray = tempCompList;
        if (tempCompList.length == 0) {
            tv_company.setText("No fleets in " + locality);
            noCompanyAvailable = true;
            tv_company.setTextColor(_this.getResources().getColor(R.color.red_light));
            View book_cover = findViewById(R.id.book_cover);
            book_cover.setVisibility(View.VISIBLE);
        }
        //if there is only one company we auto select, do not care about preference
        else if (tempCompList.length == 1) {
            tv_company.setTextColor(_this.getResources().getColor(R.color.black));
            Utils.mSelectedCompany = tempCompList[0];
            tv_company.setText(Utils.mSelectedCompany.name);
            String prefixURL = getResources().getString(R.string.url);
            prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
            company_icon.setVisibility(View.VISIBLE);
            company_icon.setDefaultImageResId(R.drawable.launcher);
            company_icon.setImageUrl(prefixURL + tempCompList[0].logo, AppController.getInstance().getImageLoader());
            noCompanyAvailable = false;
            if(preferedForThisCity!=null && preferedForThisCity.getDestId().equalsIgnoreCase(Utils.mSelectedCompany.destID)){
                icon_preferred.setVisibility(View.VISIBLE);
            }
            else{
                icon_preferred.setVisibility(View.GONE);
            }

        }

        else {
            if(preferedForThisCity!=null){
                Utils.mSelectedCompany = getPreferedCompany(tempCompList);
                if(null != Utils.mSelectedCompany){
                   tv_company.setText(Utils.mSelectedCompany.name);
                    String prefixURL = getResources().getString(R.string.url);
                    prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
                    company_icon.setVisibility(View.VISIBLE);
                    company_icon.setDefaultImageResId(R.drawable.launcher);
                    company_icon.setImageUrl(prefixURL + Utils.mSelectedCompany.logo, AppController.getInstance().getImageLoader());
                    icon_preferred.setVisibility(View.VISIBLE);
                }
            }
            else {
                // show please choose a company
                tv_company.setTextColor(_this.getResources().getColor(R.color.gray_light));
                tv_company.setText(getString(R.string.choose));
                icon_preferred.setVisibility(View.GONE);
            }
            noCompanyAvailable = false;
        }
        setEstFareIfExist();
    }

    private CompanyItem getPreferedCompany(CompanyItem[] companyItems){
        for(int i = 0; i < companyItems.length; i++){
            if(companyItems[i].destID.equals(preferedForThisCity.getDestId())){
                return companyItems[i];
            }
        }
        return null;
    }
}
