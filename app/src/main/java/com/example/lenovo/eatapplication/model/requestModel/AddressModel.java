package com.example.lenovo.eatapplication.model.requestModel;

import com.google.gson.annotations.Expose;

public class AddressModel {
    @Expose
    private String addressId;
    @Expose
    private String userId;
    @Expose
    private String name;
    @Expose
    private String phone;
    @Expose
    private String location;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
}
