package digital.dispatch.TaxiLimoNewUI;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import java.io.Serializable;

/**
 * Entity mapped to table DBBOOKING.
 */
public class DBBooking implements Serializable{

    private Long id;
    private String destID;
    private Integer taxi_ride_id;
    private String ride_id;
    private String sysId;
    private Boolean asap;
    private Double carLatitude;
    private Double carLongitude;
    private String dispatchedCar;
    private String dispatchedTime;
    private String dispatchedDriver;
    private String pickup_house_number;
    private String pickup_street_name;
    private String pickup_district;
    private String pickup_unit;
    private String pickup_landmark;
    private Double pickup_longitude;
    private Double pickup_latitude;
    private String pickup_zipCode;
    private String dropoff_house_number;
    private String dropoff_street_name;
    private String dropoff_district;
    private String dropoff_unit;
    private String dropoff_landmark;
    private Double dropoff_longitude;
    private Double dropoff_latitude;
    private String attributeList;
    private String phonenum;
    private String pickup_time;
    private String remarks;
    private String tripCancelledTime;
    private String tripCreationTime;
    private String tripModificationTime;
    private String tripCompletionTime;
    private Integer tripStatus;
    private String pickupAddress;
    private String dropoffAddress;
    private Boolean already_paid;
    private Boolean multi_pay_allow;
    private String company_name;
    private String company_description;
    private String company_phone_number;
    private String company_icon;
    private String company_attribute_list;
    private String company_dupChk_time;
    private String company_car_file;
    private Integer company_baseRate;
    private Integer company_rate_PerDistance;
    private Boolean shouldForceDisableCancel;
    private String authCode;
    private String paidAmount;

    public DBBooking() {
    }

    public DBBooking(Long id) {
        this.id = id;
    }

    public DBBooking(Long id, String destID, Integer taxi_ride_id, String ride_id, String sysId, Boolean asap, Double carLatitude, Double carLongitude, String dispatchedCar, String dispatchedTime, String dispatchedDriver, String pickup_house_number, String pickup_street_name, String pickup_district, String pickup_unit, String pickup_landmark, Double pickup_longitude, Double pickup_latitude, String pickup_zipCode, String dropoff_house_number, String dropoff_street_name, String dropoff_district, String dropoff_unit, String dropoff_landmark, Double dropoff_longitude, Double dropoff_latitude, String attributeList, String phonenum, String pickup_time, String remarks, String tripCancelledTime, String tripCreationTime, String tripModificationTime, String tripCompletionTime, Integer tripStatus, String pickupAddress, String dropoffAddress, Boolean already_paid, Boolean multi_pay_allow, String company_name, String company_description, String company_phone_number, String company_icon, String company_attribute_list, String company_dupChk_time, String company_car_file, Integer company_baseRate, Integer company_rate_PerDistance, Boolean shouldForceDisableCancel, String authCode, String paidAmount) {
        this.id = id;
        this.destID = destID;
        this.taxi_ride_id = taxi_ride_id;
        this.ride_id = ride_id;
        this.sysId = sysId;
        this.asap = asap;
        this.carLatitude = carLatitude;
        this.carLongitude = carLongitude;
        this.dispatchedCar = dispatchedCar;
        this.dispatchedTime = dispatchedTime;
        this.dispatchedDriver = dispatchedDriver;
        this.pickup_house_number = pickup_house_number;
        this.pickup_street_name = pickup_street_name;
        this.pickup_district = pickup_district;
        this.pickup_unit = pickup_unit;
        this.pickup_landmark = pickup_landmark;
        this.pickup_longitude = pickup_longitude;
        this.pickup_latitude = pickup_latitude;
        this.pickup_zipCode = pickup_zipCode;
        this.dropoff_house_number = dropoff_house_number;
        this.dropoff_street_name = dropoff_street_name;
        this.dropoff_district = dropoff_district;
        this.dropoff_unit = dropoff_unit;
        this.dropoff_landmark = dropoff_landmark;
        this.dropoff_longitude = dropoff_longitude;
        this.dropoff_latitude = dropoff_latitude;
        this.attributeList = attributeList;
        this.phonenum = phonenum;
        this.pickup_time = pickup_time;
        this.remarks = remarks;
        this.tripCancelledTime = tripCancelledTime;
        this.tripCreationTime = tripCreationTime;
        this.tripModificationTime = tripModificationTime;
        this.tripCompletionTime = tripCompletionTime;
        this.tripStatus = tripStatus;
        this.pickupAddress = pickupAddress;
        this.dropoffAddress = dropoffAddress;
        this.already_paid = already_paid;
        this.multi_pay_allow = multi_pay_allow;
        this.company_name = company_name;
        this.company_description = company_description;
        this.company_phone_number = company_phone_number;
        this.company_icon = company_icon;
        this.company_attribute_list = company_attribute_list;
        this.company_dupChk_time = company_dupChk_time;
        this.company_car_file = company_car_file;
        this.company_baseRate = company_baseRate;
        this.company_rate_PerDistance = company_rate_PerDistance;
        this.shouldForceDisableCancel = shouldForceDisableCancel;
        this.authCode = authCode;
        this.paidAmount = paidAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDestID() {
        return destID;
    }

    public void setDestID(String destID) {
        this.destID = destID;
    }

    public Integer getTaxi_ride_id() {
        return taxi_ride_id;
    }

    public void setTaxi_ride_id(Integer taxi_ride_id) {
        this.taxi_ride_id = taxi_ride_id;
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public Boolean getAsap() {
        return asap;
    }

    public void setAsap(Boolean asap) {
        this.asap = asap;
    }

    public Double getCarLatitude() {
        return carLatitude;
    }

    public void setCarLatitude(Double carLatitude) {
        this.carLatitude = carLatitude;
    }

    public Double getCarLongitude() {
        return carLongitude;
    }

    public void setCarLongitude(Double carLongitude) {
        this.carLongitude = carLongitude;
    }

    public String getDispatchedCar() {
        return dispatchedCar;
    }

    public void setDispatchedCar(String dispatchedCar) {
        this.dispatchedCar = dispatchedCar;
    }

    public String getDispatchedTime() {
        return dispatchedTime;
    }

    public void setDispatchedTime(String dispatchedTime) {
        this.dispatchedTime = dispatchedTime;
    }

    public String getDispatchedDriver() {
        return dispatchedDriver;
    }

    public void setDispatchedDriver(String dispatchedDriver) {
        this.dispatchedDriver = dispatchedDriver;
    }

    public String getPickup_house_number() {
        return pickup_house_number;
    }

    public void setPickup_house_number(String pickup_house_number) {
        this.pickup_house_number = pickup_house_number;
    }

    public String getPickup_street_name() {
        return pickup_street_name;
    }

    public void setPickup_street_name(String pickup_street_name) {
        this.pickup_street_name = pickup_street_name;
    }

    public String getPickup_district() {
        return pickup_district;
    }

    public void setPickup_district(String pickup_district) {
        this.pickup_district = pickup_district;
    }

    public String getPickup_unit() {
        return pickup_unit;
    }

    public void setPickup_unit(String pickup_unit) {
        this.pickup_unit = pickup_unit;
    }

    public String getPickup_landmark() {
        return pickup_landmark;
    }

    public void setPickup_landmark(String pickup_landmark) {
        this.pickup_landmark = pickup_landmark;
    }

    public Double getPickup_longitude() {
        return pickup_longitude;
    }

    public void setPickup_longitude(Double pickup_longitude) {
        this.pickup_longitude = pickup_longitude;
    }

    public Double getPickup_latitude() {
        return pickup_latitude;
    }

    public void setPickup_latitude(Double pickup_latitude) {
        this.pickup_latitude = pickup_latitude;
    }

    public String getPickup_zipCode() {
        return pickup_zipCode;
    }

    public void setPickup_zipCode(String pickup_zipCode) {
        this.pickup_zipCode = pickup_zipCode;
    }

    public String getDropoff_house_number() {
        return dropoff_house_number;
    }

    public void setDropoff_house_number(String dropoff_house_number) {
        this.dropoff_house_number = dropoff_house_number;
    }

    public String getDropoff_street_name() {
        return dropoff_street_name;
    }

    public void setDropoff_street_name(String dropoff_street_name) {
        this.dropoff_street_name = dropoff_street_name;
    }

    public String getDropoff_district() {
        return dropoff_district;
    }

    public void setDropoff_district(String dropoff_district) {
        this.dropoff_district = dropoff_district;
    }

    public String getDropoff_unit() {
        return dropoff_unit;
    }

    public void setDropoff_unit(String dropoff_unit) {
        this.dropoff_unit = dropoff_unit;
    }

    public String getDropoff_landmark() {
        return dropoff_landmark;
    }

    public void setDropoff_landmark(String dropoff_landmark) {
        this.dropoff_landmark = dropoff_landmark;
    }

    public Double getDropoff_longitude() {
        return dropoff_longitude;
    }

    public void setDropoff_longitude(Double dropoff_longitude) {
        this.dropoff_longitude = dropoff_longitude;
    }

    public Double getDropoff_latitude() {
        return dropoff_latitude;
    }

    public void setDropoff_latitude(Double dropoff_latitude) {
        this.dropoff_latitude = dropoff_latitude;
    }

    public String getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(String attributeList) {
        this.attributeList = attributeList;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getPickup_time() {
        return pickup_time;
    }

    public void setPickup_time(String pickup_time) {
        this.pickup_time = pickup_time;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTripCancelledTime() {
        return tripCancelledTime;
    }

    public void setTripCancelledTime(String tripCancelledTime) {
        this.tripCancelledTime = tripCancelledTime;
    }

    public String getTripCreationTime() {
        return tripCreationTime;
    }

    public void setTripCreationTime(String tripCreationTime) {
        this.tripCreationTime = tripCreationTime;
    }

    public String getTripModificationTime() {
        return tripModificationTime;
    }

    public void setTripModificationTime(String tripModificationTime) {
        this.tripModificationTime = tripModificationTime;
    }

    public String getTripCompletionTime() {
        return tripCompletionTime;
    }

    public void setTripCompletionTime(String tripCompletionTime) {
        this.tripCompletionTime = tripCompletionTime;
    }

    public Integer getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(Integer tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDropoffAddress() {
        return dropoffAddress;
    }

    public void setDropoffAddress(String dropoffAddress) {
        this.dropoffAddress = dropoffAddress;
    }

    public Boolean getAlready_paid() {
        return already_paid;
    }

    public void setAlready_paid(Boolean already_paid) {
        this.already_paid = already_paid;
    }

    public Boolean getMulti_pay_allow() {
        return multi_pay_allow;
    }

    public void setMulti_pay_allow(Boolean multi_pay_allow) {
        this.multi_pay_allow = multi_pay_allow;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_description() {
        return company_description;
    }

    public void setCompany_description(String company_description) {
        this.company_description = company_description;
    }

    public String getCompany_phone_number() {
        return company_phone_number;
    }

    public void setCompany_phone_number(String company_phone_number) {
        this.company_phone_number = company_phone_number;
    }

    public String getCompany_icon() {
        return company_icon;
    }

    public void setCompany_icon(String company_icon) {
        this.company_icon = company_icon;
    }

    public String getCompany_attribute_list() {
        return company_attribute_list;
    }

    public void setCompany_attribute_list(String company_attribute_list) {
        this.company_attribute_list = company_attribute_list;
    }

    public String getCompany_dupChk_time() {
        return company_dupChk_time;
    }

    public void setCompany_dupChk_time(String company_dupChk_time) {
        this.company_dupChk_time = company_dupChk_time;
    }

    public String getCompany_car_file() {
        return company_car_file;
    }

    public void setCompany_car_file(String company_car_file) {
        this.company_car_file = company_car_file;
    }

    public Integer getCompany_baseRate() {
        return company_baseRate;
    }

    public void setCompany_baseRate(Integer company_baseRate) {
        this.company_baseRate = company_baseRate;
    }

    public Integer getCompany_rate_PerDistance() {
        return company_rate_PerDistance;
    }

    public void setCompany_rate_PerDistance(Integer company_rate_PerDistance) {
        this.company_rate_PerDistance = company_rate_PerDistance;
    }

    public Boolean getShouldForceDisableCancel() {
        return shouldForceDisableCancel;
    }

    public void setShouldForceDisableCancel(Boolean shouldForceDisableCancel) {
        this.shouldForceDisableCancel = shouldForceDisableCancel;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

}
