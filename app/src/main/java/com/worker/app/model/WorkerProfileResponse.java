package com.worker.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkerProfileResponse {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("Worker_Detail")
    @Expose
    private List<WorkerDetail> workerDetail = null;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<WorkerDetail> getWorkerDetail() {
        return workerDetail;
    }

    public void setWorkerDetail(List<WorkerDetail> workerDetail) {
        this.workerDetail = workerDetail;
    }

    public class Education {

        @SerializedName("Degree")
        @Expose
        private String degree;
        @SerializedName("College")
        @Expose
        private String college;
        @SerializedName("Date")
        @Expose
        private String date;

        public String getDegree() {
            return degree;
        }

        public void setDegree(String degree) {
            this.degree = degree;
        }

        public String getCollege() {
            return college;
        }

        public void setCollege(String college) {
            this.college = college;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

    }


    public class Experience {

        @SerializedName("CompanyName")
        @Expose
        private String companyName;
        @SerializedName("Department")
        @Expose
        private String department;
        @SerializedName("JobTitle")
        @Expose
        private String jobTitle;
        @SerializedName("YearsOfExp")
        @Expose
        private String yearsOfExp;

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }

        public String getYearsOfExp() {
            return yearsOfExp;
        }

        public void setYearsOfExp(String yearsOfExp) {
            this.yearsOfExp = yearsOfExp;
        }

    }


    public class WorkerDetail {

        @SerializedName("WorkerID")
        @Expose
        private String workerID;
        @SerializedName("CategoryID")
        @Expose
        private String categoryID;
        @SerializedName("SubCategoryID")
        @Expose
        private String subCategoryID;
        @SerializedName("WorkerName")
        @Expose
        private String workerName;
        @SerializedName("W_LastName")
        @Expose
        private String wLastName;
        @SerializedName("MiddleName")
        @Expose
        private String middleName;
        @SerializedName("Gender")
        @Expose
        private String gender;
        @SerializedName("GenderArabic")
        @Expose
        private String genderArabic;
        @SerializedName("BirthDate")
        @Expose
        private String birthDate;
        @SerializedName("WorkerImage")
        @Expose
        private String workerImage;
        @SerializedName("Nationality")
        @Expose
        private String nationality;
        @SerializedName("NationalityArabic")
        @Expose
        private String nationalityArabic;
        @SerializedName("CountryID")
        @Expose
        private String countryID;
        @SerializedName("CountryName")
        @Expose
        private String countryName;
        @SerializedName("Religion")
        @Expose
        private String religion;
        @SerializedName("ReligionArabic")
        @Expose
        private String religionArabic;
        @SerializedName("Age")
        @Expose
        private Object age;
        @SerializedName("ContactFees")
        @Expose
        private String contactFees;
        @SerializedName("MonthlySalary")
        @Expose
        private String monthlySalary;
        @SerializedName("AgentID")
        @Expose
        private String agentID;
        @SerializedName("Approve")
        @Expose
        private String approve;
        @SerializedName("Languages")
        @Expose
        private String languages;
        @SerializedName("Education")
        @Expose
        private List<Education> education = null;
        @SerializedName("Experience")
        @Expose
        private List<Experience> experience = null;
        @SerializedName("CV")
        @Expose
        private String cV;
        @SerializedName("Video")
        @Expose
        private String video;
        @SerializedName("CategoryName")
        @Expose
        private String categoryName;
        @SerializedName("CategoryNameArabic")
        @Expose
        private String categoryNameArabic;
        @SerializedName("SubCategoryName")
        @Expose
        private String subCategoryName;
        @SerializedName("SubCategoryNameArabic")
        @Expose
        private String subCategoryNameArabic;
        @SerializedName("FirstName")
        @Expose
        private String firstName;
        @SerializedName("LastName")
        @Expose
        private String lastName;
        @SerializedName("LanguageArabic")
        @Expose
        private String languageArabic;

        public String getWorkerID() {
            return workerID;
        }

        public void setWorkerID(String workerID) {
            this.workerID = workerID;
        }

        public String getCategoryID() {
            return categoryID;
        }

        public void setCategoryID(String categoryID) {
            this.categoryID = categoryID;
        }

        public String getSubCategoryID() {
            return subCategoryID;
        }

        public void setSubCategoryID(String subCategoryID) {
            this.subCategoryID = subCategoryID;
        }

        public String getWorkerName() {
            return workerName;
        }

        public void setWorkerName(String workerName) {
            this.workerName = workerName;
        }

        public String getWLastName() {
            return wLastName;
        }

        public void setWLastName(String wLastName) {
            this.wLastName = wLastName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
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

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getWorkerImage() {
            return workerImage;
        }

        public void setWorkerImage(String workerImage) {
            this.workerImage = workerImage;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getNationalityArabic() {
            return nationalityArabic;
        }

        public void setNationalityArabic(String nationalityArabic) {
            this.nationalityArabic = nationalityArabic;
        }

        public String getCountryID() {
            return countryID;
        }

        public void setCountryID(String countryID) {
            this.countryID = countryID;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public String getReligion() {
            return religion;
        }

        public void setReligion(String religion) {
            this.religion = religion;
        }

        public String getReligionArabic() {
            return religionArabic;
        }

        public void setReligionArabic(String religionArabic) {
            this.religionArabic = religionArabic;
        }

        public Object getAge() {
            return age;
        }

        public void setAge(Object age) {
            this.age = age;
        }

        public String getContactFees() {
            return contactFees;
        }

        public void setContactFees(String contactFees) {
            this.contactFees = contactFees;
        }

        public String getMonthlySalary() {
            return monthlySalary;
        }

        public void setMonthlySalary(String monthlySalary) {
            this.monthlySalary = monthlySalary;
        }

        public String getAgentID() {
            return agentID;
        }

        public void setAgentID(String agentID) {
            this.agentID = agentID;
        }

        public String getApprove() {
            return approve;
        }

        public void setApprove(String approve) {
            this.approve = approve;
        }

        public String getLanguages() {
            return languages;
        }

        public void setLanguages(String languages) {
            this.languages = languages;
        }

        public List<Education> getEducation() {
            return education;
        }

        public void setEducation(List<Education> education) {
            this.education = education;
        }

        public List<Experience> getExperience() {
            return experience;
        }

        public void setExperience(List<Experience> experience) {
            this.experience = experience;
        }

        public String getCV() {
            return cV;
        }

        public void setCV(String cV) {
            this.cV = cV;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCategoryNameArabic() {
            return categoryNameArabic;
        }

        public void setCategoryNameArabic(String categoryNameArabic) {
            this.categoryNameArabic = categoryNameArabic;
        }

        public String getSubCategoryName() {
            return subCategoryName;
        }

        public void setSubCategoryName(String subCategoryName) {
            this.subCategoryName = subCategoryName;
        }

        public String getSubCategoryNameArabic() {
            return subCategoryNameArabic;
        }

        public void setSubCategoryNameArabic(String subCategoryNameArabic) {
            this.subCategoryNameArabic = subCategoryNameArabic;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getLanguageArabic() {
            return languageArabic;
        }

        public void setLanguageArabic(String languageArabic) {
            this.languageArabic = languageArabic;
        }

    }
}
