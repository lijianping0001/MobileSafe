package com.jianping.lee.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.service.GPSService;

/**
 * Created by Li on 2017/1/8.
 */
public class LostSmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objects = (Object[]) intent.getExtras().get("pdus");
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName name = new ComponentName(context, MyAdmin.class);
        for (Object object : objects){
            SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
            String body = message.getMessageBody();
            if ("#dingwei#".equals(body)){
                Intent service = new Intent(context, GPSService.class);
                context.startService(service);
                abortBroadcast();
            }else if ("#jingbao#".equals(body)){
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.warning);
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.start();
                abortBroadcast();
            }else if ("#shanchu#".equals(body)){
                if (manager.isAdminActive(name)){
                    manager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                }
                abortBroadcast();
            }else if ("#suoding#".equals(body)){
                if (manager.isAdminActive(name)){
                    manager.resetPassword("123", 0);
                    manager.lockNow();
                }
                abortBroadcast();
            }
        }
    }
}
