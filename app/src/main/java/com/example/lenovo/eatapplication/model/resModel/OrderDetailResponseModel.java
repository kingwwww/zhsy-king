package com.example.lenovo.eatapplication.model.resModel;

import com.google.gson.annotations.Expose;

public class OrderDetailResponseModel {
    @Expose
    private String productId;
    @Expose
    private String name;
    @Expose
    private Double price;
    @Expose
    private String picture;
    @Expose
    private Integer number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
