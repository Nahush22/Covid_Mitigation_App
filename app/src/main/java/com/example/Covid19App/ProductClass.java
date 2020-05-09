package com.example.Covid19App;

public class ProductClass {

    public String name;
    public float price;
    public String units;
    public float quantity;

    public ProductClass(String name, float price, String units, float quantity) {
        this.name = name;
        this.price = price;
        this.units = units;
        this.quantity = quantity;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setProductName(String name) {
        this.name = name;
    }

    public void setUnitType(String units) {
        this.units = units;
    }

    public void setUnits(float quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public String getProductName() {
        return name;
    }

    public String getUnitType() {
        return units;
    }

    public float getUnits() {
        return quantity;
    }

}
