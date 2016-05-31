package com.edu.wustbook.Fragment;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
        ItemClickListener, RecyclerViewController.SrollListener, View.OnClickListener {
    private RecyclerView booklist;
    private SwipeRefreshLayout refreshLayout;
    private EditText searchStr;
    private AppCompatImageButton back, clear;

    private Context context;

    private int state = NORMAL;
    private final static int NORMAL = 1;
    private final static int SEARCH = 0;

    private RecyclerViewAdapter adapter;

    private RecyclerViewController controller;

    private View parent;

    private SearchTask st;

    private List<Book> books = new ArrayList<>();
    private List<Book> searchBooks = new ArrayList<>();

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (state == NORMAL)
                changeState();
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null && !TextUtils.isEmpty(s)) {
                loadMoreData();
                searchBooks.clear();
                adapter.deleteAllDate();
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            controller.setRefreshing(false);
            switch (msg.what) {
                case HttpConnection.RESPONSE:
                    if (state == SEARCH) {
                        searchBooks = (List<Book>) msg.obj;
                    } else {
                        books = (List<Book>) msg.obj;
                    }
                    setData((List<Book>) msg.obj);
                    break;
                case HttpConnection.Exception:
                    if (BookStoreFragment.this.isAdded())
                        Toast.makeText(getActivity(), getResources().getString(R.string.network_excetption), Toast.LENGTH_SHORT).show();
                    break;
                case HttpConnection.NORESPONSE:
                    if (BookStoreFragment.this.isAdded())
                        Toast.makeText(getActivity(), getResources().getString(R.string.network_no_response), Toast.LENGTH_SHORT).show();
                    break;
                default:

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_bookstore, container, false);
        init();
        loadMoreData();
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

        searchStr = (EditText) parent.findViewById(R.id.searchtext);
        searchStr.addTextChangedListener(textWatcher);
        back = (AppCompatImageButton) parent.findViewById(R.id.back);
        back.setOnClickListener(this);
        clear = (AppCompatImageButton) parent.findViewById(R.id.clear_input);
        clear.setOnClickListener(this);
    }

    private void loadMoreData() {
        st = new SearchTask(getString(R.string.bookstoreUrl) + "SearchBooksServlet", SearchTask.BOOKSTORE, handler);
        if (state == NORMAL)
            st.execute();
        if (state == SEARCH) {
            ContentValues values = new ContentValues();
            values.put("Bname", searchStr.getText().toString());
            st.execute();
        }
        controller.setRefreshing(true);
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


    @Override
    public void onPullUp() {

    }

    @Override
    public void onPullDown() {
        if (state == NORMAL) {
            books.clear();
            adapter.deleteAllDate();
            loadMoreData();
        } else {
            if (!TextUtils.isEmpty(searchStr.getText())) {
                books.clear();
                adapter.deleteAllDate();
                loadMoreData();
            } else
                controller.setRefreshing(false);
        }
    }

    private void clearSearch() {
        searchStr.setText("");
        searchBooks.clear();
        adapter.deleteAllDate();
    }

    private void changeState() {
        if (state == NORMAL) {
            state = SEARCH;
            books.clear();
            searchBooks.clear();
            adapter.deleteAllDate();
        } else {
            state = NORMAL;
            books.clear();
            adapter.deleteAllDate();
            loadMoreData();
        }
    }

    @Override
    public void onClick(View v) {
        if (state == SEARCH) {
            if (st != null && st.getStatus() == AsyncTask.Status.RUNNING)
                st.cancel(true);
            switch (v.getId()) {
                case R.id.back:
                    searchStr.setText("");
                    changeState();
                    break;
                case R.id.clear_input:
                    clearSearch();
                    break;
                default:
            }
        }
    }

    @Override
    public void onItemClick(View view, int postion) {
        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
        intent.putExtra("type", "bookstore");
        Bundle bundle = new Bundle();
        bundle.putSerializable("book", books.get(postion));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int postion) {

    }

}
