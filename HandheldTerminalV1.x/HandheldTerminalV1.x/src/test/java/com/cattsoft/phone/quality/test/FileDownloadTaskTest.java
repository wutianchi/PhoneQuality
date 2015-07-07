package com.cattsoft.phone.quality.test;

import android.test.AndroidTestCase;
import android.util.Log;
import com.cattsoft.phone.quality.task.FileDownloadTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by yushiwei on 15-5-21.
 */
public class FileDownloadTaskTest extends AndroidTestCase {

    FileDownloadTask fileDownloadTask;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fileDownloadTask = new FileDownloadTask(getContext(), "http://dlsw.baidu.com/sw-search-sp/soft/9d/25765/sogou_mac_31.1421982306.dmg") {
            @Override
            public void onProgressUpdate(Info... values) {
                Log.i(FileDownloadTask.TAG, values[0].type + ": " + values[0].o);
            }
        };
    }

    public void testExec() throws ExecutionException, InterruptedException {
        fileDownloadTask.doInBackground();
    }
}
