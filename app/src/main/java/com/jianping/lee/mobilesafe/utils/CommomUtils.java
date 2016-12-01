package com.jianping.lee.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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


}
