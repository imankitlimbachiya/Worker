package com.worker.app.model;

public class HomeCategoryModel {

    String CategoryID, CategoryName, CategoryNameArabic, HomeScreenImage, DisplayOrder, StripImage;

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCategoryNameArabic() {
        return CategoryNameArabic;
    }

    public void setCategoryNameArabic(String categoryNameArabic) {
        CategoryNameArabic = categoryNameArabic;
    }

    public String getHomeScreenImage() {
        return HomeScreenImage;
    }

    public void setHomeScreenImage(String homeScreenImage) {
        HomeScreenImage = homeScreenImage;
    }

    public String getDisplayOrder() {
        return DisplayOrder;
    }

    public void setDisplayOrder(String displayOrder) {
        DisplayOrder = displayOrder;
    }

    public String getStripImage() {
        return StripImage;
    }

    public void setStripImage(String stripImage) {
        StripImage = stripImage;
    }
}
