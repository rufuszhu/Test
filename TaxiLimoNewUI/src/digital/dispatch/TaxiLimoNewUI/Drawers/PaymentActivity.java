package digital.dispatch.TaxiLimoNewUI.Drawers;

import java.util.List;

import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.CompanyListAdapter;
import digital.dispatch.TaxiLimoNewUI.Adapters.CreditCardListAdapter;
import digital.dispatch.TaxiLimoNewUI.Book.ModifyAddressActivity;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.DBCreditCard;
import digital.dispatch.TaxiLimoNewUI.DBCreditCardDao;
import digital.dispatch.TaxiLimoNewUI.DBCreditCardDao.Properties;
import digital.dispatch.TaxiLimoNewUI.EditCreditCardActivity;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.R.menu;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PaymentActivity extends ActionBarActivity {

	private static final String TAG = "PaymentActivity";
	private ListView lv_credit_card;
	private DBCreditCardDao creditCardDao;
	private List<DBCreditCard> cardList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_payment);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		final Context context = this;
		lv_credit_card = (ListView) findViewById(R.id.lv_credit_card);
		lv_credit_card.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, EditCreditCardActivity.class);
				intent.putExtra(MBDefinition.EXTRA_CREDIT_CARD, cardList.get(position));
				startActivity(intent);
				overridePendingTransition(R.anim.base_back_activity_enter, R.anim.base_back_activity_exit);
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
		DaoManager daoManager = DaoManager.getInstance(this);
		creditCardDao = daoManager.getDBCreditCardDao(DaoManager.TYPE_WRITE);
		cardList = creditCardDao.queryBuilder().list();
		
		CreditCardListAdapter cc_adapter = new CreditCardListAdapter(this, cardList);
		lv_credit_card.setAdapter(cc_adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.payment, menu);
		return true;
	}
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.base_back_activity_enter, R.anim.base_back_activity_exit);
    }
    
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.base_back_activity_enter, R.anim.base_back_activity_exit);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_add) {
			Intent intent = new Intent(this, AddPreferenceActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}
