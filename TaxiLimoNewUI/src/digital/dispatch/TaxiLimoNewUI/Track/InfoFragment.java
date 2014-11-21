package digital.dispatch.TaxiLimoNewUI.Track;

import java.util.List;

import com.android.volley.toolbox.NetworkImageView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBAddressDao.Properties;
import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.DaoManager.DaoManager;
import digital.dispatch.TaxiLimoNewUI.Task.SendDriverMsgTask;
import digital.dispatch.TaxiLimoNewUI.Utils.AppController;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class InfoFragment extends Fragment {

	private static final String TAG = "InfoFragment";
	private View view;
	private TextView tv_company_name;
	private TextView tv_company_description;
	private TextView tv_company_address;
	private TextView tv_company_website;
	private TextView tv_company_phone;
	private TextView call_company_btn;
	private TextView tv_driver;
	private TextView text_driver_btn;
	private TextView tv_car_type;
	private TextView tv_car_num;
	private LinearLayout ll_attr;
	private NetworkImageView company_icon;
	private DBBooking book;
	
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static InfoFragment newInstance() {
		InfoFragment fragment = new InfoFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.e(TAG, "onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_info, container, false);
		book = ((TrackDetailActivity)getActivity()).getDBBook();
		findView();
		fillValue();
		bindEvent();
		
		return view;
	}



	private void findView() {
		tv_company_name = (TextView) view.findViewById(R.id.tv_company_name);
		tv_company_description = (TextView) view.findViewById(R.id.tv_company_description);
		tv_company_address = (TextView) view.findViewById(R.id.tv_company_address);
		tv_company_website = (TextView) view.findViewById(R.id.tv_company_website);
		tv_company_phone = (TextView) view.findViewById(R.id.tv_company_phone);
		call_company_btn = (TextView) view.findViewById(R.id.call_company_btn);
		tv_driver = (TextView) view.findViewById(R.id.tv_driver);
		text_driver_btn = (TextView) view.findViewById(R.id.text_driver_btn);
		tv_car_type = (TextView) view.findViewById(R.id.tv_car_type);
		tv_car_num = (TextView) view.findViewById(R.id.tv_car_num);
		ll_attr = (LinearLayout) view.findViewById(R.id.ll_attr);
		company_icon = (NetworkImageView) view.findViewById(R.id.company_icon);
	}
	
	private void fillValue() {
		String prefixURL = getActivity().getResources().getString(R.string.url);
		prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
		company_icon.setDefaultImageResId(R.drawable.launcher);
		company_icon.setImageUrl(prefixURL + book.getCompany_icon(), AppController.getInstance().getImageLoader());
		
		tv_company_name.setText(book.getCompany_name());
		tv_company_phone.setText(book.getCompany_phone_number());
		tv_company_description.setText(book.getCompany_description());
		//tv_company_address.setText(text);
		
		
		updateDriverAndVehicle();
		
		Utils.showOption(ll_attr, book.getAttributeList().split(","), getActivity(), 10);
	}
	
	public void updateDriverAndVehicle(){
		book = ((TrackDetailActivity)getActivity()).getDBBook();
		if(book.getDispatchedDriver()==null || book.getDispatchedDriver().length()==0){
			tv_driver.setText(getActivity().getString(R.string.driver_info_not_available));
			text_driver_btn.setVisibility(View.GONE);
		}
		else{
			tv_driver.setText(book.getDispatchedDriver());
			text_driver_btn.setVisibility(View.VISIBLE);
		}
		
		if(book.getDispatchedCar()==null || book.getDispatchedCar().length()==0)
			tv_car_num.setText(getActivity().getString(R.string.vehicle_info_not_available));
		else
			tv_car_num.setText(book.getDispatchedCar());
	}

	private void bindEvent() {
		call_company_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + book.getCompany_phone_number()));
				startActivity(intent);
			}
		});

		text_driver_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setUpDriverNoteDialog();
			}
		});
	}

	public View mGetView() {
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.e(TAG, "on RESUME");
		
	}
	
	public void setUpDriverNoteDialog() {
		final EditText driverMessage;
		final TextView textRemaining;
		TextView ok;
		TextView clear;
		final Dialog messageDialog = new Dialog(getActivity());
		messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		messageDialog.setContentView(R.layout.dialog_driver_message);
		messageDialog.setCanceledOnTouchOutside(true);
		messageDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		messageDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		driverMessage = (EditText) messageDialog.getWindow().findViewById(R.id.message);
		textRemaining = (TextView) messageDialog.getWindow().findViewById(R.id.text_remaining);

		driverMessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable note) {
				textRemaining.setText(MBDefinition.DRIVER_NOTE_MAX_LENGTH - note.length() + " Charaters Left");
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
		});

		ok = (TextView) messageDialog.getWindow().findViewById(R.id.ok);
		ok.setText("Send");
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (driverMessage.getText().toString().length() == 0) {
					Utils.showErrorDialog(getActivity().getString(R.string.err_empty_msg), getActivity());
				} else {
					new SendDriverMsgTask(book.getTaxi_ride_id() + "", book.getSysId(), book.getDestID(), driverMessage.getText().toString(), getActivity())
							.execute();
					messageDialog.dismiss();
				}
			}
		});
		clear = (TextView) messageDialog.getWindow().findViewById(R.id.clear);
		clear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				driverMessage.setText("");
			}
		});

		messageDialog.show();
	}

}