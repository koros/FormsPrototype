package com.korosmatick.formsprototype.model;

/**
 * Created by koros on 1/7/16.
 */
public class SyncResponseItem {

    private String tableName;
    private long localId;
    private long serverId;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }
}

