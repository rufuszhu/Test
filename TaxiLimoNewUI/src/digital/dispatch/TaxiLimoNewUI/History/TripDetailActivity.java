package digital.dispatch.TaxiLimoNewUI.History;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.R.menu;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadImageTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TripDetailActivity extends Activity {

	private static final String TAG = "TripDetailActivity";
	private DBBooking dbBook;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_detail);
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
		
		
		tv_id.setText(dbBook.getTaxi_ride_id()+"");
		tv_receive.setText(dbBook.getTripCreationTime());
		tv_from.setText(dbBook.getPickupAddress());
		
		//It is safe to assume that the job is this activity is either completed or cancel
		if(dbBook.getTripStatus()==MBDefinition.MB_STATUS_COMPLETED){
			tv_trip_status.setText("Trip Completed");
		}
		else{
			tv_trip_status.setText("Trip Canceled");
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
			tv_payment.setText(dbBook.getPaidAmount());
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
}
