package com.github.youchatproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.github.youchatproject.tools.Loger;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/6
 * 包名： com.github.youchatproject.service
 * 文档描述：消息服务
 */
public class MessageService extends Service {
    public Intent messsageIntent = null ;
    public Intent friendIntent = null ;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Loger.i("消息服务","消息服务连接成功");
        messsageIntent = new Intent("MessageReceive");
        friendIntent = new Intent("ContactBroadcast");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        EMClient.getInstance().contactManager().setContactListener(contactListener);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 消息回调
     */
    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            sendBroadcast(messsageIntent);
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    /**
     * 联系人回调
     */
    EMContactListener contactListener = new EMContactListener() {
        @Override
        public void onContactAdded(String username) {
            //增加了好友
            Loger.i("增加了联系人");
            friendIntent.putExtra("Result","Added");
            sendBroadcast(friendIntent);
        }

        @Override
        public void onContactDeleted(String username) {
            //被删除了
            Loger.i("被删除了");
            friendIntent.putExtra("Result","Deleted");
            friendIntent.putExtra("Username",username);
            sendBroadcast(friendIntent);
        }

        @Override
        public void onContactInvited(String username, String reason) {
            //收到好友邀请
            Loger.i("收到好友申请");
            friendIntent.putExtra("Result","Invited");
            friendIntent.putExtra("Username",username);
            friendIntent.putExtra("Reason",reason);
            sendBroadcast(friendIntent);
        }

        @Override
        public void onFriendRequestAccepted(String username) {
            //好友请求被接受了
            Loger.i("好友请求被接受了");
            friendIntent.putExtra("Result","RequestAccepted");
            friendIntent.putExtra("Username",username);
            sendBroadcast(friendIntent);
        }

        @Override
        public void onFriendRequestDeclined(String username) {
            //好友请求被拒绝了
            Loger.i("好友请求被拒绝了");
            friendIntent.putExtra("Result","RequestDeclined");
            friendIntent.putExtra("Username",username);
            sendBroadcast(friendIntent);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        EMClient.getInstance().contactManager().removeContactListener(contactListener);
        Loger.i("消息服务","消息服务连接关闭");
    }
}
