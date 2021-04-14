/**
 * Copyright 2015 sunst
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on ali "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sunsta.bear.layout.refresh;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.view.ViewCompat;

import com.sunsta.bear.R;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：慕课网下拉刷新风格控件
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
public class BGAMoocStyleRefreshViewHolder extends BGARefreshViewHolder {
    private BGAMoocStyleRefreshView mMoocRefreshView;
    private int mUltimateColorResId = -1;
    private int mOriginalImageResId = -1;

    public BGAMoocStyleRefreshViewHolder(Context context, boolean isLoadingMoreEnabled) {
        super(context, isLoadingMoreEnabled);
    }

    @Override
    public View getRefreshHeaderView() {
        if (mRefreshHeaderView == null) {
            mRefreshHeaderView = View.inflate(mContext, R.layout.view_refresh_header_mooc_style, null);
            mRefreshHeaderView.setBackgroundColor(Color.TRANSPARENT);
            if (mRefreshViewBackgroundColorRes != -1) {
                mRefreshHeaderView.setBackgroundResource(mRefreshViewBackgroundColorRes);
            }
            if (mRefreshViewBackgroundDrawableRes != -1) {
                mRefreshHeaderView.setBackgroundResource(mRefreshViewBackgroundDrawableRes);
            }

            mMoocRefreshView = (BGAMoocStyleRefreshView) mRefreshHeaderView.findViewById(R.id.moocView);
            if (mOriginalImageResId != -1) {
                mMoocRefreshView.setOriginalImage(mOriginalImageResId);
            } else {
                throw new RuntimeException("请调用" + BGAMoocStyleRefreshViewHolder.class.getSimpleName() + "的setOriginalImage方法设置原始图片资源");
            }
            if (mUltimateColorResId != -1) {
                mMoocRefreshView.setUltimateColor(mUltimateColorResId);
            } else {
                throw new RuntimeException("请调用" + BGAMoocStyleRefreshViewHolder.class.getSimpleName() + "的setUltimateColor方法设置最终生成图片的填充颜色资源");
            }
        }
        return mRefreshHeaderView;
    }

    /**
     * 设置原始的图片资源
     * @param resId
     */
    public void setOriginalImage(@DrawableRes int resId) {
        mOriginalImageResId = resId;
    }

    /**
     * 设置最终生成图片的填充颜色资源
     * @param resId
     */
    public void setUltimateColor(@ColorRes int resId) {
        mUltimateColorResId = resId;
    }

    @Override
    public void handleScale(float scale, int moveYDistance) {
        scale = 0.6f + 0.4f * scale;
        ViewCompat.setScaleX(mMoocRefreshView, scale);
        ViewCompat.setScaleY(mMoocRefreshView, scale);
    }

    @Override
    public void changeToIdle() {
    }

    @Override
    public void changeToPullDown() {
    }

    @Override
    public void changeToReleaseRefresh() {
    }

    @Override
    public void changeToRefreshing() {
        mMoocRefreshView.startRefreshing();
    }

    @Override
    public void onEndRefreshing() {
        mMoocRefreshView.stopRefreshing();
    }

}