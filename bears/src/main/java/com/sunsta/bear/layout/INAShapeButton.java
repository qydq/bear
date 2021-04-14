package com.sunsta.bear.layout;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.ColorRes;
import androidx.appcompat.widget.AppCompatButton;

import com.sunsta.bear.R;
import com.sunsta.bear.immersion.ColorDrawer;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：an系列alidd框架带圆角的按钮
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 *
 * @author sunst // sunst0069
 * @version 2.0 |   2016/11/25           |   an系列alidd框架带圆角的按钮
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class INAShapeButton extends AppCompatButton {
    private int radius;
    private boolean animator = false;
    private boolean enabledButton = true;
    private int bgColor = Color.parseColor("#FFFFFF");
    private int unBgColor = Color.parseColor("#10000000");
    private float alpha = 0.8f;

    public INAShapeButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public INAShapeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.INAShapeButton, defStyleAttr, R.style.an_wrap_wrap);
        radius = a.getDimensionPixelOffset(R.styleable.INAShapeButton_anShapeRadius, 0);
        bgColor = a.getColor(R.styleable.INAShapeButton_anShapeBackground, bgColor);
        unBgColor = a.getColor(R.styleable.INAShapeButton_anShapeUnBackground, unBgColor);
        animator = a.getBoolean(R.styleable.INAShapeButton_asShapeAnimator, animator);
        alpha = a.getFloat(R.styleable.INAShapeButton_anShapeAlpha, alpha);
        initView();
    }

    private void initView() {
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{bgColor, bgColor});
        bg.setCornerRadius((float) radius);
        setBackground(bg);
        setClickable(true);
    }

    public void setAnimator(boolean animator) {
        this.animator = animator;
    }

    public void setShapeBackground(@ColorRes int shapeBackground) {
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{ColorDrawer.getColor(shapeBackground), ColorDrawer.getColor(shapeBackground)});
        bg.setCornerRadius((float) this.radius);
        setBackground(bg);
    }

    public void setEnabled(boolean enabled, @ColorRes int shapeBackground) {
        super.setEnabled(enabled);
        if (enabled) {
            setClickable(true);
        } else {
            setClickable(false);
        }
        setShapeBackground(shapeBackground);
        enabledButton = enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setClickable(true);
        } else {
            setClickable(false);
            GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{unBgColor, unBgColor});
            bg.setCornerRadius((float) this.radius);
            setBackground(bg);
        }
        enabledButton = enabled;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    protected void animateToPress() {
        AnimatorSet as = new AnimatorSet();
        as.setDuration(66);
        if (animator) {
            ObjectAnimator animation1 = ObjectAnimator.ofFloat(this, "scaleX", 1f, alpha);
            ObjectAnimator animation2 = ObjectAnimator.ofFloat(this, "scaleY", 1f, alpha);
            as.play(animation1).with(animation2);
        }

        ObjectAnimator animation3 = ObjectAnimator.ofInt(this.getBackground(), "Alpha", 204, 179);
//        ObjectAnimator animation3 = ObjectAnimator.ofInt(getBackground(), "Alpha", 179, 204);
        as.play(animation3);
//        as.play(animation1).with(animation2).with(animation3);
        as.start();
    }

    protected void animateToNormal() {
        AnimatorSet as = new AnimatorSet();
        as.setDuration(333);
        if (animator) {
            ObjectAnimator animation1 = ObjectAnimator.ofFloat(this, "scaleX", alpha, 1f);
            ObjectAnimator animation2 = ObjectAnimator.ofFloat(this, "scaleY", alpha, 1f);
            as.play(animation1).with(animation2);
        }
//        ObjectAnimator animation3 = ObjectAnimator.ofInt(this.getBackground(), "Alpha", 204, 179);
        ObjectAnimator animation3 = ObjectAnimator.ofInt(this.getBackground(), "Alpha", 179, 204);
        as.play(animation3);
//        as.play(animation1).with(animation2).with(animation3);
        as.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (enabledButton) {
            if (MotionEvent.ACTION_UP == event.getAction() || MotionEvent.ACTION_CANCEL == event.getAction()) {
                animateToNormal();
            } else if (MotionEvent.ACTION_DOWN == event.getAction()) {
                animateToPress();
            }
        }
        return super.onTouchEvent(event);
    }
}