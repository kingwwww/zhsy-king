package com.example.lenovo.eatapplication.model.resModel;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;

public class UserResponseModel {

    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String portrait;
    @Expose
    private String phone;
    @Expose
    private String password;
    @Expose
    private Bitmap mBitmap;


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

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}
