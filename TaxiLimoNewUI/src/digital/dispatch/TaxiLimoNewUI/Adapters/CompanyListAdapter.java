package digital.dispatch.TaxiLimoNewUI.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.digital.dispatch.TaxiLimoSoap.responses.CompanyItem;

import java.util.List;

import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBPreference;
import digital.dispatch.TaxiLimoNewUI.DBPreferenceDao;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Drawers.CompanyPreferenceActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.AppController;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class CompanyListAdapter extends ArrayAdapter<CompanyItem> {

    private static final java.lang.String TAG = "CompanyListAdapter";
    private final Context context;
	public CompanyItem[] items;
	boolean isFromPreference;
	private Typeface OpenSansSemibold;
	private Typeface OpenSansRegular;
	//private Typeface rionaSansBold;
	private Typeface exoBold;
	

	public CompanyListAdapter(Context context, CompanyItem[] items, boolean isFromPreference) {
		super(context, R.layout.company_list_item, items);
		this.context = context;
		this.items = items;
		this.isFromPreference = isFromPreference;
        OpenSansSemibold = FontCache.getFont(context, "fonts/OpenSansSemibold.ttf");
        OpenSansRegular = FontCache.getFont(context, "fonts/OpenSansRegular.ttf");

		exoBold = FontCache.getFont(context, "fonts/Exo2-Bold.ttf");
	}

	public static class ViewHolder {
		public NetworkImageView icon;
		public TextView name;
		public TextView description;
		public LinearLayout ll_attr;
        public FrameLayout icon_preferred;
		//public TextView tv_round_btn;
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
            viewHolder.icon_preferred = (FrameLayout) rowView.findViewById(R.id.icon_preferred);
			//TL-333
			//viewHolder.tv_round_btn = (TextView) rowView.findViewById(R.id.tv_round_btn);
			//viewHolder.estFare = (TextView) rowView.findViewById(R.id.tv_est_fare);

			rowView.setTag(viewHolder);
		} else {
			// fill data
			viewHolder = (ViewHolder) rowView.getTag();
		}
		
		viewHolder.name.setTypeface(OpenSansSemibold);
		//viewHolder.tv_round_btn.setTypeface(exoBold);
		viewHolder.description.setTypeface(OpenSansRegular);


		CompanyItem item = items[position];

		viewHolder.name.setText(item.name);
		String prefixURL = context.getResources().getString(R.string.url);
		prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
		viewHolder.icon.setDefaultImageResId(R.drawable.launcher);
		viewHolder.icon.setImageUrl(prefixURL + item.logo, AppController.getInstance().getImageLoader());
		viewHolder.description.setText(item.description);

		String[] attrs = item.attributes.split(",");
		int marginRight = 10;
		Utils.showOption(viewHolder.ll_attr, attrs, context, marginRight);
        viewHolder.icon_preferred.setVisibility(View.GONE);
        //printPreferCompany();
        String city;
        String province;
        if(isFromPreference){
           city = ((CompanyPreferenceActivity)context).getCity();
           province = ((CompanyPreferenceActivity)context).getProvince() ;
        }
        else{
            city = Utils.mPickupAddress.getLocality();
            province = LocationUtils.states.get(Utils.mPickupAddress.getAdminArea());
        }
        if(isPreferedCompany(item.destID, city, province)){
            viewHolder.icon_preferred.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.icon_preferred.setVisibility(View.GONE);
        }
		return rowView;
	}

	public CompanyItem getCompanyItem(int i) {
		return items[i];
	}
    @Override
    public CompanyItem getItem(int i) {
        return items[i];
    }

    public boolean isPreferedCompany(String destID, String city, String province){
        DaoManager daoManager = DaoManager.getInstance(context);
        DBPreferenceDao preferenceDao = daoManager.getDBPreferenceDao(DaoManager.TYPE_READ);
        List<DBPreference> preferenceList = preferenceDao.queryBuilder().list();
        for(int i=0; i<preferenceList.size(); i++){
            if(preferenceList.get(i).getDestId().equalsIgnoreCase(destID)
                    && preferenceList.get(i).getCity().equalsIgnoreCase(city)
                        && preferenceList.get(i).getState().equalsIgnoreCase(province))
            return true;
        }
        return false;
    }

    public boolean printPreferCompany(){
        DaoManager daoManager = DaoManager.getInstance(context);
        DBPreferenceDao preferenceDao = daoManager.getDBPreferenceDao(DaoManager.TYPE_READ);
        List<DBPreference> preferenceList = preferenceDao.queryBuilder().list();
        for(int i=0; i<preferenceList.size(); i++){
            Logger.e(TAG, "current prefered: " + preferenceList.get(i).getCompanyName());
        }
        return false;
    }
    @Override
    public int getCount() {
        int count = 0;
        if (items != null) {
            count = items.length;
        }
        return count;
    }

}
