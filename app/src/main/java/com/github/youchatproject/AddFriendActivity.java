package com.github.youchatproject;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.github.youchatproject.adapter.AddFriendAdapter;
import com.github.youchatproject.beans.BmobUserInfo;
import com.github.youchatproject.bmob_im.FriendsUtil;
import com.github.youchatproject.databinding.ActivityAddFriendBinding;
import com.github.youchatproject.listener.OnAddContactListener;
import com.github.youchatproject.listener.OnListItemClickListener;
import com.github.youchatproject.listener.OnSearchFriendListener;
import com.github.youchatproject.tools.KeyBoardUtil;
import com.github.youchatproject.tools.Loger;
import com.github.youchatproject.view.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/6
 * 包名： com.github.youchatproject
 * 文档描述：添加好友Activity
 */
public class AddFriendActivity extends BaseActivity{
    private ActivityAddFriendBinding binding = null ;
    private List<BmobUserInfo> mUserInfos = null ;
    private AddFriendAdapter mAdapter = null ;
    @Override
    public void initParams(Bundle params) {
        mUserInfos = new ArrayList<BmobUserInfo>();
    }

    @Override
    public void bindLayout() {
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_friend);
    }

    @Override
    public void initView(View view) {
        binding.addFriendToolbar.setTitle("");
        setSupportActionBar(binding.addFriendToolbar);
        binding.addFriendToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initAdater();
    }

    public void initAdater(){
        mAdapter = new AddFriendAdapter(this,mUserInfos);
        binding.addFriendList.setLayoutManager(new LinearLayoutManager(this));
        binding.addFriendList.setItemAnimator(new DefaultItemAnimator());
        binding.addFriendList.setHasFixedSize(true);
        binding.addFriendList.setAdapter(mAdapter);
        mAdapter.setClickListener(new OnListItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //这里暂时先直接发送好友申请 后面画一个页面专门填写申请信息的
                String username = mUserInfos.get(position).getUsername();
                addFriends(username);
            }
        });
    }

    @Override
    public void doBusiness(final Context mContext) {
        binding.addFriendSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    //完成自己的事件
                    String phone = binding.addFriendSearch.getText().toString();
                    KeyBoardUtil.hideKeyBoard(mContext,binding.addFriendSearch);
                    searchFriend(phone);
                }
                return false;
            }
        });
    }

    public void searchFriend(String phone){
        if(phone.trim().equals("")){
            $snackbar(binding.addFriendSnackbar,"手机号不能为空",true);
            return ;
        }
        FriendsUtil.getInterface().addBeSearchFriend(phone, new OnSearchFriendListener() {
            @Override
            public void onSuccess(List<BmobUserInfo> infos) {
                binding.addFriendBar.setVisibility(View.VISIBLE);
                binding.addFriendList.setVisibility(View.VISIBLE);
                mUserInfos.clear();
                for(BmobUserInfo info : infos){
                    mUserInfos.add(info);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                $snackbar(binding.addFriendSnackbar,e.getMessage(),true);
            }
        });
    }

    public void addFriends(String phone){
        FriendsUtil.getInterface().requestAddContact(phone, "这是一条默认的好友请求信息，用来测试", new OnAddContactListener() {
            @Override
            public void onSuccess() {
                Loger.i("好友请求发送成功");
                $toast("好友请求发送成功");
            }

            @Override
            public void onError() {
                Loger.e("好友请求发送失败");
            }
        });
    }
}
