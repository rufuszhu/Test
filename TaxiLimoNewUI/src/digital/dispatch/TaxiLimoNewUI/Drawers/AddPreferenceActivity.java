package digital.dispatch.TaxiLimoNewUI.Drawers;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.digital.dispatch.TaxiLimoSoap.responses.Node;

import java.util.ArrayList;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.GetServiceListTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;

public class AddPreferenceActivity extends BaseActivity {

	private static final String TAG = "AddPreferenceActivity";
	private ArrayList<Node> countryList;
	private ArrayList<Node> stateList;
	private ArrayList<Node> regionList;
	private ArrayList<Node> companyList;
	private Spinner stateSpinner;
	private Spinner countrySpinner;
	private Spinner regionSpinner;

	private MyAdapter countryAdapter;
	private MyAdapter stateAdapter;
	private MyAdapter regionAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_preference);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		new GetServiceListTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		// ArrayAdapter<Node> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, objects);

		findView();
		initAndBindEvent();
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
	
	private void initAndBindEvent() {
		OnItemSelectedListener countryListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (countryAdapter.objects.size() > 0) {
					Logger.d(TAG, "countryListener");
					String countryName = countryAdapter.objects.get(position).getName();
					stateAdapter.objects.clear();

					for (int i = 0; i < stateList.size(); i++) {
						if (stateList.get(i).getParent().equals(countryName)) {
							Logger.d("adding: " + stateList.get(i).getName());
							stateAdapter.objects.add(stateList.get(i));
						}
					}
					stateAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};

		OnItemSelectedListener stateListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (stateAdapter.objects.size() > 0) {
					String stateName = stateAdapter.objects.get(position).getName();
					regionAdapter.objects.clear();

					for (int i = 0; i < regionList.size(); i++) {
						if (regionList.get(i).getParent().equals(stateName))
							regionAdapter.objects.add(regionList.get(i));
					}
					regionAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};

		OnItemSelectedListener regionListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};
		stateSpinner.setOnItemSelectedListener(stateListener);
		countrySpinner.setOnItemSelectedListener(countryListener);
		regionSpinner.setOnItemSelectedListener(regionListener);
		
	}

	private void findView() {
		stateSpinner = (Spinner) findViewById(R.id.state_spinner);
		countrySpinner = (Spinner) findViewById(R.id.country_spinner);
		regionSpinner = (Spinner) findViewById(R.id.region_spinner);

		
	}


	// response of GetServiceListTask
	public void getData(ArrayList<Node> countryList, ArrayList<Node> stateList, ArrayList<Node> regionList, ArrayList<Node> companyList) {
		this.companyList = companyList;
		this.countryList = countryList;
		this.stateList = stateList;
		this.regionList = regionList;
		
		printList(companyList);
		printList(countryList);
		printList(stateList);
		printList(regionList);
		

		countryAdapter = new MyAdapter(this, R.layout.spinner_item, countryList);
		stateAdapter = new MyAdapter(this, R.layout.spinner_item, new ArrayList<Node>());
		regionAdapter = new MyAdapter(this, R.layout.spinner_item, new ArrayList<Node>());
		countrySpinner.setAdapter(countryAdapter);
		stateSpinner.setAdapter(stateAdapter);
		regionSpinner.setAdapter(regionAdapter);
	}

	// Creating an Adapter Class
	public class MyAdapter extends ArrayAdapter<Node> {
		private ArrayList<Node> objects;

		public MyAdapter(Context context, int textViewResourceId, ArrayList<Node> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
		}

		public View getCustomView(int position, View convertView, ViewGroup parent) {

			// Inflating the layout for the custom Spinner
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.spinner_item, parent, false);

			// Declaring and Typecasting the textview in the inflated layout
			TextView tvLanguage = (TextView) layout.findViewById(R.id.tv);

			// Setting the text using the array
			tvLanguage.setText(objects.get(position).getName());

			// Setting the color of the text
			tvLanguage.setTextColor(Color.rgb(75, 180, 225));

			// // Declaring and Typecasting the imageView in the inflated layout
			// ImageView img = (ImageView) layout.findViewById(R.id.imgLanguage);
			//
			// // Setting an image using the id's in the array
			// img.setImageResource(images[position]);

			// Setting Special atrributes for 1st element
			if (position == 0) {
				// Removing the image view
				// img.setVisibility(View.GONE);
				// Setting the size of the text
				tvLanguage.setTextSize(20f);
				// Setting the text Color
				tvLanguage.setTextColor(Color.BLACK);

			}

			return layout;
		}

		// It gets a View that displays in the drop down popup the data at the specified position
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		// It gets a View that displays the data at the specified position
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}
	}
	
	private void printList(ArrayList<Node> list){
		for(int i = 0; i< list.size();i++){
			Logger.i(TAG, "name: " + list.get(i).getName() + " parent: " + list.get(i).getParent());
		}
		
	}

}
