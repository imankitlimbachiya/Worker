package com.worker.app.model;

public class CardTypeModel {

    private int cardImag;
    private String cardName;
    private boolean isSelected;

    public CardTypeModel(int cardImag, String cardName) {
        this.cardImag = cardImag;
        this.cardName=cardName;
        this.isSelected=false;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getCardImag() {
        return cardImag;
    }

    public void setCardImag(int cardImag) {
        this.cardImag = cardImag;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
