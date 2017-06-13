package com.github.youchatproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.youchatproject.R;
import com.github.youchatproject.bmob_im.MessageUtil;
import com.github.youchatproject.tools.Loger;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

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
        Loger.i("onCreateViewHolder");
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
        Loger.i("onBindViewHolder");
        EMMessage msg = mMessageList.get(position);
        String conversationId = msg.getFrom();
        holder.chatItemImage.setTag(conversationId);
        if (holder.chatItemImage.getTag() == conversationId) {
            MessageUtil.getInstance().handleMessagesType(msg, holder.chatItemImage, holder.chatItemMessage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Loger.i("getItemViewType");
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
        Loger.i("AddMessage");
    }

    public void saveMessagesToDatabase(){
        MessageUtil.getInstance().saveToDatabase(mMessageList);
        Loger.i("消息已存入数据库");
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.chat_item_user_image)
        ImageView chatItemUserImage;
        @InjectView(R.id.chat_item_image)
        ImageView chatItemImage;
        @InjectView(R.id.chat_item_message)
        TextView chatItemMessage;
        @InjectView(R.id.chat_item_layout)
        RelativeLayout chatItemLayout;

        public MessageHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
