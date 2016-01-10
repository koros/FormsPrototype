package com.korosmatick.sample.model.api;

import java.util.List;

/**
 * Created by koros on 1/7/16.
 */
public class SyncResponse {

    List<SyncResponseItem> syncedItems;

    List<Long> updatedItemsAck; // acknowledgement for updated items

    public List<SyncResponseItem> getSyncedItems() {
        return syncedItems;
    }

    public void setSyncedItems(List<SyncResponseItem> syncedItems) {
        this.syncedItems = syncedItems;
    }

    public List<Long> getUpdatedItemsAck() {
        return updatedItemsAck;
    }

    public void setUpdatedItemsAck(List<Long> updatedItemsAck) {
        this.updatedItemsAck = updatedItemsAck;
    }
}
