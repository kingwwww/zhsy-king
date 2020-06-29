package com.example.lenovo.eatapplication.model.resModel;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;

import java.util.List;

public class StoreResponseModel {
    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String phone;
    @Expose
    private String password;
    @Expose
    private String description;
    @Expose
    private String desVideo;
    @Expose
    private String portrait;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDesVideo() {
        return desVideo;
    }

    public void setDesVideo(String desVideo) {
        this.desVideo = desVideo;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
