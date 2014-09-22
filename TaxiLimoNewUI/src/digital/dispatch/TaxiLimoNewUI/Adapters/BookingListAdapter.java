package digital.dispatch.TaxiLimoNewUI.Adapters;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

	public BookingListAdapter(Context context, List<DBBooking> values) {
		super(context, R.layout.booking_list_item, values);
		this.context = context;
		this.values = values;
	}

	public static class ViewHolder {
		public TextView address;
		public TextView status;
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
			viewHolder.status = (TextView) rowView.findViewById(R.id.text_status);
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.address.setText(values.get(position).getPickupAddress());

		if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_COMPLETED) {
			((GradientDrawable) holder.status.getBackground()).setColor(context.getResources().getColor(R.color.orange_light));
			holder.status.setText("Completed");
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_CANCELLED) {
			((GradientDrawable) holder.status.getBackground()).setColor(context.getResources().getColor(R.color.blue_light));
			holder.status.setText("Canceled");
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_ACCEPTED) {
			((GradientDrawable) holder.status.getBackground()).setColor(context.getResources().getColor(R.color.blue_light));
			holder.status.setText("Accepted");
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_ARRIVED) {
			((GradientDrawable) holder.status.getBackground()).setColor(context.getResources().getColor(R.color.blue_light));
			holder.status.setText("Arrived");
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_BOOKED) {
			((GradientDrawable) holder.status.getBackground()).setColor(context.getResources().getColor(R.color.blue_light));
			holder.status.setText("Booked");
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_IN_SERVICE) {
			((GradientDrawable) holder.status.getBackground()).setColor(context.getResources().getColor(R.color.blue_light));
			holder.status.setText("In Service");
		}

		return rowView;
	}
}
