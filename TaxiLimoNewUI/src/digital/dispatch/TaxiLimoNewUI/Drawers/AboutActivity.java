package digital.dispatch.TaxiLimoNewUI.Drawers;

import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.R.menu;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends BaseActivity {

	private static final String TAG = "AboutActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		String versionName = "";
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

		Typeface icon_pack = Typeface.createFromAsset(getAssets(), "fonts/icon_pack.ttf");
		Typeface rionaSansBold = Typeface.createFromAsset(getAssets(), "fonts/RionaSansBold.otf");
		Typeface rionaSansRegular = Typeface.createFromAsset(getAssets(), "fonts/RionaSansRegular.otf");
		Typeface rionaSansMedium = Typeface.createFromAsset(getAssets(), "fonts/RionaSansMedium.otf");

		tv_email_icon.setTypeface(icon_pack);
		tv_phone_icon.setTypeface(icon_pack);
		tv_email_icon.setText(MBDefinition.ICON_MESSAGE);
		tv_phone_icon.setText(MBDefinition.icon_phone);

		tvVersion.setTypeface(rionaSansBold);
		tv_update.setTypeface(rionaSansRegular);
		tvSupport.setTypeface(rionaSansBold);
		tvContactNum.setTypeface(rionaSansRegular);
		tvVersion.setTypeface(rionaSansBold);
		tvContactEmail.setTypeface(rionaSansRegular);
		tvAboutFooter.setTypeface(rionaSansMedium);

		tvVersion.setText("Version " + versionName);
		
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
