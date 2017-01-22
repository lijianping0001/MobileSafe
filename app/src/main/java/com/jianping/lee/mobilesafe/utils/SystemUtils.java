package com.jianping.lee.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.widget.NumberPicker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * Created by Li on 2016/11/30.
 */
public class SystemUtils {

    /**
     * 使用linux的top命令获取cpu使用率
     * @return
     */
    public static int getCPURate(){
        StringBuilder tv = new StringBuilder();
        int rate = 0;

        try {
            String Result;
            Process p;
            p = Runtime.getRuntime().exec("top -n 1");

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((Result = br.readLine()) != null) {
                if (Result.trim().length() < 1) {
                    continue;
                } else {
                    String[] CPUusr = Result.split("%");
                    tv.append("USER:" + CPUusr[0] + "\n");
                    String[] CPUusage = CPUusr[0].split("User");
                    String[] SYSusage = CPUusr[1].split("System");
                    tv.append("CPU:" + CPUusage[1].trim() + " length:" + CPUusage[1].trim().length() + "\n");
                    tv.append("SYS:" + SYSusage[1].trim() + " length:" + SYSusage[1].trim().length() + "\n");

                    rate = Integer.parseInt(CPUusage[1].trim()) + Integer.parseInt(SYSusage[1].trim());
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rate;
    }

    /**
     * 读/proc文件获取cpu运行的总时间
     * @return
     */
    public static long getTotalCpuTime()
    { // 获取系统总CPU使用时间
        String[] cpuInfos = null;
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        long totalCpu = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);

        return totalCpu;
    }

    /**
     * 获取内存使用率
     * @param context
     * @return
     */
    public static int getMemRate(Context context){
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            long availableSize = getAvailableMemory(context) / 1024;
            int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            return percent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getMemTotal(){
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            return totalMemorySize;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取可用内存
     * @param context
     * @return
     */
    private static long getAvailableMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        return info.availMem;
    }

    /**
     * 获取运行时间
     */
    public static String getSystemRunTime() {
        //获取系统启动时间,单位s
        long time = SystemClock.elapsedRealtime() / 1000;
        int day = 24 * 60 * 60;
        int hour = 60 * 60;
        int minute = 60;
        String result = time / day + "天";
        result += (time % day) / hour + "小时";
        result += ((time % day) % hour) / minute + "分";

        return result;
    }

    /**
     * 获取运营商和网络类型
     * @param context
     * @return
     */
    public static String getTelcomOperator(Context context){
        final String NETWORK_NONE = "无网络";
        final String NETWORK_WIFI = "WiFi";
        final String NETWORK_2G = "2G";
        final String NETWORK_3G = "3G";
        final String NETWORK_4G = "4G";
        final String NETWORK_MOBILE = "其他网络";

        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //如果当前没有网络
        if (null == connManager)
            return NETWORK_NONE;
        //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORK_NONE;
        }
        // 判断是不是连接的是不是wifi
        if (isWiFiConnected(context)){
            return NETWORK_WIFI;
        }

        String operatorName = getOperatorName(context);
        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return operatorName + NETWORK_2G;
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return operatorName + NETWORK_3G;
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return operatorName + NETWORK_4G;
                        default:
                        //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return operatorName + NETWORK_3G;
                            } else {
                                return operatorName + NETWORK_MOBILE;
                            }
                    }
                }
        }
        return NETWORK_NONE;
    }

    private static String getOperatorName(Context context){
        //获取运营商类型
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = manager.getSimOperator();
        if(operator!=null) {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                return "中国移动";
                //中国移动
            } else if (operator.equals("46001")) {
                return "中国联通";
                //中国联通
            } else if (operator.equals("46003")) {
                return "中国电信";
                //中国电信
            }
        }
        return "其他";
    }

    /**
     * 获取ip地址
     * @param context
     * @return
     */
    public static String getIP(Context context){
        if (isWiFiConnected(context)){
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = intToIp(ipAddress);
            return ip;
        }else {
            try
            {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
                {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                    {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress())
                        {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            }
            catch (SocketException ex)
            {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private static String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    /**
     * 获取cpu核数
     * @return
     */
    public static int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if(Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            LogUtils.i("cpu count:" + files.length);
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch(Exception e) {
            //Print exception
            LogUtils.i("cpu count : failed");
            e.printStackTrace();
            //Default to return 1 core
            return 1;
        }
    }

    /**
     * 获取cpu频率
     * @return
     */
    public static long getCpuFrequence() {
        ProcessBuilder cmd;
        try {
            String[] args = { "/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
            cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line = reader.readLine();
            return Long.parseLong(line);
        } catch (IOException ex) {
            ex.printStackTrace();
            return 0;
        }

    }

    /**
     * 判断WiFi是否连接
     * @param context
     * @return
     */
    private static boolean isWiFiConnected(Context context){
        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return true;
                }
        }
        return false;
    }
}
