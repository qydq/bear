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

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunsta.bear.immersion.ColorDrawer;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：BorderItemDecoration
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
public class BorderItemDecoration extends RecyclerView.ItemDecoration {

    private final int mWidth;
    private final int mHeight;
    private final Drawer mDrawer;

    /**
     * @param color divider line color.
     */
    public BorderItemDecoration(@ColorInt int color) {
        this(color, 4, 4);
    }

    /**
     * @param color line color.
     * @param width line width.
     * @param height line height.
     */
    public BorderItemDecoration(@ColorInt int color, int width, int height) {
        this.mWidth = Math.round(width / 2F);
        this.mHeight = Math.round(height / 2F);
        this.mDrawer = new ColorDrawer(color, mWidth, mHeight);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
        @NonNull RecyclerView.State state) {
        outRect.set(mWidth, mHeight, mWidth, mHeight);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        canvas.save();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        assert layoutManager != null;
        int childCount = layoutManager.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View view = layoutManager.getChildAt(i);
            mDrawer.drawLeft(view, canvas);
            mDrawer.drawTop(view, canvas);
            mDrawer.drawRight(view, canvas);
            mDrawer.drawBottom(view, canvas);
        }
        canvas.restore();
    }
}