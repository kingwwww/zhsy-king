package com.example.lenovo.eatapplication.beanLab;

import com.example.lenovo.eatapplication.model.resModel.StoreResponseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2020/6/12.
 */
public class StoreLab {

    private static StoreLab ourInstance;
    private List<StoreResponseModel> mStoreResponseModels;
    private StoreResponseModel mStoreResponseModel;

    public static StoreLab getInstance() {
        if (ourInstance == null){
            ourInstance = new StoreLab();
        }
        return ourInstance;
    }

    private StoreLab() {
        mStoreResponseModels = new ArrayList<>();
    }

    public StoreResponseModel getStoreResponseModel() {
        return mStoreResponseModel;
    }

    public void setStoreResponseModel(StoreResponseModel storeResponseModel) {
        mStoreResponseModel = storeResponseModel;
    }
}
