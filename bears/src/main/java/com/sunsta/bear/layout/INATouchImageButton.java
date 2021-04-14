package com.sunsta.bear.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：此ViewPager解决与父容器ScrollView冲突的问题,无法完美解决.有卡顿 此自定义组件和下拉刷新scrollview配合暂时小完美，有待改善
 * <a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以通过关注我的知乎获取更详细的信息</a>
 * <h3>版权声明：(C) 2016 The Android Developer Sunst</h3>
 * <br>创建日期：2017/4/7
 * <br>邮件email：qyddai@gmail.com
 * <br>个人Github：https://qydq.github.io
 * <p>--#---- Revision History:  --- >  : |version|date|updateinfo|----#--
 * @author sunst
 * @version 1.0 |   2017/4/7           |   TORightViewPager的滑动布局。
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class INATouchImageButton extends AppCompatImageButton {

    private Drawable mForegroundDrawable;
    private Rect mCachedBounds = new Rect();

    public INATouchImageButton(Context context) {
        super(context);
        init();
    }

    public INATouchImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public INATouchImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Reset default ImageButton background and padding.
        setBackgroundColor(0);
        setPadding(0, 0, 0, 0);

        // Retrieve the drawable resource assigned to the
        // android.R.attr.selectableItemBackground
        // theme attribute from the current theme.
        TypedArray a = getContext().obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        mForegroundDrawable = a.getDrawable(0);
        mForegroundDrawable.setCallback(this);
        a.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        // Update the state of the highlight drawable to match
        // the state of the button.
        if (mForegroundDrawable.isStateful()) {
            mForegroundDrawable.setState(getDrawableState());
        }

        // Trigger a redraw.
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // First draw the image.
        super.onDraw(canvas);

        // Then draw the highlight on top of it. If the button is neither
        // focused
        // nor pressed, the drawable will be transparent, so just the image
        // will be drawn.
        mForegroundDrawable.setBounds(mCachedBounds);
        mForegroundDrawable.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Cache the view bounds.
        mCachedBounds.set(0, 0, w, h);
    }
}