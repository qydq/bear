package com.sunsta.bear.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.faster.ScreenUtils;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.listener.OnDragLayoutListener;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：有弹性的ScrollView 实现下拉弹回和上拉弹回
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 *
 * @author sunst // sunst0069
 * @version 2.0 |   2017/12/27           |   关联INAShrinkScrollView，事件冲突的解决方法详见：https://zhuanlan.zhihu.com/p/32686674。
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class INADragLayout extends RelativeLayout {
    private View childView;
    private OnDragLayoutListener onDragLayoutListener;
    private ViewDragHelper dragHelper;
    private boolean canDragge = false;
    private boolean anDragScrollX = false;//recyclerView or some list patch
    private boolean anDragScrollY = false;//or some viewpager patch  use
    private Point dragPoint = new Point();
    private float downX = 0;
    private float downY = 0;

    public INADragLayout(Context context) {
        super(context);
        init(context, null);
    }

    public INADragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public INADragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            @SuppressLint("Recycle") TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.INADragLayout);
            anDragScrollX = ta.getBoolean(R.styleable.INADragLayout_anDragScrollX, anDragScrollX);
            anDragScrollY = ta.getBoolean(R.styleable.INADragLayout_anDragScrollY, anDragScrollY);
        }
        dragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return true;
            }

            @Override
            public int clampViewPositionVertical(@NonNull View view, int top, int dy) {
                int bottomBound = getHeight() - view.getHeight();
                return Math.min(Math.max(top, 0), bottomBound);
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                return Math.min(getWidth() - child.getWidth(), Math.max(left, 0));
            }

            @Override
            public int getViewVerticalDragRange(@NonNull View child) {
                return child.getMeasuredHeight();
            }

            @Override
            public int getViewHorizontalDragRange(@NonNull View child) {
                return child.getMeasuredWidth();
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                //dragHelper.captureChildView(childView, pointerId);
            }

            @Override
            public void onViewReleased(@NonNull View releasedView, float xvel, float yvel) {
                int[] location = new int[2];
                releasedView.getLocationOnScreen(location);
                if (location[0] > -1 && location[0] < (ScreenUtils.getDeviceWidth(getContext()) / 2)) {
                    dragPoint.x = 0;
                } else {
                    dragPoint.x = (ScreenUtils.getDeviceWidth(getContext()) - (childView.getWidth()));
                }
                dragPoint.y = location[1];
                dragHelper.settleCapturedViewAt(dragPoint.x, dragPoint.y);
                invalidate();

            }
        });
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                canDragger(childView, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (onDragLayoutListener != null) {
                    onDragLayoutListener.onDrag(true, event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        if (canDragge) {
            dragHelper.processTouchEvent(event);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (anDragScrollY && anDragScrollX) {
            return dragHelper.shouldInterceptTouchEvent(event);
        } else {
            if (anDragScrollY) {
                return intercept(interceptEvent(event, anDragScrollY), event);
            } else {
                if (anDragScrollX) {
                    return intercept(interceptEvent(event, anDragScrollY), event);
                } else {
                    return dragHelper.shouldInterceptTouchEvent(event);
                }
            }
        }
    }

    private boolean intercept(int interceptCode, MotionEvent event) {
        if (interceptCode == 1) {
            return false;
        } else if (interceptCode == 2) {
            return dragHelper.shouldInterceptTouchEvent(event);
        } else {
            return super.onInterceptTouchEvent(event);
        }
    }

    private int interceptEvent(MotionEvent event, boolean anDragScrollY) {
        try {
            int action = event.getAction();
            boolean intercept = false;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    downX = (int) event.getRawX();
                    downY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int) event.getRawY();
                    int moveX = (int) event.getRawX();
                    if (anDragScrollY) {
                        if (Math.abs(moveY - downY) > Math.abs(moveX - downX)) {
                            intercept = true;
                        }
                    } else {
                        if ((moveX - downX) > Math.abs(moveY - downY)) {
                            intercept = true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    intercept = true;
                    break;
                default:
                    break;
            }
            if (intercept) {
                return 1;
            } else {
                return 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void canDragger(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        canDragge = !(event.getX() < x) && !(event.getX() > (x + view.getWidth())) && !(event.getY() < y) && !(event.getY() > (y + view.getHeight()));
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            if (onDragLayoutListener != null) {
                onDragLayoutListener.onDrag(false, dragPoint.x, dragPoint.y);
            }
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        dragPoint.x = childView.getLeft();
        dragPoint.y = childView.getTop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() < 1) {
            throw new IllegalArgumentException(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, "must have one child"));
        }
        childView = getChildAt(0);
    }


    public void setOnDragLayoutListener(OnDragLayoutListener onDragLayoutListener) {
        this.onDragLayoutListener = onDragLayoutListener;
    }

    public void setDragScrollX(boolean anDragScrollX) {
        this.anDragScrollX = anDragScrollX;
    }

    public void setDragScrollY(boolean anDragScrollY) {
        this.anDragScrollY = anDragScrollY;
    }
}