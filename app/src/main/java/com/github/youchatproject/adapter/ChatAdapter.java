package com.github.youchatproject.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.youchatproject.R;
import com.github.youchatproject.bmob_im.MessageUtil;
import com.github.youchatproject.tools.Loger;
import com.github.youchatproject.tools.VoiceUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.himanshusoni.chatmessageview.ChatMessageView;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/9
 * 包名： com.github.youchatproject.adapter
 * 文档描述：聊天Adapter
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageHolder> {
    private List<EMMessage> mMessageList = null;
    private Context mContext;
    private LayoutInflater mInflater;
    private static final int MINE_MESSAGE = 1; //来自我
    private static final int OTHER_MESSAGE = 2;    //来自对方
    private String currentUserId;      //当前登录用户ID

    public ChatAdapter(List<EMMessage> mChatList, Context mContext) {
        this.mMessageList = mChatList;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        currentUserId = EMClient.getInstance().getCurrentUser();
    }

    @Override
    public int getItemCount() {
        return mMessageList == null || mMessageList.size() == 0 ? 0 : mMessageList.size();
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == MINE_MESSAGE) {
            view = mInflater.inflate(R.layout.layout_chat_mine, parent, false);
        } else {
            view = mInflater.inflate(R.layout.layout_chat_other, parent, false);
        }
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        EMMessage msg = mMessageList.get(position);
        String conversationId = msg.getFrom();
        holder.chatItemLayout.setTag(conversationId);
        if (holder.chatItemLayout.getTag() == conversationId) {
            handleMessagesType(msg, holder);
        }
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = mMessageList.get(position);
        if (message.getFrom().equals(currentUserId)) {
            //如果是自己发送的消息
            return MINE_MESSAGE;
        } else {
            return OTHER_MESSAGE;
        }
    }


    public void addMessage(EMMessage message) {
        mMessageList.add(message);
        notifyItemInserted(mMessageList.size() - 1);

    }

    public void saveMessagesToDatabase(){
        MessageUtil.getInstance().saveToDatabase(mMessageList);
        Loger.i("消息已存入数据库");
    }

    public boolean isScrollToButton(RecyclerView recyclerView){
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        Loger.i("可见的最后一个下标:",lastPosition+"");
        int listLastCount = recyclerView.getLayoutManager().getItemCount()-1;
        Loger.i("当前列表的最后一个下标:",listLastCount+"");
        if(lastPosition == listLastCount){
            return true ;
        }else{
            return false ;
        }
    }

    public class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.chat_item_user_image)
        ImageView chatItemUserImage;
        @InjectView(R.id.chat_item_image)
        ImageView chatItemImage;
        @InjectView(R.id.chat_item_message)
        TextView chatItemMessage;
        @InjectView(R.id.chat_item_layout)
        RelativeLayout chatItemLayout;
        @InjectView(R.id.chat_item_text_message_layout)
        LinearLayout chatItemTextMessageLayout ;
        @InjectView(R.id.chat_item_voice_message_layout)
        RelativeLayout chatItemVoiceMessageLayout ;
        @InjectView(R.id.chat_item_voice_img)
        ImageView chatItemVoiceImage ;
        @InjectView(R.id.chat_item_voice_duration)
        TextView chatItemVoiceDuration ;
        @InjectView(R.id.chat_item_message_layout)
        ChatMessageView chatItemMessageLayout ;

        public MessageHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            chatItemMessageLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            handlerClickItem(v, getAdapterPosition());
        }
    }


    /**
     * [处理消息类型的方法]
     * @param msg 消息对象
     * @param holder 控制器对象
     */
    public void handleMessagesType(EMMessage msg, MessageHolder holder){
        switch(msg.getType()){
            //图片消息
            case IMAGE:{
                holder.chatItemTextMessageLayout.setVisibility(View.VISIBLE);
                holder.chatItemImage.setVisibility(View.VISIBLE);
                holder.chatItemMessage.setVisibility(View.GONE);
                EMImageMessageBody imageBody = (EMImageMessageBody) msg.getBody();
                String url = imageBody.getThumbnailUrl();
                Loger.i("图像地址",url);
                break;
            }
            case TXT:{
                holder.chatItemTextMessageLayout.setVisibility(View.VISIBLE);
                holder.chatItemImage.setVisibility(View.GONE);
                holder.chatItemMessage.setVisibility(View.VISIBLE);
                EMTextMessageBody txtBody = (EMTextMessageBody) msg.getBody();
                String content = txtBody.getMessage();
                holder.chatItemMessage.setText(content);
                break;
            }
            case FILE:
                EMFileMessageBody fileBody = (EMFileMessageBody) msg.getBody();

                break;
            case VOICE:
                holder.chatItemTextMessageLayout.setVisibility(View.GONE);
                holder.chatItemVoiceMessageLayout.setVisibility(View.VISIBLE);
                EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) msg.getBody();
                int duraton = voiceMessageBody.getLength();
                holder.chatItemVoiceDuration.setText(String.valueOf(duraton)+"s");
                break;
            case VIDEO:
                EMVideoMessageBody videoMessageBody = (EMVideoMessageBody) msg.getBody();

                break;
        }
    }

    /**
     * 处理点击事件
     * @param position 下标
     */
    public void handlerClickItem(View v , int position){
        EMMessage msg = mMessageList.get(position);
        switch (msg.getType()){
            case VOICE:
                //从Voice对象中取出local信息
                EMVoiceMessageBody body = (EMVoiceMessageBody) msg.getBody();
                VoiceUtil.getInstance().saveVoice(mContext,body);    //对语音进行存储
                break;
            case IMAGE:

                break;
        }
    }
}
