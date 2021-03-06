package com.korosmatick.formsprototype.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by koros on 11/29/15.
 */
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
