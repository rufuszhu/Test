package digital.dispatch.TaxiLimoNewUI.Adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import digital.dispatch.TaxiLimoNewUI.DBAttribute;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Book.AttributeActivity;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class AttributeItemAdapter extends BaseAdapter {
	private Context mContext;
	private List<DBAttribute> attrList;

	public AttributeItemAdapter(Context c, List<DBAttribute> attrList) {
		mContext = c;
		this.attrList = attrList;
	}

	@Override
	public int getCount() {
		return attrList.size();
	}

	@Override
	public Object getItem(int position) {
		return attrList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		final View view;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		//not reusing cause it may cause issue when attribute list change
//		if (convertView == null) { // reuse it if it already exists
//			view = (View) inflater.inflate(R.layout.attribute_item, null);
//			
//		} else {
//			view = (View) convertView;
//		}
		
		view = (View) inflater.inflate(R.layout.attribute_item, null);
//		ImageView icon = (ImageView) view.findViewById(R.id.icon);
//		TextView name = (TextView) view.findViewById(R.id.name);
//		icon.setBackgroundResource(MBDefinition.attrBtnMap.get(Integer.parseInt(attrList.get(position).getIconId()),R.drawable.ic_action_about));
//		name.setText(attrList.get(position).getName());
		
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] {android.R.attr.state_checked},
				mContext.getResources().getDrawable(MBDefinition.attrBtnOnMap.get(Integer.parseInt(attrList.get(position).getIconId()))));
		
		states.addState(new int[] {},
				mContext.getResources().getDrawable(MBDefinition.attrBtnOffMap.get(Integer.parseInt(attrList.get(position).getIconId()))));

		ToggleButton toggleButton = (ToggleButton) view.findViewById(R.id.toggleButton);
		
		toggleButton.setBackgroundDrawable(states);
		
		if((Utils.selected_attribute!=null && Utils.selected_attribute.contains(Integer.parseInt(attrList.get(position).getAttributeId())))){
			toggleButton.setChecked(true);
		}

		toggleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				ArrayList<Integer> positive_IDs = new ArrayList<Integer>();
				for(int i = 0; i < parent.getChildCount(); i++){
					ToggleButton toggleButton = (ToggleButton) parent.getChildAt(i).findViewById(R.id.toggleButton);
					if(toggleButton.isChecked()){
						positive_IDs.add(Integer.valueOf(attrList.get(i).getAttributeId())); 
					}
				}
				((AttributeActivity)mContext).filterCompany(positive_IDs);
				
			}
		});
		return view;
	}
}