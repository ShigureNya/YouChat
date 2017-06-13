package com.github.youchatproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.github.youchatproject.beans.BmobUserInfo;
import com.github.youchatproject.databinding.ActivityControllerBinding;
import com.github.youchatproject.service.ConnectService;
import com.github.youchatproject.system.FileStorageInfo;
import com.github.youchatproject.tools.DialogUtil;
import com.github.youchatproject.tools.PermissionUtil;
import com.github.youchatproject.view.BaseActivity;
import com.hyphenate.chat.EMClient;

import cn.bmob.v3.BmobUser;
import cn.refactor.lib.colordialog.PromptDialog;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/3
 * 包名： com.github.youchatproject
 * 文档描述：e
 */
public class LoginControllerActivity extends BaseActivity {
    public ActivityControllerBinding binding = null;
    private static final int REQUEST_CODE = 0; // 请求码

    // 所需的全部权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    public void initParams(Bundle params) {

    }

    @Override
    public void bindLayout() {
        binding = DataBindingUtil.setContentView(this,R.layout.activity_controller);
    }

    @Override
    public void initView(View view) {
    }

    @Override
    public void doBusiness(Context mContext) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(PERMISSIONS);
        }else{
            start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        showProgressDialog(getString(R.string.check_user_info),"正在检查帐号登录状态，请稍候...",false);
    }

    public void startConnectionService(){
        Intent startIntent = new Intent();
        startIntent.setClass(this, ConnectService.class);
        startService(startIntent);
    }

    // 请求权限兼容低版本
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
    }

    //处理一般权限
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (PermissionUtil.hasAllPermissionsGranted(grantResults)) {
                    $Log("获得了所有的权限");
                    start();
                } else {
                    // Permission Denied
                    PromptDialog dialog = DialogUtil.buildPromptDialog(LoginControllerActivity.this,"权限获取失败","程序即将退出，请重新获取权限信息","好的");
                    dialog.setDialogType(PromptDialog.DIALOG_TYPE_WRONG);
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                    dialog.show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void start(){
        FileStorageInfo.getInstance().initFileDirs();   //初始化所有的目录
        BmobUserInfo bmobUser = BmobUser.getCurrentUser(BmobUserInfo.class);
        if(bmobUser != null){
            // 允许用户使用应用
            //保证用户进入主界面时可以加载到会话列表和联系人列表
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
            //启动并进入主界面
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            startActivity(MainActivity.class);
            startConnectionService();   //启动通讯Service监听
        }else{
            //缓存用户对象为空时， 可打开用户注册界面…
            startActivity(LoginActivity.class);
        }
        finish();
    }
}
