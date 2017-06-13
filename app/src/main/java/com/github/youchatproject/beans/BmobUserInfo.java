package com.github.youchatproject.beans;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/5
 * 包名： com.github.youchatproject.beans
 * 文档描述：e
 */
public class BmobUserInfo extends BmobUser {
    public BmobFile UserPicture ;
    public String signs ;
    public BmobFile getUserPicture() {
        return UserPicture;
    }

    public void setUserPicture(BmobFile userPicture) {
        UserPicture = userPicture;
    }

    public String getSigns() {
        return signs;
    }

    public void setSigns(String signs) {
        this.signs = signs;
    }
}
