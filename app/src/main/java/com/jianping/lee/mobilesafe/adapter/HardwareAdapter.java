package com.jianping.lee.mobilesafe.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.utils.CameraUtils;
import com.jianping.lee.mobilesafe.utils.LogUtils;
import com.jianping.lee.mobilesafe.utils.SDCardUtils;
import com.jianping.lee.mobilesafe.utils.ScreenUtils;
import com.jianping.lee.mobilesafe.utils.SystemUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Li on 2016/12/8.
 */
public class HardwareAdapter extends RecyclerView.Adapter<HardwareAdapter.ViewHolder>{

    private Context mContext;

    private List<Info> infoList;
    /**
     * 监听电池状态变化广播接收者
     */
    private BroadcastReceiver batteryLevelReceiver;
    private IntentFilter batteryLevelFilter;

    /**
     * 监听网络变化的广播接受者
     */
    private BroadcastReceiver networkReceiver;
    private IntentFilter networkFilter;


    DecimalFormat df = new DecimalFormat("###.0");

    public HardwareAdapter(Context context){
        this.mContext = context;
        infoList = new ArrayList<>();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_hardware, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Info info = infoList.get(position);

        if (TextUtils.isEmpty(info.value)){
            holder.info.setTextColor(mContext.getResources().getColor(R.color.grey));
            holder.layout.setBackgroundResource(R.color.lightGrey);
        }else {
            holder.info.setTextColor(mContext.getResources().getColor(android.R.color.black));
            holder.layout.setBackgroundResource(android.R.color.white);
        }

        holder.info.setText(info.info);
        holder.value.setText(info.value);
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public void loadData(){
        synchronized (infoList){
            infoList.clear();

            Info status = new Info("状态信息", "");
            infoList.add(status);

            Info time = new Info("已开机时间", SystemUtils.getSystemRunTime());
            infoList.add(time);

            Info battery = new Info("电池信息", "100%");
            infoList.add(battery);

            Info batteryTemp = new Info("电池温度", "25℃");
            infoList.add(batteryTemp);

            Info batteryVoltage = new Info("电池电压", "4000mV");
            infoList.add(batteryVoltage);

            Info networkInfo = new Info("网络信息", "");
            infoList.add(networkInfo);

            Info networkType = new Info("网络类型", SystemUtils.getTelcomOperator(mContext));
            infoList.add(networkType);

            Info ipAddr = new Info("IP地址", SystemUtils.getIP(mContext));
            infoList.add(ipAddr);

            Info hardware = new Info("硬件信息", "");
            infoList.add(hardware);

            Info coreNum = new Info("CPU核数", SystemUtils.getNumCores() + "核");
            infoList.add(coreNum);

            Info coreRate = new Info("CPU主频",  df.format(SystemUtils.getCpuFrequence() * 1.0f / 1024 / 1024 )+ "GHz");
            infoList.add(coreRate);

            Info memSize = new Info("运行内存", df.format(SystemUtils.getMemTotal() * 1.0f / 1024 / 1024) + "G");
            infoList.add(memSize);

            Info internalSize = new Info("机身存储", df.format(SDCardUtils.getTotalInternalMemorySize() * 1.0f/ 1024 / 1024 / 1024) + "G");
            infoList.add(internalSize);

            Info sdSize = new Info("SD卡存储", df.format(SDCardUtils.getTotalExternalMemorySize() * 1.0f/ 1024 / 1024 / 1024) + "G");
            infoList.add(sdSize);

            Info resolution = new Info("分辨率（像素）", ScreenUtils.getScreenHeight(mContext) + "x" + ScreenUtils.getScreenWidth(mContext));
            infoList.add(resolution);

            if (CameraUtils.HasBackCamera() == CameraUtils.CAMERA_FACING_BACK){
                Info backCamera = new Info("后置摄像头（像素）", CameraUtils.getCameraPixels(CameraUtils.CAMERA_FACING_BACK));
                infoList.add(backCamera);
            }

            if (CameraUtils.HasFrontCamera() == CameraUtils.CAMERA_FACING_FRONT){
                Info frontCamera = new Info("前置摄像头（像素）", CameraUtils.getCameraPixels(CameraUtils.CAMERA_FACING_FRONT));
                infoList.add(frontCamera);
            }
        }

        monitorBatteryState();
        monitorNetworkState();
    }

    /**
     * 监听网络变化
     */
    private void monitorNetworkState() {
        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateNetworkState();
                notifyDataSetChanged();
                LogUtils.i("receive network changed");
            }
        };
        networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(networkReceiver, networkFilter);
    }

    /**
     * 更新网络类型和IP地址
     */
    private void updateNetworkState() {
        if (infoList != null){
            if (infoList.size() < 7){
                return;
            }
            Info networkType = infoList.get(6);
            networkType.value = SystemUtils.getTelcomOperator(mContext);
            infoList.remove(6);
            infoList.add(6, networkType);


            if (infoList.size() < 8){
                return;
            }
            Info ipAddr = infoList.get(7);
            ipAddr.value = SystemUtils.getIP(mContext);
            infoList.remove(7);
            infoList.add(7, ipAddr);
        }
    }

    /**
     * 监听电池状态
     */
    private void monitorBatteryState() {
        batteryLevelReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                String info;
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status", -1);
                int health = intent.getIntExtra("health", -1);
                int temperature = intent.getIntExtra("temperature", 0);
                int voltage = intent.getIntExtra("voltage", 0);
                int level = -1; // percentage, or -1 for unknown
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
                    LogUtils.i("battery feels very hot!");
                } else {
                    switch (status) {
                        case BatteryManager.BATTERY_STATUS_UNKNOWN:
                            LogUtils.i("no battery.");
                            break;
                        case BatteryManager.BATTERY_STATUS_CHARGING:
                            updateBattery("正在充电" + " " + level + "%");
                            LogUtils.i("battery is charging");
                            if (level <= 33)
                                LogUtils.i(" is charging, battery level is low"
                                        + "[" + level + "]");
                            else if (level <= 84)
                                LogUtils.i(" is charging." + "[" + level + "]");
                            else
                                LogUtils.i(" will be fully charged.");
                            break;
                        case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                            updateBattery("未充电" + " " + level + "%");
                            if (level == 0)
                                LogUtils.i(" needs charging right away.");
                            else if (level > 0 && level <= 33)
                                LogUtils.i(" is about ready to be recharged, battery level is low"
                                        + "[" + level + "]");
                            else
                                LogUtils.i("'s battery level is" + "[" + level + "]");
                            break;
                        case BatteryManager.BATTERY_STATUS_FULL:
                            updateBattery("充电完成" + " " + "100%");
                            LogUtils.i(" is fully charged.");
                            break;
                        default:
                            LogUtils.i("'s battery is indescribable!");
                            break;
                    }
                }

                updateBatteryTemp(df.format(temperature * 0.1f) + "℃");
                updateBatteryVoltage(voltage + "mV");
                notifyDataSetChanged();
            }
        };
        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    /**
     * 更新电池电压
     * @param msg
     */
    private void updateBatteryVoltage(String msg) {
        if (infoList != null){
            if (infoList.size() < 5){
                return;
            }
            Info voltage = infoList.get(4);
            voltage.value = msg;
            infoList.remove(4);
            infoList.add(4, voltage);
        }
    }

    /**
     * 更新电池温度
     * @param msg
     */
    private void updateBatteryTemp(String msg) {
        if (infoList != null){
            if (infoList.size() < 4){
                return;
            }
            Info temperature = infoList.get(3);
            temperature.value = msg;
            infoList.remove(3);
            infoList.add(3, temperature);
        }
    }

    /**
     * 更新电池电量
     * @param msg
     */
    private void updateBattery(String msg){
        if (infoList != null) {
            if (infoList.size() < 3){
                return;
            }
            Info battery = infoList.get(2);
            infoList.remove(2);
            battery.value = msg;
            infoList.add(2, battery);
            notifyDataSetChanged();
        }
    }

    /**
     * 释放资源
     */
    public void release(){
        if (mContext != null){
            if (batteryLevelReceiver != null){
                mContext.unregisterReceiver(batteryLevelReceiver);
            }
            if (networkReceiver != null){
                mContext.unregisterReceiver(networkReceiver);
            }
        }
        infoList.clear();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        @InjectView(R.id.tv_item_hardware_info)
        TextView info;

        @InjectView(R.id.tv_item_hardware_value)
        TextView value;

        @InjectView(R.id.rl_item_hardware)
        RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    private static class Info{
        String info = "";
        String value = "";

        public Info(){

        }

        public Info(String info, String value){
            this.info = info;
            this.value = value;
        }
    }
}
