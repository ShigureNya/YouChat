package com.github.youchatproject;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.youchatproject.adapter.ChatAdapter;
import com.github.youchatproject.bmob_im.MessageUtil;
import com.github.youchatproject.dao.GreenDaoUtil;
import com.github.youchatproject.dao.SystemInfos;
import com.github.youchatproject.databinding.ActivityChatBinding;
import com.github.youchatproject.fragment.ChatVoiceFragment;
import com.github.youchatproject.listener.OnFragmentResultListener;
import com.github.youchatproject.listener.OnMessageListResultListener;
import com.github.youchatproject.tools.KeyBoardUtil;
import com.github.youchatproject.tools.Loger;
import com.github.youchatproject.view.BaseActivity;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/8
 * 包名： com.github.youchatproject
 * 文档描述：聊天界面儿
 */
public class ChatActivity extends BaseActivity implements TextWatcher , EMMessageListener , OnFragmentResultListener{
    public ActivityChatBinding chatBinding = null;
    public String conversationId = null;
    public CharSequence editLength;
    public List<EMMessage> mChatList = null;
    public ChatAdapter mAdapter = null;
    private EMConversation emConversation = null ;
    @InjectView(R.id.chat_send_btn)
    Button chatSendBtn;
    @InjectView(R.id.chat_functions_voice)
    ImageView chatFunctionsVoice;
    @InjectView(R.id.chat_functions_picture)
    ImageView chatFunctionsPicture;
    @InjectView(R.id.chat_functions_simeji)
    ImageView chatFunctionsSimeji;
    @InjectView(R.id.chat_functions_add)
    ImageView chatFunctionsAdd;

    @Override
    public void initParams(Bundle params) {
        mChatList = new ArrayList<EMMessage>();
        conversationId = getIntent().getStringExtra("ConversationId");
        //初始化聊天对象
        emConversation = EMClient.getInstance().chatManager().getConversation(conversationId, null, true);
    }

    @Override
    public void bindLayout() {
        chatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
    }
    @Override
    public void initView(View view) {
        initToolbar();  //初始化Toolbar
        chatBinding.chatInputMessage.addTextChangedListener(this);
        initChatAdapter();  //初始化聊天适配器
        setChatFunctionsHeight();   //设置功能菜单高度
    }

    @Override
    public void doBusiness(Context mContext) {
        initChatMessageList();  //初始化聊天记录列表
        //设置点击事件
        chatBinding.chatInputMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                $Log("点击了"+event.getAction());
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    chatBinding.chatFunctionsFragment.setVisibility(View.GONE);
                }
                return false;
            }
        });
        chatBinding.chatMessageList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                $Log("点击了"+event.getAction());
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    chatBinding.chatFunctionsFragment.setVisibility(View.GONE);
                    KeyBoardUtil.hideKeyBoard(ChatActivity.this,chatBinding.chatInputMessage);
                }
                return false;
            }
        });
    }
    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        chatBinding.chatToolbar.setTitle("");
        setSupportActionBar(chatBinding.chatToolbar);
        chatBinding.chatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        chatBinding.chatToolbarTitle.setText(conversationId);   //将conversationId 赋值给标题
    }


    /**
     * 设置功能菜单Fragment的高度
     */
    public void setChatFunctionsHeight(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int functionsLayoutHeight = chatBinding.chatFunctionsLayout.getHeight();
                $Log("功能菜单高度:"+ functionsLayoutHeight);
                SystemInfos infos = GreenDaoUtil.getInstance().queryPreferencesById("KeyBoardHeight");
                int height = infos.getSystemContentIte();
                $Log("软键盘单高度:"+height);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height+functionsLayoutHeight);
                chatBinding.chatFunctionsFragment.setLayoutParams(params);
            }
        }, 300);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //输入文本之前的状态
        editLength = s;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //输入文字中的状态，count是一次性输入字符数
    }

    @Override
    public void afterTextChanged(Editable s) {
        //输入文字后的状态
        Log.i("输入状态",editLength.length()+"");
        if (editLength.length() > 0) {
            chatBinding.chatSendBtn.setBackgroundResource(R.drawable.chat_send_button_drawable);
        } else {
            chatBinding.chatSendBtn.setBackgroundResource(R.drawable.chat_send_button_not_drawable);
        }
    }


    Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<EMMessage> messages = (List<EMMessage>) msg.obj;
            for (EMMessage emMessage : messages) {
                Loger.i("新消息:" + emMessage.getUserName());
                mAdapter.addMessage(emMessage);
                //标记为已读
                emConversation.markMessageAsRead(emMessage.getMsgId());
            }
            scrollInBottom();   //滑动至底部
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        $Log("聊天服务已关闭");
        EMClient.getInstance().chatManager().removeMessageListener(this);
        //在销毁的时候将消息传入数据库
        mAdapter.saveMessagesToDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        $Log("聊天服务已开始");
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    /**
     * [初始化聊天页面适配器]
     */
    public void initChatAdapter() {
        mAdapter = new ChatAdapter(mChatList, this);
        chatBinding.chatMessageList.setLayoutManager(new LinearLayoutManager(this));
        //以后可能会换成增加Item时增加动画
        chatBinding.chatMessageList.setItemAnimator(new DefaultItemAnimator());
        chatBinding.chatMessageList.setAdapter(mAdapter);
    }

    /**
     * 初始化获得聊天记录
     */
    public void initChatMessageList() {
        MessageUtil.getInstance().loadMessageList(conversationId, new OnMessageListResultListener() {
            @Override
            public void onSuccess(List<EMMessage> messages) {
                emConversation.markAllMessagesAsRead();
                Loger.i("已全部标记为已读状态");
                mChatList.clear();
                for (EMMessage msg : messages) {
                    mChatList.add(msg);
                }
                mAdapter.notifyDataSetChanged();
                scrollInBottom();
            }
            @Override
            public void onFailed(Throwable e) {
                $error("聊天界面无聊天记录");
            }
        });
    }

    @OnClick({R.id.chat_send_btn, R.id.chat_functions_voice, R.id.chat_functions_picture, R.id.chat_functions_simeji, R.id.chat_functions_add})
    public void onViewClicked(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("ConversationId",conversationId);

        switch (view.getId()) {
            case R.id.chat_send_btn:
                sendTextMessage();
                break;
            case R.id.chat_functions_voice:
                ChatVoiceFragment fragment = new ChatVoiceFragment();
                fragment.setArguments(bundle);
                startFragment(fragment);
                displayFunctions();
                break;
            case R.id.chat_functions_picture:
                break;
            case R.id.chat_functions_simeji:
                break;
            case R.id.chat_functions_add:
                break;
        }
        scrollInBottom();   //滑动至底部
    }

    /**
     * 滑动至最底部
     */
    public void scrollInBottom(){
        chatBinding.chatMessageList.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    /**
     * 关闭functions菜单
     */
    public void displayFunctions(){
        KeyBoardUtil.hideKeyBoard(this,chatBinding.chatInputMessage);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                chatBinding.chatFunctionsFragment.setVisibility(View.VISIBLE);
            }
        },200);
    }
    /**
     * 发送文本消息
     */
    public void sendTextMessage(){
        String message = chatBinding.chatInputMessage.getText().toString();
        if(message.trim().equals("")){
            return;
        }
        EMMessage msg = MessageUtil.getInstance().createTextMessage(conversationId,message,false);
        mAdapter.addMessage(msg);
        //点击发送以后直接初始化按钮状态
        chatBinding.chatInputMessage.setText("");
        chatBinding.chatSendBtn.setBackgroundResource(R.drawable.chat_send_button_not_drawable);
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        //通过Handler将messages
        Message msg = messageHandler.obtainMessage();
        msg.obj = messages;
        messageHandler.sendMessage(msg);
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
    public void onMessageDelivered(List<EMMessage> messages) {
        //收到已送达回执
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        //消息状态变动
    }

    @Override
    public void resultMessage(EMMessage message , int type) {
        if(type == OnFragmentResultListener.TYPE_VOICE){
            mAdapter.addMessage(message);
        }
    }
}
