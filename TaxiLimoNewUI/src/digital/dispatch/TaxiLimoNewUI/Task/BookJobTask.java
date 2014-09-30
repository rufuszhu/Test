package digital.dispatch.TaxiLimoNewUI.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.format.Time;

import com.digital.dispatch.TaxiLimoSoap.requests.BookJobRequest;
import com.digital.dispatch.TaxiLimoSoap.requests.BookJobRequest.IBookJobResponseListener;
import com.digital.dispatch.TaxiLimoSoap.requests.Request.IRequestTimerListener;
import com.digital.dispatch.TaxiLimoSoap.responses.BookJobResponse;
import com.digital.dispatch.TaxiLimoSoap.responses.JobIDListItem;
import com.digital.dispatch.TaxiLimoSoap.responses.ResponseWrapper;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.Installation;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Book.AttributeActivity;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Track.TrackingMapActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
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

		bjReq.setPhoneNum("7788594684");
		bjReq.setPassengerName("Rufus Zhu");
		bjReq.setNumOfPassenger("1");
		bjReq.setNumOfTaxi("1");
		bjReq.setAdviseArrival("1");
		bjReq.setForcedAddressFlag("Y");
		bjReq.setPriority("N");
		bjReq.setHardwareID(Installation.id(_context));
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

		// set attributes (dynamic)
		// if (mAttrRcd != null) {
		// List<String> attrList = new ArrayList<String>();
		// for (int i = 0; i < mAttrRcd.length; i++) {
		// if (mAttrRcd[i].checked == true ) {
		// attrList.add(mAttrRcd[i].shortName);
		// }
		// }
		//
		// bjReq.setAttributeList(attrList);
		// }

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
		JobIDListItem[] jobResp = response.GetList();

		Logger.v(TAG, "**Book Job response =" + jobResp.length);

		if (jobResp.length > 0) {
			Logger.v(TAG, "Taxi ride ID = " + response.getTRID());
			mbook.setTaxi_ride_id(response.getTRID());
			mbook.setTripStatus(MBDefinition.MB_STATUS_BOOKED);
			
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat pickupTimeFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss",Locale.US);
			mbook.setTripCreationTime(pickupTimeFormat.format(cal.getTime()));
			
			DaoManager daoManager = DaoManager.getInstance(_context);
			DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
			bookingDao.insert(mbook);
			//clean up
			Utils.driverNoteString = "";
			Utils.pickupDate = null;
			Utils.pickupTime = null;
			Utils.mSelectedCompany = null;
			Utils.mDropoffAddress = null;
			Utils.mPickupAddress = null;
			Utils.pickupHouseNumber="";
			Utils.showMessageDialog(_context.getString(R.string.message_book_successful), _context);
			((MainActivity)_context).switchToTrackTab();
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
				// new AlertDialog.Builder(getActivity())
				// .setTitle(R.string.booking_failed_title)
				// .setMessage(R.string.err_msg_book_req_area_not_bookable)
				// .setPositiveButton(R.string.positive_button, null)
				// .create()
				// .show();
				// break;
				Utils.showMessageDialog(_context.getString(R.string.err_msg_book_req_area_not_bookable).replace("[company phone]", mbook.getCompany_phone_number()), _context);
				break;
			case 23:
				// new AlertDialog.Builder(getActivity())
				// .setTitle(R.string.booking_failed_title)
				// .setMessage(R.string.err_msg_book_req_invalid_account)
				// .setPositiveButton(R.string.cancel_yes, new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog,int id) {
				// UserAccount.setAccountNum(getActivity(), "");
				// UserAccount.setPassword(getActivity(), "");
				// llBookByAccount.setVisibility(View.GONE);
				// }
				// })
				// .setNegativeButton(R.string.cancel_no, null)
				// .create()
				// .show();
				// break;
				Utils.showMessageDialog(_context.getString(R.string.err_msg_book_req_area_not_bookable).replace("[company phone]", mbook.getCompany_phone_number()), _context);
				break;
			case 36:
			case 40:
				// new AlertDialog.Builder(getActivity())
				// .setTitle(R.string.booking_failed_title)
				// .setMessage(R.string.err_msg_book_req_account_err)
				// .setPositiveButton(R.string.positive_button, null)
				// .create()
				// .show();
				// break;
				break;
			default:
				// alertMsgWithCallOption(R.string.booking_failed_title, R.string.booking_failed_generic_msg);
				Utils.showMessageDialog(_context.getString(R.string.err_msg_book_req_area_not_bookable).replace("[company phone]", mbook.getCompany_phone_number()), _context);

				break;
			}
		}

	}

	@Override
	public void onError() {
		Utils.stopProcessingDialog(_context);
		// alertMsgWithCallOption(R.string.booking_failed_title, R.string.booking_failed_generic_msg);
		Utils.showMessageDialog(_context.getString(R.string.err_msg_book_req_area_not_bookable).replace("[company phone]", mbook.getCompany_phone_number()), _context);;
	}

	@Override
	public void onProgressUpdate(int progress) {

	}
}
