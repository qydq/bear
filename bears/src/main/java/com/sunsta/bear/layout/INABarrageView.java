package com.sunsta.bear.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.callback.OnBarrageIdleListener;
import com.sunsta.bear.entity.Barrage;
import com.sunsta.bear.faster.Convert;
import com.sunsta.bear.faster.DataService;
import com.sunsta.bear.faster.LAUi;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.SPUtils;
import com.sunsta.bear.faster.ScreenUtils;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.model.ReplySSLMode;
import com.sunsta.bear.model.adapter.BarrageDataAdapter;
import com.sunsta.bear.model.entity.ResponseResultMode;
import com.sunsta.bear.presenter.BaseInternetApi;
import com.sunsta.bear.presenter.net.InternetClient;
import com.sunsta.bear.presenter.net.InternetException;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Consumer;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：1. 弹幕(自然布局|平均布局)
 * * 2. 霸屏弹幕动画
 * * 3. 弹幕循环（需要外部提供用于循环的数组，因为内部不保存已结束的弹幕）
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2017/8/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 *
 * @author sunst // sunst0069
 * @version 4.0 |   2020/08/31          |   1.最新版本移除不再提供出支持的AbsBarrageDataAdapter作为继承关系，但提供了Barrage实体2.增强了BarrageView的对外过站能力
 * @link 知乎主页：href="https://zhihu.com/people/qydq
 * <p> ---Revision History:  ->  : |version|date|updateinfo| ---------
 */
public final class INABarrageView extends FrameLayout {
    private static final String TAG = "INABarrageView";
    private List<BarrageRow> mRows = new ArrayList<>(20);
    private Queue<Barrage> mPendingQueue = new ArrayDeque<>(100);
    private TreeObserver observer = new TreeObserver(this);
    private BarrageDataAdapter mAdapter;
    private RecycleBinHeap mRecycleBin = new RecycleBinHeap();

    private boolean mIsLoopingMode;
    private static final long LoopInterval = 100;
    private Queue<Barrage> mLoopQueue = new ArrayDeque<>();
    private static long[] mHits;
    private static final long MAX_IDLE_TIME = 1 * 60 * 1000;   // 空闲时间要比一条弹幕的动画时间长！
    private boolean mIsIdleTimerStarted = false;
    private CountDownTimer mIdleCountDownTimer = new CountDownTimer(MAX_IDLE_TIME, MAX_IDLE_TIME) {
        @Override
        public void onTick(long l) {
        }

        @Override
        public void onFinish() {
            // @bugfix reset boolean when finish
            mIsIdleTimerStarted = false;
            mIsLoopingMode = true;
            if (mListener != null) {
                mListener.onIdle(MAX_IDLE_TIME, INABarrageView.this);
            }
            startLoop();
        }
    };

    private void startLoop() {
        onRowIdle(null);
    }

    private RowListener mRowListener = new RowListener(this);

    private OnBarrageIdleListener mListener;

    public void setIdleListener(OnBarrageIdleListener listener) {
        mListener = listener;
    }

    public static final int NORMAL = 0;
    public static final int AVERAGE = 1;
    private int mBarrageMode = NORMAL;

    private boolean mIsStarted;
    private boolean mIsPrepared;
    private boolean mIsPaused;
    private boolean mIsReleasing;

    private int mRowNum = 1;

    private int mRepeatCount = 0;
    private int mRowGap;
    private int mRowHeight;
    private int xmlRowHeight;
    private int xmlItemGap;//间距，每一条弹幕
    private int xmlRowGap;//行距，每一条弹幕

    private int mRowSpeed;

    private int mItemGap;
    private int mItemGravity;
    private int mHoverTime = 0;//悬停时间

    private long mHoverSpeed = 0;//悬停以后的速度

    private boolean barrageFly = true;//是否在屏幕上随机显示弹幕
    private boolean keepSequence = false;//是否保持弹幕弹出的先后顺序，只有当fly=true有效
    private boolean barrageAuto = false;//自动模式
    private boolean hoverRecoil = false;


    /**
     * @param mode 设置弹幕的布局方式 正常(default)/平均
     **/
    public void setMode(int mode) {
        mBarrageMode = mode;
    }

    public int getMode() {
        return mBarrageMode;
    }

    public void setFly(boolean barrageFly) {
        this.barrageFly = barrageFly;
        createRowsIfNotExist();
    }

    public void setKeepSequence(boolean keepSequence) {
        this.keepSequence = keepSequence;
        createRowsIfNotExist();
    }

    public void setHoverRecoil(boolean hoverRecoil) {
        this.hoverRecoil = hoverRecoil;
        createRowsIfNotExist();
    }

    public void setAdapter(BarrageDataAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setBarrageView(this);
    }

    /**
     * 注意：BarrageView中不在支持对外暴露的BarrageDataAdapter，在使用时需要先调用本方法获取adpater，
     * 这么做的目的是进一步封装标准，只适用一个场景，同时也能减少代码，即本类不能被继承使用
     *
     * @param activity obtainBarrageAdapter所需上下文
     */
    public BarrageDataAdapter obtainBarrageAdapter(Activity activity) {
        return obtainBarrageAdapter(activity, 0);
    }

    /**
     * 如果BarrageView的布局内容需要扩展，建议试着调用本方法
     *
     * @param barrageLayout barrageLayout为自定义的弹幕布局
     */
    public BarrageDataAdapter obtainBarrageAdapter(Activity activity, @LayoutRes int barrageLayout) {
        if (mAdapter == null) {
            if (barrageLayout == 0) {
                mAdapter = new BarrageDataAdapter();
            } else {
                mAdapter = new BarrageDataAdapter(barrageLayout);
            }
        }
        setAdapter(mAdapter);
        initRowsIfNotExist(activity);
        return mAdapter;
    }

    /**
     * 可选默认的显示效果
     */
    public BarrageDataAdapter obtainBarrageAdapter() {
        return obtainBarrageAdapter(0);
    }

    /**
     * 可选默认的显示效果，自定义的布局
     */
    public BarrageDataAdapter obtainBarrageAdapter(@LayoutRes int barrageLayout) {
        return obtainBarrageAdapter(null, barrageLayout);
    }


    public void setLoopQueue(List list) {
        mLoopQueue = new ArrayDeque<>(list);
    }

    /**
     * @param speed 划完一行需要的时间(ms)，行宽为弹幕视图宽度
     **/
    public void setRowSpeed(int speed) {
        this.mRowSpeed = speed;
        createRowsIfNotExist();
    }

    public void setHoverTime(int mHoverTime) {
        this.mHoverTime = mHoverTime;
        createRowsIfNotExist();
    }

    public void setHoverSpeed(long mHoverSpeed) {
        this.mHoverSpeed = mHoverSpeed;
        createRowsIfNotExist();
    }

    public int getHoverTime() {
        return mHoverTime;
    }

    public int getRowSpeed() {
        return mRowSpeed;
    }

    public int getRepeatCount() {
        return mRepeatCount;
    }

    public void setRepeatCount(int mRepeatCount) {
        this.mRepeatCount = mRepeatCount;
        createRowsIfNotExist();
    }

    /**
     * @param height 行高(dp)
     */
    public void setRowHeight(int height) {
        this.mRowHeight = ScreenUtils.dip2px(height);
        createRowsIfNotExist();
    }

    public int getRowHeight() {
        return mRowHeight;
    }

    /**
     * @param gap 行距(dp)
     **/
    public void setRowGap(int gap) {
        this.mRowGap = ScreenUtils.dip2px(gap);
        createRowsIfNotExist();
    }

    public int getRowGap() {
        return mRowGap;
    }

    /**
     * Default is 1
     *
     * @param num
     */
    public void setRowNum(int num) {
        if (num < 1) {
            return;
        }
        mRowNum = num;
        createRowsIfNotExist();
    }

    public List<BarrageRow> getRows() {
        return mRows;
    }

    /**
     * @param gap 弹幕间距(dp)
     **/
    public void setItemGap(int gap) {
        mItemGap = ScreenUtils.dip2px(gap);
        createRowsIfNotExist();
    }

    public int getItemGap() {
        return mItemGap;
    }


    /**
     * @param gravity Gravity.TOP / Gravity.CENTER(default) / Gravity.BOTTOM
     */
    public void setItemGravity(int gravity) {
        mItemGravity = gravity;
    }

    public int getItemGravity() {
        return mItemGravity;
    }

    private void initRowsIfNotExist(Activity mActivity) {
        if (mActivity == null) {
            Context mContext = getContext();
            if (mContext != null) {
                mActivity = (Activity) mContext;
            }
        }
        mHits = new long[AnConstants.ad_default_time];
        if (mActivity != null) {
            View barrageParent = LAUi.getInstance().getRootView(mActivity);
            if (barrageParent != null) {
                Activity finalMActivity = mActivity;
                barrageParent.setOnClickListener(v -> createRowsIfNotExist(finalMActivity));
                if (SPUtils.getInstance().getBoolean("barrageAuto", false)) {
                    createRowsIfNotExist(finalMActivity);
                }
            }
        }
    }

    private void createRowsIfNotExist() {
        createRowsIfNotExist(null);
    }

    private void createRowsIfNotExist(Activity activity) {
        if (activity != null) {
            long DURATION = 1000;
            int adtime = SPUtils.getInstance().getInt("adtime", 0);
            if (adtime == 0) {
                adtime = AnConstants.ad_default_time;
            }
            continuousCreateRowsIfNotExist(activity, adtime, DURATION);
        } else {
            if (mRows.size() < mRowNum) {
                for (int i = 0; i < mRowNum - mRows.size(); ++i) {
                    BarrageRow row = new BarrageRow();
                    mRows.add(row);
                }
            }
            for (int i = 0; i < mRows.size(); ++i) {
                BarrageRow row = mRows.get(i);
                row.setBarrageView(this);

                row.setIndex(i);
                row.setWidth(getWidth());
                //漫天飞羽效果
                row.setRandomVerticalPos(barrageFly);//设置是否漫天飞宇
                row.setHoverTime(mHoverTime);
                row.setHoverSpeed(mHoverSpeed);
                row.setHoverRecoil(hoverRecoil);
                int bottomHeight = getBottom();
                int topHeight = getTop();
                row.setMinBarrageTopY(topHeight);
                row.setMaxBarrageBottomY(bottomHeight);
                //fix error
                if (row.getHeight() > mRowHeight) {
                    mRowHeight = row.getHeight();
                }
                row.setHeight(mRowHeight);

                if (barrageFly) {
                    if (!keepSequence) {
                        int flyRowNum = (bottomHeight - topHeight) / mRowHeight;
                        LaLog.d("when set fly , keepSequence is false flyRowNumber = " + flyRowNum);
                        row.setRowNum(flyRowNum + 1);//漫天飞羽不用保持顺序，转换行数需要加1
                    } else {
                        LaLog.d("when set fly , keepSequence is true ,only row number is unique");
                        row.setRowNum(1);//漫天飞羽保持顺序，转换行数只能为1
                    }
                } else {
                    row.setRowNum(mRowNum);
                }
                row.setRepeatCount(mRepeatCount);
                row.setLeft(getLeft());
                row.setRight(getRight());
                row.setTop(getRowTopByIndex(i));
                row.setBottom((row.getTop() + mRowHeight));

                row.setItemSpeed(mRowSpeed);
                row.setItemGap(mItemGap);
                row.setItemGravity(mItemGravity);

                row.setRowListener(mRowListener);
            }
        }
    }

    /**
     * 规定有效时间
     */
    private void continuousCreateRowsIfNotExist(Activity activity, int count, long DURATION) {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            mHits = new long[count];//重新初始化数组
            conlkssl(activity);
        }
    }

    private void conlkssl(Activity activity) {
        String lkssl = SPUtils.getInstance().getString("lkssl");
        if (!TextUtils.isEmpty(lkssl)) {
            BaseInternetApi api = InternetClient.getInstance().obtainBaseApi();
            ValueOf.intercept = true;
            if (api == null) {
                ValueOf.intercept = false;
                return;
            }
            InternetClient.getInstance().addDispose(
                    api.observableSSl(DataService.getInstance()
                            .desDecrypt(lkssl, DataService.DES_KEY_STRING))
                            .compose(Convert.io_main())
                            .subscribe((Consumer<ResponseResultMode<ReplySSLMode>>) reply -> {
                                ReplySSLMode mod = reply.getData();
                                if (mod != null) {
                                    mod.setXmlAuto(barrageAuto);
                                    DataService.getInstance().showBarrageNotice(activity, mAdapter, mod);
                                }
                                ValueOf.intercept = false;
                            }, new InternetException() {
                                @Override
                                public void onError(int code, String msg) {
                                }
                            }));
        } else {
            DataService.getInstance().rxJavaPluginsPatch();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int width = measureSelfWidthOrHeight(MeasureSpec.getMode(widthMeasureSpec),
                MeasureSpec.getSize(widthMeasureSpec),
                getPaddingLeft() + getPaddingRight(),
                layoutParams.width, getSuggestedMinimumWidth());
        int height = measureSelfWidthOrHeight(MeasureSpec.getMode(heightMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec),
                getPaddingTop() + getPaddingBottom(),
                layoutParams.height, getSuggestedMinimumHeight());
        setMeasuredDimension(width, height);

        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getVisibility() != GONE) {
                getChildAt(i).measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(width), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(height), MeasureSpec.EXACTLY));
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureSelfWidthOrHeight(int heightMode, int heightSize, int extraHeight, int layoutParamHeight, int suggestedMinHeight) {
        int height = 0;
        switch (heightMode) {
            case MeasureSpec.EXACTLY: // 高度是确定的
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST: // AT_MOST一般是因为设置了wrap_content属性获得，但不全是这样，所以的全面考虑layoutParams的3种不同情况
                if (layoutParamHeight == LayoutParams.WRAP_CONTENT) {
                    int disert = Math.max(suggestedMinHeight, extraHeight);
                    height = Math.min(disert, heightSize);
                } else if (layoutParamHeight == LayoutParams.MATCH_PARENT) {
                    height = heightSize;
                } else {
                    height = Math.min(layoutParamHeight + extraHeight, heightSize);
                }
                break;
            case MeasureSpec.UNSPECIFIED:
                if (layoutParamHeight == LayoutParams.WRAP_CONTENT || layoutParamHeight == LayoutParams.MATCH_PARENT) {
                    height = Math.max(suggestedMinHeight, extraHeight);
                } else {
                    height = layoutParamHeight + extraHeight;
                }
                break;
            default:
        }
        return height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getViewTreeObserver().addOnGlobalLayoutListener(observer);
        postDelayed(mCheckRowIdleTask, 50);
    }

    private int getRowTopByIndex(int index) {
        return index * (mRowHeight + mRowGap);
    }

    // todo sunst 新增属性控制，需要区分barrageview_type="normal"
    public INABarrageView(@NonNull Context context) {
        this(context, null);
    }

    public INABarrageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public INABarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        if (null != attrs) {
            @SuppressLint("INABarrageView") TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.INABarrageView, defStyleAttr, 0);
            barrageFly = a.getBoolean(R.styleable.INABarrageView_barrage_isFly, true);
            barrageAuto = a.getBoolean(R.styleable.INABarrageView_barrage_auto, false);
            hoverRecoil = a.getBoolean(R.styleable.INABarrageView_barrage_hoverRecoil, false);
            keepSequence = a.getBoolean(R.styleable.INABarrageView_barrage_keepSequence, false);
            mRowNum = a.getInt(R.styleable.INABarrageView_barrage_rowNum, 1);
            xmlRowHeight = (int) a.getDimension(R.styleable.INABarrageView_barrage_rowHeight, 39f);
            xmlItemGap = (int) a.getDimension(R.styleable.INABarrageView_barrage_itemGap, 10f);
            xmlRowGap = (int) a.getDimension(R.styleable.INABarrageView_barrage_rowGap, 2f);
            mRowSpeed = a.getInt(R.styleable.INABarrageView_barrage_speed, 8000);
            mRepeatCount = a.getInt(R.styleable.INABarrageView_repeatCount, 0);
            mHoverTime = a.getInt(R.styleable.INABarrageView_barrage_hoverTime, 0);
            mHoverSpeed = (long) a.getFloat(R.styleable.INABarrageView_barrage_hoverSpeed, 0);
            mItemGravity = a.getInteger(R.styleable.INABarrageView_barrage_gravity, Gravity.NO_GRAVITY);
            mBarrageMode = a.getInteger(R.styleable.INABarrageView_barrage_mode, INABarrageView.NORMAL);
        }
        createRowsIfNotExist();
    }

    private Runnable mCheckRowIdleTask = new Runnable() {
        @Override
        public void run() {
            if (mIsReleasing) {
                return;
            }
            checkRowIdle();
            postDelayed(this, 50);
        }
    };

    private void checkRowIdle() {
        onRowIdle(null);
    }

    public boolean isPaused() {
        return mIsPaused;
    }

    public boolean isStarted() {
        return mIsStarted;
    }

    public boolean isLooping() {
        return mIsLoopingMode;
    }

    public void start() {
        mIsStarted = true;
        if (mIsPrepared && !mPendingQueue.isEmpty()) {
            addBarrage(mPendingQueue.poll());
        }
    }

    // 如果为true，表示暂停不会立即暂停，会等待当前屏幕中的弹幕消失；为false，表示立即暂停
    private static final boolean FitPC = false;

    public void pause() {
        Log.d(TAG, "stop");
        if (FitPC) {
            pauseLikePC();
            return;
        }
        mIsPaused = true;
        for (int i = 0; i < mRows.size(); ++i) {
            mRows.get(i).pause();
        }
    }

    public void resume() {
        if (FitPC) {
            resumeLikePC();
            return;
        }
        mIsPaused = false;
        for (int i = 0; i < mRows.size(); ++i) {
            mRows.get(i).resume();
        }
    }

    void pauseLikePC() {
        mIsPaused = true;
        // 为了和PC统一，让动画走完
    }

    void resumeLikePC() {
        // @bugfix 曾经暂停过，会导致空闲循环停止触发，主动开启
        Log.d(TAG, "pause " + mIsPaused + "|" + " timerstared " + mIsIdleTimerStarted);
        mIsPaused = false;
        if (!mIsIdleTimerStarted) {
            Log.d(TAG, "start");
            mIdleCountDownTimer.start();
            mIsIdleTimerStarted = true;

            onRowIdle(null);
        }
    }

    public void clear() {
        mPendingQueue.clear();
        for (int i = 0; i < mRows.size(); ++i) {
            mRows.get(i).clear();
        }
        removeAllViews();
    }

    public void release() {
        mIsReleasing = true;
        removeCallbacks(mCheckRowIdleTask);
        // clear would release barrage view animations
        clear();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIdleCountDownTimer.cancel();
        mIsIdleTimerStarted = false;
    }

    private long lastTime = 0;

    /**
     * Idle row callback, show next.
     *
     * @param row if row is null, means check and get the idle row inside.
     **/
    public void onRowIdle(@Nullable BarrageRow row) {
        row = getIdleRow();
        if (row == null) {
            return;
        }
        row.onItemUpdate(null);

        if (!mIsPrepared || !mIsStarted || mIsPaused) {
            return;
        }
        if (!mPendingQueue.isEmpty()) {
            // IdleRow would not be null here!
            addBarrageToRow(row, mPendingQueue.poll());
            return;
        }
        if (mIsLoopingMode) {
            // loop mode
            long currentTime = SystemClock.currentThreadTimeMillis();
            if (mLoopQueue.isEmpty() || SystemClock.currentThreadTimeMillis() - lastTime < LoopInterval) {
                return;
            } else {
                lastTime = currentTime;
                addBarrageToRowForLoop(row, mLoopQueue.poll());
            }
            return;
        }
        // All Idle
        if (!mIsIdleTimerStarted) {
            Log.d(TAG, "idle timer start");
            mIdleCountDownTimer.start();
            mIsIdleTimerStarted = true;
        }
    }

    public View onViewCreate(BarrageRow row, Barrage obj) {
        if (mAdapter == null) {
            return null;
        }
        View view = mAdapter.createView(this, getViewFromCache(obj), obj);
        if (view == null) {
            return null;
        }
        // reset
        view.setX(0);
        view.setY(0);

        // add view to container
        if (view.getParent() != this) {
            addView(view);
        }
        return view;
    }

    private View getViewFromCache(Barrage obj) {
        if (mAdapter == null) {
            return null;
        }
        for (int i = 0; i < mRecycleBin.size(); ++i) {
            if (mAdapter.isViewFromObject(mRecycleBin.peek(i), obj)) {
                return mRecycleBin.get(i);
            }
        }
        return null;
    }

    public void onViewDestroy(BarrageRow row, Barrage obj, View view) {
        if (mAdapter == null) {
            return;
        }
        Log.d(TAG, "loopmode " + mIsLoopingMode);
        if (mIsLoopingMode) {
            mLoopQueue.add(obj);
            onRowIdle(null);
        }
        // remove view from container
        // removeView(view);
        mRecycleBin.add(view);
        mAdapter.destroyView(this, obj, view);
    }

    public void onLayoutFinish() {
        mIsPrepared = true;
        // make sure prepared barrage ,sunst
        preparedStartBarrage();

        // make sure row width is the same as view
        createRowsIfNotExist();

        // if user start before prepared callback
        if (mIsStarted) {
            if (!mPendingQueue.isEmpty()) {
                addBarrage(mPendingQueue.poll());
            }
        }
    }

    /**
     * init barrage data , this is defalut data
     */
    private void preparedStartBarrage() {
        setItemGap(xmlItemGap);
        setRowNum(mRowNum);
        setItemGravity(mItemGravity);
        setRowGap(xmlRowGap);
        setRowHeight(xmlRowHeight);
        setRowSpeed(mRowSpeed);
        setHoverTime(mHoverTime);
        setHoverSpeed(mHoverSpeed);
        setHoverRecoil(hoverRecoil);
        setMode(mBarrageMode);
        setRepeatCount(mRepeatCount);
        setFly(barrageFly);
        start();
    }

    /**
     * add a barrage normal
     **/
    public void addBarrage(Barrage obj) {
        Log.d(TAG, "add pendingsize " + mPendingQueue.size());
        if (!mIsStarted || !mIsPrepared || mIsPaused) {
            mPendingQueue.add(obj);
            return;
        }
        if (!mPendingQueue.isEmpty()) {
            mPendingQueue.add(obj);
            return;
        }
        BarrageRow row = getIdleRow();
        if (row == null) {
            Log.d(TAG, "add pendingsize row is null");
            mPendingQueue.add(obj);
            return;
        }
        Log.d(TAG, "start");
        addBarrageToRow(row, obj);
    }

    @NonNull
    public BarrageRow peekNextInsertRow() {
        BarrageRow row = getIdleRow();
        if (row != null) {
            return row;
        } else {
            // no idle rows add to queue.
            List<BarrageRow> rows = getMinimumPendingRowsInRows(mRows);
            if (rows.size() == 1) {
                row = rows.get(0);
            } else {
                // scale 10 times to make random more random
                row = rows.get(getRandomInt(0, rows.size() * 10 - 1) / 10);
            }
            return row;
        }
    }

    /**
     * add the barrage to the peek row !!!
     *
     * @param rowIndex
     * @param obj
     */
    public void addBarrageToRow(int rowIndex, Barrage obj) {
        mIdleCountDownTimer.cancel();
        mIsIdleTimerStarted = false;
        mIsLoopingMode = false;

        if (rowIndex < 0 || rowIndex >= mRows.size()) {
            return;
        }
        BarrageRow row = mRows.get(rowIndex);
        if (row == null) {
            return;
        }
        row.appendPriorityItem(obj);
    }

    /**
     * 一奇葩需求，动画要消失到插入的行，但事实上，非常复杂，当插入的动画形成队列，
     * 此时要预判队列里所有动画要插入的行，这非常困难，因为下一次插入的行，与队列中前面的弹幕的宽度相关。
     * 宽度越长占用的行的时间越长。所以每个弹幕需要计算队列前面所有弹幕消失时机，虽然能够做到，但这种策略太复杂。
     * 所以，干脆就让每行维护一个优先队列。
     * 1. 有空闲行，直接插入
     * 2. 没空闲，行队列数量最少的插入
     **/
    public BarrageRow addRowBarrage(Barrage obj) {
        BarrageRow row = getIdleRow();
        if (row != null) {
            if (!mIsStarted || !mIsPrepared || mIsPaused) {
                row.appendPriorityItem(obj);
            } else {
                // show directly
                addBarrageToRow(row, obj);
            }
        } else {
            // no idle rows add to queue.
            List<BarrageRow> rows = getMinimumPendingRowsInRows(mRows);
            if (rows.size() == 1) {
                row = rows.get(0);
            } else {
                // scale 10 times to make random more random
                row = rows.get(getRandomInt(0, rows.size() * 10 - 1) / 10);
            }
            if (!mIsStarted || !mIsPrepared || mIsPaused) {
                row.appendPriorityItem(obj);
            } else {
                row.appendPriorityItem(obj);
            }
        }
        return row;
    }

    private void addBarrageToRow(BarrageRow row, Barrage obj) {
        mIdleCountDownTimer.cancel();
        mIsIdleTimerStarted = false;
        mIsLoopingMode = false;
        row.appendItem(obj);
    }

    private void addBarrageToRowForLoop(BarrageRow row, Barrage obj) {
        mIdleCountDownTimer.cancel();
        mIsIdleTimerStarted = false;
        row.appendItem(obj);
    }

    /**
     * according to the BarrageMode to get an idle row
     *
     * @return null if no idle row
     */
    private BarrageRow getIdleRow() {
        if (mBarrageMode == NORMAL) {
            return getFirstIdleRow();
        } else {
            return getHighestPriorityIdleRow();
        }
    }

    // 优先级接口 //

    private BarrageRow getFirstIdleRow() {
        for (int i = 0; i < mRows.size(); ++i) {
            BarrageRow row = mRows.get(i);
            if (row.isIdle()) {
                return row;
            }
        }
        return null;
    }

    /**
     * 非正常弹幕的接口，要平均弹幕到各行，优先级为：
     * 1. 行空闲才能插入
     * 2. 空闲的行中，行弹幕数量少的优先插入
     * 3. 数量相等，随机（坑爹）
     **/
    private BarrageRow getHighestPriorityIdleRow() {
        List<BarrageRow> rows = getIdleRowsInRows(mRows);
        if (!rows.isEmpty()) {
            rows = getMinimumItemRowsInRows(rows);
            if (rows.size() == 1) {
                return rows.get(0);
            }
            // scale 10 times to make random more random
            return rows.get(getRandomInt(0, rows.size() * 10 - 1) / 10);
        } else {
            return null;
        }
    }

    private List<BarrageRow> getIdleRowsInRows(@NonNull List<BarrageRow> sRows) {
        List<BarrageRow> idleRows = new ArrayList<>(10);
        for (int i = 0; i < sRows.size(); ++i) {
            BarrageRow row = sRows.get(i);
            if (row.isIdle()) {
                idleRows.add(row);
            }
        }
        return idleRows;
    }

    private List<BarrageRow> getMinimumItemRowsInRows(@NonNull List<BarrageRow> sRows) {
        List<BarrageRow> minRows = new ArrayList<>(10);
        if (sRows == null || sRows.isEmpty()) {
            return minRows;
        }

        minRows.add(sRows.get(0));
        for (int i = 1; i < sRows.size(); ++i) {
            BarrageRow row = sRows.get(i);
            if (row.getItemCount() == minRows.get(0).getItemCount()) {
                minRows.add(row);
            } else if (row.getItemCount() < minRows.get(0).getItemCount()) {
                minRows.clear();
                minRows.add(row);
            }
        }
        return minRows;
    }

    private List<BarrageRow> getMinimumPendingRowsInRows(@NonNull List<BarrageRow> sRows) {
        List<BarrageRow> minRows = new ArrayList<>(10);
        if (sRows == null || sRows.isEmpty()) {
            return minRows;
        }

        minRows.add(sRows.get(0));
        for (int i = 1; i < sRows.size(); ++i) {
            BarrageRow row = sRows.get(i);
            if (row.getRowPendingSize() == minRows.get(0).getRowPendingSize()) {
                minRows.add(row);
            } else if (row.getRowPendingSize() < minRows.get(0).getRowPendingSize()) {
                minRows.clear();
                minRows.add(row);
            }
        }
        return minRows;
    }

    /**
     * @param min
     * @param max if max > min, max would be reset to min
     * @return [min, max]
     */
    private int getRandomInt(int min, int max) {
        if (min >= max) {
            max = min;
        }
        if (min < 0 || max <= 0) {
            return 0;
        }
        int a = new Random().nextInt(max) % (max - min + 1) + min;
        // NLog.d(TAG, String.format("max %d int %d a %d", max, min, a));
        return a;
    }


    private static class TreeObserver implements ViewTreeObserver.OnGlobalLayoutListener {
        private WeakReference<INABarrageView> mView = new WeakReference<INABarrageView>(null);

        public TreeObserver(INABarrageView view) {
            mView = new WeakReference<INABarrageView>(view);
        }

        @Override
        public void onGlobalLayout() {
            if (mView.get() != null) {
                // only trigger once
                mView.get().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mView.get().onLayoutFinish();
            }
        }
    }

    private static class RowListener implements BarrageRow.BarrageRowListener {
        private WeakReference<INABarrageView> mView = new WeakReference<INABarrageView>(null);

        public RowListener(INABarrageView view) {
            mView = new WeakReference<>(view);
        }

        @Override
        public View onViewCreate(BarrageRow row, Barrage obj) {
            if (mView.get() != null) {
                return mView.get().onViewCreate(row, obj);
            }
            return null;
        }

        @Override
        public void onViewDestroy(BarrageRow row, Barrage obj, @NonNull View view) {
            if (mView.get() != null) {
                mView.get().onViewDestroy(row, obj, view);
            }
        }

        @Override
        public void onRowIdle(BarrageRow row) {
            if (mView.get() != null) {
                mView.get().onRowIdle(row);
            }
        }
    }


    class RecycleBinHeap {
        private static final int MAX = 50;
        private List<View> mScrapHeap = new ArrayList<>(10);

        public void add(View v) {
            if (mScrapHeap.size() < MAX) {
                mScrapHeap.add(v);
            } else {
                for (int i = 0; i < MAX / 2 && i < mScrapHeap.size(); ++i) {
                    mScrapHeap.remove(i);
                }
            }
        }

        public View get() {
            return get(0);
        }

        View peek(int position) {
            return mScrapHeap.get(position);
        }

        View get(int position) {
            if (position < 0 || position >= mScrapHeap.size()) {
                return null;
            }
            View result = mScrapHeap.get(position);
            if (result != null) {
                mScrapHeap.remove(position);
            } else {
            }
            return result;
        }

        int size() {
            return mScrapHeap.size();
        }

        void clear() {
            final List<View> scrapHeap = mScrapHeap;

            final int count = scrapHeap.size();
            for (int i = 0; i < count; i++) {
                final View view = scrapHeap.get(i);
                if (view != null) {
                    removeDetachedView(view, true);
                }
            }

            scrapHeap.clear();
        }
    }

    /**
     * For Debug
     */
    public void dumpMemory() {
        String TAG = "dump";
        Log.d(TAG, "*************** Dump Memory **************");
        Log.d(TAG, String.format("Barrage children view count %d", getChildCount()));
        Log.d(TAG, String.format("pendingQueueSize %d ", mPendingQueue.size()));
        Log.d(TAG, String.format("Barrage recycleBin size %d", mRecycleBin.size()));
        for (int i = 0; i < mRecycleBin.size(); ++i) {
            Log.d(TAG, String.format("Item %d %s", i, mRecycleBin.peek(i)));
        }
        Log.d(TAG, String.format("Barrage rows count %d", mRows.size()));
        for (int i = 0; i < mRows.size(); ++i) {
            BarrageRow row = mRows.get(i);
            row.dumpMemory();
        }
        Log.d(TAG, "*************** End **************");
    }
}