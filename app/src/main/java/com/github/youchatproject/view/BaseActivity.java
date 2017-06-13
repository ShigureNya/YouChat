package com.github.youchatproject.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.youchatproject.R;
import com.github.youchatproject.application.BaseApplication;
import com.github.youchatproject.tools.Loger;
import com.github.youchatproject.tools.SnackBarUtil;

import butterknife.ButterKnife;

/**
 * 作者： guhaoran
 * 创建于： 2017/5/31
 * 包名： io.github.youchart.view
 * 文档描述：基础类
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    /** 是否沉浸状态栏 **/
    private boolean isSetStatusBar = false;
    /** 是否允许全屏 **/
    private boolean mAllowFullScreen = false;
    /** 是否禁止旋转屏幕 **/
    private boolean isAllowScreenRoate = true;
    /** 当前Activity渲染的视图View **/
    private View mContextView = null;
    /** 是否输出日志信息 **/
    private boolean isDebug;
    private String APP_NAME;
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDebug = BaseApplication.isDebug;
        APP_NAME = BaseApplication.appName;
        $Log(TAG + "-->onCreate()");
        try {
            Bundle bundle = getIntent().getExtras();
            initParams(bundle);
            $Log("bindLayout");
            bindLayout();
            if (mAllowFullScreen) {
                this.getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
            if (isSetStatusBar) {
                steepStatusBar();
            }
            if (!isAllowScreenRoate) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            $Log("initView");
            ButterKnife.inject(this);
            initView(mContextView);
            $Log("doBusiness");
            doBusiness(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * [沉浸状态栏]
     */
    private void steepStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * [初始化Bundle参数]
     *
     * @param params
     */
    public abstract void initParams(Bundle params);

    /**
     * [绑定布局]
     *
     * @return
     */
    public abstract void bindLayout();

    /**
     * [重写： 1.是否沉浸状态栏 2.是否全屏 3.是否禁止旋转屏幕]
     */
    // public abstract void setActivityPre();

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

//    /** View点击 **/
//    public abstract void widgetClick(View v);

    @Override
    public void onClick(View v) {
//        if (fastClick())
//            $Log("widgetClick");
//        widgetClick(v);
    }

    /**
     * [页面跳转]
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * [携带数据的页面跳转]
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    /**
     * [含有Bundle通过Class打开编辑界面]
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        $Log(TAG + "--->onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        $Log(TAG + "--->onDestroy()");
    }

    /**
     * [是否允许全屏]
     *
     * @param allowFullScreen
     */
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
    }

    /**
     * [是否设置沉浸状态栏]
     *
     * @param
     */
    public void setSteepStatusBar(boolean isSetStatusBar) {
        this.isSetStatusBar = isSetStatusBar;
    }

    /**
     * [是否允许屏幕旋转]
     *
     * @param isAllowScreenRoate
     */
    public void setScreenRoate(boolean isAllowScreenRoate) {
        this.isAllowScreenRoate = isAllowScreenRoate;
    }

    /**
     * [日志输出]
     *
     * @param msg 日志正文
     */
    protected void $Log(String msg) {
        if (isDebug) {
            Log.d(APP_NAME, msg);
        }
    }

    /**
     * [错误日志输出]
     * @param msg 错误信息
     */
    protected void $error(String msg) {
        if (isDebug) {
            Loger.e(msg);
        }
    }


    /**
     * [弹出Toast]
     * @param msg 消息正文
     */
    protected void $toast(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }
    protected void $toast(int id){
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }


    /**
     * [弹出Snackbar]
     * @param view CoordinatorLayout承载对象
     * @param msg 消息正文
     * @param isError 是否为异常信息
     */
    protected void $snackbar(View view , String msg,boolean isError){
        Snackbar snackbar = null;
        if(isError){
            snackbar = SnackBarUtil.shortDefaultSnackbar(view,msg,getResources().getColor(R.color.light_red));
        }else{
            snackbar = SnackBarUtil.shortDefaultSnackbar(view,msg,0);
        }
        snackbar.show();
    }

    /**
     * [弹出Snackbar]
     * @param view CoordinatorLayout承载对象
     * @param id stringId
     * @param isError 是否为异常信息
     */
    protected void $snackbar(View view , int id,boolean isError){
        String msg = getString(id);
        Snackbar snackbar = null;
        if(isError){
            snackbar = SnackBarUtil.shortDefaultSnackbar(view,msg,getResources().getColor(R.color.light_red));
        }else{
            snackbar = SnackBarUtil.shortDefaultSnackbar(view,msg,0);
        }
        snackbar.show();
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

    /**
     * [打开Fragment]
     * @param fragment Frag对象
     */
    public void startFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.chat_functions_fragment,fragment);
        transaction.commit();
    }

}
