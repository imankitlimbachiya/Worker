package com.worker.app.model;

public class FacilityModel {

    String Title, TitleArabic;
    String FacilityID, Description, DescriptionArabic;

    public String getDescriptionArabic() {
        return DescriptionArabic;
    }

    public void setDescriptionArabic(String descriptionArabic) {
        DescriptionArabic = descriptionArabic;
    }

    public String getTitleArabic() {
        return TitleArabic;
    }

    public void setTitleArabic(String titleArabic) {
        TitleArabic = titleArabic;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getFacilityID() {
        return FacilityID;
    }

    public void setFacilityID(String facilityID) {
        FacilityID = facilityID;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    String Cost;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
