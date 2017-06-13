package com.github.youchatproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.github.youchatproject.R;
import com.github.youchatproject.beans.BmobUserInfo;
import com.github.youchatproject.bmob_im.HuanXinUtil;
import com.github.youchatproject.listener.OnListItemClickListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/7
 * 包名： com.github.youchatproject.adapter
 * 文档描述：e
 */
public class AddFriendAdapter extends RecyclerSwipeAdapter<AddFriendAdapter.AddFriendViewHolder> {
    public Context mContext;
    public List<BmobUserInfo> mUserInfos = null;
    public LayoutInflater mInflater;
    public OnListItemClickListener clickListener;

    public AddFriendAdapter(Context mContext, List<BmobUserInfo> mUserInfos) {
        this.mContext = mContext;
        this.mUserInfos = mUserInfos;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.add_friend_item_layout;
    }

    @Override
    public AddFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_add_friend_item, parent, false);
        return new AddFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddFriendViewHolder viewHolder, int position) {
        //set show mode.
        viewHolder.addFriendItemLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        //set drag edge.
        viewHolder.addFriendItemLayout.setDragEdge(SwipeLayout.DragEdge.Left);
        viewHolder.addFriendItemLayout.addSwipeListener(new SwipeItemListener());

        //设置数据
        BmobUserInfo userInfo = mUserInfos.get(position);
        String username = userInfo.getUsername();
        viewHolder.addFriendItemLayout.setTag(username);
        if(viewHolder.addFriendItemLayout.getTag() == username){
            HuanXinUtil.getInterface().getUserInfoImage(mContext,username,viewHolder.addFriendItemImg);
        }
        viewHolder.addFriendItemName.setText(username);
    }

    @Override
    public int getItemCount() {
        return mUserInfos.size();
    }

    public class AddFriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.add_friend_item_add)
        Button addFriendItemAdd;
        @InjectView(R.id.add_friend_item_info)
        Button addFriendItemInfo;
        @InjectView(R.id.add_friend_item_img)
        ImageView addFriendItemImg;
        @InjectView(R.id.add_friend_item_name)
        TextView addFriendItemName;
        @InjectView(R.id.add_friend_item_layout)
        SwipeLayout addFriendItemLayout;

        public AddFriendViewHolder (View view) {
            super(view);
            ButterKnife.inject(this, view);
            addFriendItemAdd.setOnClickListener(this);
            addFriendItemInfo.setOnClickListener(this);
            addFriendItemLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.add_friend_item_layout:
                    if(addFriendItemLayout.getOpenStatus() == SwipeLayout.Status.Open){
                        addFriendItemLayout.close();
                    }
                    break;
                case R.id.add_friend_item_add:
                    if(clickListener != null){
                        clickListener.onItemClick(v,getLayoutPosition());
                    }
                    break;
                case R.id.add_friend_item_info:

                    break;
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
