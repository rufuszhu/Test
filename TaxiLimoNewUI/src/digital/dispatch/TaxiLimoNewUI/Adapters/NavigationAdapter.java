package digital.dispatch.TaxiLimoNewUI.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;


public class NavigationAdapter extends ArrayAdapter<NavigationItemAdapter> {

	private ViewHolder holder;	
	private static Context context;
	private Typeface ex2Medium;
	public NavigationAdapter(Context context) {
		super(context, 0);
		this.context = context;
		ex2Medium = FontCache.getFont(context, "fonts/Exo2-Medium.ttf");
	}


	public void addItem(String title, int icon) {
		add(new NavigationItemAdapter(title, icon));
	}

	public void addItem(NavigationItemAdapter itemModel) {
		add(itemModel);
	}

	
	public static class ViewHolder {
		public final ImageView icon;		
		public final TextView title;		
		public final LinearLayout colorLinear;
		private Typeface ex2Medium;

		public ViewHolder(TextView title, ImageView icon, LinearLayout colorLinear) {
			this.title = title;
			this.icon = icon;			
			this.colorLinear = colorLinear;
			
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		holder = null;		
		View view = convertView;
		NavigationItemAdapter item = getItem(position);
		
		if (view == null) {

			int layout = 0;
			layout = R.layout.navigation_item;			


			view = LayoutInflater.from(getContext()).inflate(layout, null);

			TextView txttitle = (TextView) view.findViewById(R.id.title);
			ImageView imgIcon = (ImageView) view.findViewById(R.id.icon);
			txttitle.setTypeface(ex2Medium);
			LinearLayout linearColor = (LinearLayout) view.findViewById(R.id.ns_menu_row);
			view.setTag(new ViewHolder(txttitle, imgIcon, linearColor));
		}
		
		if (holder == null && view != null) {
			Object tag = view.getTag();
			if (tag instanceof ViewHolder) {
				holder = (ViewHolder) tag;
			}
		}
				
		if (item != null && holder != null) {
			if (holder.title != null)
				holder.title.setText(item.title);
		}
		
		if (holder.icon != null) {
			if (item.icon != 0) {
				holder.icon.setVisibility(View.VISIBLE);
				holder.icon.setImageResource(item.icon);
			} else {
				holder.icon.setVisibility(View.GONE);
			}
		}
	    
	    return view;		
	}

}