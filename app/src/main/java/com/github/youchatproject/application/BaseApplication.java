package com.github.youchatproject.application;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;

import com.github.youchatproject.dao.GreenDaoHelper;
import com.github.youchatproject.tools.Loger;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * 作者： guhaoran
 * 创建于： 2017/5/31
 * 包名： io.github.youchart.application
 * 文档描述： 基础Application
 */
public class BaseApplication extends Application {
    public static boolean isDebug = false ;  //默认为false
    public static String appName = null ;
    public Typeface fontType ;

    public String applicationKey = "580b2eb7c706133d0e8cdc94fd8ed376";
    @Override
    public void onCreate() {
        super.onCreate();
        isDebug = getIsDebug();   //查看是否为debug开发模式
        appName = getAppName(); //获得App名称
        openBmobSDK();  //初始化Bmob
        openHuanXinSDK();   //初始化环信SDK
        openGreenDao(); //启动Green数据库
        Loger.i("系统正常，正在启动");
        Loger.d("系统正常，正在启动");
        Loger.e("系统正常，正在启动");
    }

    public boolean getIsDebug(){
        ApplicationInfo info= getApplicationContext().getApplicationInfo();
        return (info.flags& ApplicationInfo.FLAG_DEBUGGABLE)!=0;
    }

    public String getAppName(){
        try {
            PackageManager manager = getApplicationContext().getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(
                    getApplicationContext().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null ;
    }

    public void openBmobSDK(){
        BmobConfig config =new BmobConfig.Builder(this)
        ////设置appkey
        .setApplicationId(applicationKey)
        ////文件分片上传时每片的大小（单位字节），默认512*1024
        .setUploadBlockSize(1024*1024)
        ////文件的过期时间(单位为秒)：默认1800s
        .setFileExpiration(2500)
        .build();
        Bmob.initialize(config);
        Loger.d("系统初始化装弹","Bmob装填完成!");
    }

    public void openHuanXinSDK(){
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        //初始化
        EMClient.getInstance().init(getApplicationContext(), options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
        Loger.d("系统初始化装弹","环信装填完成!");
    }

    /**
     * 开启Green数据库
     */
    public void openGreenDao(){
        GreenDaoHelper.initDatabase(getApplicationContext());
        Loger.d("系统初始化装弹","GreenDao装填完成!");
    }
}
