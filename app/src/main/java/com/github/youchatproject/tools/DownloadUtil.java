package com.github.youchatproject.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/14
 * 包名： com.github.youchatproject.tools
 * 文档描述：下载工具类
 */
public class DownloadUtil {
    private final OkHttpClient httpClient;
    private static DownloadUtil instance;
    private DownloadUtil(){
        httpClient = new OkHttpClient();
    }

    public static DownloadUtil getInstance(){
        if (instance == null) {
            instance = new DownloadUtil();
        }
        return instance;
    }

    public void download(final String url , final String filePath , final String fileName, final OnDownloadResultListener listener){
        Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(filePath);
                    if(!file.exists()){
                        file.createNewFile();
                    }
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess(file.getAbsolutePath());
                } catch (Exception e) {
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    public interface OnDownloadResultListener{
        /**
         * 下载成功
         */
        void onDownloadSuccess(String path);

        /**
         * @param progress
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }
}
