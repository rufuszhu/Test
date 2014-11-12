package digital.dispatch.TaxiLimoNewUI.Drawers;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.R.menu;
import digital.dispatch.TaxiLimoNewUI.Task.GetCompanyListTask;
import digital.dispatch.TaxiLimoNewUI.Task.GetServiceListTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class PreferenceActivity extends ActionBarActivity {

	private static final String TAG = "PreferenceActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		new GetServiceListTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		Logger.e(TAG,"");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preference, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
