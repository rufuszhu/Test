package digital.dispatch.TaxiLimoNewUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;

public class RegisterConfirmActivity extends BaseActivity {

	private Context _context;
	private CheckBox check_box;
	private TextView register_btn, confirmTitle, chkWarning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_confirm);
		getActionBar().setTitle(R.string.title_activity_register);
		_context = this;
		findBindView();
		styleView();
		updateActionBar();

	}



	private void findBindView() {

		check_box = (CheckBox) findViewById(R.id.chk_agreement);
		register_btn = (TextView) findViewById(R.id.register_confirm_btn);
		confirmTitle = (TextView) findViewById(R.id.confirm_title);
		chkWarning = (TextView) findViewById(R.id.chk_warning);

		register_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!check_box.isChecked()) {
					
					TextView warning = (TextView) findViewById(R.id.chk_warning);
					warning.setVisibility(View.VISIBLE);

				} else {
					showWelcomeDialog();
				}
			}
		});
	}
	
	private void styleView() {
		Typeface rionaSansRegular = Typeface.createFromAsset(getAssets(), "fonts/RionaSansRegular.otf");
		confirmTitle.setTypeface(rionaSansRegular);
		SpannableString spanString = new SpannableString(confirmTitle.getText());
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		confirmTitle.setText(spanString);	
		
		chkWarning.setTypeface(rionaSansRegular);
		
			
	}
	
	
	
	private void updateActionBar(){
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setIcon(R.color.transparent);
		actionBar.setIcon(null);
		int titleId = getResources().getIdentifier("action_bar_title", "id",
	            "android");
		Typeface face = Typeface.createFromAsset(_context.getAssets(), "fonts/Exo2-Light.ttf");
	    TextView yourTextView = (TextView) findViewById(titleId);
	    yourTextView.setTypeface(face);
	}

	private void showWelcomeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(_context)
				.setMessage(R.string.welcome_message).setCancelable(true);
		final AlertDialog dialog = builder.create();
		dialog.show();
		new CountDownTimer(2000, 2000) {
			public void onTick(long millisUntilFinished) {

			}

			public void onFinish() {
				dialog.dismiss();
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(_context);
				SharedPreferencesManager.savePreferences(sharedPreferences,
						MBDefinition.SHARE_ALREADY_REGISTER, true);
				//TL-250
				SharedPreferencesManager.savePreferences(sharedPreferences,
						MBDefinition.SHARE_ALREADY_SMS_VERIFY, true);
				Intent intent = new Intent(_context, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}.start();
	}

}
