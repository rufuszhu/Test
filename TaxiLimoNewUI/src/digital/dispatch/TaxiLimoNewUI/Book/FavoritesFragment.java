package digital.dispatch.TaxiLimoNewUI.Book;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao.Properties;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class FavoritesFragment extends ListFragment {

	private static final String TAG = "FavoritesFragment";
	private View view;
	private FavoritesAdapter adapter;
	private DaoManager daoManager;
	private DBAddressDao addressDao;
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static FavoritesFragment newInstance() {
		FavoritesFragment fragment = new FavoritesFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.e(TAG,"onCreate");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_favorites, container, false);
		return view;
	}
	
	public View mGetView(){
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
		daoManager = DaoManager.getInstance(getActivity());
		addressDao = daoManager.getAddressDao(DaoManager.TYPE_READ);
		List<DBAddress> values = addressDao.queryBuilder()
				.where(Properties.IsFavoriate.eq(true)).orderDesc(Properties.NickName).list();
		
		adapter = new FavoritesAdapter(getActivity(), values);
		setListAdapter(adapter);
		
		Utils.isInternetAvailable(getActivity());
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		((ModifyAddressActivity) getActivity()).updateAddress(adapter.getValues().get(position).getFullAddress());
		((ModifyAddressActivity) getActivity()).showDeleteBtn(adapter.getValues().get(position));
	}
	
	


	
	private class FavoritesAdapter extends ArrayAdapter<DBAddress> {

		private final Context context;
		private List<DBAddress> values;

		public List<DBAddress> getValues() {
			return values;
		}

		public void setValues(List<DBAddress> values) {
			this.values = values;
		}

		public FavoritesAdapter(Context context, List<DBAddress> values) {
			super(context, R.layout.favorite_item, values);
			this.context = context;
			this.values = values;
		}

		public class ViewHolder {
			public TextView address;
			public TextView title;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			// reuse views
			if (rowView == null) {

				rowView = LayoutInflater.from(getContext()).inflate(R.layout.favorite_item, null);
				// configure view holder
				ViewHolder viewHolder = new ViewHolder();

				viewHolder.address = (TextView) rowView.findViewById(R.id.tv_address);
				viewHolder.title = (TextView) rowView.findViewById(R.id.tv_title);
				rowView.setTag(viewHolder);
			}

			// fill data
			ViewHolder holder = (ViewHolder) rowView.getTag();
			holder.address.setText(values.get(position).getFullAddress());
			holder.title.setText(values.get(position).getNickName());

			return rowView;
		}
	}



	public void notifyChange() {
		List<DBAddress> values = addressDao.queryBuilder()
				.where(Properties.IsFavoriate.eq(true)).orderDesc(Properties.NickName).list();
		adapter = new FavoritesAdapter(getActivity(), values);
		setListAdapter(adapter);
	}

}