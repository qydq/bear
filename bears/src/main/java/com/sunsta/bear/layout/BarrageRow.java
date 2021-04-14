package com.sunsta.bear.layout;

import android.util.Log;
import android.view.View;

import com.sunsta.bear.entity.Barrage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

/**
 * Created by sunst 【晴雨.qy】 on 2017/9/8.
 */
public class BarrageRow {
    private static final String TAG = "BarrageRow";

    // | head ... tail |
    private Deque<BarrageItem> mItems = new ArrayDeque<>();
    private Deque<BarrageItem> mRecycleBin = new ArrayDeque<>();
    private ItemListener mItemListener = new ItemListener();
    private Queue<Barrage> mPendingPriorityQueue = new ArrayDeque<>(100);

    private int mIndex = -1;

    private int mHeight = 10;//默认高度，会用于漫天飞羽
    private int mWidth = 0;
    private int mLeft = 0;
    private int mRight = 0;
    private int mTop = 0;
    private int mBottom = 0;

    private int mItemGap = 0;
    private int mItemSpeed = 0;
    private int mItemGravity = 0;

    private int mRowNum = 1;

    private int mRepeatCount = 0;
    private int minBarrageTopY = 0;
    private int maxBarrageBottomY = 200;
    private boolean randomVerticalPos;
    private boolean hoverRecoil = false;
    private int mHoverTime = 0;
    private long mHoverSpeed = 0;

    public long getHoverSpeed() {
        return mHoverSpeed;
    }

    public void setHoverSpeed(long mHoverSpeed) {
        this.mHoverSpeed = mHoverSpeed;
    }

    private INABarrageView mINABarrageView;

    public interface BarrageRowListener {
        View onViewCreate(BarrageRow row, Barrage obj);

        void onViewDestroy(BarrageRow row, Barrage obj, @NonNull View view);

        void onRowIdle(BarrageRow row);
    }


    public boolean isHoverRecoil() {
        return hoverRecoil;
    }

    public void setHoverRecoil(boolean hoverRecoil) {
        this.hoverRecoil = hoverRecoil;
    }

    private BarrageRowListener mListener;

    public void setRowListener(BarrageRowListener listener) {
        mListener = listener;
    }

    public int getHoverTime() {
        return mHoverTime;
    }

    public void setHoverTime(int mHoverTime) {
        this.mHoverTime = mHoverTime;
    }

    public int getMinBarrageTopY() {
        return minBarrageTopY;
    }

    public void setMinBarrageTopY(int minBarrageTopY) {
        this.minBarrageTopY = minBarrageTopY;
    }

    public int getMaxBarrageBottomY() {
        return maxBarrageBottomY;
    }

    public void setMaxBarrageBottomY(int maxBarrageBottomY) {
        this.maxBarrageBottomY = maxBarrageBottomY;
    }

    public int getRepeatCount() {
        return mRepeatCount;
    }

    public void setRepeatCount(int mRepeatCount) {
        this.mRepeatCount = mRepeatCount;
    }

    public int getRowNum() {
        return mRowNum;
    }

    public void setRowNum(int mRowNum) {
        this.mRowNum = mRowNum;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    public boolean getRandomVerticalPos() {
        return randomVerticalPos;
    }

    public void setRandomVerticalPos(boolean randomVerticalPos) {
        this.randomVerticalPos = randomVerticalPos;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public int getLeft() {
        return mLeft;
    }

    public void setLeft(int left) {
        mLeft = left;
    }

    public int getRight() {
        return mRight;
    }

    public void setRight(int right) {
        mRight = right;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int rowIndex) {
        this.mIndex = rowIndex;
    }

    public int getTop() {
        return mTop;
    }

    public void setTop(int rowTop) {
        this.mTop = rowTop;
    }

    public int getBottom() {
        return mBottom;
    }

    public void setBottom(int rowBottom) {
        this.mBottom = rowBottom;
    }

    public int getRowPendingSize() {
        return mPendingPriorityQueue.size();
    }

    public int getItemCount() {
        return mItems.size();
    }

    public int getItemGap() {
        return mItemGap;
    }

    public void setItemGap(int itemGap) {
        this.mItemGap = itemGap;
    }

    public int getItemSpeed() {
        return mItemSpeed;
    }

    public void setItemSpeed(int itemSpeed) {
        this.mItemSpeed = itemSpeed;
    }

    public void setItemGravity(int itemGravity) {
        this.mItemGravity = itemGravity;
    }

    public int getItemGravity() {
        return mItemGravity;
    }

    public void setBarrageView(INABarrageView view) {
        mINABarrageView = view;
    }

    public void pause() {
        for (BarrageItem item : mItems) {
            item.pause();
        }
    }

    public void resume() {
        for (BarrageItem item : mItems) {
            item.resume();
        }
    }

    public void clear() {
        mPendingPriorityQueue.clear();
        while (mItems.size() > 0) {
            BarrageItem item = mItems.poll();
            if (item != null) {
                if (mListener != null && item.getContentView() != null) {
                    mListener.onViewDestroy(this, item.getData(), item.getContentView());
                }
                item.clear();
                mRecycleBin.add(item);
            }
        }
    }


    public void appendItem(Barrage obj) {
        if (mListener == null) {
            Log.e(TAG, "snbh. listener is null.");
            return;
        }
        View view = mListener.onViewCreate(this, obj);
        if (view == null) {
            return;
        }

        BarrageItem item = obtainBarrageItem();
        if (item != null) {
            item.setRow(this);
            item.setData(obj);
            item.setContentView(view);
            item.setDistance(mWidth);
            item.setSpeed(mItemSpeed);
            item.setHoverTimer(mHoverTime);
            item.setGravity(mItemGravity);
            item.setListener(mItemListener);
            item.start();
            mItems.addLast(item);
        }
        Log.d(TAG, String.format("distance %d speed %d", mWidth, mItemSpeed));
    }

    /**
     * 行优先队列，比View优先队列更优先
     * 仅用于动画，确保动画消失所在的行会出现对应的动画对象
     *
     * @param obj
     */
    public void appendPriorityItem(Barrage obj) {
        if (!mPendingPriorityQueue.isEmpty() || mINABarrageView.isPaused() || !mINABarrageView.isStarted()) {
            mPendingPriorityQueue.add(obj);
            return;
        }
        if (!isIdle()) {
            mPendingPriorityQueue.add(obj);
            return;
        }
        appendItem(obj);
    }

    private BarrageItem obtainBarrageItem() {
        if (mRecycleBin.isEmpty()) {
            return new BarrageItem();
        }
        return mRecycleBin.poll();
    }

    public void onItemUpdate(BarrageItem item) {
        if (isIdle()) {
            if (!mPendingPriorityQueue.isEmpty() && !mINABarrageView.isPaused() && mINABarrageView.isStarted()) {
                appendItem(mPendingPriorityQueue.poll());
            }
        }
    }

    public void onItemFinish(BarrageItem item) {
        Log.d(TAG, "remove item " + item.toString());
        if (mItems.remove(item)) {
            mRecycleBin.add(item);

            if (mListener != null && item.getContentView() != null) {
                mListener.onViewDestroy(this, item.getData(), item.getContentView());
            }
        }
    }

    public boolean isIdle() {
        BarrageItem lastItem = getLastItem();
        if (lastItem == null) {
            return true;
        }
        View contentView = lastItem.getContentView();
        if (contentView == null) {
            return true;
        }
        //  |---[ItemWidth ItemGap]--|
        if (contentView.getX() + contentView.getWidth() + mItemGap <= mWidth) {
            // NLog.d(TAG, String.format("Idle x %f l %d w %d g %d sw %d", contentView.getX(), contentView.getLeft(),
            //         contentView.getWidth(), mItemGap, mWidth));
            if (contentView.getX() == 0) {
                // means the last item was adding
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * @return 距离下一次空闲时间，当前空闲返回0
     **/
    public int peekNextIdleTime() {
        BarrageItem lastItem = getLastItem();
        if (lastItem == null) {
            return 0;
        }
        View contentView = lastItem.getContentView();
        if (contentView == null) {
            return 0;
        }
        //  |---[ItemWidth ItemGap]--|
        int moveDist = (int) ((contentView.getX() + contentView.getWidth() + mItemGap) - mWidth);
        if (moveDist <= 0) {
            if (contentView.getX() == 0) {
                // means the last item was adding
                return lastItem.getSpeed();
            }
            return 0;
        }
        return (int) (moveDist * 1.0 / mWidth * lastItem.getSpeed());
    }

    @Nullable
    private BarrageItem getLastItem() {
        if (mItems.isEmpty()) {
            return null;
        }
        return mItems.peekLast();
    }

    private class ItemListener implements BarrageItem.BarrageItemListener {
        @Override
        public void onAnimationCancel(BarrageItem item) {
        }

        @Override
        public void onAnimationEnd(BarrageItem item) {
            onItemFinish(item);
        }

        @Override
        public void onAnimationRepeat(BarrageItem item) {

        }

        @Override
        public void onAnimationStart(BarrageItem item) {

        }

        @Override
        public void onAnimationPause(BarrageItem item) {

        }

        @Override
        public void onAnimationResume(BarrageItem item) {

        }

        @Override
        public void onAnimationUpdate(BarrageItem item) {
            // onItemUpdate(item);
        }
    }

    /**
     * For Debug
     */
    public void dumpMemory() {
        String TAG = "dump";
        Log.d(TAG, String.format("Row index %d itemCount %d recycleBinCount %d pendingQueueSize %d",
                getIndex(), getItemCount(), mRecycleBin.size(), mPendingPriorityQueue.size()));
    }
}
