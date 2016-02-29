package com.korosmatick.sample.model.api;

import java.util.List;

/**
 * Created by koros on 11/29/15.
 */
public class SyncRequestPacket {

    private int type = 1; // 1 = pull, 2 = push 

    private List<Row> records;
    
    private UpdatedItemsPacket updatedItemsPacket;
    
    private Long syncPointerPosition;

    private List<NewChildObjectPacket> newChildRecords;
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Row> getRecords() {
        return records;
    }

    public void setRecords(List<Row> records) {
        this.records = records;
    }

    public UpdatedItemsPacket getUpdatedItemsPacket() {
        return updatedItemsPacket;
    }

    public void setUpdatedItemsPacket(UpdatedItemsPacket updatedItemsPacket) {
        this.updatedItemsPacket = updatedItemsPacket;
    }

	public Long getSyncPointerPosition() {
		return syncPointerPosition;
	}

	public void setSyncPointerPosition(Long syncPointerPosition) {
		this.syncPointerPosition = syncPointerPosition;
	}

	public List<NewChildObjectPacket> getNewChildRecords() {
		return newChildRecords;
	}

	public void setNewChildRecords(List<NewChildObjectPacket> newChildRecords) {
		this.newChildRecords = newChildRecords;
	}
}

