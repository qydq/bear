package com.sunsta.bear.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sunsta.bear.R;
import com.sunsta.bear.config.StatusConfig;
import com.sunsta.bear.engine.GlideEngine;
import com.sunsta.bear.faster.ScreenUtils;
import com.sunsta.bear.immersion.RichTextView;

public class INAStatusLayout extends FrameLayout {
    public enum ErrorState {
        NORMAL,
        LOADING,
        ERROR,
        NO_NETWORK
    }

    private RelativeLayout frameLayout;
    private LinearLayout action0;
    private ImageView ivError;
    private TextView tvClick, tvLight, tvPatch;
    private AnimationDrawable animationDrawable;
    private static StatusConfig statusConfig = new StatusConfig();
    private OnIvClickListener mIvListener;
    private OnTextClickListener mOnTextClickListener;
    private OnClickListener onClickListener;

    public INAStatusLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public INAStatusLayout(@NonNull Context context) {
        this(context, null);
    }

    public INAStatusLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /*
     * "姓名:<b><font color=\"#FF3D49\">" + sunstaName + "</font></b>"
     * */
    public INAStatusLayout setLightContent(String lightContent) {
        statusConfig.setLightContent(lightContent);
        return this;
    }

    public INAStatusLayout setClickContent(String clickContent) {
        statusConfig.setClickContent(clickContent);
        return this;
    }

    public INAStatusLayout setLightColor(@ColorRes int lightColor) {
        statusConfig.setLightColor(lightColor);
        return this;
    }

    public INAStatusLayout setClickColor(@ColorRes int clickColor) {
        statusConfig.setClickColor(clickColor);
        return this;
    }

    /**
     * Set the distance to trigger a sync in dips with topbar
     *
     * @param distanceToTrigger
     */
    public INAStatusLayout setDistanceToTrigger(@DimenRes int distanceToTrigger) {
        statusConfig.setDistanceToTrigger(getResources().getDimensionPixelSize(distanceToTrigger));
        return this;
    }

    public INAStatusLayout setIvErrorDrawableRes(@DrawableRes int imageError) {
        statusConfig.setIvErrorDrawableRes(imageError);
        return this;
    }

    public INAStatusLayout setIvLinkUrl(String ivLinkUrl) {
        statusConfig.setIvLinkUrl(ivLinkUrl);
        return this;
    }

    public INAStatusLayout setBackground(@ColorRes int background) {
        statusConfig.setBackground(background);
        return this;
    }

    public INAStatusLayout setIvErrorFixed(boolean ivErrorFixed) {
        statusConfig.setIvErrorFixed(ivErrorFixed);
        return this;
    }

    public INAStatusLayout setEnableClickColor(boolean enableClickColor) {
        statusConfig.setEnableClickColor(enableClickColor);
        return this;
    }

    public INAStatusLayout setIvErrorHeightWidth(@DimenRes int ivErrorHeight, @DimenRes int ivErrorWidth) {
        statusConfig.setIvErrorHeight(ivErrorHeight);
        statusConfig.setIvErrorWidth(ivErrorWidth);
        return this;
    }

    public INAStatusLayout setIvErrorSize(@DimenRes int doubleWidthHeight) {
        statusConfig.setIvErrorHeight(doubleWidthHeight);
        statusConfig.setIvErrorWidth(doubleWidthHeight);
        return this;
    }

    public INAStatusLayout setBadMode(StatusConfig mod) {
        this.statusConfig = mod;
        return this;
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            @SuppressLint("Recycle") TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.INAStatusLayout);
            statusConfig.setDistanceToTrigger(ta.getDimensionPixelSize(R.styleable.INAStatusLayout_anMarginTop, 0));
            LayoutInflater.from(context).inflate(R.layout.layout_error, this);
            frameLayout = findViewById(R.id.frameLayout);
            action0 = findViewById(R.id.action0);
            ivError = findViewById(R.id.ivError);
            tvPatch = findViewById(R.id.tvPatch);
            tvClick = findViewById(R.id.tvPrimary);
            tvLight = findViewById(R.id.tvLight);
            trigger(ErrorState.NORMAL);
        }
    }

    private void globalEvent() {
        if (null != onClickListener) {
            if (frameLayout != null) {
                frameLayout.setClickable(true);
                frameLayout.setOnClickListener(onClickListener);
            }
        } else {
            if (frameLayout != null) {
                frameLayout.setClickable(false);
                frameLayout.setOnClickListener(null);
            }
        }
        if (null != mIvListener) {
            ivError.setClickable(true);
            ivError.setOnClickListener(view -> mIvListener.onClick());
        } else {
            ivError.setClickable(false);
            ivError.setOnClickListener(null);
        }
        if (null != mOnTextClickListener) {
            tvClick.setClickable(true);
            tvClick.setOnClickListener(view -> mOnTextClickListener.onClick());
        } else {
            tvClick.setClickable(false);
            tvClick.setOnClickListener(null);
        }
    }

    public void trigger() {
        trigger(ErrorState.NORMAL);
    }

    public void trigger(ErrorState pageState) {
        if (pageState == ErrorState.NORMAL) {
            setVisibility(View.GONE);
            frameLayout.setVisibility(GONE);
            if (null != animationDrawable && animationDrawable.isRunning()) {
                animationDrawable.stop();
            }
            if (ivError != null) {
                ivError.clearAnimation();
            }
        } else if (pageState == ErrorState.LOADING) {
            if (ivError != null) {
                ivError.setBackgroundResource(R.drawable.in_selector_loading);
                Drawable loadingDrawable = ivError.getDrawable();
                if (null != loadingDrawable) {
                    animationDrawable = (AnimationDrawable) loadingDrawable;
                    if (!animationDrawable.isRunning()) {
                        animationDrawable.start();
                    }
                }
            }
        } else {
            globalEvent();
            setVisibility(View.VISIBLE);
            if (frameLayout != null) {
                frameLayout.setVisibility(VISIBLE);
            }

            if (action0 != null) {
                if (statusConfig.getBackground() != 0) {
                    action0.setBackgroundColor(statusConfig.getBackground());
                }
                if (statusConfig.getDistanceToTrigger() == 0) {
                    RelativeLayout.LayoutParams centerParams = (RelativeLayout.LayoutParams) action0.getLayoutParams();
                    centerParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    action0.setLayoutParams(centerParams);
                } else {
                    ScreenUtils.setLayoutParamsHeight(tvPatch, statusConfig.getDistanceToTrigger());
                }
            }

            if (null != tvLight) {
                if (!TextUtils.isEmpty(statusConfig.getLightContent())) {
                    RichTextView.setRichText(tvLight, statusConfig.getLightContent());
                    tvLight.setVisibility(VISIBLE);
                } else {
                    tvLight.setVisibility(GONE);
                }
                if (statusConfig.getLightColor() != 0) {
                    RichTextView.setTextColor(tvLight, statusConfig.getLightColor());
                }
            }

            if (null != tvClick) {
                if (!TextUtils.isEmpty(statusConfig.getClickContent())) {
                    RichTextView.setRichText(tvClick, statusConfig.getClickContent());
                    tvClick.setVisibility(VISIBLE);
                } else {
                    tvClick.setVisibility(GONE);
                }
                if (statusConfig.isEnableClickColor()) {
                    RichTextView.setTextColor(tvClick, R.color.in_font_selector_error);
                } else {
                    RichTextView.setTextColor(tvClick, R.color.an_font_error2);
                }
                if (statusConfig.getClickColor() != 0) {
                    RichTextView.setTextColor(tvClick, statusConfig.getClickColor());
                }
            }

            if (ivError != null) {
                if (statusConfig.isIvErrorFixed()) {
                    ScreenUtils.setLayoutParams(ivError, 100, 100);
                } else {
                    int ivWidth = statusConfig.getIvErrorWidth();
                    int ivHeight = statusConfig.getIvErrorHeight();
                    if (ivWidth != 0 || ivHeight != 0) {
                        ScreenUtils.setLayoutParams(ivError, getResources().getDimensionPixelSize(ivWidth), getResources().getDimensionPixelSize(ivHeight));
                    }
                }

                if (statusConfig.getIvErrorDrawableRes() != 0) {
//                    ivError.setImageResource(badConfig.getIvErrorDrawableRes());
                    GlideEngine.getInstance().loadImage(statusConfig.getIvErrorDrawableRes(), ivError);
                }
                if (!TextUtils.isEmpty(statusConfig.getIvLinkUrl())) {
                    GlideEngine.getInstance().loadImage(statusConfig.getIvLinkUrl(), ivError);
                }
            }
            switch (pageState) {
                case ERROR:
                    break;
                case NO_NETWORK:
                    tvLight.setText("网络错误，请点击屏幕重新加载");
                    break;
                default:
                    this.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public interface OnIvClickListener {
        void onClick();
    }

    public INAStatusLayout setOnIvClickListener(OnIvClickListener mListener) {
        this.mIvListener = mListener;
        return this;
    }

    public interface OnTextClickListener {
        void onClick();
    }

    public INAStatusLayout setOnTxtClickListener(OnTextClickListener mListener) {
        this.mOnTextClickListener = mListener;
        return this;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}