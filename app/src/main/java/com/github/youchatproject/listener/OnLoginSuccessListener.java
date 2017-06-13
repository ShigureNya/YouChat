package com.github.youchatproject.listener;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/5
 * 包名： com.github.youchatproject.listener
 * 文档描述：环信登录接口重写
 */
public interface OnLoginSuccessListener {
    void onSuccess();
    void onProgress(int progress, String status);
    void onError(int code, String message);
}
