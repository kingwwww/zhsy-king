package com.example.lenovo.eatapplication.util;

import com.google.gson.Gson;

public class GsonBuilder {
    private static Gson gson;
    public GsonBuilder(){

    }
    public static Gson newInstance(){
        if (gson == null){
            gson = new Gson();
        }
        return gson;
    }
}
