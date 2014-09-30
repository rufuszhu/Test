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
	public final static String DBBOOKING_EXTRA= "DBBOOKING";
	public final static float DEFAULT_ZOOM = 15.5f;
	
	public final static int REQUEST_PICKUPADDRESS_CODE = 2;
	public final static int REQUEST_DROPOFFADDRESS_CODE = 3;
	public final static int REQUEST_COMPANYITEM_CODE = 4;
	public final static int REQUEST_SELECT_COMPANY_TO_BOOK = 5;
	public static final int REQUEST_PAYMENT = 6;
	public static final String[] SPECIAL_CHAR_TO_REMOVE = new String[] {"%", "[", "]", "<", ">", "\\"};
	
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
	
	public final static int IS_FOR_MAP = 1;
	public final static int IS_FOR_ONE_JOB = 2;
	public final static int IS_FOR_LIST = 3;
	

	public static String SHARE_SND_MSG_DRV = "C_MB_SND_MSG_DRV"; 
	public static String SHARE_DROP_OFF_MANDATORY = "C_MB_DRP_OFF_MAND";
	public static String SHARE_MULTI_BOOK_ALLOWED = "C_MB_MLT_BOOK_ALWD";
	public static String SHARE_SAME_LOG_BOOK_ALLOWED = "C_MB_SAME_LOC_BK_ALW";
	public static String SHARE_TIP_BUTTON1 = "C_MB_TIP_BUTTON1";
	public static String SHARE_PAYMENT_TMOUT = "C_MB_PAYMNT_TMOUT";
	
	public static final int MDT_MAX_SEQUENCE_NUM = 255;
	
	public static final SparseIntArray attrIconMap;
    static {
    	SparseIntArray  aMap = new SparseIntArray();
        
        aMap.put(1, R.drawable.icon_attr_van);
        aMap.put(2, R.drawable.icon_attr_wheelchair);
        aMap.put(3, R.drawable.icon_attr_pet);
        aMap.put(4, R.drawable.icon_attr_airport);
        
        attrIconMap = aMap;
    }
    
    public static final SparseIntArray attrBtnMap;
	public static final String EXTRA_CREDIT_CARD = "EXTRA_CREDIT_CARD";
	
	

	
    static {
    	SparseIntArray  aMap = new SparseIntArray();
        
        aMap.put(1, R.drawable.ic_attr_van);
        aMap.put(2, R.drawable.ic_attr_wheelchair);
        aMap.put(3, R.drawable.ic_attr_pet);
        aMap.put(4, R.drawable.ic_attr_airport);
        
        attrBtnMap = aMap;
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
