package com.jianping.lee.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.jianping.lee.mobilesafe.model.ContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Li on 2016/11/30.
 */
public class CommomUtils {

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2)
                + Math.abs(y1 - y2) * Math.abs(y1 - y2));
    }

    public static double pointTotoDegrees(double x, double y) {
        return Math.toDegrees(Math.atan2(x, y));
    }

    public static boolean checkInRound(float sx, float sy, float r, float x,
                                       float y) {
        return Math.sqrt((sx - x) * (sx - x) + (sy - y) * (sy - y)) < r;
    }

    /**
     * 联系人信息的工具类
     *
     * @param context
     *            上下文
     * @return
     */
    public static List<ContactInfo> getContactInfos(Context context) {
        // 内容提供者的解析器
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri datauri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
                null, null, null);
        List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            //System.out.println("id=" + id);
            if (id != null) {
                ContactInfo info = new ContactInfo();
                Cursor datacursor = resolver.query(datauri, new String[] {
                                "data1", "mimetype" }, "raw_contact_id=?",
                        new String[] { id }, null);
                while (datacursor.moveToNext()) {
                    String data1 = datacursor.getString(0);
                    String mimetype = datacursor.getString(1);
                    if ("vnd.android.cursor.item/name".equals(mimetype)) {
                        // 姓名
                        info.setName(data1);
                    } else if ("vnd.android.cursor.item/phone_v2"
                            .equals(mimetype)) {
                        // 电话
                        info.setPhone(data1);
                    } else if ("vnd.android.cursor.item/email_v2"
                            .equals(mimetype)) {
                        // 邮箱
                        info.setEmail(data1);
                    }
                }
                contactInfos.add(info);
            }
        }
        return contactInfos;
    }


}
