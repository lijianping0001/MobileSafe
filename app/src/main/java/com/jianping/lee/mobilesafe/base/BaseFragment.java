package com.jianping.lee.mobilesafe.base;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;


import com.jianping.lee.mobilesafe.utils.ToastUtils;

import java.lang.ref.WeakReference;

/**
 * @fileName: BaseFragment
 * @Author: Li Jianping
 * @Date: 2016/8/18 18:24
 * @Description:
 */
public class BaseFragment extends Fragment {

    protected void handleMessage(Message msg){

    }

    protected static class MyHandler extends Handler {

        private final WeakReference<BaseFragment> mFragment;

        public MyHandler(BaseFragment fragment){
            mFragment = new WeakReference<BaseFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseFragment fragment = mFragment.get();
            fragment.handleMessage(msg);
        }
    }

    /**
     * 显示吐司
     * @param msg 文本
     */
    protected void showToast(String msg){
        ToastUtils.showToast(this.getContext(), msg, Gravity.CENTER);
    }

    /**
     * 显示吐司
     * @param drawableId 图片id
     * @param msg 文本
     */
    protected void showToast(int drawableId, String msg){
        ToastUtils.showToast(this.getContext(), drawableId, msg);
    }
}
