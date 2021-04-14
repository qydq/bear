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
package com.sunsta.bear.layout.swipe;

import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：SwipeMenu
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 4.0（可选） |   2019/11/30          |   适配androidx重构
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class SwipeMenu {

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {}

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    private SwipeMenuLayout mMenuLayout;
    private int mOrientation;
    private List<SwipeMenuItem> mSwipeMenuItems;

    public SwipeMenu(SwipeMenuLayout menuLayout) {
        this.mMenuLayout = menuLayout;
        this.mOrientation = SwipeMenu.HORIZONTAL;
        this.mSwipeMenuItems = new ArrayList<>(2);
    }

    /**
     * Set a percentage.
     *
     * @param openPercent such as 0.5F.
     */
    public void setOpenPercent(@FloatRange(from = 0.1, to = 1) float openPercent) {
        mMenuLayout.setOpenPercent(openPercent);
    }

    /**
     * The duration of the set.
     *
     * @param scrollerDuration such 500.
     */
    public void setScrollerDuration(@IntRange(from = 1) int scrollerDuration) {
        mMenuLayout.setScrollerDuration(scrollerDuration);
    }

    /**
     * Set the menu mOrientation.
     *
     * @param orientation use {@link SwipeMenu#HORIZONTAL} or {@link SwipeMenu#VERTICAL}.
     *
     * @see SwipeMenu#HORIZONTAL
     * @see SwipeMenu#VERTICAL
     */
    public void setOrientation(@OrientationMode int orientation) {
        this.mOrientation = orientation;
    }

    /**
     * Get the menu mOrientation.
     *
     * @return {@link SwipeMenu#HORIZONTAL} or {@link SwipeMenu#VERTICAL}.
     */
    @OrientationMode
    public int getOrientation() {
        return mOrientation;
    }

    public void addMenuItem(SwipeMenuItem item) {
        mSwipeMenuItems.add(item);
    }

    public void removeMenuItem(SwipeMenuItem item) {
        mSwipeMenuItems.remove(item);
    }

    public List<SwipeMenuItem> getMenuItems() {
        return mSwipeMenuItems;
    }

    public boolean hasMenuItems() {
        return !mSwipeMenuItems.isEmpty();
    }
}