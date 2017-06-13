package com.github.youchatproject.listener;

import com.github.youchatproject.beans.BmobUserInfo;

import java.util.List;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/7
 * 包名： com.github.youchatproject.listener
 * 文档描述：e
 */
public interface OnSearchFriendListener {
    void onSuccess(List<BmobUserInfo> infos);
    void onError(Throwable e);
}
