/*
 * Copyright 2016 sunst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on ali "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sunsta.bear.layout.swipe.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.sunsta.bear.R;
import com.sunsta.bear.layout.swipe.SwipeRecyclerView;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：DefaultLoadMoreView
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
public class DefaultLoadMoreView extends LinearLayout implements SwipeRecyclerView.LoadMoreView, View.OnClickListener {

    private ProgressBar mProgressBar;
    private TextView mTvMessage;

    private SwipeRecyclerView.LoadMoreListener mLoadMoreListener;

    public DefaultLoadMoreView(Context context) {
        this(context, null);
    }

    public DefaultLoadMoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        setGravity(Gravity.CENTER);
        setVisibility(GONE);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        int minHeight = (int)(displayMetrics.density * 60 + 0.5);
        setMinimumHeight(minHeight);

        inflate(getContext(), R.layout.base_swipemenu_recyclerview_load_more, this);
        mProgressBar = findViewById(R.id.progress_bar);
        mTvMessage = findViewById(R.id.tv_load_more_message);
        setOnClickListener(this);
    }

    @Override
    public void onLoading() {
        setVisibility(VISIBLE);
        mProgressBar.setVisibility(VISIBLE);
        mTvMessage.setVisibility(VISIBLE);
        mTvMessage.setText(R.string.an_load_more_message);
    }

    @Override
    public void onLoadFinish(boolean dataEmpty, boolean hasMore) {
        if (!hasMore) {
            setVisibility(VISIBLE);

            if (dataEmpty) {
                mProgressBar.setVisibility(GONE);
                mTvMessage.setVisibility(VISIBLE);
                mTvMessage.setText(R.string.an_data_empty);
            } else {
                mProgressBar.setVisibility(GONE);
                mTvMessage.setVisibility(VISIBLE);
                mTvMessage.setText(R.string.an_data_notmore);
            }
        } else {
            setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onWaitToLoadMore(SwipeRecyclerView.LoadMoreListener loadMoreListener) {
        this.mLoadMoreListener = loadMoreListener;

        setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mTvMessage.setVisibility(VISIBLE);
        mTvMessage.setText(R.string.an_click_load_more);
    }

    @Override
    public void onLoadError(int errorCode, String errorMessage) {
        setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mTvMessage.setVisibility(VISIBLE);
        mTvMessage.setText(
            TextUtils.isEmpty(errorMessage) ? getContext().getString(R.string.an_load_error) : errorMessage);
    }

    @Override
    public void onClick(View v) {
        if (mLoadMoreListener != null) mLoadMoreListener.onLoadMore();
    }
}