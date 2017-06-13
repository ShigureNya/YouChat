package com.github.youchatproject.listener;

import com.hyphenate.chat.EMMessage;

/**
 * 作者： jimhao
 * 创建于： 2017/3/30
 * 包名： com.winary.heatcloudplatform.listener
 * 文档描述：Fragment返回message
 */

public interface OnFragmentResultListener {
    public int TYPE_VOICE = 1;
    public int TYPE_PIC = 2 ;
    void resultMessage(EMMessage message , int type);
}
