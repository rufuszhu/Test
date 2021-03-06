package digital.dispatch.TaxiLimoNewUI.Book;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import digital.dispatch.TaxiLimoNewUI.Adapters.DateAdapter;
import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities;
import digital.dispatch.TaxiLimoNewUI.GCM.CommonUtilities.gcmType;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.FontCache;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;

public class SetTimeActivity extends BaseActivity {

	private WheelView dates;
	private WheelView times;
	private RadioButton now_btn;
	private RadioButton later_btn;
//	private LinearLayout save;
//	private LinearLayout cancel;
	private TextView question_exclamation;
	private TextView info;
	private DateAdapter dateAdapter;
	private DateAdapter timeTodayAdapter;
	private DateAdapter timeNotTodayAdapter;
	private BroadcastReceiver bcReceiver;
	private Context _context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_time);
		_context = this;
        setToolBar();
		findView();
		setStyles();
		setUpWheel();
		bindEvents();
	}
    private void setToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        Typeface face = FontCache.getFont(this, "fonts/Exo2-Light.ttf");
        TextView yourTextView = Utils.getToolbarTitleView(this, toolbar);
        yourTextView.setTypeface(face);
    }
	
	private void setStyles() {
		Typeface fontFamily = FontCache.getFont(this, "fonts/icon_pack.ttf");

		Typeface OpenSansSemibold = FontCache.getFont(this, "fonts/OpenSansSemibold.ttf");
		Typeface OpenSansRegular = FontCache.getFont(this, "fonts/OpenSansRegular.ttf");
        question_exclamation.setTypeface(fontFamily);
        question_exclamation.setText(MBDefinition.ICON_EXCLAMATION_CIRCLE_CODE);
		info.setTypeface(OpenSansSemibold);
		now_btn.setTypeface(OpenSansRegular);
		later_btn.setTypeface(OpenSansRegular);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
            return true;
        }
        if(id==R.id.action_accept){
            if (now_btn.isChecked()) {
                Utils.pickupDate=null;
                Utils.pickupTime=null;
            } else {
                int dateIndex = dates.getCurrentItem();
                int timeIndex = times.getCurrentItem();

                if (dates.getCurrentItem() == 0) {
                    Utils.pickupTime = timeTodayAdapter.getTime(timeIndex);

                } else {
                    Utils.pickupTime = timeNotTodayAdapter.getTime(timeIndex);
                }
                Utils.pickupDate = dateAdapter.getDate(dateIndex);
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	
	@Override
	public void onResume() {
		//TL-235
		boolean isTrackDetail = false;
		bcReceiver = CommonUtilities.getGenericReceiver(_context, isTrackDetail);
		LocalBroadcastManager.getInstance(this).registerReceiver(bcReceiver, new IntentFilter(gcmType.message.toString()));
		super.onResume();
		
	}
	
	@Override
	public void onPause(){
		LocalBroadcastManager.getInstance(this).unregisterReceiver(bcReceiver);
		super.onPause();
	}
	
	
	private void findView() {
		dates = (WheelView) findViewById(R.id.dates);
		times = (WheelView) findViewById(R.id.times);
		now_btn = (RadioButton) findViewById(R.id.now);
		later_btn = (RadioButton) findViewById(R.id.later);
//		save = (LinearLayout) findViewById(R.id.save);
//		cancel = (LinearLayout) findViewById(R.id.cancel);
		info = (TextView) findViewById(R.id.info);
		question_exclamation = (TextView) findViewById(R.id.question_exclamation);
		
//		TextView cancel_icon = (TextView) findViewById(R.id.cancel_icon);
//		TextView save_icon = (TextView) findViewById(R.id.save_icon);
//		Typeface icon_pack = FontCache.getFont(this, "fonts/icon_pack.ttf");
//		cancel_icon.setTypeface(icon_pack);

//		cancel_icon.setText(MBDefinition.ICON_CROSS);
//		save_icon.setText(MBDefinition.ICON_CHECK_CODE);
//		save_icon.setTypeface(icon_pack);
		
		
//		TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
//		TextView tv_save = (TextView) findViewById(R.id.tv_save);
//		Typeface exoBold = FontCache.getFont(this, "fonts/Exo2-Bold.ttf");
//		tv_cancel.setTypeface(exoBold);
//		tv_save.setTypeface(exoBold);
	}
	
	private void setUpWheel() {
		int transparent = this.getResources().getColor(R.color.transparent);
		dates.setVisibleItems(5); // Number of items
		dates.setWheelBackground(R.drawable.wheel_bg_holo);
		dates.setWheelForeground(R.drawable.wheel_val_holo);

		dates.setShadowColor(transparent, transparent, transparent);

		times.setVisibleItems(5); // Number of items
		times.setWheelBackground(R.drawable.wheel_bg_holo);
		times.setWheelForeground(R.drawable.wheel_val_holo);
		times.setShadowColor(transparent, transparent, transparent);

		boolean isToday = true;
		boolean isDate = true;
		dateAdapter = new DateAdapter(this, isDate, setupDateList());
		timeTodayAdapter = new DateAdapter(this, !isDate, setupTimeList(isToday));
		timeNotTodayAdapter = new DateAdapter(this, !isDate, setupTimeList(!isToday));
		dates.setViewAdapter(dateAdapter);
		dates.setCurrentItem(dateAdapter.getIndexOfDate(Utils.pickupDate));
		// if today
		if (Utils.pickupDate == null || Utils.pickupDate.getDate() == Calendar.getInstance().getTime().getDate()) {
			times.setViewAdapter(timeTodayAdapter);
			times.setCurrentItem(timeTodayAdapter.getIndexOfTime(Utils.pickupTime));
		} else {
			times.setViewAdapter(timeNotTodayAdapter);
			times.setCurrentItem(timeNotTodayAdapter.getIndexOfTime(Utils.pickupTime));
		}

	}

	
	
	private void bindEvents() {
		

		final OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
//				Logger.e("new " + newValue + ", old: " + oldValue);
//				
//				((TextView) wheel.getItemView(newValue).findViewById(R.id.date_text)).setTextColor(_context.getResources().getColor(R.color.actionbar_background));
//				
//				((TextView) wheel.getItemView(oldValue).findViewById(R.id.date_text)).setTextColor(_context.getResources().getColor(R.color.gray_line));
//				
				
				if (newValue == 0) {
					Date oldDate = timeNotTodayAdapter.getTime(times.getCurrentItem());
					times.setViewAdapter(timeTodayAdapter);
					times.setCurrentItem(timeTodayAdapter.getIndexOfTime(oldDate));
				} else {
					if (oldValue == 0) {
						Date oldDate = timeTodayAdapter.getTime(times.getCurrentItem());
						times.setViewAdapter(timeNotTodayAdapter);
						times.setCurrentItem(timeNotTodayAdapter.getIndexOfTime(oldDate));
					}
				}
			}
		};

		now_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView disable_view = (TextView) findViewById(R.id.disable_view);
				disable_view.setVisibility(View.VISIBLE);
				later_btn.setChecked(false);
				now_btn.setChecked(true);
			}
		});

		later_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView disable_view = (TextView) findViewById(R.id.disable_view);
				disable_view.setVisibility(View.GONE);
				later_btn.setChecked(true);
				now_btn.setChecked(false);
			}
		});

//		save.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				if (now_btn.isChecked()) {
//					Utils.pickupDate=null;
//					Utils.pickupTime=null;
//				} else {
//					int dateIndex = dates.getCurrentItem();
//					int timeIndex = times.getCurrentItem();
//
//					if (dates.getCurrentItem() == 0) {
//						Utils.pickupTime = timeTodayAdapter.getTime(timeIndex);
//
//					} else {
//						Utils.pickupTime = timeNotTodayAdapter.getTime(timeIndex);
//					}
//					Utils.pickupDate = dateAdapter.getDate(dateIndex);
//				}
//				finish();
//			}
//		});
//		cancel.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				finish();
//			}
//		});

		dates.addChangingListener(wheelListener);
	}


	private ArrayList<Date> setupTimeList(boolean isToday) {
		ArrayList<Date> timeList = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		if (isToday) {
			int currentMin = cal.get(Calendar.MINUTE);
			int roundMin = (int) (Math.ceil((double) currentMin / 5) * 5);
			cal.set(Calendar.MINUTE, roundMin);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		} else {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		while (cal.get(Calendar.DATE) == today.get(Calendar.DATE)) {
			Date temp = cal.getTime();
			timeList.add(temp);
			cal.add(Calendar.MINUTE, 5);
		}
		return timeList;
	}

	private ArrayList<Date> setupDateList() {
		ArrayList<Date> dateList = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i <= MBDefinition.FUTURE_BOOKING_RANGE; i++) {
			Date temp = cal.getTime();
			dateList.add(temp);
			cal.add(Calendar.DATE, 1);
		}
		return dateList;
	}
}
