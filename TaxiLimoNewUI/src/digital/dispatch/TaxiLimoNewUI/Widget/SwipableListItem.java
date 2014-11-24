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
import android.support.v4.widget.ViewDragHelper;
import digital.dispatch.TaxiLimoNewUI.Book.ModifyAddressActivity;



public class SwipableListItem extends ViewGroup {

	private static final String TAG = "SwipableListItem";
	
	private final ViewDragHelper mDragHelper;
	private Context context;
	
	private View mHeaderView;
	private RelativeLayout mSideView;

	
	private int mHeaderView_width;
	private int mSideView_width;
	private int mHeight;
	private int mBarrier;
	

//	private GestureDetectorCompat mDetector;

//	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
//		private static final String DEBUG_TAG = "Gestures";
//
//		@Override
//		public boolean onDown(MotionEvent event) {
//			Log.d(DEBUG_TAG, "onDown: " + event.toString());
//			return true;
//		}
//
//		@Override
//		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
//			Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
//			return true;
//		}
//
//		@Override
//		public void onLongPress(MotionEvent event) {
//			Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
//			isLongPressing = true;
//		}
//
//		@Override
//		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//			Log.d(DEBUG_TAG, "onScroll: " + e1.toString() + e2.toString());
//			isOnSroll = true;
//			return true;
//		}
//	}
	
	public SwipableListItem(Context context) {
		this(context, null);
		this.context = context;
	}

	public SwipableListItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.context = context;
	}
	
	public SwipableListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mDragHelper = ViewDragHelper.create(this, 1f, new MyDragHelperCallback(){
		});
		
	}
	

	@Override
	protected void onFinishInflate() {
		mHeaderView = this.findViewById(R.id.viewHeader);
		mSideView = (RelativeLayout) this.findViewById(R.id.contact_option);
		//mHeaderView.setOnClickListener(mOnClickListener);
	}
	

	private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	Toast.makeText(context, "aaabbbccc", Toast.LENGTH_SHORT).show();
        }
    };

	
	public class MyDragHelperCallback extends ViewDragHelper.Callback {
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			if (child==mHeaderView){
//				Log.e("tryCaptureView", "mHeaderView dragged");
				return true;
			}else if (child==mSideView){
//				Log.e("tryCaptureView", "mSideView dragged");
				return true;
			}else{
				return false;
			}
		}
		
		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			//Log.e(TAG,"onViewPositionChanged");
			if (changedView==mHeaderView){
				mBarrier = left + mHeaderView_width;
			}else{
				mBarrier = left;
			}
			requestLayout();
		}
		
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			Log.e(TAG,"onViewReleased xvel:" + xvel + " " + (float) Math.abs(mBarrier)/mSideView_width);
			if(xvel<0||(xvel==0&& (float) Math.abs(mHeaderView_width-mBarrier)/mSideView_width > 0.4f)){
				smoothSlideTo(-mSideView_width);
			}else{
				smoothSlideTo(0);
			}
			invalidate();
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			return mSideView_width;			
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			final int leftBound;
			final int rightBound;
			if (child==mHeaderView){
				leftBound = -mSideView_width;
				rightBound = 0;
			}else{
				leftBound = mHeaderView_width-mSideView_width;
				rightBound = mHeaderView_width;
			}
			int newLeft = left;
			if(left>rightBound){
				newLeft = rightBound;
			}else if (left<leftBound){
				newLeft = leftBound;
			}
			return newLeft;
		}
	}
	
	public void minimize(){
		mBarrier = mHeaderView_width-mSideView_width;
		mHeaderView.layout(0, 0, mHeaderView_width, mHeight);
		mSideView.layout(mHeaderView_width, 0, mHeaderView_width+mSideView_width, mHeight);
	}
	
	public void maximize() {
		mBarrier = mHeaderView_width;
		mHeaderView.layout(-mSideView_width, 0, mHeaderView_width-mSideView_width, mHeight);
		mSideView.layout(mHeaderView_width-mSideView_width, 0, mHeaderView_width, mHeight);
	}

	public boolean smoothSlideTo(float slideOffset) {
		final int leftBound = getPaddingLeft();
		int x = (int) (leftBound + slideOffset);
		if (mDragHelper.smoothSlideViewTo(mHeaderView, x, mHeaderView.getTop())) {
			ViewCompat.postInvalidateOnAnimation(this);
			return true;
		}
		return false;
	}
	
	
	public int getStatus(){
		return mBarrier;
	}
	
	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
	    if (mDragHelper.shouldInterceptTouchEvent(event)) {
	            return true;
	    }
	    return super.onInterceptTouchEvent(event);
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		switch (event.getAction() & MotionEvent.ACTION_MASK) {
//		case MotionEvent.ACTION_CANCEL :
//			//Log.e(TAG, "contactView: move cancel");
//			//reset(false);
//			break;
//		}
//		mDragHelper.processTouchEvent(event);
//	    return true;
//	}
	
	public void processDragEvent(MotionEvent event){
		mDragHelper.processTouchEvent(event);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
		int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

		setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0), resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (mHeaderView_width==0||mSideView_width==0||mHeight==0){
			mHeaderView_width = mHeaderView.getMeasuredWidth();
			mSideView_width = mSideView.getMeasuredWidth();
			mHeight = mHeaderView.getMeasuredHeight();
			mBarrier = mHeaderView_width;
		}
		mHeaderView.layout(mBarrier-mHeaderView_width, 0, mBarrier, b);
		mSideView.layout(mBarrier, 0, mBarrier+mSideView_width, b);
	}
	
}
