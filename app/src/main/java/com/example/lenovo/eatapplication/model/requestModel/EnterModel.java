package com.example.lenovo.eatapplication.model.requestModel;

import com.google.gson.annotations.Expose;

public class EnterModel {

    public static final int TAG_USER = 0;
    public static final int TAG_STORE = 1;

    public static final int PICTURE = 0;
    public static final int VIDEO = 1;

    public static final String DEFAULT_USER_PORTRAIT = "userDefault.jpg";
    public static final String DEFAULT_STORE_PORTRAIT = "storeDefault.jpg";

    public static final String DEFAULT_STORE_DESVIDEO = "defaultvideo.mp4";

    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String phone;
    @Expose
    private String password;
    @Expose
    private String portrait;
    @Expose
    private String desVideo;
    @Expose
    private String description;
    @Expose
    private Integer tag;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public String getDesVideo() {
        return desVideo;
    }

    public void setDesVideo(String desVideo) {
        this.desVideo = desVideo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
