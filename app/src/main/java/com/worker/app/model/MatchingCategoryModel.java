package com.worker.app.model;

public class MatchingCategoryModel {

    private int workerImgProfile;
    private String workerName, wCountry, wContractFees, wReligion, wAge, wSalaryMonthly, wProfession;


    public MatchingCategoryModel(int workerImgProfile, String workerName, String wCountry, String wContractFees, String wReligion, String wAge, String wSalaryMonthly, String wProfession) {
        this.workerImgProfile = workerImgProfile;
        this.workerName = workerName;
        this.wCountry = wCountry;
        this.wContractFees = wContractFees;
        this.wReligion = wReligion;
        this.wAge = wAge;
        this.wSalaryMonthly = wSalaryMonthly;
        this.wProfession = wProfession;
    }

    public int getWorkerImgProfile() {
        return workerImgProfile;
    }

    public void setWorkerImgProfile(int workerImgProfile) {
        this.workerImgProfile = workerImgProfile;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getwCountry() {
        return wCountry;
    }

    public void setwCountry(String wCountry) {
        this.wCountry = wCountry;
    }

    public String getwContractFees() {
        return wContractFees;
    }

    public void setwContractFees(String wContractFees) {
        this.wContractFees = wContractFees;
    }

    public String getwReligion() {
        return wReligion;
    }

    public void setwReligion(String wReligion) {
        this.wReligion = wReligion;
    }

    public String getwAge() {
        return wAge;
    }

    public void setwAge(String wAge) {
        this.wAge = wAge;
    }

    public String getwSalaryMonthly() {
        return wSalaryMonthly;
    }

    public void setwSalaryMonthly(String wSalaryMonthly) {
        this.wSalaryMonthly = wSalaryMonthly;
    }

    public String getwProfession() {
        return wProfession;
    }

    public void setwProfession(String wProfession) {
        this.wProfession = wProfession;
    }
}
