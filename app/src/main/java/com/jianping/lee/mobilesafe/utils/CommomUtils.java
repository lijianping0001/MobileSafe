package com.jianping.lee.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.jianping.lee.mobilesafe.model.ContactInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    /**
     * 获取文件的md5
     * @param inputFile
     * @return
     */
    public static String getMd5ByFilePath(String inputFile){
        // 缓冲区大小
        int bufferSize = 256 * 1024;
        String value = null;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;

        // 拿到一个MD5转换器（同样，这里可以换成SHA1）
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            // 使用DigestInputStream
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream,messageDigest);
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer =new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0);
            // 获取最终的MessageDigest
            messageDigest= digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            BigInteger bi = new BigInteger(1, resultByteArray);
            value = bi.toString(16);
            int valueLen = value.length();

            if (valueLen < 32){
                for (int i = 0; i < (32 - valueLen); i++){
                    value = "0" + value;
                }
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }finally {
            if(null != fileInputStream) {
                try {
                    fileInputStream.close();
                    if (digestInputStream != null) {
                        digestInputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    /**
     * 获取数组的md5
     * @param bytes
     * @return
     */
    public static String getMd5ByBytes(byte[] bytes){
        String value = null;

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
            int valueLen = value.length();
            if (valueLen < 32){
                for (int i = 0; i < (32 - valueLen); i++){
                    value = "0" + value;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return value;
    }

}
