package digital.dispatch.TaxiLimoNewUI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import digital.dispatch.TaxiLimoNewUI.DBCreditCard;
import digital.dispatch.TaxiLimoNewUI.R;

public class CreditCardListAdapter extends ArrayAdapter<DBCreditCard> {

	private static final String TAG = "CreditCardListAdapter";
	private final Context context;
	private List<DBCreditCard> values;


	public CreditCardListAdapter(Context context, List<DBCreditCard> values) {
		super(context, R.layout.credit_card_list_item, values);
		this.context = context;
		this.values = values;
	}

	public static class ViewHolder {
		public TextView number;
		public TextView nickName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		// reuse views
		if (rowView == null) {
			rowView = LayoutInflater.from(getContext()).inflate(R.layout.credit_card_list_item, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();

			viewHolder.number = (TextView) rowView.findViewById(R.id.tv_card_number);
			viewHolder.nickName = (TextView) rowView.findViewById(R.id.tv_nickname);
			rowView.setTag(viewHolder);
		}
		
		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.number.setText(values.get(position).getLast4CardNum());
		holder.nickName.setText(values.get(position).getNickName());
		
		return rowView;
	}
}
