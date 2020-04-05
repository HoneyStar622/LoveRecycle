package com.example.loverecycle.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HttpHelp {

    //单例模式
    private static class SingletonHolder {
        private static final HttpHelp INSTANCE = new HttpHelp();
    }

    private HttpHelp (){}

    public static final HttpHelp getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public String uploadFile(String uploadUrl,String uploadFilePath) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "---------------------------265001916915724";
        String strRes = "";
        //String boundary = "******";
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            //dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + pathOfPicture + "\"" + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
                 + uploadFilePath.substring(uploadFilePath.lastIndexOf("/") + 1) + "\"" + end + "Content-Type: image/jpeg" + end);
            dos.writeBytes(end);

            // 文件通过输入流读到Java代码中-++++++++++++++++++++++++++++++`````````````````````````
            FileInputStream fis = new FileInputStream(uploadFilePath);
            byte[] buffer = new byte[806000];
            int count = 0;
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);

            }
            fis.close();
            System.out.println("file send to server............");
            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            // 读取服务器返回结果
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();
            HashMap<String,Object> res = new Gson().fromJson(result, new TypeToken<HashMap<String,Object>>(){}.getType());
            ArrayList temp = (ArrayList) res.get("data");
            Map<String , Object> imgbody = ( Map<String , Object>) temp.get(0);
            strRes = imgbody.get("url").toString();
            Log.d("TAG", imgbody.get("url").toString());
            Log.d("uploadfile", result);
            dos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return strRes;
    }

}
