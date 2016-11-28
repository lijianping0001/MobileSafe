package com.jianping.lee.mobilesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.model.Icon;
import com.jianping.lee.mobilesafe.utils.DensityUtils;
import com.jianping.lee.mobilesafe.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * Created by Li on 2016/11/28.
 */
public class IconAdapter extends BaseAdapter {

    private ArrayList<Icon> dataList;

    private LayoutInflater inflater;

    public IconAdapter(Context context, ArrayList<Icon> dataList){
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_item_function, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_function_image);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_item_function_text);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setBackgroundResource(dataList.get(position).getBackgroundId());
        holder.imageView.setImageResource(dataList.get(position).getImageId());
        holder.textView.setText(dataList.get(position).getName());
        return convertView;
    }

    private static class ViewHolder{
        public ImageView imageView;
        public TextView textView;
    }
}
