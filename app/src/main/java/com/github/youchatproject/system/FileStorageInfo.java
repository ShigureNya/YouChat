package com.github.youchatproject.system;

import android.os.Environment;

import com.github.youchatproject.tools.Loger;

import java.io.File;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/11
 * 包名： com.github.youchatproject.system
 * 文档描述：文件格式存储类
 */
public class FileStorageInfo {
    private FileStorageInfo(){}
    public static FileStorageInfo instance = null ;
    public static FileStorageInfo getInstance(){
        if(instance == null){
            instance = new FileStorageInfo();
        }
        return instance;
    }

    private String absolutePath = "youchat";
    private String cacheDirName = "caches";
    private String imageDirName = "images";
    private String musicDirName = "musics";
    private String voiceDirName = "voices";
    private String downloadDirName = "download";
    private String videoDirName = "videos";
    private String fileDirName = "files";
    public void initFileDirs(){
        File dir = null ;
        dir = new File(getAbsolutePath());
        if(!dir.exists()){
            dir.mkdirs();
        }
        File dirs = null ;
        dirs = new File(getAbsolutePath(),cacheDirName);
        if(!dirs.exists()){
            dirs.mkdirs();
        }
        dirs = new File(getAbsolutePath(),imageDirName);
        if(!dirs.exists()){
            dirs.mkdirs();
        }
        dirs = new File(getAbsolutePath(),musicDirName);
        if(!dirs.exists()){
            dirs.mkdirs();
        }
        dirs = new File(getAbsolutePath(),voiceDirName);
        if(!dirs.exists()){
            dirs.mkdirs();
        }
        dirs = new File(getAbsolutePath(),downloadDirName);
        if(!dirs.exists()){
            dirs.mkdirs();
        }
        dirs = new File(getAbsolutePath(),videoDirName);
        if(!dirs.exists()){
            dirs.mkdirs();
        }
        dirs = new File(getAbsolutePath(),fileDirName);
        if(!dirs.exists()){
            dirs.mkdirs();
        }
        Loger.i("全部文件夹生成完毕");
    }

    public String getAbsolutePath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+absolutePath+File.separator;
    }

    public String getCacheDirName() {
        return getAbsolutePath()+cacheDirName;
    }

    public String getImageDirName() {
        return getAbsolutePath()+imageDirName;
    }

    public String getMusicDirName() {
        return getAbsolutePath()+musicDirName;
    }

    public String getVoiceDirName() {
        return getAbsolutePath()+voiceDirName;
    }

    public String getDownloadDirName() {
        return getAbsolutePath()+downloadDirName;
    }

    public String getVideoDirName() {
        return getAbsolutePath()+videoDirName;
    }

    public String getFileDirName() {
        return getAbsolutePath()+fileDirName;
    }
}
