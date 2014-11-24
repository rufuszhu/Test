package digital.dispatch.TaxiLimoNewUI.Book;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao.Properties;
import digital.dispatch.TaxiLimoNewUI.DaoManager.AddressDaoManager;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.R.color;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.R.string;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BookActivity extends Activity {
	private static final String TAG = "BookActivity";
	private Context _this;
	private RelativeLayout rl_pick_up, rl_drop_off, rl_date, rl_driver_note, rl_company;
	private TextView tv_pick_up, tv_drop_off, tv_date, tv_driver_note, tv_company, book_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book);
		_this = this;
		findView();
		bindEvent();
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
		checkGPSEnable();
		checkInternet();
		setPickupIfExist();
		setDropOffIfExist();
	}

	private void setDropOffIfExist() {
		if (Utils.mDropoffAddress != null) {
			tv_drop_off.setTextColor(_this.getResources().getColor(R.color.black));
			tv_drop_off.setText(LocationUtils.addressToString(_this, Utils.mDropoffAddress));
		} else {
			tv_drop_off.setTextColor(_this.getResources().getColor(R.color.gray_light));
			tv_drop_off.setText(_this.getResources().getString(R.string.empty_note));
		}
	}

	private void setPickupIfExist() {
		if(Utils.mPickupAddress!=null){
			
			tv_pick_up.setText(LocationUtils.addressToString(_this, Utils.mPickupAddress));
		}
	}

	private void bindEvent() {
		rl_pick_up.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(_this, ModifyAddressActivity.class);

				intent.putExtra(MBDefinition.ADDRESSBAR_TEXT_EXTRA, tv_pick_up.getText().toString());

				intent.putExtra(MBDefinition.IS_DESTINATION, false);
				_this.startActivity(intent);
			}
		});

		rl_drop_off.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(_this, ModifyAddressActivity.class);
				intent.putExtra(MBDefinition.IS_DESTINATION, true);
				_this.startActivity(intent);
			}
		});

		rl_driver_note.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// setUpMessageDialog();
				Dialog messageDialog = new Dialog(_this);

				Utils.setUpDriverNoteDialog(_this, messageDialog, tv_driver_note);

			}
		});

		rl_date.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TimePickerDialog.timepick(_this, 1, 20);
				// TimePickerDialog tpd = new TimePickerDialog(_this, null, 10, 10, true);
				// tpd.show();
				Utils.setUpTimeDialog(_this, tv_date);
			}
		});

		rl_company.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (Utils.mPickupAddress != null) {
					Intent intent = new Intent(_this, AttributeActivity.class);
					intent.putExtra(MBDefinition.EXTRA_SHOULD_BOOK_RIGHT_AFTER, false);
					startActivity(intent);
					// _this.startActivityForResult(intent, MBDefinition.REQUEST_COMPANYITEM_CODE);
				}
			}
		});

		book_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (checkAllowBooking()) {
					Utils.bookJob(Utils.mSelectedCompany, _this);
				}

			}
		});

	}

	private void findView() {
		rl_pick_up = (RelativeLayout) findViewById(R.id.rl_pickup);
		rl_drop_off = (RelativeLayout) findViewById(R.id.rl_drop_off);
		rl_date = (RelativeLayout) findViewById(R.id.rl_date);
		rl_driver_note = (RelativeLayout) findViewById(R.id.rl_driver_note);
		rl_company = (RelativeLayout) findViewById(R.id.rl_company);

		tv_pick_up = (TextView) findViewById(R.id.tv_pickup);
		tv_drop_off = (TextView) findViewById(R.id.tv_drop_off);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_driver_note = (TextView) findViewById(R.id.tv_driver_note);
		tv_company = (TextView) findViewById(R.id.tv_company);
		book_btn = (TextView) findViewById(R.id.book_button);
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void setTimeText(TextView tv_time) {
		if (Utils.pickupDate == null || Utils.pickupTime == null) {
			tv_time.setText(_this.getResources().getString(R.string.now));
			tv_time.setTextSize(20);
			tv_time.setTextColor(_this.getResources().getColor(R.color.gray_light));
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd", Locale.US);
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh: mm a", Locale.US);
			String date = dateFormat.format(Utils.pickupDate);
			String time = timeFormat.format(Utils.pickupTime);
			Calendar cal = Calendar.getInstance();

			if (Utils.pickupDate.getDate() == cal.get(Calendar.DATE)) {
				tv_time.setText("Today" + "\n" + time);
			} else
				tv_time.setText(date + "\n" + time);
			tv_time.setTextSize(13);
			tv_time.setTextColor(_this.getResources().getColor(R.color.black));
		}
	}

	private void checkGPSEnable() {
		final LocationManager manager = (LocationManager) _this.getSystemService(Context.LOCATION_SERVICE);

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
		}
	}

	private void checkInternet() {
		Utils.isInternetAvailable(_this);
	}

	// helper function before booking
	private boolean checkAllowBooking() {

		DaoManager daoManager = DaoManager.getInstance(_this);
		DBBookingDao bookingDao = daoManager.getDBBookingDao(DaoManager.TYPE_WRITE);
		CompanyItem selectedCompany = Utils.mSelectedCompany;
		List<DBBooking> activeBookingList = bookingDao.queryBuilder()
				.where(Properties.TripStatus.notEq(MBDefinition.MB_STATUS_CANCELLED), Properties.TripStatus.notEq(MBDefinition.MB_STATUS_COMPLETED)).list();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_this);

		if (Utils.mPickupAddress == null || Utils.mPickupAddress.equals("")) {
			Utils.showMessageDialog(_this.getString(R.string.err_no_pickup_address), _this);
			return false;
		}
		// no taxi company found in current city
		// if (noCompanyAvailable) {
		// Utils.showMessageDialog(_this.getString(R.string.err_no_company_found) + " " + Utils.mPickupAddress.getLocality(), _this);
		// return false;
		// }
		// no house number in pickup address
		if (!validateHasHouseNumber()) {
			showEnterHouseNumberDialog();
			return false;
		}
		// house number return by Google has a range
		if (!validateHouseNumberHasNoRange()) {
			Address address = Utils.mPickupAddress;
			String[] houseNumberRange = TextUtils.split(AddressDaoManager.getHouseNumberFromAddress(address), "-");
			showHouseRangeDialog(houseNumberRange[0], houseNumberRange[1]);
			return false;
		}
		// check if allow multiple booking
		if (activeBookingList.size() > 0 && !SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, MBDefinition.SHARE_MULTI_BOOK_ALLOWED, false)) {
			Utils.showErrorDialog(_this.getString(R.string.err_no_multiple_booking), _this);
			return false;

		}

		// check if allow duplicate booking TL-23
		if (activeBookingList.size() > 0
				&& !SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, MBDefinition.SHARE_SAME_LOG_BOOK_ALLOWED, false)) {

			for (int i = 0; i < activeBookingList.size(); i++) {
				DBBooking booking = activeBookingList.get(i);

				if (Utils.compareAddr(booking)) {
					// both ASAP JOB then it's duplicate
					if (booking.getPickup_time() == null && (Utils.pickupDate == null || Utils.pickupTime == null)) {

						Utils.showErrorDialog(_this.getString(R.string.err_msg_no_same_loc), _this);
						return false;

					}
					// both future job then need to check how far apart
					else if (booking.getPickup_time() != null && Utils.pickupDate != null && Utils.pickupTime != null) {

						if (checkDuplicatePickupTime(booking)) {
							Utils.showErrorDialog(_this.getString(R.string.err_msg_no_same_loc), _this);
							return false;
						}

					}
				}

			}

		}
		// check if drop off MANDATORY
		if (SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, MBDefinition.SHARE_DROP_OFF_MANDATORY, false) && Utils.mDropoffAddress == null) {
			Utils.showErrorDialog(_this.getString(R.string.err_dropoff_mandatory), _this);
			return false;
		}
		// no selected company
		if (selectedCompany == null) {
			// get into select company page
			Intent intent = new Intent(_this, AttributeActivity.class);
			intent.putExtra(MBDefinition.EXTRA_SHOULD_BOOK_RIGHT_AFTER, true);
			_this.startActivity(intent);
			return false;
		}
		return true;
	}

	// return true if the book job does not fall within the duplicate job time frame with another active job.
	private boolean checkDuplicatePickupTime(DBBooking booking) {

		try {
			SimpleDateFormat checkFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.US);
			Date checkDate = (Date) checkFormat.parse(booking.getPickup_time());
			long checkingTime = checkDate.getTime();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm:ss", Locale.US);

			String date = dateFormat.format(Utils.pickupDate);
			String time = timeFormat.format(Utils.pickupTime);

			Date bookingDate = (Date) checkFormat.parse(date + " " + time);
			long bookingTime = bookingDate.getTime();

			long bufferTime = 0;
			if (Utils.mSelectedCompany.dupChkTime.isEmpty() || Utils.mSelectedCompany.dupChkTime.equalsIgnoreCase("0")) {
				bufferTime = Long.parseLong(_this.getString(R.string.duplicate_check_buffer_time)); // default
			} else {
				bufferTime = Long.parseLong(Utils.mSelectedCompany.dupChkTime);
			}
			// within the duplicate job time frame then consider duplicate job
			if (Math.abs(bookingTime - checkingTime) > 60000 * bufferTime) {

				return false;

			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean validateHasHouseNumber() {
		Address address = Utils.mPickupAddress;
		if (address == null)
			return false;
		String ad = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";
		String[] strArray = TextUtils.split(ad, " ");

		if (!Utils.isNumeric(strArray[0]) && Utils.pickupHouseNumber.equals("")) {

			return false;
		} else
			return true;
	}

	private boolean validateHouseNumberHasNoRange() {
		Address address = Utils.mPickupAddress;
		String ad = AddressDaoManager.getHouseNumberFromAddress(address);
		if (ad.contains("-") && Utils.pickupHouseNumber.equals("")) {
			return false;
		} else
			return true;
	}

	private void showEnterHouseNumberDialog() {
		final Dialog enterHouseNumDialog = new Dialog(_this);
		enterHouseNumDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		enterHouseNumDialog.setContentView(R.layout.dialog_enter_street_number);
		enterHouseNumDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		TextView tv_house_num = (TextView) enterHouseNumDialog.getWindow().findViewById(R.id.tv_street_address);
		final EditText et_house_num = (EditText) enterHouseNumDialog.getWindow().findViewById(R.id.et_street_number);
		TextView ok = (TextView) enterHouseNumDialog.getWindow().findViewById(R.id.ok);
		TextView cancel = (TextView) enterHouseNumDialog.getWindow().findViewById(R.id.cancel);

		tv_house_num.setText(LocationUtils.addressToString(_this, Utils.mPickupAddress));
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (et_house_num.getText().toString().equals("")) {
					et_house_num.setError("Pleace enter a street number for pick up");
				} else if (et_house_num.getText().toString().length() > MBDefinition.STREET_NUMBER_MAX_LENGTH)
					et_house_num.setError("Street number too long");
				else {
					Utils.pickupHouseNumber = et_house_num.getText().toString();
					enterHouseNumDialog.dismiss();
					book_btn.callOnClick();
				}
			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				enterHouseNumDialog.dismiss();
			}
		});
		enterHouseNumDialog.setCanceledOnTouchOutside(true);
		enterHouseNumDialog.show();
	}

	private void showHouseRangeDialog(final String start, final String end) {
		final Dialog houseNumRangeDialog = new Dialog(_this);
		houseNumRangeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		houseNumRangeDialog.setContentView(R.layout.dialog_enter_street_number_range);
		houseNumRangeDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		TextView tv_range_message = (TextView) houseNumRangeDialog.getWindow().findViewById(R.id.tv_range_message);
		final EditText et_house_num = (EditText) houseNumRangeDialog.getWindow().findViewById(R.id.et_street_number);
		TextView ok = (TextView) houseNumRangeDialog.getWindow().findViewById(R.id.ok);
		TextView cancel = (TextView) houseNumRangeDialog.getWindow().findViewById(R.id.cancel);

		tv_range_message.setText(start + " and " + end);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (et_house_num.getText().toString().equals("")) {
					et_house_num.setError("Pleace enter a street number for pick up");
				} else if (Integer.parseInt(et_house_num.getText().toString()) > Integer.parseInt(end)
						|| Integer.parseInt(et_house_num.getText().toString()) < Integer.parseInt(start))
					et_house_num.setError("Street number not in range");
				else {
					Utils.pickupHouseNumber = et_house_num.getText().toString();
					houseNumRangeDialog.dismiss();
					book_btn.callOnClick();
				}
			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				houseNumRangeDialog.dismiss();
			}
		});
		houseNumRangeDialog.setCanceledOnTouchOutside(true);
		houseNumRangeDialog.show();
	}

}
