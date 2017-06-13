package com.github.youchatproject.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.youchatproject.ChatActivity;
import com.github.youchatproject.R;
import com.github.youchatproject.adapter.MainFriendAdapter;
import com.github.youchatproject.bmob_im.FriendsUtil;
import com.github.youchatproject.listener.OnDataResultListener;
import com.github.youchatproject.listener.OnListItemClickListener;
import com.github.youchatproject.tools.Loger;
import com.github.youchatproject.view.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/3
 * 包名： com.github.youchatproject.fragment
 * 文档描述：好友Fragment
 */
public class MainFriendFragment extends BaseFragment implements OnListItemClickListener{
    @InjectView(R.id.friend_list)
    RecyclerView friendList;

    public MainFriendAdapter mAdapter ;
    public ArrayList<String> mFriends = null ;
    public AddContactBroadcast receiver ;
    @Override
    public int bindLayout() {
        return R.layout.fragment_friend;
    }

    @Override
    public void initView(View view) {
        initListAdapter();
    }

    @Override
    public void doBusiness(Context mContext) {
        getFriendsList();   //好友列表
        registerBroadcast();    //注册广播
    }

    @Override
    public void initParams(Bundle bundle) {
        mFriends = new ArrayList<String>();
    }

    /**
     * [初始化列表适配器]
     */
    public void initListAdapter(){
        mAdapter = new MainFriendAdapter(getContext(),mFriends);
        friendList.setLayoutManager(new LinearLayoutManager(getContext()));
        friendList.setItemAnimator(new DefaultItemAnimator());
        friendList.setHasFixedSize(true);
        friendList.setAdapter(mAdapter);
        setHeaderView(friendList);
        mAdapter.setItemClickListener(this);
    }


    /**
     * [得到好友列表]
     */
    public void getFriendsList(){
        FriendsUtil.getInterface().imGetFriendList(new OnDataResultListener() {
            @Override
            public void onSuccess(List<String> list) {
                mFriends.clear();
                for(String username : list){
                    mFriends.add(username);
                }
                Loger.i("好友个数",mFriends.size()+"");
                setFooterView(mFriends.size(),friendList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                $Log(e.toString());
            }
        });
    }

    /**
     * [设置顶部布局]
     * @param recyclerView 列表对象
     */
    public void setHeaderView(RecyclerView recyclerView){
        View headerView = LayoutInflater.from($context()).inflate(R.layout.layout_friend_header,recyclerView,false);
        mAdapter.setmHeaderView(headerView);
    }

    /**
     * [设置底部布局]
     * @param recyclerView 列表对象
     */
    TextView friendSum ;    //好友个数
    View footerView ;
    public void setFooterView(int size , RecyclerView recyclerView){
        if(footerView == null){
            footerView = LayoutInflater.from($context()).inflate(R.layout.layout_friend_footer,recyclerView,false);
        }
        friendSum = (TextView) footerView.findViewById(R.id.friend_footer_num);
        friendSum.setText("共有"+String.valueOf(size)+"人");
        mAdapter.setmFooterView(footerView);
        if(size == 0){
            footerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        final String username = mFriends.get(position-1);
        Intent chatIntent = new Intent();
        chatIntent.putExtra("ConversationId",username);     //获得该用户聊天的ID
        chatIntent.setClass($context(), ChatActivity.class);
        startActivity(chatIntent);
    }

    //好友广播接收器
    public class AddContactBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("ContactBroadcast")){
                String result = intent.getStringExtra("Result");
                if(result.equals("Added") || result.equals("Deleted")){
                    //如果是添加了 或者被删除了 就刷新列表
                    getFriendsList();
                }
            }
        }
    }

    /**
     * 注册广播监听器
     */
    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter("ContactBroadcast");
        receiver = new AddContactBroadcast();
        $context().registerReceiver(receiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getFriendsList();   //切换回Fragment时刷新好友列表
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        $context().unregisterReceiver(receiver);
        receiver = null ;
    }
}
