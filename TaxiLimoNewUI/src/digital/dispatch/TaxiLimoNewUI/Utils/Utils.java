package digital.dispatch.TaxiLimoNewUI.Utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;

import com.digital.dispatch.TaxiLimoSoap.responses.AttributeItem;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;
import com.google.android.gms.common.GooglePlayServicesUtil;

import digital.dispatch.TaxiLimoNewUI.DBAttributeDao;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.DateAdapter;
import digital.dispatch.TaxiLimoNewUI.DaoManager.AddressDaoManager;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.BookJobTask;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class Utils {

	public static String driverNoteString = "";
	public static Date pickupDate;
	public static Date pickupTime;
	public static Address mPickupAddress;
	public static Address mDropoffAddress;
	public static ArrayList<AttributeItem> attributeList;
	public static CompanyItem mSelectedCompany;
	public static ArrayList<Integer> selected_attribute;
	public static String selected_attribute_from_bookAgain;
	public static String pickup_unit_number;
	public static String dropoff_unit_number;
	public static int currentTab = 0;
	// public static boolean mainActivityIsActivated = true;
	private static Dialog progressDialog;

	private static final int DEFAULT_FONT_SIZE = 20;
	private static final int VALUE_FONT_SIZE = 13;

	// Set all the navigation icons and always to set "zero 0" for the item is a category
	public static int[] iconNavigation = new int[] { R.drawable.icon_profile, R.drawable.icon_payment, R.drawable.icon_preferences, R.drawable.icon_about };
	public static String pickupHouseNumber = "";

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

	// public static boolean isInternetAvailable() {
	// try {
	// InetAddress ipAddr = InetAddress.getByName("www.google.com"); //You can replace it with your name
	//
	// if (ipAddr.equals("")) {
	// return false;
	// } else {
	// return true;
	// }
	//
	// } catch (Exception e) {
	// return false;
	// }
	//
	// }
	public static void isInternetAvailable(final Context context) {
		
		 new AsyncTask<URL, Integer, Boolean>(){
		     protected Boolean doInBackground(URL... urls) {
		    	 ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		 		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		 		if (activeNetwork != null && activeNetwork.isConnected()) {
		 			try {
		 				URL url = new URL("http://www.google.com/");
		 				HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
		 				urlc.setRequestProperty("User-Agent", "test");
		 				urlc.setRequestProperty("Connection", "close");
		 				urlc.setConnectTimeout(1000); // mTimeout is in seconds
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
		         if(!result)
		        	 Toast.makeText(context, R.string.err_no_internet, Toast.LENGTH_LONG).show();
		     }
		 }.execute();

	}

	public static void setUpTimeDialog(final Context context, final TextView tv_time) {

		int transparent = context.getResources().getColor(R.color.transparent);
		boolean isDate = true;
		final Dialog timeDialog = new Dialog(context);
		timeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		timeDialog.setContentView(R.layout.time_dialog);
		final WheelView dates = (WheelView) timeDialog.getWindow().findViewById(R.id.dates);
		final WheelView times = (WheelView) timeDialog.getWindow().findViewById(R.id.times);
		final RadioButton now_btn = (RadioButton) timeDialog.getWindow().findViewById(R.id.now);
		final RadioButton later_btn = (RadioButton) timeDialog.getWindow().findViewById(R.id.later);
		TextView save = (TextView) timeDialog.getWindow().findViewById(R.id.save);
		TextView cancel = (TextView) timeDialog.getWindow().findViewById(R.id.cancel);

		timeDialog.setCanceledOnTouchOutside(true);

		dates.setVisibleItems(5); // Number of items
		dates.setWheelBackground(R.drawable.wheel_bg_holo);
		dates.setWheelForeground(R.drawable.wheel_val_holo);

		dates.setShadowColor(transparent, transparent, transparent);

		times.setVisibleItems(5); // Number of items
		times.setWheelBackground(R.drawable.wheel_bg_holo);
		times.setWheelForeground(R.drawable.wheel_val_holo);
		times.setShadowColor(transparent, transparent, transparent);
		boolean isToday = true;
		final DateAdapter dateAdapter = new DateAdapter(context, isDate, setupDateList());
		final DateAdapter timeTodayAdapter = new DateAdapter(context, !isDate, setupTimeList(isToday));
		final DateAdapter timeNotTodayAdapter = new DateAdapter(context, !isDate, setupTimeList(!isToday));
		dates.setViewAdapter(dateAdapter);
		dates.setCurrentItem(dateAdapter.getIndexOfDate(pickupDate));
		// if today
		if (pickupDate == null || pickupDate.getDate() == Calendar.getInstance().getTime().getDate()) {
			times.setViewAdapter(timeTodayAdapter);
			times.setCurrentItem(timeTodayAdapter.getIndexOfTime(pickupTime));
		} else {
			times.setViewAdapter(timeNotTodayAdapter);
			times.setCurrentItem(timeNotTodayAdapter.getIndexOfTime(pickupTime));
		}

		final OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (newValue == 0) {
					Date oldDate = timeNotTodayAdapter.getTime(times.getCurrentItem());
					times.setViewAdapter(timeTodayAdapter);
					times.setCurrentItem(timeTodayAdapter.getIndexOfTime(oldDate));
				} else {
					if (oldValue == 0) {
						Date oldDate = timeTodayAdapter.getTime(times.getCurrentItem());
						times.setViewAdapter(timeNotTodayAdapter);
						times.setCurrentItem(timeNotTodayAdapter.getIndexOfTime(oldDate));
					}
				}
			}
		};

		now_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView disable_view = (TextView) timeDialog.getWindow().findViewById(R.id.disable_view);
				disable_view.setVisibility(View.VISIBLE);
				later_btn.setChecked(false);
				now_btn.setChecked(true);
			}
		});

		later_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView disable_view = (TextView) timeDialog.getWindow().findViewById(R.id.disable_view);
				disable_view.setVisibility(View.GONE);
				later_btn.setChecked(true);
				now_btn.setChecked(false);
			}
		});

		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (now_btn.isChecked()) {
					tv_time.setText(context.getString(R.string.now));
					tv_time.setTextSize(DEFAULT_FONT_SIZE);
					tv_time.setTextColor(context.getResources().getColor(R.color.gray_light));
				} else {
					int dateIndex = dates.getCurrentItem();
					int timeIndex = times.getCurrentItem();

					if (dates.getCurrentItem() == 0) {
						pickupTime = timeTodayAdapter.getTime(timeIndex);
						tv_time.setText(dateAdapter.getItemText(dates.getCurrentItem()) + "\n" + timeTodayAdapter.getItemText(times.getCurrentItem()));
						tv_time.setTextSize(VALUE_FONT_SIZE);
						tv_time.setTextColor(context.getResources().getColor(R.color.black));
					} else {
						pickupTime = timeNotTodayAdapter.getTime(timeIndex);
						tv_time.setText(dateAdapter.getItemText(dates.getCurrentItem()) + "\n" + timeNotTodayAdapter.getItemText(times.getCurrentItem()));
						tv_time.setTextSize(VALUE_FONT_SIZE);
						tv_time.setTextColor(context.getResources().getColor(R.color.black));
					}
					pickupDate = dateAdapter.getDate(dateIndex);
				}
				timeDialog.dismiss();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				timeDialog.dismiss();
			}
		});

		dates.addChangingListener(wheelListener);
		timeDialog.show();
	}

	private static ArrayList<Date> setupDateList() {
		ArrayList<Date> dateList = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i <= MBDefinition.FUTURE_BOOKING_RANGE; i++) {
			Date temp = new Date();
			temp = cal.getTime();
			dateList.add(temp);
			cal.add(Calendar.DATE, 1);
		}
		return dateList;
	}

	public static void showOption(LinearLayout ll_attr, String[] attrs, Context context, int marginRight) {
		final float scale = context.getResources().getDisplayMetrics().density;
		ll_attr.removeAllViews();
		for (int i = 0; i < attrs.length; i++) {
			if (!attrs[i].equalsIgnoreCase("")) {
				DaoManager daoManager = DaoManager.getInstance(context);
				DBAttributeDao attributeDao = daoManager.getDBAttributeDao(DaoManager.TYPE_READ);
				String iconId = attributeDao.queryBuilder().where(digital.dispatch.TaxiLimoNewUI.DBAttributeDao.Properties.AttributeId.eq(attrs[i])).list()
						.get(0).getIconId();
				if (!iconId.equalsIgnoreCase("")) {
					ImageView attr = new ImageView(context);
					attr.setImageResource(MBDefinition.attrIconMap.get(Integer.valueOf(iconId)));
					int dimens = (int) (30 * scale + 0.5f);
					int margin_right = (int) (marginRight * scale + 0.5f);
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimens, dimens);
					layoutParams.setMargins(0, 0, margin_right, 0);

					// setting image position
					attr.setLayoutParams(layoutParams);
					ll_attr.addView(attr);
				}
			}
		}
	}

	private static ArrayList<Date> setupTimeList(boolean isToday) {
		ArrayList<Date> timeList = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		if (isToday) {
			int currentMin = cal.get(Calendar.MINUTE);
			int roundMin = (int) (Math.ceil((double) currentMin / 5) * 5);
			cal.set(Calendar.MINUTE, roundMin);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		} else {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		while (cal.get(Calendar.DATE) == today.get(Calendar.DATE)) {
			Date temp = new Date();
			temp = cal.getTime();
			timeList.add(temp);
			cal.add(Calendar.MINUTE, 5);
		}
		return timeList;
	}

	// private static int getCurrentTimeIndex() {
	// int currentTimeIndex = 0;
	// Calendar today = Calendar.getInstance();
	// if (today.get(Calendar.HOUR) == 12) {
	// currentTimeIndex += Math.ceil((double) today.get(Calendar.MINUTE) / 5);
	// } else {
	// currentTimeIndex += 12 * today.get(Calendar.HOUR) + Math.ceil((double) today.get(Calendar.MINUTE) / 5);
	// }
	// if (today.get(Calendar.AM_PM) == Calendar.PM) {
	// currentTimeIndex += 144;
	// }
	// return currentTimeIndex;
	// }

	private static String setupAttributeIdList(ArrayList<Integer> selectedAttribute) {
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
			mbook.setPickupAddress(pickupHouseNumber + " " + AddressDaoManager.getStreetNameFromAddress(Utils.mPickupAddress));
		}
		if (pickup_unit_number != null && pickup_unit_number.length() > 0)
			mbook.setPickup_unit(pickup_unit_number);
		mbook.setPickup_latitude(Utils.mPickupAddress.getLatitude());
		mbook.setPickup_longitude(Utils.mPickupAddress.getLongitude());
		mbook.setPickup_street_name(AddressDaoManager.getStreetNameFromAddress(Utils.mPickupAddress));

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
		mbook.setDestID(selectedCompany.destID);
		mbook.setSysId(String.valueOf(selectedCompany.systemID));

		if (selected_attribute_from_bookAgain == null)
			mbook.setAttributeList(setupAttributeIdList(Utils.selected_attribute));
		else {
			mbook.setAttributeList(selected_attribute_from_bookAgain);
		}

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

	public static void setUpDriverNoteDialog(final Context context, final Dialog messageDialog, final TextView textNote) {
		final EditText driverMessage;
		final TextView textRemaining;
		TextView ok;
		TextView clear;

		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.dialog_driver_message);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		messageDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		driverMessage = (EditText) messageDialog.getWindow().findViewById(R.id.message);
		textRemaining = (TextView) messageDialog.getWindow().findViewById(R.id.text_remaining);
		driverMessage.setHint("Buzzer #");
		driverMessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable note) {
				textRemaining.setText(MBDefinition.DRIVER_NOTE_MAX_LENGTH - note.length() + " Charaters Left");
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
		});

		driverMessage.setText(driverNoteString);
		driverMessage.setSelection(driverNoteString.length());

		ok = (TextView) messageDialog.getWindow().findViewById(R.id.ok);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				driverNoteString = driverMessage.getText().toString();
				if (textNote != null) {
					setNoteIndication(context, textNote);
				}
				messageDialog.dismiss();
			}
		});
		clear = (TextView) messageDialog.getWindow().findViewById(R.id.clear);
		clear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				driverMessage.setText("");
			}
		});

		messageDialog.show();
	}

	public static void setNoteIndication(Context context, TextView textNote) {
		if (driverNoteString.length() > 20) {
			textNote.setTextColor(context.getResources().getColor(R.color.black));
			textNote.setTextSize(VALUE_FONT_SIZE);
			textNote.setText(driverNoteString.substring(0, 20) + "...");
		} else if (driverNoteString.length() == 0) {
			textNote.setTextColor(context.getResources().getColor(R.color.gray_light));
			textNote.setTextSize(DEFAULT_FONT_SIZE);
			textNote.setText(context.getString(R.string.empty_note));
		} else {
			textNote.setTextColor(context.getResources().getColor(R.color.black));
			textNote.setTextSize(VALUE_FONT_SIZE);
			textNote.setText(driverNoteString);
		}
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

		// Get the error dialog from Google Play services
		Dialog errorDialog = builder.create();

		// If Google Play services can provide an error dialog
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
		// This dialog can't be cancelled by pressing the back key
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

	public static void showMessageDialog(String message, Context _context) {
		Dialog messageDialog = new Dialog(_context);
		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.dialog_message);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView tv_message = (TextView) messageDialog.getWindow().findViewById(R.id.tv_message);
		tv_message.setText(message);
		messageDialog.show();
		// AlertDialog.Builder builder = new AlertDialog.Builder(_context);
		// builder.setMessage(message).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// dialog.dismiss();
		// }
		// });
		//
		// builder.show();
	}

	// public static boolean isNumeric(String str)
	// {
	// try
	// {
	// double d = Double.parseDouble(str);
	// }
	// catch(NumberFormatException nfe)
	// {
	// return false;
	// }
	// return true;
	// }

	public static boolean isNumeric(String str) {
		// match a number with optional '-'(or '.') in the middle. this is for street number return by google Geo
		return str.matches("-?\\d+(\\-\\d+)?") || str.matches("-?\\d+(\\.\\d+)?");
	}

}
