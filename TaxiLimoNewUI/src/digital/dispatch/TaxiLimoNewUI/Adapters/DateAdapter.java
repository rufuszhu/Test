package digital.dispatch.TaxiLimoNewUI.Adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import digital.dispatch.TaxiLimoNewUI.R;
import digital.dispatch.TaxiLimoNewUI.Utils.Logger;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

/**
 * Adapter for countries
 */
public class DateAdapter extends AbstractWheelTextAdapter {
	
	// City names
	//final String dates[] = new String[FUTURE_BOOKING_RANGE+1];
	private ArrayList<Date> timeList;
	private ArrayList<Date> dateList;
	private boolean isDate;
	private Typeface rionaSansMedium;
	private Context context;


	/**
	 * Constructor
	 */
	public DateAdapter(Context context, boolean misDate, ArrayList<Date> list) {
		super(context, R.layout.date_holo_layout, NO_RESOURCE);
		setItemTextResource(R.id.date_text);
		rionaSansMedium = Typeface.createFromAsset(context.getAssets(), "fonts/RionaSansMedium.otf");
		this.isDate = misDate;
		if(isDate){
			dateList = list;
		}
		else{
			timeList = list;
		}
		this.context = context;
	}

	@Override
	public View getItem(int index, View cachedView, ViewGroup parent) {
		View view = super.getItem(index, cachedView, parent);
		TextView date_text =  (TextView) view.findViewById(R.id.date_text);
		
		date_text.setTypeface(rionaSansMedium);
		

		return view;
	}

	@Override
	public int getItemsCount() {
		if(isDate)
			return dateList.size();
		else
			return timeList.size();
	}

	@Override
	public CharSequence getItemText(int index) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd",Locale.US);
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh: mm a",Locale.US);
		if(isDate){
			if(index==0)
				return "Today";
			else
				return dateFormat.format(dateList.get(index).getTime());
		}
		else{
			
			return timeFormat.format(timeList.get(index).getTime());
		}
	}
	
	public Date getDate(int index){
		return dateList.get(index);
	}
	
	public Date getTime(int index){
		return timeList.get(index);
	}
	
	public int getIndexOfTime(Date date){
		if(date == null)
			return 0;
		for(int i=0;i<timeList.size();i++){
			if(date.getMinutes()==timeList.get(i).getMinutes() && date.getHours() == timeList.get(i).getHours()){
				return i;
			}
		}
		return 0;
	}
	
	public int getIndexOfDate(Date date){
		if(date == null)
			return 0;
		for(int i=0;i<dateList.size();i++){
			if(date.getDate()==dateList.get(i).getDate()){
				return i;
			}
		}
		return 0;
	}
}