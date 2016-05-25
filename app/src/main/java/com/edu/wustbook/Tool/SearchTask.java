package com.edu.wustbook.Tool;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.edu.wustbook.Model.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SearchTask extends AsyncTask<ContentValues, Integer, List<Book>> {

    public static final int LIBARY = 1;
    public static final int MYLIBARY = 2;
    public static final int BOOKSTORE = 3;
    public static final int PUSH = 4;

    private String urlStr;
    private String cookie;
    private int Type;

    private Handler handler;


    private HttpConnection httpConnection;


    public SearchTask(String url, int Type, Handler handler) {
        this.urlStr = url;
        this.Type = Type;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        httpConnection = new HttpConnection(urlStr);
    }

    @Override
    protected void onPostExecute(List<Book> books) {
        super.onPostExecute(books);
        Message message = Message.obtain();
        message.what = httpConnection.getConnState();
        message.obj = books;
        handler.sendMessage(message);
        httpConnection.close();
    }

    @Override
    protected List<Book> doInBackground(ContentValues... params) {
        if (params != null) {
            for (ContentValues values : params)
                httpConnection.setVales(values);
        }
        if (cookie != null)
            httpConnection.setCookie(cookie);
        if (httpConnection.open() != null) {
//            String html;
//            if (Type != PUSH || Type==BOOKSTORE)
//                html = ConversionUtils.inputStreamToString(httpConnection.getInputStream());
//            else
//                html = ConversionUtils.inputStreamToString(httpConnection.getInputStream(), "GB2312");
            // httpConnection.close();
            if (Type == LIBARY)
                return libarySearch();
            if (Type == BOOKSTORE)
                return bookStoreSearch();
            if (Type == MYLIBARY)
                return myLibarySearch();
            if (Type == PUSH)
                return pushSearch();
            return null;
        } else {
            return null;
        }
    }

    private List<Book> pushSearch() {
        String html = ConversionUtils.inputStreamToString(httpConnection.getInputStream(), "gb2312");
        List<Book> books = null;
        if (html != null) {
            books = new ArrayList<>();
            Document document = Jsoup.parse(html);
            Elements elements = document.select("table#GridView3 a[href]");

            for (int i = 0; i < 4; i++) {
                int k = (int) (Math.random() * 100);
                try {
                    Element element = elements.get(k);
                    if (element == elements.first() || element == elements.last()) {
                        i -= 2;
                        continue;
                    }
                    Book book = new Book();
                    book.setName(element.text().trim());
                    book.setUrl(element.attr("href").trim());
                    book.setType(Book.LIBARY);
                    books.add(book);
                } catch (IndexOutOfBoundsException e) {
                    i -= 2;
                }
            }
        }
        return books;
    }


    private List<Book> bookStoreSearch() {
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "UTF-8"));
//            StringBuffer html = new StringBuffer();
//            String s = null;
//            while ((s = bufferedReader.readLine()) != null) {
//                html.append(s);
//            }
            String html =ConversionUtils.inputStreamToString(httpConnection.getInputStream(),"gb2312");
            return JsonUtils.getBooks(html.toString());
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    private void writeToFile(String s) {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Search.txt";
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

    private List<Book> myLibarySearch() {
        String html = ConversionUtils.inputStreamToString(httpConnection.getInputStream(), "UTF-8");
        List<Book> books = null;
        if (html != null) {
            books = new ArrayList<>();
            Document document = Jsoup.parse(html);
            Elements elements = document.select("tr");
            for (Element element : elements) {
                Book book = new Book();
                Elements elements1 = element.select("td");
                book.setName(elements1.get(2).text().trim());
                book.setUrl(elements1.get(2).select("a[href]").attr("href").trim());
                book.setAuthor(elements1.get(3).text().trim());
                book.setCallNo(elements1.get(1).text().trim());
                book.setType(Book.LIBARY);
                books.add(book);
            }
            books.remove(0);
        }
        return books;
    }

    private List<Book> libarySearch() {
        String html = ConversionUtils.inputStreamToString(httpConnection.getInputStream(), "UTF-8");
        List<Book> books = null;
        if (html != null) {
            books = new ArrayList<Book>();
            Document document = Jsoup.parse(html);
            Elements elements = document.select("li.book_list_info");
            for (Element element : elements) {
                Elements elements1 = element.select("h3");

                Book book = new Book();
                String bookName = elements1.select("a").text().trim();
                book.setName(bookName);
                String docType = elements1.select("span").text().trim();
                book.setDocType(docType);
                book.setCallNo(elements1.text().trim().replace(bookName, "").replace(docType, "").trim());
                book.setUrl(elements1.select("a[href]").attr("href").trim());
                elements1.clear();
                String[] ss = element.select("p").text().trim().split(" ");
                book.setAuthor(ss[3]);
                book.setPublisher(ss[4]);
                book.setType(Book.LIBARY);
                books.add(book);
            }
        }
        return books;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
