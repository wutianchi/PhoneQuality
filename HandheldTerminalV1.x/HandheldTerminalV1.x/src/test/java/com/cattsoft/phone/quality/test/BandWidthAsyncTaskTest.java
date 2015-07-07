package com.cattsoft.phone.quality.test;

import android.test.AndroidTestCase;
import android.util.Log;
import com.cattsoft.phone.quality.task.UpDownAsyncTask;
import com.cattsoft.phone.quality.task.common.SpeedRecord;

/**
 * Created by yushiwei on 15-5-14.
 */
public class BandWidthAsyncTaskTest extends AndroidTestCase {

    public static final String TAG = BandWidthAsyncTaskTest.class.getSimpleName();

    private UpDownAsyncTask bandWidthAsyncTask;

    @Override
    protected void setUp() throws Exception {

    }

    public void testDoinbackGround() {
        for (int i = 0; i < 10; i++) {
            UpDownAsyncTask task = new UpDownAsyncTask("http://61.135.214.54:8080/speed/doload.do", getContext());
            task.setTestTime(10000);
            task.setCycle(50);
            task.setUnit(1);
            task.doInBackground(UpDownAsyncTask.UPLOAD);

            SpeedRecord sr = task.getSpeedRecord();
            for (int j = 0; j < sr.size(); j++) {
                long[] record = sr.getRecord(j);
                Log.d(TAG, "time: " + record[0] + ", uploaded: " + record[1]);
            }
        }

    }
}
