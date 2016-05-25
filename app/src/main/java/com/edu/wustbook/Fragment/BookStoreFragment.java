package com.edu.wustbook.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.edu.wustbook.Activity.BookDetailActivity;
import com.edu.wustbook.Model.Book;
import com.edu.wustbook.Model.ItemClickListener;
import com.edu.wustbook.Model.RecyclerViewAdapter;
import com.edu.wustbook.R;
import com.edu.wustbook.Tool.HttpConnection;
import com.edu.wustbook.Tool.RecyclerViewController;
import com.edu.wustbook.Tool.SearchTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookStoreFragment extends Fragment implements
        ItemClickListener, RecyclerViewController.SrollListener {
    private RecyclerView booklist;
    private SwipeRefreshLayout refreshLayout;
    private Context context;

    private RecyclerViewAdapter adapter;

    private RecyclerViewController controller;

    private View parent;

    private SearchTask st;

    private List<Book> books = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpConnection.RESPONSE:
                    books = (List<Book>) msg.obj;
                    setData(books);
                    setRefreshing(false);
                    break;
                case HttpConnection.Exception:
                    setRefreshing(false);
                    Toast.makeText(getActivity(), getResources().getString(R.string.network_excetption), Toast.LENGTH_LONG).show();
                    break;
                case HttpConnection.NORESPONSE:
                    setRefreshing(false);
                    Toast.makeText(getActivity(), getResources().getString(R.string.network_no_response), Toast.LENGTH_LONG).show();
                    break;
                default:

            }
        }
    };

    public interface MenuButtonClick extends View.OnClickListener {
        @Override
        void onClick(View v);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_bookstore, container, false);
        init();
        loadData();
        return parent;
    }

    private void init() {
        context = getActivity();
        adapter = new RecyclerViewAdapter(context, new ArrayList<Map<String, Object>>());
        adapter.setItemClickListener(this);

        booklist = (RecyclerView) parent.findViewById(R.id.booklist);
        booklist.setLayoutManager(new LinearLayoutManager(getActivity()));
        booklist.setAdapter(adapter);

        refreshLayout = (SwipeRefreshLayout) parent.findViewById(R.id.refreshlayout);
        controller = new RecyclerViewController(booklist, refreshLayout);
        controller.setListener(this);
    }

    private void loadData() {
        st = new SearchTask(getString(R.string.bookstoreUrl) + "SearchBooksServlet", SearchTask.BOOKSTORE, handler);
        st.execute();
        setRefreshing(true);
    }

    private void setData(List<Book> newBooks) {
        if (newBooks != null && newBooks.size() != 0) {
            List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
            for (Book b : newBooks) {
                Map map = new HashMap<String, Object>();
                map.put("bookname", b.getName());
                map.put("bookauthor", b.getAuthor());
                map.put("flag", "bookstore");
                map.put("price", b.getPrice());
                maps.add(map);
            }
            adapter.insertData(maps);
        }
        books.addAll(newBooks);
    }

    private void setRefreshing(boolean isRefreshing) {
        refreshLayout.setRefreshing(isRefreshing);
        if (!isRefreshing)
            refreshLayout.setEnabled(isRefreshing);
    }

    private void loadMore(List<Book> newBooks) {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (Book book : newBooks) {
            Map<String, Object> map = new HashMap<String, Object>();
            //map.put("bookcover", IMGUtils.getPicture(context, R.drawable.ic_menu_share));
            map.put("bookname", "书名:" + book.getName());
            map.put("bookauthor", "出售者:" + book.getSaler());
            map.put("bookprice", "售价:" + book.getPrice());
            map.put("flag", "bookstore");
            data.add(map);
        }
        adapter.insertData(data);
        books.addAll(newBooks);
    }

    @Override
    public void onPullUp() {

    }

    @Override
    public void onPullDown() {

    }

    @Override
    public void onItemClick(View view, int postion) {
        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
        intent.putExtra("type", "bookstore");
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int postion) {

    }

}
