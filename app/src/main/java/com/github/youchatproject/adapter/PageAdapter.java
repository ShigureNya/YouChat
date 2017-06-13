package com.github.youchatproject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/3
 * 包名： com.github.youchatproject.adapter
 * 文档描述：ViewPager适配器
 */
public class PageAdapter extends FragmentPagerAdapter{
    List<Fragment> mFragmentList  = new ArrayList<Fragment>();
    //Tab名称集合
    private String[] listTitle;

    public PageAdapter(FragmentManager fm , List<Fragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList ;
    }

    public PageAdapter(FragmentManager fm, List<Fragment> fragments, String[] titles) {
        super(fm);
        mFragmentList = fragments ;
        listTitle = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return listTitle[position];
    }
}
