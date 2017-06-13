package com.github.youchatproject.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.youchatproject.application.BaseApplication;

import butterknife.ButterKnife;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/3
 * 包名： com.github.youchatproject.view
 * 文档描述：e
 */
public  abstract  class BaseFragment extends Fragment {
    private boolean isDebug;
    private String APP_NAME;
    protected final String TAG = this.getClass().getSimpleName();
    private View mContextView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDebug = BaseApplication.isDebug;
        APP_NAME = BaseApplication.appName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initParams(getArguments());
        mContextView = inflater.inflate(bindLayout(), container, false);
        ButterKnife.inject(this, mContextView);
        initView(mContextView);
        doBusiness(getActivity());
        return mContextView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    /**
     * [绑定布局]
     *
     * @return
     */
    public abstract int bindLayout();

    /**
     * [初始化控件]
     *
     * @param view
     */
    public abstract void initView(final View view);

    /**
     * [业务操作]
     *
     * @param mContext
     */
    public abstract void doBusiness(Context mContext);

    /**
     * [初始化参数]
     */
    public abstract void initParams(Bundle bundle);

    @SuppressWarnings("unchecked")
    public <T extends View> T $(View view, int resId) {
        return (T) view.findViewById(resId);
    }

    /**
     * [日志输出]
     *
     * @param msg
     */
    protected void $Log(String msg) {
        if (isDebug) {
            Log.d(APP_NAME, msg);
        }
    }

    protected Context $context(){
        return getContext();
    }

    /**
     * [防止快速点击]
     *
     * @return
     */
    private boolean fastClick() {
        long lastClick = 0;
        if (System.currentTimeMillis() - lastClick <= 1000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }


}
