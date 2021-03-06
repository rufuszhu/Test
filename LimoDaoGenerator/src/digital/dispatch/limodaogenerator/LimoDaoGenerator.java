package digital.dispatch.limodaogenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * Generates entities and DAOs for the example project DaoExample.
 * 
 * Run it as a Java application (not Android).
 * 
 * @author Markus
 */

/*IMPORTANT to update database, increase the schema number and need to read this 
 * http://stackoverflow.com/questions/24515967/green-dao-onupdate-how-can-i-add-new-columns-to-old-tables  
 * You need to code this, not just copy and paste
 * EXAMPLE:
 * 
 * @Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.i(TAG, "Update Schema to version: "+Integer.toString(oldVersion)+"->"+Integer.toString(newVersion));
    switch (oldVersion) {
        case 1:
            // v1->v2: all changes made in version 2 come here
            db.execSQL("ALTER TABLE "+MyDao.TABLENAME+" ADD COLUMN 'NEW_COL_1' INTEGER;");
            db.execSQL("DROP TABLE IF EXISTS 'MY_OLD_ENTITY'");
            // break was omitted by purpose.
        case 2:
            // v2->v3: all changes made in version 3 come here 
            MyNewDao.createTable(db, true);
            db.execSQL("ALTER TABLE "+MyDao.TABLENAME+" ADD COLUMN 'NEW_COL_2' TEXT;");
            // break was omitted by purpose.
    }
} */

//@Override
//public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//    Log.i(TAG, "Update Schema to version: "+Integer.toString(oldVersion)+"->"+Integer.toString(newVersion));
//    switch (oldVersion) {
//        case 1:
//            // v1->v2: all changes made in version 2 come here
//            db.execSQL("ALTER TABLE "+DBPreferenceDao.TABLENAME+" ADD COLUMN 'ATTRIBUTE_LIST' TEXT;");
//            db.execSQL("ALTER TABLE "+DBPreferenceDao.TABLENAME+" ADD COLUMN 'DESCRIPTION' TEXT;");
//            // break was omitted by purpose.
//    }
//}
public class LimoDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(2, "digital.dispatch.TaxiLimoNewUI");

        addAddress(schema);
        addJob(schema);
        addAttribute(schema);
        addCreditCard(schema);
        addPreference(schema);
        new DaoGenerator().generateAll(schema, "../TaxiLimoNewUI/src-gen");
    }

    private static void addPreference(Schema schema) {
    	Entity preference = schema.addEntity("DBPreference");
    	preference.addIdProperty();
    	preference.addStringProperty("country");
    	preference.addStringProperty("city");
    	preference.addStringProperty("region");
    	preference.addStringProperty("state");
    	preference.addStringProperty("destId");
    	preference.addStringProperty("companyName");
    	preference.addStringProperty("img");
    	preference.addStringProperty("attributeList");
    	preference.addStringProperty("description");
	}

	private static void addAddress(Schema schema) {
        Entity address = schema.addEntity("DBAddress");
        address.addIdProperty();
        address.addStringProperty("unit");
        address.addStringProperty("streetName");
        address.addStringProperty("houseNumber");
        address.addStringProperty("district");
        address.addStringProperty("province");
        address.addStringProperty("country");
        address.addStringProperty("nickName");
        address.addDoubleProperty("latitude");
        address.addDoubleProperty("longitude");
        address.addBooleanProperty("isFavoriate");
        address.addStringProperty("fullAddress");
        address.addStringProperty("zipCode"); //TL-301
    }
    
    private static void addCreditCard(Schema schema) {
        Entity cc = schema.addEntity("DBCreditCard");
        cc.addIdProperty();
        cc.addStringProperty("first4CardNum");
        cc.addStringProperty("last4CardNum");
        cc.addStringProperty("holderName");
        cc.addStringProperty("expiryMonth");
        cc.addStringProperty("expiryYear");
        cc.addStringProperty("zip");
        cc.addStringProperty("nickName");
        cc.addStringProperty("token");
        cc.addStringProperty("cardBrand");
    }
    
    private static void addJob(Schema schema) {
        Entity booking = schema.addEntity("DBBooking");
        booking.addIdProperty();
        booking.addStringProperty("destID");
        booking.addIntProperty("taxi_ride_id");
        booking.addStringProperty("ride_id");
        booking.addStringProperty("sysId");
        booking.addBooleanProperty("asap");
        
        booking.addDoubleProperty("carLatitude");
        booking.addDoubleProperty("carLongitude");
        
        booking.addStringProperty("dispatchedCar");
        booking.addStringProperty("dispatchedTime");
        booking.addStringProperty("dispatchedDriver");
        
        
        booking.addStringProperty("pickup_house_number");
        booking.addStringProperty("pickup_street_name");
        booking.addStringProperty("pickup_district");
        booking.addStringProperty("pickup_unit");
        booking.addStringProperty("pickup_landmark");
        booking.addDoubleProperty("pickup_longitude");
        booking.addDoubleProperty("pickup_latitude");
        booking.addStringProperty("pickup_zipCode"); //TL-301
        
        booking.addStringProperty("dropoff_house_number");
        booking.addStringProperty("dropoff_street_name");
        booking.addStringProperty("dropoff_district");
        booking.addStringProperty("dropoff_unit");
        booking.addStringProperty("dropoff_landmark");
        booking.addDoubleProperty("dropoff_longitude");
        booking.addDoubleProperty("dropoff_latitude");
        
        booking.addStringProperty("attributeList");
        booking.addStringProperty("phonenum");
        booking.addStringProperty("pickup_time");
        booking.addStringProperty("remarks");
        
        booking.addStringProperty("tripCancelledTime");
        booking.addStringProperty("tripCreationTime");
        booking.addStringProperty("tripModificationTime"); 
        booking.addStringProperty("tripCompletionTime");
        
        booking.addIntProperty("tripStatus");
        
        booking.addStringProperty("pickupAddress");
        booking.addStringProperty("dropoffAddress");
        booking.addBooleanProperty("already_paid");
        booking.addBooleanProperty("multi_pay_allow");
        
        booking.addStringProperty("company_name");
        booking.addStringProperty("company_description");
        booking.addStringProperty("company_phone_number");
        booking.addStringProperty("company_icon");
        booking.addStringProperty("company_attribute_list");
        booking.addStringProperty("company_dupChk_time");
        booking.addStringProperty("company_car_file");
        booking.addIntProperty("company_baseRate"); //TL-304
        booking.addIntProperty("company_rate_PerDistance");
        
        booking.addBooleanProperty("shouldForceDisableCancel");//TL-343
        
        booking.addStringProperty("authCode");
        booking.addStringProperty("paidAmount");
        
    }
    
    private static void addAttribute(Schema schema) {
        Entity address = schema.addEntity("DBAttribute");
        address.addIdProperty();
        address.addStringProperty("attributeId");
        address.addStringProperty("name");
        address.addStringProperty("iconId");
    }

//    private static void addCustomerOrder(Schema schema) {
//        Entity customer = schema.addEntity("Customer");
//        customer.addIdProperty();
//        customer.addStringProperty("name").notNull();
//
//        Entity order = schema.addEntity("Order");
//        order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
//        order.addIdProperty();
//        Property orderDate = order.addDateProperty("date").getProperty();
//        Property customerId = order.addLongProperty("customerId").notNull().getProperty();
//        order.addToOne(customer, customerId);
//
//        ToMany customerToOrders = customer.addToMany(order, customerId);
//        customerToOrders.setName("orders");
//        customerToOrders.orderAsc(orderDate);
//    }

}

