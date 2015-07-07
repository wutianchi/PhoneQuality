package com.cattsoft.phone.quality.utils.speed;

import android.util.Log;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Xiaohong on 2014/5/4.
 */
public abstract class NetSpeedTest {
    private final Counter counter = new Counter();
    private ExecutorService executorService;
    private SecondsTimerThread timerThread;
    /** 文件保存目录 */
    private File fileDir;
    private int poolSize = 10, seconds = 10, preheat = 5;
    private Type type;

    private boolean cancelled = false;

    public NetSpeedTest(File fileDir, int poolSize, int seconds) {
        this.fileDir = fileDir;
        this.poolSize = poolSize;
        this.seconds = seconds;
    }

    private void awaitTermination() {
        executorService.submit(timerThread);
        try {
            // 等待执行线程池关闭，增加30毫秒，细微差别促使在计时器线程执行完毕后超时
            executorService.awaitTermination(TimeUnit.SECONDS.toMillis(30) + 100, TimeUnit.MILLISECONDS);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            List<Runnable> runnables = executorService.shutdownNow();
            for (Runnable runnable : runnables) {
                if (runnable instanceof SpeedTask)
                    ((SpeedTask) runnable).interrupt();
            }
        }
    }

    private void reset(Type type) {
        this.type = type;
        counter.reset();
        try {
            if (null != executorService)
                executorService.shutdownNow();
        } catch (Throwable t) {
        }
        // 增加线程池数量，适用于计时器线程
        executorService = Executors.newFixedThreadPool(poolSize + 1);
        timerThread = new SecondsTimerThread(seconds + preheat, new ShutDownCallback());
    }

    public float ping(String host) {
        type = Type.PING;
        try {
            String result = new ProcessExecutor().command("ping", "-i", "0.8", "-c", "3", host)
                    .redirectErrorStream(true)
                    .readOutput(true).execute().outputString("UTF-8");
            if (cancelled)
                return -2;
            Matcher matcher = Pattern.compile("[\\n]rtt\\s+([\\s\\S]*)\\s+=\\s*([\\s\\S]*)\\s+\\w+$").matcher(result);
            if (matcher.find()) {
                Map<String, Float> map = new HashMap<String, Float>();
                String[] names = matcher.group(1).split("/");
                String[] values = matcher.group(2).split("/");
                for (int i = 0; i < names.length; i++)
                    map.put(names[i], Float.parseFloat(values[i]));
                return map.get("avg").floatValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long download(URL url) throws Exception {
        reset(Type.DOWNLOAD);
        File file = new File(fileDir, System.currentTimeMillis() + ".tmp");
        // 获取要下载的数据文件长度
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(3));
            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5));
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setChunkedStreamingMode(32);
            connection.setUseCaches(false);

            connection.connect();
            if (cancelled)
                throw new Exception("用户已取消");
            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                // 连接成功
                long length = connection.getContentLength();
                if (length <= 0)
                    throw new Exception("无法获取当前文件长度，无法进行下载");
                Log.d("SPEED", "待下载数据文件长度：" + (length / 1024.0));
                counter.setLength(length);
                String disposition = connection.getHeaderField("Content-Disposition");
                String fileName = file.getName();
                if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10, disposition.length() - 1);
                    }
                } else {
                    // extracts file name from URL
                    fileName = url.getPath().substring(url.getPath().lastIndexOf("/") + 1, url.getPath().toString().length());
                }
                file = new File(fileDir, fileName);
                file.delete();
                // 创建临时文件
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.setLength(length);
                randomAccessFile.close();
            } else {
                throw new Exception("当前服务器无法进行下载测试：" + connection.getResponseMessage());
            }
            connection.disconnect();
        } catch (Exception e) {
            throw new Exception("无法请求当前下载文件：" + url.toString(), e);
        }
        // 各线程下载大小分配
        long length = counter.getLength();
        long block = length % poolSize == 0 ? length / poolSize : counter.getLength() / poolSize + 1;
        for (int i = 0; i < poolSize; i++) {
            long offset = block * i; //开始位置
            long end = (i + 1 == poolSize) ? length : block * (i + 1) - 1; //结束位置
            if (length - end < block) {
                end = length;
                executorService.submit(new DownloadSpeedTask(counter, url, file, offset, end));
                System.out.println("下载分配完成，共分配" + (i + 1) + "个下载线程");
                break;
            }
            if (end < offset)
                System.out.println("线程" + i + "未分配下载");
            else
                executorService.submit(new DownloadSpeedTask(counter, url, file, offset, end));
        }
        awaitTermination();
        // 删除临时文件
        file.delete();
        return counter.getBytes();
    }

    /**
     * 上传直接往服务器写数据.
     * 不考虑多线程同时传同一个文件的问题.
     */
    public long upload(URL url) {
        reset(Type.UPLOAD);
        for (int i = 0; i < poolSize; i++)
            executorService.submit(new UploadSpeedTask(counter, url));
//        System.out.println("上传分配完成，共分配" + poolSize + "个上传线程");
        awaitTermination();
        return counter.getBytes();
    }

    /**
     * @param type
     * @param length 文件长度
     * @param bytes  已下载长度
     * @param speed  每秒速率 bytes
     */
    public abstract void handleProgress(Type type, long length, long bytes, long speed, long sequence);

    public void cancel() {
        if (type == Type.PING) {
            // 正在执行PING操作，关闭命令执行器
            // 命令执行器会在0.4秒内退出
        }
        try {
            if (null != executorService && !executorService.isShutdown()) {
                List<Runnable> runnables = executorService.shutdownNow();
                for (Runnable runnable : runnables) {
                    try {
                        ((SpeedTask) runnable).interrupt();
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum Type {
        UPLOAD(101), DOWNLOAD(102), PING(100);

        public int value;

        Type(int value) {
            this.value = value;
        }
    }

    public class ShutDownCallback extends SecondsTimerThread.Callback {
        @Override
        public void callback() {
            try {
                if (null != executorService)
                    executorService.shutdownNow();
            } catch (Exception e) {
            }
        }

        @Override
        public void progress() {
            super.progress();
            long bytes = counter.getBytes();
            long length = counter.getLength();
            long sequence = counter.incrementSequence();
            System.out.println("时间序列：" + sequence);
            // 前几秒数据不要的干活
            if (sequence > preheat)
                handleProgress(type, length, bytes, (bytes - counter.getLast()), sequence - preheat);
            counter.setLast(bytes);

            if (sequence - preheat > seconds)
                timerThread.interrupt();

//            if(counter.getLength() > 0 && bytes >= counter.getLength())
//                timerThread.interrupt();
        }
    }
}
