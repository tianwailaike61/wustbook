package com.edu.wustbook.Fragment;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.edu.wustbook.Model.Book;
import com.edu.wustbook.Model.ItemClickListener;
import com.edu.wustbook.Model.RecyclerViewAdapter;
import com.edu.wustbook.R;
import com.edu.wustbook.Tool.HttpConnection;
import com.edu.wustbook.Tool.SearchTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyBookStoreFragment extends Fragment implements ItemClickListener {
    private RecyclerView booklist;

    private SwipeRefreshLayout refreshLayout;
    private Context context;
    private RecyclerViewAdapter adapter;
    private View parent;

    private List<Book> books = new ArrayList<>();

    private SearchTask st;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpConnection.RESPONSE:
                    books = (List<Book>) msg.obj;
                    setData(books);
                    setRefreshing(true);
                    break;
                case HttpConnection.Exception:
                    Toast.makeText(getActivity(), getResources().getString(R.string.network_excetption), Toast.LENGTH_LONG).show();
                    break;
                case HttpConnection.NORESPONSE:
                    Toast.makeText(getActivity(), getResources().getString(R.string.network_no_response), Toast.LENGTH_LONG).show();
                    break;
                default:

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_mybookstore, container, false);
        init();
        loadData();
        return parent;
    }

    private void init() {
        context = getActivity();
        booklist = (RecyclerView) parent.findViewById(R.id.booklist);
        adapter = new RecyclerViewAdapter(context, new ArrayList<Map<String, Object>>());
        adapter.setItemClickListener(this);
        booklist.setLayoutManager(new LinearLayoutManager(getActivity()));
        booklist.setAdapter(adapter);

        refreshLayout = (SwipeRefreshLayout) parent.findViewById(R.id.refreshlayout);
    }

    private void loadData() {
        st = new SearchTask(getString(R.string.bookstoreUrl)+"SearchBooksServlet", SearchTask.BOOKSTORE, handler);
        ContentValues values=new ContentValues();
        values.put("Uid",1);
        st.execute();
        setRefreshing(true);
    }

    private void setRefreshing(boolean isRefreshing) {
        refreshLayout.setRefreshing(isRefreshing);
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
    public void onItemClick(View view, int postion) {

    }

    @Override
    public void onItemLongClick(View view, int postion) {

    }
}
