/**
 * 
 */
package digital.dispatch.TaxiLimoNewUI;

import java.util.Locale;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;



public class BaseActivity extends ActionBarActivity {
    
	private int activityAnimEnter;
    private int activityAnimExit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setUpActionBar();
    }
    
    
    private void setUpActionBar() {
    	ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setIcon(R.color.transparent);
		actionBar.setIcon(null);
		int titleId = getResources().getIdentifier("action_bar_title", "id",
	            "android");
		Typeface face = Typeface.createFromAsset(this.getAssets(), "fonts/Exo2-Light.ttf");
	    TextView yourTextView = (TextView) findViewById(titleId);

	    yourTextView.setTypeface(face);
		
	}


	public void startActivityForAnim(Intent intent) {
        startActivity(intent);
        overridePendingTransition(activityAnimEnter, activityAnimExit);
    }
    

    @Override
    public void onBackPressed() {
        finish();
    }
    

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.base_back_activity_enter, R.anim.base_back_activity_exit);
    }
    
    private void init() {
    	activityAnimEnter = R.anim.base_activity_enter;
        activityAnimExit = R.anim.base_activity_exit;
    }
    

}
