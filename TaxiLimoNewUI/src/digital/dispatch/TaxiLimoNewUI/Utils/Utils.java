package digital.dispatch.TaxiLimoNewUI.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import digital.dispatch.TaxiLimoNewUI.DBAttribute;
import digital.dispatch.TaxiLimoNewUI.DBAttributeDao;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DaoManager.AddressDaoManager;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Installation;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.BookJobTask;

public class Utils {

	public static String driverNoteString = "";
	public static String last_city = "";
	public static Date pickupDate;
	public static Date pickupTime;
	public static Address mPickupAddress;
	public static Address mDropoffAddress;
	public static CompanyItem mSelectedCompany;
	public static ArrayList<Integer> selected_attribute;
	public static String pickup_unit_number;
	public static String dropoff_unit_number;
	public static int currentTab = 0;
	// public static boolean mainActivityIsActivated = true;
	private static Dialog progressDialog;
    private static ProgressDialog progressDialogWithMessage;
	// Set all the navigation icons and always to set "zero 0" for the item is a category
	// public static int[] iconNavigation = new int[] { R.drawable.icon_profile, R.drawable.icon_payment, R.drawable.icon_preferences, R.drawable.icon_about };
	public static int[] iconNavigation = new int[] { R.drawable.icon_profile, R.drawable.icon_preferences, R.drawable.icon_about };
	public static String pickupHouseNumber = "";

	// use the old hardwareId if exist, otherwise create a new one and save it in share preference
	public static String getHardWareId(Context _context) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
		String hardwareId = prefs.getString(MBDefinition.PROPERTY_HARDWARE_ID, "");

		if (hardwareId.isEmpty()) {
			Logger.i("HardwareID not found, first time install.");
			hardwareId = Installation.id(_context);
			SharedPreferencesManager.savePreferences(prefs, MBDefinition.PROPERTY_HARDWARE_ID, hardwareId);
			return hardwareId;
		}

		else {
			return hardwareId;
		}
	}

	// get title of the item navigation
	public static String getTitleItem(Context context, int posicao) {
		String[] titulos = context.getResources().getStringArray(R.array.nav_menu_items);
		return titulos[posicao];
	}

	/**
	 * Uses static final constants to detect if the device's platform version is Gingerbread or later.
	 */
	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	/**
	 * Uses static final constants to detect if the device's platform version is Honeycomb or later.
	 */
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	/**
	 * Uses static final constants to detect if the device's platform version is Honeycomb MR1 or later.
	 */
	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	/**
	 * Uses static final constants to detect if the device's platform version is ICS or later.
	 */
	public static boolean hasICS() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static void isInternetAvailable(final Context context) {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected)
            Toast.makeText(context, R.string.err_no_internet, Toast.LENGTH_LONG).show();

        else {

            new AsyncTask<URL, Integer, Boolean>() {
                protected Boolean doInBackground(URL... urls) {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null && activeNetwork.isConnected()) {
                        try {
                            URL url = new URL("http://www.google.com/");
                            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                            urlc.setRequestProperty("User-Agent", "test");
                            urlc.setRequestProperty("Connection", "close");
                            urlc.setConnectTimeout(5000); // mTimeout is in 5 seconds
                            urlc.connect();
                            if (urlc.getResponseCode() == 200) {
                                return true;
                            } else {
                                return false;
                            }
                        } catch (IOException e) {
                            Log.i("warning", "Error checking internet connection", e);
                            return false;
                        }
                    }

                    return false;
                }

                protected void onPostExecute(Boolean result) {
                    if (!result)
                        Toast.makeText(context, R.string.err_no_internet, Toast.LENGTH_LONG).show();
                }
            }.execute();
        }
	}

	public static void showOption(LinearLayout ll_attr, String[] attr_id_list, Context context, int marginRight) {
		final float scale = context.getResources().getDisplayMetrics().density;
		ll_attr.removeAllViews();
		for (int i = 0; i < attr_id_list.length; i++) {
			if (!attr_id_list[i].equalsIgnoreCase("")) {
				DaoManager daoManager = DaoManager.getInstance(context);
				DBAttributeDao attributeDao = daoManager.getDBAttributeDao(DaoManager.TYPE_READ);
                List<DBAttribute> list = attributeDao.queryBuilder().where(digital.dispatch.TaxiLimoNewUI.DBAttributeDao.Properties.AttributeId.eq(attr_id_list[i])).list();
                String iconId = "";
                if(list.size()>0){
                    iconId = list.get(0).getIconId();
                }
				if (!iconId.equalsIgnoreCase("")) {
                    int dimens = (int) (30 * scale + 0.5f);
                    int margin_right = (int) (marginRight * scale + 0.5f);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimens, dimens);
                    layoutParams.setMargins(0, 0, margin_right, 0);


					if(MBDefinition.attrIconMap.get(Integer.valueOf(iconId)) != 0){ //TL-276 to avoid resource NotFoundException
                        ImageView attr = new ImageView(context);
                        attr.setImageResource(MBDefinition.attrIconMap.get(Integer.valueOf(iconId)));
                        // setting image position
                        attr.setLayoutParams(layoutParams);
                        ll_attr.addView(attr);
					}
                    else{
                        TextView attr_custom = new TextView(context);
                        attr_custom.setBackground(context.getResources().getDrawable(R.drawable.attr_icon_background));
                        attr_custom.setText(getShortName(attributeDao, list.get(0), i, attr_id_list));
                        // setting image position

                        attr_custom.setLayoutParams(layoutParams);
                        attr_custom.setGravity(Gravity.CENTER);
                        attr_custom.setTextColor(context.getResources().getColor(R.color.blue_grenn_color2));
                        attr_custom.setTypeface(FontCache.getFont(context, "fonts/Exo2-SemiBold.ttf"));

                        ll_attr.addView(attr_custom);
                    }
				}
			}
		}
	}


    private static String getShortName(DBAttributeDao attributeDao, DBAttribute dbAttr, int position, String[] attr_id_list) {
        int repeatCount = 0;
        int beforeCount = 0;
        char firstChar = dbAttr.getName().charAt(0);
        for (int i = 0; i < attr_id_list.length; i++) {
            DBAttribute temp = getDBAttrById(attributeDao,attr_id_list[i]);
            if (temp.getName().charAt(0) == firstChar) {
                if(!checkIconAvailable(Integer.valueOf(temp.getIconId()))){
                    repeatCount++;
                    if (i < position)
                        beforeCount++;
                }
            }
        }
        if (repeatCount > 1) {
            return Character.toString(firstChar) + Integer.valueOf(beforeCount + 1).toString();
        } else
            return Character.toString(firstChar);
    }

    private static DBAttribute getDBAttrById(DBAttributeDao attributeDao, String id){
        return attributeDao.queryBuilder().where(digital.dispatch.TaxiLimoNewUI.DBAttributeDao.Properties.AttributeId.eq(id)).list()
                .get(0);
    }

    private static boolean checkIconAvailable(int attrIconId){
        return MBDefinition.attrBtnOnMap.get(attrIconId) != 0 && MBDefinition.attrBtnOffMap.get(attrIconId) != 0;
    }

	public static String setupAttributeIdList(ArrayList<Integer> selectedAttribute) {
		String temp = "";
		if (selectedAttribute == null)
			return temp;
		for (int i = 0; i < selectedAttribute.size(); i++) {
			String attrid = selectedAttribute.get(i) + "";
			temp += attrid + ",";
		}
		if (!temp.equalsIgnoreCase("")) {
			temp = temp.substring(0, temp.length() - 1);
		}
		return temp;
	}

	private static void setUpPickupAddress(DBBooking mbook, Context context) {
		mbook.setPickup_district(Utils.mPickupAddress.getLocality());
		if (pickupHouseNumber.equals("")) {
			mbook.setPickup_house_number(AddressDaoManager.getHouseNumberFromAddress(Utils.mPickupAddress));
			mbook.setPickupAddress(LocationUtils.addressToString(context, Utils.mPickupAddress));
		} else {
			mbook.setPickup_house_number(pickupHouseNumber);
			mbook.setPickupAddress(pickupHouseNumber + " " + AddressDaoManager.getStreetNameFromAddress(Utils.mPickupAddress) + " "
					+ Utils.mPickupAddress.getLocality());
		}
		if (pickup_unit_number != null && pickup_unit_number.length() > 0)
			mbook.setPickup_unit(pickup_unit_number);
		mbook.setPickup_latitude(Utils.mPickupAddress.getLatitude());
		mbook.setPickup_longitude(Utils.mPickupAddress.getLongitude());
		mbook.setPickup_street_name(AddressDaoManager.getStreetNameFromAddress(Utils.mPickupAddress));
		mbook.setPickup_zipCode(Utils.mPickupAddress.getPostalCode()); //TL-301
	}

	private static void setUpDropoffAddress(DBBooking mbook, Context context) {
		Address ad = Utils.mDropoffAddress;
		if (ad != null) {
			mbook.setDropoff_district(ad.getLocality());
			mbook.setDropoff_house_number(AddressDaoManager.getHouseNumberFromAddress(ad));
			mbook.setDropoff_latitude(ad.getLatitude());
			mbook.setDropoff_longitude(ad.getLongitude());
			if (dropoff_unit_number != null && dropoff_unit_number.length() > 0)
				mbook.setDropoff_unit(dropoff_unit_number);
			mbook.setDropoff_street_name(AddressDaoManager.getStreetNameFromAddress(ad));
			mbook.setDropoffAddress(LocationUtils.addressToString(context, ad));
		}
	}

	public static void bookJob(CompanyItem selectedCompany, Context context) {
		DBBooking mbook = new DBBooking();

		mbook.setCompany_attribute_list(selectedCompany.attributes);
		mbook.setCompany_description(selectedCompany.description);
		mbook.setCompany_icon(selectedCompany.logo);
		mbook.setCompany_name(selectedCompany.name);
		mbook.setCompany_phone_number(selectedCompany.phoneNr);
		mbook.setCompany_dupChk_time(selectedCompany.dupChkTime); // added duplicate check time frame to DBBooking table
		mbook.setDestID(selectedCompany.destID);
		mbook.setSysId(String.valueOf(selectedCompany.systemID));
		// TL-222
		mbook.setCompany_car_file(selectedCompany.carFile);
		//Tl-304
		mbook.setCompany_baseRate(selectedCompany.baseRate);
		mbook.setCompany_rate_PerDistance(selectedCompany.ratePerDistance);

		mbook.setAttributeList(setupAttributeIdList(Utils.selected_attribute));

		if (selectedCompany.multiPay.equalsIgnoreCase("Y"))
			mbook.setMulti_pay_allow(true);
		else
			mbook.setMulti_pay_allow(false);

		setUpPickupAddress(mbook, context);
		setUpDropoffAddress(mbook, context);
		mbook.setRemarks(Utils.driverNoteString);
		// if pick up time not spcify,
		if (Utils.pickupDate == null || Utils.pickupTime == null) {
			// Calendar cal = Calendar.getInstance();
			// SimpleDateFormat pickupTimeFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.US);
			// mbook.setPickup_time(pickupTimeFormat.format(cal.getTime()));
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm:ss", Locale.US);
			String date = dateFormat.format(Utils.pickupDate);
			String time = timeFormat.format(Utils.pickupTime);
			// Logger.e("date: " + date);
			// Logger.e("time: " + time);

			mbook.setPickup_time(date + " " + time);
		}
		new BookJobTask(context, mbook).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
	 * 
	 * @param context
	 *            Context reference to get the TelephonyManager instance from
	 * @return country code or null
	 */
	public static String getUserCountry(Context context) {
		try {
			final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			final String simCountry = tm.getSimCountryIso();
			if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
				return simCountry.toLowerCase(Locale.US);
			} else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
				String networkCountry = tm.getNetworkCountryIso();
				if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
					return networkCountry.toLowerCase(Locale.US);
				}
			}
		} catch (Exception e) {
		}
		return context.getString(R.string.default_country_code);
	}

	public static void showErrorDialog(String error, Context context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(error).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});

		Dialog errorDialog = builder.create();

		if (errorDialog != null) {

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);

			try {
				FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
				// Show the error dialog in the DialogFragment
				errorFragment.show(fragmentManager, LocationUtils.APPTAG);
			} catch (ClassCastException e) {
				Logger.e("Can't get fragment manager");
			}
		}
	}

	public static void showProcessingDialog(Context _context) {

		progressDialog = new Dialog(_context);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Drawable d = new ColorDrawable(Color.BLACK);
		d.setAlpha(0);
		progressDialog.getWindow().setBackgroundDrawable(d);
		progressDialog.setContentView(R.layout.custom_dialog);
		// uncomment the next line if you want this dialog can't be cancelled by pressing the back key
		progressDialog.setCancelable(false);

		DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {

			}
		};
        progressDialog.setOnCancelListener(cancelListener);
        progressDialog.setCanceledOnTouchOutside(false);

		progressDialog.show();
	}

	public static void stopProcessingDialog(Context _context) {
		progressDialog.dismiss();
	}

    public static void showProcessingDialogWithMessage(Context _context, String message) {

        progressDialogWithMessage = new ProgressDialog(_context);
        progressDialogWithMessage.setIndeterminate(false);
        progressDialogWithMessage.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialogWithMessage.setCanceledOnTouchOutside(false);
        progressDialogWithMessage.setCancelable(false);
        progressDialogWithMessage.setMessage(message);
        progressDialogWithMessage.show();
    }

    public static void stopProcessingDialogWithMessage(Context _context) {
        progressDialogWithMessage.dismiss();
    }

	public static void showMessageDialog(String message, Context _context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(_context);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		// Dialog messageDialog = new Dialog(_context);

		// messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// messageDialog.setContentView(R.layout.dialog_message);
		// messageDialog.setCanceledOnTouchOutside(true);
		// messageDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// TextView tv_message = (TextView) messageDialog.getWindow().findViewById(R.id.tv_message);
		// tv_message.setText(message);
		// messageDialog.show();

	}

	public static boolean isNumeric(String str) {
		// match a number with optional '-'(or '.') in the middle. this is for street number return by google Geo
		return str.matches("-?\\d+(\\-\\d+)?") || str.matches("-?\\d+(\\.\\d+)?");
	}

	// TL-23 check duplicate address
	public static boolean compareAddr(DBBooking booking) {

		String district = Utils.mPickupAddress.getLocality();
		if (district != null && booking.getPickup_district() != null) {
			if (!district.equalsIgnoreCase(booking.getPickup_district())) {
				return false;
			}
		}

		String houseNumber = null;
		if (pickupHouseNumber.equals("")) {
			houseNumber = AddressDaoManager.getHouseNumberFromAddress(Utils.mPickupAddress);
		} else {
			houseNumber = pickupHouseNumber;
		}

		if (houseNumber != null && booking.getPickup_house_number() != null) {
			if (!houseNumber.equalsIgnoreCase(booking.getPickup_house_number())) {
				return false;
			}
		}

		String unitNumber = null;
		if (pickup_unit_number != null && pickup_unit_number.length() > 0) {
			unitNumber = pickup_unit_number;
		}

		if (unitNumber != null && booking.getPickup_unit() != null) {
			if (!unitNumber.equalsIgnoreCase(booking.getPickup_unit())) {
				return false;
			}
		}

		String streetName = AddressDaoManager.getStreetNameFromAddress(Utils.mPickupAddress);
		if (streetName != null && booking.getPickup_street_name() != null) {
			if (!streetName.equalsIgnoreCase(booking.getPickup_street_name())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	public static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public static void showUILockScreen(final Context context) {

		final Dialog lockScreenDialog = new Dialog(context);
		lockScreenDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		lockScreenDialog.setContentView(R.layout.dialog_lock_screen);
		lockScreenDialog.setCanceledOnTouchOutside(false);
		lockScreenDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		Typeface icon_pack = FontCache.getFont(context, "fonts/icon_pack.ttf");
		Typeface OpenSansSemibold = FontCache.getFont(context, "fonts/OpenSansSemibold.ttf");
		Typeface exoBold = FontCache.getFont(context, "fonts/Exo2-Bold.ttf");
		
		TextView icon_call = (TextView) lockScreenDialog.getWindow().findViewById(R.id.icon_call);
		TextView icon_email = (TextView) lockScreenDialog.getWindow().findViewById(R.id.icon_email);
		TextView tv_email_us = (TextView) lockScreenDialog.getWindow().findViewById(R.id.tv_email_us);
		TextView tv_call_us = (TextView) lockScreenDialog.getWindow().findViewById(R.id.tv_call_us);
		TextView warning_icon = (TextView) lockScreenDialog.getWindow().findViewById(R.id.warning_icon);
		TextView tv_denied = (TextView) lockScreenDialog.getWindow().findViewById(R.id.tv_denied);
		TextView tv_lock_text = (TextView) lockScreenDialog.getWindow().findViewById(R.id.tv_lock_text);

		tv_denied.setTypeface(exoBold);
		tv_lock_text.setTypeface(OpenSansSemibold);
		tv_call_us.setTypeface(exoBold);
		tv_email_us.setTypeface(exoBold);
		
		icon_call.setTypeface(icon_pack);
		icon_email.setTypeface(icon_pack);
		warning_icon.setTypeface(icon_pack);
		
		icon_call.setText(MBDefinition.ICON_PHONE_CIRCLE); 
		warning_icon.setText(MBDefinition.ICON_WARNNING);
		icon_email.setText(MBDefinition.ICON_MESSAGE);
		
		LinearLayout ll_call = (LinearLayout) lockScreenDialog.getWindow().findViewById(R.id.ll_call);
		LinearLayout ll_email = (LinearLayout) lockScreenDialog.getWindow().findViewById(R.id.ll_email);

		ll_call.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +"18773378008"));
				context.startActivity(intent);
			}
		});

		ll_email.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@zoroapp.com" });
				i.putExtra(Intent.EXTRA_SUBJECT, "");
				i.putExtra(Intent.EXTRA_TEXT, "");
				try {
					context.startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		lockScreenDialog.show();
	}



    public static TextView getToolbarTitleView(ActionBarActivity activity, Toolbar toolbar){
        ActionBar actionBar = activity.getSupportActionBar();
        CharSequence actionbarTitle = null;
        if(actionBar != null)
            actionbarTitle = actionBar.getTitle();
        actionbarTitle = TextUtils.isEmpty(actionbarTitle) ? toolbar.getTitle() : actionbarTitle;
        if(TextUtils.isEmpty(actionbarTitle)) return null;
        // can't find if title not set
        for(int i= 0; i < toolbar.getChildCount(); i++){
            View v = toolbar.getChildAt(i);
            if(v != null && v instanceof TextView){
                TextView t = (TextView) v;
                CharSequence title = t.getText();
                if(!TextUtils.isEmpty(title) && actionbarTitle.equals(title) && t.getId() == View.NO_ID){
                    //Toolbar does not assign id to views with layout params SYSTEM, hence getId() == View.NO_ID
                    //in same manner subtitle TextView can be obtained.
                    return t;
                }
            }
        }
        return null;
    }
}
