package digital.dispatch.TaxiLimoNewUI.Adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Book.MyAddress;
import digital.dispatch.TaxiLimoNewUI.Utils.ImageLoader;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;

public class ContactExpandableListAdapter extends BaseExpandableListAdapter {
	private static final String TAG = "ContactExpandableListAdapter";
	private Context _context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<MyAddress>> _listDataChild;
	private ImageLoader mImageLoader;
	

	public ContactExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<MyAddress>> listChildData, ImageLoader mImageLoader) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		this.mImageLoader = mImageLoader;
	}
	
	public void updateFavlist(ArrayList<MyAddress> ma){
		_listDataChild.get("Favorites").clear();
		_listDataChild.get("Favorites").addAll(ma);
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@SuppressLint("NewApi")
	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		final MyAddress mAddr = (MyAddress) getChild(groupPosition, childPosition);
		final String name = mAddr.getName();
		final String addr = mAddr.getAddress();
		final Uri uri = mAddr.getImg_URI();

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group_item, null);
		}

		TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		TextView tv_addr = (TextView) convertView.findViewById(R.id.tv_address);
		ImageView profile = (ImageView) convertView.findViewById(R.id.profile_icon);
		if(groupPosition==1){
			
			mImageLoader.loadImage(uri.toString()+"/photo", profile);
		}
		else{
			mImageLoader.loadImage(null, profile);
		}
		

		tv_name.setText(name);
		tv_addr.setText(addr);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}

		TextView lblListHeader = (TextView) convertView.findViewById(R.id.list_group_text);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
