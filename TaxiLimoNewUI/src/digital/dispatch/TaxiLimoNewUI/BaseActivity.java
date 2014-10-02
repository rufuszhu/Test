/**
 * 
 */
package digital.dispatch.TaxiLimoNewUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout.LayoutParams;



public class BaseActivity extends Activity {
    
	private int activityAnimEnter;
    private int activityAnimExit;
   // private BlurRelativeLayout blurRelativeLayout;
    private Animation blurEnter;
    private Animation blurExit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        bindEvent();
        
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
        
        blurEnter = AnimationUtils.loadAnimation(this, R.anim.dialog_bg_pop);
        blurExit = AnimationUtils.loadAnimation(this, R.anim.dialog_bg_exit);
    }
    
    private void bindEvent() {
    	blurEnter.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				//blurRelativeLayout.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	
    	blurExit.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
//				if(blurRelativeLayout != null) {
//					blurRelativeLayout.setVisibility(View.GONE);
//				}
			}
		});
    }

    
//    public void showBlur() {
//    	if(blurRelativeLayout == null) {
//    		blurRelativeLayout = new BlurRelativeLayout(this);
//    		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//    		blurRelativeLayout.setBlurRadius(getResources().getDimensionPixelSize(R.dimen.dialog_radius));
//    		getWindow().addContentView(blurRelativeLayout, lp);
//    	}
//    	blurRelativeLayout.startAnimation(blurEnter);
//    }
//    
//    public void hideBlur() {
//    	if(blurRelativeLayout != null) {
//    		blurRelativeLayout.startAnimation(blurExit);
//    	}
//    }
}
