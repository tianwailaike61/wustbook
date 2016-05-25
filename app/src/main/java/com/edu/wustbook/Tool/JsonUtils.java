package com.edu.wustbook.Tool;

import android.util.Log;

import com.edu.wustbook.Model.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 2016/5/24.
 */
public class JsonUtils {
    public static int getUid(String jsonStr){
        JSONObject jsonObj = null;
        //jsonObj = JSONObject.fromObject(jsonStr);
        try {
            jsonObj = new JSONObject(jsonStr);
            return jsonObj.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static Book getBook(String jsonStr) {
        Book book = new Book();
        // 将json字符串转换为json对象
        JSONObject jsonObj = null;
        //jsonObj = JSONObject.fromObject(jsonStr);
        try {
            jsonObj = new JSONObject(jsonStr);
            return getBook(jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    public static List<Book> getBooks(String jsonStr) {
//        List<Book> books = new ArrayList<>();
//        // 将json字符串转换为json对象
//        String str = "{" + jsonStr + "}";
//        Log.e("jhk", "--" + str);
//        JSONObject jsonObj = JSONObject.fromObject(str);
//        // 得到指定json key对象的value对象
//        JSONArray personList = jsonObj.getJSONArray("");
//        // 遍历jsonArray
//        for (int i = 0; i < personList.size(); i++) {
//            // 获取每一个json对象
//            JSONObject jsonItem = personList.getJSONObject(i);
//            Book book = getBook(jsonItem);
//            if (book != null)
//                books.add(book);
//        }
//        return books;
//    }
    public static List<Book> getBooks(String jsonStr) {
        List<Book> books = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                books.add(getBook(jsonObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }

    private static Book getBook(JSONObject jsonObj) {
        Book book = new Book();
        JSONObject bookObj = jsonObj;
        try {
           // bookObj = jsonObj.getJSONObject("");
            // 获取之对象的所有属性
            book.setId(bookObj.getInt("id"));
            book.setName(bookObj.getString("name"));
            book.setType(book.BOOKSTORE);
            book.setState(bookObj.getInt("state"));
            book.setPrice(bookObj.getInt("price"));
            book.setAuthor(bookObj.getString("author"));
            book.setSaler(bookObj.getString("saler"));
            book.setQq(bookObj.getString("qq"));
            book.setPhoneNumber(bookObj.getString("phoneNumber"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return book;
    }
}
