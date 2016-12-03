package com.jianping.lee.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class IntentUtils {
	/**
	 * 开启一个activity
	 * @param context
	 * @param cls
	 */
	public static void startActivity(Activity context, Class<?> cls){
		Intent intent = new Intent(context,cls);
		context.startActivity(intent);
	}

	public static void startActivityWithAnim(Activity context, Class<?> cls, int enterAnim, int exitAnim){
		Intent intent = new Intent(context,cls);
		context.startActivity(intent);
		context.overridePendingTransition(enterAnim, exitAnim);
	}
	
	/**
	 * 开启一个activity
	 * @param context
	 * @param cls
	 */
	public static void startActivityAndFinish(Activity context, Class<?> cls){
		Intent intent = new Intent(context,cls);
		context.startActivity(intent);
		context.finish();
	}
	
	/**
	 * 开启一个activity
	 * @param context
	 * @param cls
	 * @param delaytime 延迟执行的时间毫秒
	 */
	public static void startActivityForDelay(final Activity context,final Class<?> cls,final long delaytime){
		new Thread(){
			public void run() {
				try {
					Thread.sleep(delaytime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(context,cls);
				context.startActivity(intent);
			};
		}.start();
	}
	
	/**
	 * 开启一个activity
	 * @param context
	 * @param cls
	 * @param delaytime 延迟执行的时间毫秒
	 */
	public static void startActivityForDelayAndFinish(final Activity context,final Class<?> cls,final long delaytime){
		new Thread(){
			public void run() {
				try {
					Thread.sleep(delaytime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(context,cls);
				context.startActivity(intent);
				context.finish();
			};
		}.start();
	}
}
