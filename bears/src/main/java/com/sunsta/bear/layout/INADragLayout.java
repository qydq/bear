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

import com.sunsta.bear.R;
import com.sunsta.bear.faster.ScreenUtils;
import com.sunsta.bear.listener.OnDragLayoutListener;

public class INADragLayout extends RelativeLayout {
    private View childView;
    private OnDragLayoutListener onDragLayoutListener;

    public void setOnDragLayoutListener(OnDragLayoutListener onDragLayoutListener) {
        this.onDragLayoutListener = onDragLayoutListener;
    }

    public void setoverScrollDraggerX(boolean overScrollDraggerX) {
        this.overScrollDraggerX = overScrollDraggerX;
    }

    private ViewDragHelper dragHelper;
    private boolean canDragge = false;
    private boolean overScrollDraggerX = false;//recyclerView or some list patch
    private boolean overScrollDraggerY = false;//recyclerView or some list patch
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
            overScrollDraggerX = ta.getBoolean(R.styleable.INADragLayout_overScrollDraggerX, overScrollDraggerX);
            overScrollDraggerY = ta.getBoolean(R.styleable.INADragLayout_overScrollDraggerY, overScrollDraggerY);
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
        if (overScrollDraggerY && overScrollDraggerX) {
            return dragHelper.shouldInterceptTouchEvent(event);
        } else {
            if (overScrollDraggerY) {
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
                            if (Math.abs(moveY - downY) > Math.abs(moveX - downX)) {
                                intercept = true;
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
                        return false;
                    } else {
                        return dragHelper.shouldInterceptTouchEvent(event);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return super.onInterceptTouchEvent(event);
            } else {
                if (overScrollDraggerX) {
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
                                /*判断向右滑动,或者判断xiang右滑动，大于先上滑动*/
                                if ((moveX - downX) > Math.abs(moveY - downY)) {
                                    intercept = true;
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
                            return false;
                        } else {
                            return dragHelper.shouldInterceptTouchEvent(event);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return super.onInterceptTouchEvent(event);
                } else {
                    return dragHelper.shouldInterceptTouchEvent(event);
                }
            }
        }
    }

    private void canDragger(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            canDragge = false;
        } else {
            canDragge = true;
        }
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
            throw new IllegalArgumentException("must have one child");
        }
        childView = getChildAt(0);
    }
}