package digital.dispatch.TaxiLimoNewUI.History;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.R.menu;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class TripDetailActivity extends Activity {

	private static final String TAG = "TripDetailActivity";
	private DBBooking dbBook;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_detail);
		dbBook = (DBBooking) getIntent().getSerializableExtra(MBDefinition.DBBOOKING_EXTRA);
		TextView tv_id = (TextView) findViewById(R.id.tv_history_id);
		TextView tv_receive = (TextView) findViewById(R.id.tv_history_receive);
		TextView tv_from = (TextView) findViewById(R.id.tv_history_pickup_address);
		TextView tv_to = (TextView) findViewById(R.id.tv_history_dropoff_address);

		
		tv_id.setText(dbBook.getTaxi_ride_id()+"");
		tv_receive.setText(dbBook.getTripCreationTime());
		tv_from.setText(dbBook.getPickupAddress());
		tv_to.setText(dbBook.getDropoffAddress());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trip_detail, menu);
		return true;
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
