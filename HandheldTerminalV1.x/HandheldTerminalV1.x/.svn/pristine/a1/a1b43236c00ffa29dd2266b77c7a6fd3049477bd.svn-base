package com.cattsoft.phone.quality.utils.speed;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by Xiaohong on 2014/5/4.
 */
public class DownloadSpeedTask extends SpeedTask {
    private static final int BUFFER_SIZE = 8192;
    /** 下载起始偏移量 */
    private long offset;
    private long completeOffset;
    /** 下载长度 */
    private long length;
    /** 下载URL */
    private URL url;
    /** 下载数据保存 */
    private File file;
    /** 标识运行状态 */
    private boolean running;

    private HttpURLConnection connection = null;
    private InputStream inputStream = null;

    public DownloadSpeedTask(Counter counter, URL url, File file, long offset, long length) {
        super(counter);
        this.url = url;
        this.file = file;
        this.offset = offset;
        this.length = length;
        this.running = true;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            RandomAccessFile threadfile = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Accept-Encoding", "identity");
//                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(3));
                connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(3));
                connection.setInstanceFollowRedirects(true);
//                connection.setChunkedStreamingMode(32);
                // 设置范围，格式为Range：bytes x-y;
                connection.setRequestProperty("Range", "bytes=" + (offset + completeOffset) + "-" + length);

                connection.connect();
                if (!running || Thread.currentThread().isInterrupted())
                    break;
                int responseCode = connection.getResponseCode();

                int bytesRead = 0;
                long readed = bytesRead;
                // 检查连接状态
                if (responseCode == HttpURLConnection.HTTP_OK ||
                        responseCode == HttpURLConnection.HTTP_PARTIAL) {
//                    threadfile = new RandomAccessFile(file, "rwd");
//                    threadfile.seek(offset + completeOffset);

                    // 读取写入数据
                    inputStream = connection.getInputStream();

                    byte[] buffer = new byte[BUFFER_SIZE];
                    while (running && !Thread.currentThread().isInterrupted() && (bytesRead = inputStream.read(buffer)) != -1) {
//                        threadfile.write(buffer, 0, bytesRead);
                        counter.addBytes(bytesRead);
                        readed += bytesRead;
                    }
                    // 下载完成
//                    break;
                } else {
                    // 重新连接下载
//                    completeOffset += readed;
                    continue;
                }
            } catch (Throwable t) {
                if (!running || Thread.currentThread().isInterrupted()) {
                    // 线程退出
                    break;
                }
            } finally {
                try {
                    if (null != inputStream)
                        inputStream.close();
                } catch (Exception e) {
                }
                try {
                    if (null != threadfile)
                        threadfile.close();
                } catch (Exception e) {
                }
                closeQuality(connection);
            }
        }
    }

    @Override
    public void interrupt() {
        System.out.println("中断");
        running = false;
        closeQuality(inputStream);
        closeQuality(connection);
        try {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
        }
    }
}
