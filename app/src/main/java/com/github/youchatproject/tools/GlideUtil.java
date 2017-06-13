package com.github.youchatproject.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.youchatproject.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/5
 * 包名： com.github.youchatproject.tools
 * 文档描述：e
 */
public class GlideUtil {
    /**
     * 加载图片
     *
     * @param url
     * @param context
     * @param imageView
     */
    public static void loadImage(String url, Context context, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.vector_drawable_pic_loader_logo)
                .error(R.drawable.vector_drawable_loader_error_drawable)
                .crossFade()
                .into(imageView);
    }

    /**
     *
     * 是否禁止磁盘缓存加载图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param type
     *            缓存的类型
     *            <li>磁盘缓存全部 DiskCacheStrategy.ALL</li>
     *            <li>磁盘禁止缓存DiskCacheStrategy.NONE</li>
     */
    public static void loadImage(String url, Context context, ImageView imageView, DiskCacheStrategy type) {
        Glide.with(context).load(url).diskCacheStrategy(type).into(imageView);
    }

    /**
     *
     * 是否禁止内存缓存加载图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param skipMemoryCache
     *            禁止内存缓存 true为禁止
     */
    public static void loadImage(String url, Context context, ImageView imageView, boolean skipMemoryCache) {
        Glide.with(context).load(url).skipMemoryCache(skipMemoryCache).into(imageView);
    }

    /**
     *
     * 是否禁止内存/磁盘缓存加载图片
     *
     * @param url
     * @param context
     * @param imageView
     * @param type
     *            缓存的类型
     *            <li>磁盘缓存全部 DiskCacheStrategy.ALL</li>
     *            <li>磁盘禁止缓存DiskCacheStrategy.NONE</li>
     * @param skipMemoryCache
     *            禁止内存缓存 true为禁止
     */
    public static void loadImage(String url, Context context, ImageView imageView, DiskCacheStrategy type,
                                 boolean skipMemoryCache) {
        Glide.with(context).load(url).skipMemoryCache(skipMemoryCache).diskCacheStrategy(type).into(imageView);
    }

    /**
     * 清除内存中的缓存 必须在UI线程中调用
     *
     * @param context
     */
    public static void clearMemory(Context context) {
        Glide.get(context).clearMemory();
    }

    /**
     * 清除磁盘中的缓存 必须在后台线程中调用，建议同时clearMemory()
     *
     * @param context
     */
    public static void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }

    /**
     * 优先级加载图片
     * @param url
     * @param context
     * @param imageView
     * @param priority  优先级  Priority.LOW/Priority.HIGH
     */
    public static void loadImageWithPriority(String url, Context context, ImageView imageView, Priority priority) {
        Glide.with(context).load(url).priority(priority).into(imageView);
    }

    /**
     * [通过url得到Bitmap]
     * @param mContext 上下文
     * @param url 地址Url
     * @return Bitmap结果
     */
    public static void getBitmapToUrl(final Context mContext , final String url , final OnBitmapDownloadResultListener listener){
        Observable<Bitmap> observable = Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    Bitmap bitmap = Glide.with(mContext)
                            .load(url)
                            .asBitmap() //必须
                            .centerCrop()
                            .into(1200, 500)
                            .get();
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } catch (Exception e){
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
        observable.subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onNext(Bitmap bitmap) {
                        listener.onSuccess(bitmap);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onError(e);
                    }
                });
    }

    public interface OnBitmapDownloadResultListener{
        void onSuccess(Bitmap bitmap);
        void onError(Throwable e);
    }

    /**
     * 保存文件
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public static void saveFile(Bitmap bm, String fileName) throws IOException {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/youchat/";
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + fileName);
        if(!myCaptureFile.exists()){
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }
    }
}
