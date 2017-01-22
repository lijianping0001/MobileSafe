package com.jianping.lee.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.adapter.SortAdapter;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.model.CharacterParser;
import com.jianping.lee.mobilesafe.model.PinyinComparator;
import com.jianping.lee.mobilesafe.model.ContactInfo;
import com.jianping.lee.mobilesafe.utils.CommonUtils;
import com.jianping.lee.mobilesafe.views.sortListView.ClearEditText;
import com.jianping.lee.mobilesafe.views.sortListView.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.InjectView;

public class SelectedContactActivity extends BaseActivity {
    @InjectView(R.id.lv_contact)
    ListView mListView;

    @InjectView(R.id.sb_contact)
    SideBar mSideBar;

    @InjectView(R.id.tv_contact_toast)
    TextView mToast;

    @InjectView(R.id.et_contact_search)
    ClearEditText mSearch;

    private SortAdapter mAdapter;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<ContactInfo> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_contact);
        initView();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.contact));
        mBack.setVisibility(View.VISIBLE);

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        mSideBar.setTextView(mToast);

        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                String phoneNum = ((ContactInfo)mAdapter.getItem(position)).getPhone();
                String name = ((ContactInfo)mAdapter.getItem(position)).getName();
                Intent data = new Intent();
                data.putExtra("phone", phoneNum);
                data.putExtra("name",name);
                setResult(0, data);
                finish();
            }
        });

        List<ContactInfo> sourceData = CommonUtils.getContactInfos(this);
        SourceDateList = fillData(sourceData);

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        mAdapter = new SortAdapter(this, SourceDateList);
        mListView.setAdapter(mAdapter);

        //根据输入框输入值的改变来过滤搜索
        mSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    protected void initData() {

    }

    /**
     * 为ListView填充数据
     * @param data
     * @return
     */
    private List<ContactInfo> fillData(List<ContactInfo> data){
        for(int i=0; i<data.size(); i++){
            ContactInfo contactInfo = data.get(i);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(data.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                contactInfo.setSortLetters(sortString.toUpperCase());
            }else{
                contactInfo.setSortLetters("#");
            }
        }
        return data;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        List<ContactInfo> filterDateList = new ArrayList<ContactInfo>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = SourceDateList;
        }else{
            filterDateList.clear();
            for(ContactInfo contactInfo : SourceDateList){
                String name = contactInfo.getName();
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(contactInfo);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        mAdapter.updateListView(filterDateList);
    }
}
