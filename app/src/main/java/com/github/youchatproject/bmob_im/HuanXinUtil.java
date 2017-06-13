package com.github.youchatproject.bmob_im;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.github.youchatproject.R;
import com.github.youchatproject.beans.BmobUserInfo;
import com.github.youchatproject.listener.OnLoginSuccessListener;
import com.github.youchatproject.listener.OnSignSuccessListener;
import com.github.youchatproject.listener.OnUserInfoResultListener;
import com.github.youchatproject.tools.GlideUtil;
import com.github.youchatproject.tools.Loger;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import cn.bmob.v3.datatype.BmobFile;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/5
 * 包名： com.github.youchatproject.bmob_im
 * 文档描述：环信SDK工具包
 */
public class HuanXinUtil {
    private HuanXinUtil(){}

    public static HuanXinUtil huanXinUtil = new HuanXinUtil();
    //单例
    public static HuanXinUtil getInterface(){
        if(huanXinUtil != null){
            return huanXinUtil ;
        }
        return null;
    }

    public void signHuanXin(final String phone , final String password , final OnSignSuccessListener listener){
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    EMClient.getInstance().createAccount(phone, password);//同步方法
                    subscriber.onNext("注册成功");
                    subscriber.onCompleted();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    subscriber.onNext("注册失败");
                    subscriber.onError(new Throwable(e));
                }
            }
        });
        observable.subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String string) {
                        Loger.i(string);
                    }

                    @Override
                    public void onCompleted() {
                        listener.onSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onError(e);
                    }
                });
    }

    /**
     * [登录到环信]
     * @param phone 手机号
     * @param password 密码
     * @param listener 返回接口
     */
    public void loginHuanXin(String phone , String password , final OnLoginSuccessListener listener){
        EMClient.getInstance().login(phone,password,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
                listener.onSuccess();
            }

            @Override
            public void onProgress(int progress, String status) {
                listener.onProgress(progress,status);
            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
                listener.onError(code,message);
            }
        });
    }

    /**
     * @param mContext 上下文
     * @param username 用户名
     * @param userImage 设置的ImageView
     */
    public void getUserInfoImage(final Context mContext , String username, final ImageView userImage){
        FriendsUtil.getInterface().selectorUserInfo(username, new OnUserInfoResultListener() {
            @Override
            public void onSuccess(BmobUserInfo info) {
                BmobFile picFile = info.getUserPicture();
                if(picFile != null){
                    String picFileUrl = picFile.getFileUrl();
                    Loger.d("图片地址:"+picFileUrl);
                    GlideUtil.loadImage(picFileUrl,mContext,userImage);
                }else{
                    userImage.setImageResource(R.drawable.vector_drawable_default_user_picture_logo);
                }
            }

            @Override
            public void onError(Throwable e) {
                userImage.setImageResource(R.drawable.vector_drawable_default_user_picture_logo);
            }
        });
    }

    /**
     * [得到最后一条消息类型]
     * @param message 消息体对象
     * @return
     */
    public String getLastMessageType(EMMessage message){
        String str = "" ;
        switch(message.getType()){
            //图片消息
            case IMAGE:{
                EMImageMessageBody imageBody = (EMImageMessageBody) message.getBody();
                str = "[图片文件]";
                break;
            }
            case TXT:{
                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                str = txtBody.getMessage();
                break;
            }
            case FILE:
                EMFileMessageBody fileBody = (EMFileMessageBody) message.getBody();
                str = "[文件]"+fileBody.getFileName();
                break;
            case VOICE:
                EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) message.getBody();
                str = "[语音]";
                break;
            case VIDEO:
                EMVideoMessageBody videoMessageBody = (EMVideoMessageBody) message.getBody();
                str = "[视频文件]";
                break;
        }
        return str;
    }
}
