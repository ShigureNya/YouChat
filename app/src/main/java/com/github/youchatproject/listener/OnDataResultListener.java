package com.github.youchatproject.listener;

import java.util.List;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/5
 * 包名： com.github.youchatproject.listener
 * 文档描述：数据传递接口
 */
public interface OnDataResultListener {
    void onSuccess(List<String> list);
    void onError(Throwable e);
}
