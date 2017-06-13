package com.github.youchatproject;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.youchatproject.beans.BmobUserInfo;
import com.github.youchatproject.bmob_im.HuanXinUtil;
import com.github.youchatproject.dao.GreenDaoUtil;
import com.github.youchatproject.dao.SystemInfos;
import com.github.youchatproject.databinding.ActivityLoginBinding;
import com.github.youchatproject.listener.OnLoginSuccessListener;
import com.github.youchatproject.service.ConnectService;
import com.github.youchatproject.tools.KeyBoardUtil;
import com.github.youchatproject.view.BaseActivity;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 作者： guhaoran
 * 创建于： 2017/5/31
 * 包名： PACKAGE_NAME
 * 文档描述：登录页面
 */
public class LoginActivity extends BaseActivity {
    public ActivityLoginBinding binding = null;
    @InjectView(R.id.login_button)
    Button loginButton;
    @InjectView(R.id.login_forget_pwd_button)
    TextView loginForgetPwdButton;
    @InjectView(R.id.login_sign_button)
    TextView loginSignButton;
    @InjectView(R.id.login_quick_qq_button)
    ImageView loginQuickQqButton;
    @InjectView(R.id.login_quick_wechat)
    ImageView loginQuickWechat;
    @InjectView(R.id.login_quick_weibo)
    ImageView loginQuickWeibo;

    @Override
    public void initParams(Bundle params) {
        setSteepStatusBar(true);
    }

    @Override
    public void bindLayout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
    }

    @Override
    public void initView(View view) {
        getKeyBoardHeight();    //得到软键盘高度
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @OnClick({R.id.login_button, R.id.login_forget_pwd_button, R.id.login_sign_button, R.id.login_quick_qq_button, R.id.login_quick_wechat, R.id.login_quick_weibo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                String userPhone = binding.loginInputPhone.getText().toString();
                String userPassword = binding.loginInputPassword.getText().toString();
                login(userPhone,userPassword);
                break;
            case R.id.login_forget_pwd_button:
                startActivity(ForgetCheckActivity.class);
                break;
            case R.id.login_sign_button:
                startActivity(SignActivity.class);
                break;
            case R.id.login_quick_qq_button:
                break;
            case R.id.login_quick_wechat:
                break;
            case R.id.login_quick_weibo:
                break;
        }
    }

    public void login(final String userPhone , final String userPassword){
        if(userPhone.trim().equals("")){
            $toast(R.string.login_phone_error);
            return ;
        }
        if(userPassword.trim().equals("")){
            $toast(R.string.login_password_error);
            return ;
        }
        BmobUser bmobUser = new BmobUser();
        bmobUser.setUsername(userPhone);
        bmobUser.setPassword(userPassword);
        bmobUser.login(new SaveListener<BmobUserInfo>() {
            @Override
            public void done(BmobUserInfo bmobUser, BmobException e) {
                if(e == null){
                    imLogin(userPhone,userPassword);
                }else{
                    $error(e.toString());
                    $toast(R.string.login_input_error);
                }
            }
        });
    }

    /**
     * 登录到环信SDK
     */
    public void imLogin(String phone , String password){
        HuanXinUtil.getInterface().loginHuanXin(phone, password, new OnLoginSuccessListener() {
            @Override
            public void onSuccess() {
                //登录成功
                startConnectionService();   //登录到通讯服务器服务
                startActivity(MainActivity.class);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                $Log("进度:"+progress+"/状态:"+status);
            }

            @Override
            public void onError(int code, String message) {
                $error(message);
                $toast(R.string.login_input_error);
            }
        });
    }

    /**
     * [登录到通讯服务器服务]
     */
    public void startConnectionService(){
        Intent startIntent = new Intent();
        startIntent.setClass(this, ConnectService.class);
        startService(startIntent);
    }

    public void getKeyBoardHeight(){
        KeyBoardUtil.setListener(this, new KeyBoardUtil.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                SystemInfos info = new SystemInfos();
                info.setSystemPreferences("KeyBoardHeight");
                info.setSystemContentIte(height);
                long rowId = GreenDaoUtil.getInstance().insertConversation(info);
                $Log("行ID等于:"+rowId);
            }

            @Override
            public void keyBoardHide(int height) {
            }
        });
    }
}
