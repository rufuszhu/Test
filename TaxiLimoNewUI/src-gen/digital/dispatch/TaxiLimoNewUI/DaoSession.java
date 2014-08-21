package digital.dispatch.TaxiLimoNewUI;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import digital.dispatch.TaxiLimoNewUI.DBAddress;
import digital.dispatch.TaxiLimoNewUI.DBBooking;

import digital.dispatch.TaxiLimoNewUI.DBAddressDao;
import digital.dispatch.TaxiLimoNewUI.DBBookingDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig dBAddressDaoConfig;
    private final DaoConfig dBBookingDaoConfig;

    private final DBAddressDao dBAddressDao;
    private final DBBookingDao dBBookingDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        dBAddressDaoConfig = daoConfigMap.get(DBAddressDao.class).clone();
        dBAddressDaoConfig.initIdentityScope(type);

        dBBookingDaoConfig = daoConfigMap.get(DBBookingDao.class).clone();
        dBBookingDaoConfig.initIdentityScope(type);

        dBAddressDao = new DBAddressDao(dBAddressDaoConfig, this);
        dBBookingDao = new DBBookingDao(dBBookingDaoConfig, this);

        registerDao(DBAddress.class, dBAddressDao);
        registerDao(DBBooking.class, dBBookingDao);
    }
    
    public void clear() {
        dBAddressDaoConfig.getIdentityScope().clear();
        dBBookingDaoConfig.getIdentityScope().clear();
    }

    public DBAddressDao getDBAddressDao() {
        return dBAddressDao;
    }

    public DBBookingDao getDBBookingDao() {
        return dBBookingDao;
    }

}
