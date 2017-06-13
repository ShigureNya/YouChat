package com.github.youchatproject;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.youchatproject.databinding.ActivityForgetPwdChangeBinding;
import com.github.youchatproject.tools.KeyBoardUtil;
import com.github.youchatproject.tools.SnackBarUtil;
import com.github.youchatproject.view.BaseActivity;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/1
 * 包名： io.github.youchart
 * 文档描述：
 */
public class ForgetChangeActivity extends BaseActivity {
    @InjectView(R.id.forget_next_button)
    Button forgetNextButton;

    public ActivityForgetPwdChangeBinding binding = null;
    public String messageId;
    @Override
    public void initParams(Bundle params) {
    }

    @Override
    public void bindLayout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget_pwd_change);
    }

    @Override
    public void initView(View view) {
        binding.forgetCheckToolbar.setTitle("");
        setSupportActionBar(binding.forgetCheckToolbar);
        binding.forgetCheckToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        Bundle resultBundle = getIntent().getExtras();
        messageId = resultBundle.getString("MessageId");
        $Log("消息ID"+messageId);
    }

    @OnClick(R.id.forget_next_button)
    public void onViewClicked() {
        KeyBoardUtil.hideKeyBoard(this,binding.forgetReinputNewPassword);
        String password = binding.forgetInputNewPassword.getText().toString();
        String newPassword = binding.forgetReinputNewPassword.getText().toString();
        if(password.trim().equals("") || newPassword.trim().equals("")){
            $snackbar(binding.forgetChangeSnackBar,R.string.forget_input_password_error,true);
            return ;
        }
        if(!password.equals(newPassword)){
            $snackbar(binding.forgetChangeSnackBar,R.string.forget_new_password_error,true);
            return ;
        }
        resetPassword(newPassword); //重置密码
    }

    /**
     * 重置密码
     * @param newPassword 新密码
     */
    public void resetPassword(String newPassword){
        BmobUser.resetPasswordBySMSCode(messageId,newPassword, new UpdateListener() {

            @Override
            public void done(BmobException ex) {
                if(ex==null){
                    Log.i("smile", "密码重置成功");
                    Snackbar snackbar = SnackBarUtil.shortDefaultSnackbar(binding.forgetChangeSnackBar,getString(R.string.forget_change_complate),getResources().getColor(R.color.light_blue));
                    SnackBarUtil.setSnackbarMessageColor(snackbar,getResources().getColor(R.color.iconTextColor));
                    snackbar.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },3000);
                }else{
                    Log.i("smile", "重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                    $snackbar(binding.forgetChangeSnackBar,R.string.forget_change_error,true);
                }
            }
        });
    }
}
