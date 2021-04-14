package com.sunsta.bear.layout;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatButton;

import com.sunsta.bear.R;

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
 * @author sunst // sunst0069
 * @version 2.0 |   2016/11/25           |   an系列alidd框架带圆角的按钮
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class INAShapeButton extends AppCompatButton {
    private int radius;
    private int bgColor;

    public INAShapeButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public INAShapeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.INAShapeButton, defStyleAttr, R.style.an_wrap_wrap);
        radius = a.getDimensionPixelOffset(R.styleable.INAShapeButton_anCornerRadius, 0);
        bgColor = a.getColor(R.styleable.INAShapeButton_anBackgrount, 0);
        initView();
    }

    private void initView() {
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{this.bgColor, this.bgColor});
        bg.setCornerRadius((float) this.radius);
        setBackground(bg);
        setClickable(true);
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    protected void animateToPress() {
        AnimatorSet as = new AnimatorSet();
        as.setDuration(66);
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.8f);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.8f);
//ObjectAnimator animation3 = ObjectAnimator.ofInt(this.getBackground(), "Alpha", 204, 179);
        ObjectAnimator animation3 = ObjectAnimator.ofInt(getBackground(), "Alpha", 179, 204);
        as.play(animation1).with(animation2).with(animation3);
        as.start();
    }

    protected void animateToNormal() {
        AnimatorSet as = new AnimatorSet();
        as.setDuration(333);
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(this, "scaleX", 0.8f, 1f);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(this, "scaleY", 0.8f, 1f);
        ObjectAnimator animation3 = ObjectAnimator.ofInt(this.getBackground(), "Alpha", 204, 179);
//ObjectAnimator animation3 = ObjectAnimator.ofInt(this.getBackground(), "Alpha", 179, 204);
        as.play(animation1).with(animation2).with(animation3);
        as.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction() || MotionEvent.ACTION_CANCEL == event.getAction()) {
            animateToNormal();
        } else if (MotionEvent.ACTION_DOWN == event.getAction()) {
            animateToPress();
        }
        return super.onTouchEvent(event);
    }
}