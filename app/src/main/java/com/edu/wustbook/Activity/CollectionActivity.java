package com.edu.wustbook.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.edu.wustbook.Model.Book;
import com.edu.wustbook.Model.BookDBManager;
import com.edu.wustbook.Model.ItemClickListener;
import com.edu.wustbook.Model.RecyclerViewAdapter;
import com.edu.wustbook.R;
import com.edu.wustbook.Tool.BindView;
import com.edu.wustbook.Tool.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionActivity extends AppCompatActivity implements ItemClickListener {
    @BindView(values = R.id.booklist)
    private RecyclerView booklist;

    private Context context;

    private RecyclerViewAdapter adapter;

    private List<Book> books;

    private boolean isLoadingMore=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        context = CollectionActivity.this;
        ViewUtils.autoInjectAllFiled(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        books = BookDBManager.getInstance(context).search();
        for (Book book : books) {
            Map<String, Object> map = new HashMap<>();
            map = new HashMap<String, Object>();
            //map.put("bookcover", IMGUtils.getPicture(context, R.drawable.gaoshu2));
            map.put("bookname", "name:" + book.getName());
            map.put("bookauthor", "author:" + book.getAuthor());
            map.put("bookprice", "");
            map.put("flag", "libary");
            data.add(map);
        }
        adapter = new RecyclerViewAdapter(context, data);
        adapter.setItemClickListener(this);
        final LinearLayoutManager layoutManager= new LinearLayoutManager(getApplicationContext());
        booklist.setLayoutManager(layoutManager);
        booklist.setAdapter(adapter);

        booklist.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastVisibleItem=layoutManager.findLastVisibleItemPosition();
                int totalItemCount=layoutManager.getItemCount();
                if(lastVisibleItem>=totalItemCount-2 && dy>0){
                    if(!isLoadingMore){
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore(){

    }

    @Override
    public void onItemClick(View view, int postion) {
        Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
        Book book = books.get(postion);
        if (book.getType() == Book.LIBARY) {
            intent.putExtra("type", "libary");
            intent.putExtra("url", books.get(postion).getUrl());
        } else if (book.getType() == Book.BOOKSTORE) {
            intent.putExtra("type", "bookstore");
            //intent.putExtra("url", getString(R.string.libaryBaseUrl) + books.get(postion).getUrl());
        }
        intent.putExtra("collect", true);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int postion) {

    }
}
