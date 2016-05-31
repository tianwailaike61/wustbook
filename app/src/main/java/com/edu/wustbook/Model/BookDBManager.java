package com.edu.wustbook.Model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookDBManager {
    private DBhelper helper;
    private final String table = "book";

    private static BookDBManager manager;

    private BookDBManager(Context context) {
        helper = new DBhelper(context);
    }

    public static BookDBManager getInstance(Context cotext) {
        if (manager == null)
            manager = new BookDBManager(cotext);
        return manager;
    }

    public boolean insert(Book book) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long result = -1;
        if (db != null) {
            result = db.insert(table, null, book.toContentValues());
            db.close();
        }
//        StringBuilder keys=new StringBuilder(),values=new StringBuilder();
//        for(Map.Entry<String, Object> item:book.toContentValues().valueSet()){
//            keys.append(item.getKey()+",");
//            Object o=item.getValue();
//            if(o instanceof  String)
//                values.append("'"+item.getValue()+"',");
//            else
//                values.append(item.getValue()+",");
//        }
//        keys.delete(keys.length()-1,keys.length());
//        values.delete(values.length()-1,values.length());
//        String sql="insert into book("+keys+") values("+values+")";
//        Log.e("jhk","===="+sql);
//        db.execSQL(sql);

        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean Update(Book book) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(book.getId())};
        int result = db.update(table, book.toContentValues(), whereClause, whereArgs);
        db.close();
        if (result == 0)
            return false;
        else
            return true;
    }

    public Book searchById(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        StringBuilder sql = new StringBuilder("select * from book where id=" + id);
        Cursor c = db.rawQuery(sql.toString(), null);
        if (c == null) {
            db.close();
            return null;
        } else {
            while (c.moveToNext()) {
                return getBook(c);
            }
            c.close();
            db.close();
            return null;
        }
    }

    public List<Book> search(Book book) {
        SQLiteDatabase db = helper.getReadableDatabase();
        StringBuilder sql = new StringBuilder("select * from book where ");
        String s = getParam(book);
        if (TextUtils.isEmpty(s))
            sql.delete(sql.length() - 6, sql.length());
        sql.append(s);
        Log.e("jhk","----"+sql);
        Cursor c = db.rawQuery(sql.toString(), null);
        if (c == null) {
            db.close();
            return null;
        } else {
            List<Book> books = new ArrayList<>();
            while (c.moveToNext()) {
                Book book1 = getBook(c);
                books.add(book1);
            }
            c.close();
            db.close();
            return books;
        }
    }

    private String getParam(Book book) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> item : book.toContentValues().valueSet()) {
            Object valueObj = item.getValue();
            if (valueObj instanceof String)
                sb.append(item.getKey() + "='" + valueObj + "' and ");
            else {
                String value = valueObj.toString().replace("'", "");
                sb.append(item.getKey() + "=" + valueObj + " and ");
            }
        }
        if (!TextUtils.isEmpty(sb.toString()))
            sb.delete(sb.length() - 4, sb.length());
        return sb.toString();
    }


    public List<Book> search(String selection) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] selectionArgs = {String.valueOf("a")};
        Cursor c = db.query(table, null, selection, selectionArgs, null, null, null, null);
        if (c == null) {
            db.close();
            return null;
        } else {
            List<Book> books = new ArrayList<>();
            while (c.moveToNext()) {
                Book book = getBook(c);
                books.add(book);
            }
            c.close();
            db.close();
            return books;
        }
    }

    public List<Book> search() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(table, null, null, null, null, null, null, null);
        if (c == null) {
            db.close();
            return null;
        }
        while (c.moveToNext()) {
            Book book = getBook(c);
            books.add(book);
        }
        c.close();
        db.close();
        return books;
    }

    public boolean delete(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(id)};
        int result = db.delete(table, whereClause, whereArgs);
        db.close();
        if (result == 0)
            return false;
        else
            return true;
    }

    public boolean delete(Book book) {
        Book b = searchById(book.getId());
        boolean flag = false;
        flag = delete(b.getId());
        return flag;
    }

    private Book getBook(Cursor c) {
        Book book = new Book();
        book.setId(c.getInt(c.getColumnIndex("id")));
        String s = c.getString(c.getColumnIndex("name"));
        if (s != null && !"".equals(s))
            book.setName(s);
        s = c.getString(c.getColumnIndex("author"));
        if (s != null && !"".equals(s))
            book.setAuthor(s);
        s = c.getString(c.getColumnIndex("url"));
        if (s != null && !"".equals(s))
            book.setUrl(s);
        book.setType(c.getInt(c.getColumnIndex("type")));
        s = c.getString(c.getColumnIndex("publisher"));
        if (s != null && !"".equals(s))
            book.setPublisher(s);
        book.setType(c.getInt(c.getColumnIndex("type")));
        s = c.getString(c.getColumnIndex("callNo"));
        if (s != null && !"".equals(s))
            book.setCallNo(s);
        s = c.getString(c.getColumnIndex("docType"));
        if (s != null && !"".equals(s))
            book.setDocType(s);
        book.setPrice(c.getFloat(c.getColumnIndex("price")));
        s = c.getString(c.getColumnIndex("icoPath"));
        if (s != null && !"".equals(s))
            book.setIconPath(s);

        s = c.getString(c.getColumnIndex("saler"));
        if (s != null && !"".equals(s))
            book.setSaler(s);
        s = c.getString(c.getColumnIndex("phoneNumber"));
        if (s != null && !"".equals(s))
            book.setPhoneNumber(s);
        s = c.getString(c.getColumnIndex("qq"));
        if (s != null && !"".equals(s))
            book.setQq(s);

        book.setState(c.getInt(c.getColumnIndex("state")));

        return book;
    }
}
