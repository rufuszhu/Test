/**
 * 
 */
package digital.dispatch.TaxiLimoNewUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.SharedPreferencesManager;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;


public class BaseActivity extends ActionBarActivity {
    
	private int activityAnimEnter;
    private int activityAnimExit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onResume(){
        setUpActionBar();
        super.onResume();
    }
    
    
    private void setUpActionBar() {
    	ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.color.transparent);
		actionBar.setIcon(null);
	}


	public void startActivityForAnim(Intent intent) {
        startActivity(intent);
        overridePendingTransition(activityAnimEnter, activityAnimExit);
    }

    public boolean canOpenTutorial(String className) {
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesManager.SP_TUTORIALS, Context.MODE_APPEND);
        return SharedPreferencesManager.loadBooleanPreferences(sharedPreferences, className, true);
    }

    public void stopShowingToolTip(String className) {
        SharedPreferences preference = getSharedPreferences(SharedPreferencesManager.SP_TUTORIALS, Context.MODE_APPEND);
        SharedPreferencesManager.savePreferences(preference, className, false);
    }

    public void showToolTip(String tip, final String className){
        final View view =getWindow().getDecorView().findViewById(android.R.id.content); //returns base view of the fragment
        if ( view == null)
            return;
        if ( !(view instanceof ViewGroup)){
            return;
        }
        final ViewGroup viewGroup = (ViewGroup) view;
        final View tooltip = View.inflate(this, R.layout.tooltip, viewGroup);
        TextView icon_light_bulb = (TextView) tooltip.findViewById(R.id.icon_light_bulb);
        TextView tv_tooltip = (TextView) tooltip.findViewById(R.id.tv_tooltip);
        TextView icon_close = (TextView) tooltip.findViewById(R.id.icon_close);

        Typeface icon_pack = FontCache.getFont(this, "fonts/icon_pack.ttf");
        Typeface openSansRegular = FontCache.getFont(this, "fonts/OpenSansRegular.ttf");
        icon_light_bulb.setTypeface(icon_pack);
        icon_light_bulb.setText(MBDefinition.ICON_LIGHT_BULB);
        icon_close.setTypeface(icon_pack);
        icon_close.setText(MBDefinition.ICON_CROSS_SMALL);
        tv_tooltip.setTypeface(openSansRegular);

        tv_tooltip.setText(tip);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewGroup.findViewById(R.id.tooltip).setVisibility(View.GONE);
                stopShowingToolTip(className);
            }
        };

        viewGroup.findViewById(R.id.tooltip).setOnClickListener(listener);
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

    //TL-369 used by activity onDestroy() to unbind drawables
    public void unbindDrawables(View view ){

        if(view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }

        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setImageBitmap(null);
        }
        else if(view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for( int i= 0; i< (( ViewGroup ) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            if(!(view instanceof AdapterView))
                viewGroup.removeAllViews();
        }
    }
}