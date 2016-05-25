package com.edu.wustbook.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBhelper extends SQLiteOpenHelper {

    private final static int VERSION = 1;
    private final static String NAME = "wustbook.db";

    private String booktable = "book";


    public DBhelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + booktable + " (id INTEGER primary key autoincrement, name varchar(255), " +
                "author varchar(255),publisher varchar(255),url varchar(255),type INTEGER,callNo varchar(255),docType varchar(100)," +
                "price float,icoPath varchar(255),state INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + booktable;
        db.execSQL(sql);
        onCreate(db);
    }
}
