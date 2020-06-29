package com.example.lenovo.eatapplication.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.lenovo.eatapplication.model.resModel.ResponseModel;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lenovo on 2020/6/11.
 */

public class HttpConnectionUtil {

    public synchronized void  getConnection(final String jsonData, final String urlPath, final Handler handler, final Message message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlPath);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setRequestProperty("charset","UTF-8");
                    connection.setRequestProperty("Content-Type","application/json");
                    DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                    os.write(jsonData.getBytes());
                    os.flush();
                    os.close();
                    if (connection.getResponseCode() == connection.HTTP_OK){
                        InputStream inputStream = connection.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] b = new byte[1024];
                        int len = -1;
                        while ((len = inputStream.read(b))!= -1){
                            baos.write(b, 0, len);
                        }
                        Gson gson = GsonBuilder.newInstance();
                        Bundle bundle = new Bundle();
                        //接收服务器返回的json数据
                        String resJsonData = new String(baos.toByteArray());
                        // 拆分出对象数据json
                        Object dataJson = gson.fromJson(resJsonData, Map.class).get("result");
                        // 拆分返回码
                        ResponseModel model = gson.fromJson(resJsonData, ResponseModel.class);
                        bundle.putInt("code",model.getCode());
                        bundle.putString("dataJson",gson.toJson(dataJson));
                        message.setData(bundle);
                        handler.sendMessage(message);
                        inputStream.close();
                        baos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
