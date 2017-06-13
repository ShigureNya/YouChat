package com.github.youchatproject.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.github.youchatproject.R;
import com.github.youchatproject.view.BaseFragment;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/3
 * 包名： com.github.youchatproject.fragment
 * 文档描述：动态Fragment
 */
public class MainDynamicFragment extends BaseFragment {
    @Override
    public int bindLayout() {
        return R.layout.fragment_dynamic;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void initParams(Bundle bundle) {

    }
}
