package com.jianping.lee.mobilesafe.model;

/**
 * Created by Li on 2016/11/28.
 */
public class Icon {

    int backgroundId;

    int imageId;

    String name;

    public Icon(){

    }

    public Icon(int backgroundId, int imageId, String name){
        this.backgroundId = backgroundId;
        this.imageId = imageId;
        this.name = name;
    }

    public int getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(int backgroundId) {
        this.backgroundId = backgroundId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
