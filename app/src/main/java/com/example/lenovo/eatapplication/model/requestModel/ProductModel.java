package com.example.lenovo.eatapplication.model.requestModel;

import com.google.gson.annotations.Expose;

public class ProductModel {
    @Expose
    private String productId;
    @Expose
    private String name;
    @Expose
    private Double price;
    @Expose
    private Integer stock;
    @Expose
    private String detail;
    @Expose
    private String classfiyId;
    @Expose
    private String picture;
    @Expose
    private Integer number;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getClassfiyId() {
        return classfiyId;
    }

    public void setClassfiyId(String classfiyId) {
        this.classfiyId = classfiyId;
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
}
