package com.github.youchatproject.system;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/11
 * 包名： com.github.youchatproject.tools
 * 文档描述：MD5工具类
 */
public class MD5Util {

    public static String getMD5(String val) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        return getString(m);
    }
    private static String getString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i ++){
            sb.append(b[i]);
        }
        return sb.toString();
    }
}
