package com.sunsta.bear.layout;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

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
 * @author sunst // sunst0069
 * @version 1.0 |   2016/12/27           |   有弹性的ScrollView 实现下拉弹回和上拉弹回。
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class INAShrinkScrollView extends ScrollView {
    // 移动因子, 是一个百分比, 比如手指移动了100px, 那么View就只移动50px，目的是达到一个延迟的效果
    private static final float MOVE_FACTOR = 0.5f;

    // 松开手指后, 界面回到正常位置需要的动画时间
    private static final int ANIM_TIME = 280;
    // ScrollView的子View， 也是ScrollView的唯一一个子View
    private View contentView;
    // 手指按下时的Y值, 用于在移动时计算移动距离，如果按下时不能上拉和下拉， 会在手指移动时更新为当前手指的Y值
    private float startY;
    // 用于记录正常的布局位置
    private Rect originalRect = new Rect();
    // 手指按下时记录是否可以继续下拉
    private boolean canPullDown = false;
    // 手指按下时记录是否可以继续上拉
    private boolean canPullUp = false;
    // 在手指滑动的过程中记录是否移动了布局
    private boolean isMoved = false;
    private int downX, downY;

    public INAShrinkScrollView(Context context) {
        super(context);
        setFadingEdgeLength(0);
    }

    public INAShrinkScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFadingEdgeLength(0);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            contentView = getChildAt(0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (contentView == null)
            return;
        // ScrollView中的唯一子控件的位置信息, 这个位置信息在整个控件的生命周期中保持不变
        originalRect.set(contentView.getLeft(), contentView.getTop(), contentView.getRight(), contentView.getBottom());
    }

    /**
     * 在触摸事件中, 处理上拉和下拉的逻辑
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            if (contentView == null) {
                return super.dispatchTouchEvent(ev);
            }
            // 手指是否移动到了当前ScrollView控件之外
            boolean isTouchOutOfScrollView = ev.getY() >= this.getHeight() || ev.getY() <= 0;
            if (isTouchOutOfScrollView) { // 如果移动到了当前ScrollView控件之外
                if (isMoved) // 如果当前contentView已经被移动, 首先把布局移到原位置, 然后消费点这个事件
                    boundBack();
                return true;
            }
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // 判断是否可以上拉和下拉
                    canPullDown = isCanPullDown();
                    canPullUp = isCanPullUp();
                    // 记录按下时的Y值
                    startY = ev.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    boundBack();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 在移动的过程中， 既没有滚动到可以上拉的程度， 也没有滚动到可以下拉的程度
                    if (!canPullDown && !canPullUp) {
                        startY = ev.getY();
                        canPullDown = isCanPullDown();
                        canPullUp = isCanPullUp();
                        break;
                    }
                    // 计算手指移动的距离
                    float nowY = ev.getY();
                    int deltaY = (int) (nowY - startY);
                    // 是否应该移动布局
                    boolean shouldMove = (canPullDown && deltaY > 0) // 可以下拉， 并且手指向下移动
                            || (canPullUp && deltaY < 0) // 可以上拉， 并且手指向上移动
                            || (canPullUp && canPullDown); // 既可以上拉也可以下拉（这种情况出现在ScrollView包裹的控件比ScrollView还小）
                    if (shouldMove) {
                        // 计算偏移量
                        int offset = (int) (deltaY * MOVE_FACTOR);
                        // 随着手指的移动而移动布局
                        contentView.layout(originalRect.left, originalRect.top + offset, originalRect.right, originalRect.bottom + offset);
                        isMoved = true; // 记录移动了布局
                    }
                    break;
                default:
                    break;
            }
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /*
     * fix ：2018年1月2日15:31:11
     * 修复scrollview和recyclerview冲突的问题
     * */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    downX = (int) ev.getRawX();
                    downY = (int) ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int) ev.getRawY();
                    int moveX = (int) ev.getRawX();
                    /*判断向上滑动,或者向下滑动。判断y > x ；x-y分别为水平，垂直滑动的距离*/
                    if (Math.abs(moveY - downY) > Math.abs(moveX - downX)) {
                        return true;
                    }
                    /*判断向右滑动,或者判断xiang右滑动，大于先上滑动*/
                    if ((moveX - downX) > Math.abs(moveY - downY)) {
                    }
                    break;
                default:
                    break;
            }
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 将内容布局移动到原位置 可以在UP事件中调用, 也可以在其他需要的地方调用, 如手指移动到当前ScrollView外时
     */
    private void boundBack() {
        if (!isMoved) return;
        TranslateAnimation anim = new TranslateAnimation(0, 0, contentView.getTop(), originalRect.top);
        anim.setDuration(ANIM_TIME);
        contentView.startAnimation(anim);
        contentView.layout(originalRect.left, originalRect.top, originalRect.right, originalRect.bottom);
        canPullDown = false;
        canPullUp = false;
        isMoved = false;
    }

    /**
     * 判断是否滚动到顶部
     */
    private boolean isCanPullDown() {
        return getScrollY() == 0 || contentView.getHeight() < getHeight() + getScrollY();
    }

    /**
     * 解决ListView和ScrollView嵌套冲突的方法。
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams( );
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * 判断是否滚动到底部
     */
    private boolean isCanPullUp() {
        return contentView.getHeight() <= getHeight() + getScrollY();
    }

    @Override
    public void fling(int velocityY) {
        //这里设置滑动的速度。
        super.fling(velocityY / 2);
    }
}