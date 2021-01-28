package com.worker.app.model;

public class CatModel {

    String categoryName;

    public CatModel(String categoryName) {
        this.categoryName = categoryName;

    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
