package com.sunsta.bear.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import com.sunsta.bear.entity.Barrage;
import com.sunsta.bear.faster.DataService;
import com.sunsta.bear.faster.LaLog;

import java.lang.ref.WeakReference;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

/**
 * Created by sunst 【晴雨.qy】 on 2017/9/8.
 */
public final class BarrageItem {
    private static final String TAG = "BarrageItem";

    private Barrage mData;
    private BarrageRow mRow;
    private WeakReference<View> mContentView;
    private ValueAnimator mAnimator = new ValueAnimator();
    private AnimatorListener mAnimatorListener = new AnimatorListener();
    private TreeObserver observer = new TreeObserver(this);

    public interface BarrageItemListener {
        void onAnimationCancel(BarrageItem item);

        void onAnimationEnd(BarrageItem item);

        void onAnimationRepeat(BarrageItem item);

        void onAnimationStart(BarrageItem item);

        void onAnimationPause(BarrageItem item);

        void onAnimationResume(BarrageItem item);

        void onAnimationUpdate(BarrageItem item);
    }

    private BarrageItemListener mListener;

    public void setListener(BarrageItemListener listener) {
        mListener = listener;
    }

    protected int mDistance = 0;
    protected int mSpeed = 0;
    protected int mGravity = 0;
    protected int mHoverTimer = 0;
    protected long mHoverSpeed = 0;
    protected boolean mHoverRecoil = false;

    public int getHoverTimer() {
        return mHoverTimer;
    }

    public void setHoverTimer(int mHoverTimer) {
        this.mHoverTimer = mHoverTimer;
    }

    public int getDistance() {
        return mDistance;
    }

    public void setDistance(int distance) {
        this.mDistance = distance;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
    }

    public int getGravity() {
        return mGravity;
    }

    public void setData(Barrage data) {
        mData = data;
    }

    public Barrage getData() {
        return mData;
    }

    public void setRow(BarrageRow row) {
        mRow = row;
    }

    @Nullable
    public BarrageRow getRow() {
        return mRow;
    }

    public void setContentView(View view) {
        mContentView = new WeakReference<View>(view);
    }

    @Nullable
    public View getContentView() {
        if (mContentView == null) {
            return null;
        }
        return mContentView.get();
    }

    public BarrageItem() {
        mAnimator.addUpdateListener(mAnimatorListener);
        mAnimator.addListener(mAnimatorListener);
        mAnimator.setInterpolator(new LinearInterpolator());
    }

    public void clear() {
        mContentView = null;
        mAnimator.cancel();
    }

    public void start() {
        if (mContentView == null || mContentView.get() == null) {
            LaLog.e(TAG, "fetal error. content view is null");
            return;
        }
        mContentView.get().getViewTreeObserver().addOnGlobalLayoutListener(observer);
    }

    private void realStartAnim() {
        if (mContentView == null || mContentView.get() == null) {
            LaLog.e(TAG, "fetal error. content view is null");
            return;
        }
        mContentView.get().setFocusable(false);
        mContentView.get().setEnabled(false);
        mContentView.get().setAccessibilityDelegate(null);

        mContentView.get().setX(0);
        if (mRow.getRandomVerticalPos()) {
            //设置随机出现垂直位置RichTextView. 对于漫天飞宇这种状态
            mContentView.get().setY(getRandomTopByGravity(mGravity));
        } else {
            mContentView.get().setY(getTopByGravity(mGravity));
        }
        /*
         * 开启动画
         * */
//        LaLog.d(TAG, "imageView width:" + mContentView.get().getWidth() +
//                "| minWidth:" + mContentView.get().getMinimumWidth() +
//                "| measureWidth:" + mContentView.get().getMeasuredWidth() +
//                "| distance:" + mDistance +
//                "| hoverTime:" + mRow.getHoverTime());
        mHoverTimer = mData.getHoverTime();
        if (mHoverTimer <= 0) {
            mHoverTimer = mRow.getHoverTime();
        }
        //优先使用OjectAnimator
        showObjectAnimator();
//        showOldInAnimation(mContentView.get());
    }

    /*
     * 这里是属性动画插值计算
     * */
    private void showObjectAnimator() {
        mAnimator.setTarget(mContentView.get());
        // mAnimator.setPropertyName("translationX");
        mAnimator.setFloatValues(mDistance, -mContentView.get().getWidth());
        mAnimator.setDuration(getDurationBySpeed(mSpeed));
        if (mData.isAccelerate()) {
            mAnimator.setInterpolator(new AccelerateInterpolator());
        }
        mAnimator.setRepeatCount(mRow.getRepeatCount());
        mAnimator.start();
    }

    /*
     * 【2020保留参考】原始进入动画使用，本设置为从从右便屏幕外，过度到左边屏幕内
     * */
    private void showOldInAnimation(@NonNull View childView) {
        //创建动画，参数表示他的子动画是否共用一个插值器
        AnimationSet animationSet = new AnimationSet(true);
        //添加动画
//        animationSet.addAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.aim_barrage_in));
        animationSet.addAnimation(new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, 0));
        //设置插值器
//        animationSet.setInterpolator(new AccelerateInterpolator());
        animationSet.setInterpolator(new LinearInterpolator());
        //设置动画持续时长
        animationSet.setDuration(getDurationBySpeed(mSpeed));
        //设置动画结束之后是否保持动画的目标状态
        animationSet.setFillAfter(false);
        //设置动画结束之后是否保持动画开始时的状态
        animationSet.setFillBefore(true);
        //设置重复模式
//        animationSet.setRepeatMode(AnimationSet.REVERSE);
        //设置重复次数
//        animationSet.setRepeatCount(AnimationSet.INFINITE);
        //取消动画
        animationSet.cancel();
        //释放资源
        animationSet.reset();
        //开始动画
        childView.startAnimation(animationSet);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showOldOutAnimation(childView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /*
     * 【2020保留参考】原始出去动画使用，本设置为从从左边屏幕内，过度到左便屏幕外
     * */
    private void showOldOutAnimation(@NonNull View childView) {
        //创建动画，参数表示他的子动画是否共用一个插值器
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, -1f,
                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, 0));
//        animationSet.addAnimation(new TranslateAnimation(
//                Animation.RELATIVE_TO_PARENT, mDistance, Animation.RELATIVE_TO_PARENT, -mContentView.get().getWidth(),
//                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, 0));
        //设置插值器
        animationSet.setInterpolator(new LinearInterpolator());
//        animator.setInterpolator(new BounceInterpolator());//实现反复移动的效果
        animationSet.setDuration(getDurationBySpeed(mSpeed));
        //开始动画
        childView.startAnimation(animationSet);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                childView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 悬停边界的时间和动画控制
     */
    private void handHoverTime(View childView, ValueAnimator animator) {
        animator.setFloatValues(0f);
        //first use mData
        mHoverSpeed = mData.getBarrageHoverSpeed();
        if (mHoverSpeed <= 0) {
            mHoverSpeed = mRow.getHoverSpeed();
        }
        if (mHoverSpeed == 0) {
            mHoverSpeed = getDurationBySpeed(mSpeed);
        }
        //first use mRow
        mHoverRecoil = mRow.isHoverRecoil();
        if (!mHoverRecoil) {
            mHoverRecoil = mData.isHoverRecoil();
        }
        long totalTime = mHoverTimer + mHoverSpeed;
        animator.setDuration(totalTime);
        childView.setX(0f);
        // 左移动动画这里不可用，其它地方可用
//        ObjectAnimator animator = ObjectAnimator.ofFloat(contentView, "translationX", 0, -500f);
//        animator.setDuration(5000);
//        animator.start();

//        ValueAnimator targeAnimator = new ValueAnimator();
//        targeAnimator.setFloatValues(0f, 500f);
//        targeAnimator.setFloatValues(mDistance, -500f);
//        targeAnimator.setTarget(contentView);
//        targeAnimator.setFloatValues(0, -600f);
//        targeAnimator.setDuration(getDurationBySpeed(mSpeed));
//        targeAnimator.start();

        new Handler().postDelayed(() -> {
            Activity activity = (Activity) childView.getContext();
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    ObjectAnimator animator1;
                    if (mHoverRecoil) {
                        animator1 = ObjectAnimator.ofFloat(childView, "translationX", 0.0f, 20, 0f, -childView.getWidth());
                    } else {
                        animator1 = ObjectAnimator.ofFloat(childView, "translationX", 0, -childView.getWidth());
                    }
                    animator1.setDuration(mHoverSpeed);
                    animator1.start();
                });
            }
        }, mHoverTimer);
    }


    private int getRandomTopByGravity(int gravity) {
//        int randonTop = (int) (Math.random() * mRow.getHeight());//每一行的高度
        int resultPatchHeight = mRow.getHeight();
        int contentViewXmlHeight = mContentView.get().getHeight();//布局中的高度

        if (contentViewXmlHeight > resultPatchHeight) {
            resultPatchHeight = contentViewXmlHeight;//布局中的高度
        }
//        resultPatchHeight = (int) (Math.random() * resultPatchHeight);//这里重新计算距离的高度
        int MIN = mRow.getMinBarrageTopY();
        int MAX = mRow.getMaxBarrageBottomY();
        switch (gravity) {
            case Gravity.TOP:
                MIN = 0;
                MAX = ((MAX - MIN) / 3) - resultPatchHeight;
                break;
            case Gravity.CENTER:
                MIN = (MAX - MIN) / 3;
                MAX = MAX - MIN - resultPatchHeight - 3;
                break;
            case Gravity.BOTTOM:
                MIN = (((MAX - MIN) / 3) * 2) - 5;
                MAX = MAX - MIN + resultPatchHeight + 20;
                break;
            default:
                MIN = 0;
                MAX = MAX - MIN + resultPatchHeight + 20;
                break;
        }
        return DataService.getInstance().randomNextInt(MIN, MAX);
    }

    /**
     * 坐标 相对于当前行的Top或Bottom 定位
     * top： 从上往下排列，bottom：从下往上显示，center2bottom：从中间往下显示
     * default如果没有设置，则忽略代码设置，和xml中设置带来的误差
     **/
    private int getTopByGravity(int gravity) {
        int resultPatchHeight = mRow.getHeight();
        int contentViewXmlHeight = mContentView.get().getHeight();//布局中的高度
        int MIN = mRow.getMinBarrageTopY();
        int MAX = mRow.getMaxBarrageBottomY();
        switch (gravity) {

            case Gravity.BOTTOM:
                //当代码设置的弹幕高度小于xml中的高度，则重新设置高度，建议代码设置的高度和xml中设置的弹幕高度相等
                if (contentViewXmlHeight > resultPatchHeight) {
                    resultPatchHeight = contentViewXmlHeight;
                    mRow.setHeight(resultPatchHeight);
                    return MAX - MIN - resultPatchHeight;
                } else {
                    return MAX - MIN - mRow.getBottom();
                }
            case Gravity.CENTER:
                //当代码设置的弹幕高度小于xml中的高度，则重新设置高度，建议代码设置的高度和xml中设置的弹幕高度相等
                if (contentViewXmlHeight > resultPatchHeight) {
                    resultPatchHeight = contentViewXmlHeight;
                    mRow.setHeight(resultPatchHeight);
                }
                return mRow.getTop() + ((MAX - MIN) / 2) - (resultPatchHeight / 2);
            case Gravity.TOP:
            default:
                //当代码设置的弹幕高度小于xml中的高度，则重新设置高度，建议代码设置的高度和xml中设置的弹幕高度相等
                if (contentViewXmlHeight > resultPatchHeight) {
                    resultPatchHeight = contentViewXmlHeight;
                    mRow.setHeight(resultPatchHeight);
                }
                return mRow.getTop();
        }
    }

    /**
     * 不同宽度的物体，划过同一个窗口，规定了总时间，以此获取对应的速度
     **/
    private long getDurationBySpeed(int speed) {
        return (long) ((mDistance + mContentView.get().getWidth()) / (mDistance * 1.0) * speed);
    }

    public void pause() {
        mAnimator.pause();
    }

    public void resume() {
        mAnimator.resume();
    }

    public void cancel() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    public boolean isStarted() {
        if (mAnimator == null) {
            return false;
        } else {
            return mAnimator.isStarted();
        }
    }

    public boolean isPaused() {
        if (mAnimator == null) {
            return false;
        } else {
            return mAnimator.isPaused();
        }
    }

    public void onLayoutFinish() {
        realStartAnim();
    }


    private static class TreeObserver implements ViewTreeObserver.OnGlobalLayoutListener {
        private WeakReference<BarrageItem> mItem;

        public TreeObserver(BarrageItem view) {
            mItem = new WeakReference<>(view);
        }

        @Override
        public void onGlobalLayout() {
            if (mItem.get() != null) {
                // only trigger once
                View contentView = mItem.get().getContentView();
                if (contentView != null && contentView.getViewTreeObserver() != null) {
                    contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mItem.get().onLayoutFinish();
                }
            }
        }
    }

    private class AnimatorListener extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            View contentView = BarrageItem.this.getContentView();
            if (contentView != null) {
                float xAnimationValue = (Float) animation.getAnimatedValue();
                contentView.setX(xAnimationValue);
                if (xAnimationValue < 0f && mHoverTimer > 0) {
                    handHoverTime(contentView, animation);
                } else {
                    if (mListener != null) {
                        mListener.onAnimationUpdate(BarrageItem.this);
                    }
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            if (mListener != null) {
                mListener.onAnimationCancel(BarrageItem.this);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mListener != null) {
                mListener.onAnimationEnd(BarrageItem.this);
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            if (mListener != null) {
                mListener.onAnimationRepeat(BarrageItem.this);
            }
        }

        @Override
        public void onAnimationStart(Animator animation) {
            if (mListener != null) {
                mListener.onAnimationStart(BarrageItem.this);
            }
        }

        @Override
        public void onAnimationPause(Animator animation) {
            if (mListener != null) {
                mListener.onAnimationPause(BarrageItem.this);
            }
        }

        @Override
        public void onAnimationResume(Animator animation) {
            if (mListener != null) {
                mListener.onAnimationResume(BarrageItem.this);
            }
        }
    }
}