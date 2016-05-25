package com.edu.wustbook.Model;


import android.view.View;

public interface ItemClickListener {

    /**
     * Item 普通点击
     */

    public void onItemClick(View view, int postion);

    /**
     * Item 内部View点击
     */

    public void onItemLongClick(View view, int postion);
}
