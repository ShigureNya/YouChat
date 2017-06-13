package com.github.youchatproject.listener;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/9
 * 包名： com.github.youchatproject.listener
 * 文档描述：聊天界面获取聊天记录的回调接口
 */
public interface OnMessageListResultListener {
    void onSuccess(List<EMMessage> messages);
    void onFailed(Throwable e);
}
