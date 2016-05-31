package com.edu.wustbook.Model;

import android.support.v7.widget.RecyclerView;

/**
 * Created by PC on 2016/5/28.
 */
public interface DragListener {
    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}
