package com.edu.wustbook.Tool;


import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class BookDetailTask extends AsyncTask<ContentValues, Integer, String> {

    private String urlStr;

    private Handler handler;

    private HttpConnection httpConnection;

    public BookDetailTask(String urlStr,Handler handler) {
        this.urlStr = urlStr;
        this.handler=handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        httpConnection = new HttpConnection(urlStr);
    }

    @Override
    protected String doInBackground(ContentValues... params) {
        if (params != null)
            for (ContentValues values : params)
                httpConnection.setVales(values);
        if (httpConnection.open() != null) {
            InputStream is = httpConnection.getInputStream();
            return ConversionUtils.inputStreamToString(is);
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Message message=Message.obtain();
        message.what=httpConnection.getConnState();
        Map<String,Object>map=new HashMap<>();
        writeToFile(s);
        map.put("html",s);
        map.put("cookie",httpConnection.getCookie());
        message.obj=map;
        handler.sendMessage(message);
        httpConnection.close();
    }

    private void writeToFile(String s) {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/detail.txt";
        try {
            File file = new File(filePath);
            if (!file.exists())
                file.createNewFile();
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println(s);// 往文件里写入字符串
            ps.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
