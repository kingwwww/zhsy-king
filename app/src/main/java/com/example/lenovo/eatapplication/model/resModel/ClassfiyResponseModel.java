package com.example.lenovo.eatapplication.model.resModel;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ClassfiyResponseModel {
    @Expose
    private String classfiyId;
    @Expose
    private String name;
    @Expose
    private int index;
    @Expose
    private List<ProductResponseModel> productResponseModels;

    public String getClassfiyId() {
        return classfiyId;
    }

    public void setClassfiyId(String classfiyId) {
        this.classfiyId = classfiyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductResponseModel> getProductResponseModels() {
        return productResponseModels;
    }

    public void setProductResponseModels(List<ProductResponseModel> productResponseModels) {
        this.productResponseModels = productResponseModels;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
