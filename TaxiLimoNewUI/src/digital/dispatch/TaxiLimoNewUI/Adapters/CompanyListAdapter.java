package digital.dispatch.TaxiLimoNewUI.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Address;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.DownloadImageTask;
import digital.dispatch.TaxiLimoNewUI.Utils.AppController;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import digital.dispatch.TaxiLimoNewUI.Task.GetEstimateFareTask;

public class CompanyListAdapter extends ArrayAdapter<CompanyItem> {

	private final Context context;
	private final CompanyItem[] items;
	boolean bookRightAfter;
	private Typeface rionaSansMedium;
	private Typeface rionaSansRegular;
	private Typeface rionaSansBold;
	private Typeface exoBold;
	

	public CompanyListAdapter(Context context, CompanyItem[] items, boolean bookRightAfter) {
		super(context, R.layout.company_list_item, items);
		this.context = context;
		this.items = items;
		this.bookRightAfter = bookRightAfter;
		rionaSansMedium = Typeface.createFromAsset(context.getAssets(), "fonts/RionaSansMedium.otf");
		rionaSansRegular = Typeface.createFromAsset(context.getAssets(), "fonts/RionaSansRegular.otf");
		
		exoBold = Typeface.createFromAsset(context.getAssets(), "fonts/Exo2-Bold.ttf");
	}

	public static class ViewHolder {
		public NetworkImageView icon;
		public TextView name;
		public TextView description;
		public LinearLayout ll_attr;
		public TextView tv_round_btn;
		//public TextView estFare;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder viewHolder = null;
		// reuse views
		if (rowView == null) {
			rowView = LayoutInflater.from(getContext()).inflate(R.layout.company_list_item, null);
			// configure view holder
			viewHolder = new ViewHolder();

			viewHolder.icon = (NetworkImageView) rowView.findViewById(R.id.company_icon);
			viewHolder.name = (TextView) rowView.findViewById(R.id.tv_name);
			viewHolder.description = (TextView) rowView.findViewById(R.id.tv_description);
			viewHolder.ll_attr = (LinearLayout) rowView.findViewById(R.id.ll_attr);
			viewHolder.tv_round_btn = (TextView) rowView.findViewById(R.id.tv_round_btn);
			//viewHolder.estFare = (TextView) rowView.findViewById(R.id.tv_est_fare);

			rowView.setTag(viewHolder);
		} else {
			// fill data
			viewHolder = (ViewHolder) rowView.getTag();
		}
		
		viewHolder.name.setTypeface(rionaSansMedium);
		viewHolder.tv_round_btn.setTypeface(exoBold);
		viewHolder.description.setTypeface(rionaSansRegular);
		

		if (bookRightAfter)
			viewHolder.tv_round_btn.setText(context.getString(R.string.book));
		else
			viewHolder.tv_round_btn.setText(context.getString(R.string.select));

		CompanyItem item = items[position];

		viewHolder.name.setText(item.name);
		String prefixURL = context.getResources().getString(R.string.url);
		prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
		// String[] locArray = item.logo.split("/");
		// new DownloadLogoTask(prefixURL + item.logo, locArray[locArray.length - 1], viewHolder.icon,
		// context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		// new DownloadImageTask(viewHolder.icon).execute(prefixURL + item.logo);
		viewHolder.icon.setDefaultImageResId(R.drawable.launcher);
		viewHolder.icon.setImageUrl(prefixURL + item.logo, AppController.getInstance().getImageLoader());
		viewHolder.description.setText(item.description);

		String[] attrs = item.attributes.split(",");
		int marginRight = 10;
		Utils.showOption(viewHolder.ll_attr, attrs, context, marginRight);
		
//		if(position%2==1){
//			rowView.setBackgroundResource(R.drawable.list_background2_selector);
//		}
		
		
		// TL-88 add fare estimate if drop off address is set and baseRate set up
//		if (Utils.mDropoffAddress != null && item.baseRate != 0 && item.ratePerDistance != 0) {
//
//			if (Utils.hasHoneycomb()) {
//				// --post GB use serial executor by default --
//				new GetEstimateFareTask(viewHolder.estFare, item.baseRate, item.ratePerDistance).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
//						Utils.mPickupAddress.getLatitude() + "," + Utils.mPickupAddress.getLongitude(), Utils.mDropoffAddress.getLatitude() + ","
//								+ Utils.mDropoffAddress.getLongitude(), "driving");
//			} else {
//				// --GB uses ThreadPoolExecutor by default--
//				new GetEstimateFareTask(viewHolder.estFare, item.baseRate, item.ratePerDistance).execute(Utils.mPickupAddress.getLatitude() + ","
//						+ Utils.mPickupAddress.getLongitude(), Utils.mDropoffAddress.getLatitude() + "," + Utils.mDropoffAddress.getLongitude(), "driving");
//			}
//
//		} else {
//			viewHolder.estFare.setText("");
//		}

		return rowView;
	}

	public CompanyItem getCompanyItem(int i) {
		return items[i];
	}

}
