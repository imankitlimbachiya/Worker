package com.worker.app.model;

public class DrawerModel {
    private String  itemTitle, id;

    public DrawerModel(String itemTitle, String id) {
        this.itemTitle = itemTitle;
        this.id = id;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
