package com.github.youchatproject.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/10
 * 包名： com.github.youchatproject.dao
 * 文档描述：ConversationDao帮助类
 */
public class GreenDaoHelper {
    private static DaoMaster.DevOpenHelper mHelper;
    private static SQLiteDatabase db;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;

    /**
     * 初始化greenDao，这个操作建议在Application初始化的时候添加；
     */
    public static void initDatabase(Context context) {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(context, "db_oracle", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }
    public static DaoSession getDaoSession() {
        return mDaoSession;
    }
    public static SQLiteDatabase getDb() {
        return db;
    }


}
