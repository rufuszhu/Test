//package digital.dispatch.TaxiLimoNewUI;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Locale;
//
//import digital.dispatch.TaxiLimoNewUI.Book.ModifyAddressActivity;
//import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
//import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
//import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//public class BookActivity extends Activity {
//	private static final String TAG = "BookActivity";
//	private Context _this;
//	private RelativeLayout rl_pick_up,rl_drop_off,rl_date,rl_driver_note,rl_company;
//	private TextView tv_pick_up,tv_drop_off,tv_date,tv_driver_note,tv_company, book_button;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_book);
//		_this = this;
//		findView();
//		bindEvent();
//	}
//	
//	@Override
//	public void onResume() {
//		super.onResume();
//		Logger.e(TAG, "on RESUME");
//		checkGPSEnable();
//		checkInternet();
//		}
//
//	private void bindEvent() {
//		rl_pick_up.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Intent intent = new Intent(_this, ModifyAddressActivity.class);
//
//				intent.putExtra(MBDefinition.ADDRESSBAR_TEXT_EXTRA, tv_pick_up.getText().toString());
//
//				intent.putExtra(MBDefinition.IS_DESTINATION, false);
//				_this.startActivityForResult(intent, MBDefinition.REQUEST_PICKUPADDRESS_CODE);
//			}
//		});
//		
//		rl_drop_off.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Intent intent = new Intent(getActivity(), ModifyAddressActivity.class);
//				intent.putExtra(MBDefinition.IS_DESTINATION, true);
//				getActivity().startActivityForResult(intent, MBDefinition.REQUEST_DROPOFFADDRESS_CODE);
//			}
//		});
//		
//		rl_driver_note.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				// setUpMessageDialog();
//				Dialog messageDialog = new Dialog(getActivity());
//
//				Utils.setUpDriverNoteDialog(getActivity(), messageDialog, tv_driver_note);
//
//			}
//		});
//		
//		book_button
//		
//		
//	}
//
//	private void findView() {
//		rl_pick_up = (RelativeLayout) findViewById(R.id.rl_pickup);
//		rl_drop_off = (RelativeLayout) findViewById(R.id.rl_drop_off);
//		rl_date = (RelativeLayout) findViewById(R.id.rl_date);
//		rl_driver_note = (RelativeLayout) findViewById(R.id.rl_driver_note);
//		rl_company = (RelativeLayout) findViewById(R.id.rl_company);
//
//		tv_pick_up = (TextView) findViewById(R.id.tv_pickup);
//		tv_drop_off = (TextView) findViewById(R.id.tv_drop_off);
//		tv_date = (TextView) findViewById(R.id.tv_date);
//		tv_driver_note = (TextView) findViewById(R.id.tv_driver_note);
//		tv_company = (TextView) findViewById(R.id.tv_company);
//		book_button = (TextView) findViewById(R.id.book_button);
//	}
//	
//	private void buildAlertMessageNoGps() {
//		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false)
//				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//					public void onClick(final DialogInterface dialog, final int id) {
//						startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//					}
//				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//					public void onClick(final DialogInterface dialog, int which) {
//						dialog.cancel();
//					}
//				});
//		final AlertDialog alert = builder.create();
//		alert.show();
//	}
//	
//	private void setTimeText(TextView tv_time) {
//		if (Utils.pickupDate == null || Utils.pickupTime == null) {
//			tv_time.setText(getActivity().getResources().getString(R.string.now));
//			tv_time.setTextSize(20);
//			tv_time.setTextColor(getActivity().getResources().getColor(R.color.gray_light));
//		} else {
//			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd", Locale.US);
//			SimpleDateFormat timeFormat = new SimpleDateFormat("hh: mm a", Locale.US);
//			String date = dateFormat.format(Utils.pickupDate);
//			String time = timeFormat.format(Utils.pickupTime);
//			Calendar cal = Calendar.getInstance();
//
//			if (Utils.pickupDate.getDate() == cal.get(Calendar.DATE)) {
//				tv_time.setText("Today" + "\n" + time);
//			} else
//				tv_time.setText(date + "\n" + time);
//			tv_time.setTextSize(13);
//			tv_time.setTextColor(getActivity().getResources().getColor(R.color.black));
//		}
//	}
//	
//	private void checkGPSEnable() {
//		final LocationManager manager = (LocationManager) _this.getSystemService(Context.LOCATION_SERVICE);
//
//		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//			buildAlertMessageNoGps();
//		}
//	}
//
//	private void checkInternet() {
//		Utils.isInternetAvailable(_this);
//	}
//
//}
