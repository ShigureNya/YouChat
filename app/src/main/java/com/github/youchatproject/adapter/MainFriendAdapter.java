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
import com.github.youchatproject.bmob_im.HuanXinUtil;
import com.github.youchatproject.listener.OnListItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.github.youchatproject.tools.AdapterAddViewUtil.BODY_VIEW;
import static com.github.youchatproject.tools.AdapterAddViewUtil.FOOTER_VIEW;
import static com.github.youchatproject.tools.AdapterAddViewUtil.HEADER_VIEW;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/4
 * 包名： com.github.youchatproject.adapter
 * 文档描述：好友列表适配器
 */
public class MainFriendAdapter extends RecyclerView.Adapter<MainFriendAdapter.MainFriendViewHolder> {
    public Context mContext;
    public LayoutInflater mInflate;
    public ArrayList<String> mUserInfoList ;    //这个是从环信SDK获得的好友列表
    public View mHeaderView ;
    public View mFooterView ;
    public OnListItemClickListener itemClickListener;

    public void setmHeaderView(View mHeaderView) {
        this.mHeaderView = mHeaderView;
        notifyItemChanged(0);
}

    public void setmFooterView(View mFooterView) {
        this.mFooterView = mFooterView;
        notifyItemInserted(getItemCount()-1);
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null){
            return BODY_VIEW;
        }
        if (position == 0){
            //第一个item应该加载Header
            return HEADER_VIEW;
        }
        if (position == getItemCount()-1){
            //最后一个,应该加载Footer
            return FOOTER_VIEW;
        }
        return BODY_VIEW;
    }

    public MainFriendAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        mInflate = LayoutInflater.from(mContext);
        this.mUserInfoList = (ArrayList<String>) mList;
    }
    @Override
    public MainFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == HEADER_VIEW ){
            return new MainFriendViewHolder(mHeaderView);
        }
        if(mFooterView != null && viewType == FOOTER_VIEW){
            return new MainFriendViewHolder(mFooterView);
        }
        View view = mInflate.inflate(R.layout.layout_friend_item,parent,false);
        return new MainFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainFriendViewHolder holder, int position) {
        if(getItemViewType(position) == HEADER_VIEW) return;
        if(getItemViewType(position) == FOOTER_VIEW) return;

        int pos = getRealPosition(holder);
        String userName = mUserInfoList.get(pos);
        holder.friendItemLayout.setTag(userName);
        if(holder.friendItemLayout.getTag().equals(userName)){
            HuanXinUtil.getInterface().getUserInfoImage(mContext,userName,holder.friendItemImg);
        }
        holder.friendItemName.setText(userName);
    }

    @Override
    public int getItemCount() {
        if(mHeaderView == null && mFooterView == null){
            return mUserInfoList.size();
        }else if(mHeaderView == null && mFooterView != null){
            return mUserInfoList.size() + 1;
        }else if (mHeaderView != null && mFooterView == null){
            return mUserInfoList.size() + 1;
        }else {
            return mUserInfoList.size() + 2;
        }
    }


    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }


    public class MainFriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @InjectView(R.id.friend_item_img)
        ImageView friendItemImg;
        @InjectView(R.id.friend_item_name)
        TextView friendItemName;
        @InjectView(R.id.friend_item_layout)
        RelativeLayout friendItemLayout;

        public MainFriendViewHolder(View view) {
            super(view);
            if (view == mHeaderView){
                return;
            }
            if (view == mFooterView){
                return;
            }
            ButterKnife.inject(this, view);
            friendItemLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener!=null){
                itemClickListener.onItemClick(v,getLayoutPosition());
            }
        }
    }

    public void setItemClickListener(OnListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
