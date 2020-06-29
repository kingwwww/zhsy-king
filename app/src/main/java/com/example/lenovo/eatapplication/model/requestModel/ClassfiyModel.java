package com.example.lenovo.eatapplication.model.requestModel;

import com.google.gson.annotations.Expose;

public class ClassfiyModel {
    @Expose
    private String classfiyId;
    @Expose
    private String storeId;
    @Expose
    private String name;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassfiyId() {
        return classfiyId;
    }

    public void setClassfiyId(String classfiyId) {
        this.classfiyId = classfiyId;
    }
}
