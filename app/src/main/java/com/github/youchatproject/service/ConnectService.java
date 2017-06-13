package com.github.youchatproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.github.youchatproject.R;
import com.github.youchatproject.tools.Loger;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/5
 * 包名： com.github.youchatproject.service
 * 文档描述：连接Service
 */
public class ConnectService extends Service {
    public static final int CONNECT_ERROR = 1 ;
    public static final int NETWORK_ERROR = 2 ;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        return super.onStartCommand(intent, flags, startId);
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            Loger.i("连接状态","连接成功");
        }
        @Override
        public void onDisconnected(final int error) {
            Loger.e("连接状态","丢失与服务器的连接，错误代码："+error);
            new Runnable() {
                @Override
                public void run() {
                    Message msg = mConnectionHandler.obtainMessage();
                    switch (error){
                        case EMError.USER_REMOVED:
                            // 显示帐号已经被移除
                            break;
                        case EMError.USER_LOGIN_ANOTHER_DEVICE:
                            // 显示帐号在其他设备登录
                            break;
                        default:
                            if (NetUtils.hasNetwork(getApplicationContext())) {
                                //连接不到聊天服务器
                                msg.what = CONNECT_ERROR;
                            } else {
                                //当前网络不可用，请检查网络设置
                                msg.what = NETWORK_ERROR;
                            }
                            break;
                    }
                    msg.sendToTarget();
                }
            };
        }
    }

    Handler mConnectionHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CONNECT_ERROR:
                    Toast.makeText(ConnectService.this, R.string.connect_error, Toast.LENGTH_SHORT).show();
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(ConnectService.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
