package com.worker.app.model;

public class WalletModel {

    String UserWalletID, OrderID, Description, Type, Date, Amount, TrType;

    public String getUserWalletID() {
        return UserWalletID;
    }

    public void setUserWalletID(String userWalletID) {
        UserWalletID = userWalletID;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getTrType() {
        return TrType;
    }

    public void setTrType(String trType) {
        TrType = trType;
    }
}
