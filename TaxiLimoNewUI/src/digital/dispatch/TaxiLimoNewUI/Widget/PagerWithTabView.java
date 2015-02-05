package digital.dispatch.TaxiLimoNewUI.Widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.RelativeLayout;



public class PagerWithTabView extends RelativeLayout {

	private static final String TAG = "PagerWithTabView";
	
	private Context context;
	private ViewPager mPager;
	
	
	public PagerWithTabView(Context context) {
		this(context, null);
		this.context = context;
		findView();
	}

	private void findView() {
		
	}

	public PagerWithTabView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.context = context;
	}
	
	public PagerWithTabView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}
	
	public void setPager(ViewPager mPager){
		this.mPager = mPager;
	}
	
	

}
