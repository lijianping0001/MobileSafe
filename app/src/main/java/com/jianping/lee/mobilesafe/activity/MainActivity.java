package com.jianping.lee.mobilesafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.IconAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.model.Icon;
import com.jianping.lee.mobilesafe.utils.DensityUtils;
import com.jianping.lee.mobilesafe.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        GridView gridView = (GridView) findViewById(R.id.gv_main_content);

        //动态计算GridView的宽度，适配不同的屏幕
        int screenWidth = ScreenUtils.getScreenWidth(this);
        int iconWidth = (screenWidth - DensityUtils.dp2px(this, 120)) / 3;
        gridView.setColumnWidth(iconWidth);

        ArrayList<Icon> dataList = new ArrayList<>();
        addAllFunction(dataList);
        IconAdapter adapter = new IconAdapter(this, dataList);
        gridView.setAdapter(adapter);
    }

    private void addAllFunction(ArrayList<Icon> dataList){
        dataList.clear();
        dataList.add(new Icon(R.drawable.round_background_green, R.drawable.icon_protection,
                getString(R.string.func_protection)));
        dataList.add(new Icon(R.drawable.round_background_orange, R.drawable.icon_speed,
                getString(R.string.func_speed_up)));
        dataList.add(new Icon(R.drawable.round_background_purple, R.drawable.icon_app,
                getString(R.string.func_app_manager)));
        dataList.add(new Icon(R.drawable.round_background_blue, R.drawable.icon_defend,
                getString(R.string.func_defend)));
        dataList.add(new Icon(R.drawable.round_background_green, R.drawable.icon_app_key,
                getString(R.string.func_app_lock)));
        dataList.add(new Icon(R.drawable.round_background_red, R.drawable.icon_add,
                getString(R.string.func_virus)));
        dataList.add(new Icon(R.drawable.round_background_orange, R.drawable.icon_clean,
                getString(R.string.func_clean_cache)));
        dataList.add(new Icon(R.drawable.round_background_purple, R.drawable.icon_check,
                getString(R.string.func_check)));
        dataList.add(new Icon(R.drawable.round_background_yellow, R.drawable.icon_location,
                getString(R.string.func_location)));
        dataList.add(new Icon(R.drawable.round_background_green, R.drawable.icon_qrcode,
                getString(R.string.func_qrcode)));
        dataList.add(new Icon(R.drawable.round_background_blue, R.drawable.icon_test_speed,
                getString(R.string.func_net_speed)));
        dataList.add(new Icon(R.drawable.round_background_green, R.drawable.icon_contact,
                getString(R.string.func_backup)));

    }
}
