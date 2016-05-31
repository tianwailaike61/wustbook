package com.edu.wustbook.Tool;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by PC on 2016/5/25.
 */
public class UpdateTask extends AsyncTask<ContentValues, Integer, Boolean> {
    private String urlStr;
    private String cookie;

    private Handler handler;

    private HttpConnection httpConnection;

    public UpdateTask(String url, Handler handler) {
        this.urlStr = url;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        httpConnection = new HttpConnection(urlStr);
    }

    @Override
    protected Boolean doInBackground(ContentValues... params) {
        if (params != null) {
            for (ContentValues values : params)
                httpConnection.setVales(values);
        }
        if (cookie != null)
            httpConnection.setCookie(cookie);
        if (httpConnection.open() != null) {
            String s = ConversionUtils.inputStreamToString(httpConnection.getInputStream());
            if ("fail".equals(s))
                return false;
            if ("success".equals(s))
                return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Message message = Message.obtain();
        message.what = httpConnection.getConnState();
        message.obj = aBoolean;
        message.arg1=1;
        handler.sendMessage(message);
        httpConnection.close();
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
