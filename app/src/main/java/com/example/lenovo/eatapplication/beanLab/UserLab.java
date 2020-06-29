package com.example.lenovo.eatapplication.beanLab;


import com.example.lenovo.eatapplication.model.resModel.UserResponseModel;

/**
 * Created by Lenovo on 2020/6/12.
 */
public class UserLab {
    private static UserLab ourInstance;
    private UserResponseModel mUser;

    public static UserLab getInstance() {
        if (ourInstance == null){
            ourInstance = new UserLab();
        }
        return ourInstance;
    }

    private UserLab() {
    }

    public UserResponseModel getUser() {
        return mUser;
    }

    public void setUser(UserResponseModel user) {
        mUser = user;
    }
}
