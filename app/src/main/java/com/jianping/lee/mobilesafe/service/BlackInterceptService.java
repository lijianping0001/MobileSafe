package com.jianping.lee.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.jianping.lee.mobilesafe.db.MyBlackNumDao;
import com.jianping.lee.mobilesafe.utils.LogUtils;

import java.lang.reflect.Method;

/**
 * Created by Li on 2017/1/8.
 */
public class BlackInterceptService extends Service {

    private MyBlackNumDao dao;

    private TelephonyManager manager;

    private MyPhoneListener listener;

    private InnerSmsReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("开启黑名单拦截服务===");
        dao = MyBlackNumDao.getInstance(this);
        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        listener = new MyPhoneListener();
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        receiver = new InnerSmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        receiver = null;
        manager.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;
    }

    private class InnerSmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object object : objects){
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String address = message.getOriginatingAddress();
                String result = dao.find(address);

                if ("2".equals(result) || "3".equals(result)){
                    abortBroadcast();
                }
            }
        }
    }

    private class MyPhoneListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE://空闲状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃状态
                    String result = dao.find(incomingNumber);
                    if ("1".equals(result) || "3".equals(result)){
                        //记录被拦截的电话号码，还有时间
                        endCall();
                        //挂断电话后，呼叫记录可能没有生成出来，监视呼叫记录的数据库，生成后删除掉
                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
                            @Override
                            public void onChange(boolean selfChange) {
                                deleteCallLog(incomingNumber);
                                super.onChange(selfChange);
                            }
                        });
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接通电话
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void deleteCallLog(String incomingNumber) {
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri, "number=?",new String[]{incomingNumber});
    }

    private void endCall() {
        try {
            Class clazz = BlackInterceptService.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
