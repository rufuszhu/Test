package digital.dispatch.TaxiLimoNewUI.Adapters;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.digital.dispatch.TaxiLimoSQLDatabase.MBBooking;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.BookingListAdapter.ViewHolder;

public class CompanyListAdapter extends ArrayAdapter<CompanyItem>{
	
	  private final Context context;
	  private final CompanyItem[] items;

	  public CompanyListAdapter(Context context, CompanyItem[] items) {
	    super(context, R.layout.company_list_item, items);
	    this.context = context;
	    this.items = items;
	  }
	  
	  
		public static class ViewHolder {
			public ImageView icon;
			public TextView name;	
			public TextView description;	
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
	    	
	      rowView = LayoutInflater.from(getContext()).inflate(R.layout.company_list_item, null);
	      // configure view holder
	      ViewHolder viewHolder = new ViewHolder();
	      
	      viewHolder.name = (TextView) rowView.findViewById(R.id.tv_name);
	      viewHolder.description = (TextView) rowView.findViewById(R.id.tv_description);
	      rowView.setTag(viewHolder);
	    }

	    // fill data
	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    holder.name.setText(items[position].name);
	    
	    holder.description.setText(items[position].description);

	    return rowView;
	  }
}
