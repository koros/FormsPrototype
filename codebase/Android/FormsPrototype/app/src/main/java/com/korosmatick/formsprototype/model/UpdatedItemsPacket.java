package com.korosmatick.formsprototype.model;

import java.util.List;

/**
 * Created by koros on 1/10/16.
 */
public class UpdatedItemsPacket {

    private List<UpdatedItem> updatedItemList;

    public List<UpdatedItem> getUpdatedItemList() {
        return updatedItemList;
    }

    public void setUpdatedItemList(List<UpdatedItem> updatedItemList) {
        this.updatedItemList = updatedItemList;
    }
}
