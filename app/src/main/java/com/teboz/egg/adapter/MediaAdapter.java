package com.teboz.egg.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teboz.egg.R;

import java.util.List;

public class MediaAdapter extends BaseAdapter {

    private List<String> list;
    private LayoutInflater mInflater;
    private Context context;
    private int checkedPosition = -1;
    private boolean isAnim = false;

    public MediaAdapter(Context ctx, List<String> list) {
        super();
        this.context = ctx;
        this.list = list;
        this.mInflater = LayoutInflater.from(ctx);
    }

    public void setCheckedPosition(int n) {
        checkedPosition = n;
    }

    public void setIsAnim(boolean b) {
        isAnim = b;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list != null ? list.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean isAnimruning = false;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_songlist, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == checkedPosition) {//
            if (isAnim) {
                holder.name_tv.setTextColor(Color.GRAY);
                /*if (!isAnimruning)*/ {
                    isAnimruning = true;
                    holder.play_iv.setVisibility(View.VISIBLE);
                    holder.play_iv.setImageResource(R.drawable.points_anim);
                    AnimationDrawable animationDrawable = (AnimationDrawable) holder.play_iv.getDrawable();
                    animationDrawable.start();
                }
            } else {
//                isAnimruning = false;
                holder.name_tv.setTextColor(context.getResources().getColor(R.color.content_textcolor));
                holder.play_iv.clearAnimation();
                holder.play_iv.setVisibility(View.INVISIBLE);
            }

        } else {
            holder.name_tv.setTextColor(context.getResources().getColor(R.color.content_textcolor));
            holder.play_iv.clearAnimation();
            holder.play_iv.setVisibility(View.INVISIBLE);
        }
        holder.name_tv.setText(list.get(position));


        return convertView;
    }


    class ViewHolder {
        TextView name_tv;
        ImageView play_iv;

        ViewHolder(View v) {
            name_tv = (TextView) v.findViewById(R.id.name_tv);
            play_iv = (ImageView) v.findViewById(R.id.play_iv);
        }

    }


}
