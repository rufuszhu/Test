package digital.dispatch.TaxiLimoNewUI.Utils;

import java.util.ArrayList;
import java.util.Locale;

import com.google.android.gms.common.GooglePlayServicesUtil;

import digital.dispatch.TaxiLimoNewUI.R;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Utils {

	public static String driverNoteString = "";

	// Set all the navigation icons and always to set "zero 0" for the item is a category
	public static int[] iconNavigation = new int[] { R.drawable.ic_action_about, R.drawable.ic_action_about, R.drawable.ic_action_about,
			R.drawable.ic_action_about };

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
	 * @param context Context reference to get the TelephonyManager instance from
	 * @return country code or null
	 */
	public static String getUserCountry(Context context) {
	    try {
	        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        final String simCountry = tm.getSimCountryIso();
	        if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
	            return simCountry.toLowerCase(Locale.US);
	        }
	        else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
	            String networkCountry = tm.getNetworkCountryIso();
	            if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
	                return networkCountry.toLowerCase(Locale.US);
	            }
	        }
	    }
	    catch (Exception e) { }
	    return context.getString(R.string.default_country_code);
	}
	
	public static void showErrorDialog(String error, Context context) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(error)
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
	
//	public static boolean isNumeric(String str)  
//	{  
//	  try  
//	  {  
//	    double d = Double.parseDouble(str);  
//	  }  
//	  catch(NumberFormatException nfe)  
//	  {  
//	    return false;  
//	  }  
//	  return true;  
//	}
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\-\\d+)?");  //match a number with optional '-' in the middle.
	}

}
