package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

/**
 * 通话断开原因.
 * 作缓存值，处理完成后删除.
 * Created by Xiaohong on 2014/5/10.
 */
@DatabaseTable(tableName = "pq_call_cause_cache")
public class CallCauseCache {
    @DatabaseField(generatedId = true, dataType = DataType.LONG)
    private long id;
    /** 通话类型 */
    @DatabaseField
    private int type;
    /** 联系人名称 */
    @DatabaseField
    private String name;
    /** 对方号码 */
    @DatabaseField
    private String number;
    /** 是否已接听 */
    @DatabaseField
    private boolean answered;
    /** 断开原因 */
    @DatabaseField
    private String cause;
    /** 通话时间 */
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime ddate;

    public CallCauseCache() {
    }

    public CallCauseCache(int type, String name, String number, boolean answered) {
        this.type = type;
        this.name = name;
        this.number = number;
        this.answered = answered;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public DateTime getDdate() {
        return ddate;
    }

    public void setDdate(DateTime ddate) {
        this.ddate = ddate;
    }
}
