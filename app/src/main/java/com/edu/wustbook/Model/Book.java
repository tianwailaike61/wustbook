package com.edu.wustbook.Model;

import android.content.ContentValues;

public class Book {
    private int id;
    private int type = LIBARY;
    private int state = -1;

    private String author;
    private String name;
    private String publisher; // 出版信息

    private String saler;
    private String qq;
    private String phoneNumber;
    private float price = -1;


    public static final int LIBARY = 1;
    public static final int BOOKSTORE = 2;

    public static final int UNABLE_TO_BORROW = 0;
    public static final int ABLE_TO_BORROW = 1;

    public static final int SALING = 2;
    public static final int COMPLETE = 3;


    private String callNo; // 索书号
    private String docType; // 文献类型

    private String url;

    private String iconPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCallNo() {
        return callNo;
    }

    public void setCallNo(String callNo) {
        this.callNo = callNo;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void setSaler(String saler) {
        this.saler = saler;
    }

    public String getSaler() {
        return saler;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("type", type);
        if (price > 0)
            values.put("price", price);
        if (state >= 0)
            values.put("state", state);
        if (name != null)
            values.put("name", name);
        if (author != null)
            values.put("author", author);
        if (publisher != null)
            values.put("publisher", publisher);
        if (callNo != null)
            values.put("callNo", callNo);
        if (url != null)
            values.put("url", url);
        if (iconPath != null)
            values.put("iconPath", iconPath);
        if(docType !=null)
            values.put("docType", docType);
        return values;
    }
}
