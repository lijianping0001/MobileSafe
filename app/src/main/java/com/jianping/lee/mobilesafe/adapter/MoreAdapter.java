package com.jianping.lee.mobilesafe.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Li on 2016/12/29.
 */
public class MoreAdapter extends RecyclerView.Adapter<MoreAdapter.ViewHolder> {

    private List<Info> mDataList;

    private Context mContext;

    private MoreItemClickListener mListener;

    public interface MoreItemClickListener{
        public void onItemClick(View view, int position);
    }

    public MoreAdapter(@NonNull Context context){
        this.mContext = context;
        mDataList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_more, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Info info = mDataList.get(position);

        holder.icon.setImageResource(info.icon);
        holder.appName.setText(info.value);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void loadData(){
        mDataList.clear();
        Info feedBack = new Info(R.drawable.icon_feedback, "问题反馈");
        mDataList.add(feedBack);

        Info app = new Info(R.drawable.icon_app, "精品应用");
        mDataList.add(app);

        Info share = new Info(R.drawable.icon_share, "推荐给朋友");
        mDataList.add(share);

        Info setting = new Info(R.drawable.icon_setting, "设置");
        mDataList.add(setting);

    }

    public void setOnItemClickListener(MoreItemClickListener listener){
        this.mListener = listener;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.iv_item_app_more)
        ImageView icon;

        @InjectView(R.id.tv_item_app_more)
        TextView appName;

        @InjectView(R.id.rl_item_app_more)
        RelativeLayout layout;

        private MoreItemClickListener mListener;

        public ViewHolder(View itemView, MoreItemClickListener mListener) {
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

        public Info(){

        }

        public Info(int icon, String value){
            this.icon = icon;
            this.value = value;
        }
    }
}
