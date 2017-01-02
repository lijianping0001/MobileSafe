package com.jianping.lee.mobilesafe.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Li on 2016/12/29.
 */
public class AppSettingAdapter extends RecyclerView.Adapter<AppSettingAdapter.ViewHolder> {

    private List<Info> mDataList;

    private Context mContext;

    private SettingItemClickListener mListener;

    public interface SettingItemClickListener{
        public void onItemClick(View view, int position);
    }

    public AppSettingAdapter(@NonNull Context context){
        this.mContext = context;
        mDataList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_setting, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Info info = mDataList.get(position);

        holder.icon.setImageResource(info.icon);
        holder.appName.setText(info.value);
        if (info.showArray){
            holder.array.setVisibility(View.VISIBLE);
        }else {
            holder.array.setVisibility(View.GONE);
        }

        if (info.showSwitch){
            holder.mSwitch.setVisibility(View.VISIBLE);
            holder.mSwitch.setChecked(info.checked);
        }else {
            holder.mSwitch.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void loadData(){
        mDataList.clear();
        Info check = new Info(R.drawable.icon_check_update, "检查更新");
        check.showArray = true;
        mDataList.add(check);

        Info blackNum = new Info(R.drawable.icon_setting_user, "黑名单拦截");
        blackNum.showSwitch = true;
        blackNum.checked = true;
        mDataList.add(blackNum);

        Info location = new Info(R.drawable.icon_setting_location, "显示电话号码归属地");
        location.showSwitch = true;
        location.checked = true;
        mDataList.add(location);

        Info about = new Info(R.drawable.icon_setting_about, "关于");
        about.showArray = true;
        mDataList.add(about);

    }

    public void setOnItemClickListener(SettingItemClickListener listener){
        this.mListener = listener;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.iv_item_app_setting)
        ImageView icon;

        @InjectView(R.id.tv_item_app_setting)
        TextView appName;

        @InjectView(R.id.rl_item_app_setting)
        RelativeLayout layout;

        @InjectView(R.id.iv_item_app_setting_array)
        ImageView array;

        @InjectView(R.id.sw_item_app_setting_switch)
        Switch mSwitch;

        private SettingItemClickListener mListener;

        public ViewHolder(View itemView, SettingItemClickListener mListener) {
            super(itemView);
            this.mListener = mListener;
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null){
                mListener.onItemClick(v, getPosition());
            }
        }
    }

    private static class Info{
        int icon;
        String value = "";
        boolean showArray;
        boolean showSwitch;
        boolean checked;

        public Info(){

        }

        public Info(int icon, String value){
            this.icon = icon;
            this.value = value;
        }
    }
}
