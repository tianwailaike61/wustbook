package com.edu.wustbook.Model;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.wustbook.R;

import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements IAdapter<Map<String, Object>> {

    private List<Map<String, Object>> data;
    private Context context;
    private LayoutInflater mInflater;

    private ItemClickListener itemClickListener;

    public RecyclerViewAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_recyclerview, viewGroup,
                false));
        return holder;

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MyViewHolder holder = (MyViewHolder) viewHolder;
        Map<String, Object> map = data.get(position);
        Drawable drawable = (Drawable) map.get("bookcover");
        if (drawable != null)
            holder.bookcover.setBackground(drawable);
//        else
//            holder.bookcover.setVisibility(View.GONE);
        holder.bookname.setText(map.get("bookname").toString());
        Object o = map.get("bookauthor");
        if (o != null && !"".equals(o.toString()))
            holder.bookauthor.setText(map.get("bookauthor").toString());
        o = map.get("price");
        if (o != null && !TextUtils.isEmpty(o.toString())) {
            String price=o.toString();
            holder.bookprice.setText(o.toString());
        }
    }

    @Override
    public List<Map<String, Object>> getDatas() {
        return data;
    }

    public Map<String, Object> getData(int position) {
        return data.get(position);
    }

    @Override
    public void updateData(int position, Map<String, Object> stringObjectMap) {
        data.add(position, stringObjectMap);
        notifyItemChanged(position);
    }

    @Override
    public void insertData(int position, Map<String, Object> stringObjectMap) {
        data.add(position, stringObjectMap);
        notifyItemInserted(position);
    }

    @Override
    public void insertData(List<Map<String, Object>> maps) {
        data.addAll(maps);
        notifyDataSetChanged();
    }

    public void deleteDate(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void deleteAllDate() {
        data.removeAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView bookcover;
        public AppCompatTextView bookname, bookauthor, bookprice;


        public MyViewHolder(View itemView) {
            super(itemView);
            bookcover = (AppCompatImageView) itemView.findViewById(R.id.bookcover);
            bookname = (AppCompatTextView) itemView.findViewById(R.id.bookname);
            bookauthor = (AppCompatTextView) itemView.findViewById(R.id.bookauthor);
            bookprice = (AppCompatTextView) itemView.findViewById(R.id.bookprice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onItemClick(v, getPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onItemLongClick(v, getPosition());
                    return true;
                }
            });
        }
    }


}
