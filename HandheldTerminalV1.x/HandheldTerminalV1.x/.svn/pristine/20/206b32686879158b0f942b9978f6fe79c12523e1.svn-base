package com.cattsoft.phone.quality.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.*;
import com.google.common.base.Strings;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "quality.db";
    // any time you make changes to your database objects, you may have to increase the database version
    // private static final int DATABASE_VERSION = 1;

    private final Class<?>[] classes = new Class<?>[]{
            MetaData.class, CallCauseCache.class, CallsStructure.class,
            GeoLocation.class, MobileServiceActivity.class, NetworkActivity.class,
            SmsStructure.class, SpeedTestResult.class, SystemTraffic.class,
            AppTraffic.class, WifiResult.class, SpeedTarget.class, NeighborBaseCell.class,
            BaseCell.class, SignalActivity.class, SpeedTimeLine.class, PseudoSms.class
    };

    // the DAO object we use to access the SimpleData table
    private RuntimeExceptionDao<MetaData, String> metaDataDAO = null;
    private RuntimeExceptionDao<CallCauseCache, Long> callCauseCacheDAO = null;
    private RuntimeExceptionDao<CallsStructure, Long> callsStructureDAO = null;
    private RuntimeExceptionDao<GeoLocation, Long> geoLocationDAO = null;
    private RuntimeExceptionDao<MobileServiceActivity, Long> mobileServiceActivitieDAO = null;
    private RuntimeExceptionDao<NetworkActivity, Long> networkActivitieDAO = null;
    private RuntimeExceptionDao<SmsStructure, Long> smsStructureDAO = null;
    private RuntimeExceptionDao<SpeedTestResult, Long> speedTestResultDAO = null;
    private RuntimeExceptionDao<SystemTraffic, Long> systemTrafficDAO = null;
    private RuntimeExceptionDao<AppTraffic, Long> appTrafficDAO = null;
    private RuntimeExceptionDao<WifiResult, Long> wifiResultDAO = null;
    private RuntimeExceptionDao<SpeedTarget, Long> speedTargetDAO = null;
    private RuntimeExceptionDao<BaseCell, Long> baseCellDAO = null;
    private RuntimeExceptionDao<NeighborBaseCell, Long> neighborBaseCellDAO = null;
    private RuntimeExceptionDao<SignalActivity, Long> signalActivitieDAO = null;
    private RuntimeExceptionDao<SpeedTimeLine, Long> speedTimeLineDAO = null;
    private RuntimeExceptionDao<PseudoSms, Long> pseudoSmsDAO = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, context.getResources().getInteger(R.integer.database_version), R.raw.ormlite_config);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            for (Class<?> cls : classes)
                TableUtils.createTable(connectionSource, cls);
        } catch (Exception e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            for (Class<?> cls : classes)
                TableUtils.dropTable(connectionSource, cls, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (Exception e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<MetaData, String> getMetaDataDAO() {
        return (null == metaDataDAO ? (metaDataDAO = getRuntimeExceptionDao(MetaData.class)) : metaDataDAO);
    }

    public RuntimeExceptionDao<CallCauseCache, Long> getCallCauseCacheDAO() {
        return (null == callCauseCacheDAO ? (callCauseCacheDAO = getRuntimeExceptionDao(CallCauseCache.class)) : callCauseCacheDAO);
    }

    public RuntimeExceptionDao<CallsStructure, Long> getCallsStructureDAO() {
        return (null == callsStructureDAO ? (callsStructureDAO = getRuntimeExceptionDao(CallsStructure.class)) : callsStructureDAO);
    }

    public RuntimeExceptionDao<GeoLocation, Long> getGeoLocationDAO() {
        return (null == geoLocationDAO ? (geoLocationDAO = getRuntimeExceptionDao(GeoLocation.class)) : geoLocationDAO);
    }

    public RuntimeExceptionDao<MobileServiceActivity, Long> getMobileServiceActivitieDAO() {
        return (null == mobileServiceActivitieDAO ? (mobileServiceActivitieDAO = getRuntimeExceptionDao(MobileServiceActivity.class)) : mobileServiceActivitieDAO);
    }

    public RuntimeExceptionDao<NetworkActivity, Long> getNetworkActivitieDAO() {
        return (null == networkActivitieDAO ? (networkActivitieDAO = getRuntimeExceptionDao(NetworkActivity.class)) : networkActivitieDAO);
    }

    public RuntimeExceptionDao<SmsStructure, Long> getSmsStructureDAO() {
        return (null == smsStructureDAO ? (smsStructureDAO = getRuntimeExceptionDao(SmsStructure.class)) : smsStructureDAO);
    }

    public RuntimeExceptionDao<SpeedTestResult, Long> getSpeedTestResultDAO() {
        return (null == speedTestResultDAO ? (speedTestResultDAO = getRuntimeExceptionDao(SpeedTestResult.class)) : speedTestResultDAO);
    }

    public RuntimeExceptionDao<SystemTraffic, Long> getSystemTrafficDAO() {
        return (null == systemTrafficDAO ? (systemTrafficDAO = getRuntimeExceptionDao(SystemTraffic.class)) : systemTrafficDAO);
    }

    public RuntimeExceptionDao<AppTraffic, Long> getAppTrafficDAO() {
        return (null == appTrafficDAO ? (appTrafficDAO = getRuntimeExceptionDao(AppTraffic.class)) : appTrafficDAO);
    }

    public RuntimeExceptionDao<WifiResult, Long> getWifiResultDAO() {
        return (null == wifiResultDAO ? (wifiResultDAO = getRuntimeExceptionDao(WifiResult.class)) : wifiResultDAO);
    }

    public RuntimeExceptionDao<SpeedTarget, Long> getSpeedTargetDAO() {
        return (null == speedTargetDAO ? (speedTargetDAO = getRuntimeExceptionDao(SpeedTarget.class)) : speedTargetDAO);
    }

    public RuntimeExceptionDao<BaseCell, Long> getBaseCellDAO() {
        return (null == baseCellDAO ? (baseCellDAO = getRuntimeExceptionDao(BaseCell.class)) : baseCellDAO);
    }

    public RuntimeExceptionDao<NeighborBaseCell, Long> getNeighborBaseCellDAO() {
        return (null == neighborBaseCellDAO ? (neighborBaseCellDAO = getRuntimeExceptionDao(NeighborBaseCell.class)) : neighborBaseCellDAO);
    }

    public RuntimeExceptionDao<SignalActivity, Long> getSignalActivitieDAO() {
        return (null == signalActivitieDAO ? (signalActivitieDAO = getRuntimeExceptionDao(SignalActivity.class)) : signalActivitieDAO);
    }

    public RuntimeExceptionDao<SpeedTimeLine, Long> getSpeedTimeLineDAO() {
        return (null == speedTimeLineDAO ? (speedTimeLineDAO = getRuntimeExceptionDao(SpeedTimeLine.class)) : speedTimeLineDAO);
    }

    public RuntimeExceptionDao<PseudoSms, Long> getPseudoSmsDAO() {
        return (null == pseudoSmsDAO ? (pseudoSmsDAO = getRuntimeExceptionDao(PseudoSms.class)) : pseudoSmsDAO);
    }

    public int getInt(String key, int def) {
        try {
            String v = getString(key, null);
            return Strings.isNullOrEmpty(v) ? def : Integer.parseInt(v);
        } catch (Exception e) {
        }
        return def;
    }

    public String getString(String key, String def) {
        String val = def;
        try {
            String value = getMetaDataDAO().queryForId(key).getValue();
            if (!Strings.isNullOrEmpty(value))
                val = value;
        } catch (Exception e) {
        }
        return val;
    }

    public GeoLocation getLastLocation() {
        try {
            return getGeoLocationDAO().queryForFirst(getGeoLocationDAO().queryBuilder().orderBy("ddate", false).prepare());
        } catch (Exception e) {
            //
        }
        return null;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        metaDataDAO = null;
        callCauseCacheDAO = null;
        callsStructureDAO = null;
        geoLocationDAO = null;
        mobileServiceActivitieDAO = null;
        networkActivitieDAO = null;
        smsStructureDAO = null;
        speedTestResultDAO = null;
        systemTrafficDAO = null;
        appTrafficDAO = null;
        wifiResultDAO = null;
        speedTargetDAO = null;
        baseCellDAO = null;
        neighborBaseCellDAO = null;
        signalActivitieDAO = null;
        speedTimeLineDAO = null;
        pseudoSmsDAO = null;
    }
}
