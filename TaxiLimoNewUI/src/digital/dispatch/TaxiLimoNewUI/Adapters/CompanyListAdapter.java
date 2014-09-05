package digital.dispatch.TaxiLimoNewUI.Adapters;

import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digital.dispatch.TaxiLimoSQLDatabase.MBBooking;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.BookingListAdapter.ViewHolder;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadImageTask;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadLogoTask;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class CompanyListAdapter extends ArrayAdapter<CompanyItem> {

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
		public LinearLayout ll_attr;
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
			
//			final float scale = context.getResources().getDisplayMetrics().density;
//
//			for (int i = 0; i < attrs.length; i++) {
//				if (!attrs[i].equalsIgnoreCase("")) {
//					ImageView attr = new ImageView(context);
//					attr.setImageResource(MBDefinition.attrIconMap.get(Integer.valueOf(attrs[i])));
//					int dimens = (int) (30 * scale + 0.5f);
//					int margin_right = (int) (10 * scale + 0.5f);
//					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimens, dimens);
//					layoutParams.setMargins(0, 0, margin_right, 0);
//					// setting image position
//					attr.setLayoutParams(layoutParams);
//					viewHolder.ll_attr.addView(attr);
//				}
//
//			}
			rowView.setTag(viewHolder);
		} else {
			// fill data
			viewHolder = (ViewHolder) rowView.getTag();
		}

		return rowView;
	}


	
	public CompanyItem getCompanyItem(int i){
		return items[i];
	}
}
