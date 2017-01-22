package com.jianping.lee.mobilesafe.model;

/**
 * Created by Li on 2017/1/7.
 */
public class SpeedInfo {

    private double speed;

    private int transferredByte;

    private int totalByte;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getTransferredByte() {
        return transferredByte;
    }

    public void setTransferredByte(int transferredByte) {
        this.transferredByte = transferredByte;
    }

    public int getTotalByte() {
        return totalByte;
    }

    public void setTotalByte(int totalByte) {
        this.totalByte = totalByte;
    }
}
