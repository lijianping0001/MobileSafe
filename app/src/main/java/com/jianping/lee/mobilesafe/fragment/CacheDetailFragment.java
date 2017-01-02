package com.jianping.lee.mobilesafe.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.activity.CleanCacheActivity;
import com.jianping.lee.mobilesafe.adapter.CacheDetailAdapter;
import com.jianping.lee.mobilesafe.base.BaseFragment;
import com.jianping.lee.mobilesafe.model.CacheInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CacheDetailFragment extends BaseFragment {

    private TextView mTitle;

    private ImageView mBack;

    private List<List<CacheInfo>> itemList;

    private List<Map<String, Object>> groupList;

    @InjectView(R.id.elv_cache_detail_content)
    ExpandableListView mListView;

    private CacheDetailAdapter mAdapter;


    private CleanCacheActivity getAttachedActivity(){
        return (CleanCacheActivity) getActivity();
    }

    public CacheDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cache_detail, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    private void initView() {
        mTitle = (TextView) getAttachedActivity().findViewById(R.id.tv_title_center);
        mBack = (ImageView) getAttachedActivity().findViewById(R.id.iv_title_back);
        mBack.setOnClickListener(backListener);
        mTitle.setText(getString(R.string.cache_detail));
        mBack.setVisibility(View.VISIBLE);

        if (itemList == null){
            itemList = new ArrayList<>();
        }

        mListView.setGroupIndicator(null);

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (itemList.get(groupPosition) == null) {
                    return true;
                }
                return false;
            }
        });

        if (mAdapter == null){
            itemList.add(getAttachedActivity().getCacheInfos());
            itemList.add(getAttachedActivity().getApkInfos());
            loadGroupData();
            mAdapter = new CacheDetailAdapter(getContext(), groupList, itemList);
            mListView.setAdapter(mAdapter);
        }
    }


    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .remove(CacheDetailFragment.this).commit();
//            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            getActivity().onBackPressed();

        }
    };


    private void loadGroupData(){
        groupList = new ArrayList<>();
        Map<String ,Object> group1 = new HashMap<>();
        group1.put("desc", "应用缓存");
        group1.put("size", getAttachedActivity().getTotalCacheSize() + "");
        group1.put("checked", "true");

        groupList.add(group1);

        Map<String ,Object> group2 = new HashMap<>();
        group2.put("desc", "安装包");
        group2.put("size", getAttachedActivity().getTotalApkSize() + "");
        group2.put("checked", "true");

        groupList.add(group2);
    }

    @Override
    public void onDetach() {
        //更新信息
        long cacheSize =Long.parseLong(groupList.get(0).get("size").toString());
        long apkSize = Long.parseLong(groupList.get(1).get("size").toString());
        getAttachedActivity().resetSize(cacheSize + apkSize);
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
