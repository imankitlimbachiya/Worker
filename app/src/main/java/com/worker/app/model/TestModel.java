package com.worker.app.model;

public class TestModel {

    private String category;
    private String[] items;

    public TestModel(String category, String[] items) {
        this.category = category;
        this.items = items;
    }

    public String getCategory() {
        return category;
    }

    public String[] getItems() {
        return items;
    }
}