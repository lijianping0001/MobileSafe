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
public class AppLockAdapter extends RecyclerView.Adapter<AppLockAdapter.ViewHolder> {

    public interface LockListener{
        abstract void onClickLock(AppInfo info, View view);
    }

    private Context mContext;

    private List<AppInfo> appInfoList;

    private int lastPosition = -1;

    private LockListener lockListener;

    private boolean lock = false;

    public AppLockAdapter(Context context, List<AppInfo> infoList, LockListener listener, boolean isLocked){
        this.mContext = context;
        this.appInfoList = infoList;
        this.lockListener = listener;
        this.lock = isLocked;
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

        if (lock){
            holder.lock.setImageResource(R.drawable.icon_unlock);
        }else {
            holder.lock.setImageResource(R.drawable.icon_lock);
        }

        holder.lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lockListener != null){
                    lockListener.onClickLock(appInfoList.get(position), holder.layout);
                }
            }
        });

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
