package com.cattsoft.phone.quality.utils.speed;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据处理计算.
 * Created by Xiaohong on 2014/5/4.
 */
public class Counter {
    private final AtomicLong bytes = new AtomicLong(0);
    private final AtomicLong last = new AtomicLong(0);
    private final AtomicLong length = new AtomicLong(0);
    private final AtomicLong sequence = new AtomicLong(0);

    public long addBytes(long bytes) {
        return this.bytes.addAndGet(bytes);
    }

    public long incrementSequence() {
        return this.sequence.incrementAndGet();
    }

    public void reset() {
        this.bytes.set(0);
        this.last.set(0);
        this.length.set(0);
        this.sequence.set(0);
    }

    public long getBytes() {
        return this.bytes.get();
    }

    public long getLast() {
        return this.last.get();
    }

    public void setLast(long last) {
        this.last.set(last);
    }

    public long getLength() {
        return length.get();
    }

    public void setLength(long length) {
        this.length.set(length);
    }

    public long getSequence() {
        return sequence.get();
    }
}
