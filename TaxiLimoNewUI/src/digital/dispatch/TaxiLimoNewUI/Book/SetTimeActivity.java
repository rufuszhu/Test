package digital.dispatch.TaxiLimoNewUI.Book;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import digital.dispatch.TaxiLimoNewUI.BaseActivity;
import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Adapters.DateAdapter;
import digital.dispatch.TaxiLimoNewUI.R.color;
import digital.dispatch.TaxiLimoNewUI.R.drawable;
import digital.dispatch.TaxiLimoNewUI.R.id;
import digital.dispatch.TaxiLimoNewUI.R.layout;
import digital.dispatch.TaxiLimoNewUI.Utils.MBDefinition;
import digital.dispatch.TaxiLimoNewUI.Utils.Utils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;

public class SetTimeActivity extends BaseActivity {

	private WheelView dates;
	private WheelView times;
	private RadioButton now_btn;
	private RadioButton later_btn;
	private TextView save;
	private TextView cancel;
	private DateAdapter dateAdapter;
	private DateAdapter timeTodayAdapter;
	private DateAdapter timeNotTodayAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_time);
		findView();
		setUpWheel();
		bindEvents();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id==android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void findView() {
		dates = (WheelView) findViewById(R.id.dates);
		times = (WheelView) findViewById(R.id.times);
		now_btn = (RadioButton) findViewById(R.id.now);
		later_btn = (RadioButton) findViewById(R.id.later);
		save = (TextView) findViewById(R.id.save);
		cancel = (TextView) findViewById(R.id.cancel);
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

		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
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
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

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
			Date temp = new Date();
			temp = cal.getTime();
			timeList.add(temp);
			cal.add(Calendar.MINUTE, 5);
		}
		return timeList;
	}

	private ArrayList<Date> setupDateList() {
		ArrayList<Date> dateList = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i <= MBDefinition.FUTURE_BOOKING_RANGE; i++) {
			Date temp = new Date();
			temp = cal.getTime();
			dateList.add(temp);
			cal.add(Calendar.DATE, 1);
		}
		return dateList;
	}
}