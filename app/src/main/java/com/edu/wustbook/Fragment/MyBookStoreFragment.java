package com.edu.wustbook.Fragment;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.edu.wustbook.Activity.BaseActivity;
import com.edu.wustbook.Activity.BookDetailActivity;
import com.edu.wustbook.Model.Book;
import com.edu.wustbook.Model.ItemClickListener;
import com.edu.wustbook.Model.RecyclerViewAdapter;
import com.edu.wustbook.R;
import com.edu.wustbook.Tool.HttpConnection;
import com.edu.wustbook.Tool.SearchTask;
import com.edu.wustbook.Tool.UpdateTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyBookStoreFragment extends Fragment implements ItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView booklist;

    private SwipeRefreshLayout refreshLayout;
    private Context context;
    private RecyclerViewAdapter adapter;
    private View parent;

    private List<Book> books = new ArrayList<>();

    public int changingPosition;
    public int changingState;

    private SearchTask st;
    private boolean isRefreshing = false;

    private int Uid;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpConnection.RESPONSE:
                    if (msg.arg1 == 1) {
                        boolean flag = (boolean) msg.obj;
                        if (flag) {
                            books.get(changingPosition).setState(changingState);
                            Toast.makeText(getActivity(), getString(R.string.changesuccess), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getActivity(), getString(R.string.changesuccess), Toast.LENGTH_SHORT).show();
                    } else {
                        adapter.deleteAllDate();
                        books = (List<Book>) msg.obj;
                        setData(books);
                        setRefreshing(false);
                    }
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
        refreshLayout.setOnRefreshListener(this);
    }

    private void loadData() {
        Uid = getActivity().getSharedPreferences("bookstore", 0).getInt("Uid", 0);
        st = new SearchTask(getString(R.string.bookstoreUrl) + "SearchBooksServlet", SearchTask.BOOKSTORE, handler);
        ContentValues values = new ContentValues();
        values.put("Uid", Uid);
        st.execute(values);
        setRefreshing(true);
    }

    private void setRefreshing(boolean isRefreshing) {
        this.isRefreshing = isRefreshing;
        refreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    public void onRefresh() {
        if (!isRefreshing)
            loadData();
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
        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
        intent.putExtra("type", "bookstore");
        Bundle bundle = new Bundle();
        bundle.putSerializable("book", books.get(postion));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(final View view, final int postion) {
        Book book = books.get(postion);
        if (book.getState() != Book.COMPLETE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (book.getState() == Book.SALING) {
                builder.setTitle(getString(R.string.prompt)).setMessage(getString(R.string.completemean))
                        .setPositiveButton(getString(R.string.complete), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changeState(postion, Book.COMPLETE);
                            }
                        });
            }
            if (book.getState() == Book.OUTOFTIME) {
                builder.setTitle(getString(R.string.prompt)).setMessage(getString(R.string.completemean) + "\n" + getString(R.string.salemean))
                        .setPositiveButton(getString(R.string.complete), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changeState(postion, Book.COMPLETE);
                            }
                        });
                builder.setNegativeButton(getString(R.string.sale), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeState(postion, Book.SALING);
                    }
                });
            }
            builder.create().show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.salecomplete), Toast.LENGTH_SHORT).show();
        }
    }

    public void changeState(int position, int state) {
        UpdateTask ut = new UpdateTask(getString(R.string.bookstoreUrl) + "UpdateBookServlet", handler);
        ContentValues values = new ContentValues();
        values.put("Uid", Uid);
        values.put("Bid", books.get(position).getId());
        values.put("state", state);
        ut.execute(values);
        changingPosition = position;
        changingState = state;
    }
}
