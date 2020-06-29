package com.example.lenovo.eatapplication.beanLab;

import com.example.lenovo.eatapplication.model.resModel.ClassfiyResponseModel;
import com.example.lenovo.eatapplication.model.resModel.ProductResponseModel;

import java.util.List;

/**
 * Created by Lenovo on 2020/6/18.
 */
public class ProductLab {
    private static ProductLab ourInstance;
    private List<ProductResponseModel> mProductResponseModels;

    public static ProductLab getInstance() {
        if (ourInstance == null){
            ourInstance = new ProductLab();
        }
        return ourInstance;
    }

    private ProductLab() {
    }

    public List<ProductResponseModel> getProductResponseModels() {
        return mProductResponseModels;
    }

    public void setProductResponseModels(List<ProductResponseModel> productResponseModels) {
        mProductResponseModels = productResponseModels;
    }
}
