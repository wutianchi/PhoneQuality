package com.cattsoft.phone.quality.task;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.GeoLocation;
import com.cattsoft.phone.quality.model.SpeedTestResult;
import com.cattsoft.phone.quality.utils.Devices;
import com.cattsoft.phone.quality.utils.OAuth;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Strings;
import org.json.JSONObject;

/**
 * 测速记录数据共享
 * Created by Xiaohong on 14-3-5.
 */
public class SpeedShareTask extends BaseAsyncTask<JSONObject> {

    SpeedTestResult record;
    String token = null;

    public SpeedShareTask(Context context, Handler handler, SpeedTestResult record) {
        super(context, handler);
        this.record = record;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            Toast.makeText(context, "正在分享数据到网络..", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
        try {
            token = QualityApplication.getApplication(context).getDatabaseHelper().getString("oauth_verifier", null);
            if (Strings.isNullOrEmpty(token)) {
                try {
                    token = OAuth.oauth(getContext());
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public JSONObject call() throws Exception {
        JSONObject json = new JSONObject();
        json.put("userId", 1001);
        json.put("server", record.getServer());
        json.put("netType", record.getNetworkType());
        json.put("ping", record.getPing());
        json.put("download", record.getDownload());
        json.put("upload", record.getUpload());
        json.put("mcc", Devices.getMnc(context));
        json.put("ddate", record.getDdate());
        json.put("operator", record.getOperator());
        json.put("operatorName", record.getOperatorName());
        json.put("type", record.getType());

        GeoLocation location = record.getLocation();
        if (null == location)
            location = QualityApplication.getApplication(context).getDatabaseHelper().getLastLocation();

        // json.put("address", location.getProvince() + "," + location.getCity() + "," + location.getDistrict());
        json.put("address", null != location ? location.getAddress() : "");
        if (Strings.isNullOrEmpty(token))
            return null;
        HttpRequest request = HttpRequest.post(context.getString(R.string.speed_share_url))
                .contentType("application/json")
                .authorization(token)
                .acceptJson()
                .connectTimeout(1000 * 10)
                .readTimeout(1000 * 10)
                .send(json.toString());
        if (request.ok()) {
            JSONObject resp = new JSONObject(request.body());
            try {
                if (resp.getBoolean("success")) {
                    record.setShared(true);
                    QualityApplication.getApplication(context).getDatabaseHelper().getSpeedTestResultDAO().update(record);
                }
            } catch (Exception e) {
                Log.e("share", "测速数据分享状态更新失败：" + e);
            }
            return resp;
        } else {
            Log.e("speedShare", "网络请求失败!:" + request.body());
        }
        return null;
    }

    @Override
    protected void onSuccess(JSONObject o) throws Exception {
        super.onSuccess(o);
        if (null != o) {
            try {
                if (o.getBoolean("success")) {
                    Toast.makeText(context, "测速记录已成功分享到网络。", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            }
        }
        if (null != handler)
            handler.obtainMessage(null != o ? 1 : 0, o).sendToTarget();
    }

    @Override
    protected void onThrowable(Throwable t) throws RuntimeException {
        super.onThrowable(t);
        try {
            QualityApplication.runInUi(new QualityApplication.Callback() {
                @Override
                public void call() {
                    Toast.makeText(context, "测速记录分享失败!请稍后重试。", Toast.LENGTH_SHORT).show();
                }
            }, context);
        } catch (Exception e) {
        }
        if (null != handler)
            handler.obtainMessage(0).sendToTarget();
    }
}
