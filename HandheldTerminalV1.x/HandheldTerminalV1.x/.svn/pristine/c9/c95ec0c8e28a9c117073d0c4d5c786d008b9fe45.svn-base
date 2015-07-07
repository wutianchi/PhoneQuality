package com.cattsoft.phone.quality.utils.speed;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Xiaohong on 2014/5/4.
 */
public class UploadSpeedTask extends SpeedTask {
    private static final String BOUNDARY = "00content0boundary00";
    private static final String CONTENT_TYPE_MULTIPART = "multipart/form-data; boundary=" + BOUNDARY;
    private static final String CRLF = "\r\n";
    private Random random;
    private URL url;
    private HttpURLConnection connection = null;
    private OutputStream outputStream = null;
    /** 标识运行状态 */
    private boolean running;

    public UploadSpeedTask(Counter counter, URL url) {
        super(counter);
        this.url = url;
        random = new Random(System.currentTimeMillis());
        this.running = true;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            byte[] bytes = new byte[128]; // 128 byte
            random.nextBytes(bytes); // 随机数据填充
            DataOutputStream output = null;
            String fileName = "SPEED_" + System.currentTimeMillis();
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(3));
                connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5));
                // 发送POST请求必须设置如下两行
                // Allow Inputs
                connection.setDoInput(true);
                // Allow Outputs
                connection.setDoOutput(true);
                // Don't use a cached copy.
                connection.setUseCaches(false);
//                connection.setDefaultUseCaches(false);
                // Use a post method.
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
                // 设置每次传输的流大小，可有效防止因内存不足崩溃
                // 用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。适用于大数据传输
                connection.setChunkedStreamingMode(32);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("Charsert", "UTF-8");
                connection.setRequestProperty("Content-Type", CONTENT_TYPE_MULTIPART);
                // 连接到服务器
//                connection.connect();

                outputStream = connection.getOutputStream();
                output = new DataOutputStream(outputStream);

                if (!running || Thread.currentThread().isInterrupted())
                    break;

                // 发送文件上传请求头
                output.writeBytes("--" + BOUNDARY + CRLF);
                output.writeBytes("Content-Disposition: form-data; name=\"upload\";filename=\"" + fileName + "\"" + CRLF);
//                output.writeBytes("Content-Type: application/octet-stream" + CRLF);
                output.writeBytes(CRLF);
                output.flush();
                while (running && !Thread.currentThread().isInterrupted()) {
                    output.write(bytes);
                    output.flush();
                    counter.addBytes(bytes.length);
                }
                break;
            } catch (Throwable e) {
                if (!running || Thread.currentThread().isInterrupted()) {
                    // 线程退出
                    break;
                }
            } finally {
                try {
                    if (null != output)
                        output.close();
                } catch (Exception e) {
                }
                closeQuality(outputStream);
                closeQuality(connection);
            }
        }
    }

    private int writeBytes(OutputStream out, String str) throws IOException {
        byte[] bytes = new byte[str.length()];
        for (int index = 0; index < str.length(); index++)
            bytes[index] = (byte) str.charAt(index);
        out.write(bytes);
        return bytes.length;
    }

    @Override
    public void interrupt() {
        running = false;
        closeQuality(outputStream);
        closeQuality(connection);
        try {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
        }
    }
}
