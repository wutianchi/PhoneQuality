package com.cattsoft.phone.quality.utils;

import android.content.Context;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.model.MetaData;
import com.github.kevinsawicki.http.HttpRequest;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import org.json.JSONObject;
import roboguice.util.Strings;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Xiaohong on 2014/6/11.
 */
public class OAuth {

    /**
     * @param context
     * @return
     * @throws Exception
     */
    public static String oauth(Context context) throws Exception {
        Map<String, String> request = new LinkedHashMap<String, String>();
        request.put("device", Devices.getSerial(context));

        JSONObject json = null;
        try {
            json = new JSONObject(HttpRequest.post("http://219.239.97.62:8080/quality/devices/oauth", request, false)
                    .readTimeout((int) TimeUnit.SECONDS.toMillis(3))
                    .connectTimeout((int) TimeUnit.SECONDS.toMillis(5)).body("UTF-8"));
        } catch (Exception e) {
            throw new Exception(json.getString("message"), e);
        }
        String token = json.getString("token");
        if (null == json || Strings.isEmpty(token))
            throw new Exception("认证失败，未能获取令牌信息");
        RuntimeExceptionDao<MetaData, String> dao = QualityApplication.getApplication(context).getDatabaseHelper().getMetaDataDAO();
        try {
            dao.deleteById("oauth_verifier");
        } catch (Exception e) {
        }
        try {
            dao.create(new MetaData("oauth_verifier", token));
        } catch (Exception e) {
        }
        return token;
    }
}
