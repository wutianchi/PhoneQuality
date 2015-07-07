package com.cattsoft.phone.quality.task;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.model.MetaData;
import com.cattsoft.phone.quality.model.SpeedTimeLine;
import com.cattsoft.phone.quality.utils.OAuth;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Strings;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Xiaohong on 14-3-5.
 */
public class SpeedTimeLineTask extends BaseAsyncTask<SpeedTimeLine[]> {

    String token = null;

    public SpeedTimeLineTask(Context context, Handler handler) {
        super(context, handler);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            token = QualityApplication.getApplication(context).getDatabaseHelper().getString("oauth_verifier", null);
            if (Strings.isNullOrEmpty(token)) {
                try {
                    token = OAuth.oauth(getContext());
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SpeedTimeLine[] call() throws Exception {
        RuntimeExceptionDao<MetaData, String> dao = QualityApplication.getApplication(context).getDatabaseHelper().getMetaDataDAO();
        if (Strings.isNullOrEmpty(token))
            return null;
        MetaData metaData = dao.queryForId("speed_share_timeline");
        long ddate = null != metaData ? Long.parseLong(metaData.getValue()) : DateTime.now().minusDays(1).getMillis();
        try {
            HttpRequest request = HttpRequest.get(context.getString(R.string.speed_timeline_url, ddate))
                    .authorization(token)
                    .acceptJson()
                    .connectTimeout(1000 * 10)
                    .readTimeout(1000 * 10);
            if (request.ok()) {
                JSONObject json = new JSONObject(request.body());
                try {
                    if (null != metaData) {
                        metaData.setValue(Long.toString(DateTime.parse(json.getString("timeline")).getMillis()));
                        dao.update(metaData);
                    } else {
                        dao.create(new MetaData("speed_share_timeline", Long.toString(DateTime.now().getMillis())));
                    }
                } catch (Exception e) {
                }
                // 解析数据
                JSONArray jsonArray = json.getJSONArray("data");
                SpeedTimeLine[] lines = new SpeedTimeLine[jsonArray.length()];
                if (jsonArray.length() > 0) {
                    RuntimeExceptionDao<SpeedTimeLine, Long> speedTimeLineDAO = QualityApplication.getApplication(context).getDatabaseHelper().getSpeedTimeLineDAO();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        // String server, int netType, float ping, long download, long upload, DateTime ddate, DateTime sdate
                        lines[i] = new SpeedTimeLine(obj.getString("server"),
                                obj.getInt("netType"),
                                Float.parseFloat(obj.getString("ping")), obj.getLong("download"), obj.getLong("upload"),
                                new DateTime(obj.getLong("ddate")),
                                new DateTime(obj.getLong("sdate")));
                        try {
                            if (!Strings.isNullOrEmpty(obj.getString("operator")))
                                lines[i].setOperator(obj.getInt("operator"));
                        } catch (Exception e) {
                        }

                        try {
                            if (!Strings.isNullOrEmpty(obj.getString("operatorName")))
                                lines[i].setOperatorName(obj.getString("operatorName"));
                        } catch (Exception e) {
                        }
                        if (null != obj.get("address"))
                            lines[i].setAddress(obj.getString("address"));
                        try {
                            speedTimeLineDAO.create(lines[i]);
                        } catch (Exception e) {
                            Log.e("speed", "无法保存在线数据", e);
                        }
                    }
                }
                return lines;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onSuccess(SpeedTimeLine[] o) throws Exception {
        super.onSuccess(o);
        handler.obtainMessage(null != o ? 1 : 0, o).sendToTarget();
    }

    @Override
    protected void onThrowable(Throwable t) throws RuntimeException {
        super.onThrowable(t);
        handler.obtainMessage(0, t).sendToTarget();
    }
}
