package com.worker.app.model;

public class WorkerDetailModel {

    String requestId;
    String requstDate;
    String nationality;
    String catagory;
    String requestedWorker;
    String totalMatcgWorker;
    String status;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequstDate() {
        return requstDate;
    }

    public void setRequstDate(String requstDate) {
        this.requstDate = requstDate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    public String getRequestedWorker() {
        return requestedWorker;
    }

    public void setRequestedWorker(String requestedWorker) {
        this.requestedWorker = requestedWorker;
    }


    public String getTotalMatcgWorker() {
        return totalMatcgWorker;
    }

    public void setTotalMatcgWorker(String tatalMatcgWorker) {
        this.totalMatcgWorker = tatalMatcgWorker;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
