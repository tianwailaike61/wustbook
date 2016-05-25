package com.edu.wustbook.Tool;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.util.Log;

public class HttpConnection {
	public static final int RESPONSE=200;
	public static final int Exception=0;
	public static final int NORESPONSE=-200;
	public int ConnState=RESPONSE;

	private String urlStr;
	private ContentValues vales;

	private String RequestMethod="GET";

	private String cookie;
	private InputStream in;

	private HttpURLConnection conn;

	public HttpConnection() {
		urlStr=null;
		this.vales=null;
	}

	public HttpConnection(String urlStr) {
		this.urlStr = urlStr;
		this.vales=null;
	}

	public HttpConnection(URL url) {
		this.urlStr = url.getPath();
		this.vales=null;
	}
	
	public HttpConnection(String urlStr,ContentValues vales) {
		this.urlStr = urlStr;
		this.vales=vales;
	}

	public HttpConnection(URL url,ContentValues vales) {
		this.urlStr = url.getPath();
		this.vales=vales;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public void setRequestMethod(String requestMethod) {
		RequestMethod = requestMethod;
	}

	public String getUrlStr() {
		return urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}

	public ContentValues getVales() {
		return vales;
	}

	public void setVales(ContentValues vales) {
		this.vales = vales;
	}

	public HttpConnection open() {
		ConnState=RESPONSE;
		if (vales != null) {
			urlStr += getParam();
		}
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(RequestMethod);
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setRequestProperty("Content-type", "application/x-java-serialized-object");
			conn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
			if(cookie!=null){
				conn.setRequestProperty("Cookie",cookie);
			}
			conn.connect();
			if(conn.getResponseCode()==200) {
				cookie=conn.getHeaderField("Set-Cookie");
				in = conn.getInputStream();
				return this;
			}else{
				ConnState=NORESPONSE;
			}
		} catch (MalformedURLException e) {
			ConnState=Exception;
			e.printStackTrace();

		} catch (IOException e) {
			ConnState=Exception;
			e.printStackTrace();
		}
		return null;
	}

	public void close() {
		if (conn != null) {
			conn.disconnect();
			conn = null;
		}
	}

	public int getConnState(){
		return ConnState;
	}

	private String getParam() {
		StringBuffer sb = new StringBuffer("?");
		for (Entry<String, Object> item : vales.valueSet()) {
			sb.append(item.getKey() + "=" + item.getValue() + "&");
		}
		sb.replace(sb.length() - 1, sb.length(), "");
		return sb.toString();
	}

	public InputStream getInputStream() {
		return in;
	}
}