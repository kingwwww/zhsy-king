package com.example.lenovo.eatapplication.model.resModel;

import com.google.gson.annotations.Expose;

public class AddressResponseModel {
    @Expose
    private String addressId;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
