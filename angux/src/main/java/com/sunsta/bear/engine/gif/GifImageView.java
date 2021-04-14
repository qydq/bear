package com.sunsta.bear.engine.gif;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;

import com.sunsta.bear.AnApplication;
import com.sunsta.bear.R;
import com.sunsta.bear.engine.GlideEngine;
import com.sunsta.bear.faster.LaBitmap;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.NetBroadcastReceiverUtils;
import com.sunsta.bear.faster.StringUtils;
import com.sunsta.bear.faster.ThreadPool;
import com.sunsta.bear.faster.ValueOf;

import java.io.IOException;

import static com.sunsta.bear.AnConstants.VALUE.LOG_LIVERY_ERROR;

/**
 * An {@link ImageView} which tries treating background and src as {@link GifDrawable}
 * @author koral--
 */
public class GifImageView extends AppCompatImageView {
    private int MSG_GIF = 1;
    private int MSG_GIF_EXCEPTION = 2;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_GIF) {
                GifDrawable gifDrawable = (GifDrawable) msg.obj;
                if (gifDrawable != null) {
                    setBackgroundDrawable(gifDrawable);
//                    setBackground(gifDrawable);
                }
            } else if (msg.what == MSG_GIF_EXCEPTION) {
                GifImageView gifImageView = (GifImageView) msg.obj;
                Bundle bundle = msg.getData();
                if (bundle != null && gifImageView != null) {
                    GlideEngine.getInstance().loadImage(bundle.getString("url"), gifImageView);
                }
            }
            return false;
        }
    });

    private boolean mFreezesAnimation;

    /**
     * A corresponding superclass constructor wrapper.
     * @param context
     * @see ImageView#ImageView(Context)
     */
    public GifImageView(Context context) {
        super(context);
    }

    /**
     * Like equivalent from superclass but also try to interpret src and background
     * attributes as {@link GifDrawable}.
     * @param context
     * @param attrs
     * @see ImageView#ImageView(Context, AttributeSet)
     */
    public GifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInit(GifViewUtils.initImageView(this, attrs, 0, 0));
    }

    /**
     * Like equivalent from superclass but also try to interpret src and background
     * attributes as GIFs.
     * @param context
     * @param attrs
     * @param defStyle
     * @see ImageView#ImageView(Context, AttributeSet, int)
     */
    public GifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        postInit(GifViewUtils.initImageView(this, attrs, defStyle, 0));
    }

    /**
     * Like equivalent from superclass but also try to interpret src and background
     * attributes as GIFs.
     * @param context
     * @param attrs
     * @param defStyle
     * @param defStyleRes
     * @see ImageView#ImageView(Context, AttributeSet, int, int)
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public GifImageView(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyleRes);
        postInit(GifViewUtils.initImageView(this, attrs, defStyle, defStyleRes));
    }

    private void postInit(GifViewUtils.GifImageViewAttributes result) {
        mFreezesAnimation = result.freezesAnimation;
        if (result.mSourceResId > 0) {
            super.setImageResource(result.mSourceResId);
        }
        if (result.mBackgroundResId > 0) {
            super.setBackgroundResource(result.mBackgroundResId);
        }
    }

    /**
     * Sets the content of this GifImageView to the specified Uri.
     * If uri destination is not a GIF then {@link ImageView#setImageURI(Uri)}
     * is called as fallback.
     * For supported URI schemes see: {@link android.content.ContentResolver#openAssetFileDescriptor(Uri, String)}.
     * @param uri The Uri of an image
     */
    @Override
    public void setImageURI(Uri uri) {
        if (!GifViewUtils.setGifImageUri(this, uri)) {
            super.setImageURI(uri);
        }
    }

    @Override
    public void setImageResource(int resId) {
        if (!GifViewUtils.setResource(this, true, resId)) {
            super.setImageResource(resId);
        }
    }

    @Override
    public void setBackgroundResource(int resId) {
        if (!GifViewUtils.setResource(this, false, resId)) {
            super.setBackgroundResource(resId);
        }
    }

    /**
     * Sets the content of this GifImageView to the specified url.
     * If url destination is not a GIF then will use GlideEngine loadImageView
     * is called as fallback.
     * For supported URI schemes see: {@link android.content.ContentResolver#openAssetFileDescriptor(Uri, String)}.
     * @param url The Uri of an image
     */
    public void setImageUrl(String url) {
        if (NetBroadcastReceiverUtils.isConnectedToInternet(AnApplication.getApplication())) {
            ThreadPool.getInstance().getThreadPoolExecutor().execute(() -> {
                Message message = handler.obtainMessage();
                try {
                    message.obj = new GifDrawable(LaBitmap.INSTANCE.getByteOfURL(url));
                    message.what = MSG_GIF;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    message.obj = this;
                    message.what = MSG_GIF_EXCEPTION;
                    Bundle bundle = new Bundle();
                    bundle.putString("url", url);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            });
        } else {
            LaLog.e(ValueOf.logLivery(LOG_LIVERY_ERROR, StringUtils.getString(R.string.an_no_connect_network)));
        }
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Drawable source = mFreezesAnimation ? getDrawable() : null;
        Drawable background = mFreezesAnimation ? getBackground() : null;
        return new GifViewSavedState(super.onSaveInstanceState(), source, background);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof GifViewSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        GifViewSavedState ss = (GifViewSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        ss.restoreState(getDrawable(), 0);
        ss.restoreState(getBackground(), 1);
    }

    /**
     * Sets whether animation position is saved in {@link #onSaveInstanceState()} and restored
     * in {@link #onRestoreInstanceState(Parcelable)}
     * @param freezesAnimation whether animation position is saved
     */
    public void setFreezesAnimation(boolean freezesAnimation) {
        mFreezesAnimation = freezesAnimation;
    }
}
