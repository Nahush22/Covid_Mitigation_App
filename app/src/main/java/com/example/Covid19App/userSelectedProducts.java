package com.example.Covid19App;

public class userSelectedProducts {

    int position;
    String prodName;
    float userUnits;
    float userPrice;
    String unitType;

    public userSelectedProducts(int position, String prodName, float userUnits, float userPrice, String unitType) {
        this.position = position;
        this.prodName = prodName;
        this.userUnits = userUnits;
        this.userPrice = userPrice;
        this.unitType = unitType;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public float getUserUnits() {
        return userUnits;
    }

    public void setUserUnits(float userUnits) {
        this.userUnits = userUnits;
    }

    public float getUserPrice() {
        return userPrice;
    }

    public void setUserPrice(float userPrice) {
        this.userPrice = userPrice;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }
}
