package digital.dispatch.TaxiLimoNewUI.Drawers;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.R.menu;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends BaseActivity {

	private static final String TAG = "AboutActivity";
    private Context _this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
        _this = this;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_this); //TL-379

		String versionName = "";
        String supportEmail = "";
        String supportPhone = "";
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (Exception e) {
			Logger.v(TAG, e.toString());
			e.printStackTrace();
		}

		TextView tv_phone_icon = (TextView) findViewById(R.id.tv_phone_icon);
		TextView tv_email_icon = (TextView) findViewById(R.id.tv_email_icon);
		TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
		TextView tv_update = (TextView) findViewById(R.id.tv_update);
		TextView tvSupport = (TextView) findViewById(R.id.tvSupport);
		final TextView tvContactNum = (TextView) findViewById(R.id.tvContactNum);
		final TextView tvContactEmail = (TextView) findViewById(R.id.tvContactEmail);
		TextView tvAboutFooter = (TextView) findViewById(R.id.tvAboutFooter);
        LinearLayout llContact = (LinearLayout)findViewById(id.ll_support_phone);
        LinearLayout llEmail = (LinearLayout)findViewById(id.ll_support_email);

		Typeface icon_pack = FontCache.getFont(this, "fonts/icon_pack.ttf");
		Typeface OpenSansBold = FontCache.getFont(this, "fonts/OpenSansBold.ttf");
		Typeface OpenSansRegular = FontCache.getFont(this, "fonts/OpenSansRegular.ttf");
		Typeface OpenSansSemibold = FontCache.getFont(this, "fonts/OpenSansSemibold.ttf");

		tv_email_icon.setTypeface(icon_pack);
		tv_phone_icon.setTypeface(icon_pack);
		tv_email_icon.setText(MBDefinition.ICON_MESSAGE);
		tv_phone_icon.setText(MBDefinition.icon_phone);

		tvVersion.setTypeface(OpenSansBold);
		tv_update.setTypeface(OpenSansRegular);
		tvSupport.setTypeface(OpenSansBold);
		tvContactNum.setTypeface(OpenSansRegular);
		tvVersion.setTypeface(OpenSansBold);
		tvContactEmail.setTypeface(OpenSansRegular);
		tvAboutFooter.setTypeface(OpenSansSemibold);

		tvVersion.setText("Version " + versionName);
        //Tl-379
        supportEmail = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_SUPPORT_EMAIL );
        supportPhone = SharedPreferencesManager.loadStringPreferences(sharedPreferences, MBDefinition.SHARE_SUPPORT_PHONE );



        if(!supportEmail.isEmpty()  && !supportPhone.isEmpty()){
            //set up phone and email from mb parameters
            tvContactNum.setText(supportPhone);
            tvContactEmail.setText(supportEmail);
        }else if(supportEmail.isEmpty() && !supportPhone.isEmpty()){
            tvContactNum.setText(supportPhone);
            llEmail.setVisibility(View.GONE);
        }else if(supportPhone.isEmpty() && !supportEmail.isEmpty()){
            tvContactEmail.setText(supportEmail);
            llContact.setVisibility(View.GONE);
        }else{
            llContact.setVisibility(View.GONE);
            llEmail.setVisibility(View.GONE);
            tvSupport.setVisibility(View.GONE);

        }
		tvContactNum.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +tvContactNum.getText().toString().replace("-", "")));
				startActivity(intent);
			}
		});

		tvContactEmail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, new String[] { tvContactEmail.getText().toString() });
				i.putExtra(Intent.EXTRA_SUBJECT, "");
				i.putExtra(Intent.EXTRA_TEXT, "");
				try {
					startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(AboutActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
