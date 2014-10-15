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
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;

public class BookingListAdapter extends ArrayAdapter<DBBooking> {

	private final Context context;
	private List<DBBooking> values;

	public List<DBBooking> getValues() {
		return values;
	}

	public void setValues(List<DBBooking> values) {
		this.values = values;
	}
	
	public void updateValues(DBBooking dbBook){
		int trid = dbBook.getTaxi_ride_id();
		for(int i=0;i<values.size();i++){
			if(values.get(i).getTaxi_ride_id()==trid)
				values.set(i, dbBook);
		}
	}

	public BookingListAdapter(Context context, List<DBBooking> values) {
		super(context, R.layout.booking_list_item, values);
		this.context = context;
		this.values = values;
	}

	public static class ViewHolder {
		public TextView address;
		public ImageView status;
	}

	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// LayoutInflater inflater = (LayoutInflater) context
	// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// View rowView = inflater.inflate(R.layout.list_item, parent, false);
	// TextView address = (TextView) rowView.findViewById(R.id.text_address);
	// TextView status = (TextView) rowView.findViewById(R.id.text_status);
	// address.setText(values.get(position).getAttribute());
	// status.setText(values.get(position).getDispatchedCar());
	//
	// return rowView;
	// }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		// reuse views
		if (rowView == null) {

			rowView = LayoutInflater.from(getContext()).inflate(R.layout.booking_list_item, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();

			viewHolder.address = (TextView) rowView.findViewById(R.id.text_address);
			viewHolder.status = (ImageView) rowView.findViewById(R.id.text_status);
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.address.setText(values.get(position).getPickupAddress());

		if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_COMPLETED) {
			holder.status.setImageResource(R.drawable.tag_completed);
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_CANCELLED) {
			holder.status.setImageResource(R.drawable.tag_canceled);
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_ACCEPTED) {
			holder.status.setImageResource(R.drawable.tag_dispatched);
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_ARRIVED) {
			holder.status.setImageResource(R.drawable.tag_arrived);
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_BOOKED) {
			holder.status.setImageResource(R.drawable.tag_booked);
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_IN_SERVICE) {
			holder.status.setImageResource(R.drawable.tag_in_service);
		}

		return rowView;
	}
}
