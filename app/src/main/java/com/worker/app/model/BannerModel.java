package com.worker.app.model;

public class BannerModel {

    String BannerImage, BannerID, CriteriaType, CategoryID, WorkerID, StripImage, BannerTitle, BannerTitleArabic;

    public BannerModel() {
        this.BannerImage = BannerImage;
        this.BannerID = BannerID;
        this.CriteriaType = CriteriaType;
        this.CategoryID = CategoryID;
        this.WorkerID = WorkerID;
    }

    public String getBannerTitle() {
        return BannerTitle;
    }

    public void setBannerTitle(String bannerTitle) {
        BannerTitle = bannerTitle;
    }

    public String getBannerTitleArabic() {
        return BannerTitleArabic;
    }

    public void setBannerTitleArabic(String bannerTitleArabic) {
        BannerTitleArabic = bannerTitleArabic;
    }

    public String getBannerImage() {
        return BannerImage;
    }

    public void setBannerImage(String bannerImage) {
        BannerImage = bannerImage;
    }

    public String getBannerID() {
        return BannerID;
    }

    public void setBannerID(String bannerID) {
        BannerID = bannerID;
    }

    public String getCriteriaType() {
        return CriteriaType;
    }

    public void setCriteriaType(String criteriaType) {
        CriteriaType = criteriaType;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getWorkerID() {
        return WorkerID;
    }

    public void setWorkerID(String workerID) {
        WorkerID = workerID;
    }

    public String getStripImage() {
        return StripImage;
    }

    public void setStripImage(String stripImage) {
        StripImage = stripImage;
    }
}
