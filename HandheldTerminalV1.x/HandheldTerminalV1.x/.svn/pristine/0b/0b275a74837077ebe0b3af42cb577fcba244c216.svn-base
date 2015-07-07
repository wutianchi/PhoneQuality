package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * 网络信号活动统计.
 * Created by Xiaohong on 2014/5/11.
 */
@DatabaseTable(tableName = "pq_signal_activity")
public class SignalActivity {
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;
    /** 2G 网络信号时长，毫秒 */
    @DatabaseField
    private long signal2G;
    /** 3G 网络信号时长，毫秒 */
    @DatabaseField
    private long signal3G;
    /** 4G 网络信号时长，毫秒 */
    @DatabaseField
    private long signal4G;
    /** 无网络信号时长，毫秒 */
    @DatabaseField
    private long noSignal;
    /** 网络信号总量 */
    @DatabaseField
    private long signals;
    @DatabaseField
    private long signals4G;
    /** 平均信号采集指标数量 */
    @DatabaseField
    private long targets;
    @DatabaseField
    private long targets4G;
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime ddate;

    public SignalActivity() {
    }

    public SignalActivity(DateTime ddate) {
        this.ddate = ddate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSignal2G() {
        return signal2G;
    }

    public void setSignal2G(long signal2G) {
        this.signal2G = signal2G;
    }

    public long getSignal3G() {
        return signal3G;
    }

    public void setSignal3G(long signal3G) {
        this.signal3G = signal3G;
    }

    public long getSignal4G() {
        return signal4G;
    }

    public void setSignal4G(long signal4G) {
        this.signal4G = signal4G;
    }

    public long getNoSignal() {
        return noSignal;
    }

    public void setNoSignal(long noSignal) {
        this.noSignal = noSignal;
    }

    public long getSignals() {
        return signals;
    }

    public void setSignals(long signals) {
        this.signals = signals;
    }

    public long getTargets() {
        return targets;
    }

    public void setTargets(long targets) {
        this.targets = targets;
    }

    public DateTime getDdate() {
        return ddate;
    }

    public void setDdate(DateTime ddate) {
        this.ddate = ddate;
    }

    public long getSignals4G() {
        return signals4G;
    }

    public void setSignals4G(long signals4G) {
        this.signals4G = signals4G;
    }

    public long getTargets4G() {
        return targets4G;
    }

    public void setTargets4G(long targets4G) {
        this.targets4G = targets4G;
    }
}
