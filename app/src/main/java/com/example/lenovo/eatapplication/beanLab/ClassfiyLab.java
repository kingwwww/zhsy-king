package com.example.lenovo.eatapplication.beanLab;

import com.example.lenovo.eatapplication.model.resModel.ClassfiyResponseModel;
import com.example.lenovo.eatapplication.model.resModel.UserResponseModel;

import java.util.List;

/**
 * Created by Lenovo on 2020/6/14.
 */

public class ClassfiyLab {
    private static ClassfiyLab ourInstance;
    private List<ClassfiyResponseModel> mClassfiyResponseModels;

    public static ClassfiyLab getInstance() {
        if (ourInstance == null){
            ourInstance = new ClassfiyLab();
        }
        return ourInstance;
    }

    private ClassfiyLab() {
    }

    public List<ClassfiyResponseModel> getClassfiyResponseModels() {
        return mClassfiyResponseModels;
    }

    public void setClassfiyResponseModels(List<ClassfiyResponseModel> classfiyResponseModels) {
        mClassfiyResponseModels = classfiyResponseModels;
    }
}
