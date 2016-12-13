package com.lee;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyGreendaoGenerator {
    //辅助文件生成的相对路径
    public static final String DAO_PATH = "./app/src/main/java-gen";
    //辅助文件的包名
    public static final String PACKAGE_NAME = "com.jianping.lee.greendao";
    //数据库的版本号
    public static final int DATA_VERSION_CODE = 2;

    public static void main(String[] args) throws Exception {

        Schema schema = new Schema(DATA_VERSION_CODE, PACKAGE_NAME);
        addBlackNum(schema, "BlackNum");
        addLockApp(schema, "LockedApp");
        //生成Dao文件路径
        new DaoGenerator().generateAll(schema, DAO_PATH);

    }

    /**
     * 添加不同的缓存表
     * @param schema
     * @param tableName
     */
    private static void addBlackNum(Schema schema, String tableName) {

        Entity blackNum = schema.addEntity(tableName);

        //主键id自增长
        blackNum.addIdProperty().primaryKey().autoincrement();
        //名字
        blackNum.addStringProperty("name");
        //电话号码
        blackNum.addStringProperty("phone");
        //mode
        blackNum.addIntProperty("mode");
        //index
        blackNum.addIntProperty("index");

    }

    private static void addLockApp(Schema schema, String tableName){
        Entity appinfo = schema.addEntity(tableName);

        //主键id自增长
        appinfo.addIdProperty().primaryKey().autoincrement();
        //包名
        appinfo.addStringProperty("packName");
        //应用名
        appinfo.addStringProperty("appName");

    }
}
