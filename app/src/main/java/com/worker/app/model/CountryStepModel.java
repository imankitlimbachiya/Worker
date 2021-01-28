package com.worker.app.model;

public class CountryStepModel {

    String CountryId, CountryName, CountryIsoCode, CountryIsdCode, CountryNameArabic, CountryFlag;

    public CountryStepModel(String CountryId, String CountryName, int CountryIsoCode, String CountryIsdCode, String CountryNameArabic, int CountryFlag) {
        this.CountryId = CountryId;
        this.CountryName = CountryName;
        this.CountryIsdCode = CountryIsdCode;
        this.CountryNameArabic = CountryNameArabic;
    }

    public String getCountryId() {
        return CountryId;
    }

    public void setCountryId(String countryId) {
        CountryId = countryId;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getCountryIsoCode() {
        return CountryIsoCode;
    }

    public void setCountryIsoCode(String countryIsoCode) {
        CountryIsoCode = countryIsoCode;
    }

    public String getCountryIsdCode() {
        return CountryIsdCode;
    }

    public void setCountryIsdCode(String countryIsdCode) {
        CountryIsdCode = countryIsdCode;
    }

    public String getCountryNameArabic() {
        return CountryNameArabic;
    }

    public void setCountryNameArabic(String countryNameArabic) {
        CountryNameArabic = countryNameArabic;
    }

    public String getCountryFlag() {
        return CountryFlag;
    }

    public void setCountryFlag(String countryFlag) {
        CountryFlag = countryFlag;
    }
}
