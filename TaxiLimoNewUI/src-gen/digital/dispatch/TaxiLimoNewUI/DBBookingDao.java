package digital.dispatch.TaxiLimoNewUI;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import digital.dispatch.TaxiLimoNewUI.DBBooking;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DBBOOKING.
*/
public class DBBookingDao extends AbstractDao<DBBooking, Long> {

    public static final String TABLENAME = "DBBOOKING";

    /**
     * Properties of entity DBBooking.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DestID = new Property(1, String.class, "destID", false, "DEST_ID");
        public final static Property Taxi_ride_id = new Property(2, Integer.class, "taxi_ride_id", false, "TAXI_RIDE_ID");
        public final static Property Ride_id = new Property(3, String.class, "ride_id", false, "RIDE_ID");
        public final static Property SysId = new Property(4, String.class, "sysId", false, "SYS_ID");
        public final static Property Asap = new Property(5, Boolean.class, "asap", false, "ASAP");
        public final static Property CarLatitude = new Property(6, Double.class, "carLatitude", false, "CAR_LATITUDE");
        public final static Property CarLongitude = new Property(7, Double.class, "carLongitude", false, "CAR_LONGITUDE");
        public final static Property DispatchedCar = new Property(8, String.class, "dispatchedCar", false, "DISPATCHED_CAR");
        public final static Property DispatchedTime = new Property(9, String.class, "dispatchedTime", false, "DISPATCHED_TIME");
        public final static Property DispatchedDriver = new Property(10, String.class, "dispatchedDriver", false, "DISPATCHED_DRIVER");
        public final static Property Pickup_house_number = new Property(11, String.class, "pickup_house_number", false, "PICKUP_HOUSE_NUMBER");
        public final static Property Pickup_street_name = new Property(12, String.class, "pickup_street_name", false, "PICKUP_STREET_NAME");
        public final static Property Pickup_district = new Property(13, String.class, "pickup_district", false, "PICKUP_DISTRICT");
        public final static Property Pickup_unit = new Property(14, String.class, "pickup_unit", false, "PICKUP_UNIT");
        public final static Property Pickup_landmark = new Property(15, String.class, "pickup_landmark", false, "PICKUP_LANDMARK");
        public final static Property Pickup_longitude = new Property(16, Double.class, "pickup_longitude", false, "PICKUP_LONGITUDE");
        public final static Property Pickup_latitude = new Property(17, Double.class, "pickup_latitude", false, "PICKUP_LATITUDE");
        public final static Property Dropoff_house_number = new Property(18, String.class, "dropoff_house_number", false, "DROPOFF_HOUSE_NUMBER");
        public final static Property Dropoff_street_name = new Property(19, String.class, "dropoff_street_name", false, "DROPOFF_STREET_NAME");
        public final static Property Dropoff_district = new Property(20, String.class, "dropoff_district", false, "DROPOFF_DISTRICT");
        public final static Property Dropoff_unit = new Property(21, String.class, "dropoff_unit", false, "DROPOFF_UNIT");
        public final static Property Dropoff_landmark = new Property(22, String.class, "dropoff_landmark", false, "DROPOFF_LANDMARK");
        public final static Property Dropoff_longitude = new Property(23, Double.class, "dropoff_longitude", false, "DROPOFF_LONGITUDE");
        public final static Property Dropoff_latitude = new Property(24, Double.class, "dropoff_latitude", false, "DROPOFF_LATITUDE");
        public final static Property AttributeList = new Property(25, String.class, "attributeList", false, "ATTRIBUTE_LIST");
        public final static Property Phonenum = new Property(26, String.class, "phonenum", false, "PHONENUM");
        public final static Property Pickup_time = new Property(27, String.class, "pickup_time", false, "PICKUP_TIME");
        public final static Property Remarks = new Property(28, String.class, "remarks", false, "REMARKS");
        public final static Property TripCancelledTime = new Property(29, String.class, "tripCancelledTime", false, "TRIP_CANCELLED_TIME");
        public final static Property TripCreationTime = new Property(30, String.class, "tripCreationTime", false, "TRIP_CREATION_TIME");
        public final static Property TripModificationTime = new Property(31, String.class, "tripModificationTime", false, "TRIP_MODIFICATION_TIME");
        public final static Property TripCompletionTime = new Property(32, String.class, "tripCompletionTime", false, "TRIP_COMPLETION_TIME");
        public final static Property TripStatus = new Property(33, Integer.class, "tripStatus", false, "TRIP_STATUS");
        public final static Property PickupAddress = new Property(34, String.class, "pickupAddress", false, "PICKUP_ADDRESS");
        public final static Property DropoffAddress = new Property(35, String.class, "dropoffAddress", false, "DROPOFF_ADDRESS");
        public final static Property Already_paid = new Property(36, Boolean.class, "already_paid", false, "ALREADY_PAID");
        public final static Property Multi_pay_allow = new Property(37, Boolean.class, "multi_pay_allow", false, "MULTI_PAY_ALLOW");
        public final static Property Company_name = new Property(38, String.class, "company_name", false, "COMPANY_NAME");
        public final static Property Company_description = new Property(39, String.class, "company_description", false, "COMPANY_DESCRIPTION");
        public final static Property Company_phone_number = new Property(40, String.class, "company_phone_number", false, "COMPANY_PHONE_NUMBER");
        public final static Property Company_icon = new Property(41, String.class, "company_icon", false, "COMPANY_ICON");
        public final static Property Company_attribute_list = new Property(42, String.class, "company_attribute_list", false, "COMPANY_ATTRIBUTE_LIST");
        public final static Property Company_dupChk_time = new Property(43, String.class, "company_dupChk_time", false, "COMPANY_DUP_CHK_TIME");
        public final static Property AuthCode = new Property(44, String.class, "authCode", false, "AUTH_CODE");
        public final static Property PaidAmount = new Property(45, String.class, "paidAmount", false, "PAID_AMOUNT");
    };


    public DBBookingDao(DaoConfig config) {
        super(config);
    }
    
    public DBBookingDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DBBOOKING' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'DEST_ID' TEXT," + // 1: destID
                "'TAXI_RIDE_ID' INTEGER," + // 2: taxi_ride_id
                "'RIDE_ID' TEXT," + // 3: ride_id
                "'SYS_ID' TEXT," + // 4: sysId
                "'ASAP' INTEGER," + // 5: asap
                "'CAR_LATITUDE' REAL," + // 6: carLatitude
                "'CAR_LONGITUDE' REAL," + // 7: carLongitude
                "'DISPATCHED_CAR' TEXT," + // 8: dispatchedCar
                "'DISPATCHED_TIME' TEXT," + // 9: dispatchedTime
                "'DISPATCHED_DRIVER' TEXT," + // 10: dispatchedDriver
                "'PICKUP_HOUSE_NUMBER' TEXT," + // 11: pickup_house_number
                "'PICKUP_STREET_NAME' TEXT," + // 12: pickup_street_name
                "'PICKUP_DISTRICT' TEXT," + // 13: pickup_district
                "'PICKUP_UNIT' TEXT," + // 14: pickup_unit
                "'PICKUP_LANDMARK' TEXT," + // 15: pickup_landmark
                "'PICKUP_LONGITUDE' REAL," + // 16: pickup_longitude
                "'PICKUP_LATITUDE' REAL," + // 17: pickup_latitude
                "'DROPOFF_HOUSE_NUMBER' TEXT," + // 18: dropoff_house_number
                "'DROPOFF_STREET_NAME' TEXT," + // 19: dropoff_street_name
                "'DROPOFF_DISTRICT' TEXT," + // 20: dropoff_district
                "'DROPOFF_UNIT' TEXT," + // 21: dropoff_unit
                "'DROPOFF_LANDMARK' TEXT," + // 22: dropoff_landmark
                "'DROPOFF_LONGITUDE' REAL," + // 23: dropoff_longitude
                "'DROPOFF_LATITUDE' REAL," + // 24: dropoff_latitude
                "'ATTRIBUTE_LIST' TEXT," + // 25: attributeList
                "'PHONENUM' TEXT," + // 26: phonenum
                "'PICKUP_TIME' TEXT," + // 27: pickup_time
                "'REMARKS' TEXT," + // 28: remarks
                "'TRIP_CANCELLED_TIME' TEXT," + // 29: tripCancelledTime
                "'TRIP_CREATION_TIME' TEXT," + // 30: tripCreationTime
                "'TRIP_MODIFICATION_TIME' TEXT," + // 31: tripModificationTime
                "'TRIP_COMPLETION_TIME' TEXT," + // 32: tripCompletionTime
                "'TRIP_STATUS' INTEGER," + // 33: tripStatus
                "'PICKUP_ADDRESS' TEXT," + // 34: pickupAddress
                "'DROPOFF_ADDRESS' TEXT," + // 35: dropoffAddress
                "'ALREADY_PAID' INTEGER," + // 36: already_paid
                "'MULTI_PAY_ALLOW' INTEGER," + // 37: multi_pay_allow
                "'COMPANY_NAME' TEXT," + // 38: company_name
                "'COMPANY_DESCRIPTION' TEXT," + // 39: company_description
                "'COMPANY_PHONE_NUMBER' TEXT," + // 40: company_phone_number
                "'COMPANY_ICON' TEXT," + // 41: company_icon
                "'COMPANY_ATTRIBUTE_LIST' TEXT," + // 42: company_attribute_list
                "'COMPANY_DUP_CHK_TIME' TEXT," + // 43: company_dupChk_time
                "'AUTH_CODE' TEXT," + // 44: authCode
                "'PAID_AMOUNT' TEXT);"); // 45: paidAmount
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DBBOOKING'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DBBooking entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String destID = entity.getDestID();
        if (destID != null) {
            stmt.bindString(2, destID);
        }
 
        Integer taxi_ride_id = entity.getTaxi_ride_id();
        if (taxi_ride_id != null) {
            stmt.bindLong(3, taxi_ride_id);
        }
 
        String ride_id = entity.getRide_id();
        if (ride_id != null) {
            stmt.bindString(4, ride_id);
        }
 
        String sysId = entity.getSysId();
        if (sysId != null) {
            stmt.bindString(5, sysId);
        }
 
        Boolean asap = entity.getAsap();
        if (asap != null) {
            stmt.bindLong(6, asap ? 1l: 0l);
        }
 
        Double carLatitude = entity.getCarLatitude();
        if (carLatitude != null) {
            stmt.bindDouble(7, carLatitude);
        }
 
        Double carLongitude = entity.getCarLongitude();
        if (carLongitude != null) {
            stmt.bindDouble(8, carLongitude);
        }
 
        String dispatchedCar = entity.getDispatchedCar();
        if (dispatchedCar != null) {
            stmt.bindString(9, dispatchedCar);
        }
 
        String dispatchedTime = entity.getDispatchedTime();
        if (dispatchedTime != null) {
            stmt.bindString(10, dispatchedTime);
        }
 
        String dispatchedDriver = entity.getDispatchedDriver();
        if (dispatchedDriver != null) {
            stmt.bindString(11, dispatchedDriver);
        }
 
        String pickup_house_number = entity.getPickup_house_number();
        if (pickup_house_number != null) {
            stmt.bindString(12, pickup_house_number);
        }
 
        String pickup_street_name = entity.getPickup_street_name();
        if (pickup_street_name != null) {
            stmt.bindString(13, pickup_street_name);
        }
 
        String pickup_district = entity.getPickup_district();
        if (pickup_district != null) {
            stmt.bindString(14, pickup_district);
        }
 
        String pickup_unit = entity.getPickup_unit();
        if (pickup_unit != null) {
            stmt.bindString(15, pickup_unit);
        }
 
        String pickup_landmark = entity.getPickup_landmark();
        if (pickup_landmark != null) {
            stmt.bindString(16, pickup_landmark);
        }
 
        Double pickup_longitude = entity.getPickup_longitude();
        if (pickup_longitude != null) {
            stmt.bindDouble(17, pickup_longitude);
        }
 
        Double pickup_latitude = entity.getPickup_latitude();
        if (pickup_latitude != null) {
            stmt.bindDouble(18, pickup_latitude);
        }
 
        String dropoff_house_number = entity.getDropoff_house_number();
        if (dropoff_house_number != null) {
            stmt.bindString(19, dropoff_house_number);
        }
 
        String dropoff_street_name = entity.getDropoff_street_name();
        if (dropoff_street_name != null) {
            stmt.bindString(20, dropoff_street_name);
        }
 
        String dropoff_district = entity.getDropoff_district();
        if (dropoff_district != null) {
            stmt.bindString(21, dropoff_district);
        }
 
        String dropoff_unit = entity.getDropoff_unit();
        if (dropoff_unit != null) {
            stmt.bindString(22, dropoff_unit);
        }
 
        String dropoff_landmark = entity.getDropoff_landmark();
        if (dropoff_landmark != null) {
            stmt.bindString(23, dropoff_landmark);
        }
 
        Double dropoff_longitude = entity.getDropoff_longitude();
        if (dropoff_longitude != null) {
            stmt.bindDouble(24, dropoff_longitude);
        }
 
        Double dropoff_latitude = entity.getDropoff_latitude();
        if (dropoff_latitude != null) {
            stmt.bindDouble(25, dropoff_latitude);
        }
 
        String attributeList = entity.getAttributeList();
        if (attributeList != null) {
            stmt.bindString(26, attributeList);
        }
 
        String phonenum = entity.getPhonenum();
        if (phonenum != null) {
            stmt.bindString(27, phonenum);
        }
 
        String pickup_time = entity.getPickup_time();
        if (pickup_time != null) {
            stmt.bindString(28, pickup_time);
        }
 
        String remarks = entity.getRemarks();
        if (remarks != null) {
            stmt.bindString(29, remarks);
        }
 
        String tripCancelledTime = entity.getTripCancelledTime();
        if (tripCancelledTime != null) {
            stmt.bindString(30, tripCancelledTime);
        }
 
        String tripCreationTime = entity.getTripCreationTime();
        if (tripCreationTime != null) {
            stmt.bindString(31, tripCreationTime);
        }
 
        String tripModificationTime = entity.getTripModificationTime();
        if (tripModificationTime != null) {
            stmt.bindString(32, tripModificationTime);
        }
 
        String tripCompletionTime = entity.getTripCompletionTime();
        if (tripCompletionTime != null) {
            stmt.bindString(33, tripCompletionTime);
        }
 
        Integer tripStatus = entity.getTripStatus();
        if (tripStatus != null) {
            stmt.bindLong(34, tripStatus);
        }
 
        String pickupAddress = entity.getPickupAddress();
        if (pickupAddress != null) {
            stmt.bindString(35, pickupAddress);
        }
 
        String dropoffAddress = entity.getDropoffAddress();
        if (dropoffAddress != null) {
            stmt.bindString(36, dropoffAddress);
        }
 
        Boolean already_paid = entity.getAlready_paid();
        if (already_paid != null) {
            stmt.bindLong(37, already_paid ? 1l: 0l);
        }
 
        Boolean multi_pay_allow = entity.getMulti_pay_allow();
        if (multi_pay_allow != null) {
            stmt.bindLong(38, multi_pay_allow ? 1l: 0l);
        }
 
        String company_name = entity.getCompany_name();
        if (company_name != null) {
            stmt.bindString(39, company_name);
        }
 
        String company_description = entity.getCompany_description();
        if (company_description != null) {
            stmt.bindString(40, company_description);
        }
 
        String company_phone_number = entity.getCompany_phone_number();
        if (company_phone_number != null) {
            stmt.bindString(41, company_phone_number);
        }
 
        String company_icon = entity.getCompany_icon();
        if (company_icon != null) {
            stmt.bindString(42, company_icon);
        }
 
        String company_attribute_list = entity.getCompany_attribute_list();
        if (company_attribute_list != null) {
            stmt.bindString(43, company_attribute_list);
        }
 
        String company_dupChk_time = entity.getCompany_dupChk_time();
        if (company_dupChk_time != null) {
            stmt.bindString(44, company_dupChk_time);
        }
 
        String authCode = entity.getAuthCode();
        if (authCode != null) {
            stmt.bindString(45, authCode);
        }
 
        String paidAmount = entity.getPaidAmount();
        if (paidAmount != null) {
            stmt.bindString(46, paidAmount);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public DBBooking readEntity(Cursor cursor, int offset) {
        DBBooking entity = new DBBooking( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // destID
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // taxi_ride_id
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ride_id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // sysId
            cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0, // asap
            cursor.isNull(offset + 6) ? null : cursor.getDouble(offset + 6), // carLatitude
            cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7), // carLongitude
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // dispatchedCar
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // dispatchedTime
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // dispatchedDriver
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // pickup_house_number
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // pickup_street_name
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // pickup_district
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // pickup_unit
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // pickup_landmark
            cursor.isNull(offset + 16) ? null : cursor.getDouble(offset + 16), // pickup_longitude
            cursor.isNull(offset + 17) ? null : cursor.getDouble(offset + 17), // pickup_latitude
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // dropoff_house_number
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // dropoff_street_name
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // dropoff_district
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // dropoff_unit
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // dropoff_landmark
            cursor.isNull(offset + 23) ? null : cursor.getDouble(offset + 23), // dropoff_longitude
            cursor.isNull(offset + 24) ? null : cursor.getDouble(offset + 24), // dropoff_latitude
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25), // attributeList
            cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26), // phonenum
            cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27), // pickup_time
            cursor.isNull(offset + 28) ? null : cursor.getString(offset + 28), // remarks
            cursor.isNull(offset + 29) ? null : cursor.getString(offset + 29), // tripCancelledTime
            cursor.isNull(offset + 30) ? null : cursor.getString(offset + 30), // tripCreationTime
            cursor.isNull(offset + 31) ? null : cursor.getString(offset + 31), // tripModificationTime
            cursor.isNull(offset + 32) ? null : cursor.getString(offset + 32), // tripCompletionTime
            cursor.isNull(offset + 33) ? null : cursor.getInt(offset + 33), // tripStatus
            cursor.isNull(offset + 34) ? null : cursor.getString(offset + 34), // pickupAddress
            cursor.isNull(offset + 35) ? null : cursor.getString(offset + 35), // dropoffAddress
            cursor.isNull(offset + 36) ? null : cursor.getShort(offset + 36) != 0, // already_paid
            cursor.isNull(offset + 37) ? null : cursor.getShort(offset + 37) != 0, // multi_pay_allow
            cursor.isNull(offset + 38) ? null : cursor.getString(offset + 38), // company_name
            cursor.isNull(offset + 39) ? null : cursor.getString(offset + 39), // company_description
            cursor.isNull(offset + 40) ? null : cursor.getString(offset + 40), // company_phone_number
            cursor.isNull(offset + 41) ? null : cursor.getString(offset + 41), // company_icon
            cursor.isNull(offset + 42) ? null : cursor.getString(offset + 42), // company_attribute_list
            cursor.isNull(offset + 43) ? null : cursor.getString(offset + 43), // company_dupChk_time
            cursor.isNull(offset + 44) ? null : cursor.getString(offset + 44), // authCode
            cursor.isNull(offset + 45) ? null : cursor.getString(offset + 45) // paidAmount
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DBBooking entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDestID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTaxi_ride_id(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setRide_id(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSysId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAsap(cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0);
        entity.setCarLatitude(cursor.isNull(offset + 6) ? null : cursor.getDouble(offset + 6));
        entity.setCarLongitude(cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7));
        entity.setDispatchedCar(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDispatchedTime(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setDispatchedDriver(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setPickup_house_number(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setPickup_street_name(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setPickup_district(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setPickup_unit(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setPickup_landmark(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setPickup_longitude(cursor.isNull(offset + 16) ? null : cursor.getDouble(offset + 16));
        entity.setPickup_latitude(cursor.isNull(offset + 17) ? null : cursor.getDouble(offset + 17));
        entity.setDropoff_house_number(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setDropoff_street_name(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setDropoff_district(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setDropoff_unit(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setDropoff_landmark(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setDropoff_longitude(cursor.isNull(offset + 23) ? null : cursor.getDouble(offset + 23));
        entity.setDropoff_latitude(cursor.isNull(offset + 24) ? null : cursor.getDouble(offset + 24));
        entity.setAttributeList(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
        entity.setPhonenum(cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26));
        entity.setPickup_time(cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27));
        entity.setRemarks(cursor.isNull(offset + 28) ? null : cursor.getString(offset + 28));
        entity.setTripCancelledTime(cursor.isNull(offset + 29) ? null : cursor.getString(offset + 29));
        entity.setTripCreationTime(cursor.isNull(offset + 30) ? null : cursor.getString(offset + 30));
        entity.setTripModificationTime(cursor.isNull(offset + 31) ? null : cursor.getString(offset + 31));
        entity.setTripCompletionTime(cursor.isNull(offset + 32) ? null : cursor.getString(offset + 32));
        entity.setTripStatus(cursor.isNull(offset + 33) ? null : cursor.getInt(offset + 33));
        entity.setPickupAddress(cursor.isNull(offset + 34) ? null : cursor.getString(offset + 34));
        entity.setDropoffAddress(cursor.isNull(offset + 35) ? null : cursor.getString(offset + 35));
        entity.setAlready_paid(cursor.isNull(offset + 36) ? null : cursor.getShort(offset + 36) != 0);
        entity.setMulti_pay_allow(cursor.isNull(offset + 37) ? null : cursor.getShort(offset + 37) != 0);
        entity.setCompany_name(cursor.isNull(offset + 38) ? null : cursor.getString(offset + 38));
        entity.setCompany_description(cursor.isNull(offset + 39) ? null : cursor.getString(offset + 39));
        entity.setCompany_phone_number(cursor.isNull(offset + 40) ? null : cursor.getString(offset + 40));
        entity.setCompany_icon(cursor.isNull(offset + 41) ? null : cursor.getString(offset + 41));
        entity.setCompany_attribute_list(cursor.isNull(offset + 42) ? null : cursor.getString(offset + 42));
        entity.setCompany_dupChk_time(cursor.isNull(offset + 43) ? null : cursor.getString(offset + 43));
        entity.setAuthCode(cursor.isNull(offset + 44) ? null : cursor.getString(offset + 44));
        entity.setPaidAmount(cursor.isNull(offset + 45) ? null : cursor.getString(offset + 45));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DBBooking entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DBBooking entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
