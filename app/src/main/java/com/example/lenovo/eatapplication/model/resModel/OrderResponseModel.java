package com.example.lenovo.eatapplication.model.resModel;

import com.google.gson.annotations.Expose;

import java.util.List;

public class OrderResponseModel {
    @Expose
    private String id;
    @Expose
    private String storeName;
    @Expose
    private String storePhone;
    @Expose
    private String name;
    @Expose
    private String phone;
    @Expose
    private String address;
    @Expose
    private Integer status;
    @Expose
    private Double total;
    @Expose
    List<OrderDetailResponseModel> orderDetailResponseModels;

    public List<OrderDetailResponseModel> getOrderDetailResponseModels() {
        return orderDetailResponseModels;
    }

    public void setOrderDetailResponseModels(List<OrderDetailResponseModel> orderDetailResponseModels) {
        this.orderDetailResponseModels = orderDetailResponseModels;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }
}
