package com.github.youchatproject.dao;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/10
 * 包名： com.github.youchatproject.dao
 * 文档描述：增删改查工具类
 */
public class GreenDaoUtil {
    public static SystemInfosDao systemInfosDao ;

    private GreenDaoUtil(){}
    public static GreenDaoUtil instance = null;
    public static GreenDaoUtil getInstance(){
        if(instance == null){
            instance = new GreenDaoUtil();
        }
        //在静态中初始化函数对象
        if(systemInfosDao == null){
            systemInfosDao = GreenDaoHelper.getDaoSession().getSystemInfosDao();
        }
        return instance;
    }

    /**
     * [向会话列表数据库中插入数据]
     * @param entity 装载了数据的entity对象
     */
    public long insertConversation(SystemInfos entity){
        long id = systemInfosDao.insertOrReplace(entity);
        return id ;
    }

    /**
     * [根据ID查询会话]
     * @param preferencesId 当前用户的id、
     * @return 当前用户的会话列表
     */
    public SystemInfos queryPreferencesById(String preferencesId){
        SystemInfos entity = systemInfosDao.queryBuilder().where(SystemInfosDao.Properties.SystemPreferences.eq(preferencesId)).unique();
        return entity;
    }
}
