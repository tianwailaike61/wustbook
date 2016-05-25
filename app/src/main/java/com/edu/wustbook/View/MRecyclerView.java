package com.edu.wustbook.View;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

public class MRecyclerView extends SwipeRefreshLayout{
    private Context context;
    private RecyclerView recyclerView;
    public MRecyclerView(Context context) {
        super(context);
        this.context=context;
    }
}
