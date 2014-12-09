package digital.dispatch.TaxiLimoNewUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;

public class RegisterConfirmActivity extends BaseActivity {

	private Context context;
	private CheckBox check_box;
	private TextView register_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_confirm);
		getActionBar().setTitle(R.string.title_activity_register);
		context = this;
		findView();

	}



	private void findView() {

		check_box = (CheckBox) findViewById(R.id.chk_agreement);
		register_btn = (TextView) findViewById(R.id.register_confirm_btn);

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

	private void showWelcomeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setMessage(R.string.welcome_message).setCancelable(true);
		final AlertDialog dialog = builder.create();
		dialog.show();
		new CountDownTimer(2000, 2000) {
			public void onTick(long millisUntilFinished) {

			}

			public void onFinish() {
				dialog.dismiss();
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(context);
				SharedPreferencesManager.savePreferences(sharedPreferences,
						MBDefinition.SHARE_ALREADY_REGISTER, true);
				//TL-250
				SharedPreferencesManager.savePreferences(sharedPreferences,
						MBDefinition.SHARE_ALREADY_SMS_VERIFY, true);
				Intent intent = new Intent(context, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}.start();
	}

}
