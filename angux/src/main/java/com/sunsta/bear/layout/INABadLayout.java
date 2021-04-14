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
import com.sunsta.bear.config.BadConfig;
import com.sunsta.bear.engine.GlideEngine;
import com.sunsta.bear.faster.LoadingDialog;
import com.sunsta.bear.faster.ScreenUtils;
import com.sunsta.bear.immersion.RichTextView;

public class INABadLayout extends FrameLayout {
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
    private static BadConfig badConfig = new BadConfig();
    private OnIvClickListener mIvListener;
    private OnTextClickListener mOnTextClickListener;
    private OnClickListener onClickListener;

    public INABadLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public INABadLayout(@NonNull Context context) {
        this(context, null);
    }

    public INABadLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /*
     * "姓名:<b><font color=\"#FF3D49\">" + sunstaName + "</font></b>"
     * */
    public INABadLayout setLightContent(String lightContent) {
        badConfig.setLightContent(lightContent);
        return this;
    }

    public INABadLayout setClickContent(String clickContent) {
        badConfig.setClickContent(clickContent);
        return this;
    }

    public INABadLayout setLightColor(@ColorRes int lightColor) {
        badConfig.setLightColor(lightColor);
        return this;
    }

    public INABadLayout setClickColor(@ColorRes int clickColor) {
        badConfig.setClickColor(clickColor);
        return this;
    }

    /**
     * Set the distance to trigger a sync in dips with topbar
     *
     * @param distanceToTrigger
     */
    public INABadLayout setDistanceToTrigger(@DimenRes int distanceToTrigger) {
        badConfig.setDistanceToTrigger(getResources().getDimensionPixelSize(distanceToTrigger));
        return this;
    }

    public INABadLayout setIvErrorDrawableRes(@DrawableRes int imageError) {
        badConfig.setIvErrorDrawableRes(imageError);
        return this;
    }

    public INABadLayout setIvLinkUrl(String ivLinkUrl) {
        badConfig.setIvLinkUrl(ivLinkUrl);
        return this;
    }

    public INABadLayout setBackground(@ColorRes int background) {
        badConfig.setBackground(background);
        return this;
    }

    public INABadLayout setIvErrorFixed(boolean ivErrorFixed) {
        badConfig.setIvErrorFixed(ivErrorFixed);
        return this;
    }

    public INABadLayout setEnableClickColor(boolean enableClickColor) {
        badConfig.setEnableClickColor(enableClickColor);
        return this;
    }

    public INABadLayout setIvErrorHeightWidth(@DimenRes int ivErrorHeight, @DimenRes int ivErrorWidth) {
        badConfig.setIvErrorHeight(ivErrorHeight);
        badConfig.setIvErrorWidth(ivErrorWidth);
        return this;
    }

    public INABadLayout setIvErrorSize(@DimenRes int doubleWidthHeight) {
        badConfig.setIvErrorHeight(doubleWidthHeight);
        badConfig.setIvErrorWidth(doubleWidthHeight);
        return this;
    }

    public INABadLayout setBadMode(BadConfig mod) {
        this.badConfig = mod;
        return this;
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            @SuppressLint("Recycle") TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.INABadLayout);
            badConfig.setDistanceToTrigger(ta.getDimensionPixelSize(R.styleable.INABadLayout_bad_marginTop, 0));
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
                if (badConfig.getBackground() != 0) {
                    action0.setBackgroundColor(badConfig.getBackground());
                }
                if (badConfig.getDistanceToTrigger() == 0) {
                    RelativeLayout.LayoutParams centerParams = (RelativeLayout.LayoutParams) action0.getLayoutParams();
                    centerParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    action0.setLayoutParams(centerParams);
                } else {
                    ScreenUtils.setLayoutParamsHeight(tvPatch, badConfig.getDistanceToTrigger());
                }
            }

            if (null != tvLight) {
                if (!TextUtils.isEmpty(badConfig.getLightContent())) {
                    RichTextView.setRichText(tvLight, badConfig.getLightContent());
                    tvLight.setVisibility(VISIBLE);
                } else {
                    tvLight.setVisibility(GONE);
                }
                if (badConfig.getLightColor() != 0) {
                    RichTextView.setTextColor(tvLight, badConfig.getLightColor());
                }
            }

            if (null != tvClick) {
                if (!TextUtils.isEmpty(badConfig.getClickContent())) {
                    RichTextView.setRichText(tvClick, badConfig.getClickContent());
                    tvClick.setVisibility(VISIBLE);
                } else {
                    tvClick.setVisibility(GONE);
                }
                if (badConfig.isEnableClickColor()) {
                    RichTextView.setTextColor(tvClick, R.color.in_font_selector_error);
                } else {
                    RichTextView.setTextColor(tvClick, R.color.an_font_error2);
                }
                if (badConfig.getClickColor() != 0) {
                    RichTextView.setTextColor(tvClick, badConfig.getClickColor());
                }
            }

            if (ivError != null) {
                if (badConfig.isIvErrorFixed()) {
                    ScreenUtils.setLayoutParams(ivError, 100, 100);
                } else {
                    int ivWidth = badConfig.getIvErrorWidth();
                    int ivHeight = badConfig.getIvErrorHeight();
                    if (ivWidth != 0 || ivHeight != 0) {
                        ScreenUtils.setLayoutParams(ivError, getResources().getDimensionPixelSize(ivWidth), getResources().getDimensionPixelSize(ivHeight));
                    }
                }

                if (badConfig.getIvErrorDrawableRes() != 0) {
// ivError.setImageResource(badConfig.getIvErrorDrawableRes());
                    GlideEngine.getInstance().loadImage(badConfig.getIvErrorDrawableRes(), ivError);
                }
                if (!TextUtils.isEmpty(badConfig.getIvLinkUrl())) {
                    GlideEngine.getInstance().loadImage(badConfig.getIvLinkUrl(), ivError);
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

    public INABadLayout setOnIvClickListener(OnIvClickListener mListener) {
        this.mIvListener = mListener;
        return this;
    }

    public interface OnTextClickListener {
        void onClick();
    }

    public INABadLayout setOnTxtClickListener(OnTextClickListener mListener) {
        this.mOnTextClickListener = mListener;
        return this;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}