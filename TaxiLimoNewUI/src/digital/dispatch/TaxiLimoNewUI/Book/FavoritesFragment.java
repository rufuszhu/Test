package digital.dispatch.TaxiLimoNewUI.Book;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import digital.dispatch.TaxiLimoNewUI.BaseFragment;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao.Properties;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.GecoderGoogle;
import digital.dispatch.TaxiLimoNewUI.Utils.LocationUtils;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import digital.dispatch.TaxiLimoNewUI.Widget.SwipableListItem;

public class FavoritesFragment extends BaseFragment {

    private static final String TAG = "FavoritesFragment";
    private boolean logEnabled = false;
    //private View view;
    private FavoritesAdapter adapter;
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
        Logger.d(TAG, "onCreate");

        List<DBAddress> values = new ArrayList<>();
        adapter = new FavoritesAdapter(getActivity(), values);

    }

    private void setUp404(View view) {
        Typeface OpenSansSemibold = FontCache.getFont(getActivity(), "fonts/OpenSansSemibold.ttf");
        TextView tv_fav404_text = (TextView) view.findViewById(R.id.tv_fav404_text);
        tv_fav404_text.setTypeface(OpenSansSemibold);
        mListView.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    public View mGetView() {
        return getView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, "on RESUME");
        mListView = (ListView) getView().findViewById(R.id.fav_list);
        mListView.setAdapter(adapter);
        ((ModifyAddressActivity)getActivity()).getFavList();

        Utils.isInternetAvailable(getActivity());
        /*
        int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
        String usedMegsString = String.format(" - Memory Used: %d MB", usedMegs);
        Logger.d(TAG,  usedMegsString);
        */
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d(TAG, "on Pause");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbindDrawables(getView().findViewById(R.id.fav_frag));
        Logger.d(TAG, "onDestroyView");
    }


    //TL-369
    public void unbindDrawables(View view) {

        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }

        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setImageBitmap(null);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            if (!(view instanceof AdapterView))
                viewGroup.removeAllViews();
        }
    }

    /**
     * Handle touch events to lock list view scrolling during swipe and block multiple swipe
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        float mDownX;
        private int mSwipeSlop = -1;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
            }

            ((SwipableListItem) v).processDragEvent(event);

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

    public void notifyDataChange(DBAddress value) {
        adapter.addValues(value);
        // adapter.notifyDataSetChanged();
    }

    private class FavoritesAdapter extends ArrayAdapter<DBAddress> {

        private final Context context;
        private List<DBAddress> values;
        private Typeface fontFamily;
        private Typeface OpenSansSemibold;
        private Typeface OpenSansRegular;

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
            fontFamily = FontCache.getFont(getActivity(), "fonts/icon_pack.ttf");
            OpenSansSemibold = FontCache.getFont(getActivity(), "fonts/OpenSansSemibold.ttf");
            OpenSansRegular = FontCache.getFont(getActivity(), "fonts/OpenSansRegular.ttf");
        }

        @Override
        public int getCount() {
            int count = 0;
            if (values != null) {
                count = values.size();
            }
            return count;
        }

        @Override
        public DBAddress getItem(int i) {
            return values.get(i);
        }


        public class ViewHolder {
            public TextView address;
            public TextView title;
            public TextView delete_btn;
            public TextView edit_btn;
            public RelativeLayout viewHeader;
            public ViewGroup swipeContactView;
            public TextView green_circle_edit;
            public TextView green_circle_delete;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            // reuse views
            if (convertView == null) {

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.favorite_item, null);
                // configure view holder
                viewHolder = new ViewHolder();

                viewHolder.address = (TextView) convertView.findViewById(R.id.tv_address);
                viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.delete_btn = (TextView) convertView.findViewById(R.id.delete_btn);
                viewHolder.edit_btn = (TextView) convertView.findViewById(R.id.edit_btn);
                viewHolder.swipeContactView = (ViewGroup) convertView.findViewById(R.id.swipeContactView);
                viewHolder.viewHeader = (RelativeLayout) convertView.findViewById(R.id.viewHeader);

                viewHolder.green_circle_edit = (TextView) convertView.findViewById(R.id.green_circle_edit);
                viewHolder.green_circle_delete = (TextView) convertView.findViewById(R.id.green_circle_delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // fill data

            final ViewHolder holder = (ViewHolder) convertView.getTag();

            holder.title.setTypeface(OpenSansSemibold);
            holder.title.setText(values.get(position).getNickName());

            holder.address.setTypeface(OpenSansRegular);
            holder.address.setText(values.get(position).getFullAddress());

            final View temp = convertView;

            holder.edit_btn.setTypeface(fontFamily);
            holder.edit_btn.setText(MBDefinition.icon_pencil);

            holder.delete_btn.setTypeface(fontFamily);
            holder.delete_btn.setText(MBDefinition.icon_delete);

            holder.edit_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Animation pop = AnimationUtils.loadAnimation(context, R.anim.pop);
                    pop.setFillAfter(true);
                    pop.setAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            setUpEditNickNameDialog(values.get(position), position);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    holder.green_circle_edit.setVisibility(View.VISIBLE);
                    holder.green_circle_edit.startAnimation(pop);

                }
            });

            holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Animation pop = AnimationUtils.loadAnimation(context, R.anim.pop);
                    pop.setFillAfter(true);
                    pop.setAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(getActivity().getString(R.string.warning));
                            builder.setMessage(getActivity().getString(R.string.delete_confirmation));
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DaoManager daoManager = DaoManager.getInstance(getActivity());
                                    DBAddressDao addressDao = daoManager.getAddressDao(DaoManager.TYPE_READ);
                                    DBAddress dbAddress = addressDao.queryBuilder().where(Properties.Id.eq(values.get(position).getId())).list().get(0);
                                    values.remove(position);
                                    addressDao.delete(dbAddress);
                                    ((ModifyAddressActivity)getActivity()).getFavList();

                                    Toast.makeText(getActivity(), dbAddress.getNickName() + getActivity().getString(R.string.delete_successful),
                                            Toast.LENGTH_SHORT).show();
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

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    holder.green_circle_delete.setVisibility(View.VISIBLE);
                    holder.green_circle_delete.startAnimation(pop);

                }
            });

            if (position % 2 == 1) {
                holder.viewHeader.setBackgroundResource(R.drawable.list_background2_selector);
            }

            holder.swipeContactView.setOnTouchListener(mTouchListener);
            holder.viewHeader.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ValidateAddressTask(getActivity()).execute(adapter.getValues().get(position).getFullAddress());
                }
            });
            return convertView;
        }
    }

    public void notifyChange(List<DBAddress> favList) {
        adapter.values = favList;
        adapter.notifyDataSetChanged();

        RelativeLayout llfav404 = (RelativeLayout) getView().findViewById(R.id.llfav404);
        if (adapter.getCount() > 0) {

            if (canOpenTutorial(FavoritesFragment.class.getSimpleName())) {
                showToolTip(getActivity().getString(R.string.tooltip_favorite), FavoritesFragment.class.getSimpleName());
            }
            llfav404.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);

        } else {
            hideToolTip();
            setUp404(getView());
            llfav404.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }



protected class ValidateAddressTask extends AsyncTask<String, Void, List<Address>> {

    // Store the context passed to the AsyncTask when the system instantiates it.
    Context localContext;

    // Constructor called by the system to instantiate the task
    public ValidateAddressTask(Context context) {

        // Required by the semantics of AsyncTask
        super();

        // Set a Context for the background task
        localContext = context;
    }

    /**
     * Get a geocoding service instance, pass latitude and longitude to it, format the returned address, and return the address to the UI thread.
     */
    @Override
    protected List<Address> doInBackground(String... params) {


        // Get the current location from the input parameter list
        String locationName = params[0];

        // Create a list to contain the result address
        List<Address> addresses = null;

        // Try to get an address for the current location. Catch IO or network problems.
        try {

				/*
                 * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform
				 * to address standards can also be used.
				 */
            Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());
				/*
				 * Call the synchronous getFromLocation() method with the latitude and longitude of the current location. Return at most 1 address.
				 */
            if (geocoder != null)
                addresses = geocoder.getFromLocationName(locationName, 10);

            // Catch network or other I/O problems.
        } catch (Exception e) {
            e.printStackTrace();
            // Log an error
            Logger.e(TAG, "geocoder failed , moving on to HTTP");
        }

        //If the geocoder returned an address
        if (addresses != null && addresses.size() > 0) {
            return addresses;
        } else {
            //try HTTP lookup to the maps API
            GecoderGoogle mGecoderGoogle = new GecoderGoogle(localContext, Locale.getDefault(), logEnabled);

            try {
                addresses = mGecoderGoogle.getFromLocationName(locationName, 10/* MaxResult */);
            } catch (IOException exception1) {

                // Log an error
                Logger.e(TAG, getString(R.string.IO_Exception_getFromLocation));

                // print the stack trace
                exception1.printStackTrace();


                // Catch incorrect latitude or longitude values
            } catch (IllegalArgumentException exception2) {

                // Construct a message containing the invalid arguments
                String errorString = getString(R.string.illegal_argument_exception, locationName);
                // Log the error and print the stack trace
                Logger.e(TAG, errorString);
                exception2.printStackTrace();

            } catch (Exception e) {
                Logger.e(TAG, "other exception");
                e.printStackTrace();
            }

            return addresses;
        }
    }

    /**
     * A method that's called once doInBackground() completes. Set the text of the UI element that displays the address. This method runs on the UI thread.
     */
    @Override
    protected void onPostExecute(List<Address> addresses) {
        if (isAdded()) { //added to avoid NullPointerException

            if (addresses == null) {
                Utils.showMessageDialog(getActivity().getString(R.string.cannot_get_address_from_google), getActivity());
            } else if (addresses.size() > 1) {
                // pop up list
                setUpListDialog(getActivity(), LocationUtils.addressListToStringList(getActivity(), addresses), addresses);
            } else if (addresses.size() == 1) {

                if (((ModifyAddressActivity) getActivity()).getIsDesitination())
                    Utils.mDropoffAddress = addresses.get(0);
                else {
                    Utils.pickupHouseNumber = "";
                    Utils.mPickupAddress = addresses.get(0);
                    Intent intent = new Intent(getActivity(), BookActivity.class);
                    ((ModifyAddressActivity) getActivity()).startActivityForAnim(intent);
                }
                getActivity().finish();

            } else {
                Utils.showErrorDialog(getActivity().getString(R.string.err_invalid_street_name), getActivity());

            }
        }
    }

}

    private void setUpListDialog(final Context context, final ArrayList<String> addresses, final List<Address> addressesObj) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        // builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Please be more specific");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, R.layout.autocomplete_list_item);
        arrayAdapter.addAll(addresses);
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (((ModifyAddressActivity) getActivity()).getIsDesitination())
                    Utils.mDropoffAddress = addressesObj.get(which);
                else {
                    Utils.pickupHouseNumber = "";
                    Utils.mPickupAddress = addressesObj.get(which);
                    Intent intent = new Intent(getActivity(), BookActivity.class);
                    ((ModifyAddressActivity) getActivity()).startActivityForAnim(intent);
                }
                getActivity().finish();
            }
        });
        builderSingle.show();
    }

    private void setUpEditNickNameDialog(final DBAddress address, final int position) {
        final EditText nickname_edit;
        TextView address_text;
        TextView cancel;
        TextView add;
        final Dialog nicknameDialog = new Dialog(getActivity());
        nicknameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nicknameDialog.setContentView(R.layout.nickname_dialog);
        nicknameDialog.setCanceledOnTouchOutside(true);

        address_text = (TextView) nicknameDialog.getWindow().findViewById(R.id.addr);
        nickname_edit = (EditText) nicknameDialog.getWindow().findViewById(R.id.nickname);
        address_text.setText(address.getFullAddress());

        cancel = (TextView) nicknameDialog.getWindow().findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nickname_edit.getWindowToken(), 0);
                nicknameDialog.dismiss();
            }
        });
        add = (TextView) nicknameDialog.getWindow().findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nickname = nickname_edit.getText().toString();
                if (nickname.length() == 0) {
                    nickname_edit.setError(getActivity().getString(R.string.nickName_cannot_be_empty));
                } else {
                    address.setNickName(nickname);
                    DaoManager daoManager = DaoManager.getInstance(getActivity());
                    DBAddressDao addressDao = daoManager.getAddressDao(DaoManager.TYPE_READ);
                    addressDao.update(address);
                    Toast.makeText(getActivity(), address.getNickName() + " is successfully updated", Toast.LENGTH_SHORT).show();
                    ((ModifyAddressActivity)getActivity()).getFavList();
                    ((SwipableListItem) mListView.getChildAt(position).findViewById(R.id.swipeContactView)).maximize();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nickname_edit.getWindowToken(), 0);
                    nicknameDialog.dismiss();
                }
            }
        });
        nicknameDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        nicknameDialog.show();
    }
}