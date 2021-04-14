package com.sunsta.bear.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：此ViewPager解决与父容器ScrollView冲突的问题,无法完美解决.有卡顿 此自定义组件和下拉刷新scrollview配合暂时小完美，有待改善
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2016/8/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 1.0 |   2017/3/15           |   TORightViewPager的滑动布局。
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class INAToRightViewPager extends ViewPager {
    /*fix 常量*/
    private int downRawX, downRawY;
    private int mTouchSlop = 10;
    private static String TAG = "INAToRightViewPager";

    public INAToRightViewPager(Context context) {
        // TODO Auto-generated constructor stub
        super(context);
    }

    public INAToRightViewPager(Context context, AttributeSet attrs) {
        // TODO Auto-generated constructor stub
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //继续分发到--onInterceptTouchEvent
        return super.dispatchTouchEvent(ev);
    }

    /*
     * 拦截该事件，不再分发，交给onInterceptTouchEvent消费。
     * */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downRawX = (int) ev.getRawX();
                downRawY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getRawY();
                int moveX = (int) ev.getRawX();
                /*判断向右滑动,或者判断xiang右滑动，大于先上滑动*/
                if ((moveX - downRawX) > Math.abs(moveY - downRawY)) {
                    Log.i(TAG, "--qydq->右边滑动了");
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

}
