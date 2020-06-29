package com.example.lenovo.eatapplication.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.example.lenovo.eatapplication.model.requestModel.EnterModel;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class DownloadFileUtil {

    public synchronized void  downloadFile(final List<String> filePaths, final List<Bitmap> mBitmaps, final Handler handler, final Message message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String basePath = "http://112.124.13.208:8080/media/pic/";
                try {
                    for (int i = 0; i < filePaths.size(); i++) {
                        String realPath = basePath + filePaths.get(i);
                        URL url = new URL(realPath);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);
                        connection.setRequestProperty("charset", "UTF-8");
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            InputStream inputStream = connection.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            mBitmaps.add(bitmap);
                        }
                    }
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
