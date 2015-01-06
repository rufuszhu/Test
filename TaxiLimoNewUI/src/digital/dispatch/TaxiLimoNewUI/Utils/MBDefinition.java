package digital.dispatch.TaxiLimoNewUI.Utils;

import android.util.SparseIntArray;
import digital.dispatch.TaxiLimoNewUI.R;

//this class is for global constant
public class MBDefinition {
	public final static String SYSTEM_PASSWORD= "MBOOKER";
	public final static int DRIVER_NOTE_MAX_LENGTH = 256;
	public final static int STREET_NUMBER_MAX_LENGTH = 7;
	
	public final static String OSP_VERSION= "3.0";
	
	public final static String ADDRESS = "ADDRESS";
	public final static String COMPANY_ITEM = "COMPANY_ITEM";
	public final static String SELECTED_ATTRIBUTE = "SELECTED_ATTRIBUTE";
	
	public final static String ADDRESSBAR_TEXT_EXTRA= "ADDRESSBAR_TEXT_EXTRA";
	public final static String EXTRA_SHOULD_BOOK_RIGHT_AFTER= "EXTRA_SHOULD_BOOK_RIGHT_AFTER";
	public static final String EXTRA_RETURN_SHOULD_BOOK_RIGHT_AFTER = "EXTRA_SHOULD_BOOK_RIGHT_AFTER";
	public final static String IS_DESTINATION= "IS_DESTINATION";
	public static final String EXTRA_BOOKING = "EXTRA_BOOKING";
	public final static String DBBOOKING_EXTRA= "DBBOOKING";
	public final static float DEFAULT_ZOOM = 15.5f;
	
	public final static int REQUEST_PICKUPADDRESS_CODE = 2;
	public final static int REQUEST_DROPOFFADDRESS_CODE = 3;
	public final static int REQUEST_COMPANYITEM_CODE = 4;
	public final static int REQUEST_SELECT_COMPANY_TO_BOOK = 5;
	public static final int REQUEST_PAYMENT = 6;
	public static final int REQUEST_REGISTER_CC = 7;
	public static final String[] SPECIAL_CHAR_TO_REMOVE = new String[] {"%", "[", "]", "<", ">", "\\"};
	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static final String PROPERTY_REG_ID = "registration_id";
	public static final String PROPERTY_APP_VERSION = "appVersion";
	public static final String PROPERTY_HARDWARE_ID = "hardware_id";
	public static final int FUTURE_BOOKING_RANGE = 14;
	
	// tripStatusUniformCode
	public final static int TRIP_STATUS_BOOKED = 1;
	public final static int TRIP_STATUS_DISPATCHING = 2;
	public final static int TRIP_STATUS_ACCEPTED = 3;
	public final static int TRIP_STATUS_ARRIVED = 4;
	public final static int TRIP_STATUS_COMPLETE = 5;
	
	// detailTripStatusUniformCode
	public final static int DETAIL_STATUS_IN_SERVICE = 1;
	public final static int DETAIL_STATUS_COMPLETE = 2;
	public final static int DETAIL_STATUS_CANCEL = 3;
	public final static int DETAIL_STATUS_NO_SHOW = 4;
	public final static int DETAIL_STATUS_FORCE_COMPLETE = 5;
	public final static int DETAIL_OTHER_IGNORE = 6;
	
	public final static int MB_STATUS_BOOKED = 11;
	public final static int MB_STATUS_ACCEPTED = 12;
	public final static int MB_STATUS_ARRIVED = 13;
	public final static int MB_STATUS_IN_SERVICE = 14;
	public final static int MB_STATUS_COMPLETED = 15;
	public final static int MB_STATUS_CANCELLED = 16;
	public final static int MB_STATUS_UNKNOWN = 17;
	
	public final static int IS_FOR_MAP = 1;
	public final static int IS_FOR_ONE_JOB = 2;
	public final static int IS_FOR_LIST = 3;
	

	public static final String SHARE_SND_MSG_DRV = "C_MB_SND_MSG_DRV"; 
	public static final String SHARE_DROP_OFF_MANDATORY = "C_MB_DRP_OFF_MAND";
	public static final String SHARE_MULTI_BOOK_ALLOWED = "C_MB_MLT_BOOK_ALWD";
	public static final String SHARE_SAME_LOG_BOOK_ALLOWED = "C_MB_SAME_LOC_BK_ALW";
	public static final String SHARE_TIP_BUTTON1 = "C_MB_TIP_BUTTON1";
	public static final String SHARE_PAYMENT_TMOUT = "C_MB_PAYMNT_TMOUT";
	public static final String SHARE_NAME = "SHARE_NAME";
	public static final String SHARE_EMAIL = "SHARE_EMAIL";
	public static final String SHARE_PHONE_NUMBER = "SHARE_PHONE_NUMBER";
	public static final String SHARE_ALREADY_REGISTER = "SHARE_ALREADY_REGISTER";
	public static final String SHARE_CC_PIN = "SHARE_CC_PIN";
	public static final String SHARE_ALREADY_SMS_VERIFY = "SHARE_ALREADY_SMS_VERIFY";
	public static final String SHARE_START_REGISTER = "SHARE_START_REGISTER";
	
	public static final int MDT_MAX_SEQUENCE_NUM = 255;
	//GCM notification event
	public final static int ACCEPT_EVENT = 0;	 //OSP_JOB_ACCEPT
	public final static int METER_ON_EVENT = 1;	 //OSP_METER_ON
	public final static int METER_OFF_EVENT = 2; //OSP_METER_OFF
	public final static int CANCELLED_EVENT = 3; //OSP_COMPLETE_BY_TRIP_CANCEL
	public final static int NO_SHOW_EVENT = 4;  //OSP_COMPLETE_BY_NO_SHOW
	public final static int FORCED_COMPLETE_EVENT = 5;  //OSP_COMPLETE_FORCE
	public final static int ARRIVED_EVENT = 6;  //OSP_ARRIVED
	public final static int LATE_TRIP_EVENT = 7;  //OSP_LATE_TRIP
	public final static int REJECT_FOR_STREET_HIRE = 8; // OSP_REJECT_TRIP
	public final static int REJECT_OFFER = 9; // OSP_REJECT_OFFER
	public final static int TRIP_OFFER = 10; // OSP_JOB_OFFER
	public final static int FARE_EVENT = 97; //driver initiated payment amt
	public final static int MSG_TO_RIDER = 98;	//Canned message to rider
	public final static int GPS_UPDATE_EVENT = 99;  //OSP_GPS_UPDATE
	
	//fontawesome icon Unicode
	//eg. http://fortawesome.github.io/Font-Awesome/icon/question-circle/
	public final static String icon_tab_calendar = "\ue3e6";
	public final static String icon_tab_track = "\ue0d3";
	public final static String icon_tab_clock = "\ue3e7";
	public final static String icon_tab_search = "\ue10d";
	public final static String icon_tab_fav = "\uf005";
	public final static String icon_tab_contact = "\ue451";
	public final static String icon_star_hollow = "\uf006";
	public final static String icon_pencil = "\ue0ec";
	public final static String icon_delete = "\ue0e6";
	public final static String icon_location = "\ue325";
	public final static String icon_phone = "\ue061";
	public final static String icon_current_location = "\uf124";
	//public final static String ICON_QUESTION_CIRCLE_CODE = "\uf059";
	public final static String ICON_QUESTION_CIRCLE_CODE = "\ue213";
	//public final static String ICON_TIMES_CODE = "\uf00d";//http://fortawesome.github.io/Font-Awesome/icon/times/
	public final static String ICON_TIMES_CODE = "\ue39f";
	//public final static String ICON_CHECK_CODE = "\uf00c";//http://fortawesome.github.io/Font-Awesome/icon/check/
	public final static String ICON_CHECK_CODE = "\ue396";
	public final static String ICON_CHECK_SQUARE_CODE = "\uf14a"; //http://fortawesome.github.io/Font-Awesome/icon/check-square/
	public final static String ICON_QUARE_O_CODE = "\uf096"; //http://fortawesome.github.io/Font-Awesome/icon/square-o/
	public final static String ICON_PERSON = "\uf183";
	public final static String ICON_DROPOFF = "\uf041";
	public final static String ICON_ANGLE_RIGHT = "\uf105";
	public final static String ICON_COMMENT = "\ue01e";
	public final static String ICON_COMPANY = "\uf1ba";
	public final static String ICON_EXCLAMATION_CIRCLE_CODE = "\ue214";
	public final static String ICON_CROSS = "\ue3a2";
	public final static String ICON_CROSS_SMALL = "\ue1f5";
	public final static String ICON_FUNNEL = "\ue3ea";
	public final static String ICON_INFO = "\ue214";
	public final static String ICON_ARROW_RIGHT = "\uf178";
	public final static String ICON_CHECK_CIRCLE = "\ue38d";
	public final static String ICON_CROSS_CIRCLE = "\ue1fd";
	public final static String ICON_BOOK_AGAIN = "\ue0db";
	public final static String ICON_SEARCH_AWESOME = "\uf002";
	public final static String ICON_MESSAGE = "\ue04d";
	public final static String ICON_PHONE_CIRCLE = "\ue062";
	public final static String ICON_WARNNING = "\ue1ed";
	
	public final static String ICON_TRACK_TAXI_YELLOW = "yellow";
	public final static String ICON_TRACK_TAXI_BLUE = "blue";
	public final static String ICON_TRACK_TAXI_RED = "red";
	public final static String ICON_TRACK_TAXI_ORANGE = "orange";
	public final static String ICON_TRACK_TAXI_GREEN = "green";

	public static final SparseIntArray attrIconMap;
    static {
    	SparseIntArray  aMap = new SparseIntArray();
        
    	aMap.put(1, R.drawable.attrbutes_icon_wheelchair);
        aMap.put(2, R.drawable.attrbutes_icon_van);  
        aMap.put(3, R.drawable.attrbutes_icon_pet);
        aMap.put(4, R.drawable.attrbutes_icon_airport);
        aMap.put(5, R.drawable.attrbutes_icon_ac);
        aMap.put(6, R.drawable.attrbutes_icon_creditcard);
        aMap.put(7, R.drawable.attrbutes_icon_female);
        aMap.put(8, R.drawable.attrbutes_icon_green);
        aMap.put(9, R.drawable.attrbutes_icon_grocery);
        aMap.put(10, R.drawable.attrbutes_icon_newspaper);
        aMap.put(11, R.drawable.attrbutes_icon_wifi);
        
        attrIconMap = aMap;
    }
    

	public static final String EXTRA_CREDIT_CARD = "EXTRA_CREDIT_CARD";
	
    public static final SparseIntArray attrBtnOnMap;
    static {
    	SparseIntArray  aMap = new SparseIntArray();
    	
    	aMap.put(1, R.drawable.attrbutes_filter_wheelchair_on);
        aMap.put(2, R.drawable.attrbutes_filter_van_on);   
        aMap.put(3, R.drawable.attrbutes_filter_pet_on);
        aMap.put(4, R.drawable.attrbutes_filter_airport_on);
        aMap.put(5, R.drawable.attrbutes_filter_ac_on);
        aMap.put(6, R.drawable.attrbutes_filter_creditcard_on);
        aMap.put(7, R.drawable.attrbutes_filter_female_on);
        aMap.put(8, R.drawable.attrbutes_filter_green_on);
        aMap.put(9, R.drawable.attrbutes_filter_grocery_on);
        aMap.put(10, R.drawable.attrbutes_filter_newspaper_on);
        aMap.put(11, R.drawable.attrbutes_filter_wifi_on);
        
        attrBtnOnMap = aMap;
    }
    
    public static final SparseIntArray attrBtnOffMap;
    static {
    	SparseIntArray  aMap = new SparseIntArray();
        
    	aMap.put(1, R.drawable.attrbutes_filter_wheelchair_off);
        aMap.put(2, R.drawable.attrbutes_filter_van_off);
        aMap.put(3, R.drawable.attrbutes_filter_pet_off);
        aMap.put(4, R.drawable.attrbutes_filter_airport_off);
        aMap.put(5, R.drawable.attrbutes_filter_ac_off);
        aMap.put(6, R.drawable.attrbutes_filter_creditcard_off);
        aMap.put(7, R.drawable.attrbutes_filter_female_off);
        aMap.put(8, R.drawable.attrbutes_filter_green_off);
        aMap.put(9, R.drawable.attrbutes_filter_grocery_off);
        aMap.put(10, R.drawable.attrbutes_filter_newspaper_off);
        aMap.put(11, R.drawable.attrbutes_filter_wifi_off);
        
        attrBtnOffMap = aMap;
    }
    
	public static enum ccRequestType {
		AddCard("2"), DeleteCard("3"), EditCard("4");
		
		private final String typeID;   // the actual value use in soap request
		ccRequestType(String tID) {
	        typeID = tID;
		}
		
		@Override
		public String toString() {
			return typeID;
		}
	}
}
