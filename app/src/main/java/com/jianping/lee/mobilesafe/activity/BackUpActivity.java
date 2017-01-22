package com.jianping.lee.mobilesafe.activity;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.Contacts.People;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.model.MyUser;
import com.jianping.lee.mobilesafe.model.UserContact;
import com.jianping.lee.mobilesafe.utils.CommonUtils;
import com.jianping.lee.mobilesafe.utils.IntentUtils;
import com.jianping.lee.mobilesafe.utils.SDCardUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BackUpActivity extends BaseActivity {

    @InjectView(R.id.tv_back_up_name)
    TextView mLogin;

    @InjectView(R.id.rb_back_up_download)
    RadioButton mDownload;

    @InjectView(R.id.rb_back_up_upload)
    RadioButton mUpload;

    private MyUser mUser;

    private String str;

    private Dialog dialog;

    private Context mContext;

    private String filePath;

    private String objectId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_up);
        mContext = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_backup));
        mBack.setVisibility(View.VISIBLE);


        mUser = BmobUser.getCurrentUser(MyUser.class);
        if (mUser == null){
            mLogin.setText("点击登录");
        }else {
            mLogin.setText(mUser.getUsername());
        }

    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.tv_back_up_name)
    void onClickLogin(){
        if (mUser == null){
            IntentUtils.startActivityWithAnim(this, LoginActivity.class,
                    R.anim.push_bottom_in, R.anim.push_bottom_out);
        }
    }

    @OnClick(R.id.rb_back_up_download)
    void onClickDownload(){
        if (mUser == null){
            showToast("请先登录");
            return;
        }

        dialog = CommonUtils.createLoadingDialog(this, "正在恢复");
        dialog.show();

        Subscription subscription = getFindPath()
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return downloadFile(s);
                    }
                })
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        readContact(s);
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        dialog.dismiss();
                        showToast("网络不给力，请稍后再试");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        showToast("恢复成功");
                    }
                });


    }

    private void readContact(String filePath) {
        File file = new File(filePath);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1025 * 5];
            int length = -1;
            while ((length = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            String txt = outputStream.toString();

            String[] str = txt.split("\n");
            for (int i = 0; i < str.length; i++){
                if (str[i].indexOf(",") >= 0){
                    String[] nameAndTel = str[i].split(",");
                    addContacts(nameAndTel[0], nameAndTel[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addContacts(String name, String num) {
        ContentValues values = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        values.clear();
        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        values.put(StructuredName.GIVEN_NAME, name);

        getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();
        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        values.put(Phone.NUMBER, num);
        values.put(Phone.TYPE, Phone.TYPE_HOME);

        getContentResolver().insert(Data.CONTENT_URI, values);
    }

    /**
     * 下载文件
     * @param path
     * @return
     */
    private Observable<String> downloadFile(final String path){

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                BmobFile file = new BmobFile(mUser.getUsername() + ".txt", null, path);
                file.download(new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            subscriber.onNext(s);
                        }else{
                            subscriber.onError(new Throwable());
                        }
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onProgress(Integer integer, long l) {

                    }
                });
            }
        });

    }

    @OnClick(R.id.rb_back_up_upload)
    void onClickUpload(){
        if (mUser == null){
            showToast("请先登录");
            return;
        }

        dialog = CommonUtils.createLoadingDialog(this, "正在备份");
        dialog.show();

        Subscription subscription = rx.Observable.create(new rx.Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                //获取联系人信息
                getContact();
                //联系人信息保存到文件
                filePath = SDCardUtils.getAvailableFileDir(mContext) + "/" + mUser.getUsername() + ".txt";
                File saveFile  = new File(filePath);
                FileOutputStream stream;

                try {
                    stream = new FileOutputStream(saveFile);
                    stream.write(str.getBytes());
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(new Throwable());
                }
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        })
                .flatMap(new Func1<Boolean, rx.Observable<String>>() {
                    @Override
                    public rx.Observable<String> call(Boolean aBoolean) {
                        return getFindPath();
                    }
                })
                .flatMap(new Func1<String, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(String s) {
                        return deleteBackupFile(s);
                    }
                })
                .flatMap(new Func1<Boolean, Observable<String>>() {
                    @Override
                    public Observable<String> call(Boolean aBoolean) {
                        return uploadFile();
                    }
                })
                .flatMap(new Func1<String, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(String s) {
                        return modifyFile(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        dialog.dismiss();
                        showToast("网络不给力，请稍后再试");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        showToast("备份成功");
                    }
                });
        addSubscription(subscription);
    }

    /**
     * 修改文件
     * @param fileUrl
     * @return
     */
    private Observable<Boolean> modifyFile(final String fileUrl){
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                //添加一行数据
                UserContact userContact = new UserContact();
                userContact.setUserName(mUser.getUsername());
                userContact.setFilePath(fileUrl);
                if (TextUtils.isEmpty(objectId)){
                    userContact.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e== null){
                                subscriber.onNext(true);
                            }else{
                                subscriber.onError(new Throwable());
                            }
                            subscriber.onCompleted();
                        }
                    });
                }else {
                    userContact.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                subscriber.onNext(true);
                            }else{
                                subscriber.onError(new Throwable());
                            }
                            subscriber.onCompleted();
                        }
                    });
                }
                objectId = "";
            }
        });
    }

    /**
     * 上传文件
     * @return
     */
    private Observable<String> uploadFile(){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                final BmobFile file = new BmobFile(new File(filePath));
                file.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            subscriber.onNext(file.getFileUrl());
                        } else {
                            subscriber.onError(new Throwable());
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    /**
     * 删除文件
     * @param path
     * @return
     */
    private Observable<Boolean> deleteBackupFile(final String path){
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                if (TextUtils.isEmpty(path)){
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                    return;
                }
                BmobFile file = new BmobFile();
                file.setUrl(path);
                file.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            subscriber.onNext(true);
                        } else {
                            subscriber.onError(new Throwable());
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    /**
     * 找到存放路径
     * @return
     */
    private Observable<String> getFindPath(){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                BmobQuery<UserContact> query = new BmobQuery<>();
                query.addWhereEqualTo("userName", mUser.getUsername())
                        .findObjects(new FindListener<UserContact>() {
                            @Override
                            public void done(List<UserContact> list, BmobException e) {
                                if (e != null) {
                                    subscriber.onError(new Throwable());
                                }else {
                                    if (list.size() == 0){
                                        subscriber.onNext(null);
                                    }else{
                                        objectId = list.get(0).getObjectId();
                                        subscriber.onNext(list.get(0).getFilePath());
                                    }
                                    subscriber.onCompleted();
                                }

                            }
                        });
            }
        });
    }

    /**
     * 获取联系人信息
     */
    private void getContact() {
        str = "";
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()){
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int displayNameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            do {
                //获取联系人的id号
                String contactId = cursor.getString(idColumn);
                //获取联系人的姓名
                String displayName = cursor.getString(displayNameColumn);
                str +=displayName;
                //查看该联系人有多少个电话号码，若没有则返回0
                int phoneNum = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (phoneNum > 0){
                    //获取联系人的电话号码
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =" + contactId, null, null);
                    int i = 0;
                    String phoneStr = "";
                    if (phones.moveToFirst()){
                        do {
                            i++;
                            phoneStr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (i == 1){
                                str = str + "," + phoneStr;
                            }
                        }while (phones.moveToNext());
                    }
                }
                str += "\r\n";

            }while (cursor.moveToNext());
        }
    }

}
