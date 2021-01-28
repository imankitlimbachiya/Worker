package com.worker.app.model;

import java.util.List;

public class CatSubCatModel {

    private String CategoryID, CategoryName, CategoryNameArabic;
    private List<SubCatModel> SubCategory;

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

    public List<SubCatModel> getSubCategory() {
        return SubCategory;
    }

    public void setSubCategory(List<SubCatModel> subCategory) {
        SubCategory = subCategory;
    }

    public class SubCatModel {
        private String SubCategoryID, SubCategoryName, SubCategoryNameArabic, Quantity;

        public String getSubCategoryID() {
            return SubCategoryID;
        }

        public void setSubCategoryID(String subCategoryID) {
            SubCategoryID = subCategoryID;
        }

        public String getSubCategoryName() {
            return SubCategoryName;
        }

        public void setSubCategoryName(String subCategoryName) {
            SubCategoryName = subCategoryName;
        }

        public String getSubCategoryNameArabic() {
            return SubCategoryNameArabic;
        }

        public void setSubCategoryNameArabic(String subCategoryNameArabic) {
            SubCategoryNameArabic = subCategoryNameArabic;
        }

        public String getQuantity() {
            return Quantity;
        }

        public void setQuantity(String quantity) {
            Quantity = quantity;
        }
    }
}
