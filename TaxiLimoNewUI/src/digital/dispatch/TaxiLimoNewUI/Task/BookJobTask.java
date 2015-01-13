package digital.dispatch.TaxiLimoNewUI.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.digital.dispatch.TaxiLimoSoap.requests.BookJobRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.BookJobRequest.IBookJobResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.BookJobResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.JobIDListItem;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Book.BookActivity;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class BookJobTask extends AsyncTask<Void, Integer, Void> implements IBookJobResponseListener, IRequestTimerListener {
	private static final String TAG = null;
	BookJobRequest bjReq;
	DBBooking mbook;
	Context _context;

	public BookJobTask(Context context, DBBooking book) {
		_context = context;
		mbook = book;
	}

	// Before running code in separate thread
	@Override
	protected void onPreExecute() {
		Utils.showProcessingDialog(_context);
	}

	// The code to be executed in a background thread.
	@Override
	protected Void doInBackground(Void... params) {

		bjReq = new BookJobRequest(this, this);
		// sysid get from company list response
		bjReq.setSysID(mbook.getSysId() + "");
		bjReq.setReqType("1");

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
		String phone = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER);
		String userName = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_NAME);
		bjReq.setPhoneNum(phone);
		bjReq.setPassengerName(userName);
		bjReq.setNumOfPassenger("1");
		bjReq.setNumOfTaxi("1");
		bjReq.setAdviseArrival("1");
		bjReq.setForcedAddressFlag("Y");
		bjReq.setPriority("N");
		bjReq.setHardwareID(Utils.getHardWareId(_context));
		bjReq.setOSPVersion("3.0");

		String multiPay_allow = "";

		bjReq.setTaxiCompanyID(mbook.getDestID() + "");

		bjReq.setPickUpTime(mbook.getPickup_time());

		if (mbook.getPickup_unit() != null) {
			bjReq.setPickUpUnit(mbook.getPickup_unit());
		}

		bjReq.setPickupHouseNum(mbook.getPickup_house_number());
		bjReq.setPickUpStreetName(mbook.getPickup_street_name());
		bjReq.setPickUpDistrict(mbook.getPickup_district());
		bjReq.setPickUpLatitude(Double.toString(mbook.getPickup_latitude()));
		bjReq.setPickUpLongitude(Double.toString(mbook.getPickup_longitude()));
		bjReq.setZipCode(mbook.getPickup_zipCode());//TL-301 this is 

		if (mbook.getDropoff_unit() != null) {
			bjReq.setDropOffUnit(mbook.getDropoff_unit());
		}

		if (mbook.getDropoff_house_number() != null) {
			bjReq.setDropOffHouseNum(mbook.getDropoff_house_number());
		}

		if (mbook.getDropoff_street_name() != null) {
			bjReq.setDropOffStreetName(mbook.getDropoff_street_name());
		}

		if (mbook.getDropoff_district() != null) {
			bjReq.setDropOffDistrict(mbook.getDropoff_district());
		}

		if (Utils.selected_attribute!=null) {
			ArrayList<String> temp = new ArrayList<String>();
			for(int i=0;i<Utils.selected_attribute.size();i++){
				temp.add(Utils.selected_attribute.get(i).toString());
			}
			bjReq.setAttributeList(temp);
		}

		String remark = mbook.getRemarks();
		for (int i = 0; i < MBDefinition.SPECIAL_CHAR_TO_REMOVE.length; i++) {
			remark = remark.replace(MBDefinition.SPECIAL_CHAR_TO_REMOVE[i], "");
		}

		bjReq.setRemark(remark);

		bjReq.sendRequest(_context.getResources().getString(R.string.name_space), _context.getResources().getString(R.string.url));

		return null;
	}

	// Update the progress
	@Override
	protected void onProgressUpdate(Integer... values) {
		// set the current progress of the progress dialog
		// progressDialog.setProgress(values[0]);
	}

	@Override
	public void onResponseReady(BookJobResponse response) {
		Logger.v(TAG, "++BookingJob response");
		Utils.stopProcessingDialog(_context);
		
		if ( _context instanceof Activity ) {
		    Activity activity = (Activity)_context;
		    if ( activity.isFinishing() ) {
		        return;
		    }
		}
		
		
		JobIDListItem[] jobResp = response.GetList();

		Logger.v(TAG, "**Book Job response =" + jobResp.length);

		if (jobResp.length > 0) {
			Logger.v(TAG, "Taxi ride ID = " + response.getTRID());
			mbook.setTaxi_ride_id(response.getTRID());
			mbook.setTripStatus(MBDefinition.MB_STATUS_BOOKED);

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat createTimeFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.US);
			mbook.setTripCreationTime(createTimeFormat.format(cal.getTime()));
			mbook.setAlready_paid(false);

			DaoManager daoManager = DaoManager.getInstance(_context);
			DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
			bookingDao.insert(mbook);
			// clean up
			Utils.driverNoteString = "";
			Utils.pickupDate = null;
			Utils.pickupTime = null;
			Utils.mSelectedCompany = null;
			Utils.mDropoffAddress = null;
			Utils.mPickupAddress = null;
			Utils.pickupHouseNumber = "";
			Utils.last_city = "";
			Utils.selected_attribute = null;
			Utils.pickup_unit_number = null;
			Utils.dropoff_unit_number = null;

			//Utils.showMessageDialog(_context.getString(R.string.message_book_successful), _context);
			Utils.currentTab = 1;
			((BookActivity) _context).showBookSuccessDialog();
			Logger.d(TAG, "ride id: " + mbook.getTaxi_ride_id());
			Logger.d(TAG, "create time: " + mbook.getTripCreationTime());
		} else {
			Logger.e(TAG, "no response found");
		}

		// bookResponseDialog(MBDefinition.MB_BOOK_SUCCESS);
	}

	@Override
	public void onErrorResponse(ResponseWrapper resWrapper) {
		Utils.stopProcessingDialog(_context);
		
		if ( _context instanceof Activity ) {
		    Activity activity = (Activity)_context;
		    if ( activity.isFinishing() ) {
		        return;
		    }
		}

		if (resWrapper.getStatus() == 3) {
			// new AlertDialog.Builder(getActivity())
			// .setTitle(R.string.booking_failed_title)
			// .setMessage(R.string.err_msg_book_req_area_not_bookable)
			// .setPositiveButton(R.string.positive_button, null)
			// .create()
			// .show();
			Utils.showMessageDialog(_context.getString(R.string.err_msg_book_req_area_not_bookable), _context);

		} else {
			switch (resWrapper.getErrCode()) {
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 39:
				Utils.showMessageDialog(_context.getString(R.string.err_msg_book_req_area_not_bookable), _context);
				break;
			case 23:
				Utils.showMessageDialog(_context.getString(R.string.err_msg_book_req_invalid_account), _context);
				break;
			case 36:
			case 40:
				Utils.showMessageDialog(_context.getString(R.string.err_msg_book_req_account_err).replace("[company phone]", mbook.getCompany_phone_number()),
						_context);
				break;
			default:
				Utils.showMessageDialog(_context.getString(R.string.booking_failed_generic_msg).replace("[company phone]", mbook.getCompany_phone_number()),
						_context);

				break;
			}
		}

	}

	@Override
	public void onError() {
		Utils.stopProcessingDialog(_context);
		
		if ( _context instanceof Activity ) {
		    Activity activity = (Activity)_context;
		    if ( activity.isFinishing() ) {
		        return;
		    }
		}
		// alertMsgWithCallOption(R.string.booking_failed_title, R.string.booking_failed_generic_msg);
		Utils.showMessageDialog(_context.getString(R.string.err_msg_book_req_area_not_bookable).replace("[company phone]", mbook.getCompany_phone_number()),
				_context);
		;
	}

	@Override
	public void onProgressUpdate(int progress) {

	}
}
