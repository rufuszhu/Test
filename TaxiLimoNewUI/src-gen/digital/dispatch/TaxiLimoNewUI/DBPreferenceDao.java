package digital.dispatch.TaxiLimoNewUI;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import digital.dispatch.TaxiLimoNewUI.DBPreference;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DBPREFERENCE.
*/
public class DBPreferenceDao extends AbstractDao<DBPreference, Long> {

    public static final String TABLENAME = "DBPREFERENCE";

    /**
     * Properties of entity DBPreference.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Country = new Property(1, String.class, "country", false, "COUNTRY");
        public final static Property City = new Property(2, String.class, "city", false, "CITY");
        public final static Property Region = new Property(3, String.class, "region", false, "REGION");
        public final static Property State = new Property(4, String.class, "state", false, "STATE");
        public final static Property DestId = new Property(5, String.class, "destId", false, "DEST_ID");
        public final static Property CompanyName = new Property(6, String.class, "companyName", false, "COMPANY_NAME");
        public final static Property Img = new Property(7, String.class, "img", false, "IMG");
    };


    public DBPreferenceDao(DaoConfig config) {
        super(config);
    }
    
    public DBPreferenceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DBPREFERENCE' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'COUNTRY' TEXT," + // 1: country
                "'CITY' TEXT," + // 2: city
                "'REGION' TEXT," + // 3: region
                "'STATE' TEXT," + // 4: state
                "'DEST_ID' TEXT," + // 5: destId
                "'COMPANY_NAME' TEXT," + // 6: companyName
                "'IMG' TEXT);"); // 7: img
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DBPREFERENCE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DBPreference entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String country = entity.getCountry();
        if (country != null) {
            stmt.bindString(2, country);
        }
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(3, city);
        }
 
        String region = entity.getRegion();
        if (region != null) {
            stmt.bindString(4, region);
        }
 
        String state = entity.getState();
        if (state != null) {
            stmt.bindString(5, state);
        }
 
        String destId = entity.getDestId();
        if (destId != null) {
            stmt.bindString(6, destId);
        }
 
        String companyName = entity.getCompanyName();
        if (companyName != null) {
            stmt.bindString(7, companyName);
        }
 
        String img = entity.getImg();
        if (img != null) {
            stmt.bindString(8, img);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public DBPreference readEntity(Cursor cursor, int offset) {
        DBPreference entity = new DBPreference( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // country
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // city
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // region
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // state
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // destId
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // companyName
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // img
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DBPreference entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCountry(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCity(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRegion(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setState(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDestId(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCompanyName(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setImg(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DBPreference entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DBPreference entity) {
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