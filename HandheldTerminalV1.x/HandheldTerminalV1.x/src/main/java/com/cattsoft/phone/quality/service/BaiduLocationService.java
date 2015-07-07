package com.cattsoft.phone.quality.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.GeoLocation;
import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import roboguice.service.RoboService;

import java.util.concurrent.TimeUnit;

/**
 * Created by Xiaohong on 14-3-18.
 */
public class BaiduLocationService extends RoboService implements BDLocationListener {
    public static final String TAG = "location";
    public static final String LOCATION_ACTION = "com.cattsoft.phone.LOCATION";

    public static final String LOCATED_TIME_KEY = "located_time";
    @Inject
    SharedPreferences sharedPreferences;

    private LocationClient locationClient;
    private LocationClientOption option;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            Intent intent = new Intent(LOCATION_ACTION);
            intent.putExtra("state", 1);
            sendBroadcast(intent);

            stopSelf();
            return true;
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        locationClient = new LocationClient(getApplicationContext());
        // 访问Key为4.1版本，在百度位置服务页面申请
//        locationClient.setAK("K92VswFp6le5MHfmGGxqRRuZ");
        locationClient.setAccessKey("K92VswFp6le5MHfmGGxqRRuZ");
        // 参数设置
        option = new LocationClientOption();
//        option.setOpenGps(true);    // 是否打开gps，使用gps前提是用户硬件打开gps。默认不打开gps
        option.setAddrType("all");  // 是否要返回地址信息，默认为无地址信息。
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(0);//设置发起定位请求的间隔时间
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (intent.getBooleanExtra("local", false)) {
                // 读取最近的一次定位信息
                try {
                    RuntimeExceptionDao<GeoLocation, Long> dao = QualityApplication.getApplication(getApplicationContext()).getDatabaseHelper().getGeoLocationDAO();
                    GeoLocation geoLocation = dao.queryForFirst(dao.queryBuilder().orderBy("ddate", false).prepare());
                    if (null != geoLocation) {
                        // 发送广播
                        Intent i = new Intent(LOCATION_ACTION);
                        i.putExtra("address", geoLocation.getAddress());
                        i.putExtra("latitude", geoLocation.getLatitude());
                        i.putExtra("longitude", geoLocation.getLongitude());
                        i.putExtra("date", geoLocation.getDdate().toString("MM-dd HH:mm:ss"));
                        sendBroadcast(i);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "无法读取最近的一次定位信息", e);
                }
            }
            if (!locationClient.isStarted()) {
                locationClient.setLocOption(option);
                locationClient.start();
                locationClient.requestLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    private void handleLocation(BDLocation location) {
        if (null == location)
            return;
        // 定位类型
        int type = location.getLocType();
        if (type == BDLocation.TypeNetWorkLocation ||
                type == BDLocation.TypeGpsLocation) {
            // 当定位结果为网络或GPS时处理位置信息
//            Log.d(TAG, "位置:" + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAddrStr());

            Intent intent = new Intent(LOCATION_ACTION);
            intent.putExtra("address", location.getAddrStr());
            intent.putExtra("latitude", location.getLatitude());
            intent.putExtra("longitude", location.getLongitude());
            intent.putExtra("date", location.getTime());
            sendBroadcast(intent);


            GeoLocation geoLocation = null;
            try {
                RuntimeExceptionDao<GeoLocation, Long> dao = QualityApplication.getApplication(getApplicationContext()).getDatabaseHelper().getGeoLocationDAO();
                // 查询定位信息
                geoLocation = dao.queryForFirst(dao.queryBuilder().where().eq("longitude", location.getLongitude()).and().eq("latitude", location.getLatitude()).prepare());
            } catch (Exception e) {
                Log.w(TAG, "无法查询当前位置是否已存在", e);
            }
            try {
                if (null == geoLocation) {
                    geoLocation = new GeoLocation(type, location.getLongitude(), location.getLatitude(),
                            DateTime.parse(location.getTime(), DateTimeFormat.forPattern("yyyy-MM-dd HH:m:s")));
                    QualityApplication.getApplication(getApplicationContext()).getDatabaseHelper().getGeoLocationDAO().create(geoLocation);
                }
            } catch (Exception e) {
                Log.w(TAG, "目前无法保存位置信息", e);
            }
            geoLocation.setLocType(location.getLocType());
            // 定位精度，单位：m
            if (location.hasRadius())
                geoLocation.setRadius(location.getRadius());
            if (type == BDLocation.TypeNetWorkLocation) {
                geoLocation.setOperators(location.getOperators());
                geoLocation.setNetworkLocationType(location.getNetworkLocationType());
                // 类型为网络定位时才有省市街道等信息
                geoLocation.setAddr(location.hasAddr());
                if (location.hasAddr()) {
                    geoLocation.setAddress(location.getAddrStr());
                    geoLocation.setProvince(location.getProvince());
                    geoLocation.setCity(location.getCity());
                    geoLocation.setCityCode(location.getCityCode());
                    geoLocation.setDistrict(location.getDistrict());
                    geoLocation.setStreet(location.getStreet());
                    geoLocation.setStreetNumber(location.getStreetNumber());
                }
            } else {
                // GPS 定位信息
                if (location.hasSateNumber())
                    geoLocation.setSatelliteNumber(location.getSatelliteNumber());
                if (location.hasSpeed()) {
                    geoLocation.setConSpeed(location.hasSpeed());
                    geoLocation.setSpeed(location.getSpeed());
                }
            }
            try {
                QualityApplication.getApplication(getApplicationContext()).getDatabaseHelper().getGeoLocationDAO().update(geoLocation);
                sharedPreferences.edit().putLong(LOCATED_TIME_KEY, DateTime.now().getMillis()).apply();
            } catch (Exception e) {
                Log.d(TAG, "位置信息更新失败", e);
            }
            // 停止
            stopSelf();
        } else {
            if (type == BDLocation.TypeOffLineLocation) {
                // 离线定位结果
                // Log.d(TAG, "离线定位结果");
            } else {
                Log.w(TAG, "无法获取当前位置：" + type);
            }
            handler.sendEmptyMessageDelayed(1, TimeUnit.SECONDS.toMillis(15));
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        try {
            handleLocation(location);
        } catch (Exception e) {
            Log.e(TAG, "无法处理位置信息", e);
            stopSelf();
        }
    }

    @Override
    public void onReceivePoi(BDLocation bdLocation) {

    }

    @Override
    public void onDestroy() {
        try {
            locationClient.stop();
            locationClient.unRegisterLocationListener(this);
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
