package com.sunsta.bear.layout;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.core.content.ContextCompat;

import com.sunsta.bear.R;


public class INASwipeCloseFrameLayout extends FrameLayout {
    private Activity mActivity;
    private Scroller mScroller;
    /**
     * 上次ACTION_MOVE时的X坐标
     */
    private int mLastMotionX;
    /**
     * 屏幕宽度
     */
    private int mWidth = -1;
    /**
     * 可滑动的最小X坐标，小于该坐标的滑动不处理
     */
    private int mMinX;
    /**
     * 页面边缘的阴影图
     */
    private Drawable mLeftShadow;
    /**
     * 页面边缘阴影的宽度默认值
     */
    private static final int SHADOW_WIDTH = 16;
    /**
     * 页面边缘阴影的宽度
     */
    private int mShadowWidth;

    public INASwipeCloseFrameLayout(Activity activity) {
        this(activity, null);
    }

    public INASwipeCloseFrameLayout(Activity activity, AttributeSet attrs) {
        this(activity, attrs, 0);
    }

    public INASwipeCloseFrameLayout(Activity activity, AttributeSet attrs, int defStyleAttr) {
        super(activity, attrs, defStyleAttr);
        initView(activity);
    }

    private void initView(Activity activity) {
        mActivity = activity;
        mScroller = new Scroller(mActivity);
//        mLeftShadow = getResources().getDrawable(R.drawable.yy_drawable_slidingclose_shadow);
        mLeftShadow = ContextCompat.getDrawable(mActivity, R.drawable.in_drawable_swipe_shadow);
        int density = (int) activity.getResources().getDisplayMetrics().density;
        mShadowWidth = SHADOW_WIDTH * density;
    }

    /**
     * 绑定Activity
     */
    public void bindActivity(Activity activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View child = decorView.getChildAt(0);
        decorView.removeView(child);
        addView(child);
        decorView.addView(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = (int) event.getX();
                mWidth = getWidth();
                mMinX = mWidth / 10;
                System.out.println("RTLayout---onTouchEvent---DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                int rightMovedX = mLastMotionX - (int) event.getX();
                if (getScrollX() + rightMovedX >= 0) {// 左侧即将滑出屏幕
                    scrollTo(0, 0);
                } else if ((int) event.getX() > mMinX) {// 手指处于屏幕边缘时不处理滑动
                    scrollBy(rightMovedX, 0);
                }
                mLastMotionX = (int) event.getX();
                System.out.println("RTLayout---onTouchEvent---ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                if (-getScrollX() < mWidth / 2) {
                    scrollBack();
                } else {
                    scrollClose();
                }
                System.out.println("RTLayout---onTouchEvent---ACTION_UP");
                break;
        }
        return true;
    }

    /**
     * 滑动返回
     */
    private void scrollBack() {
        int startX = getScrollX();
        int dx = -getScrollX();
        mScroller.startScroll(startX, 0, dx, 0, 300);
        invalidate();
    }

    /**
     * 滑动关闭
     */
    private void scrollClose() {
        int startX = getScrollX();
        int dx = -getScrollX() - mWidth;
        mScroller.startScroll(startX, 0, dx, 0, 300);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        } else if (-getScrollX() == mWidth) {
            mActivity.finish();
        }
        super.computeScroll();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawShadow(canvas);
    }

    /**
     * 绘制边缘的阴影
     */
    private void drawShadow(Canvas canvas) {
        // 保存画布当前的状态
        canvas.save();
        // 设置drawable的大小范围
        mLeftShadow.setBounds(0, 0, mShadowWidth, getHeight());
        // 让画布平移一定距离
        canvas.translate(-mShadowWidth, 0);
        // 绘制Drawable
        mLeftShadow.draw(canvas);
        // 恢复画布的状态
        canvas.restore();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("RTLayout---onInterceptTouchEvent---DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("RTLayout---onInterceptTouchEvent---MOVE");
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("RTLayout---onInterceptTouchEvent---UP");
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(event);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("RTLayout---dispatchTouchEvent---DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("RTLayout---dispatchTouchEvent---MOVE");
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("RTLayout---dispatchTouchEvent---UP");
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
