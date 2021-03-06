package digital.dispatch.TaxiLimoNewUI.Track;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import digital.dispatch.TaxiLimoNewUI.DBBooking;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.SendDriverMsgTask;
import digital.dispatch.TaxiLimoNewUI.Utils.AppController;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class InfoFragment extends Fragment {

    private static final String TAG = "InfoFragment";
    private View view;
    private TextView tv_company_name;
    private TextView tv_company_description;
    private LinearLayout call_company_btn;
    private TextView tv_driver;
    private TextView icon_text;
    private TextView icon_call;
    private TextView tv_text;
    private TextView tv_call;
    private LinearLayout text_driver_btn;
    private TextView tv_car_num;
    private LinearLayout ll_attr;
    private NetworkImageView company_icon;
    private DBBooking book;
    private TextView company_title;
    private TextView itinerary_title;
    private TextView option_title;
    private TextView tv_from;
    private TextView tv_to;
    private TextView tv_pickup_time;
    private TextView icon_pickup;
    private TextView icon_dropoff;
    private TextView tv_waiting;
    private ImageView icon_taxi;
    private View space_diver_car;
    private TextView icon_male_user;

    private TextView icon_calander;

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
        Logger.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info, container, false);
        book = ((TrackDetailActivity) getActivity()).getDBBook();
        findViewAndSetFont();
        fillValue();
        bindEvent();

        return view;
    }

    private void findViewAndSetFont() {
        tv_company_name = (TextView) view.findViewById(R.id.tv_company_name);
        tv_company_description = (TextView) view.findViewById(R.id.tv_company_description);
        call_company_btn = (LinearLayout) view.findViewById(R.id.call_company_btn);
        tv_driver = (TextView) view.findViewById(R.id.tv_driver);
        text_driver_btn = (LinearLayout) view.findViewById(R.id.text_driver_btn);
        tv_car_num = (TextView) view.findViewById(R.id.tv_car_num);
        ll_attr = (LinearLayout) view.findViewById(R.id.ll_attr);
        company_icon = (NetworkImageView) view.findViewById(R.id.company_icon);
        icon_text = (TextView) view.findViewById(R.id.icon_text);
        icon_call = (TextView) view.findViewById(R.id.icon_call);
        tv_text = (TextView) view.findViewById(R.id.tv_text);
        tv_call = (TextView) view.findViewById(R.id.tv_call);
        company_title = (TextView) view.findViewById(R.id.company_title);
        itinerary_title = (TextView) view.findViewById(R.id.itinerary_title);
        option_title = (TextView) view.findViewById(R.id.option_title);
        tv_from = (TextView) view.findViewById(R.id.tv_pickup_address);
        tv_to = (TextView) view.findViewById(R.id.tv_dropoff_address);
        tv_pickup_time = (TextView) view.findViewById(R.id.tv_pickup_time);
        icon_pickup = (TextView) view.findViewById(R.id.icon_pickup);
        icon_dropoff = (TextView) view.findViewById(R.id.icon_dropoff);
        icon_calander = (TextView) view.findViewById(R.id.icon_calander);
        tv_waiting = (TextView) view.findViewById(R.id.tv_waiting);
        icon_taxi = (ImageView) view.findViewById(R.id.icon_taxi);
        space_diver_car = (View) view.findViewById(R.id.space_diver_car);
        icon_male_user = (TextView) view.findViewById(R.id.icon_male_user);


        Typeface icon_pack = FontCache.getFont(getActivity(), "fonts/icon_pack.ttf");
        Typeface fontawesome = FontCache.getFont(getActivity(), "fonts/fontawesome.ttf");
		Typeface openSansSemibold = FontCache.getFont(getActivity(), "fonts/OpenSansSemibold.ttf");
        Typeface exo2Light = FontCache.getFont(getActivity(), "fonts/Exo2-Light.ttf");
        Typeface exo2SemiBold = FontCache.getFont(getActivity(), "fonts/Exo2-SemiBold.ttf");


        tv_company_name.setTypeface(openSansSemibold, Typeface.NORMAL);
        tv_company_description.setTypeface(openSansSemibold, Typeface.ITALIC);
        tv_driver.setTypeface(openSansSemibold, Typeface.NORMAL);
        tv_car_num.setTypeface(openSansSemibold, Typeface.NORMAL);
        tv_text.setTypeface(exo2SemiBold, Typeface.NORMAL);
        tv_call.setTypeface(exo2SemiBold, Typeface.BOLD);
        company_title.setTypeface(openSansSemibold);
        itinerary_title.setTypeface(openSansSemibold);
        option_title.setTypeface(openSansSemibold);

        tv_from.setTypeface(openSansSemibold);
        tv_to.setTypeface(openSansSemibold);
        tv_pickup_time.setTypeface(openSansSemibold);


        icon_text.setTypeface(icon_pack);
        icon_call.setTypeface(icon_pack);
        icon_dropoff.setTypeface(icon_pack);
        icon_pickup.setTypeface(fontawesome);
        icon_calander.setTypeface(icon_pack);

        icon_pickup.setText(MBDefinition.ICON_PERSON);
        icon_dropoff.setText(MBDefinition.icon_location);
        icon_calander.setText(MBDefinition.icon_tab_calendar);

        icon_text.setText(MBDefinition.ICON_COMMENT);
        icon_call.setText(MBDefinition.icon_phone);

        tv_waiting.setTypeface(openSansSemibold, Typeface.NORMAL);
        icon_male_user.setTypeface(icon_pack);

        icon_male_user.setText(MBDefinition.ICON_MALE_USER);
    }

    private void fillValue() {
        String prefixURL = getActivity().getResources().getString(R.string.url);
        prefixURL = prefixURL.substring(0, prefixURL.lastIndexOf("/"));
        company_icon.setDefaultImageResId(R.drawable.launcher);
        company_icon.setImageUrl(prefixURL + book.getCompany_icon(), AppController.getInstance().getImageLoader());

        tv_company_name.setText(book.getCompany_name());
        tv_company_description.setText(book.getCompany_description());

        tv_from.setText(book.getPickupAddress());

        if (book.getDropoffAddress() != null && book.getDropoffAddress().length() > 0) {
            if (book.getDropoff_unit() != null && !book.getDropoff_unit().equals("")) {
                tv_to.setText(book.getDropoff_unit() + "-" + book.getDropoffAddress());
            } else
                tv_to.setText(book.getDropoffAddress());
        } else
            tv_to.setText("Destination Not Given");

        try {
            if (book.getPickup_time() != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Date date = format.parse(book.getPickup_time());
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a MMM dd, yyyy", Locale.US);
                String dateString = dateFormat.format(date);
                tv_pickup_time.setText(dateString);
            } else
                tv_pickup_time.setText("As soon as possible");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        updateDriverAndVehicle();

        Utils.showOption(ll_attr, book.getAttributeList().split(","), getActivity(), 10);
    }

    public void updateDriverAndVehicle() {


        book = ((TrackDetailActivity) getActivity()).getDBBook();
        //if both no car and no driver available, show awaiting dispatch
        if((book.getDispatchedCar() == null || book.getDispatchedCar().length() == 0 || book.getDispatchedCar().equals("0"))
                && (book.getDispatchedDriver() == null || book.getDispatchedDriver().length()==0) || book.getDispatchedDriver().equals("0")){
            tv_waiting.setVisibility(View.VISIBLE);
            icon_taxi.setVisibility(View.GONE);
            space_diver_car.setVisibility(View.GONE);
            icon_male_user.setVisibility(View.GONE);
            tv_driver.setVisibility(View.GONE);
            tv_car_num.setVisibility(View.GONE);
        }
        //TL-326 text driver button should be shown when status is dispatched or arrived, even when driver information is not available.
        //else if only driver is available
        else if ((book.getDispatchedDriver() != null && book.getDispatchedDriver().length()>0 && !book.getDispatchedDriver().equals("0"))
                && (book.getDispatchedCar() == null || book.getDispatchedCar().length() == 0) || book.getDispatchedCar().equals("0")){
            tv_waiting.setVisibility(View.GONE);
            icon_taxi.setVisibility(View.GONE);
            space_diver_car.setVisibility(View.GONE);
            icon_male_user.setVisibility(View.VISIBLE);
            tv_driver.setVisibility(View.VISIBLE);
            tv_car_num.setVisibility(View.GONE);
            tv_driver.setText(book.getDispatchedDriver());
        }
        //else if only car is available
        else if((book.getDispatchedDriver() == null || book.getDispatchedDriver().equals("0") || book.getDispatchedDriver().length()==0)
                && (book.getDispatchedCar() != null && book.getDispatchedCar().length() > 0 && !book.getDispatchedCar().equals("0"))){
            tv_waiting.setVisibility(View.GONE);
            icon_taxi.setVisibility(View.VISIBLE);
            space_diver_car.setVisibility(View.GONE);
            icon_male_user.setVisibility(View.GONE);
            tv_driver.setVisibility(View.GONE);
            tv_car_num.setVisibility(View.VISIBLE);
            tv_car_num.setText(book.getDispatchedCar());
        }
        //both available
        else{
            tv_waiting.setVisibility(View.GONE);
            icon_taxi.setVisibility(View.VISIBLE);
            space_diver_car.setVisibility(View.VISIBLE);
            icon_male_user.setVisibility(View.VISIBLE);
            tv_driver.setVisibility(View.VISIBLE);
            tv_car_num.setVisibility(View.VISIBLE);
            tv_driver.setText(book.getDispatchedDriver());
            tv_car_num.setText(book.getDispatchedCar());
        }

        //message driver button
        if (book.getTripStatus() != MBDefinition.MB_STATUS_ACCEPTED && book.getTripStatus() != MBDefinition.MB_STATUS_ARRIVED) {
            text_driver_btn.setVisibility(View.GONE);
        } else {
            //TL-261 if company allow send message to driver then show the button
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (SharedPreferencesManager.loadBooleanPreferences(sp, MBDefinition.SHARE_SND_MSG_DRV, false)) {
                text_driver_btn.setVisibility(View.VISIBLE);
            } else {
                text_driver_btn.setVisibility(View.INVISIBLE);
            }
        }


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
        Logger.d(TAG, "on RESUME");

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
        messageDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        messageDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        driverMessage = (EditText) messageDialog.getWindow().findViewById(R.id.message);
        textRemaining = (TextView) messageDialog.getWindow().findViewById(R.id.text_remaining);

        driverMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable note) {
                textRemaining.setText(MBDefinition.DRIVER_NOTE_MAX_LENGTH - note.length() + " Characters Left");
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