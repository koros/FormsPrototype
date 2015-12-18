package com.korosmatick.sample.model.api;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class Row {
	String tableName;
    Map<String, String> row; //column:value values
    List<Row> childRows;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getRow() {
        return row;
    }

    public void setRow(Map<String, String> row) {
        this.row = row;
    }

    public List<Row> getChildRows() {
        return childRows;
    }

    public void setChildRows(List<Row> childRows) {
        this.childRows = childRows;
    }

}
