package digital.dispatch.TaxiLimoNewUI.History;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadImageTask;
import digital.dispatch.TaxiLimoNewUI.Utils.AppController;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class TripDetailActivity extends BaseActivity {

	private static final String TAG = "TripDetailActivity";
	private DBBooking dbBook;
	private Context _context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_detail);
		_context = this;
		Typeface icon_pack = Typeface.createFromAsset(getAssets(), "fonts/icon_pack.ttf");
		Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
		Typeface rionaSansBold = Typeface.createFromAsset(getAssets(), "fonts/RionaSansBold.otf");
		Typeface rionaSansRegular = Typeface.createFromAsset(getAssets(), "fonts/RionaSansRegular.otf");
		Typeface exo2Light = Typeface.createFromAsset(getAssets(), "fonts/Exo2-Light.ttf");
		Typeface exo2SemiBold = Typeface.createFromAsset(getAssets(), "fonts/Exo2-SemiBold.ttf");
		Typeface rionaSansMedium = Typeface.createFromAsset(getAssets(), "fonts/RionaSansMedium.otf");
		
		dbBook = (DBBooking) getIntent().getSerializableExtra(MBDefinition.DBBOOKING_EXTRA);
		TextView tv_trip_status = (TextView) findViewById(R.id.tv_trip_status);
		TextView tv_id = (TextView) findViewById(R.id.tv_history_id);
		TextView tv_receive = (TextView) findViewById(R.id.tv_history_receive);
		TextView tv_from = (TextView) findViewById(R.id.tv_pickup_address);
		TextView tv_to = (TextView) findViewById(R.id.tv_dropoff_address);
		NetworkImageView iv_company_icon = (NetworkImageView) findViewById(R.id.iv_tracking_company_icon);
		LinearLayout ll_attr = (LinearLayout) findViewById(R.id.ll_attr);
		TextView tv_company_name =(TextView) findViewById(R.id.tv_company_name);
		TextView tv_company_description = (TextView) findViewById(R.id.tv_company_description);
		TextView tv_driver = (TextView) findViewById(R.id.tv_driver);
		TextView tv_car_num = (TextView) findViewById(R.id.tv_car_num);
		TextView tv_call = (TextView) findViewById(R.id.tv_call);
		TextView icon_call = (TextView) findViewById(R.id.icon_call);
		TextView status_icon = (TextView) findViewById(R.id.status_icon);
		TextView icon_pickup = (TextView) findViewById(R.id.icon_pickup);
		TextView icon_dropoff = (TextView) findViewById(R.id.icon_dropoff);
		
		TextView route_title = (TextView) findViewById(R.id.route_title);
		TextView company_title = (TextView) findViewById(R.id.company_title);
		
		route_title.setTypeface(rionaSansBold);
		company_title.setTypeface(rionaSansBold);
		
		tv_trip_status.setTypeface(exo2SemiBold);
		tv_id.setTypeface(rionaSansBold);
		tv_receive.setTypeface(rionaSansRegular);
		tv_from.setTypeface(rionaSansMedium);
		tv_to.setTypeface(rionaSansMedium);
		
		tv_company_name.setTypeface(exo2SemiBold,Typeface.BOLD);
		tv_company_description.setTypeface(exo2Light, Typeface.ITALIC);
		tv_driver.setTypeface(exo2SemiBold,Typeface.BOLD);
		tv_car_num.setTypeface(exo2SemiBold,Typeface.BOLD);
		tv_call.setTypeface(exo2SemiBold,Typeface.BOLD);
		
		icon_call.setTypeface(icon_pack);
		icon_call.setText(MBDefinition.icon_phone);
		status_icon.setTypeface(icon_pack);
		icon_dropoff.setTypeface(icon_pack);
		icon_pickup.setTypeface(fontawesome);
		
		icon_pickup.setText(MBDefinition.ICON_PERSON);
		icon_dropoff.setText(MBDefinition.icon_location);
		
		setUpListener();

		tv_driver.setText("Driver # " + dbBook.getDispatchedDriver());
		tv_car_num.setText("Car # " + dbBook.getDispatchedCar());
		
		
		tv_id.setText("Trip ID " + dbBook.getTaxi_ride_id());
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
			Date date = format.parse(dbBook.getTripCreationTime());
			SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm MMM dd, yyyy", Locale.US);
			String dateString = dateFormat.format(date);
			tv_receive.setText("Received at " + dateString);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(dbBook.getPickup_unit()!=null && !dbBook.getPickup_unit().equals("")){
			tv_from.setText(dbBook.getPickup_unit()+"-"+dbBook.getPickupAddress());
		}
		else
			tv_from.setText(dbBook.getPickupAddress());
		
		//It is safe to assume that the job in this activity is either completed or cancel
		if(dbBook.getTripStatus()==MBDefinition.MB_STATUS_COMPLETED){
			tv_trip_status.setText("Trip Completed");
			status_icon.setText(MBDefinition.ICON_CHECK_CIRCLE);
		}
		else{
			tv_trip_status.setText("Trip Canceled");
			status_icon.setText(MBDefinition.ICON_CROSS_CIRCLE);
		}
		
		if (dbBook.getDropoffAddress() != null && dbBook.getDropoffAddress().length() > 0){
			if(dbBook.getDropoff_unit()!=null && !dbBook.getDropoff_unit().equals("")){
				tv_to.setText(dbBook.getDropoff_unit()+"-"+dbBook.getDropoffAddress());
			}
			else
				tv_to.setText(dbBook.getDropoffAddress());
		}
		else
			tv_to.setText("Not Given");
		

		String prefixURL = getResources().getString(R.string.url);
		prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
		iv_company_icon.setDefaultImageResId(R.drawable.launcher);
		iv_company_icon.setImageUrl(prefixURL + dbBook.getCompany_icon(), AppController.getInstance().getImageLoader());

		tv_company_name.setText(dbBook.getCompany_name());
		tv_company_description.setText(dbBook.getCompany_description());
		
		if (dbBook.getAttributeList() != null){
			int margin_right = 10;
			Utils.showOption(ll_attr, dbBook.getAttributeList().split(","), this, margin_right);
		}
		
	}
	
	private void setUpListener() {
		LinearLayout call_btn = (LinearLayout) findViewById(R.id.call_company_btn);
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
