package com.github.youchatproject.listener;

import com.hyphenate.chat.EMConversation;

import java.util.Map;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/6
 * 包名： com.github.youchatproject.listener
 * 文档描述：e
 */
public interface OnConversationResultListener {
    void onSuccess(Map<String,EMConversation> map);
    void onError(Throwable e);
}
