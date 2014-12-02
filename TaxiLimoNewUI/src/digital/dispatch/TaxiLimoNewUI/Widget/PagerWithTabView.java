package digital.dispatch.TaxiLimoNewUI.Widget;

import digital.dispatch.TaxiLimoNewUI.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import digital.dispatch.TaxiLimoNewUI.Book.ModifyAddressActivity;



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
