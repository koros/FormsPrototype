package com.korosmatick.formsprototype.model;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by koros on 11/29/15.
 */
public class Row {

    String tableName;
    JSONObject row; //column:value values
    List<Row> childRows;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public JSONObject getRow() {
        return row;
    }

    public void setRow(JSONObject row) {
        this.row = row;
    }

    public List<Row> getChildRows() {
        return childRows;
    }

    public void setChildRows(List<Row> childRows) {
        this.childRows = childRows;
    }

}
