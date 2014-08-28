package digital.dispatch.TaxiLimoNewUI.Adapters;

import java.util.ArrayList;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Book.AttributeActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AttributeItemAdapter extends BaseAdapter {
	private Context mContext;

	public AttributeItemAdapter(Context c) {
		mContext = c;
	}

	@Override
	public int getCount() {
		return 10;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		final View view;
		final int pos = position;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) { // reuse it if it already exists
			view = (View) inflater.inflate(R.layout.attribute_item, null);

		} else {
			view = (View) convertView;
		}

		ToggleButton toggleButton = (ToggleButton) view.findViewById(R.id.toggleButton1);

		toggleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<Integer> positive_IDs = new ArrayList<Integer>();
				for(int i = 0; i < parent.getChildCount(); i++){
					ToggleButton toggleButton = (ToggleButton) parent.getChildAt(i).findViewById(R.id.toggleButton1);
					if(toggleButton.isChecked()){
						positive_IDs.add(i); 
					}
				}
				
				((AttributeActivity)mContext).dothis(positive_IDs);
				
			}
		});
		return view;
	}
}