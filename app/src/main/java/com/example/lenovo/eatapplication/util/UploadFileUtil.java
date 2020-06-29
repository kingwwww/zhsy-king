package com.example.lenovo.eatapplication.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.lenovo.eatapplication.model.requestModel.EnterModel;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadFileUtil {

    public static final String BOUNDARY = "--------WEBRIANSXUANFROMDATA";
    public static final String MYURL = "http://112.124.13.208:8080/Eat_war/api/file/upload";
    public static String fileName = "";
    public static final String name = "file";

    public synchronized void  uoloadFile(final File uploadFile,final int fileTag, final String subFileUrl, final String uniqueStr, final Handler handler){

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    URL url = new URL(MYURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    int intFlag = (int)(Math.random() * 1000000);
                    if (fileTag == EnterModel.PICTURE){
                        fileName = "brians"+intFlag+".jpg";
                        sb.append("Content-Type: image/jpeg" + "\r\n");
                    }else if (fileTag == EnterModel.VIDEO){
                        fileName = "brians"+intFlag+".mp4";
                        sb.append("Content-Type: video/mpeg4" + "\r\n");
                    }
                    sb.append("--" + BOUNDARY + "\r\n");
                    sb.append("Content-Disposition: form-data; name=" + name + "; filename=" + fileName + "\r\n");
                    sb.append("\r\n");

                    byte[] headerInfo = sb.toString().getBytes("UTF-8");
                    byte[] endInfo = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");

                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    //模仿表单
                    conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
                    conn.setRequestProperty("tag",String.valueOf(fileTag));
                    conn.setRequestProperty("subFileUrl",subFileUrl);
                    conn.setRequestProperty("uniqueStr",uniqueStr);

                    //数据长度
                    conn.setRequestProperty("Content-Length", String.valueOf(uploadFile
                            .length() + headerInfo.length + endInfo.length));
                    //通过conn拿到服务器的字节输出流
                    OutputStream out = conn.getOutputStream();
                    //需要上传的文件封装成字节输入流
                    InputStream in = new FileInputStream(uploadFile);
                    out.write(headerInfo);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) != -1)
                        out.write(buf, 0, len);
                    out.write(endInfo);
                    in.close();
                    out.close();
                    if (conn.getResponseCode() == 200) {
                        Log.i("Upload Message", "SUCCESS");
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }

}
