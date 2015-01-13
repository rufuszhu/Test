package digital.dispatch.TaxiLimoNewUI.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Book.ModifyAddressActivity;
import digital.dispatch.TaxiLimoNewUI.DaoManager.AddressDaoManager;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.GecoderGoogle;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class AddFavoriteTask extends AsyncTask<String, Void, List<Address>> {

	private static final String TAG = "AddFavoriteTask";
	// Store the context passed to the AsyncTask when the system
	// instantiates it.
	Context localContext;

	// Constructor called by the system to instantiate the task
	public AddFavoriteTask(Context context) {
		// Required by the semantics of AsyncTask
		super();
		// Set a Context for the background task
		localContext = context;
	}

	/**
	 * Get a geocoding service instance, pass latitude and longitude to it, format the returned address, and return the address to the UI thread.
	 */
	@Override
	protected List<Address> doInBackground(String... params) {
		

		// Get the current location from the input parameter list
		String locationName = params[0];

		// Create a list to contain the result address
		List<Address> addresses = null;

		// Try to get an address for the current location. Catch IO or
		// network problems.
		try {

			/*
			 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform to
			 * address standards can also be used.
			 */
			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());
			/*
			 * Call the synchronous getFromLocation() method with the latitude and longitude of the current location. Return at most 1 address.
			 */
			if(geocoder != null)
				addresses = geocoder.getFromLocationName(locationName, 10);

			// Catch network or other I/O problems.
		} catch (Exception e) {
			e.printStackTrace();
			// Log an error 
			Logger.e(TAG, "geocoder failed , moving on to HTTP");
		}
		//If the geocoder returned an address
		if (addresses != null && addresses.size() > 0) {
			return addresses;
		}			
		else{
			boolean logEnabled = false;
			//try HTTP lookup to the maps API					
			GecoderGoogle mGecoderGoogle = new GecoderGoogle(localContext, Locale.getDefault(), logEnabled);
		
			try{
				addresses = mGecoderGoogle.getFromLocationName(locationName, 10);
			}catch (IOException exception1) {

				Logger.e(TAG, localContext.getString(R.string.IO_Exception_getFromLocation));
				// Log an error and return an error message
				// print the stack trace
				exception1.printStackTrace();			
	
				// Catch incorrect latitude or longitude values
			} catch (IllegalArgumentException exception2) {
	
				// Construct a message containing the invalid arguments
				String errorString = localContext.getString(R.string.illegal_argument_exception, locationName);
				// Log the error and print the stack trace
				Logger.e(TAG, errorString);
	
				exception2.printStackTrace();
	
			}catch(Exception e){
				Logger.e(TAG, "other exception");
				e.printStackTrace();
			}
			return addresses;
		}
	}

	/**
	 * A method that's called once doInBackground() completes. Set the text of the UI element that displays the address. This method runs on the UI thread.
	 */
	@Override
	protected void onPostExecute(final List<Address> addresses) {
		if ( localContext instanceof Activity ) {
		    Activity activity = (Activity)localContext;
		    if ( activity.isFinishing() ) {
		        return;
		    }
		}
		
		if (addresses == null) {
			Utils.showMessageDialog(localContext.getString(R.string.cannot_get_address_from_google), localContext);
		} else if (addresses.size() > 1) {
			// pop up list
			boolean isSave = false;
			setUpListDialog(localContext, LocationUtils.addressListToStringList(localContext, addresses), addresses, isSave);
		} else if (addresses.size() == 1) {
			setUpEnterNickNameDialog(addresses.get(0));
		} else {
			Utils.showErrorDialog(localContext.getString(R.string.cannot_get_address_from_google), localContext);
		}
	}

	private void setUpListDialog(final Context context, final ArrayList<String> addresses, final List<Address> addressesObj, final boolean isSave) {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
		// builderSingle.setIcon(R.drawable.ic_launcher);
		builderSingle.setTitle("Please be more specific");
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.autocomplete_list_item);
		arrayAdapter.addAll(addresses);
		builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
					new AddFavoriteTask(localContext).execute(addresses.get(which));
			}
		});
		builderSingle.show();
	}
	
	private void setUpEnterNickNameDialog(final Address address) {
		final EditText nickname_edit;
		TextView address_text;
		TextView cancel;
		TextView add;
		final Dialog nicknameDialog = new Dialog(localContext);
		nicknameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		nicknameDialog.setContentView(R.layout.nickname_dialog);
		nicknameDialog.setCanceledOnTouchOutside(true);

		address_text = (TextView) nicknameDialog.getWindow().findViewById(R.id.addr);
		nickname_edit = (EditText) nicknameDialog.getWindow().findViewById(R.id.nickname);
		address_text.setText(LocationUtils.addressToString(localContext, address));

		cancel = (TextView) nicknameDialog.getWindow().findViewById(R.id.cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) localContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(nickname_edit.getWindowToken(), 0);
				nicknameDialog.dismiss();
			}
		});
		add = (TextView) nicknameDialog.getWindow().findViewById(R.id.add);
		add.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String nickname = nickname_edit.getText().toString();
				if (nickname.length() == 0) {
					nickname_edit.setError(localContext.getString(R.string.nickName_cannot_be_empty));
				} else {
					DaoManager daoManager = DaoManager.getInstance(localContext);
					DBAddressDao addressDao = daoManager.getAddressDao(DaoManager.TYPE_WRITE);
					DBAddress dbAddress = AddressDaoManager.addDaoAddressByAddress(address, "", nickname, true, addressDao);
					Toast.makeText(localContext, dbAddress.getNickName() + " is successfully added", Toast.LENGTH_SHORT).show();

					InputMethodManager imm = (InputMethodManager) localContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(nickname_edit.getWindowToken(), 0);
                    if (localContext instanceof ModifyAddressActivity) {
                        ((ModifyAddressActivity) localContext).favoritesFragment.notifyChange();
                    }
					nicknameDialog.dismiss();
				}
			}
		});
		nicknameDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		nicknameDialog.show();
	}

}
