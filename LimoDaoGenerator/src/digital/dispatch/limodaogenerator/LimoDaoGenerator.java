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
public class LimoDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1000, "digital.dispatch.TaxiLimoNewUI");

        addAddress(schema);
        addJob(schema);
        new DaoGenerator().generateAll(schema, "../TaxiLimoNewUI/src-gen");
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
        
        
        booking.addStringProperty("pickup_house_number");
        booking.addStringProperty("pickup_street_name");
        booking.addStringProperty("pickup_district");
        booking.addStringProperty("pickup_unit");
        booking.addStringProperty("pickup_landmark");
        booking.addDoubleProperty("pickup_longitude");
        booking.addDoubleProperty("pickup_latitude");
        
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

