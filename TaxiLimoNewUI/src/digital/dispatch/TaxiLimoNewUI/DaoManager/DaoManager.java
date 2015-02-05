package digital.dispatch.TaxiLimoNewUI.DaoManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBAttributeDao;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;
import digital.dispatch.TaxiLimoNewUI.DBCreditCardDao;
import digital.dispatch.TaxiLimoNewUI.DBPreferenceDao;
import digital.dispatch.TaxiLimoNewUI.DaoMaster;
import digital.dispatch.TaxiLimoNewUI.DaoSession;

public class DaoManager {

    public final static int TYPE_READ = 0;
    public final static int TYPE_WRITE = 1;

    private DaoMaster.DevOpenHelper devOpenHelper;

    private static DaoManager mDaoManager;

    private DaoManager(Context context) {
        devOpenHelper = new DaoMaster.DevOpenHelper(context, "TaxiLimo", null);
    }

    private SQLiteDatabase getDB(int type) {
        SQLiteDatabase db = null;
        if(type == TYPE_READ) {
            db = devOpenHelper.getReadableDatabase();
        } else {
            db = devOpenHelper.getWritableDatabase();
        }
        return db;
    }

    public DBAddressDao getAddressDao(int type) {
        SQLiteDatabase db = getDB(type);
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        DBAddressDao dbAddressDao = daoSession.getDBAddressDao();
        return dbAddressDao;
    }
    
    public DBBookingDao getDBBookingDao(int type) {
        SQLiteDatabase db = getDB(type);
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        DBBookingDao dbBookingDao = daoSession.getDBBookingDao();
        return dbBookingDao;
    }
    
    public DBAttributeDao getDBAttributeDao(int type) {
        SQLiteDatabase db = getDB(type);
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        DBAttributeDao dbAttributeDao = daoSession.getDBAttributeDao();
        return dbAttributeDao;
    }
    
    public DBCreditCardDao getDBCreditCardDao(int type) {
        SQLiteDatabase db = getDB(type);
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        DBCreditCardDao dbCreditCardDao = daoSession.getDBCreditCardDao();
        return dbCreditCardDao;
    }
    
    public DBPreferenceDao getDBPreferenceDao(int type) {
        SQLiteDatabase db = getDB(type);
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        DBPreferenceDao dbPreferenceDao = daoSession.getDBPreferenceDao();
        return dbPreferenceDao;
    }

    public static synchronized DaoManager getInstance(Context context) {
        if(mDaoManager == null) {
            mDaoManager = new DaoManager(context);
        }
        return  mDaoManager;
    }

}
