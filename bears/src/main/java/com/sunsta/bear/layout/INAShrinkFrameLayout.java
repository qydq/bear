package com.sunsta.bear.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：可以缩放的布局FrameLayout
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 1.0 |   2018/01/10           |   可以缩放的布局FrameLayout。
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class INAShrinkFrameLayout extends FrameLayout {
    // 屏幕宽高
    private int screenHeight;
    private int screenWidth;
    private ViewDragHelper mDragHelper;
    private long lastMultiTouchTime;// 记录多点触控缩放后的时间
    private int originalWidth;// view宽度
    private int originalHeight;// view高度
    private ScaleGestureDetector mScaleGestureDetector = null;
    // private View view;
    private int downX;// 手指按下的x坐标值
    private int downY;// 手指按下的y坐标值
    private int left;// view的左坐标值
    private int top;// view的上坐标值
    private int right;// view的右坐标值
    private int bottom;// view的下坐标值
    private int newHeight;
    private int newWidth;

    private float scale;
    private float preScale = 1;// 默认前一次缩放比例为1

    public INAShrinkFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public INAShrinkFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public INAShrinkFrameLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mDragHelper = ViewDragHelper.create(this, callback);
        mScaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureListener());

        // view.post(new Runnable() {
        //
        // @Override
        // public void run() {
        // left = view.getLeft();
        // top = view.getTop();
        // right = view.getRight();
        // bottom = view.getBottom();
        // originalWidth = view.getWidth();
        // originalHeight = view.getHeight();
        // }
        // });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = getMeasuredWidth();
        screenHeight = getMeasuredHeight();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        boolean b = mDragHelper.shouldInterceptTouchEvent(ev);// 由mDragHelper决定是否拦截事件，并传递给onTouchEvent
        return b;
    }

    private boolean needToHandle = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int pointerCount = event.getPointerCount(); // 获得多少点
        if (pointerCount > 1) {// 多点触控，
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    needToHandle = false;
                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_POINTER_2_UP://第二个手指抬起的时候
                    needToHandle = true;
                    break;

                default:
                    break;
            }
            return mScaleGestureDetector.onTouchEvent(event);//让mScaleGestureDetector处理触摸事件
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - lastMultiTouchTime > 200 && needToHandle) {
//                  多点触控全部手指抬起后要等待200毫秒才能执行单指触控的操作，避免多点触控后出现颤抖的情况
                try {
                    mDragHelper.processTouchEvent(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
//            }
        }
        return false;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸事件
         *
         * @param child
         * 当前触摸的子view
         * @param pointerId
         * @return true就捕获并解析；false不捕获
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (preScale > 1)
                return true;
            return false;
        }

        /**
         * 控制水平方向上的位置
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            if (left < (screenWidth - screenWidth * preScale) / 2)
                left = (int) (screenWidth - screenWidth * preScale) / 2;// 限制mainView可向左移动到的位置
            if (left > (screenWidth * preScale - screenWidth) / 2)
                left = (int) (screenWidth * preScale - screenWidth) / 2;// 限制mainView可向右移动到的位置
            return left;
        }

        public int clampViewPositionVertical(View child, int top, int dy) {

            if (top < (screenHeight - screenHeight * preScale) / 2) {
                top = (int) (screenHeight - screenHeight * preScale) / 2;// 限制mainView可向上移动到的位置
            }
            if (top > (screenHeight * preScale - screenHeight) / 2) {
                top = (int) (screenHeight * preScale - screenHeight) / 2;// 限制mainView可向上移动到的位置
            }
            return top;
        }

    };

    public class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float previousSpan = detector.getPreviousSpan();// 前一次双指间距
            float currentSpan = detector.getCurrentSpan();// 本次双指间距
            if (currentSpan < previousSpan) {
                // 缩小
                // scale = preScale-detector.getScaleFactor()/3;
                scale = preScale - (previousSpan - currentSpan) / 1000;
            } else {
                // 放大
                // scale = preScale+detector.getScaleFactor()/3;
                scale = preScale + (currentSpan - previousSpan) / 1000;
            }
            // 缩放view
            if (scale > 0.5) {
                ViewCompat.setScaleX(INAShrinkFrameLayout.this, scale);// x方向上缩放
                ViewCompat.setScaleY(INAShrinkFrameLayout.this, scale);// y方向上缩放
            }
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            // 一定要返回true才会进入onScale()这个函数
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            preScale = scale;// 记录本次缩放比例
            lastMultiTouchTime = System.currentTimeMillis();// 记录双指缩放后的时间
        }
    }

}