package com.worker.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AgeRangeModel {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("AgeRange")
    @Expose
    private List<AgeRange> ageRange = null;
    @SerializedName("Gender")
    @Expose
    private List<Gender> gender = null;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<AgeRange> getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(List<AgeRange> ageRange) {
        this.ageRange = ageRange;
    }

    public List<Gender> getGender() {
        return gender;
    }

    public void setGender(List<Gender> gender) {
        this.gender = gender;
    }

    public class Gender {

        @SerializedName("GenderID")
        @Expose
        private String genderID;
        @SerializedName("Gender")
        @Expose
        private String gender;
        @SerializedName("GenderArabic")
        @Expose
        private String genderArabic;
        @SerializedName("DisplayOrder")
        @Expose
        private String displayOrder;
        @SerializedName("CreatedDate")
        @Expose
        private String createdDate;
        @SerializedName("UpdatedDate")
        @Expose
        private String updatedDate;
        @SerializedName("Active")
        @Expose
        private String active;
        @SerializedName("Deleted")
        @Expose
        private Object deleted;

        public String getGenderID() {
            return genderID;
        }

        public void setGenderID(String genderID) {
            this.genderID = genderID;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getGenderArabic() {
            return genderArabic;
        }

        public void setGenderArabic(String genderArabic) {
            this.genderArabic = genderArabic;
        }

        public String getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(String displayOrder) {
            this.displayOrder = displayOrder;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getUpdatedDate() {
            return updatedDate;
        }

        public void setUpdatedDate(String updatedDate) {
            this.updatedDate = updatedDate;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public Object getDeleted() {
            return deleted;
        }

        public void setDeleted(Object deleted) {
            this.deleted = deleted;
        }

    }

    public class AgeRange {

        @SerializedName("AgeID")
        @Expose
        private String ageID;
        @SerializedName("AgeRange")
        @Expose
        private String ageRange;
        @SerializedName("Active")
        @Expose
        private String active;
        @SerializedName("DisplayOrder")
        @Expose
        private Object displayOrder;
        @SerializedName("CreatedDate")
        @Expose
        private String createdDate;

        public String getAgeID() {
            return ageID;
        }

        public void setAgeID(String ageID) {
            this.ageID = ageID;
        }

        public String getAgeRange() {
            return ageRange;
        }

        public void setAgeRange(String ageRange) {
            this.ageRange = ageRange;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public Object getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(Object displayOrder) {
            this.displayOrder = displayOrder;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

    }


}
