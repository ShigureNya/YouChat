package com.github.youchatproject.tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/5
 * 包名： com.github.youchatproject.tools
 * 文档描述：Adapter增强工具
 */
public class AdapterAddViewUtil {
    public static final int HEADER_VIEW = 1 ;
    public static final int FOOTER_VIEW = 2 ;
    public static final int BODY_VIEW = 3 ;

    /**
     *
     * [有Header后实际处理的下标情况]
     * @param holder 处理器对象
     * @return 实际下标
     */
    public static int getRealPosition(RecyclerView.ViewHolder holder, View mHeaderView) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }
}
