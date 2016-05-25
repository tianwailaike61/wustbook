package com.edu.wustbook.Tool;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PictureTask extends AsyncTask<ContentValues, Integer, Bitmap> {
	private String urlStr;

	private String cookie;

	private Handler handler;

	private HttpConnection httpConnection;

	public PictureTask(String urlStr,Handler handler) {
		this.urlStr = urlStr;
		this.handler=handler;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		httpConnection = new HttpConnection(urlStr);
	}

	@Override
	protected Bitmap doInBackground(ContentValues... params) {
		if (params != null)
			for (ContentValues values : params)
				httpConnection.setVales(values);
		if(cookie!=null){
			httpConnection.setCookie(cookie);
		}
		if(httpConnection.open()!=null){
			InputStream is=httpConnection.getInputStream();
			return IMGUtils.bytesToBimap(is);
		}else{
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		super.onPostExecute(bitmap);
		Message message=Message.obtain();

		Map<String,Object> map=new HashMap<>();
		map.put("cookie",httpConnection.getCookie());
		map.put("IMG",bitmap);
		message.what=httpConnection.getConnState();
		message.obj=map;
		message.arg1=1;
		handler.sendMessage(message);
		httpConnection.close();
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

}
