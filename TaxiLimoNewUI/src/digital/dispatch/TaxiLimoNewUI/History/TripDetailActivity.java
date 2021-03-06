package digital.dispatch.TaxiLimoNewUI.History;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.Book.BookActivity;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities.gcmType;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.AppController;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.GecoderGoogle;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class TripDetailActivity extends BaseActivity {

	private static final String TAG = "TripDetailActivity";
	private boolean logEnabled = false;
	private DBBooking dbBook;
	private Context _context;
	private BroadcastReceiver bcReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_detail);
		_context = this;
        setToolBar();
		Typeface icon_pack = FontCache.getFont(this, "fonts/icon_pack.ttf");
		Typeface fontawesome = FontCache.getFont(this, "fonts/fontawesome.ttf");
		Typeface OpenSansBold = FontCache.getFont(this, "fonts/OpenSansBold.ttf");
		Typeface OpenSansRegular = FontCache.getFont(this, "fonts/OpenSansRegular.ttf");
		Typeface exo2SemiBold = FontCache.getFont(this, "fonts/Exo2-SemiBold.ttf");
		Typeface exo2Bold = FontCache.getFont(this, "fonts/Exo2-Bold.ttf");
		Typeface OpenSansSemibold = FontCache.getFont(this, "fonts/OpenSansSemibold.ttf");
		
		dbBook = (DBBooking) getIntent().getSerializableExtra(MBDefinition.DBBOOKING_EXTRA);
		TextView tv_trip_status = (TextView) findViewById(R.id.tv_trip_status);
		TextView tv_id = (TextView) findViewById(R.id.tv_history_id);
		TextView tv_receive = (TextView) findViewById(R.id.tv_history_receive);
		TextView tv_from = (TextView) findViewById(R.id.tv_pickup_address);
		TextView tv_to = (TextView) findViewById(R.id.tv_dropoff_address);
        TextView tv_pickup_time = (TextView) findViewById(R.id.tv_pickup_time);
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
        TextView icon_calander = (TextView) findViewById(R.id.icon_calander);
		
		TextView itineary_title = (TextView) findViewById(R.id.itinerary_title);
		TextView company_title = (TextView) findViewById(R.id.company_title);
		
		
		TextView delete_icon = (TextView) findViewById(R.id.delete_icon);
		TextView bookAgain_icon = (TextView) findViewById(R.id.bookAgain_icon);
		TextView tv_delete = (TextView) findViewById(R.id.tv_delete);
		TextView tv_bookAgain = (TextView) findViewById(R.id.tv_bookAgain);

        ImageView icon_taxi = (ImageView) findViewById(R.id.icon_taxi);
        View space_diver_car = (View) findViewById(R.id.space_diver_car);
        TextView icon_male_user = (TextView) findViewById(R.id.icon_male_user);


		
		delete_icon.setTypeface(icon_pack);
		bookAgain_icon.setTypeface(icon_pack);
		delete_icon.setText(MBDefinition.ICON_CROSS);
		bookAgain_icon.setText(MBDefinition.ICON_BOOK_AGAIN);
		tv_delete.setTypeface(exo2Bold);
		tv_bookAgain.setTypeface(exo2Bold);
		
		itineary_title.setTypeface(OpenSansBold);
		company_title.setTypeface(OpenSansBold);
		
		tv_trip_status.setTypeface(exo2SemiBold);
		tv_id.setTypeface(OpenSansBold);
		tv_receive.setTypeface(OpenSansRegular);
		tv_from.setTypeface(OpenSansSemibold);
		tv_to.setTypeface(OpenSansSemibold);
        tv_pickup_time.setTypeface(OpenSansSemibold);
		
		tv_company_name.setTypeface(OpenSansSemibold, Typeface.NORMAL);
		tv_company_description.setTypeface(OpenSansSemibold, Typeface.ITALIC);
		tv_driver.setTypeface(OpenSansSemibold,Typeface.NORMAL);
		tv_car_num.setTypeface(OpenSansSemibold,Typeface.NORMAL);
		tv_call.setTypeface(OpenSansSemibold,Typeface.NORMAL);

        icon_male_user.setTypeface(icon_pack);
        icon_male_user.setText(MBDefinition.ICON_MALE_USER);
		
		icon_call.setTypeface(icon_pack);
        icon_calander.setTypeface(icon_pack);
		icon_call.setText(MBDefinition.icon_phone);
		status_icon.setTypeface(icon_pack);
		icon_dropoff.setTypeface(icon_pack);
		icon_pickup.setTypeface(fontawesome);
        icon_calander.setTypeface(icon_pack);

		icon_pickup.setText(MBDefinition.ICON_PERSON);
		icon_dropoff.setText(MBDefinition.icon_location);
        icon_calander.setText(MBDefinition.icon_tab_calendar);
		
		setUpListener();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        //else if only driver is available
        if ((dbBook.getDispatchedDriver() != null && !dbBook.getDispatchedDriver().equals("")) &&  (dbBook.getDispatchedCar() == null || dbBook.getDispatchedCar().length() == 0)){
            linearLayout.setVisibility(View.VISIBLE);
            icon_taxi.setVisibility(View.GONE);
            space_diver_car.setVisibility(View.GONE);
            icon_male_user.setVisibility(View.VISIBLE);
            tv_driver.setVisibility(View.VISIBLE);
            tv_car_num.setVisibility(View.GONE);
            tv_driver.setText(dbBook.getDispatchedDriver());
        }
        //else if only car is available
        else if((dbBook.getDispatchedDriver() == null || dbBook.getDispatchedDriver().equals("")) && (dbBook.getDispatchedCar() != null && dbBook.getDispatchedCar().length() > 0)){
            linearLayout.setVisibility(View.VISIBLE);
            icon_taxi.setVisibility(View.VISIBLE);
            space_diver_car.setVisibility(View.GONE);
            icon_male_user.setVisibility(View.GONE);
            tv_driver.setVisibility(View.GONE);
            tv_car_num.setVisibility(View.VISIBLE);
            tv_car_num.setText(dbBook.getDispatchedCar());
        }
        //both available
        else if((dbBook.getDispatchedDriver() != null && dbBook.getDispatchedDriver().length()>0) && (dbBook.getDispatchedCar() != null && dbBook.getDispatchedCar().length() > 0)){
            linearLayout.setVisibility(View.VISIBLE);
            icon_taxi.setVisibility(View.VISIBLE);
            space_diver_car.setVisibility(View.VISIBLE);
            icon_male_user.setVisibility(View.VISIBLE);
            tv_driver.setVisibility(View.VISIBLE);
            tv_car_num.setVisibility(View.VISIBLE);
            tv_driver.setText(dbBook.getDispatchedDriver());
            tv_car_num.setText(dbBook.getDispatchedCar());
        }

		
		
		tv_id.setText("Trip ID " + dbBook.getTaxi_ride_id());
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
			Date date = format.parse(dbBook.getTripCreationTime());
			SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a MMM dd, yyyy", Locale.US);
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
			tv_trip_status.setText(getString(R.string.trip_completed));
			status_icon.setText(MBDefinition.ICON_CHECK_CIRCLE);
		}
		else{
			tv_trip_status.setText(getString(R.string.trip_cancled));
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
			tv_to.setText("Destination Not Given");

        try {
            if (dbBook.getPickup_time() != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Date date = format.parse(dbBook.getPickup_time());
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a MMM dd, yyyy", Locale.US);
                String dateString = dateFormat.format(date);
                tv_pickup_time.setText(dateString);
            } else
                tv_pickup_time.setText("As soon as possible");
        } catch (ParseException e) {
            e.printStackTrace();
        }
		

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

    private void setToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        Typeface face = FontCache.getFont(_context, "fonts/Exo2-Light.ttf");
        TextView yourTextView = Utils.getToolbarTitleView(this, toolbar);
        yourTextView.setTypeface(face);
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
		//TL-235
		boolean isTrackDetail = false;
		bcReceiver = CommonUtilities.getGenericReceiver(_context, isTrackDetail);
		LocalBroadcastManager.getInstance(this).registerReceiver(bcReceiver, new IntentFilter(gcmType.message.toString()));
		super.onResume();
		Logger.d(TAG, "on RESUME");
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(bcReceiver);
		super.onPause();
		Logger.d(TAG, "on PAUSE");
		
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
			

			// Get the current location from the input parameter list
			String locationName = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;

			// Try to get an address for the current location. Catch IO or network problems.
			try {
				/*
				 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform to address standards can also be used.
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
				
				//try HTTP lookup to the maps API					
				GecoderGoogle mGecoderGoogle = new GecoderGoogle(localContext, Locale.getDefault(), logEnabled);
			
				try{
					addresses = mGecoderGoogle.getFromLocationName(locationName, 10);
				}	
				catch (IOException exception1) {

					// Log an error and return an error message
					Logger.e(TAG, getString(R.string.IO_Exception_getFromLocation));
	
					// print the stack trace
					exception1.printStackTrace();

				// Catch incorrect latitude or longitude values
				} catch (IllegalArgumentException exception2) {
	
					// Construct a message containing the invalid arguments
					String errorString = getString(R.string.illegal_argument_exception, locationName);
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
		protected void onPostExecute(List<Address> addresses) {
				
			if(addresses==null){
				Utils.showMessageDialog(_context.getString(R.string.cannot_get_address_from_google), _context);
			}
			else if (addresses.size() != 0) {
				if(isPickup){
                    Utils.pickupHouseNumber = "";
					Utils.mPickupAddress = addresses.get(0);
					boolean isPickup = false;
					if(dbBook.getDropoffAddress()!=null && dbBook.getDropoffAddress().length()>0)
						new GetAddressTask(_context, isPickup).execute(dbBook.getDropoffAddress());
					else{
                        Utils.mDropoffAddress = null;
						setUpCompanyAndGoToBook();
					}
				}
				else{
					//TL-300 add DropOff address if exist
					Utils.mDropoffAddress = addresses.get(0);
					setUpCompanyAndGoToBook();
				}
			} else {
				Utils.showMessageDialog(_context.getString(R.string.cannot_get_address_from_google), _context);
				// Toast.makeText(_context, "invalid address", Toast.LENGTH_SHORT).show();
			}
		
		}
	}
	
	private void setUpCompanyAndGoToBook(){
		CompanyItem company = new CompanyItem();
		company.description = dbBook.getCompany_description();
		company.attributes = dbBook.getCompany_attribute_list();
		company.logo = dbBook.getCompany_icon();
		company.name = dbBook.getCompany_name();
		company.phoneNr = dbBook.getCompany_phone_number();
		company.destID = dbBook.getDestID();
		company.systemID = Integer.parseInt(dbBook.getSysId());
		company.baseRate = dbBook.getCompany_baseRate();
		company.ratePerDistance = dbBook.getCompany_rate_PerDistance();
		
		Utils.mSelectedCompany = company;
		String[] stringList = dbBook.getAttributeList().split(",");
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(int i = 0 ; i < stringList.length; i++){
			int attr = -1;
			try{
				attr = Integer.parseInt(stringList[i]);
			}
			catch (NumberFormatException e){
				Logger.e(TAG,"No valid attribute found, e: " + e.getMessage());
			}
			if(attr!=-1)
				temp.add(attr);
		}
		Utils.selected_attribute = temp;
		Utils.last_city = dbBook.getPickup_district();
		Intent intent = new Intent(_context, BookActivity.class);
		((TripDetailActivity) _context).startActivityForAnim(intent);
		finish();
	}
}
