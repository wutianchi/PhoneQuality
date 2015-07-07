package com.cattsoft.phone.quality.task.common;

/**
 * Created by yushiwei on 15-5-20.
 */
public class SpeedRecord {

    private int limit = 10000;

    private long[][] records = new long[limit][2];

    private int curr = -1;

    public static void main(String[] args) {
        SpeedRecord sr = new SpeedRecord();

        sr.getRecordByTime(1);
    }

    public long[] getLast() {
        if (curr >= 0) {
            return records[curr];
        } else {
            return new long[]{0, 0};
        }
    }

    public int size() {
        return curr;
    }

    public long[] getRecordByTime(int time) {
        return find(time, 0, curr);
    }

    public long[] find(long num, int start, int end) {
        if (end - start < 0) {
            return new long[]{0, 0};
        } else if (end - start == 0) {
            return records[start];
        } else if (end - start == 1) {
            if (records[start][0] == num) {
                return records[start];
            } else {
                return records[end];
            }
        }
        int length = end - start + 1;
        int middle = (start + length / 2);
        if (records[middle][0] < num) {
            return find(num, middle, end);
        } else if (records[middle][0] == num) {
            return records[middle];
        } else {
            return find(num, start, middle);
        }
    }

    public long[] getRecord(int i) {
        return records[i];
    }

    public boolean addRecord(long time, long size) {
        if (curr < limit) {
            curr++;
            records[curr][0] = time;
            records[curr][1] = size;
            return true;
        }
        return false;
    }
}
