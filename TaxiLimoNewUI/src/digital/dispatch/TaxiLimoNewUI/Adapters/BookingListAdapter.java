package digital.dispatch.TaxiLimoNewUI.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;

public class BookingListAdapter extends ArrayAdapter<DBBooking> {

	private final Context context;
	private List<DBBooking> values;
	private Typeface OpenSansRegular,exo2FontFamily;

	public List<DBBooking> getValues() {
		return values;
	}

	public void setValues(List<DBBooking> values) {
		this.values = values;
	}

	// remove object from value if is completed or canceled
//	public void updateValue(DBBooking dbBook) {
//		int trid = dbBook.getTaxi_ride_id();
//		for (int i = 0; i < values.size(); i++) {
//			if (values.get(i).getTaxi_ride_id() == trid) {
//				if(dbBook.getTripStatus()==MBDefinition.MB_STATUS_COMPLETED
//						||dbBook.getTripStatus()==MBDefinition.MB_STATUS_CANCELLED
//						||dbBook.getTripStatus()==MBDefinition.MB_STATUS_UNKNOWN) //TL-264
//					values.remove(i);
//				else
//					values.set(i, dbBook);
//			}
//		}
//	}


	public BookingListAdapter(Context context, List<DBBooking> values) {
		super(context, R.layout.booking_list_item, values);
		this.context = context;
		this.values = values;
        OpenSansRegular = FontCache.getFont(context, "fonts/OpenSansRegular.ttf");
		exo2FontFamily = FontCache.getFont(context, "fonts/Exo2-SemiBold.ttf");
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
		ViewHolder viewHolder;
		// reuse views
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.booking_list_item, null);
			viewHolder = new ViewHolder();

			viewHolder.address = (TextView) convertView.findViewById(R.id.text_address);
			viewHolder.status = (TextView) convertView.findViewById(R.id.tv_status);
			viewHolder.status_bar = (TextView) convertView.findViewById(R.id.tv_status_bar);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// fill data
		viewHolder.address.setTypeface(OpenSansRegular);
		viewHolder.address.setText(values.get(position).getPickupAddress());
		
		viewHolder.status.setTypeface(exo2FontFamily);
		if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_COMPLETED) {
			viewHolder.status.setText(context.getString(R.string.completed));
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.completed_color));
			viewHolder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.completed_color));
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_CANCELLED) {
			viewHolder.status.setText(context.getString(R.string.canceled));
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.canceled_color));
			viewHolder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.canceled_color));
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_ACCEPTED) {
			viewHolder.status.setText(context.getString(R.string.dispatched));
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.dispatched_color));
			viewHolder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.dispatched_color));
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_ARRIVED) {
			viewHolder.status.setText(context.getString(R.string.arrived));
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.arrived_color));
			viewHolder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.arrived_color));
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_BOOKED) {
			viewHolder.status.setText(context.getString(R.string.booked));
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.completed_color));
			viewHolder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.completed_color));
		} else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_IN_SERVICE) {
			viewHolder.status.setText(context.getString(R.string.in_service));
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.inservice_color));
			viewHolder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.inservice_color));
		}
//        else if (values.get(position).getTripStatus() == MBDefinition.MB_STATUS_UNKNOWN) { //TL-264
//			viewHolder.status.setText(context.getString(R.string.completed));
//			viewHolder.status.setTextColor(context.getResources().getColor(R.color.completed_color));
//			viewHolder.status_bar.setBackgroundColor(context.getResources().getColor(R.color.completed_color));
//		}
		return convertView;
	}

}
