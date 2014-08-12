package digital.dispatch.TaxiLimoNewUI.Utils;

import java.util.ArrayList;

import digital.dispatch.TaxiLimoNewUI.R;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.StrictMode;
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

}
