package com.jianping.lee.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.jianping.lee.mobilesafe.service.AppLockService;
import com.jianping.lee.mobilesafe.service.BlackInterceptService;
import com.jianping.lee.mobilesafe.utils.CommonUtils;
import com.jianping.lee.mobilesafe.utils.LogUtils;
import com.jianping.lee.mobilesafe.utils.SPUtils;
import com.jianping.lee.mobilesafe.utils.ServiceStautsUtils;

/**
 * Created by Li on 2017/1/8.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i("bootcomplete===");
        boolean enable = (boolean) SPUtils.get(context, SPUtils.PROTECTING, false);
        if (!enable){
            LogUtils.i("未开启防盗");
            return;
        }
        //取出当前手机的串号
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String curSim = manager.getSimSerialNumber();

        String bindSim = (String) SPUtils.get(context, SPUtils.SIM_NUM, "");
        LogUtils.i("bindSim==" + bindSim + "  curSim==" + curSim);
        if (!curSim.equals(bindSim)){
            SmsManager.getDefault().sendTextMessage((String) SPUtils.get(context, SPUtils.PHONE_NUM, ""), null, "sim卡已被更换", null, null);
        }

        CommonUtils.startBlackNumService(context);

        CommonUtils.startAppLockService(context);
    }
}
