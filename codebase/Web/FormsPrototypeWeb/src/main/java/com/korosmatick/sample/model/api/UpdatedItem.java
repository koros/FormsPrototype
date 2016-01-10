package com.korosmatick.sample.model.api;

/**
 * Created by koros on 1/10/16.
 */
public class UpdatedItem {

    private String tableName;
    private String columnName;
    private String newValue;
    private String serverId;
    private String syncItemId;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

	public String getSyncItemId() {
		return syncItemId;
	}

	public void setSyncItemId(String syncItemId) {
		this.syncItemId = syncItemId;
	}
    
}

