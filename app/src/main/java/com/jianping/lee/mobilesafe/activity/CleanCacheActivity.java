package com.jianping.lee.mobilesafe.activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.fragment.CacheDetailFragment;

public class CleanCacheActivity extends BaseActivity {

    private CacheDetailFragment cacheDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_clean_cache));
        mBack.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initData() {

    }

    private void addOrShowFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ll_clean_cache_main, fragment)
                .commit();
    }

//    private void initProgramFragment() {
//        if (editProgramFragment == null) {
//            editProgramFragment = new EditProgramFragment();
//        }
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.ll_clean_cache_main, editProgramFragment)
//                .commit();
//
//
//    }

//
//    /**
//     * 显示编辑分区fragment
//     */
//    private void showAreaFragment() {
//        if (editAreaFragment == null) {
//            editAreaFragment = new EditAreaFragment();
//        }
//        if (currFragment == editAreaFragment) {
//            /**
//             * 此处当当前fragment为 editAreaFragment时，点击不同的分区切换不同图标和位置
//             */
//            return;
//        }
//        addOrShowFragment(editAreaFragment);
//    }
}
