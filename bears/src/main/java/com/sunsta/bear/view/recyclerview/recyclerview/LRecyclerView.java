package com.sunsta.bear.view.recyclerview.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sunsta.bear.listener.AppBarStateChangeListener;
import com.sunsta.bear.view.recyclerview.interfaces.ILoadMoreFooter;
import com.sunsta.bear.view.recyclerview.interfaces.IRefreshHeader;
import com.sunsta.bear.view.recyclerview.interfaces.OnLoadMoreListener;
import com.sunsta.bear.view.recyclerview.interfaces.OnNetWorkErrorListener;
import com.sunsta.bear.view.recyclerview.interfaces.OnRefreshListener;
import com.sunsta.bear.view.recyclerview.view.ArrowRefreshHeader;
import com.sunsta.bear.view.recyclerview.view.LoadingFooter;
import com.google.android.material.appbar.AppBarLayout;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：LRecyclerView基类
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 3.0（可选） |   2019/11/30          |   适配androidx重构
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class LRecyclerView extends RecyclerView {
    private boolean mPullRefreshEnabled = true;
    private boolean mLoadMoreEnabled = true;
    private boolean mRefreshing = false;//是否正在下拉刷新
    private boolean mLoadingData = false;//是否正在加载数据
    private boolean flag = false;//标记是否setAdapter
    private OnRefreshListener mRefreshListener;
    private OnLoadMoreListener mLoadMoreListener;
    private LScrollListener mLScrollListener;
    private IRefreshHeader mRefreshHeader;
    private ILoadMoreFooter mLoadMoreFooter;
    private View mEmptyView;
    private View mFootView;

    private final AdapterDataObserver mDataObserver = new DataObserver();
    private float mLastY = -1;
    private float sumOffSet;
    private static final float DRAG_RATE = 2.0f;
    private int mPageSize = 10; //一次网络请求默认数量

    private LRecyclerViewAdapter mWrapAdapter;
    private boolean isNoMore = false;
    private boolean mIsVpDragger;
    private int mTouchSlop;
    private float startY;
    private float startX;
    //scroll variables begin
    /**
     * 当前RecyclerView类型
     */
    protected LayoutManagerType layoutManagerType;

    /**
     * 最后一个的位置
     */
    private int[] lastPositions;

    /**
     * 最后一个可见的item的位置
     */
    private int lastVisibleItemPosition;

    /**
     * 当前滑动的状态
     */
    private int currentScrollState = 0;

    /**
     * 触发在上下滑动监听器的容差距离
     */
    private static final int HIDE_THRESHOLD = 20;

    /**
     * 滑动的距离
     */
    private int mDistance = 0;

    /**
     * 是否需要监听控制
     */
    private boolean mIsScrollDown = true;

    /**
     * Y轴移动的实际距离（最顶部为0）
     */
    private int mScrolledYDistance = 0;

    /**
     * X轴移动的实际距离（最左侧为0）
     */
    private int mScrolledXDistance = 0;
    //scroll variables end


    private AppBarStateChangeListener.State appbarState = AppBarStateChangeListener.State.EXPANDED;

    public LRecyclerView(Context context) {
        this(context, null);
    }

    public LRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext().getApplicationContext()).getScaledTouchSlop();
        if (mPullRefreshEnabled) {
            setRefreshHeader(new ArrowRefreshHeader(getContext().getApplicationContext()));
        }

        if (mLoadMoreEnabled) {
            setLoadMoreFooter(new LoadingFooter(getContext().getApplicationContext()));
        }


    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = (LRecyclerViewAdapter) adapter;
        super.setAdapter(mWrapAdapter);

        if(flag) {
            mWrapAdapter.getInnerAdapter().unregisterAdapterDataObserver(mDataObserver);
        }
        mWrapAdapter.getInnerAdapter().registerAdapterDataObserver(mDataObserver);
        flag = true;

        mDataObserver.onChanged();

        mWrapAdapter.setRefreshHeader(mRefreshHeader);

        if (mLoadMoreEnabled) {
            mWrapAdapter.addFooterView(mFootView);
        }

    }

    private class DataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter instanceof LRecyclerViewAdapter) {
                LRecyclerViewAdapter lRecyclerViewAdapter = (LRecyclerViewAdapter) adapter;
                if (lRecyclerViewAdapter.getInnerAdapter() != null && mEmptyView != null) {
                    int count = lRecyclerViewAdapter.getInnerAdapter().getItemCount();
                    if (count == 0) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        LRecyclerView.this.setVisibility(View.GONE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                        LRecyclerView.this.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (adapter != null && mEmptyView != null) {
                    if (adapter.getItemCount() == 0) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        LRecyclerView.this.setVisibility(View.GONE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                        LRecyclerView.this.setVisibility(View.VISIBLE);
                    }
                }
            }

            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
                if(mWrapAdapter.getInnerAdapter().getItemCount() < mPageSize ) {
                    mFootView.setVisibility(GONE);
                }
            }

        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart + mWrapAdapter.getHeaderViewsCount() + 1, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart + mWrapAdapter.getHeaderViewsCount() + 1, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart + mWrapAdapter.getHeaderViewsCount() + 1, itemCount);
            if(mWrapAdapter.getInnerAdapter().getItemCount() < mPageSize ) {
                mFootView.setVisibility(GONE);
            }

        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            int headerViewsCountCount = mWrapAdapter.getHeaderViewsCount();
            mWrapAdapter.notifyItemRangeChanged(fromPosition + headerViewsCountCount + 1, toPosition + headerViewsCountCount + 1 + itemCount);
        }

    }

    /**
     * 解决嵌套RecyclerView滑动冲突问题
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下的位置
                startY = ev.getY();
                startX = ev.getX();
                // 初始化标记
                mIsVpDragger = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if (mIsVpDragger) {
                    return false;
                }

                // 获取当前手指位置
                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDragger = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 初始化标记
                mIsVpDragger = false;
                break;
        }
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                sumOffSet = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = (ev.getRawY() - mLastY) / DRAG_RATE;
                mLastY = ev.getRawY();
                sumOffSet += deltaY;
                if (isOnTop() && mPullRefreshEnabled  && (appbarState == AppBarStateChangeListener.State.EXPANDED)) {
                    mRefreshHeader.onMove(deltaY, sumOffSet);
                    if (mRefreshHeader.getVisibleHeight() > 0 && mRefreshing) {
                        return false;
                    }
                }

                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && mPullRefreshEnabled /*&& appbarState == AppBarStateChangeListener.State.EXPANDED*/) {
                    if (mRefreshHeader.onRelease()) {
                        if (mRefreshListener != null) {
                            mFootView.setVisibility(GONE);
                            mRefreshing = true;
                            mRefreshListener.onRefresh();

                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public boolean isOnTop() {
        return  mPullRefreshEnabled && (mRefreshHeader.getHeaderView().getParent() != null);
    }

    /**
     * set view when no content item
     *
     * @param emptyView visiable view when items is empty
     */
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        mDataObserver.onChanged();
    }

    /**
     *
     * @param pageSize 一页加载的数量
     */
    public void refreshComplete(int pageSize) {
        this.mPageSize = pageSize;
        if (mRefreshing) {
            isNoMore = false;
            mRefreshHeader.refreshComplete();
            mRefreshing = false;
            if(mWrapAdapter.getInnerAdapter().getItemCount() < pageSize) {
                mFootView.setVisibility(GONE);
            }
        } else if (mLoadingData) {
            mLoadingData = false;
            mLoadMoreFooter.onComplete();
        }

    }

    /**
     * 设置是否已加载全部
     * @param noMore
     */
    public void setNoMore(boolean noMore){
        mLoadingData = false;
        isNoMore = noMore;
        if(isNoMore) {
            mLoadMoreFooter.onNoMore();
        } else {
            mLoadMoreFooter.onComplete();
        }
    }

    /**
     * 设置自定义的RefreshHeader
     */
    private void setRefreshHeader(IRefreshHeader refreshHeader) {
        this.mRefreshHeader = refreshHeader;
    }

    /**
     * 设置自定义的footerview
     */
    public void setLoadMoreFooter(ILoadMoreFooter loadMoreFooter) {
        this.mLoadMoreFooter = loadMoreFooter;
        mFootView = loadMoreFooter.getFootView();
        mFootView.setVisibility(GONE);
    }

    public void setPullRefreshEnabled(boolean enabled) {
        mPullRefreshEnabled = enabled;
    }

    /**
     * 到底加载是否可用
     */
    public void setLoadMoreEnabled(boolean enabled) {
        if(mWrapAdapter == null){
            throw new NullPointerException("mWrapAdapter cannot be null, please make sure the variable mWrapAdapter have been initialized.");
        }
        mLoadMoreEnabled = enabled;
        if (!enabled) {
            if (null != mWrapAdapter) {
                mWrapAdapter.removeFooterView();
            } else {
                mLoadMoreFooter.onReset();
            }
        }
    }

    public void setRefreshProgressStyle(int style) {
        if (mRefreshHeader != null && mRefreshHeader instanceof ArrowRefreshHeader) {
            ((ArrowRefreshHeader) mRefreshHeader).setProgressStyle(style);
        }
    }

    public void setArrowImageView(int resId) {
        if (mRefreshHeader != null && mRefreshHeader instanceof ArrowRefreshHeader) {
            ((ArrowRefreshHeader) mRefreshHeader).setArrowImageView(resId);
        }
    }

    public void setLoadingMoreProgressStyle(int style) {
        if (mLoadMoreFooter != null && mLoadMoreFooter instanceof LoadingFooter) {
            ((LoadingFooter) mLoadMoreFooter).setProgressStyle(style);
        }

    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void setOnNetWorkErrorListener(final OnNetWorkErrorListener listener) {
        final LoadingFooter loadingFooter = ((LoadingFooter) mFootView);
        loadingFooter.setState(LoadingFooter.State.NetWorkError);
        loadingFooter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadMoreFooter.onLoading();
                listener.reload();
            }
        });
    }

    public void setFooterViewHint(String loading, String noMore, String noNetWork) {
        if (mLoadMoreFooter != null && mLoadMoreFooter instanceof LoadingFooter) {
            LoadingFooter loadingFooter = ((LoadingFooter) mLoadMoreFooter);
            loadingFooter.setLoadingHint(loading);
            loadingFooter.setNoMoreHint(noMore);
            loadingFooter.setNoNetWorkHint(noNetWork);
        }
    }

    /**
     * 设置Footer文字颜色
     * @param indicatorColor
     * @param hintColor
     * @param backgroundColor
     */
    public void setFooterViewColor(int indicatorColor, int hintColor, int backgroundColor) {
        if (mLoadMoreFooter != null && mLoadMoreFooter instanceof LoadingFooter) {
            LoadingFooter loadingFooter = ((LoadingFooter) mLoadMoreFooter);
            loadingFooter.setIndicatorColor(ContextCompat.getColor(getContext(),indicatorColor));
            loadingFooter.setHintTextColor(hintColor);
            loadingFooter.setViewBackgroundColor(backgroundColor);
        }
    }

    /**
     * 设置颜色
     * @param indicatorColor Only call the method setRefreshProgressStyle(int style) to take effect
     * @param hintColor
     * @param backgroundColor
     */
    public void setHeaderViewColor(int indicatorColor, int hintColor, int backgroundColor) {
        if (mRefreshHeader != null && mRefreshHeader instanceof ArrowRefreshHeader) {
            ArrowRefreshHeader arrowRefreshHeader = ((ArrowRefreshHeader) mRefreshHeader);
            arrowRefreshHeader.setIndicatorColor(ContextCompat.getColor(getContext(),indicatorColor));
            arrowRefreshHeader.setHintTextColor(hintColor);
            arrowRefreshHeader.setViewBackgroundColor(backgroundColor);
        }

    }

    public void setLScrollListener(LScrollListener listener) {
        mLScrollListener = listener;
    }

    public interface LScrollListener {

        void onScrollUp();//scroll down to up

        void onScrollDown();//scroll from up to down

        void onScrolled(int distanceX, int distanceY);// moving state,you can get the move distance

        void onScrollStateChanged(int state);
    }

    public void refresh() {
        if (mPullRefreshEnabled && mRefreshListener != null) {
            mRefreshHeader.onRefreshing();
            int offSet = mRefreshHeader.getHeaderView().getMeasuredHeight();
            mRefreshHeader.onMove(offSet,offSet);
            mRefreshing = true;

            mFootView.setVisibility(GONE);
            mRefreshListener.onRefresh();

        }
    }

    public void forceToRefresh() {

        if (mLoadingData) {
            return;
        }

        refresh();
    }


    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        int firstVisibleItemPosition = 0;
        LayoutManager layoutManager = getLayoutManager();

        if (layoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LayoutManagerType.LinearLayout;
            } else if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LayoutManagerType.GridLayout;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LayoutManagerType.StaggeredGridLayout;
            } else {
                throw new RuntimeException(
                        "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (layoutManagerType) {
            case LinearLayout:
                firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GridLayout:
                firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case StaggeredGridLayout:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (lastPositions == null) {
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
                staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(lastPositions);
                firstVisibleItemPosition = findMax(lastPositions);
                break;
        }

        // 根据类型来计算出第一个可见的item的位置，由此判断是否触发到底部的监听器
        // 计算并判断当前是向上滑动还是向下滑动
        calculateScrollUpOrDown(firstVisibleItemPosition, dy);
        // 移动距离超过一定的范围，我们监听就没有啥实际的意义了
        mScrolledXDistance += dx;
        mScrolledYDistance += dy;
        mScrolledXDistance = (mScrolledXDistance < 0) ? 0 : mScrolledXDistance;
        mScrolledYDistance = (mScrolledYDistance < 0) ? 0 : mScrolledYDistance;
        if (mIsScrollDown && (dy == 0)) {
            mScrolledYDistance = 0;
        }
        //Be careful in here
        if (null != mLScrollListener) {
            mLScrollListener.onScrolled(mScrolledXDistance, mScrolledYDistance);
        }

    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        currentScrollState = state;

        if (mLScrollListener != null) {
            mLScrollListener.onScrollStateChanged(state);
        }

        if (mLoadMoreListener != null && mLoadMoreEnabled) {
            if (currentScrollState == RecyclerView.SCROLL_STATE_IDLE) {
                LayoutManager layoutManager = getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if (visibleItemCount > 0
                        && lastVisibleItemPosition >= totalItemCount - 1
                        && totalItemCount > visibleItemCount
                        && !isNoMore
                        && !mRefreshing) {

                    mFootView.setVisibility(View.VISIBLE);
                    if (mLoadingData) {
                        return;
                    } else {
                        mLoadingData = true;
                        mLoadMoreFooter.onLoading();
                        mLoadMoreListener.onLoadMore();
                    }

                }

            }
        }

    }

    /**
     * 计算当前是向上滑动还是向下滑动
     */
    private void calculateScrollUpOrDown(int firstVisibleItemPosition, int dy) {
        if (null != mLScrollListener) {
            if (firstVisibleItemPosition == 0) {
                if (!mIsScrollDown) {
                    mIsScrollDown = true;
                    mLScrollListener.onScrollDown();
                }
            } else {
                if (mDistance > HIDE_THRESHOLD && mIsScrollDown) {
                    mIsScrollDown = false;
                    mLScrollListener.onScrollUp();
                    mDistance = 0;
                } else if (mDistance < -HIDE_THRESHOLD && !mIsScrollDown) {
                    mIsScrollDown = true;
                    mLScrollListener.onScrollDown();
                    mDistance = 0;
                }
            }
        }

        if ((mIsScrollDown && dy > 0) || (!mIsScrollDown && dy < 0)) {
            mDistance += dy;
        }
    }

    public enum LayoutManagerType {
        LinearLayout,
        StaggeredGridLayout,
        GridLayout
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //解决LRecyclerView与CollapsingToolbarLayout滑动冲突的问题
        AppBarLayout appBarLayout = null;
        ViewParent p = getParent();
        while (p != null) {
            if (p instanceof CoordinatorLayout) {
                break;
            }
            p = p.getParent();
        }
        if(p instanceof CoordinatorLayout) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout)p;
            final int childCount = coordinatorLayout.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = coordinatorLayout.getChildAt(i);
                if(child instanceof AppBarLayout) {
                    appBarLayout = (AppBarLayout)child;
                    break;
                }
            }
            if(appBarLayout != null) {
                appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        appbarState = state;
                    }
                });
            }
        }
    }

}
