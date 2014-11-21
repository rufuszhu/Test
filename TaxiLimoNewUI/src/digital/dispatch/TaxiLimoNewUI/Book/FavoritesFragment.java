package digital.dispatch.TaxiLimoNewUI.Book;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao.Properties;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import digital.dispatch.TaxiLimoNewUI.Widget.SwipableListItem;

public class FavoritesFragment extends ListFragment {

	private static final String TAG = "FavoritesFragment";
	private View view;
	private FavoritesAdapter adapter;
	private DaoManager daoManager;
	private DBAddressDao addressDao;
	private ListView mListView;
	private boolean mSwiping = false;
	private boolean mItemPressed = false;
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
		mListView = (ListView) view.findViewById(android.R.id.list);
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
//		((ModifyAddressActivity) getActivity()).updateAddress(adapter.getValues().get(position).getFullAddress());
//		((ModifyAddressActivity) getActivity()).showDeleteBtn(adapter.getValues().get(position));
	}
	
	
	/**
	 * Handle touch events to lock list view swiping during swipe and block multiple swipe
	 */
	private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

		float mDownX;
		private int mSwipeSlop = -1;

		@Override
		public boolean onTouch(final View v, MotionEvent event) {
			if (mSwipeSlop < 0) {
				mSwipeSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
			}
			
			
			((SwipableListItem) (v.findViewById(R.id.swipeContactView))).processDragEvent(event);
			

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mItemPressed) {
					// Multi-item swipes not handled
					return false;
				}
				mItemPressed = true;
				mDownX = event.getX();
				break;
			case MotionEvent.ACTION_CANCEL:
				mItemPressed = false;
				break;
			case MotionEvent.ACTION_MOVE: {
				float x = event.getX() + v.getTranslationX();
				float deltaX = x - mDownX;
				float deltaXAbs = Math.abs(deltaX);
				if (!mSwiping) {
					if (deltaXAbs > mSwipeSlop) {
						mSwiping = true;
						mListView.requestDisallowInterceptTouchEvent(true);
					}
				}
				if (mSwiping) {
					// ((SwipableListItem)(v.findViewById(R.id.swipeContactView))).processDragEvent(event);
				}
			}
				break;
			case MotionEvent.ACTION_UP: {
				mSwiping = false;
				mItemPressed = false;
				break;
			}
			default:
				return false;
			}
			return true;
		}
	};
	
	public void notifyDataChange(DBAddress value){
		adapter.addValues(value);
		adapter.notifyDataSetChanged();
	}

	
	private class FavoritesAdapter extends ArrayAdapter<DBAddress> {

		private final Context context;
		private List<DBAddress> values;

		public List<DBAddress> getValues() {
			return values;
		}

		public void addValues(DBAddress value) {
			this.values.add(value);
		}

		public FavoritesAdapter(Context context, List<DBAddress> values) {
			super(context, R.layout.favorite_item, values);
			this.context = context;
			this.values = values;
		}

		public class ViewHolder {
			public TextView address;
			public TextView title;
			public RelativeLayout delete_btn;
		}


		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			// reuse views
			if (rowView == null) {

				rowView = LayoutInflater.from(getContext()).inflate(R.layout.favorite_item, null);
				// configure view holder
				ViewHolder viewHolder = new ViewHolder();

				viewHolder.address = (TextView) rowView.findViewById(R.id.tv_address);
				viewHolder.title = (TextView) rowView.findViewById(R.id.tv_title);
				viewHolder.delete_btn = (RelativeLayout) rowView.findViewById(R.id.contact_option);
				rowView.setTag(viewHolder);
			}

			// fill data
			ViewHolder holder = (ViewHolder) rowView.getTag();
			holder.address.setText(values.get(position).getFullAddress());
			holder.title.setText(values.get(position).getNickName());
			final View temp = rowView;
			holder.delete_btn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle(getActivity().getString(R.string.warning));
					builder.setMessage(getActivity().getString(R.string.delete_confirmation));
					builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							DBAddress dbAddress = addressDao.queryBuilder().where(Properties.FullAddress.eq(values.get(position).getFullAddress())).list().get(0);
							values.remove(position);							
							addressDao.delete(dbAddress);
							adapter.notifyDataSetChanged();
							Toast.makeText(getActivity(), dbAddress.getNickName() + getActivity().getString(R.string.delete_successful), Toast.LENGTH_SHORT).show();
							((SwipableListItem) (temp.findViewById(R.id.swipeContactView))).maximize();
						}
					});
					
					builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.show();
				}
			});
			rowView.setOnTouchListener(mTouchListener);
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