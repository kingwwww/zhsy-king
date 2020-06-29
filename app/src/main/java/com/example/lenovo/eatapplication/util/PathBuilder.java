package com.example.lenovo.eatapplication.util;

public class PathBuilder {

    public static String buildPath(String fileName){
           int code1 = fileName.hashCode();
           int d1 = code1 & 0xf;
           int code2 = d1 >>> 4;
           int d2 = code2 & 0xf;
           return d1+"/"+d2;
    }
}
