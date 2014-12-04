package digital.dispatch.TaxiLimoNewUI.Adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
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

	// remove object from value if is completed or canceled
	public void updateValue(DBBooking dbBook) {
		int trid = dbBook.getTaxi_ride_id();
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i).getTaxi_ride_id() == trid) {
				if(dbBook.getTripStatus()==MBDefinition.MB_STATUS_COMPLETED
						||dbBook.getTripStatus()==MBDefinition.MB_STATUS_CANCELLED)
					values.remove(i);
				else
					values.set(i, dbBook);
			}
		}
	}


	public BookingListAdapter(Context context, List<DBBooking> values) {
		super(context, R.layout.booking_list_item, values);
		this.context = context;
		this.values = values;
	}

	public static class ViewHolder {
		public TextView address;
		public TextView status;
		public TextView status_bar;
	}

	// public void removeIfNotInList(List<DBBooking> newList) {
	// for(int i=0;i<values.size();i++){
	// DBBooking oldValue = values.get(i)
	// }
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
			viewHolder.status = (TextView) rowView.findViewById(R.id.tv_status);
			viewHolder.status_bar = (TextView) rowView.findViewById(R.id.tv_status_bar);
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		Typeface fontFamily = Typeface.createFromAsset(context.getAssets(), "fonts/RionaSansRegular.otf");
		holder.address.setTypeface(fontFamily);
		holder.address.setText(values.get(position).getPickupAddress());
		
		Typeface exo2FontFamily = Typeface.createFromAsset(context.getAssets(), "fonts/Exo2-SemiBold.ttf");
		holder.status.setTypeface(exo2FontFamily);
		if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_COMPLETED) {
			holder.status.setText(context.getString(R.string.completed));
			holder.status.setTextColor(context.getResources().getColor(R.color.completed_color));
			holder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.completed_color));
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_CANCELLED) {
			holder.status.setText(context.getString(R.string.canceled));
			holder.status.setTextColor(context.getResources().getColor(R.color.canceled_color));
			holder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.canceled_color));
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_ACCEPTED) {
			holder.status.setText(context.getString(R.string.dispatched));
			holder.status.setTextColor(context.getResources().getColor(R.color.dispatched_color));
			holder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.dispatched_color));
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_ARRIVED) {
			holder.status.setText(context.getString(R.string.arrived));
			holder.status.setTextColor(context.getResources().getColor(R.color.arrived_color));
			holder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.arrived_color));
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_BOOKED) {
			holder.status.setText(context.getString(R.string.booked));
			holder.status.setTextColor(context.getResources().getColor(R.color.completed_color));
			holder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.completed_color));
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_IN_SERVICE) {
			holder.status.setText(context.getString(R.string.in_service));
			holder.status.setTextColor(context.getResources().getColor(R.color.inservice_color));
			holder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.inservice_color));
		}

		return rowView;
	}

}
