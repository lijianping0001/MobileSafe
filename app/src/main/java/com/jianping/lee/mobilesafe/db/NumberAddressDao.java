package com.jianping.lee.mobilesafe.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Li on 2016/12/13.
 */
public class NumberAddressDao {

    /**
     * 返回电话号码的归属地信息
     *
     * @param number
     *            电话号码
     * @return 归属地信息
     */
    public static String getAddress(String number) {
        // 把location里面的内容设置为电话号码
        String location = number;
        // 检测number是手机号 还是 固定电话
        // 13xxxxx 15x xxx 18xx 17xx
        // ^1[3578]\d{9}$
        if (number.matches("^1[3578]\\d{9}$")) {// 手机号码
            // 把apk里面的数据库文件 ,拷贝到手机的data/data/包名/files目录
            SQLiteDatabase db = SQLiteDatabase.openDatabase(
                    "/data/data/com.jianping.lee.mobilesafe/files/address.db", null,
                    SQLiteDatabase.OPEN_READONLY);
            Cursor cursor = db
                    .rawQuery(
                            "select location from data2 where id = (select outkey from data1 where id=?)",
                            new String[] { number.substring(0, 7) });
            if (cursor.moveToFirst()) {
                location = cursor.getString(0);
            }
            cursor.close();
            db.close();
        } else {// 其他电话
            switch (number.length()) {
                case 3:
                    if ("110".equals(number)) {
                        return "匪警";
                    } else if ("120".equals(number)) {
                        return "急救电话";
                    } else if ("119".equals(number)) {
                        return "火警";
                    }
                    break;
                case 4:
                    return "模拟器";
                case 5:
                    return "客服电话";
                case 7:
                    if ((!number.startsWith("0")) && (!number.startsWith("1"))) {
                        return "本地号码";
                    }
                    break;
                case 8:
                    if ((!number.startsWith("0")) && (!number.startsWith("1"))) {
                        return "本地号码";
                    }
                    break;

                default:
                    if(number.length()>=10&&number.startsWith("0")){
                        //长途电话
                        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                                "/data/data/com.jianping.lee.mobilesafe/files/address.db", null,
                                SQLiteDatabase.OPEN_READONLY);
                        Cursor cursor = db.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 3)});
                        if(cursor.moveToNext()){
                            location = cursor.getString(0).substring(0, cursor.getString(0).length()-2);
                        }
                        cursor.close();
                        cursor = db.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 4)});
                        if(cursor.moveToNext()){
                            location = cursor.getString(0).substring(0, cursor.getString(0).length()-2);
                        }
                        cursor.close();
                        db.close();
                    }
                    break;
            }
        }
        return location;
    }
}
