package com.github.youchatproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.youchatproject.adapter.PageAdapter;
import com.github.youchatproject.beans.BmobUserInfo;
import com.github.youchatproject.bmob_im.FriendsUtil;
import com.github.youchatproject.databinding.ActivityMainBinding;
import com.github.youchatproject.fragment.MainConversationFragment;
import com.github.youchatproject.fragment.MainDynamicFragment;
import com.github.youchatproject.fragment.MainFriendFragment;
import com.github.youchatproject.listener.OnUserInfoResultListener;
import com.github.youchatproject.service.MessageService;
import com.github.youchatproject.tools.DialogUtil;
import com.github.youchatproject.tools.GlideUtil;
import com.github.youchatproject.view.BaseActivity;
import com.github.youchatproject.view.NoScrollViewPager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;

import butterknife.InjectView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;

import static com.github.youchatproject.R.id.main_menu_add_group_chat;
import static com.github.youchatproject.R.id.main_menu_add_new_friend;
import static com.github.youchatproject.R.id.main_menu_add_qr_code;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/2
 * 包名： io.github.youchart
 * 文档描述：应用主页
 */
public class MainActivity extends BaseActivity  {
    @InjectView(R.id.main_content_toolbar)
    Toolbar mainContentToolbar;
    @InjectView(R.id.main_content_toolbar_layout)
    AppBarLayout mainContentToolbarLayout;
    @InjectView(R.id.main_content_navigation_menu)
    BottomNavigationView mainContentNavigationMenu;
    @InjectView(R.id.main_content_viewpager)
    NoScrollViewPager mainContentViewpager;
    @InjectView(R.id.main_content_layout)
    CoordinatorLayout mainContentLayout;
    @InjectView(R.id.main_navigation)
    NavigationView mainNavigation;
    @InjectView(R.id.main_content_title)
    TextView mainContentTitle;

    public PageAdapter pageAdapter = null;
    public ActivityMainBinding binding = null;
    public MainConversationFragment conversationFragment = null;
    public MainFriendFragment friendFragment = null ;
    public MainDynamicFragment dynamicFragment = null ;

    public ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
    public String[] menuArray = new String[3];
    public AddContactBroadcast receiver ;
    @Override
    public void initParams(Bundle params) {

    }

    @Override
    public void bindLayout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    @Override
    public void initView(View view) {
        mainContentToolbar.setTitle("");
        setSupportActionBar(mainContentToolbar);
        mainContentToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.mainDrawer.openDrawer(GravityCompat.START);  //打开抽屉
            }
        });
        initViewPager();    //初始化initViewPager
        initNavigationView();    //初始化NavigationView
        initUserCenterView();   //初始化个人中心数据
    }

    @Override
    public void doBusiness(Context mContext) {
        startMessageService();  //启动消息服务
        registerBroadcast();    //注册好友广播监听器
        bindMenuClick();    //绑定加号菜单点击事件
    }

    /**
     * 绑定加号菜单点击事件
     */
    public void bindMenuClick(){
        mainContentToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case main_menu_add_new_friend:
                        startActivity(AddFriendActivity.class);
                        break;
                    case main_menu_add_group_chat:

                        break;
                    case main_menu_add_qr_code:

                        break;
                }
                return true;
            }
        });
    }
    /**
     * 启动消息服务器
     */
    public void startMessageService(){
        Intent i = new Intent(this, MessageService.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(i);
    }
    /**
     * 初始化ViewPager
     */
    public void initViewPager() {
        conversationFragment = new MainConversationFragment();
        friendFragment = new MainFriendFragment() ;
        dynamicFragment = new MainDynamicFragment() ;

        mFragments.add(conversationFragment);
        mFragments.add(friendFragment);
        mFragments.add(dynamicFragment);

        menuArray = getResources().getStringArray(R.array.menu_array);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), mFragments, menuArray);

        mainContentViewpager.setScroll(false);
        mainContentViewpager.setAdapter(pageAdapter);
        mainContentViewpager.setCurrentItem(0, false); //选定第一个ViewPager且禁止滑动动画
        mainContentViewpager.setOffscreenPageLimit(3);  //设置ViewPager缓存数量
        //为ViewPager添加滑动事件
        mainContentViewpager.addOnPageChangeListener(mPagerSiteChangeListener);
    }

    public ImageView navigationImg ;
    public TextView navigationUsername ;
    public TextView navigationSigns ;
    /**
     * 初始化侧滑菜单
     */
    public void initNavigationView() {
        binding.mainNavigation.setNavigationItemSelectedListener(new MyNavigationItemListener());
        binding.mainNavigation.setCheckedItem(0);
        binding.mainNavigation.setItemIconTintList(null);
        View headerView = binding.mainNavigation.getHeaderView(0);  //获得顶部layout的布局
        navigationImg = (ImageView) headerView.findViewById(R.id.navigation_layout_image);
        navigationSigns = (TextView) headerView.findViewById(R.id.navigation_layout_signs);
        navigationUsername = (TextView) headerView.findViewById(R.id.navigation_layout_username);
        //为NavigationMenu绑定点击事件
        mainContentNavigationMenu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * 初始化用户中心界面
     */
    public void initUserCenterView(){
        BmobUserInfo bmobUser = BmobUser.getCurrentUser(BmobUserInfo.class);
        String conversationId = bmobUser.getUsername();
        String signs = bmobUser.getSigns();
        BmobFile userPic = bmobUser.getUserPicture();
        if(userPic != null){
            String url = userPic.getUrl();
            GlideUtil.loadImage(url,this,navigationImg);
        }else{
            navigationImg.setImageResource(R.drawable.vector_drawable_default_user_picture_logo);
        }
        navigationUsername.setText(conversationId);
        if(signs != null){
            navigationSigns.setText(signs);
        }else{
            navigationSigns.setText("暂无个性签名信息");
        }
    }


    /**
     * 底部NavigationMenuView的点击事件处理
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main_menu_conversation:
                    mainContentViewpager.setCurrentItem(0);
                    return true;
                case R.id.main_menu_friends:
                    mainContentViewpager.setCurrentItem(1);
                    return true;
                case R.id.main_menu_dynamic:
                    mainContentViewpager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    /**
     * ViewPager的滑动事件处理
     */
    private ViewPager.OnPageChangeListener mPagerSiteChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            $Log("触发了滑动事件");
            mainContentNavigationMenu.getMenu().getItem(position).setChecked(true);
            mainContentTitle.setText(menuArray[position]);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_main_add_menu,menu);
        return true ;
    }

    /**
     * 抽屉中Menu的点击事件
     */
    private class MyNavigationItemListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            int itemId = item.getItemId();
            item.setCheckable(true);    //设为选中
            binding.mainDrawer.closeDrawer(GravityCompat.START);  //关闭抽屉
            return true;
        }
    }

    //回退按钮使DrawerLayout关闭
    @Override
    public void onBackPressed() {
        if (binding.mainDrawer.isDrawerOpen(GravityCompat.START)) {
            binding.mainDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //好友广播接收器
    public class AddContactBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("ContactBroadcast")){
                String result = intent.getStringExtra("Result");
                String username = intent.getStringExtra("Username");

                if(result.equals("Invited")){
                    //收到好友请求
                    String reason = intent.getStringExtra("Reason");
                    downloadDialogImage(username,reason);
                }else if(result.equals("RequestAccepted")){
                    //好友请求被同意
                    PromptDialog dialog = DialogUtil.buildPromptDialog(MainActivity.this,username+"请求已同意","马上开始聊天吧！","好的");
                    dialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
                    dialog.show();
                }else if(result.equals("RequestDeclined")){
                    //好友请求被拒绝
                    PromptDialog dialog = DialogUtil.buildPromptDialog(MainActivity.this,username+"请求已拒绝","对方拒绝了您的好友申请！","好的");
                    dialog.setDialogType(PromptDialog.DIALOG_TYPE_WRONG);
                    dialog.show();
                }
            }
        }
    }

    /**
     * 注册广播监听器
     */
    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter("ContactBroadcast");
        receiver = new AddContactBroadcast();
        registerReceiver(receiver, filter);
    }

    /**
     * 下载对话框图像
     * @param username 用户名
     * @param reason 请求信息
     */
    public void downloadDialogImage(final String username , final String reason){
        FriendsUtil.getInterface().selectorUserInfo(username, new OnUserInfoResultListener() {
            @Override
            public void onSuccess(BmobUserInfo info) {
                BmobFile file = info.getUserPicture();
                if(file != null){
                    String fileUrl = file.getFileUrl();
                    GlideUtil.getBitmapToUrl(MainActivity.this, fileUrl, new GlideUtil.OnBitmapDownloadResultListener() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            showAddContactDialog(username,reason,bitmap,true);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
                }else{
                    showAddContactDialog(username,reason,null,false);
                }
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    /**
     * [显示添加好友提示的对话框]
     * @param username 用户名
     * @param reason 请求信息
     * @param bitmap 头像图片
     * @param isBitmap 是否存在图片
     */
    public void showAddContactDialog(final String username , String reason , Bitmap bitmap , boolean isBitmap){
        String title = "来自"+username+"的好友申请";
        ColorDialog dialog = DialogUtil.buildDialog(this,title,reason,isBitmap,bitmap);
        dialog.setPositiveListener("同意", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog colorDialog) {
                try {
                    //同意好友请求
                    EMClient.getInstance().contactManager().acceptInvitation(username);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                colorDialog.dismiss();
            }
        });
        dialog.setNegativeListener("拒绝", new ColorDialog.OnNegativeListener() {
            @Override
            public void onClick(ColorDialog colorDialog) {
                try {
                    //拒绝好友请求
                    EMClient.getInstance().contactManager().declineInvitation(username);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                colorDialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        receiver = null ;
    }
}
