package digital.dispatch.TaxiLimoNewUI.Adapters;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadImageTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import digital.dispatch.TaxiLimoNewUI.Task.GetEstimateFareTask;

public class CompanyListAdapter extends ArrayAdapter<CompanyItem> {

	private final Context context;
	private final CompanyItem[] items;
	boolean bookRightAfter;
	

	public CompanyListAdapter(Context context, CompanyItem[] items, boolean bookRightAfter) {
		super(context, R.layout.company_list_item, items);
		this.context = context;
		this.items = items;
		this.bookRightAfter = bookRightAfter;
	}

	public static class ViewHolder {
		public ImageView icon;
		public TextView name;
		public TextView description;
		public LinearLayout ll_attr;
		public TextView tv_round_btn;
		public TextView estFare;
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
		ViewHolder viewHolder = null;
		// reuse views
		if (rowView == null) {
			rowView = LayoutInflater.from(getContext()).inflate(R.layout.company_list_item, null);
			// configure view holder
			viewHolder = new ViewHolder();

			viewHolder.icon = (ImageView) rowView.findViewById(R.id.company_icon);
			viewHolder.name = (TextView) rowView.findViewById(R.id.tv_name);
			viewHolder.description = (TextView) rowView.findViewById(R.id.tv_description);
			viewHolder.ll_attr = (LinearLayout) rowView.findViewById(R.id.ll_attr);
			viewHolder.tv_round_btn = (TextView) rowView.findViewById(R.id.tv_round_btn);
			viewHolder.estFare = (TextView) rowView.findViewById(R.id.tv_est_fare);

			if (bookRightAfter)
				viewHolder.tv_round_btn.setText("Book");
			else
				viewHolder.tv_round_btn.setText("Select");

			CompanyItem item = items[position];

			viewHolder.name.setText(item.name);
			String prefixURL = context.getResources().getString(R.string.url);
			prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
			// String[] locArray = item.logo.split("/");
			// new DownloadLogoTask(prefixURL + item.logo, locArray[locArray.length - 1], viewHolder.icon, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new DownloadImageTask(viewHolder.icon).execute(prefixURL + item.logo);
			viewHolder.description.setText(item.description);

			String[] attrs = item.attributes.split(",");
			int marginRight = 10;
			Utils.showOption(viewHolder.ll_attr, attrs, context, marginRight);
			//TL-88 add fare estimate if drop off address is set and baseRate set up
			if (Utils.mDropoffAddress != null && item.baseRate != 0 && item.ratePerDistance != 0){
				
				if (Utils.hasHoneycomb()) {
					// --post GB use serial executor by default --
					new GetEstimateFareTask(viewHolder.estFare, item.baseRate, item.ratePerDistance).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							Utils.mPickupAddress.getLatitude() + "," + Utils.mPickupAddress.getLongitude(), Utils.mDropoffAddress.getLatitude() + "," + Utils.mDropoffAddress.getLongitude(), "driving");
				} else {
					// --GB uses ThreadPoolExecutor by default--
					new GetEstimateFareTask(viewHolder.estFare, item.baseRate, item.ratePerDistance).execute(Utils.mPickupAddress.getLatitude() + "," + Utils.mPickupAddress.getLongitude(),
							Utils.mDropoffAddress.getLatitude() + "," + Utils.mDropoffAddress.getLongitude(), "driving");
				}
				
			}else{
				viewHolder.estFare.setText("");
			}

			// final float scale = context.getResources().getDisplayMetrics().density;
			//
			// for (int i = 0; i < attrs.length; i++) {
			// if (!attrs[i].equalsIgnoreCase("")) {
			// ImageView attr = new ImageView(context);
			// attr.setImageResource(MBDefinition.attrIconMap.get(Integer.valueOf(attrs[i])));
			// int dimens = (int) (30 * scale + 0.5f);
			// int margin_right = (int) (10 * scale + 0.5f);
			// LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimens, dimens);
			// layoutParams.setMargins(0, 0, margin_right, 0);
			// // setting image position
			// attr.setLayoutParams(layoutParams);
			// viewHolder.ll_attr.addView(attr);
			// }
			//
			// }
			rowView.setTag(viewHolder);
		} else {
			// fill data
			viewHolder = (ViewHolder) rowView.getTag();
		}

		return rowView;
	}

	public CompanyItem getCompanyItem(int i) {
		return items[i];
	}
	
	
}
