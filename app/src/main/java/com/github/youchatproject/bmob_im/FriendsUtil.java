package com.github.youchatproject.bmob_im;

import android.support.v4.util.Pair;

import com.github.youchatproject.beans.BmobUserInfo;
import com.github.youchatproject.listener.OnAddContactListener;
import com.github.youchatproject.listener.OnConversationResultListener;
import com.github.youchatproject.listener.OnDataResultListener;
import com.github.youchatproject.listener.OnSearchFriendListener;
import com.github.youchatproject.listener.OnUserInfoResultListener;
import com.github.youchatproject.tools.Loger;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/5
 * 包名： com.github.youchatproject.bmob_im
 * 文档描述：e
 */
public class FriendsUtil {
    private FriendsUtil(){}

    public static FriendsUtil friendsUtil = new FriendsUtil();
    //单例
    public static FriendsUtil getInterface(){
        if(friendsUtil != null){
            return friendsUtil ;
        }
        return null;
    }

    /**
     * [获得好友列表]
     * @param resultListener 回调接口
     */
    public void imGetFriendList(final OnDataResultListener resultListener) {
        Observable<List<String>> observable = Observable.create(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                try {
                    List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    subscriber.onNext(usernames);
                    subscriber.onCompleted();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
        observable.subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onNext(List<String> list) {
                        Loger.i("长度:"+list.size());
                        resultListener.onSuccess(list);
                    }

                    @Override
                    public void onCompleted() {
                        Loger.i("好友状态","获取好友列表成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        resultListener.onError(e);
                        Loger.e("好友状态","获取好友列表失败,错误原因"+e);
                    }
                });
    }

    /**
     * [查询Bmob服务器中的数据得到用户头像]
     * @param username 用户名
     * @param listener
     */
    public synchronized void selectorUserInfo(String username , final OnUserInfoResultListener listener){
        BmobQuery<BmobUserInfo> query = new BmobQuery<BmobUserInfo>("_User");
        query.addWhereEqualTo("username",username);
        query.findObjects(new FindListener<BmobUserInfo>() {
            @Override
            public void done(List<BmobUserInfo> list, BmobException e) {
                if(e == null){
                    BmobUserInfo bmobUserInfos = list.get(0);
                    listener.onSuccess(bmobUserInfos);
                }else{
                    listener.onError(e);
                }
            }
        });
    }

    /**
     * [得到会话列表]
     * @param listener 返回接口
     */
    public void getConversationList(final OnConversationResultListener listener){
        Observable<Map<String,EMConversation>> observable = Observable.create(new Observable.OnSubscribe<Map<String,EMConversation>>() {
            @Override
            public void call(Subscriber<? super Map<String,EMConversation>> subscriber) {
                Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
                if(conversations == null || conversations.isEmpty()){
                    subscriber.onError(new Throwable("无会话数据"));
                }else{
                    subscriber.onNext(conversations);
                    subscriber.onCompleted();
                }
            }
        });
        observable.subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<Map<String,EMConversation>>() {
                    @Override
                    public void onNext(Map<String,EMConversation> conversations) {
                        listener.onSuccess(conversations);
                    }

                    @Override
                    public void onCompleted() {
                        Loger.i("会话状态","会话状态获取成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Loger.e("会话状态","会话状态获取失败,错误原因"+e);
                        listener.onError(e);
                    }
                });
    }

    /**
     * [根据最后一条消息的时间排序]
     * @param map 返回的会话列表 ArrayList<EMConversation>
     */
    public List<Pair<Long, EMConversation>> getConversationByLastChatTime(Map<String, EMConversation> map) {
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();

        synchronized (map) {
            for (EMConversation conversation : map.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        Collections.sort(sortList, new Comparator<Pair<Long, EMConversation>>() {

            @Override
            public int compare(Pair<Long, EMConversation> con1,
                               Pair<Long, EMConversation> con2) {
                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return sortList;
    }

    /**
     * [搜索添加朋友]
     * @param phone 输入的用户手机号
     * @param listener 回掉监听器
     */
    public void addBeSearchFriend(final String phone , final OnSearchFriendListener listener){
        final String currentUserName = BmobUserInfo.getCurrentUser().getUsername() ;    //得到当前用户名
        BmobQuery<BmobUserInfo> query = new BmobQuery<BmobUserInfo>("_User");
        query.findObjects(new FindListener<BmobUserInfo>() {
            @Override
            public void done(List<BmobUserInfo> object,BmobException e) {
                if(e==null){
                    //自己实现模糊查找逻辑
                    List<BmobUserInfo> listenerResult = new ArrayList<BmobUserInfo>();
                    for(BmobUserInfo info : object){
                        String nickname = info.getUsername();
                        if(nickname.equals(currentUserName)){
                            continue;
                        }
                        if(nickname.contains(phone)){
                            listenerResult.add(info);
                        }
                    }
                    Loger.i("好友查询成功","条目:"+listenerResult.size()+"个");
                    if(listenerResult.size() == 0){
                        listener.onError(new Throwable("暂无该用户的相关信息"));
                    }else{
                        listener.onSuccess(listenerResult);
                    }
                }else{
                    Loger.e("好友查询失败","错误原因:"+e.toString());
                }
            }
        });
    }

    /**
     * [申请好友]
     * @param phone 用户手机号
     * @param reason 请求描述
     * @param listener 回调接口
     */
    public void requestAddContact(final String phone, final String reason , final OnAddContactListener listener){
        //参数为要添加的好友的username和添加理由
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    EMClient.getInstance().contactManager().addContact(phone, reason);
                    subscriber.onNext("请求发送成功");
                    subscriber.onCompleted();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    subscriber.onNext("请求发送失败,错误信息:"+e);
                }
            }
        });
        observable.subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String content) {
                        Loger.i(content);
                    }

                    @Override
                    public void onCompleted() {
                        listener.onSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onError();
                    }
                });
    }

    /**
     * 删除好友
     * @param username 好友用户名
     * @param listener 接口
     */
    public void delContact(String username , OnAddContactListener listener){
        try {
            EMClient.getInstance().contactManager().deleteContact(username);
            listener.onSuccess();
        } catch (HyphenateException e) {
            e.printStackTrace();
            listener.onError();
        }
    }
}
