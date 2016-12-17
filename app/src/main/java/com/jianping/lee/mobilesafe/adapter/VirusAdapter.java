package com.jianping.lee.mobilesafe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.model.AppInfo;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Li on 2016/12/10.
 */
public class VirusAdapter extends RecyclerView.Adapter<VirusAdapter.ViewHolder> {

    private Context mContext;

    private List<AppInfo> appInfoList;

    private int lastPosition = -1;

    public VirusAdapter(Context context, List<AppInfo> infoList){
        this.mContext = context;
        this.appInfoList = infoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_app_lock, parent, false);
        return new ViewHolder(view);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R
                    .anim.item_bottom_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position >= appInfoList.size()){
            return;
        }
        AppInfo appInfo = appInfoList.get(position);

        holder.icon.setImageDrawable(appInfo.getIcon());
        holder.appName.setText(appInfo.getAppName());

        if (appInfoList.get(position).isUserApp()){
            holder.lock.setImageResource(R.drawable.icon_right);
        }else {
            holder.lock.setImageResource(R.drawable.icon_error);
        }

        setAnimation(holder.layout, position);
    }

    @Override
    public int getItemCount() {
        return appInfoList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.iv_item_app_lock)
        ImageView icon;

        @InjectView(R.id.tv_item_app_lock_name)
        TextView appName;

        @InjectView(R.id.rl_item_app_lock)
        RelativeLayout layout;

        @InjectView(R.id.iv_item_app_lock_lock)
        ImageView lock;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
