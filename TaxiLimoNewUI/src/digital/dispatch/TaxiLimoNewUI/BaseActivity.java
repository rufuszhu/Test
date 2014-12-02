/**
 * 
 */
package digital.dispatch.TaxiLimoNewUI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;



public class BaseActivity extends ActionBarActivity {
    
	private int activityAnimEnter;
    private int activityAnimExit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        
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
