package com.korosmatick.formsprototype.model;

import java.util.List;

/**
 * Created by koros on 1/7/16.
 */
public class SyncResponse {

    List<SyncResponseItem> syncedItems;

    public List<SyncResponseItem> getSyncedItems() {
        return syncedItems;
    }

    public void setSyncedItems(List<SyncResponseItem> syncedItems) {
        this.syncedItems = syncedItems;
    }
}
