package com.jianping.lee.mobilesafe.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @fileName: ToastUtils
 * @Author: Li Jianping
 * @Date: 2016/8/10 16:24
 * @Description:
 */
public class ToastUtils {
    /**
     * 显示吐司
     * @param context 上下文
     * @param msg 消息
     */
    public static void showToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示吐司
     * @param context 上下文
     * @param msg 消息
     * @param gravity 位置
     */
    public static void showToast(Context context, String msg, int gravity){
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }

    /**
     * 显示吐司，带图片
     * @param context 上下文
     * @param drawableId 图片资源
     * @param msg 消息
     */
    public static void showToast(Context context, int drawableId, String msg){
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout layout = (LinearLayout) toast.getView();
        layout.setOrientation(LinearLayout.VERTICAL);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(drawableId);
//        layout.addView(imageView, DensityUtils.dp2px(context, 30), DensityUtils.dp2px(context, 30));
        layout.addView(imageView, 0);
        toast.show();
    }
}
