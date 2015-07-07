package com.cattsoft.phone.quality.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.task.common.SpeedRecord;
import com.cattsoft.phone.quality.utils.speed.NetSpeedTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.*;

public abstract class BandWidthAsyncTask extends AsyncTask<Void, BandWidthAsyncTask.Info, UpDownAsyncTask.State> {
    //得到类的简写名称
    public static final String TAG = BandWidthAsyncTask.class.getSimpleName();

    private UpDownAsyncTask[] tasks;

    private long startTime;

    private long minTime;

    private long avgTime = 10000;

    private int taskSize;

    private int recordCycle;

    private int showCycle;

    private String server;

    private Context context;

    private int downloadTestTime;

    private int uploadTestTime;

    private int testTimeAdditional;

    private State state;

    private int taskFailCount;

    private int fatalTaskCount;

    public BandWidthAsyncTask(Context context, String server) {
        this.context = context;
        this.server = server;
        this.startTime = System.currentTimeMillis();
        this.minTime = getInteger(context, R.integer.bandwidth_mintime);
        this.avgTime = getInteger(context, R.integer.bandwidth_avgtime);
        this.recordCycle = getInteger(context, R.integer.bandwidth_record_cycle);
        this.showCycle = getInteger(context, R.integer.bandwidth_show_cycle);
        this.taskSize = getInteger(context, R.integer.bandwidth_task_size);
        this.downloadTestTime = getInteger(context, R.integer.bandwidth_download_test_time);
        this.uploadTestTime = getInteger(context, R.integer.bandwidth_upload_test_time);
        this.testTimeAdditional = getInteger(context, R.integer.bandwidth_testtime_additional);
        this.fatalTaskCount = getInteger(context, R.integer.bandwidth_fatal_task_size);
    }

    private int getInteger(Context context, int bandwidth_avgtime) {
        return context.getResources().getInteger(bandwidth_avgtime);
    }

    private int taskFailCount() {
        int count = 0;
        for (UpDownAsyncTask task : tasks) {
            if (task.getState() == UpDownAsyncTask.State.ERROR) {
                count++;
            }
        }
        return count;
    }

    private boolean isTasksEnd() {
        if (System.currentTimeMillis() - startTime > getTestTime()) {
            return true;
        }

        int isEnd = 1;
        for (UpDownAsyncTask task : tasks) {
            switch (task.getState()) {
                case ERROR:
                case BREAK:
                case SUCCEED:
                    isEnd &= 0;
                    break;
                case NOTBEGIN:
                case PROCESSING:
                    isEnd &= 1;
                    break;
            }
        }
        return isEnd == 0;
    }

    private int getTestTime() {
        return (getState() == State.DOWNLOAD ? downloadTestTime : uploadTestTime) + testTimeAdditional;
    }

    private float showTotalAvgSpeed() {
        float speed = getSpeed(getEndRecords(), getDefaultRecord());
        if (getState() == State.DOWNLOAD) {
            publishProgress(new Info(ShowType.SHOW_RANGE_AVG_DOWNLOAD, speed));
        } else if (getState() == State.UPLOAD) {
            publishProgress(new Info(ShowType.SHOW_RANGE_AVG_UPLOAD, speed));
        }
        Log.d(TAG, "TOTAL AVG SPEED: " + speed + "kbyte/s");
        return speed;
    }

    public long[] getDefaultRecord() {
        return new long[]{0, 0};
    }

    private long[] getStartRecords(long minTime) {
        long[] startRecord = {0, 0};
        if (System.currentTimeMillis() - startTime > minTime) {
            startRecord = getRecord(System.currentTimeMillis() - startTime - minTime);
        }
        Log.d(TAG, "StartRecord: " + Arrays.toString(startRecord));
        return startRecord;
    }

    private long[] getEndRecords() {
        long[] endRecord = getRecord(Long.MAX_VALUE);
        Log.d(TAG, "EndRecord: " + Arrays.toString(endRecord));
        return endRecord;
    }

    private float getSpeed(long[] endRecord, long[] startRecord) {
        Double speed = 0D;
        // 从bit/ms换算成Kbyte/s
        if (endRecord[0] - startRecord[0] > 0) {
            speed = ((endRecord[1] - startRecord[1]) / 1024D) / ((endRecord[0] - startRecord[0]) / 1000D) * taskSize;
        }
        return speed.floatValue();
    }

    private long[] getRecord(long time) {
        long totalTime = 0;
        long totalData = 0;

        for (UpDownAsyncTask task : tasks) {
            SpeedRecord sr = task.getSpeedRecord();
            long[] record;
            if (time == Long.MAX_VALUE) {
                record = sr.getLast();
            } else {
                record = sr.getRecordByTime(Double.valueOf(time).intValue());
            }
            totalTime += record[0];
            totalData += record[1];
        }
        return new long[]{totalTime, totalData};
    }

    private void printRecord() {
        int j = 1;
        for (UpDownAsyncTask task : tasks) {
            SpeedRecord sr = task.getSpeedRecord();
            for (int i = 0; i < sr.size(); i++) {
                Log.d(TAG, "SpeedRecord" + j + ": " + Arrays.toString(sr.getRecord(i)));
            }
            j++;
        }
    }

    private void waitTasksDown() throws ExecutionException, InterruptedException {
        for (UpDownAsyncTask task : tasks) {
            try {
                task.get(1000, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                Log.i(TAG, "上传/下载任务超时, 强制结束");
                task.cancel(true);
            }
        }
    }

    @Override
    protected UpDownAsyncTask.State doInBackground(Void... params) {
        try {
            start();
            delay();
            download();
            upload();
            end();
        } catch (ExecutionException e) {
            onException(e);
        } catch (InterruptedException e) {
            onException(e);
        } catch (IOException e) {
            onException(e);
        } catch (Exception e) {
            onException(e);
        }
        return UpDownAsyncTask.State.SUCCEED;
    }

    private void onException(Exception e) {
        setState(State.ERROR);
        Log.e(TAG, "", e);
        publishProgress(new Info(ShowType.FATAL, e));
    }

    private void delay() throws ExecutionException, InterruptedException {
        setState(State.DELAY);
        publishProgress(new Info(ShowType.SWITCH, NetSpeedTest.Type.PING));
        UpDownAsyncTask task = new UpDownAsyncTask(server, context);
        task.doInBackground(UpDownAsyncTask.DELAY);
        publishProgress(new Info(ShowType.SHOW_DELAY, task.getDelay()));
    }

    private void start() {
        publishProgress(new Info(ShowType.START, null));
    }

    private void end() throws InterruptedException {
        publishProgress(new Info(ShowType.END, null));
    }

    private void download() throws ExecutionException, InterruptedException, IOException {
        setState(State.DOWNLOAD);
        publishProgress(new Info(ShowType.SWITCH, NetSpeedTest.Type.DOWNLOAD));
        startTime = System.currentTimeMillis();
        doTask();
        showBand();
    }

    private void doTask() throws InterruptedException, ExecutionException, IOException {
        ExecutorService executorService = null;
        try {
            executorService = createTasks();
            refreshUI();
            waitTasksDown();
            printRecord();
            showTotalAvgSpeed();
        } finally {
            if (executorService != null) {
                executorService.shutdownNow();
            }
        }
    }

    private void refreshUI() throws InterruptedException, IOException {
        while (!isTasksEnd()) {
            Thread.sleep(this.showCycle);

            showCurrSpeed();

            showAvgSpeed();

            showError();
        }
    }

    private void showError() throws IOException {
        int currFailCount = taskFailCount();
        if (taskFailCount < currFailCount) {
            taskFailCount = currFailCount;
            if (taskFailCount >= fatalTaskCount) {
                throw new IOException("致命错误！");
            } else {
                publishProgress(new Info(ShowType.ERROR, currFailCount));
            }
        }
    }

    private void showBand() {
        if (getState() == State.DOWNLOAD) {
            // 刨除前30%的记录速率
            long time = Math.round(downloadTestTime * .3D);
            float bandSpeed = getSpeed(getEndRecords(), getRecord(time));
            publishProgress(new Info(ShowType.SHOW_BAND, bandSpeed));
//            Log.d(TAG, "BAND SPEED: " + bandSpeed);
        }
    }

    private void showAvgSpeed() {
        float avgSpeed = getSpeed(getEndRecords(), getStartRecords(avgTime));
        publishProgress(new Info(ShowType.SHOW_RANGE_AVG_BAND, avgSpeed));
//        Log.d(TAG, "AVG SPEED: " + avgSpeed + "kbyte/s");
    }

    private void showCurrSpeed() {
        float currSpeed = getSpeed(getEndRecords(), getStartRecords(minTime));
        if (getState() == State.DOWNLOAD) {
            publishProgress(new Info(ShowType.SHOW_CURR_DOWNLOAD, currSpeed));
        } else if (getState() == State.UPLOAD) {
            publishProgress(new Info(ShowType.SHOW_CURR_UPLOAD, currSpeed));
        }
//        Log.d(TAG, "CURR SPEED: " + currSpeed + "kbyte/s");
    }

    private ExecutorService createTasks() {
        tasks = new UpDownAsyncTask[taskSize];
        ExecutorService executorService = Executors.newFixedThreadPool(taskSize + 1);
        for (int i = 0; i < taskSize; i++) {
            tasks[i] = new UpDownAsyncTask(server, context);
            if (getState() == State.DOWNLOAD) {
                tasks[i].setTestTime(downloadTestTime);
                tasks[i].setCycle(recordCycle);
                tasks[i].executeOnExecutor(executorService, UpDownAsyncTask.DOWNLOAD);
            } else if (getState() == State.UPLOAD) {
                tasks[i].setTestTime(uploadTestTime);
                tasks[i].setCycle(recordCycle);
                tasks[i].executeOnExecutor(executorService, UpDownAsyncTask.UPLOAD);
            }
        }
        return executorService;
    }

    private void upload() throws ExecutionException, InterruptedException, IOException {
        setState(State.UPLOAD);
        publishProgress(new Info(ShowType.SWITCH, NetSpeedTest.Type.UPLOAD));
        startTime = System.currentTimeMillis();
        doTask();
    }

    public State getState() {
        return this.state;
    }

    private void setState(State state) {
        this.state = state;
    }

    public abstract void onProgressUpdate(Info... values);

    public enum State {DELAY, DOWNLOAD, UPLOAD, ERROR}

    public enum ShowType {FATAL, SHOW_DELAY, SHOW_BAND, SHOW_RANGE_AVG_BAND, SHOW_RANGE_AVG_UPLOAD, SHOW_RANGE_AVG_DOWNLOAD, SHOW_CURR_UPLOAD, SHOW_CURR_DOWNLOAD, SWITCH, END, RESET, START, ERROR}

    public class Info {
        public final ShowType oper;

        public final Object value;

        public Info(ShowType oper, Object value) {
            this.oper = oper;
            this.value = value;
        }
    }
}