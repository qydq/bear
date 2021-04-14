package com.sunsta.bear.layout;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.sunsta.bear.R;
import com.sunsta.bear.faster.ViewUtils;
import com.sunsta.bear.faster.LaBitmap;
import com.sunsta.bear.faster.ScreenUtils;
import com.sunsta.bear.listener.OnLikeListener;
import com.sunsta.bear.model.entity.Icon;
import com.sunsta.bear.model.entity.IconType;

import java.util.Iterator;
import java.util.List;

public class INALikeButton extends FrameLayout implements OnClickListener {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4.0F);
    private ImageView icon;
    private INADotsView dotsView;
    private INACircleView circleView;
    private Icon currentIcon;
    private OnLikeListener likeListener;
    private int dotPrimaryColor;
    private int dotSecondaryColor;
    private int circleStartColor;
    private int circleEndColor;
    private int iconSize;
    private float animationScaleFactor;
    private boolean isChecked;
    private boolean isEnabled;
    private AnimatorSet animatorSet;
    private Drawable likeDrawable;
    private Drawable unLikeDrawable;

    public INALikeButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public INALikeButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public INALikeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater.from(this.getContext()).inflate(R.layout.base_likeview, this, true);
        this.icon = this.findViewById(R.id.icon);
        this.dotsView = this.findViewById(R.id.dots);
        this.circleView = this.findViewById(R.id.circle);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.INALikeButton, defStyle, 0);
        this.iconSize = array.getDimensionPixelSize(R.styleable.INALikeButton_anLikeIconSize, -1);
        if (this.iconSize == -1) {
            this.iconSize = 40;
        }

        String iconType = array.getString(R.styleable.INALikeButton_anLikeIconType);
        this.likeDrawable = this.getDrawableFromResource(array, R.styleable.INALikeButton_anLikeDrawable);
        if (this.likeDrawable != null) {
            this.setLikeDrawable(this.likeDrawable);
        }

        this.unLikeDrawable = this.getDrawableFromResource(array, R.styleable.INALikeButton_anUnlikeDrawable);
        if (this.unLikeDrawable != null) {
            this.setUnlikeDrawable(this.unLikeDrawable);
        }

        if (iconType != null && !iconType.isEmpty()) {
            this.currentIcon = this.parseIconType(iconType);
        }

        this.circleStartColor = array.getColor(R.styleable.INALikeButton_anLikeCircleStartColor, 0);
        if (this.circleStartColor != 0) {
            this.circleView.setStartColor(this.circleStartColor);
        }

        this.circleEndColor = array.getColor(R.styleable.INALikeButton_anLikeCircleEndColor, 0);
        if (this.circleEndColor != 0) {
            this.circleView.setEndColor(this.circleEndColor);
        }

        this.dotPrimaryColor = array.getColor(R.styleable.INALikeButton_anDotPrimaryColor, 0);
        this.dotSecondaryColor = array.getColor(R.styleable.INALikeButton_anDotSecondaryColor, 0);
        if (this.dotPrimaryColor != 0 && this.dotSecondaryColor != 0) {
            this.dotsView.setColors(this.dotPrimaryColor, this.dotSecondaryColor);
        }

        if (this.likeDrawable == null && this.unLikeDrawable == null) {
            if (this.currentIcon != null) {
                this.setIcon();
            } else {
                this.setIcon(IconType.Heart);
            }
        }

        this.setEnabled(array.getBoolean(R.styleable.INALikeButton_asLikeEnabled, true));
        Boolean status = array.getBoolean(R.styleable.INALikeButton_asLiked, false);
        this.setAnimationScaleFactor(array.getFloat(R.styleable.INALikeButton_anLikeAnimScaleFactor, 3.0F));
        this.setLiked(status);
        this.setOnClickListener(this);
        array.recycle();
    }

    private Drawable getDrawableFromResource(TypedArray array, int styleableIndexId) {
        int id = array.getResourceId(styleableIndexId, -1);
        return -1 != id ? ContextCompat.getDrawable(this.getContext(), id) : null;
    }

    public void onClick(View v) {
        if (this.isEnabled) {
            this.isChecked = !this.isChecked;
            this.icon.setImageDrawable(this.isChecked ? this.likeDrawable : this.unLikeDrawable);
            if (this.likeListener != null) {
                if (this.isChecked) {
                    this.likeListener.liked(this);
                } else {
                    this.likeListener.unLiked(this);
                }
            }

            if (this.animatorSet != null) {
                this.animatorSet.cancel();
            }

            if (this.isChecked) {
                this.icon.animate().cancel();
                this.icon.setScaleX(0.0F);
                this.icon.setScaleY(0.0F);
                this.circleView.setInnerCircleRadiusProgress(0.0F);
                this.circleView.setOuterCircleRadiusProgress(0.0F);
                this.dotsView.setCurrentProgress(0.0F);
                this.animatorSet = new AnimatorSet();
                ObjectAnimator outerCircleAnimator = ObjectAnimator.ofFloat(this.circleView, INACircleView.OUTER_CIRCLE_RADIUS_PROGRESS, new float[]{0.1F, 1.0F});
                outerCircleAnimator.setDuration(250L);
                outerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);
                ObjectAnimator innerCircleAnimator = ObjectAnimator.ofFloat(this.circleView, INACircleView.INNER_CIRCLE_RADIUS_PROGRESS, new float[]{0.1F, 1.0F});
                innerCircleAnimator.setDuration(200L);
                innerCircleAnimator.setStartDelay(200L);
                innerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);
                ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(this.icon, ImageView.SCALE_Y, new float[]{0.2F, 1.0F});
                starScaleYAnimator.setDuration(350L);
                starScaleYAnimator.setStartDelay(250L);
                starScaleYAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);
                ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(this.icon, ImageView.SCALE_X, new float[]{0.2F, 1.0F});
                starScaleXAnimator.setDuration(350L);
                starScaleXAnimator.setStartDelay(250L);
                starScaleXAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);
                ObjectAnimator dotsAnimator = ObjectAnimator.ofFloat(this.dotsView, INADotsView.DOTS_PROGRESS, new float[]{0.0F, 1.0F});
                dotsAnimator.setDuration(900L);
                dotsAnimator.setStartDelay(50L);
                dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);
                this.animatorSet.playTogether(new Animator[]{outerCircleAnimator, innerCircleAnimator, starScaleYAnimator, starScaleXAnimator, dotsAnimator});
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationCancel(Animator animation) {
                        INALikeButton.this.circleView.setInnerCircleRadiusProgress(0.0F);
                        INALikeButton.this.circleView.setOuterCircleRadiusProgress(0.0F);
                        INALikeButton.this.dotsView.setCurrentProgress(0.0F);
                        INALikeButton.this.icon.setScaleX(1.0F);
                        INALikeButton.this.icon.setScaleY(1.0F);
                    }
                });
                this.animatorSet.start();
            }

        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isEnabled) {
            return true;
        } else {
            switch (event.getAction()) {
                case 0:
                    this.setPressed(true);
                    break;
                case 1:
                    this.icon.animate().scaleX(0.7F).scaleY(0.7F).setDuration(150L).setInterpolator(DECCELERATE_INTERPOLATOR);
                    this.icon.animate().scaleX(1.0F).scaleY(1.0F).setInterpolator(DECCELERATE_INTERPOLATOR);
                    if (this.isPressed()) {
                        this.performClick();
                        this.setPressed(false);
                    }
                    break;
                case 2:
                    float x = event.getX();
                    float y = event.getY();
                    boolean isInside = x > 0.0F && x < (float) this.getWidth() && y > 0.0F && y < (float) this.getHeight();
                    if (this.isPressed() != isInside) {
                        this.setPressed(isInside);
                    }
            }

            return true;
        }
    }

    public void setLikeDrawableRes(@DrawableRes int resId) {
        this.likeDrawable = ContextCompat.getDrawable(this.getContext(), resId);
        if (this.iconSize != 0) {
            this.likeDrawable = LaBitmap.INSTANCE.resizeDrawable(this.getContext(), this.likeDrawable, this.iconSize, this.iconSize);
        }

        if (this.isChecked) {
            this.icon.setImageDrawable(this.likeDrawable);
        }

    }

    public void setLikeDrawable(Drawable likeDrawable) {
        this.likeDrawable = likeDrawable;
        if (this.iconSize != 0) {
            this.likeDrawable = LaBitmap.INSTANCE.resizeDrawable(this.getContext(), likeDrawable, this.iconSize, this.iconSize);
        }

        if (this.isChecked) {
            this.icon.setImageDrawable(this.likeDrawable);
        }

    }

    public void setUnlikeDrawableRes(@DrawableRes int resId) {
        this.unLikeDrawable = ContextCompat.getDrawable(this.getContext(), resId);
        if (this.iconSize != 0) {
            this.unLikeDrawable = LaBitmap.INSTANCE.resizeDrawable(this.getContext(), this.unLikeDrawable, this.iconSize, this.iconSize);
        }

        if (!this.isChecked) {
            this.icon.setImageDrawable(this.unLikeDrawable);
        }

    }

    public void setUnlikeDrawable(Drawable unLikeDrawable) {
        this.unLikeDrawable = unLikeDrawable;
        if (this.iconSize != 0) {
            this.unLikeDrawable = LaBitmap.INSTANCE.resizeDrawable(this.getContext(), unLikeDrawable, this.iconSize, this.iconSize);
        }

        if (!this.isChecked) {
            this.icon.setImageDrawable(this.unLikeDrawable);
        }

    }

    public void setIcon(IconType currentIconType) {
        this.currentIcon = this.parseIconType(currentIconType);
        this.setLikeDrawableRes(this.currentIcon.getOnIconResourceId());
        this.setUnlikeDrawableRes(this.currentIcon.getOffIconResourceId());
        this.icon.setImageDrawable(this.unLikeDrawable);
    }

    public void setIcon() {
        this.setLikeDrawableRes(this.currentIcon.getOnIconResourceId());
        this.setUnlikeDrawableRes(this.currentIcon.getOffIconResourceId());
        this.icon.setImageDrawable(this.unLikeDrawable);
    }

    public void setIconSizeDp(int iconSize) {
        this.setIconSizePx(ScreenUtils.dip2px((float) iconSize));
    }

    public void setIconSizePx(int iconSize) {
        this.iconSize = iconSize;
        this.setEffectsViewSize();
        this.unLikeDrawable = LaBitmap.INSTANCE.resizeDrawable(this.getContext(), this.unLikeDrawable, iconSize, iconSize);
        this.likeDrawable = LaBitmap.INSTANCE.resizeDrawable(this.getContext(), this.likeDrawable, iconSize, iconSize);
    }

    private Icon parseIconType(String iconType) {
        List<Icon> icons = ViewUtils.getInstance().getIcons();
        Iterator var3 = icons.iterator();

        Icon icon;
        do {
            if (!var3.hasNext()) {
                throw new IllegalArgumentException("Correct icon type not specified.");
            }

            icon = (Icon) var3.next();
        } while (!icon.getIconType().name().toLowerCase().equals(iconType.toLowerCase()));

        return icon;
    }

    private Icon parseIconType(IconType iconType) {
        List<Icon> icons = ViewUtils.getInstance().getIcons();
        Iterator var3 = icons.iterator();

        Icon icon;
        do {
            if (!var3.hasNext()) {
                throw new IllegalArgumentException("Correct icon type not specified.");
            }

            icon = (Icon) var3.next();
        } while (!icon.getIconType().equals(iconType));

        return icon;
    }

    public void setOnLikeListener(OnLikeListener likeListener) {
        this.likeListener = likeListener;
    }

    public void setExplodingDotColorsRes(@ColorRes int primaryColor, @ColorRes int secondaryColor) {
        this.dotsView.setColors(ContextCompat.getColor(this.getContext(), primaryColor), ContextCompat.getColor(this.getContext(), secondaryColor));
    }

    public void setExplodingDotColorsInt(@ColorInt int primaryColor, @ColorInt int secondaryColor) {
        this.dotsView.setColors(primaryColor, secondaryColor);
    }

    public void setCircleStartColorRes(@ColorRes int circleStartColor) {
        this.circleStartColor = ContextCompat.getColor(this.getContext(), circleStartColor);
        this.circleView.setStartColor(this.circleStartColor);
    }

    public void setCircleStartColorInt(@ColorInt int circleStartColor) {
        this.circleStartColor = circleStartColor;
        this.circleView.setStartColor(circleStartColor);
    }

    public void setCircleEndColorRes(@ColorRes int circleEndColor) {
        this.circleEndColor = ContextCompat.getColor(this.getContext(), circleEndColor);
        this.circleView.setEndColor(this.circleEndColor);
    }

    private void setEffectsViewSize() {
        if (this.iconSize != 0) {
            this.dotsView.setSize((int) ((float) this.iconSize * this.animationScaleFactor), (int) ((float) this.iconSize * this.animationScaleFactor));
            this.circleView.setSize(this.iconSize, this.iconSize);
        }

    }

    public void setLiked(Boolean status) {
        if (status) {
            this.isChecked = true;
            this.icon.setImageDrawable(this.likeDrawable);
        } else {
            this.isChecked = false;
            this.icon.setImageDrawable(this.unLikeDrawable);
        }

    }

    public boolean isLiked() {
        return this.isChecked;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public void setAnimationScaleFactor(float animationScaleFactor) {
        this.animationScaleFactor = animationScaleFactor;
        this.setEffectsViewSize();
    }
}
