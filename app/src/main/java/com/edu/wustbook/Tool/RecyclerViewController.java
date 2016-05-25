package com.edu.wustbook.Tool;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class RecyclerViewController implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private SrollListener listener;

    private boolean isRefreshing, isLoadingMore;

    public interface SrollListener {
        void onPullUp();

        void onPullDown();

    }

    public RecyclerViewController(RecyclerView recyclerView, SwipeRefreshLayout swipeRefreshLayout) {
        this.recyclerView = recyclerView;
        this.swipeRefreshLayout = swipeRefreshLayout;
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                //lastVisibleItem>=totalItemCount-2 剩下2个item自动加载
                //dy>0 向下滑动
                if (lastVisibleItem >= totalItemCount - 2 && dy > 0) {
                    if (!isLoadingMore && listener != null)
                        listener.onPullUp();
                }
            }
        });
    }

    public void setLoadingMore(boolean loadingMore) {
        isLoadingMore = loadingMore;
    }

    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
    }

    public void setListener(SrollListener listener) {
        this.listener = listener;
    }

    @Override
    public void onRefresh() {
        if (!isRefreshing && listener != null)
            listener.onPullDown();
    }
}
