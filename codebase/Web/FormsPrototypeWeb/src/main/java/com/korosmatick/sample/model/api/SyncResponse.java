package com.korosmatick.sample.model.api;

import java.util.List;

public class SyncResponse {
	List<SyncResponseItem> syncedItems;

	public List<SyncResponseItem> getSyncedItems() {
		return syncedItems;
	}

	public void setSyncedItems(List<SyncResponseItem> syncedItems) {
		this.syncedItems = syncedItems;
	}
}
