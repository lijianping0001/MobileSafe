package com.jianping.lee.mobilesafe.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.greendao.BlackNum;
import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.db.MyBlackNumDao;
import com.jianping.lee.mobilesafe.utils.LogUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Li on 2016/12/11.
 */
public class BlackNumAdapter extends RecyclerView.Adapter<BlackNumAdapter.ViewHolder> {

    private List<BlackNum> dataList;
    private Context mContext;
    private View emptyLayout;

    public BlackNumAdapter(Context context, View emptyLayout){
        this.mContext = context;
        this.emptyLayout = emptyLayout;
    }

    /**
     * 加载数据
     */
    public void loadData(){
        dataList = MyBlackNumDao.getInstance(mContext).getData();
        notifyDataChanged();
    }

    private void notifyDataChanged(){
        notifyDataSetChanged();
        if (dataList.size() == 0){
            emptyLayout.setVisibility(View.VISIBLE);
        }else {
            emptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_black_num, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BlackNum blackNum = dataList.get(position);
        if (TextUtils.isEmpty(blackNum.getName())){
            holder.name.setText("陌生人");
        }else {
            holder.name.setText(blackNum.getName());
        }

        holder.phoneNum.setText(blackNum.getPhone());

        switch (blackNum.getMode()){
            case 1://电话拦截
                holder.mode.setText(mContext.getString(R.string.blacknum_mode_phone));
                break;
            case 2://短信拦截
                holder.mode.setText(mContext.getString(R.string.blacknum_mode_text));
                break;
            case 3:
                holder.mode.setText(mContext.getString(R.string.blacknum_mode_all));
                break;
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("警告");
                builder.setMessage("确定删除这个黑名单号码？");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteData(position);
                    }
                });
                builder.show();
            }
        });
    }

    private void deleteData(int position) {
        MyBlackNumDao.getInstance(mContext).deleteData(dataList.get(position));
        dataList.remove(position);
        notifyDataChanged();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.tv_item_black_num_name)
        TextView name;

        @InjectView(R.id.tv_item_black_num_phone)
        TextView phoneNum;

        @InjectView(R.id.tv_item_black_num_hint)
        TextView mode;

        @InjectView(R.id.iv_item_black_num)
        ImageView delete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
