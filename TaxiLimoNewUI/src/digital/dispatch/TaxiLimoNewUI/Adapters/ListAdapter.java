package digital.dispatch.TaxiLimoNewUI.Adapters;

import java.util.List;

import com.digital.dispatch.TaxiLimoSQLDatabase.MBBooking;

import digital.dispatch.TaxiLimoNewUI.R;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<MBBooking>{
	
	  private final Context context;
	  private final List<MBBooking> values;

	  public ListAdapter(Context context, List<MBBooking> values) {
	    super(context, R.layout.list_item, values);
	    this.context = context;
	    this.values = values;
	  }
	  
		public static class ViewHolder {
			public TextView address;	
			public TextView status;	
		}

//	  @Override
//	  public View getView(int position, View convertView, ViewGroup parent) {
//	    LayoutInflater inflater = (LayoutInflater) context
//	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	    View rowView = inflater.inflate(R.layout.list_item, parent, false);
//	    TextView address = (TextView) rowView.findViewById(R.id.text_address);
//	    TextView status = (TextView) rowView.findViewById(R.id.text_status);
//	    address.setText(values.get(position).getAttribute());
//	    status.setText(values.get(position).getDispatchedCar());
//
//	    return rowView;
//	  }
	  
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    View rowView = convertView;
	    // reuse views
	    if (rowView == null) {
	    	
	      rowView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
	      // configure view holder
	      ViewHolder viewHolder = new ViewHolder();
	      
	      viewHolder.address = (TextView) rowView.findViewById(R.id.text_address);
	      viewHolder.status = (TextView) rowView.findViewById(R.id.text_status);
	      rowView.setTag(viewHolder);
	    }

	    // fill data
	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    holder.address.setText(values.get(position).getAttribute());
	    if(values.get(position).getDispatchedCar().equalsIgnoreCase("Complete"))
	    {
	    	((GradientDrawable)holder.status.getBackground()).setColor(
	    			context.getResources().getColor(R.color.orange_light));
	    }
	    else{
	    	((GradientDrawable)holder.status.getBackground()).setColor(
	    			context.getResources().getColor(R.color.blue_light));
	    }
	    holder.status.setText(values.get(position).getDispatchedCar());

	    return rowView;
	  }
}
