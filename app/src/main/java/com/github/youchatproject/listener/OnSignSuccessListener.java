package com.github.youchatproject.listener;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/5
 * 包名： com.github.youchatproject.listener
 * 文档描述：注册接口回掉
 */
public interface OnSignSuccessListener {
    void onSuccess();
    void onError(Throwable e);
}
