package com.github.youchatproject.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.youchatproject.ChatActivity;
import com.github.youchatproject.R;
import com.github.youchatproject.adapter.MainConversationAdapter;
import com.github.youchatproject.bmob_im.FriendsUtil;
import com.github.youchatproject.listener.OnConversationResultListener;
import com.github.youchatproject.listener.OnListItemClickListener;
import com.github.youchatproject.tools.Loger;
import com.github.youchatproject.view.BaseFragment;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/3
 * 包名： com.github.youchatproject.fragment
 * 文档描述：主页会话页面
 */
public class MainConversationFragment extends BaseFragment implements OnListItemClickListener{
    public MainConversationAdapter mAdapter ;
    public ArrayList<EMConversation> mConversations;
    @InjectView(R.id.conversation_list)
    RecyclerView conversationList;

    public MessageReceiver receiver ; //广播对象
    @Override
    public void onItemClick(View view, int position) {
        final EMConversation conversation = mConversations.get(position);
        String conversationId = conversation.conversationId();
        Intent chatIntent = new Intent();
        chatIntent.putExtra("ConversationId",conversationId);     //获得该用户聊天的ID
        chatIntent.setClass($context(), ChatActivity.class);
        startActivity(chatIntent);
    }

    //消息广播接收器
    public class MessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("MessageReceive")){
                getConversationList();  //直接去重新加载列表
            }
        }
    }
    @Override
    public int bindLayout() {
        return R.layout.fragment_conversation;
    }

    @Override
    public void initView(View view) {
        initAdapter();
    }

    public void initAdapter(){
        mAdapter = new MainConversationAdapter($context(),mConversations);
        conversationList.setLayoutManager(new LinearLayoutManager($context()));
        conversationList.setHasFixedSize(true);
        conversationList.setItemAnimator(new DefaultItemAnimator());
        conversationList.setAdapter(mAdapter);
        mAdapter.setClickListener(this);
    }
    @Override
    public void doBusiness(Context mContext) {
        registerBroadcast();    //注册广播监听器
    }

    @Override
    public void initParams(Bundle bundle) {
        mConversations = new ArrayList<EMConversation>();
    }

    /**
     * 注册广播接收器 从消息服务发送回来的广播 去刷新列表状态
     */
    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter("MessageReceive");
        receiver = new MessageReceiver();
        $context().registerReceiver(receiver, filter);
    }

    /**
     * [得到会话列表]
     */
    public void getConversationList(){
        FriendsUtil.getInterface().getConversationList(new OnConversationResultListener() {
            @Override
            public void onSuccess(Map<String, EMConversation> map) {
                //排序
                mConversations.clear();
                List<Pair<Long, EMConversation>> sortList = FriendsUtil.getInterface().getConversationByLastChatTime(map);
                for(Pair<Long, EMConversation> sortItem : sortList){
                    EMConversation conversation = sortItem.second;
                    String name = conversation.conversationId();
                    Loger.i("名字",name);
                    if(name.equals("") ||  name.contentEquals("")){ //此处需要contentEquals方法去过滤空值内容
                        break ;
                    }
                    mConversations.add(conversation);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                $Log("会话加载错误:"+e);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        getConversationList();  //得到会话列表
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        $context().unregisterReceiver(receiver);
        receiver = null ;
    }
}
