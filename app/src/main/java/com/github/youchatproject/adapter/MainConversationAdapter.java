package com.github.youchatproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.github.youchatproject.R;
import com.github.youchatproject.bmob_im.HuanXinUtil;
import com.github.youchatproject.listener.OnListItemClickListener;
import com.github.youchatproject.tools.TimerFormat;
import com.hyphenate.chat.EMConversation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/3
 * 包名： com.github.youchatproject.adapter
 * 文档描述：会话Adapter
 */
public class MainConversationAdapter extends RecyclerSwipeAdapter<MainConversationAdapter.MainConversationHolder> {
    public Context mContext;
    public LayoutInflater mInflater;
    public List<EMConversation> mConversationList ;
    public OnListItemClickListener clickListener ;

    public MainConversationAdapter(Context mContext, List<EMConversation> mConversationList) {
        this.mContext = mContext;
        this.mConversationList = mConversationList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MainConversationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = mInflater.inflate(R.layout.layout_conversation_item, parent, false);
        return new MainConversationHolder(mView);
    }

    @Override
    public void onBindViewHolder(MainConversationHolder viewHolder, int position) {
        //从集合中取出对象
        EMConversation conversation = mConversationList.get(position);
        String username = conversation.conversationId();
        String message = HuanXinUtil.getInterface().getLastMessageType(conversation.getLastMessage());
        int unReadMsgCount = conversation.getUnreadMsgCount();  //得到未读消息数量
        //设置名称
        viewHolder.conversationItemUserName.setText(username);
        //为Layout设置tag
        viewHolder.conversationItemLayout.setTag(username);
        if(viewHolder.conversationItemLayout.getTag().equals(username)){
            HuanXinUtil.getInterface().getUserInfoImage(mContext,username,viewHolder.conversationItemImg);
        }
        //设置最后一条消息
        viewHolder.conversationItemMessage.setText(message);
        //获得消息时间
        Date messageTime = new Date(conversation.getLastMessage().getMsgTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String messageTimeStr = format.format(messageTime);
        //设置消息时间
        viewHolder.conversationItemTime.setText(TimerFormat.formatDateTime(messageTimeStr));
        //设置未读消息数量
        if(unReadMsgCount == 0){
            viewHolder.conversationItemUnreadPoint.setText("0");
            viewHolder.conversationItemUnreadPoint.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.conversationItemUnreadPoint.setText(String.valueOf(unReadMsgCount));
            viewHolder.conversationItemUnreadPoint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mConversationList.size() ;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.conversation_item_layout;
    }

    class MainConversationHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @InjectView(R.id.conversation_item_img)
        ImageView conversationItemImg;
        @InjectView(R.id.conversation_item_time)
        TextView conversationItemTime;
        @InjectView(R.id.conversation_item_user_name)
        TextView conversationItemUserName;
        @InjectView(R.id.conversation_item_message)
        TextView conversationItemMessage;
        @InjectView(R.id.conversation_item_layout)
        SwipeLayout conversationItemLayout;
        @InjectView(R.id.conversation_item_unread_point)
        TextView conversationItemUnreadPoint ;

        public MainConversationHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            conversationItemLayout.setOnClickListener(this);
            //set show mode.
            conversationItemLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            //set drag edge.
            conversationItemLayout.setDragEdge(SwipeLayout.DragEdge.Left);
            //设置滑动监听器
            conversationItemLayout.addSwipeListener(new SwipeItemListener());
        }

        @Override
        public void onClick(View v) {
            //如果状态为打开
            if(conversationItemLayout.getOpenStatus() == SwipeLayout.Status.Open){
                conversationItemLayout.close();
            }else if(conversationItemLayout.getOpenStatus() == SwipeLayout.Status.Close){
                clickListener.onItemClick(conversationItemLayout,getAdapterPosition());
            }
        }
    }

    class SwipeItemListener implements SwipeLayout.SwipeListener{

        @Override
        public void onStartOpen(SwipeLayout layout) {
//            Loger.i("onStartOpen");
        }

        @Override
        public void onOpen(SwipeLayout layout) {
//            Loger.i("onOpen");
        }

        @Override
        public void onStartClose(SwipeLayout layout) {
//            Loger.i("onStartClose");
        }

        @Override
        public void onClose(SwipeLayout layout) {
//            Loger.i("onClose");
        }

        @Override
        public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
//            Loger.i("onUpdate");
        }

        @Override
        public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
//            Loger.i("onHandRelease");
        }
    }

    public void setClickListener(OnListItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
