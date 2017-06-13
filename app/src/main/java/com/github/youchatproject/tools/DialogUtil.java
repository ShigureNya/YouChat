package com.github.youchatproject.tools;

import android.content.Context;
import android.graphics.Bitmap;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/7
 * 包名： com.github.youchatproject.tools
 * 文档描述：提示框工具
 */
public class DialogUtil {
    /**
     * [生成对话框]
     * @param mContext 上下文
     * @param title 主标题
     * @param content 内容
     * @param hasBitmap 是否有Bitmap
     * @param bitmap 从网络上下载的图片
     * @return 对话框对象
     */
    public static ColorDialog buildDialog(Context mContext, String title, String content, boolean hasBitmap , Bitmap bitmap){
        ColorDialog dialog = new ColorDialog(mContext);
        dialog.setTitle(title);
        dialog.setContentText(content);
        if(hasBitmap){
            dialog.setContentImage(bitmap);

        }
        return dialog;
    }

    /**
     * @param mContext 上下文
     * @param title 标题
     * @param content 内容
     * @param btnText 按钮文本
     * @return
     */
    public static PromptDialog buildPromptDialog(Context mContext , String title , String content , String btnText){
        final PromptDialog dialog = new PromptDialog(mContext);
        dialog.setAnimationEnable(true);
        dialog.setTitleText(title);
        dialog.setContentText(content);
        dialog.setPositiveListener(btnText, new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog promptDialog) {
                promptDialog.dismiss();
            }
        });
        return dialog;
    }

}
