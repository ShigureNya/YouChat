package com.github.youchatproject.listener;

import com.github.youchatproject.beans.BmobUserInfo;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/5
 * 包名： com.github.youchatproject.listener
 * 文档描述：获得好友列表返回的接口
 */
public interface OnUserInfoResultListener {
    void onSuccess(BmobUserInfo info);
    void onError(Throwable e);
}
