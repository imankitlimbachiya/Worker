package com.worker.app.model;

public class RequestWorkerModel {
    String Name;
    String Nationality, NationalityArabic;
    String Category, CategoryArabic;
    String Contractfees;
    String RequestOrderID;
    String Subcat, SubcatArabic;
    String OrderID;
    String WorkerID;

    public String getCategoryArabic() {
        return CategoryArabic;
    }

    public void setCategoryArabic(String categoryArabic) {
        CategoryArabic = categoryArabic;
    }

    public String getSubcatArabic() {
        return SubcatArabic;
    }

    public void setSubcatArabic(String subcatArabic) {
        SubcatArabic = subcatArabic;
    }

    public String getNationalityArabic() {
        return NationalityArabic;
    }

    public void setNationalityArabic(String nationalityArabic) {
        NationalityArabic = nationalityArabic;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getSubcat() {
        return Subcat;
    }

    public void setSubcat(String subcat) {
        Subcat = subcat;
    }

    public String getContractfees() {
        return Contractfees;
    }

    public void setContractfees(String contractfees) {
        Contractfees = contractfees;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNationality() {
        return Nationality;
    }

    public void setNationality(String nationality) {
        Nationality = nationality;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getRequestOrderID() {
        return RequestOrderID;
    }

    public void setRequestOrderID(String requestOrderID) {
        RequestOrderID = requestOrderID;
    }

    public String getWorkerID() {
        return WorkerID;
    }

    public void setWorkerID(String workerID) {
        WorkerID = workerID;
    }
}
