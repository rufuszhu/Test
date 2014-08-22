package digital.dispatch.TaxiLimoNewUI.Adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import digital.dispatch.TaxiLimoNewUI.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

/**
 * Adapter for countries
 */
public class DateAdapter extends AbstractWheelTextAdapter {
	private static final int FUTURE_BOOKING_RANGE = 14;
	// City names
	final String dates[] = new String[FUTURE_BOOKING_RANGE+1];
	private ArrayList<String> timeList;
	public ArrayList<Calendar> dateListForReturn;
	public ArrayList<Date> timeListForReturn;
	private boolean isDate;


	/**
	 * Constructor
	 */
	public DateAdapter(Context context, boolean misDate) {
		super(context, R.layout.date_holo_layout, NO_RESOURCE);
		setItemTextResource(R.id.date_text);
		this.isDate = misDate;
		if(isDate){
			SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd",Locale.US);
			dateListForReturn = new ArrayList<Calendar>();
			Calendar cal = Calendar.getInstance();
			dateListForReturn.add(cal);
			dates[0]="Today";
			for (int i = 1; i <= FUTURE_BOOKING_RANGE; i ++) {
		    	
		    	cal.add(Calendar.DATE, 1);
		    	dateListForReturn.add(cal);
		    	Date date = cal.getTime();
		    	dates[i]=format.format(date);
		    }
			
		}
		else{
			timeList = new ArrayList<String>();
			timeListForReturn = new ArrayList<Date>();
			SimpleDateFormat format = new SimpleDateFormat("hh: mm a",Locale.US);
			Calendar cal = Calendar.getInstance();
			Calendar today = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			while(cal.get(Calendar.DATE)==today.get(Calendar.DATE)){
				timeListForReturn.add(cal.getTime());
				timeList.add(format.format(cal.getTime()));
				cal.add(Calendar.MINUTE, 5);
			}
			
		}
		
	}

	@Override
	public View getItem(int index, View cachedView, ViewGroup parent) {
		View view = super.getItem(index, cachedView, parent);
		return view;
	}

	@Override
	public int getItemsCount() {
		if(isDate)
		return dates.length;
		else
			return timeList.size();
	}

	@Override
	public CharSequence getItemText(int index) {
		if(isDate)
			return dates[index];
		else
			return timeList.get(index);
	}
	
	public Calendar getDate(int index){
		return dateListForReturn.get(index);
	}
	
	public Date getTime(int index){
		return timeListForReturn.get(index);
	}
	
}