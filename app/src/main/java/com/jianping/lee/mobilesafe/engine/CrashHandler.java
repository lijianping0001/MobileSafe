package com.jianping.lee.mobilesafe.engine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.jianping.lee.mobilesafe.activity.MainActivity;
import com.jianping.lee.mobilesafe.base.MyApplication;
import com.jianping.lee.mobilesafe.model.Feedback;
import com.jianping.lee.mobilesafe.utils.CommonUtils;
import com.jianping.lee.mobilesafe.utils.LogUtils;
import com.jianping.lee.mobilesafe.utils.SDCardUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @fileName: CrashHandler
 * @Author: Li Jianping
 * @Date: 2016/8/23 9:25
 * @Description:
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //程序的Context对象
    private Context mContext;

    private static CrashHandler mHandler;

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance(){
        if (mHandler == null){
            synchronized (CrashHandler.class){
                if (mHandler == null){
                    mHandler = new CrashHandler();
                }
            }
        }
        return mHandler;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {

            //使用Toast来显示异常信息
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast toast = Toast.makeText(mContext, "程序发生意外，正在重启", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Looper.loop();
                }
            }.start();

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    mContext.getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            //退出程序
            AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                    restartIntent); // 1秒钟后重启应用
            //结束掉所有activity
            ((MyApplication)(mContext)).finishAllActivities();

            //杀掉进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                //保存日志文件
                String fileInfo = saveCrashInfoToFile(ex);
                //发送错误报告到服务器
                sendMsgToServer(fileInfo);
            }
        }).start();

        return true;
    }

    /**
     * 发送错误信息到服务器
     * @param fileInfo
     */
    private void sendMsgToServer(final String fileInfo) {
        //检测网络是否通畅
        if (!CommonUtils.isNetworkConnected(mContext)){
            return;
        }

        Feedback feedback = new Feedback();
        feedback.setContent(fileInfo);
        feedback.setContact("crash");
        feedback.setVersion(CommonUtils.getAppVersionName(mContext));
        feedback.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                LogUtils.i("crash upload done:" + e.getErrorCode());
            }
        });

//        BmobFile bmobFile = new BmobFile(new File(filePath));
//        bmobFile.uploadblock(new UploadFileListener() {
//            @Override
//            public void done(BmobException e) {
//                uploadFlag = true;
//                LogUtils.i("upload error msg done");
//            }
//        });
    }


    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return	返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfoToFile(Throwable ex) {
        //保存错误信息
        String filePath = SDCardUtils.getAvailableFileDir(mContext) + "/error.log";
        final StringBuilder stringBuffer = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");
        stringBuffer.append("time: ").append(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", Calendar.getInstance())).append(lineSeparator);
        stringBuffer.append("app_version_code: ").append(CommonUtils.getAppVersionCode(mContext)).append(lineSeparator);
        stringBuffer.append("app_version name: ").append(CommonUtils.getAppVersionName(mContext)).append(lineSeparator);
        stringBuffer.append("os_version_name: ").append(Build.VERSION.RELEASE).append(lineSeparator);
        stringBuffer.append("os_version_code: ").append(Build.VERSION.SDK_INT).append(lineSeparator);
        stringBuffer.append("os_display_name: ").append(Build.DISPLAY).append(lineSeparator);
        stringBuffer.append("brand_info: ").append(Build.BRAND).append(lineSeparator);
        stringBuffer.append("product_info: ").append(Build.PRODUCT).append(lineSeparator);
        stringBuffer.append("model_info: ").append(Build.MODEL).append(lineSeparator);
        stringBuffer.append("manufacture: ").append(Build.MANUFACTURER).append(lineSeparator);
//                    Field[] fields = Build.class.getDeclaredFields();
//                    for (Field field:fields) {
//                        field.setAccessible(true);
//                        String value = field.get(null).toString();
//                        String name = field.getName();
//                        stringBuffer.append(name);
//                        stringBuffer.append(":");
//                        stringBuffer.append(value);
//                        stringBuffer.append("\n");
//
//                    }

        try {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            ex.printStackTrace(printWriter);
            String errLog = stringWriter.toString();
            stringBuffer.append(errLog);
            outputStream.write(stringBuffer.toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
}
