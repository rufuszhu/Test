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
        address.addDoubleProperty("latitude");
        address.addDoubleProperty("longitude");
        address.addBooleanProperty("isFavoriate");
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

