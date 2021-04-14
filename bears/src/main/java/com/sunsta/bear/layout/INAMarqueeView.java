package com.sunsta.bear.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.sunsta.bear.R;
import com.sunsta.bear.faster.ScreenUtils;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：一个可以轮播的TextView控件。
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2016/8/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 *
 * @author sunst // sunst0069
 * @version 1.0 |   2016/04/28           |   一个可以轮播的TextView控件。
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class INAMarqueeView extends SurfaceView implements SurfaceHolder.Callback {
    public Context mContext;

    private float mTextSize = 100; //字体大小

    private int mTextColor = Color.RED; //字体的颜色

    private int mBackgroundColor = Color.WHITE;//背景色

    private boolean mIsRepeat;//是否重复滚动

    private int mStartPoint;// 开始滚动的位置  0是从最左面开始    1是从最末尾开始

    private int mDirection;//滚动方向 0 向左滚动   1向右滚动

    private int mSpeed;//滚动速度

    private SurfaceHolder holder;

    private TextPaint mTextPaint;

    private MarqueeViewThread mThread;

    private String margueeString;

    private int textWidth = 0, textHeight = 0;

    private int ShadowColor = Color.BLACK;

    public int currentX = 0;// 当前x的位置

    public int sepX = 5;//每一步滚动的距离

    public INAMarqueeView(Context context) {
        this(context, null);
    }

    public INAMarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public INAMarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.INAMarqueeView, defStyleAttr, 0);
        mTextColor = a.getColor(R.styleable.INAMarqueeView_anMarqueeTextColor, Color.RED);
        mTextSize = a.getDimension(R.styleable.INAMarqueeView_anMarqueeTextSize, 48);
        mBackgroundColor = a.getColor(R.styleable.INAMarqueeView_anMarqueeBackground, Color.WHITE);
        mIsRepeat = a.getBoolean(R.styleable.INAMarqueeView_asMarqueeRepeat, false);
        mStartPoint = a.getInt(R.styleable.INAMarqueeView_anMarqueeStartPoint, 0);
        mDirection = a.getInt(R.styleable.INAMarqueeView_anMarqueeDirection, 0);
        mSpeed = a.getInt(R.styleable.INAMarqueeView_anMarqueeSpeed, 20);
        a.recycle();

        holder = this.getHolder();
        holder.addCallback(this);
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    /**
     * 设置显示的文字
     *
     * @param msg 显示的文字。
     */
    public void setText(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            measurementsText(msg);
        }
    }

    protected void measurementsText(String msg) {
        margueeString = msg;
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStrokeWidth(0.5f);
        mTextPaint.setFakeBoldText(true);
        // 设定阴影(柔边, X 轴位移, Y 轴位移, 阴影颜色)
//        mTextPaint.setShadowLayer(5, 3, 3, ShadowColor);
        textWidth = (int) mTextPaint.measureText(margueeString);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        textHeight = (int) fontMetrics.bottom;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        if (mStartPoint == 0)
            currentX = 0;
        else
            currentX = width - getPaddingLeft() - getPaddingRight();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mThread != null)
            mThread.isRun = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mThread != null)
            mThread.isRun = false;
    }

    /**
     * 开始滚动
     */
    public void startScroll() {

        if (mThread != null && mThread.isRun)
            return;
        mThread = new MarqueeViewThread(holder);//创建一个绘图线程
        mThread.start();
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        if (mThread != null) {
            mThread.isRun = false;
            mThread.interrupt();
        }
        mThread = null;
    }

    /**
     * 线程
     */
    class MarqueeViewThread extends Thread {

        private SurfaceHolder holder;

        public boolean isRun;//是否在运行


        public MarqueeViewThread(SurfaceHolder holder) {
            this.holder = holder;
            isRun = true;
        }

        public void onDraw() {
            try {
                synchronized (holder) {
                    if (TextUtils.isEmpty(margueeString)) {
                        Thread.sleep(1000);//睡眠时间为1秒
                        return;
                    }
                    Canvas canvas = holder.lockCanvas();
                    int paddingLeft = getPaddingLeft();
                    int paddingTop = getPaddingTop();
                    int paddingRight = getPaddingRight();
                    int paddingBottom = getPaddingBottom();

                    int contentWidth = getWidth() - paddingLeft - paddingRight;
                    int contentHeight = getHeight() - paddingTop - paddingBottom;

                    int centeYLine = paddingTop + contentHeight / 2;//中心线

                    if (mDirection == 0) {//向左滚动
                        if (currentX <= -textWidth) {
                            if (!mIsRepeat) {//如果是不重复滚动
                                mHandler.sendEmptyMessage(ROLL_OVER);
                            }
                            currentX = contentWidth;
                        } else {
                            currentX -= sepX;
                        }
                    } else {//  向右滚动
                        if (currentX >= contentWidth) {
                            if (!mIsRepeat) {//如果是不重复滚动
                                mHandler.sendEmptyMessage(ROLL_OVER);
                            }
                            currentX = -textWidth;
                        } else {
                            currentX += sepX;
                        }
                    }

                    if (canvas != null)
                        canvas.drawColor(mBackgroundColor);
                    canvas.drawText(margueeString, currentX, centeYLine + ScreenUtils.dip2px(textHeight) / 2, mTextPaint);
                    holder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变。

                    int a = textWidth / margueeString.trim().length();
                    int b = a / sepX;
                    int c = mSpeed / b == 0 ? 1 : mSpeed / b;

                    Thread.sleep(c);//睡眠时间为移动的频率


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            while (isRun) {
                onDraw();
            }

        }

    }

    public static final int ROLL_OVER = 100;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ROLL_OVER:
                    stopScroll();
                    if (mOnMargueeListener != null) {
                        mOnMargueeListener.onRollOver();
                    }
                    break;
            }
        }
    };

    public void reset() {
        int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if (mStartPoint == 0)
            currentX = 0;
        else
            currentX = contentWidth;
    }

    /**
     * 滚动回调
     */
    public interface OnMargueeListener {
        void onRollOver();//滚动完毕
    }

    OnMargueeListener mOnMargueeListener;

    public void setOnMargueeListener(OnMargueeListener mOnMargueeListener) {
        this.mOnMargueeListener = mOnMargueeListener;
    }
}
