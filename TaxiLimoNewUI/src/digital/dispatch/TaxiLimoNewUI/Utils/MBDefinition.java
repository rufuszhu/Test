package digital.dispatch.TaxiLimoNewUI.Utils;
//this class is for global constant
public class MBDefinition {
	
	public final static String OSP_VERSION= "3.0";
	
	public final static String ADDRESS = "ADDRESS";
	public final static String ADDRESSBAR_TEXT_EXTRA= "ADDRESSBAR_TEXT_EXTRA";
	public final static String IS_DESTINATION= "IS_DESTINATION";
	public final static String DBBOOKING_EXTRA= "DBBOOKING";
	public final static float DEFAULT_ZOOM = 16.5f;
	public final static int DRIVER_NOTE_MAX_LENGTH = 256;
	public final static int REQUEST_PICKUPADDRESS_CODE = 2;
	public final static int REQUEST_DROPOFFADDRESS_CODE = 3;
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
}
