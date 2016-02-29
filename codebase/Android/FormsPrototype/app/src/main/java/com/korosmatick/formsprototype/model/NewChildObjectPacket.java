package com.korosmatick.formsprototype.model;

import java.util.List;

/**
 * Created by koros on 1/24/16.
 */
public class NewChildObjectPacket {

    private Long parentId;

    private List<Row> records;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<Row> getRecords() {
        return records;
    }

    public void setRecords(List<Row> records) {
        this.records = records;
    }
}
