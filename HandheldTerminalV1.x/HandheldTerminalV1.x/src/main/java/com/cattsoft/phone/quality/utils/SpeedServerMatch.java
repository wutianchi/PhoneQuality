package com.cattsoft.phone.quality.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.github.kevinsawicki.http.HttpRequest;
import org.json.JSONObject;
import roboguice.util.RoboAsyncTask;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.cattsoft.phone.quality.utils.LogUtils.LOGD;
import static com.cattsoft.phone.quality.utils.LogUtils.makeLogTag;

/**
 * 测速服务器自动匹配。
 * Created by Xiaohong on 2015/4/10.
 */
public class SpeedServerMatch extends RoboAsyncTask<JSONObject> {
    public static final String TAG = makeLogTag(SpeedServerMatch.class);

//    private ProgressDialog dialog;

    public SpeedServerMatch(Context context, Handler handler) {
        super(context, handler);
    }

    @Override
    protected void onPreExecute() throws Exception {
        super.onPreExecute();
//        dialog = new ProgressDialog(getContext(), "正在匹配服务器");
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
    }

    @Override
    public JSONObject call() throws Exception {
        // 原服务器
        SpeedServer speedServer = PrefUtils.getSpeedServer(context);
        int code = 1;
        try {
            final LastLocation location = PrefUtils.getLastLocation(context);
            final int[] cells = PrefUtils.getLastBaseCellInfo(context);
            HttpRequest request = HttpRequest.post("http://124.193.141.197:9000/speed/interface/mobilematch.do", new HashMap<Object, Object>() {{
                put("ip", Devices.getRealIp());
                put("network", MobileNetType.getMobileType(context).type);
//                put("mobileoperid", "46001");// TEST 平板测试
                put("mobileoperid", Devices.getOperatorCode(context));
                put("province", Strings.nullToEmpty(location.getProvince()));
                put("city", Strings.nullToEmpty(location.getCity()));
                put("district", Strings.nullToEmpty(location.getDistrict()));
                put("latitude", location.getLatitude());
                put("longitude", location.getLongitude());
                put("lac", cells[0]);
                put("cid", cells[1]);
            }}, true)
                    .connectTimeout((int) TimeUnit.SECONDS.toMillis(5))
                    .readTimeout((int) TimeUnit.SECONDS.toMillis(5))
                    .acceptJson();
            if(request.ok()) {
                JSONObject response = new JSONObject(request.body());
                // 组建服务器信息
                speedServer = new SpeedServer(response.getInt("hostid"), response.getString("city"),
                        response.getString("hostname"), response.getString("hostip"), response.getInt("port"));
                speedServer.setRegion(response.getString("pname"));

                // 自动选择就近服务器
                PrefUtils.markSpeedServerAutoSelect(context);
                PrefUtils.saveSpeedServer(context, speedServer);
                code = 0;
            }
        } finally {
            // 返回服务器信息
            Message msg = new Message();
            msg.what = code;
            msg.obj = speedServer;
            handler.sendMessage(msg);
        }
        return null;
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);
        LOGD(TAG, "测速服务器匹配失败", e);
    }

    @Override
    protected void onFinally() throws RuntimeException {
        super.onFinally();
//        dialog.dismiss();
    }
}
