package com.edu.wustbook.Model;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
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
    private int openedPosition = -1;

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
        MyViewHolder holder = (MyViewHolder) viewHolder;
        Map<String, Object> map = data.get(position);
        Drawable drawable = (Drawable) map.get("bookcover");
        if (drawable != null)
            holder.bookcover.setBackground(drawable);
        holder.bookname.setText(map.get("bookname").toString());
        holder.bookauthor.setText(map.get("bookauthor").toString());
        Object o = map.get("bookprice");
        if (o != null && !"".equals(o.toString()))
            holder.bookprice.setText(map.get("bookprice").toString());
        else
            holder.bookprice.setVisibility(View.INVISIBLE);
//        if (openedPosition == position) {
//            holder.item.setVisibility(View.VISIBLE);
//            SetState(map.get("flag").toString(),holder);
//            AddListener(holder,position);
//        } else {
//            holder.item.setVisibility(View.GONE);
//        }
    }


//    private void SetState(String s,MyViewHolder holder) {
//        holder.button[0].setText(listenerManager.LOOK);
//        holder.button[1].setText(listenerManager.COLLECT);
//        holder.button[2].setText(listenerManager.BUY);
//        switch (s) {
//            case "libary":
//                holder.bookcover.setVisibility(View.GONE);
//                break;
//            case "bookstore":
//                holder.button[2].setVisibility(View.VISIBLE);
//                holder.item.setWeightSum(3);
//                break;
//            case "libary_collection":
//                holder.button[1].setText(listenerManager.DELETE);
//                break;
//            case "bookstore_collection":
//                holder.button[1].setText(listenerManager.DELETE);
//                holder.button[2].setVisibility(View.VISIBLE);
//                holder.item.setWeightSum(3);
//                break;
//            case "mybookstore":
//                holder.button[1].setText(listenerManager.COMPLETE);
//                break;
//        }
//    }

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

//    private void AddListener(MyViewHolder holder,int position) {
//        for (int i = 0; i < holder.item.getWeightSum(); i++) {
//            String type = holder.button[i].getText().toString();
//            holder.button[i].setOnClickListener(listenerManager.getListeners(
//                    type, position, this, context));
//        }
//    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //@BindView(values = R.id.bookcover)
        public AppCompatImageView bookcover;
        //@BindView(values = {R.id.bookname, R.id.bookauthor, R.id.bookprice})
        public AppCompatTextView bookname, bookauthor, bookprice;
        // public LinearLayout item;
//        public MButton[] button;


        public MyViewHolder(View itemView) {
            super(itemView);
           // ViewUtils.autoInjectAllFiled(itemView);
            bookcover= (AppCompatImageView) itemView.findViewById(R.id.bookcover);
            bookname= (AppCompatTextView) itemView.findViewById(R.id.bookname);
            bookauthor= (AppCompatTextView) itemView.findViewById(R.id.bookauthor);
            bookprice= (AppCompatTextView) itemView.findViewById(R.id.bookprice);
//            item = (LinearLayout) itemView.findViewById(R.id.item);
//            button = new MButton[3];
//            button[0] = (MButton) item.findViewById(R.id.button1);
//            button[1] = (MButton) item.findViewById(R.id.button2);
//            button[2] = (MButton) item.findViewById(R.id.button3);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onItemClick(v, getPosition());
                }
            });
        }

    }


}
