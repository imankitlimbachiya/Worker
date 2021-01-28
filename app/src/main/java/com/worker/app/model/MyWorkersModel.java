package com.worker.app.model;

public class MyWorkersModel {

    String WorkerID, WorkerImage, WorkerName, ContactFees, OrderID, Status, Timeline, userId,
           isComplete, orderStatus, MaxStatus, TotalStatus, CompletedSatus;

    public String getCompletedSatus() {
        return CompletedSatus;
    }

    public void setCompletedSatus(String completedSatus) {
        CompletedSatus = completedSatus;
    }

    public String getTotalStatus() {
        return TotalStatus;
    }

    public void setTotalStatus(String totalStatus) {
        TotalStatus = totalStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(String isComplete) {
        this.isComplete = isComplete;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkerID() {
        return WorkerID;
    }

    public void setWorkerID(String workerID) {
        WorkerID = workerID;
    }

    public String getWorkerImage() {
        return WorkerImage;
    }

    public void setWorkerImage(String workerImage) {
        WorkerImage = workerImage;
    }

    public String getWorkerName() {
        return WorkerName;
    }

    public void setWorkerName(String workerName) {
        WorkerName = workerName;
    }

    public String getContactFees() {
        return ContactFees;
    }

    public void setContactFees(String contactFees) {
        ContactFees = contactFees;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTimeline() {
        return Timeline;
    }

    public void setTimeline(String timeline) {
        Timeline = timeline;
    }

    public String getMaxStatus() {
        return MaxStatus;
    }

    public void setMaxStatus(String maxStatus) {
        MaxStatus = maxStatus;
    }
}





