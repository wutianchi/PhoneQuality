package com.cattsoft.phone.quality.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.task.common.SpeedRecord;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yushiwei on 15-5-20.
 */
public abstract class FileDownloadTask extends AsyncTask<Void, FileDownloadTask.Info, FileDownloadTask.State> {

    public static final String TAG = FileDownloadTask.class.getSimpleName();

    public static final long LAST_RECORD = Long.MAX_VALUE;

    public static final String INDEX = "index";

    public static final String PROGRESS = "progress";

    private final Context context;

    private final int bufferSize = 1024;

    private final long recordCycle = 100;

    private long refreshCycle;

    private long maxTime;

    private float maxSpeed;

    private int readTimeout;

    private int connTimeout;

    private long lastRefresh;

    private String url;

    private State state;

    private int blockCount;

    private ExecutorService executorService;

    private long startTime;

    private long currRefreshTime;

    private int currSpeedRange;

    private int avgSpeedRange;

    public FileDownloadTask(Context context, String url) {
        this.context = context;
        this.url = url;

        this.readTimeout = getInteger(R.integer.filedownload_read_timeout);
        this.connTimeout = getInteger(R.integer.filedownload_conn_timeout);
        this.blockCount = getInteger(R.integer.filedownload_block_count);
        this.maxTime = getInteger(R.integer.filedownload_max_test_time);
        this.refreshCycle = getInteger(R.integer.filedownload_show_cycle);
        this.currSpeedRange = getInteger(R.integer.filedownload_currspeed_range);
        this.avgSpeedRange = getInteger(R.integer.filedownload_avgspeed_range);

        this.executorService = Executors.newFixedThreadPool(blockCount + 1);
    }

    public abstract void onProgressUpdate(Info... values);

    @Override
    public State doInBackground(Void... params) {
        try {
            int length = getLength();

            List<DownloadCallable> tasks = initTasks(length);

            List<Future<SpeedRecord>> results = startTask(tasks);

            startTime = System.currentTimeMillis();
            lastRefresh = System.currentTimeMillis();

            Thread.sleep(500);

            while (!isDone(results)) {
                publish(length, tasks);
            }
            // 最后要再SHOW一次,让进度条填满100%
            showProgress(tasks, length);
            waitTasksStop(tasks, results);
        } catch (IOException e) {
            Log.e(TAG, "", e);
            onError(e);
        } catch (InterruptedException e) {
            Log.e(TAG, "", e);
            onError(e);
        } finally {
            close(executorService);
        }

        end();
        return state;
    }

    private void publish(int length, List<DownloadCallable> tasks) throws InterruptedException {
        Thread.sleep(refreshCycle);
        before();
        showCurrSpeed(tasks);
        showAvgSpeed(tasks);
        showMaxSpeed(tasks);
        showUsedTime();
        showDownloaded(tasks);
        showProgress(tasks, length);
        showError(tasks);
        after();
    }

    private void end() {
        publishProgress(new Info<State>(Type.END, state));
    }

    private void showError(List<DownloadCallable> tasks) {
        for (DownloadCallable task : tasks) {
            if (task.getState() == State.ERROR) {
                publishProgress(new Info<Integer>(Type.BLOCK_ERROR, task.getConfig().index));
            }
        }
    }

    private void showProgress(List<DownloadCallable> tasks, int length) {
        List<Map<String, ?>> data = Lists.newArrayList();
        for (DownloadCallable task : tasks) {
            long[] record = task.getSpeedRecord().getLast();
            Map<String, Object> map = Maps.newHashMap();
            map.put(INDEX, task.getConfig().index);
            map.put(PROGRESS, (record[1] + 0.0F) / task.getConfig().length);
            data.add(map);
        }
        publishProgress(new Info<List<Map<String, ?>>>(Type.BLOCK, data));
    }

    private void waitTasksStop(List<DownloadCallable> tasks, List<Future<SpeedRecord>> results) {
        for (int i = 0; i < tasks.size(); i++) {
            DownloadCallable dc = tasks.get(i);
            Future<SpeedRecord> sr = results.get(i);
            dc.cancel();
            try {
                sr.get(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                sr.cancel(true);
            } catch (ExecutionException e) {
                sr.cancel(true);
            } catch (TimeoutException e) {
                sr.cancel(true);
            }
        }
        state = State.SUCCEED;
    }

    private void before() {
        currRefreshTime = System.currentTimeMillis();
    }

    private void showUsedTime() {
        publishProgress(new Info<Long>(Type.USED_TIME, currRefreshTime - startTime));
    }

    private void showDownloaded(List<DownloadCallable> tasks) {
        long[] data = getRecord(tasks, LAST_RECORD);
        publishProgress(new Info<Long>(Type.DOWNLOADED, data[1]));
    }

    private void showAvgSpeed(List<DownloadCallable> tasks) {
        long[] endRecord = getRecord(tasks, LAST_RECORD);
        long[] startRecord = getRecord(tasks, currRefreshTime - startTime - avgSpeedRange);
        float avg = countSpeed(endRecord, startRecord);
        publishProgress(new Info<Float>(Type.AVG, avg));
    }

    private float countSpeed(long[] endRecord, long[] startRecord) {
        Double speed = 0D;
        if (endRecord[0] - startRecord[0] > 0)
            speed = ((endRecord[1] - startRecord[1]) / 1024D) / ((endRecord[0] - startRecord[0]) / 1000D) * blockCount;
        return speed.floatValue();
    }

    private void showMaxSpeed(List<DownloadCallable> tasks) {
        float currSpeed = showCurrSpeed(tasks);
        maxSpeed = maxSpeed < currSpeed ? currSpeed : maxSpeed;
        publishProgress(new Info<Float>(Type.MAX, maxSpeed));
    }

    private float showCurrSpeed(List<DownloadCallable> tasks) {
        long[] endRecord = getRecord(tasks, LAST_RECORD);
        long[] startRecord = getRecord(tasks, currRefreshTime - startTime - currSpeedRange);
        float currSpeed = countSpeed(endRecord, startRecord);
        publishProgress(new Info<Float>(Type.CURR, currSpeed));
        return currSpeed;
    }

    private void after() {
        lastRefresh = currRefreshTime;
    }

    private long[] getRecord(List<DownloadCallable> tasks, long time) {
        if (time < 0) {
            return new long[]{0, 0};
        }

        long totalTransfered = 0;
        long totalTime = 0;
        for (DownloadCallable task : tasks) {
            long[] data = null;
            if (time == Long.MAX_VALUE) {
                data = task.getSpeedRecord().getLast();
            } else {
                data = task.getSpeedRecord().getRecordByTime(Double.valueOf(time).intValue());
            }
            totalTransfered += data[1];
            totalTime += data[0];
        }
        return new long[]{totalTime, totalTransfered};
    }

    private void onError(Exception e) {
        publishProgress(new Info<Exception>(Type.ERROR, e));
        state = State.ERROR;
    }

    private boolean isDone(List<Future<SpeedRecord>> results) {
        if (currRefreshTime - startTime > maxTime) {
            return true;
        }
        int done = 1;
        for (Future<SpeedRecord> f : results) {
            if (!f.isDone()) {
                done &= 0;
            }
        }
        return done == 1;
    }

    private List<DownloadCallable> initTasks(int length) {
        int more = length % blockCount;
        int blockLength = (length - more) / blockCount;

        List<DownloadCallable> tasks = Lists.newArrayList();
        for (int i = 0; i < blockCount; i++) {
            tasks.add(new DownloadCallable(new Config(url, i * blockLength, i == blockCount - 1 ? blockLength + more : blockLength, i)));
        }
        return tasks;
    }


    private List<Future<SpeedRecord>> startTask(List<DownloadCallable> tasks) {
        List<Future<SpeedRecord>> taskResults = Lists.newArrayList();

        for (int i = 0; i < blockCount; i++) {
            Future<SpeedRecord> future = executorService.submit(tasks.get(i));
            taskResults.add(future);
        }
        return taskResults;
    }

    private int getLength() throws IOException {
        long start = System.currentTimeMillis();
        int length = 0;
        HttpURLConnection connection = null;
        try {
            connection = connectionDownload();
            if (connection.getResponseCode() != 200 || (length = connection.getContentLength()) <= 0) {
                state = State.ERROR;
                publishProgress(new Info<Void>(Type.WRONG_URL, null));
            }
        } finally {
            close(connection);
        }
        Log.i(TAG, String.format("获取文件大小: %dM, 所用时间: %.1f秒.", length / 1024 / 1024, (System.currentTimeMillis() - start) / 1000F));
        return length;
    }

    private HttpURLConnection connectionDownload() throws IOException {
        URL urlC = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlC.openConnection();
        connection.setRequestProperty("Accept-Encoding", "identity");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setReadTimeout(readTimeout);
        connection.setConnectTimeout(connTimeout);
        connection.setInstanceFollowRedirects(true);
        connection.connect();

        return connection;
    }

    private void close(Object closeObj) {
        if (closeObj != null) {
            if (HttpURLConnection.class.isInstance(closeObj)) {
                ((HttpURLConnection) closeObj).disconnect();
            } else if (InputStream.class.isInstance(closeObj)) {
                try {
                    ((InputStream) closeObj).close();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            } else if (ExecutorService.class.isInstance(closeObj)) {
                try {
                    ((ExecutorService) closeObj).shutdownNow();
                } catch (RuntimeException e) {
                    Log.e(TAG, "", e);
                }
            }
        }
    }

    private int getInteger(int r) {
        return context.getResources().getInteger(r);
    }

    public enum Type {BLOCK, CURR, MAX, AVG, BLOCK_ERROR, ERROR, WRONG_URL, USED_TIME, DOWNLOADED, END}

    public enum State {BEGIN, SUCCEED, ERROR, BREAK}

    private class DownloadCallable implements Callable<SpeedRecord> {

        private long transfered;

        private Config config;

        private SpeedRecord speedRecord;

        private long lastTime;

        private long totalStart;

        private State state;

        private Exception e;

        private AtomicBoolean stop = new AtomicBoolean(false);

        private long blockSize;

        public DownloadCallable(Config config) {
            this.config = config;
            this.state = State.BEGIN;

            this.speedRecord = new SpeedRecord();
        }

        public SpeedRecord getSpeedRecord() {
            return speedRecord;
        }

        public void cancel() {
            stop.set(true);
        }

        @Override
        public SpeedRecord call() throws Exception {
            HttpURLConnection connection = null;
            InputStream is = null;
            try {
                byte[] buffer = new byte[bufferSize];
                connection = getConnection();
                if (connection.getResponseCode() == 206) {
                    is = connection.getInputStream();
                    totalStart = System.currentTimeMillis();
                    int size = -1;
                    while ((size = is.read(buffer)) > 0 && !stop.get()) {
                        if (recordByCycle(size)) {
                            break;
                        }
                    }
                    // 记录最后的结果
                    record(System.currentTimeMillis());
                    state = State.SUCCEED;
                    Log.i(TAG, Thread.currentThread().getName() + " SUCCEED");
                } else {
                    state = State.ERROR;
                    throw new IOException("Response Code: " + connection.getResponseCode());
                }
            } catch (IOException e) {
                onError(e);
            } finally {
                close(is);
                close(connection);
            }
            return speedRecord;
        }

        private void onError(IOException e) {
            state = State.ERROR;
            this.e = e;
            Log.e(TAG, Thread.currentThread().getName(), e);
        }

        public State getState() {
            return state;
        }

        private HttpURLConnection getConnection() throws IOException {
            URL url = new URL(config.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setReadTimeout(readTimeout);
            connection.setConnectTimeout(connTimeout);
            connection.setInstanceFollowRedirects(true);

            setRangeToConn(connection);
            return connection;
        }

        private void setRangeToConn(HttpURLConnection connection) {
            int[] range = countRange();
            // 设置范围，格式为Range：bytes x-y;
            connection.setRequestProperty("Range", "bytes=" + range[0] + "-" + range[1]);
        }

        private int[] countRange() {
            return new int[]{config.start, config.start + config.length - 1};
        }

            private void record(long now) {
                speedRecord.addRecord(now - totalStart, transfered);
                Log.d(TAG, "recordByCycle: " + (now - totalStart) + ": " + transfered + "/" + config.length);
                lastTime = now;
            }

        private boolean recordByCycle(int size) {
            transfered += size;

            long now = System.currentTimeMillis();
            if (now - lastTime >= recordCycle) {
                record(now);
            }
            if (now - totalStart >= maxTime) {
                state = State.BREAK;
                return true;
            }
            return false;
        }

        public Config getConfig() {
            return config;
        }
    }

    public class Info<D> {
        public final FileDownloadTask.Type type;

        public D o;

        public Info(FileDownloadTask.Type type, D data) {
            this.type = type;
            this.o = data;
        }
    }
}

class Config {
    public final String url;

    public final int index;

    public final int start;

    public final int length;

    public Config(String url, int start, int length, int index) {
        this.url = url;
        this.index = index;
        this.start = start;
        this.length = length;
    }
}
