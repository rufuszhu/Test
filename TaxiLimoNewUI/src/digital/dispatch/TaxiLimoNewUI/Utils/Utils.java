package digital.dispatch.TaxiLimoNewUI.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;

import com.google.android.gms.common.GooglePlayServicesUtil;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.DateAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Utils {

	public static String driverNoteString = "";
	public static Date pickupDate;
	public static Date pickupTime;

	// Set all the navigation icons and always to set "zero 0" for the item is a category
	public static int[] iconNavigation = new int[] { R.drawable.ic_action_about, R.drawable.ic_action_about, R.drawable.ic_action_about, R.drawable.ic_action_about };

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
		//if today
		if(pickupDate == null || pickupDate.getDate()== Calendar.getInstance().getTime().getDate()){
			times.setViewAdapter(timeTodayAdapter);
			times.setCurrentItem(timeTodayAdapter.getIndexOfTime(pickupTime));
		}else{
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
				if (now_btn.isChecked())
					tv_time.setText("Now");
				else {
					int dateIndex = dates.getCurrentItem();
					int timeIndex = times.getCurrentItem();
					
					if(dates.getCurrentItem()==0){
						pickupTime = timeTodayAdapter.getTime(timeIndex);
						tv_time.setText(dateAdapter.getItemText(dates.getCurrentItem()) + "\n" + timeTodayAdapter.getItemText(times.getCurrentItem()));
					}
					else{
						pickupTime = timeNotTodayAdapter.getTime(timeIndex);
						tv_time.setText(dateAdapter.getItemText(dates.getCurrentItem()) + "\n" + timeNotTodayAdapter.getItemText(times.getCurrentItem()));
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

	private static int getCurrentTimeIndex() {
		int currentTimeIndex = 0;
		Calendar today = Calendar.getInstance();
		if (today.get(Calendar.HOUR) == 12) {
			currentTimeIndex += Math.ceil((double) today.get(Calendar.MINUTE) / 5);
		} else {
			currentTimeIndex += 12 * today.get(Calendar.HOUR) + Math.ceil((double) today.get(Calendar.MINUTE) / 5);
		}
		if (today.get(Calendar.AM_PM) == Calendar.PM) {
			currentTimeIndex += 144;
		}
		return currentTimeIndex;
	}
	

	public static void setUpDriverNoteDialog(final Context context, final Dialog messageDialog, final TextView textNote) {
		final EditText driverMessage;
		final TextView textRemaining;
		TextView ok;
		TextView clear;

		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.message_dialog);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		messageDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		driverMessage = (EditText) messageDialog.getWindow().findViewById(R.id.message);
		textRemaining = (TextView) messageDialog.getWindow().findViewById(R.id.text_remaining);

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
		if (driverNoteString.length() > 10)
			textNote.setText(driverNoteString.substring(0, 10) + "...");
		else if (driverNoteString.length() == 0)
			textNote.setText(context.getString(R.string.empty_note));
		else
			textNote.setText(driverNoteString);
	}

	public static void setUpListDialog(final Context context, ArrayList<String> addresses) {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
		builderSingle.setIcon(R.drawable.ic_launcher);
		builderSingle.setTitle("Select One Name:-");
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
		arrayAdapter.addAll(addresses);
		builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String strName = arrayAdapter.getItem(which);
				AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
				builderInner.setMessage(strName);
				builderInner.setTitle("Your Selected Item is");
				builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builderInner.show();
			}
		});
		builderSingle.show();
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
		return str.matches("-?\\d+(\\-\\d+)?"); // match a number with optional '-' in the middle. this is for street number return by google Geo
	}

}
