package com.teboz.egg.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 简单的RecyclerView的适配器
 * Created by meijian on 2016/5/10.
 */
public abstract class SimpleRecyclerViewAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T>{

    private List<T> list;
    private int layoutId;
    private Context context;

    public SimpleRecyclerViewAdapter(List list, int layoutId, Context context) {
        this.list = list;
        this.layoutId = layoutId;
        this.context = context;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, layoutId, null);


        return buildHolder(view);
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        bindData(holder, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void remove(int position){
        list.remove(position);
        notifyDataSetChanged();
    }

    public void refresh(){

    }

    public abstract T buildHolder(View view);
    public abstract void bindData(T holder, int position);
}
