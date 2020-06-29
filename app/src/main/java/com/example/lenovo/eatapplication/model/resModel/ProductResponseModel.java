package com.example.lenovo.eatapplication.model.resModel;


import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;



public class ProductResponseModel {
    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private Double price;
    @Expose
    private Integer stock;
    @Expose
    private String detail;
    @Expose
    private String picture;
    @Expose
    private Bitmap bitmap;
    @Expose
    private int number;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
