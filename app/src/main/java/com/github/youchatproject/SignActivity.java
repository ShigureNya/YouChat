package com.github.youchatproject;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.youchatproject.beans.BmobUserInfo;
import com.github.youchatproject.bmob_im.HuanXinUtil;
import com.github.youchatproject.databinding.ActivitySignBinding;
import com.github.youchatproject.listener.OnSignSuccessListener;
import com.github.youchatproject.tools.KeyBoardUtil;
import com.github.youchatproject.tools.SnackBarUtil;
import com.github.youchatproject.view.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 作者： guhaoran
 * 创建于： 2017/5/31
 * 包名： io.github.youchart
 * 文档描述：
 */
public class SignActivity extends BaseActivity {
    public ActivitySignBinding binding = null;
    @InjectView(R.id.sign_get_phone_code)
    Button signGetPhoneCode;
    @InjectView(R.id.sign_button)
    Button signButton;

    @Override
    public void initParams(Bundle params) {
        setSteepStatusBar(false);
    }

    @Override
    public void bindLayout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign);
    }

    @Override
    public void initView(View view) {
        binding.signToolbar.setTitle("");
        setSupportActionBar(binding.signToolbar);
        binding.signToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @OnClick({R.id.sign_get_phone_code, R.id.sign_button})
    public void onViewClicked(View view) {
        KeyBoardUtil.hideKeyBoard(this,binding.signInputPhone);
        String phone = binding.signInputPhone.getText().toString();
        switch (view.getId()) {
            case R.id.sign_get_phone_code:
                getPhoneCode(phone);
                break;
            case R.id.sign_button:
                String code = binding.signInputPhoneCode.getText().toString();
                verifyPhone(phone,code);    //验证手机号
                break;
        }
    }

    /**
     * [注册]
     * @param phone 手机号
     * @param password 密码
     */
    public void sign(final String phone , final String password){
        if(password.trim().equals("")){
            $snackbar(binding.signSnackBar,R.string.sign_input_password_error,true);
            return ;
        }
        //注册流程
        BmobUserInfo bmobUser = new BmobUserInfo() ;
        bmobUser.setUsername(phone);
        bmobUser.setPassword(password);
        bmobUser.setMobilePhoneNumber(phone);
        bmobUser.setMobilePhoneNumberVerified(true);
        bmobUser.signUp(new SaveListener<BmobUserInfo>() {
            @Override
            public void done(BmobUserInfo bmobUser, BmobException e) {
                //首先判断Bmob注册是否成功 如果成功 注册环信SDK
                if(e == null){
                    imSign(phone,password);
                    //注册不成功直接返回出来
                }else{
                    $error(e.toString());
                    $snackbar(binding.signSnackBar,R.string.sign_error,true);
                }
            }
        });
    }

    /**
     * [获得短信验证码]
     * @param phone 手机号
     */
    public void getPhoneCode(String phone){
        if(phone.trim().equals("")){
            $snackbar(binding.signSnackBar,R.string.sign_input_phone_error,true);
            return ;
        }
        //请求短信验证码的服务
        BmobSMS.requestSMSCode(phone, "获得注册短信验证码",new QueryListener<Integer>() {
            @Override
            public void done(Integer smsId,BmobException ex) {
                if(ex==null){//验证码发送成功
                    Log.i("smile", "短信id："+smsId);//用于查询本次短信发送详情
                    runTimer();
                }
            }
        });
    }

    /**
     * [验证手机号]
     * @param phone 手机号
     * @param code 验证码
     */
    public void verifyPhone(final String phone , String code){
        //显示进度条确认信息情况
        if(code.trim().equals("")){
            $snackbar(binding.signSnackBar,R.string.sign_input_code_error,true);
            return;
        }
        if(phone.trim().equals("")){
            $snackbar(binding.signSnackBar,R.string.sign_input_phone_error,true);
        }
        $snackbar(binding.signSnackBar,R.string.sign_complate_content,false);
        BmobSMS.verifySmsCode(phone, code, new UpdateListener() {
            @Override
            public void done(BmobException ex) {
                if(ex==null){//短信验证码已验证成功
                    String password = binding.signInputPassword.getText().toString();
                    sign(phone,password);
                }else{
                    $error("验证失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                    $snackbar(binding.signSnackBar,R.string.sign_code_error,true);
                }
            }
        });
    }

    //验证码倒计时
    Timer timer = null ;
    public int time  = 60 ;
    public void runTimer(){
        timer=new Timer();
        TimerTask task=new TimerTask() {

            @Override
            public void run(){
                time--;
                Message msg=handler.obtainMessage();
                msg.what=1;
                handler.sendMessage(msg);

            }
        };
        timer.schedule(task, 100, 1000);
    }

    Handler handler =new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if(time>0){
                        binding.signGetPhoneCode.setEnabled(false);
                        binding.signGetPhoneCode.setTextColor(getResources().getColor(R.color.secondTextColor));
                        binding.signGetPhoneCode.setText(getString(R.string.sign_wait_phone_code_button_text)+"("+time+")");
                    }else{
                        timer.cancel();
                        binding.signGetPhoneCode.setEnabled(true);
                        binding.signGetPhoneCode.setTextColor(getResources().getColor(R.color.mainTextColor));
                        binding.signGetPhoneCode.setText(getString(R.string.sign_get_phone_code_button_text));
                    }
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
            timer = null ;
        }
    }

    /**
     * [登录到环信SDK]
     * @param phone 手机号
     * @param password 密码
     */
    public void imSign(String phone , String password){
        HuanXinUtil.getInterface().signHuanXin(phone, password, new OnSignSuccessListener() {
            @Override
            public void onSuccess() {
                //如果环信
                Snackbar snackbar = SnackBarUtil.shortDefaultSnackbar(binding.signSnackBar,getString(R.string.sign_complate),getResources().getColor(R.color.light_blue));
                SnackBarUtil.setSnackbarMessageColor(snackbar,getResources().getColor(R.color.iconTextColor));
                snackbar.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },3000);
            }
            @Override
            public void onError(Throwable e) {
                $error(e.toString());
                $snackbar(binding.signSnackBar,R.string.sign_error,true);
            }
        });
    }

}
