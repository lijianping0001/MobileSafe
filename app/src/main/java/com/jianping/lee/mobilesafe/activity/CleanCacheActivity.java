package com.jianping.lee.mobilesafe.activity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jianping.lee.mobilesafe.R;
import com.jianping.lee.mobilesafe.base.BaseActivity;
import com.jianping.lee.mobilesafe.engine.ApkProvider;
import com.jianping.lee.mobilesafe.fragment.CacheDetailFragment;
import com.jianping.lee.mobilesafe.model.CacheInfo;
import com.jianping.lee.mobilesafe.utils.LogUtils;
import com.jianping.lee.mobilesafe.utils.ToastUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class CleanCacheActivity extends BaseActivity {

    @InjectView(R.id.tv_clean_cache_size)
    TextView tvTotalSize;

    @InjectView(R.id.tv_clean_cache_suffix)
    TextView tvSuffix;

    @InjectView(R.id.tv_clean_cache_hint)
    TextView tvHint;

    @InjectView(R.id.btn_clean_cache)
    Button btnClean;

    private final int SCANNING_WHAT = 1;

    private final int SCANNING_OVER_WHAT = 2;

    private CacheDetailFragment cacheDetailFragment;

    private List<CacheInfo> cacheInfos;

    private List<CacheInfo> apkInfos;

    private MyHandler handler = new MyHandler(this);

    private long totalCacheSize;

    private long totalApkSize;

    private long totalSize;

    public List<CacheInfo> getCacheInfos() {
        return cacheInfos;
    }

    public long getTotalApkSize() {
        return totalApkSize;
    }

    public List<CacheInfo> getApkInfos() {
        return apkInfos;
    }

    public long getTotalCacheSize() {
        return totalCacheSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTitle.setText(getString(R.string.func_clean_cache));
        mBack.setVisibility(View.VISIBLE);
        if (cacheInfos == null){
            cacheInfos = new ArrayList<>();
        }

        if (apkInfos == null){
            apkInfos = new ArrayList<>();
        }
        scanCache();
    }

    @Override
    protected void initData() {

    }

    private void addOrShowFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ll_clean_cache_main, fragment)
                .commit();
    }

    private void scanCache(){
        btnClean.setClickable(false);
        tvHint.setClickable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {

                PackageManager manager = getPackageManager();
                List<PackageInfo> packageInfos = manager.getInstalledPackages(0);
                for (PackageInfo packageInfo : packageInfos){
                    String packName = packageInfo.packageName;
                    try {
                        Method method = PackageManager.class.getMethod("getPackageSizeInfo",
                                String.class, IPackageStatsObserver.class);
                        method.invoke(manager, packName, new MyObserver());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SystemClock.sleep(10);
                }
                findAllAPKFile(Environment.getExternalStorageDirectory());
                findAllAPKFile(Environment.getDataDirectory());
                totalSize = totalApkSize + totalCacheSize;
                handler.sendEmptyMessage(SCANNING_OVER_WHAT);
            }
        }).start();
    }

    private class MyObserver extends IPackageStatsObserver.Stub{
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            Message msg = Message.obtain();
            msg.what = SCANNING_WHAT;
            try {
                String name = getPackageManager().getPackageInfo(pStats.packageName,0).
                        applicationInfo.loadLabel(getPackageManager()).toString();
                msg.obj = name;
                totalCacheSize +=pStats.cacheSize;
                handler.sendMessage(msg);
                if (pStats.cacheSize > 0){
                    CacheInfo cacheInfo = new CacheInfo();
                    cacheInfo.setCacheSize(pStats.cacheSize);
                    cacheInfo.setPackName(pStats.packageName);
                    cacheInfo.setAppName(name);
                    cacheInfo.setSelected(true);
                    cacheInfo.setIcon(getPackageManager().getPackageInfo(cacheInfo.getPackName(), 0)
                            .applicationInfo.loadIcon(getPackageManager()));
                    cacheInfos.add(cacheInfo);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case SCANNING_WHAT://更新扫描状态
                String appName = (String) msg.obj;
                tvHint.setText("正在扫描：" + appName);
                String size = Formatter.formatFileSize(this, totalCacheSize);
                tvSuffix.setText(size.substring(size.length() - 2));
                tvTotalSize.setText(size.substring(0, size.length() - 2));
                break;
            case SCANNING_OVER_WHAT://
                tvHint.setText("查看详情");
                tvHint.setClickable(true);
                btnClean.setClickable(true);
                refreshSizeHint();
            case 3://
                refreshSizeHint();
                break;
        }
    }

    private void showDetailFragment() {
        if (cacheDetailFragment == null){
            cacheDetailFragment = new CacheDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ll_clean_cache_main, cacheDetailFragment)
                    .addToBackStack(cacheDetailFragment.toString())
                    .commit();
        }else {
            getSupportFragmentManager().beginTransaction()
                    .show(cacheDetailFragment)
                    .commit();
        }


    }

    @OnClick(R.id.tv_clean_cache_hint)
    void onClickDetail(){
        showDetailFragment();
    }


    @OnClick(R.id.btn_clean_cache)
    void onClickClean(){
        showToast("清理缓存");
        new Thread(new Runnable() {
            @Override
            public void run() {
                cleanApk();
                cleanCache();
            }
        }).start();
    }

    @WorkerThread
    private void cleanCache(){
        for (CacheInfo cacheInfo : cacheInfos){
            if (!cacheInfo.isSelected()){
                continue;
            }

            Method[] methods = PackageManager.class.getMethods();
            for (Method method : methods){
                if("deleteApplicationCacheFiles".equals(method.getName())){
                    try {
                        method.invoke(getPackageManager(), cacheInfo.getPackName(),new ClearCacheObserver());
                        totalSize -= cacheInfo.getCacheSize();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }

        }
    }
    @WorkerThread
    private void cleanApk(){
        Iterator<CacheInfo> iterator = apkInfos.iterator();
        while (iterator.hasNext()){
            CacheInfo cacheInfo = iterator.next();
            if (!cacheInfo.isSelected()){
                continue;
            }

            File file = new File(cacheInfo.getPackName());
            if (file.exists()){
                totalSize -= file.length();
                totalApkSize -= file.length();
                file.delete();
                iterator.remove();
                handler.sendEmptyMessage(3);
            }
        }
    }

    @WorkerThread
    public void findAllAPKFile(File file) {
        // 手机上的文件,目前只判断SD卡上的APK文件
        // file = Environment.getDataDirectory();
        // SD卡上的文件目录
        if (file.isFile()) {
            String nameStr = file.getName();
            CacheInfo cacheInfo = new CacheInfo();
            String apkPath = null;
            // MimeTypeMap.getSingleton()
            if (nameStr.toLowerCase().endsWith(".apk")) {
                apkPath = file.getAbsolutePath();// apk文件的绝对路劲
                LogUtils.i("file path===" + apkPath);
                PackageManager pm = this.getPackageManager();
                PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
                ApplicationInfo appInfo = packageInfo.applicationInfo;

                cacheInfo.setPackName(apkPath);
                /**获取apk的图标 */
                appInfo.sourceDir = apkPath;
                appInfo.publicSourceDir = apkPath;
                Drawable apkIcon = appInfo.loadIcon(pm);
                cacheInfo.setIcon(apkIcon);
                /** 得到包名 */
                String packageName = packageInfo.packageName;

                cacheInfo.setSelected(true);
                cacheInfo.setAppName(file.getName());
                /**安装处理类型*/
                int type = doType(pm, packageName, packageInfo.versionCode);
                if (type == 2){
                    cacheInfo.setInstalled(false);
                }else {
                    cacheInfo.setInstalled(true);
                }
                totalApkSize += file.length();
                cacheInfo.setCacheSize(file.length());
                apkInfos.add(cacheInfo);
            }
        } else {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File fileStr : files) {
                    findAllAPKFile(fileStr);
                }
            }
        }
    }

    /**
     * 判断该应用在手机中的安装情况
     * @param pm                   PackageManager
     * @param packageName  要判断应用的包名
     * @param versionCode     要判断应用的版本号
     */
    private int doType(PackageManager pm, String packageName, int versionCode) {
        List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pi : pakageinfos) {
            String pi_packageName = pi.packageName;
            int pi_versionCode = pi.versionCode;
            //如果这个包名在系统已经安装过的应用中存在
            if(packageName.endsWith(pi_packageName)){
                //Log.i("test","此应用安装过了");
                if(versionCode==pi_versionCode){
//                    Log.i("test", "已经安装，不用更新，可以卸载该应用");
                    return 0;
                }else if(versionCode>pi_versionCode){
//                    Log.i("test","已经安装，有更新");
                    return 1;
                }
            }
        }
//        Log.i("test","未安装该应用，可以安装");
        return 2;
    }


    @Override
    public void onBackPressed() {
        if (cacheDetailFragment != null && cacheDetailFragment.isVisible()){
            getSupportFragmentManager().beginTransaction()
                    .hide(cacheDetailFragment).commit();
        }else {
            finish();
        }
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

    }

    private void refreshSizeHint(){
        String allSize = Formatter.formatFileSize(this, totalSize);
        tvSuffix.setText(allSize.substring(allSize.length() - 2));
        tvTotalSize.setText(allSize.substring(0, allSize.length() - 2));
    }

    public void resetSize(long size){
        totalSize = size;
        refreshSizeHint();
    }


    class ClearCacheObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName, final boolean succeeded) {
            showToast("清除==" + succeeded);
            handler.sendEmptyMessage(3);
        }
    }
}
