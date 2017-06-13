package com.github.youchatproject.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by guhaoran on 2017/4/28.
 * LinaerLayoutManager 设置可不可滑动
 */

public class CustomLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}