package com.jianping.lee.mobilesafe.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.greendao.LockedApp;
import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.model.CacheInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Li on 2016/12/19.
 */
public class CacheDetailAdapter extends BaseExpandableListAdapter {


    private Context mContext;

    private List<List<CacheInfo>> itemList;

    private List<Map<String, Object>> groupList;

    private int lastPosition = -1;


    public CacheDetailAdapter(Context context, List<Map<String, Object>> groupList, List<List<CacheInfo>> itemList){
        this.mContext = context;
        this.groupList = groupList;
        this.itemList = itemList;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itemList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_item_cache_group, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_cache_group);
            holder.groupDesc = (TextView) convertView.findViewById(R.id.tv_item_cache_group_desc);
            holder.totalSize = (TextView) convertView.findViewById(R.id.tv_item_cache_group_size);
            holder.selected = (CheckBox) convertView.findViewById(R.id.cb_item_cache_group_check);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (isExpanded){
            holder.imageView.setImageResource(R.drawable.icon_array_down);
        }else {
            holder.imageView.setImageResource(R.drawable.icon_array);
        }

        holder.groupDesc.setText(groupList.get(groupPosition).get("desc").toString());
        holder.totalSize.setText(Formatter.formatFileSize(mContext, Long.parseLong(groupList.get(groupPosition).get("size").toString())));
        holder.selected.setChecked(Boolean.valueOf(groupList.get(groupPosition).get("checked").toString()));

        holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectAllChild(groupPosition, isChecked);
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_item_cache, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_cache);
            holder.groupDesc = (TextView) convertView.findViewById(R.id.tv_item_cache_name);
            holder.installed = (TextView) convertView.findViewById(R.id.tv_item_cache_subtext);
            holder.totalSize = (TextView) convertView.findViewById(R.id.tv_item_cache_size);
            holder.selected = (CheckBox) convertView.findViewById(R.id.cb_item_cache);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        CacheInfo cacheInfo = itemList.get(groupPosition).get(childPosition);
        holder.imageView.setImageDrawable(cacheInfo.getIcon());
        holder.groupDesc.setText(cacheInfo.getAppName());
        if (cacheInfo.isInstalled()){
            holder.installed.setText("已安装");
        }else {
            holder.installed.setText("未安装");
        }
        holder.totalSize.setText(Formatter.formatFileSize(mContext, cacheInfo.getCacheSize()));
        holder.selected.setChecked(cacheInfo.isSelected());

        holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectOneChild(groupPosition, childPosition, isChecked);
            }
        });

        return convertView;
    }

    private void selectOneChild(int groupPosition, int childPosition, boolean isChecked) {
        //获取总大小
        long totalSize = Long.parseLong(groupList.get(groupPosition).get("size").toString());
        CacheInfo cacheInfo = itemList.get(groupPosition).get(childPosition);
        if (isChecked){
            totalSize += cacheInfo.getCacheSize();
            cacheInfo.setSelected(true);
        }else {
            totalSize -= cacheInfo.getCacheSize();
            cacheInfo.setSelected(false);
        }
        //更新总大小
        Map<String ,Object> group1 = groupList.get(groupPosition);
        group1.put("size", totalSize + "");
        notifyDataSetChanged();
    }


    private void selectAllChild(int position, boolean isChecked){
        int totalSize = 0;
        if (isChecked){
            for (CacheInfo cacheInfo : itemList.get(position)){
                cacheInfo.setSelected(true);
                totalSize += cacheInfo.getCacheSize();
            }

            Map<String ,Object> group1 = groupList.get(position);
            group1.put("size", totalSize + "");
            group1.put("checked", "true");
        }else {
            for (CacheInfo cacheInfo : itemList.get(position)){
                cacheInfo.setSelected(false);
                totalSize += cacheInfo.getCacheSize();
            }

            Map<String ,Object> group1 = groupList.get(position);
            group1.put("size", 0 + "");
            group1.put("checked", "false");
        }

        notifyDataSetChanged();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    static class ViewHolder{
        public ImageView imageView;
        public TextView groupDesc;
        public TextView totalSize;
        public TextView installed;
        public CheckBox selected;
    }

}
