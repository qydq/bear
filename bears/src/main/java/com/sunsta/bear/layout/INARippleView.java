package com.sunsta.bear.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.sunsta.bear.R;
import com.sunsta.bear.faster.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 涟漪效果 , Android自定义View控件实现多种水波纹涟漪扩散效果
 * author: sunst
 * date:2018/06/09
 */
public class INARippleView extends FrameLayout {
    private Paint mPaint;
    private float mWidth;
    private float mHeight;
    private List<Circle> mRipples; // 声波的圆圈集合
    private int mSpeed; // 圆圈扩散的速度
    private int mDensity; // 圆圈之间的密度
    private int mColor; // 圆圈的颜色
    private boolean mIsFill; // 圆圈是否为填充模式
    private boolean mIsAlpha;// 圆圈是否为渐变模式
    private boolean mInCircle;// 圆圈是否为内切圆 , 默认true
    private boolean mSpreadMode;//圆圈水波纹模式，false为水波纹涟漪；true表示Spread效果，水波纹使用动画来控制，只有改模式下可以调用startAniatiom方法
    @DrawableRes
    private int mCircleResource;// 圆圈资源文件，默认image_small_circle，也可参考R.drawable.round
    private static final int ANIM_DELAY = 1050;//如不支持动画下一个运行时候的延迟
    private static final int MSG_START = 0;
    private static final int MSG_FORCE_STOP = 1;

    private Context mContext;
    private View mLayer1;
    private View mLayer2;

    private AnimationSet mAnimLayer1;
    private AnimationSet mAnimLayer2;

    private boolean mIsRunning;
    private PortraitAnimationHandler mHandler;
    private PortraitAnimationRunnable mRunnable;

    public INARippleView(Context context) {
        this(context, null);
    }

    public INARippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public INARippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        @SuppressLint("CustomViewStyleable") TypedArray tya = context.obtainStyledAttributes(attrs, R.styleable.INARippleView);
        mSpreadMode = tya.getBoolean(R.styleable.INARippleView_ripple_spread, false);
        if (!mSpreadMode) {
            mColor = tya.getColor(R.styleable.INARippleView_ripple_color, Color.BLUE);
            mSpeed = tya.getInt(R.styleable.INARippleView_ripple_speed, 1);
            mDensity = tya.getInt(R.styleable.INARippleView_ripple_density, 10);
            mIsFill = tya.getBoolean(R.styleable.INARippleView_ripple_isFill, false);
            mIsAlpha = tya.getBoolean(R.styleable.INARippleView_ripple_isAlpha, false);
            mInCircle = tya.getBoolean(R.styleable.INARippleView_ripple_inCircle, true);
            tya.recycle();
            initRipple();
        } else {
            mCircleResource = tya.getResourceId(R.styleable.INARippleView_ripple_src, R.drawable.image_small_circle);
            initSpreadAnimation();
        }
    }

    private void initRipple() {
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(ScreenUtils.dip2px(1));
        if (mIsFill) {
            mPaint.setStyle(Paint.Style.FILL);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
        }
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mRipples = new ArrayList<>();// 添加第一个圆圈
        Circle c = new Circle(0, 255);
        mRipples.add(c);

        mDensity = ScreenUtils.dip2px(mDensity);
        setBackgroundColor(Color.TRANSPARENT);// 设置View的圆为半透明
    }

    private void initSpreadAnimation() {
        mHandler = new PortraitAnimationHandler(mContext.getMainLooper());
        mRunnable = new PortraitAnimationRunnable();
        setVisibility(GONE);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mLayer1 = new ImageView(mContext);
        mLayer1.setBackgroundResource(mCircleResource);
        mLayer2 = new ImageView(mContext);
        mLayer2.setBackgroundResource(mCircleResource);
        addView(mLayer1, layoutParams);
        addView(mLayer2, layoutParams);
        initAnimation1();
        initAnimation2();
    }

    private void initAnimation1() {
        mAnimLayer1 = new AnimationSet(true);
        AlphaAnimation alpha1 = new AlphaAnimation(0, 1);
        alpha1.setDuration(200);
        alpha1.setInterpolator(new LinearInterpolator());

        AlphaAnimation alpha2 = new AlphaAnimation(1, 0.2f);
        alpha2.setDuration(700);
        alpha2.setStartOffset(200);
        alpha2.setInterpolator(new AccelerateDecelerateInterpolator());

        AlphaAnimation alpha3 = new AlphaAnimation(0.2f, 0);
        alpha3.setDuration(100);
        alpha3.setStartOffset(900);
        alpha3.setInterpolator(new LinearInterpolator());

        mAnimLayer1.setDuration(1000);
        mAnimLayer1.setFillAfter(true);
        mAnimLayer1.addAnimation(alpha1);
        mAnimLayer1.addAnimation(alpha2);
        mAnimLayer1.addAnimation(alpha3);
    }

    private void initAnimation2() {
        mAnimLayer2 = new AnimationSet(true);
        ScaleAnimation scale = new ScaleAnimation(1, 1.2f, 1, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(1000);

        AlphaAnimation alpha1 = new AlphaAnimation(0, 1);
        alpha1.setDuration(125);

        AlphaAnimation alpha2 = new AlphaAnimation(1, 0);
        alpha2.setDuration(875);
        alpha2.setStartOffset(125);

        mAnimLayer2.setFillAfter(true);
        mAnimLayer2.addAnimation(scale);
        mAnimLayer2.addAnimation(alpha1);
        mAnimLayer2.addAnimation(alpha2);
    }

    /**
     * 注意：spreadMode=true模式下动态控制动画
     */
    public void startAnimation() {
        if (mSpreadMode) {
            Message message = new Message();
            message.what = MSG_START;
            mHandler.sendMessage(message);
        }
    }

    private class PortraitAnimationHandler extends Handler {
        private PortraitAnimationHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_START) {
                if (!mIsRunning) {
                    mIsRunning = true;
                    mHandler.post(mRunnable);
                    mHandler.postDelayed(this::stop, ANIM_DELAY);
                }
            } else if (msg.what == MSG_FORCE_STOP) {
                forceStop();
            }
        }

        private void stop() {
            mIsRunning = false;
            mLayer1.clearAnimation();
            mLayer2.clearAnimation();
            setVisibility(GONE);
        }

        private void forceStop() {
            stop();
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private class PortraitAnimationRunnable implements Runnable {
        @Override
        public void run() {
            setVisibility(VISIBLE);
            mLayer1.startAnimation(mAnimLayer1);
            mLayer2.startAnimation(mAnimLayer2);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mInCircle) {
// 内切正方形
            drawInCircle(canvas);
        } else {
// 外切正方形
            drawOutCircle(canvas);
        }
    }

    /**
     * 圆到宽度
     */
    private void drawInCircle(Canvas canvas) {
        canvas.save();
// 处理每个圆的宽度和透明度
        for (int i = 0; i < mRipples.size(); i++) {
            Circle c = mRipples.get(i);
            mPaint.setAlpha(c.alpha);// （透明）0~255（不透明）
            canvas.drawCircle(mWidth / 2, mHeight / 2, c.width - mPaint.getStrokeWidth(), mPaint);
// 当圆超出View的宽度后删除
            if (c.width > mWidth / 2) {
                mRipples.remove(i);
            } else {
// 计算不透明的数值，这里有个小知识，就是如果不加上double的话，255除以一个任意比它大的数都将是0
                if (mIsAlpha) {
                    double alpha = 255 - c.width * (255 / ((double) mWidth / 2));
                    c.alpha = (int) alpha;
                }
// 修改这个值控制速度
                c.width += mSpeed;
            }
        }
// 里面添加圆
        if (mRipples.size() > 0) {
// 控制第二个圆出来的间距
            if (mRipples.get(mRipples.size() - 1).width > ScreenUtils.dip2px(mDensity)) {
                mRipples.add(new Circle(0, 255));
            }
        }
        invalidate();
        canvas.restore();
    }

    /**
     * 圆到对角线
     */
    private void drawOutCircle(Canvas canvas) {
        canvas.save();
        int sqrtNumber = (int) (Math.sqrt(mWidth * mWidth + mHeight * mHeight) / 2);// 使用勾股定律求得一个外切正方形中心点离角的距离
// 变大
        for (int i = 0; i < mRipples.size(); i++) {
// 启动圆圈
            Circle c = mRipples.get(i);
            mPaint.setAlpha(c.alpha);// （透明）0~255（不透明）
            canvas.drawCircle(mWidth / 2, mHeight / 2, c.width - mPaint.getStrokeWidth(), mPaint);
// 当圆超出对角线后删掉
            if (c.width > sqrtNumber) {
                mRipples.remove(i);
            } else {
                double degree = 255 - c.width * (255 / (double) sqrtNumber);// 计算不透明的度数
                c.alpha = (int) degree;
                c.width += 1;
            }
        }
// 里面添加圆
        if (mRipples.size() > 0) {
// 控制第二个圆出来的间距
            if (mRipples.get(mRipples.size() - 1).width == 50) {
                mRipples.add(new Circle(0, 255));
            }
        }
        invalidate();
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int myWidthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int myWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myHeightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int myHeightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (myWidthSpecMode == MeasureSpec.EXACTLY) {
            mWidth = myWidthSpecSize;
        } else {
            mWidth = ScreenUtils.dip2px(120);
        }
        if (myHeightSpecMode == MeasureSpec.EXACTLY) {
            mHeight = myHeightSpecSize;
        } else {
            mHeight = ScreenUtils.dip2px(120);
        }
        setMeasuredDimension((int) mWidth, (int) mHeight);// 设置该view的宽高
    }

    static class Circle {
        Circle(int width, int alpha) {
            this.width = width;
            this.alpha = alpha;
        }

        int width;
        int alpha;
    }
}