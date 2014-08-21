package digital.dispatch.TaxiLimoNewUI.Book;

import java.util.ArrayList;

import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.R.menu;
import digital.dispatch.TaxiLimoNewUI.Adapters.AttributeItemAdapter;
import digital.dispatch.TaxiLimoNewUI.Adapters.CompanyListAdapter;
import digital.dispatch.TaxiLimoNewUI.Task.GetCompanyListTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

public class AttributeActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attribute);
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new AttributeItemAdapter(this));

	    gridview.setOnItemClickListener(new OnItemClickListener() {

	        @Override
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(AttributeActivity.this, "aabbcc" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	    
	    new GetCompanyListTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void dothis(ArrayList<Integer> positive_IDs){
		for(int i=0;i<positive_IDs.size();i++){
			Logger.e(positive_IDs.get(i)+"");
		}
	}
	
	public void loadCompanyList(CompanyItem[] tempCompList){
		Logger.e("loadCompanyList");
		for (int i =0;i<tempCompList.length;i++){
			CompanyItem.printCompanyItem(tempCompList[i]);
		}
		ListView lv_company = (ListView) findViewById(R.id.lv_company);
		CompanyListAdapter cp_adapter = new CompanyListAdapter(this,tempCompList);
		lv_company.setAdapter(cp_adapter);
	}


}
