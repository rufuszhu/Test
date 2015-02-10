package digital.dispatch.TaxiLimoNewUI.Drawers;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities.gcmType;
import digital.dispatch.TaxiLimoNewUI.MainActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Task.RegisterDeviceTask;
import digital.dispatch.TaxiLimoNewUI.Task.VerifyDeviceTask;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;

public class ProfileActivity extends BaseActivity implements
        OnFocusChangeListener {

    private EditText edtPhone, edtName, edtUEmail, et_code;
    private LinearLayout save_btn, cancel_btn;
    private TextView verify_btn, request_new_btn;
    private LinearLayout ll_sms_verify, button_groups;
    private TextView question_ic, cancel_ic, save_ic;
    private String curPhoneNum = "";
    private String curName = "";
    private String curEmail = "";
    private final String TAG = "ProfileActivity";
    private Context _context;
    boolean sendVerifySMS = false;
    boolean isChanged = false;
    boolean mBlockCompletion = false; // use this to bypass assigning existing
    // value
    boolean isBlcoked = false; // disable back button if it sets true
    SharedPreferences sharedPreferences;
    private BroadcastReceiver bcReceiver;

    private boolean isAutoRecovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if(getIntent().getExtras()!=null)
            isAutoRecovery = getIntent().getExtras().getBoolean(MBDefinition.EXTRA_AUTO_RECOVERY, false);
        else
            isAutoRecovery = false;
        setToolBar();
        findView();
        styleView();
        bindEvent();
    }
    private void setToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        Typeface face = FontCache.getFont(_context, "fonts/Exo2-Light.ttf");
        TextView yourTextView = Utils.getToolbarTitleView(this, toolbar);
        yourTextView.setTypeface(face);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onResume() {
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(_context);

        boolean alreadySMSVerify = SharedPreferencesManager
                .loadBooleanPreferences(sharedPreferences,
                        MBDefinition.SHARE_ALREADY_SMS_VERIFY, false);

        if (sharedPreferences != null) {
            curPhoneNum = SharedPreferencesManager.loadStringPreferences(
                    sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER);
            mBlockCompletion = true;
            edtPhone.setText(curPhoneNum);
            curName = SharedPreferencesManager.loadStringPreferences(
                    sharedPreferences, MBDefinition.SHARE_NAME);
            edtName.setText(curName);
            curEmail = SharedPreferencesManager.loadStringPreferences(
                    sharedPreferences, MBDefinition.SHARE_EMAIL);
            edtUEmail.setText(curEmail);
            mBlockCompletion = false;
            if (!alreadySMSVerify) {
                // SMS verify is not done or successful
                ll_sms_verify.setVisibility(View.VISIBLE);
                button_groups.setVisibility(View.GONE);
                et_code.requestFocus();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }

        }

        // TL-235
        boolean isTrackDetail = false;
        bcReceiver = CommonUtilities
                .getGenericReceiver(_context, isTrackDetail);
        LocalBroadcastManager.getInstance(this).registerReceiver(bcReceiver,
                new IntentFilter(gcmType.message.toString()));

        //TL-257 Auto Recovery
        if(isAutoRecovery){
            if(validate(null)){
                ll_sms_verify.setVisibility(View.VISIBLE);
                button_groups.setVisibility(View.GONE);
                et_code.requestFocus();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                boolean isFirstTime = false; // set this parameter to false when called from profile page
                sendVerifySMS = true;
                boolean isUpdateGCM = false;
                String regid = getRegistrationId(_context);

                RegisterDeviceTask task = new RegisterDeviceTask(_context, regid, isFirstTime, sendVerifySMS, isUpdateGCM);
                String[] params = {edtName.getText().toString(), edtUEmail.getText().toString(), edtPhone.getText().toString()};

                task.execute(params);
                Utils.showProcessingDialog(_context);
            }
            else{
                verify_btn.setVisibility(View.VISIBLE);
                verify_btn.setText("Register");
                button_groups.setVisibility(View.GONE);
                ll_sms_verify.setVisibility(View.GONE);

                verify_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(validate(null)) {
                            boolean isFirstTime = false; // set this parameter to false when called from profile page
                            sendVerifySMS = true;
                            boolean isUpdateGCM = false;
                            String regid = getRegistrationId(_context);

                            RegisterDeviceTask task = new RegisterDeviceTask(_context, regid, isFirstTime, sendVerifySMS, isUpdateGCM);
                            String[] params = {edtName.getText().toString(), edtUEmail.getText().toString(), edtPhone.getText().toString()};

                            task.execute(params);
                            Utils.showProcessingDialog(_context);
                        }
                    }
                });
            }

        }

        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bcReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        boolean alreadySMSVerify = SharedPreferencesManager
                .loadBooleanPreferences(sharedPreferences,
                        MBDefinition.SHARE_ALREADY_SMS_VERIFY, false);
        if (alreadySMSVerify) {
            super.onBackPressed();
        }
    }

    private void findView() {
        edtPhone = (EditText) findViewById(R.id.edt_phoneNum);
        edtName = (EditText) findViewById(R.id.edt_name);
        edtUEmail = (EditText) findViewById(R.id.edt_userEmail);
        et_code = (EditText) findViewById(R.id.et_code);

        save_btn = (LinearLayout) findViewById(R.id.profile_save_btn);
        cancel_btn = (LinearLayout) findViewById(R.id.profile_cancel_btn);
        verify_btn = (TextView) findViewById(R.id.profile_verify_btn);

        _context = this;

        ll_sms_verify = (LinearLayout) findViewById(R.id.ll_sms_verify);

        button_groups = (LinearLayout) findViewById(R.id.profile_btn_group);

        request_new_btn = (TextView) findViewById(R.id.request_new_btn);
        question_ic = (TextView) findViewById(R.id.question_circle);
        cancel_ic = (TextView) findViewById(R.id.cancel_close_icon);
        save_ic = (TextView) findViewById(R.id.save_check_icon);
    }

    private void styleView() {
        Typeface OpenSansRegular = FontCache.getFont(this,
                "fonts/OpenSansRegular.ttf");
        Typeface icon_pack = FontCache.getFont(this,
                "fonts/icon_pack.ttf");
        Typeface OpenSansSemibold = FontCache.getFont(this,
                "fonts/OpenSansSemibold.ttf");
        Typeface OpenSansBold = FontCache.getFont(this,
                "fonts/OpenSansBold.ttf");
        Typeface exoBold = FontCache.getFont(this,
                "fonts/Exo2-Bold.ttf");

        question_ic.setTypeface(icon_pack);
        question_ic.setText(MBDefinition.ICON_QUESTION_CIRCLE_CODE);
        cancel_ic.setTypeface(icon_pack);

        cancel_ic.setText(MBDefinition.ICON_CROSS);

        save_ic.setText(MBDefinition.ICON_CHECK_CODE);
        save_ic.setTypeface(icon_pack);

        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        TextView tv_save = (TextView) findViewById(R.id.tv_save);

        tv_cancel.setTypeface(exoBold);
        tv_save.setTypeface(exoBold);


        TextView register_title = (TextView) findViewById(R.id.register_title);
        register_title.setTypeface(OpenSansSemibold);


        verify_btn.setTypeface(OpenSansBold);

        edtPhone.setTypeface(OpenSansRegular);
        edtName.setTypeface(OpenSansRegular);
        edtUEmail.setTypeface(OpenSansRegular);
        et_code.setTypeface(OpenSansRegular);

        TextView verify_not_receive = (TextView) findViewById(R.id.verify_not_receive);
        TextView verify_check_phone = (TextView) findViewById(R.id.verify_check_phone);
        TextView request_new_btn = (TextView) findViewById(R.id.request_new_btn);
        TextView enter_verification = (TextView) findViewById(R.id.enter_verification);
        verify_not_receive.setTypeface(OpenSansRegular);
        verify_check_phone.setTypeface(OpenSansRegular);
        request_new_btn.setTypeface(OpenSansRegular);
        enter_verification.setTypeface(OpenSansRegular);


    }

    private void bindEvent() {

        edtName.setOnFocusChangeListener(this);
        edtUEmail.setOnFocusChangeListener(this);
        edtPhone.setOnFocusChangeListener(this);

        edtPhone.addTextChangedListener(new GenericTextWatcher(edtPhone));
        edtName.addTextChangedListener(new GenericTextWatcher(edtName));
        edtUEmail.addTextChangedListener(new GenericTextWatcher(edtUEmail));
        et_code.addTextChangedListener(new GenericTextWatcher(et_code));

        save_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (validate(null)) {

                    // send register device again
                    boolean isFirstTime = false; // set this parameter to false when called from profile page
                    boolean isUpdateGCM = false;

                    String regid = getRegistrationId(_context);

                    //new RegisterDeviceTask(_context, regid, isFirstTime, sendVerifySMS).execute();
                    RegisterDeviceTask task = new RegisterDeviceTask(_context, regid, isFirstTime, sendVerifySMS, isUpdateGCM);
                    String[] params = {edtName.getText().toString(), edtUEmail.getText().toString(), edtPhone.getText().toString()};

                    task.execute(params);
                    Utils.showProcessingDialog(_context);
                }
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                finish();
            }
        });

        verify_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // send VerifySMSRequest
                boolean isFirstTime = false; // set this parameter to false when
                // called from profile page
                new VerifyDeviceTask(_context, isFirstTime, et_code.getText()
                        .toString()).execute();
                Utils.showProcessingDialog(_context);
            }
        });

        request_new_btn = (TextView) findViewById(R.id.request_new_btn);

        // request new verification code, here need to refresh the phone number
        // in case number is updated
        request_new_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate(null)) {

                    boolean isFirstTime = false; // set this parameter to false
                    // when called from profile
                    // page
                    sendVerifySMS = true;
                    boolean isUpdateGCM = false;
                    String regid = getRegistrationId(_context);

                    RegisterDeviceTask task = new RegisterDeviceTask(_context, regid, isFirstTime, sendVerifySMS, isUpdateGCM);
                    String[] params = {edtName.getText().toString(), edtUEmail.getText().toString(), edtPhone.getText().toString()};

                    task.execute(params);
                    Utils.showProcessingDialog(_context);
                }
            }
        });
    }

    private boolean validate(EditText target) {

        if (target != null) {
            if (target == edtName) {
                return validateName();
            } else if (target == edtUEmail) {
                return validateEmail();
            } else if (target == edtPhone) {
                return validatePhone();
            } else
                return false;
        }
        // validate all if not from afterTextChanged
        else {
            return validateName() && validateEmail() && validatePhone();
        }

    }

    private boolean validateName() {
        String userName = edtName.getText().toString();

        if (userName.length() == 0) {
            edtName.setError(getResources().getString(R.string.empty_name));
            return false;
        } else {
            return true;
        }

    }

    private boolean validatePhone() {
        String phone = edtPhone.getText().toString();
        if (phone.length() == 0) {
            edtPhone.setError(getResources().getString(
                    R.string.empty_phone_number));
            return false;
        } else if (phone.length() > 13) {
            edtPhone.setError(getResources().getString(
                    R.string.phone_number_too_long));
            return false;
        } else {
            // if phone number changed we need show sms verification
            if (!phone.equalsIgnoreCase(curPhoneNum)) {
                sendVerifySMS = true;

            }
            return true;
        }
    }

    private boolean validateEmail() {
        boolean isValid = true;

        String newEmail = edtUEmail.getText().toString();

        if (newEmail.equalsIgnoreCase("")) {
            edtUEmail.setError(getResources().getString(
                    R.string.credit_card_empty_email));
            isValid = false;
        } else {
            // check if the entered email is a valid pattern
            Pattern ePattern = Pattern.compile(MBDefinition.EMAIL_PATTERN);
            Matcher matcher = ePattern.matcher(newEmail);

            if (matcher.matches()) {
                edtUEmail.setError(null);
            } else {
                edtUEmail.setError(getResources().getString(
                        R.string.credit_card_email_invalid));
                isValid = false;
            }
        }

        return isValid;
    }

    private void storeInfo() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences((_context));
        String userName = edtName.getText().toString();

        SharedPreferencesManager.savePreferences(sharedPreferences,
                MBDefinition.SHARE_NAME, userName);
        SharedPreferencesManager.savePreferences(sharedPreferences,
                MBDefinition.SHARE_EMAIL, edtUEmail.getText().toString());
        SharedPreferencesManager.savePreferences(sharedPreferences,
                MBDefinition.SHARE_PHONE_NUMBER, edtPhone.getText().toString());

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (!hasFocus) {

            validate((EditText) v);

        }

    }

    private String getRegistrationId(Context context) {
        // getGCMPreferences
        final SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(MBDefinition.PROPERTY_REG_ID,
                "");
        if (registrationId.isEmpty()) {
            Logger.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(MBDefinition.PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = Utils.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    // callback by VerifyDeviceTask
    public void showProfileVerifySuccessMessage() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(_context);
        SharedPreferencesManager.savePreferences(sharedPreferences,
                MBDefinition.SHARE_ALREADY_SMS_VERIFY, true);

        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setMessage(_context.getString(R.string.verify_success));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // flow to register confirmation page
                Intent intent = new Intent(_context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // flow to register confirmation page
                Intent intent = new Intent(_context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


        // restore original profile layout
        ll_sms_verify.setVisibility(View.GONE);
        verify_btn.setVisibility(View.GONE);
        button_groups.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void showProfileVerifyFailedMessage() {

        et_code.setText("");
        verify_btn.setVisibility(View.GONE);

        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setMessage(_context.getString(R.string.verify_failed));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    // callback by RegisterDeviceTask for successful update
    public void showResendSuccessMessage() {
        storeInfo();// update local database

        //TL-257 reset to original text and listener
        verify_btn.setVisibility(View.GONE);
        verify_btn.setText("Verify");
        verify_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // send VerifySMSRequest
                boolean isFirstTime = false; // set this parameter to false when
                // called from profile page
                new VerifyDeviceTask(_context, isFirstTime, et_code.getText()
                        .toString()).execute();
                Utils.showProcessingDialog(_context);
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(_context);

        String phone = SharedPreferencesManager.loadStringPreferences(
                sharedPreferences, MBDefinition.SHARE_PHONE_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (sendVerifySMS == true) {
                    ll_sms_verify.setVisibility(View.VISIBLE);
                    button_groups.setVisibility(View.GONE);
                    et_code.requestFocus();

                } else {
                    // save is done hide all buttons
                    button_groups.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }
        });
        if (sendVerifySMS == true) {
            // reset already SMS verify to false
            SharedPreferencesManager.savePreferences(sharedPreferences,
                    MBDefinition.SHARE_ALREADY_SMS_VERIFY, false);

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            builder.setMessage(_context.getString(R.string.verify_dialog_text, phone));
        } else {
            builder.setMessage(_context.getString(R.string.profile_update_text));

        }

        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (sendVerifySMS == true) {
                    ll_sms_verify.setVisibility(View.VISIBLE);
                    button_groups.setVisibility(View.GONE);
                    et_code.requestFocus();

                } else {
                    // save is done hide all buttons
                    button_groups.setVisibility(View.GONE);
                }

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    // inner class implements TextWatcher for show and hide button when user
    // enter something
    private class GenericTextWatcher implements TextWatcher {

        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                      int i2) {
            if (mBlockCompletion)
                return;
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1,
                                  int i2) {
        }

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.edt_name:
                    if (!curName.equals(text)) {
                        button_groups.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.edt_userEmail:
                    if (!curEmail.equals(text)) {
                        button_groups.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.edt_phoneNum:
                    if (!curPhoneNum.equalsIgnoreCase(text)) {
                        button_groups.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.et_code:
                    verify_btn.setVisibility(View.VISIBLE);
                default:
                    break;
            }
        }
    }

}
