package digital.dispatch.TaxiLimoNewUI.History;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadImageTask;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class TripDetailActivity extends Activity {

	private static final String TAG = "TripDetailActivity";
	private DBBooking dbBook;
	private Context _context;
	private Address pickupAddress;
	private Address dropOffAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_detail);
		_context = this;
		dbBook = (DBBooking) getIntent().getSerializableExtra(MBDefinition.DBBOOKING_EXTRA);
		TextView tv_trip_status = (TextView) findViewById(R.id.tv_trip_status);
		TextView tv_id = (TextView) findViewById(R.id.tv_history_id);
		TextView tv_receive = (TextView) findViewById(R.id.tv_history_receive);
		TextView tv_from = (TextView) findViewById(R.id.tv_pickup_address);
		TextView tv_to = (TextView) findViewById(R.id.tv_dropoff_address);
		ImageView iv_company_icon = (ImageView) findViewById(R.id.iv_tracking_company_icon);
		LinearLayout ll_attr = (LinearLayout) findViewById(R.id.ll_attr);
		TextView tv_company_name =(TextView) findViewById(R.id.tv_company_name);
		TextView tv_company_description = (TextView) findViewById(R.id.tv_company_description);
		TextView tv_payment = (TextView) findViewById(R.id.tv_payment);
		TextView tv_auth_code = (TextView) findViewById(R.id.tv_auth_code); 
		
		TableRow auth_code_row =(TableRow) findViewById(R.id.auth_code_row);
		TableRow payment_row =(TableRow) findViewById(R.id.payment_row);
		
		Button call_btn = (Button) findViewById(R.id.call_btn);
		call_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dbBook.getCompany_phone_number()));
				startActivity(intent);
			}
		});
		
		LinearLayout ll_bookAgain_btn = (LinearLayout) findViewById(R.id.ll_bookAgain_btn);
		LinearLayout ll_remove_btn = (LinearLayout) findViewById(R.id.ll_remove_btn);
		ll_bookAgain_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CompanyItem company = new CompanyItem();
				company.description = dbBook.getCompany_description();
				company.attributes = dbBook.getCompany_attribute_list();
				company.logo = dbBook.getCompany_icon();
				company.name = dbBook.getCompany_name();
				company.phoneNr = dbBook.getCompany_phone_number();
				company.destID = dbBook.getDestID();
				company.systemID = Integer.parseInt(dbBook.getSysId());
				
				Utils.mSelectedCompany = company;
				Utils.selected_attribute_from_bookAgain = dbBook.getAttributeList();
				boolean isPickup = true;
				new GetAddressTask(_context, isPickup).execute(dbBook.getPickupAddress());
			}
		});
		ll_remove_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(_context);
				builder.setTitle("Confirm Delete");
				builder.setMessage("Are you sure you want to delete this job history?");
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						DaoManager daoManager = DaoManager.getInstance(_context);
						DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
						bookingDao.delete(dbBook);
						finish();
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
		});
		
		tv_id.setText(dbBook.getTaxi_ride_id()+"");
		tv_receive.setText(dbBook.getTripCreationTime());
		tv_from.setText(dbBook.getPickupAddress());
		
		//It is safe to assume that the job is this activity is either completed or cancel
		if(dbBook.getTripStatus()==MBDefinition.MB_STATUS_COMPLETED){
			tv_trip_status.setText("Trip Completed");
			tv_trip_status.setBackgroundColor(getResources().getColor(R.color.completed_color));
		}
		else{
			tv_trip_status.setText("Trip Canceled");
			tv_trip_status.setBackgroundColor(getResources().getColor(R.color.canceled_color));
		}
		
		if (dbBook.getDropoffAddress() != null && dbBook.getDropoffAddress().length() > 0)
			tv_to.setText(dbBook.getDropoffAddress());
		else
			tv_to.setText("Not Given");
		
		

		String prefixURL = this.getString(R.string.url);
		prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
		new DownloadImageTask(iv_company_icon).execute(prefixURL + dbBook.getCompany_icon());

		tv_company_name.setText(dbBook.getCompany_name());
		tv_company_description.setText(dbBook.getCompany_description());
		
		if (dbBook.getAttributeList() != null){
			int margin_right = 10;
			Utils.showOption(ll_attr, dbBook.getAttributeList().split(","), this, margin_right);
		}
		
		if(dbBook.getAlready_paid()){
			auth_code_row.setVisibility(View.VISIBLE);
			payment_row.setVisibility(View.VISIBLE);
			tv_payment.setText("$ " + dbBook.getPaidAmount());
			tv_auth_code.setText(dbBook.getAuthCode());
		}
		else{
			auth_code_row.setVisibility(View.GONE);
			payment_row.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
	}

	@Override
	public void onPause() {
		super.onPause();
		Logger.e(TAG, "on PAUSE");
		
	}
	
	private class GetAddressTask extends AsyncTask<String, Void, List<Address>> {

		// Store the context passed to the AsyncTask when the system instantiates it.
		Context localContext;
		boolean isPickup;

		// Constructor called by the system to instantiate the task
		public GetAddressTask(Context context, boolean isPickup) {

			// Required by the semantics of AsyncTask
			super();

			// Set a Context for the background task
			localContext = context;
			this.isPickup = isPickup;
		}

		/**
		 * Get a geocoding service instance, pass latitude and longitude to it, format the returned address, and return the address to the UI thread.
		 */
		@Override
		protected List<Address> doInBackground(String... params) {
			/*
			 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform to address standards can also be used.
			 */
			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

			// Get the current location from the input parameter list
			String locationName = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;

			// Try to get an address for the current location. Catch IO or network problems.
			try {

				/*
				 * Call the synchronous getFromLocation() method with the latitude and longitude of the current location. Return at most 1 address.
				 */
				addresses = geocoder.getFromLocationName(locationName, 10);

				// Catch network or other I/O problems.
			} catch (IOException exception1) {

				// Log an error and return an error message
				Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));

				// print the stack trace
				exception1.printStackTrace();

				// Return an error message
				// return (getString(R.string.IO_Exception_getFromLocation));

				// Catch incorrect latitude or longitude values
			} catch (IllegalArgumentException exception2) {

				// Construct a message containing the invalid arguments
				String errorString = getString(R.string.illegal_argument_exception, locationName);
				// Log the error and print the stack trace
				Log.e(LocationUtils.APPTAG, errorString);
				exception2.printStackTrace();

				//
				// return errorString;
			}
			return addresses;
		}

		/**
		 * A method that's called once doInBackground() completes. Set the text of the UI element that displays the address. This method runs on the UI thread.
		 */
		@Override
		protected void onPostExecute(List<Address> addresses) {
			if(addresses==null){
				Utils.showMessageDialog(_context.getString(R.string.cannot_get_address_from_google), _context);
			}
			else if (addresses.size() != 0) {
				if(isPickup){
					Utils.mPickupAddress = addresses.get(0);
					boolean isPickup = false;
					if(dbBook.getDropoffAddress()!=null && dbBook.getDropoffAddress().length()>0)
						new GetAddressTask(_context, isPickup).execute(dbBook.getDropoffAddress());
					else{
						Utils.currentTab=0;
						finish();
					}
				}
				else{
					Utils.mDropoffAddress = addresses.get(0);
					Utils.currentTab=0;
					finish();
				}
			} else {
				Utils.showMessageDialog(_context.getString(R.string.cannot_get_address_from_google), _context);
				// Toast.makeText(_context, "invalid address", Toast.LENGTH_SHORT).show();
			}

		}
	}
}
