package com.edu.wustbook.Tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ConversionUtils {

//    public static String inputStreamToString(InputStream is, String charsetName) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        int i = -1;
//        try {
//            while ((i = is.read()) != -1) {
//                baos.write(i);
//            }
//            return new String(baos.toByteArray(), charsetName);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static String inputStreamToString(InputStream is, String charsetName) {
        StringBuffer sb = new StringBuffer();
       // InputStream is = null;
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(is, charsetName);
            int len = 0;
            char[] buf = new char[1024];
            while ((len = isr.read(buf)) != -1) {
                sb.append(new String(buf, 0, len));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String inputStreamToString(InputStream is) {
        return inputStreamToString(is, "UTF-8");
    }
}
