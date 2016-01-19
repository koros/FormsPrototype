package com.korosmatick.sample.model.api;

import java.util.List;

/**
 * Created by koros on 1/7/16.
 */
public class SyncResponse {

    List<SyncResponseItem> syncedItems;

    List<Long> updatedItemsAck; // acknowledgement for updated items
    
    private List<Row> newRecords;
    
    private UpdatedItemsPacket updatedItemsPacket;
    
    private Long syncPointerPosition;
    
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

	public List<Row> getNewRecords() {
		return newRecords;
	}

	public void setNewRecords(List<Row> newRecords) {
		this.newRecords = newRecords;
	}

	public Long getSyncPointerPosition() {
		return syncPointerPosition;
	}

	public void setSyncPointerPosition(Long syncPointerPosition) {
		this.syncPointerPosition = syncPointerPosition;
	}

	public UpdatedItemsPacket getUpdatedItemsPacket() {
		return updatedItemsPacket;
	}

	public void setUpdatedItemsPacket(UpdatedItemsPacket updatedItemsPacket) {
		this.updatedItemsPacket = updatedItemsPacket;
	}
    
}
