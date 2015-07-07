package com.cattsoft.phone.quality.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 配置信息.
 */
@DatabaseTable(tableName = "pq_metadata")
public class MetaData {
//    @DatabaseField(generatedId = true, dataType = DataType.LONG)
//    private long id;

    @DatabaseField(id = true, unique = true, canBeNull = false)
    private String name;

    @DatabaseField
    private String value;

    public MetaData() {
    }

    public MetaData(String name) {
        this(name, null);
    }

    public MetaData(String name, String value) {
        this.name = name;
        this.value = value;
    }

//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
