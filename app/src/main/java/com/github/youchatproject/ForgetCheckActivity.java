package com.github.youchatproject;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.youchatproject.databinding.ActivityForgetPwdCheckBinding;
import com.github.youchatproject.tools.KeyBoardUtil;
import com.github.youchatproject.view.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/1
 * 包名： io.github.youchart
 * 文档描述：
 */
public class ForgetCheckActivity extends BaseActivity {
    public ActivityForgetPwdCheckBinding binding = null;
    @InjectView(R.id.forget_get_phone_code)
    Button forgetGetPhoneCode;
    @InjectView(R.id.forget_next_button)
    Button forgetNextButton;

    public Integer messageId ;
    @Override
    public void initParams(Bundle params) {

    }

    @Override
    public void bindLayout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget_pwd_check);
    }

    @Override
    public void initView(View view) {
        binding.forgetCheckToolbar.setTitle("");
        setSupportActionBar(binding.forgetCheckToolbar);
        binding.forgetCheckToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @OnClick({R.id.forget_get_phone_code, R.id.forget_next_button})
    public void onViewClicked(View view) {
        String phone = binding.forgetInputPhone.getText().toString();
        KeyBoardUtil.hideKeyBoard(this,binding.forgetInputPhoneCode);
        switch (view.getId()) {
            case R.id.forget_get_phone_code:
                getPhoneCode(phone);
                break;
            case R.id.forget_next_button:
                String code = binding.forgetInputPhoneCode.getText().toString();
                verifyPhone(phone,code);
                break;
        }
    }

    /**
     * [获得短信验证码]
     * @param phone 手机号
     */
    public void getPhoneCode(String phone){
        if(phone.trim().equals("")){
            $snackbar(binding.forgetCheckSnackbar,R.string.forget_input_phone_error,true);
            return ;
        }
        //请求短信验证码的服务
        BmobSMS.requestSMSCode(phone, "获得注册短信验证码",new QueryListener<Integer>() {
            @Override
            public void done(Integer smsId,BmobException ex) {
                if(ex==null){//验证码发送成功
                    Log.i("smile", "短信id："+smsId);//用于查询本次短信发送详情
                    messageId = smsId ;
                    runTimer();
                }
            }
        });
    }

    /**
     * [验证手机号] -- 开发者在进行重置密码操作时，无需调用verifySmsCode接口去验证该验证码的有效性。
     * @param phone 手机号
     * @param code 验证码
     */
    public void verifyPhone(final String phone , final String code){
        //显示进度条确认信息情况
        if(code.trim().equals("")){
            $snackbar(binding.forgetCheckSnackbar,R.string.forget_input_code_error,true);
            return;
        }
        if(phone.trim().equals("")){
            $snackbar(binding.forgetCheckSnackbar,R.string.forget_input_phone_error,true);
        }
        Bundle userBundle = new Bundle();
        userBundle.putString("MessageId",code);
        startActivity(ForgetChangeActivity.class,userBundle);
        finish();
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
                        binding.forgetGetPhoneCode.setEnabled(false);
                        binding.forgetGetPhoneCode.setTextColor(getResources().getColor(R.color.secondTextColor));
                        binding.forgetGetPhoneCode.setText(getString(R.string.forget_wait_phone_code_button_text)+"("+time+")");
                    }else{
                        timer.cancel();
                        binding.forgetGetPhoneCode.setEnabled(true);
                        binding.forgetGetPhoneCode.setTextColor(getResources().getColor(R.color.mainTextColor));
                        binding.forgetGetPhoneCode.setText(getString(R.string.forget_get_phone_code_button_text));
                    }
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(timer!=null){
            timer.cancel();
            timer = null ;
            handler = null;
        }
    }
}
