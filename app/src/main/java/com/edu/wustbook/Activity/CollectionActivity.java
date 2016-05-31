package com.edu.wustbook.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.edu.wustbook.Model.Book;
import com.edu.wustbook.Model.BookDBManager;
import com.edu.wustbook.Model.DragListener;
import com.edu.wustbook.Model.ItemClickListener;
import com.edu.wustbook.Model.ItemTouchHelperCallback;
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

    private boolean isLoadingMore = false;


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
            map.put("bookname", book.getName());
            map.put("bookauthor", book.getAuthor());
            map.put("bookprice", book.getPrice());
            data.add(map);
        }
        adapter = new RecyclerViewAdapter(context, data);
        adapter.setItemClickListener(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        booklist.setHasFixedSize(true);
        booklist.setItemAnimator(new DefaultItemAnimator());
        booklist.setLayoutManager(layoutManager);
        booklist.setAdapter(adapter);

        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getPosition();
                BookDBManager.getInstance(context).delete(books.get(position).getId());
                adapter.deleteDate(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) /
                            (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(booklist);

        booklist.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                if (lastVisibleItem >= totalItemCount - 2 && dy > 0) {
                    if (!isLoadingMore) {
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore() {

    }

    @Override
    public void onItemClick(View view, int postion) {
        Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
        Book book = books.get(postion);
        Bundle bundle = new Bundle();
        bundle.putSerializable("book", book);
        intent.putExtras(bundle);
        intent.putExtra("collected", true);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int postion) {

    }
}
