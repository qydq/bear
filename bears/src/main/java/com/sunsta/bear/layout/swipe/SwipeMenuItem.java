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

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：SwipeMenuItem
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
public class SwipeMenuItem {

    private Context mContext;
    private Drawable background;
    private Drawable icon;
    private String title;
    private ColorStateList titleColor;
    private int titleSize;
    private Typeface textTypeface;
    private int textAppearance;
    private int width = -2;
    private int height = -2;
    private int weight = 0;

    public SwipeMenuItem(Context context) {
        mContext = context;
    }

    public SwipeMenuItem setBackground(@DrawableRes int resId) {
        return setBackground(ContextCompat.getDrawable(mContext, resId));
    }

    public SwipeMenuItem setBackground(Drawable background) {
        this.background = background;
        return this;
    }

    public SwipeMenuItem setBackgroundColorResource(@ColorRes int color) {
        return setBackgroundColor(ContextCompat.getColor(mContext, color));
    }

    public SwipeMenuItem setBackgroundColor(@ColorInt int color) {
        this.background = new ColorDrawable(color);
        return this;
    }

    public Drawable getBackground() {
        return background;
    }

    public SwipeMenuItem setImage(@DrawableRes int resId) {
        return setImage(ContextCompat.getDrawable(mContext, resId));
    }

    public SwipeMenuItem setImage(Drawable icon) {
        this.icon = icon;
        return this;
    }

    public Drawable getImage() {
        return icon;
    }

    public SwipeMenuItem setText(@StringRes int resId) {
        return setText(mContext.getString(resId));
    }

    public SwipeMenuItem setText(String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return title;
    }

    public SwipeMenuItem setTextColorResource(@ColorRes int titleColor) {
        return setTextColor(ContextCompat.getColor(mContext, titleColor));
    }

    public SwipeMenuItem setTextColor(@ColorInt int titleColor) {
        this.titleColor = ColorStateList.valueOf(titleColor);
        return this;
    }

    public ColorStateList getTitleColor() {
        return titleColor;
    }

    public SwipeMenuItem setTextSize(int titleSize) {
        this.titleSize = titleSize;
        return this;
    }

    public int getTextSize() {
        return titleSize;
    }

    public SwipeMenuItem setTextAppearance(@StyleRes int textAppearance) {
        this.textAppearance = textAppearance;
        return this;
    }

    public int getTextAppearance() {
        return textAppearance;
    }

    public SwipeMenuItem setTextTypeface(Typeface textTypeface) {
        this.textTypeface = textTypeface;
        return this;
    }

    public Typeface getTextTypeface() {
        return textTypeface;
    }

    public SwipeMenuItem setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public SwipeMenuItem setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public SwipeMenuItem setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public int getWeight() {
        return weight;
    }
}