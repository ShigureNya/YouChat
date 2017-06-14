package com.github.youchatproject.tools;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.github.youchatproject.system.FileStorageInfo;
import com.github.youchatproject.system.MD5Util;
import com.hyphenate.chat.EMVoiceMessageBody;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/11
 * 包名： com.github.youchatproject.tools
 * 文档描述：音频工具类
 */
public class VoiceUtil {
    //音频录制接口
    private MediaRecorder recorder;
    private Timer voiceTimer ;  //录音时长类
    private VoiceUtil(){}
    public static VoiceUtil instance = null ;
    public static VoiceUtil getInstance(){
        if(instance == null){
            instance = new VoiceUtil();
        }
        return instance;
    }

    /**
     * [开始录制音频]
     * @param conversationId 对方ID
     * @return
     */
    public String startRecord(Context mContext , String conversationId){
        if(recorder == null){
            String recordFileName = buildRecordName(conversationId);
            File recordFile = new File(FileStorageInfo.getInstance().getVoiceDirName(),recordFileName);
            if(!recordFile.exists()){
                try {
                    recordFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            recorder = new MediaRecorder();
            //声音来源
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //音频输出格式
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //设置声音编码的格式
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //设置音频输出
            recorder.setOutputFile(recordFile.getAbsolutePath());

            try {
                recorder.prepare();
                recorder.start();
                startTimeDuration(mContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return recordFile.getAbsolutePath();
        }
        return null ;
    }

    /**
     * [停止录音]
     */
    public int stopRecord(){
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            int duration = stopTimeDuration();
            return duration;
        }
        return  0;
    }

    /**
     * [根据对方ID和时间生成文件名 并使用MD5算法加密]
     * @param conversationId 对方ID
     * @return 文件名
     */
    public String buildRecordName(String conversationId){
        String name = conversationId+System.currentTimeMillis();
        String result = null;
        try {
            result = MD5Util.getMD5(name)+".amr";
        } catch (NoSuchAlgorithmException e) {
            Loger.e("无法将该字符串转义为Md5格式");
        }
        return result ;
    }
    private int voiceDuration ;

    /**
     * [启动时间计时]
     */
    public void startTimeDuration(final Context mContext){
        voiceDuration = 0 ;
        final Intent durationIntent = new Intent("VoiceDuration");

        if(voiceTimer == null){
            voiceTimer = new Timer();
        }
        voiceTimer.schedule(new TimerTask(){
            @Override
            public void run() {
                voiceDuration = voiceDuration + 1;
                durationIntent.putExtra("Duration",voiceDuration);
                mContext.sendBroadcast(durationIntent);
            }
        },0,1000);
    }

    /**
     * [关闭计时]
     * @return 录音时间
     */
    public int stopTimeDuration(){
        if(voiceTimer != null){
            voiceTimer.cancel();
            voiceTimer = null ;
        }
        return voiceDuration;
    }


    public void saveVoice(final EMVoiceMessageBody voiceMessageBody){
        String fileName = voiceMessageBody.getFileName();
        File file = new File(FileStorageInfo.getInstance().getVoiceDirName(),fileName);
        //如果不存在 则去下载
        if(!file.exists()){
            String remoteUrl = voiceMessageBody.getRemoteUrl();
            final String filePath = file.getAbsolutePath();
            DownloadUtil.getInstance().download(remoteUrl, filePath, fileName, new DownloadUtil.OnDownloadResultListener() {
                @Override
                public void onDownloadSuccess() {
                    Loger.i("下载成功");
                    voiceMessageBody.setLocalUrl(filePath);
                }

                @Override
                public void onDownloading(int progress) {

                }

                @Override
                public void onDownloadFailed() {
                    Loger.e("下载失败");
                    voiceMessageBody.setLocalUrl("");
                }
            });
        }else{
            voiceMessageBody.setLocalUrl(file.getAbsolutePath());
        }
        //如果存在，则点击可直接进行文件读取
    }

    /**
     * 播放语音
     * @param path 语音地址
     */
    public void playVoice(String path){
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
