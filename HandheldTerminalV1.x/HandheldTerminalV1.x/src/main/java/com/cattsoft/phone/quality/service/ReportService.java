package com.cattsoft.phone.quality.service;

import android.content.Intent;
import android.util.Log;
import com.cattsoft.commons.base.FTP;
import com.cattsoft.commons.base.FTPUtil;
import com.cattsoft.commons.digest.DigestUtils;
import com.cattsoft.phone.quality.BuildConfig;
import com.cattsoft.phone.quality.QualityApplication;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.utils.DatabaseHelper;
import com.github.kevinsawicki.http.HttpRequest;
import org.json.JSONObject;
import roboguice.inject.InjectResource;
import roboguice.service.RoboIntentService;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Xiaohong on 2014/5/12.
 */
public class ReportService extends RoboIntentService {
    public static final String TAG = "report";

    @InjectResource(R.string.report_file_matcher)
    String report_file_matcher;

    public ReportService() {
        super("report");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FTP ftp = null;
        boolean connected = false;
        File[] files = null;
        try {
            // 查找文件
            files = getFilesDir().listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.matches(report_file_matcher);
                }
            });
            if (null == files || files.length == 0) {
                // 不存在待上传的数据文件,上传请求返回
                return;
            }
            DatabaseHelper databaseHelper = QualityApplication.getApplication(this).getDatabaseHelper();
            /** FTP 上传服务器信息 */
            String ftp_mode = databaseHelper.getString("ftp_mode", "FTP");
            String ftp_server = databaseHelper.getString("ftp_server", "219.239.97.62");
            int ftp_port = databaseHelper.getInt("ftp_port", 21);
            String ftp_user = databaseHelper.getString("ftp_user", "collect");
            String ftp_pswd = databaseHelper.getString("ftp_pswd", "Bb$4.b");
            String ftp_path = databaseHelper.getString("ftp_path", "/collect/data/quality");

            ftp = FTPUtil.newInstance(ftp_server, ftp_port, ftp_user, ftp_pswd, ftp_path);
            if (BuildConfig.DEBUG)
                Log.d(TAG, "待上传报文数量：" + files.length);
            ftp.connect();
            if (ftp.isConnected() && !ftp.isLogged())
                ftp.login();
            connected = ftp.isConnected();

            for (File file : files) {
                if (!file.exists())
                    continue;
                boolean uploaded = ftp.upload(ftp.getHome(), file);
                if (uploaded)
                    file.delete();
            }
            if (BuildConfig.DEBUG)
                Log.d(TAG, "已上报数据:" + files.length);
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                Log.w(TAG, "数据报文无法上传:" + e);
        } catch (Throwable t) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "数据上传出现错误!", t);
        } finally {
            try {
                if (null != ftp) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (Exception e) {
            }
        }
        if (!connected) {
            // FTP 无法连接，使用HTTP上传
            try {
                String oauth_verifier = QualityApplication.getApplication(this).getDatabaseHelper().getString("oauth_verifier", "");
                for (File file : files) {
                    try {
                        if (!file.exists())
                            continue;

                        HttpRequest request = HttpRequest.post("http://219.239.97.62:8080/quality/reports")
                                .authorization(oauth_verifier)
                                .part("file", file.getName(), file);
                        if (request.ok()) {
                            JSONObject resp = new JSONObject(request.body());
                            if (DigestUtils.md5(file).equals(resp.getString("md5"))) {
                                file.delete();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "HTTP上传失败", e);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "无法使用HTTP上传报文", e);
            }

        }
    }
}
