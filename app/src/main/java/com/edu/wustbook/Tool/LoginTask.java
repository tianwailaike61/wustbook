package com.edu.wustbook.Tool;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class LoginTask extends AsyncTask<ContentValues, Integer, String> {
    private String urlStr;
    private String cookie;
    private Handler handler;

    private HttpConnection httpConnection;

    public LoginTask(String urlStr, Handler handler) {
        this.urlStr = urlStr;
        this.handler = handler;
    }

    public LoginTask(String urlStr,String cookie, Handler handler) {
        this.urlStr = urlStr;
        this.cookie = cookie;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        httpConnection = new HttpConnection(urlStr);
    }

    @Override
    protected String doInBackground(ContentValues... params) {
        httpConnection.setRequestMethod("POST");
        if (params != null)
            for (ContentValues values : params)
                httpConnection.setVales(values);
        if (cookie != null)
            httpConnection.setCookie(cookie);
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
        Message m=Message.obtain();
        Map<String ,Object> map=new HashMap<>();
        map.put("cookie",httpConnection.getCookie());
        map.put("html",s);
        //writeToFile(s);
        m.obj=map;
        m.what=httpConnection.getConnState();
        handler.sendMessage(m);
        httpConnection.close();
    }

//    private void writeToFile(String s){
//        String filePath= Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/Login.txt";
//        Log.e("jhk","file---"+filePath);
//        try {
//            File file = new File(filePath);
//            Log.e("jhk","file---"+1);
//            if(!file.exists())
//                file.createNewFile();
//            Log.e("jhk","file---"+2);
//            PrintStream ps = new PrintStream(new FileOutputStream(file));
//            Log.e("jhk","file---"+3);
//            ps.println(s);// 往文件里写入字符串
//            Log.e("jhk","file---"+4);
//            ps.close();
//            //ps.append("http://www.jb51.net");// 在已有的基础上添加字符串
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
