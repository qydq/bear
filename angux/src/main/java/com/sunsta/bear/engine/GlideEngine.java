package com.sunsta.bear.engine;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.sunsta.bear.R;

import io.reactivex.annotations.NonNull;

/**
 * 提供资源图片，圆形，圆角和普通图片四种加载的方法，基本满足我们日常开发中的使用
 */
public class GlideEngine {
    private static GlideEngine instance;

    public static GlideEngine getInstance() {
        if (null == instance) {
            synchronized (GlideEngine.class) {
                if (null == instance) {
                    instance = new GlideEngine();
                }
            }
        }
        return instance;
    }

    /**
     * 加载图片
     * @param imgUrl           图片地址
     * @param imagePlaceholder //加载成功前显示的图片
     * @param imageFallback    //url为空的时候,显示的图片
     * @param imageError       //异常时候显示的图片
     */
    public void loadImageOption(String imgUrl, int imageFallback, int imagePlaceholder, int imageError, @NonNull ImageView imageView) {
        final RequestOptions options = new RequestOptions();
        if (imageError != 0 || imageFallback != 0 || imagePlaceholder != 0) {
            options.skipMemoryCache(false);
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
            options.priority(Priority.HIGH);
            options.error(imageError);
            options.fallback(imageFallback);
            options.placeholder(imagePlaceholder);
        }
        Glide.with(imageView.getContext()).load(imgUrl).apply(options).into(imageView);
    }

    public void loadImage(String imgUrl, int imageFallback, int imagePlaceholder, int imageError, @NonNull ImageView imageView) {
        if (imagePlaceholder != 0) {
            if (imageFallback != 0) {
                Glide.with(imageView.getContext()).load(imgUrl).error(imageError).placeholder(imagePlaceholder).fallback(imageFallback).into(imageView);
            } else {
                Glide.with(imageView.getContext()).load(imgUrl).error(imageError).placeholder(imagePlaceholder).into(imageView);
            }
        } else {
            if (imageFallback != 0) {
                Glide.with(imageView.getContext()).load(imgUrl).error(imageError).fallback(imageFallback).into(imageView);
            } else {
                Glide.with(imageView.getContext()).load(imgUrl).error(imageError).into(imageView);
            }
        }
    }

    public void loadImage(String imgUrl, @NonNull ImageView imageView) {
        loadImage(imgUrl, R.drawable.default_error, 0, R.drawable.default_error_nodata, imageView);
    }

    public void loadImage(String imgUrl, int imageError, @NonNull ImageView imageView) {
        loadImage(imgUrl, 0, 0, imageError, imageView);
    }

    public void loadImage(@DrawableRes int imgResource, int imagePlaceholder, int imageError, ImageView imageView) {
        final RequestOptions options = new RequestOptions();
        options.skipMemoryCache(false);
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.priority(Priority.HIGH);
        options.error(imageError);
        options.placeholder(imagePlaceholder);
        Glide.with(imageView.getContext()).load(imgResource).apply(options).into(imageView);
    }

    public void loadImage(@DrawableRes int imgResource, @NonNull ImageView imageView) {
        final RequestOptions options = new RequestOptions();
        options.skipMemoryCache(false);
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.priority(Priority.HIGH);
        Glide.with(imageView.getContext()).load(imgResource).apply(options).into(imageView);
    }

    public void loadImage(@DrawableRes int imgResource, int defaultPic, @NonNull ImageView imageView) {
        final RequestOptions options = new RequestOptions();
        options.skipMemoryCache(false);
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.priority(Priority.HIGH);
        options.placeholder(defaultPic);
        Glide.with(imageView.getContext()).load(imgResource).apply(options).into(imageView);
    }

    /**
     * 加载圆角图片
     * @param roundingRadius //圆角半径
     */
    public void loadCornerImage(String imgUrl, int roundingRadius, @NonNull ImageView imageView) {
        Glide.with(imageView.getContext()).load(imgUrl).apply(RequestOptions.bitmapTransform(new RoundedCorners(roundingRadius))).into(imageView);
    }

    public void loadCornerImage(@DrawableRes int resId, int roundingRadius, @NonNull ImageView imageView) {
        Glide.with(imageView.getContext()).load(resId).apply(RequestOptions.bitmapTransform(new RoundedCorners(roundingRadius))).into(imageView);
    }

    public void loadRoundImage(String imgUrl, int roundingRadius, @NonNull ImageView imageView) {
        loadRoundImage(imgUrl, roundingRadius, imageView, 300);
    }

    public void loadRoundImage(String imgUrl, int roundingRadius, @NonNull ImageView imageView, int widthHeight) {
        RoundedCorners roundedCorners = new RoundedCorners(roundingRadius);
//通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(widthHeight, widthHeight);
        Glide.with(imageView.getContext())
                .load(imgUrl)
                .apply(options)
                .error(R.drawable.default_error_nodata) //异常时候显示的图片
                .fallback(R.drawable.default_error) //url为空的时候,显示的图片
                .into(imageView);
    }

    /**
     * 加载圆形图片
     */
    public void loadCircleImage(String imgUrl, @NonNull ImageView imageView) {
        Glide.with(imageView.getContext()).load(imgUrl).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(imageView);
    }

    public void loadCircleImage(String imgUrl, int imageError, @NonNull ImageView imageView) {
        Glide.with(imageView.getContext()).load(imgUrl).apply(RequestOptions.bitmapTransform(new CircleCrop())).error(imageError).into(imageView);
    }

    public void loadCircleImage(@DrawableRes int resId, @NonNull ImageView imageView) {
        Glide.with(imageView.getContext()).load(resId).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(imageView);
    }

    public void loadCircleImage(@DrawableRes int resId, int imageError, @NonNull ImageView imageView) {
        Glide.with(imageView.getContext()).load(resId).apply(RequestOptions.bitmapTransform(new CircleCrop())).error(imageError).into(imageView);
    }

    /**
     * 加载Gif第一针图片
     */
    public void loadFrameGifImage(@NonNull String url, @NonNull ImageView imageView) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        Drawable drawable = new BitmapDrawable(resource);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }
}