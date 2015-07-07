package com.cattsoft.phone.quality.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import com.cattsoft.phone.quality.BuildConfig;
import com.github.kevinsawicki.http.HttpRequest;
import roboguice.util.RoboAsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * Created by Xiaohong on 13-10-29.
 */
public class GeoImageTask extends RoboAsyncTask<Bitmap> {
    public static final String TAG = "PQ_GEOIMAGE_TASK";

    private String url;
    private Map<String, Object> params;
    private ImageView imageView;

    private String cache = null;

    public GeoImageTask(Context context, Handler handler, String url, Map<String, Object> params, ImageView imageView) {
        super(context, handler);
        this.url = url;
        this.params = params;
        this.imageView = imageView;

        this.cache = ((String) params.get("center")) + ".png";
    }

    @Override
    public Bitmap call() throws Exception {
        File cacheFile = new File(context.getCacheDir(), cache);
        if (cacheFile.exists()) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "存在该位置图片缓存");
            // 存在缓存图片
            return BitmapFactory.decodeFile(cacheFile.getPath());
        }
        if (null != params.get("online")) {
            throw new RuntimeException("未在线获取位置图片");
        }
        if (BuildConfig.DEBUG)
            Log.d(TAG, "正在读取静态地图图片,URL:" + url);
        byte[] data = HttpRequest.get(url, params, false).connectTimeout(1000 * 10).readTimeout(1000 * 10).bytes();
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    @Override
    protected void onSuccess(Bitmap bitmap) throws Exception {
        super.onSuccess(bitmap);
        imageView.setImageBitmap(bitmap);
        if (null != handler)
            handler.obtainMessage(0, "已设置").sendToTarget();
        File imgFile = new File(context.getCacheDir(), ((String) params.get("center")) + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imgFile, false);
            // 压缩 PNG 格式
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "保存缓存图片失败!", e);
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    protected void onThrowable(Throwable t) throws RuntimeException {
        super.onThrowable(t);
        if (null != handler)
            handler.obtainMessage(-1, "读取图片失败,请检查网络连接").sendToTarget();
        if (BuildConfig.DEBUG)
            Log.e(TAG, "读取图片失败");
    }
}
