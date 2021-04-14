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
package com.sunsta.bear.immersion;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.core.content.ContextCompat;

import com.sunsta.bear.AnApplication;
import com.sunsta.bear.layout.swipe.widget.Drawer;

import io.reactivex.annotations.NonNull;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：ColorDrawer 相当于颜色转换工具类用于，swipe系列Decoration
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
public class ColorDrawer extends Drawer {

    public ColorDrawer(int color, int width, int height) {
        super(new ColorDrawable(opaqueColor(color)), width, height);
    }

    /**
     * The target color is packaged in an opaque color.
     * @param color color.
     * @return color.
     */
    @ColorInt
    public static int opaqueColor(@ColorInt int color) {
        int alpha = Color.alpha(color);
        if (alpha == 0) return color;
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(255, red, green, blue);
    }

    /**
     * 把 color-string 转换为 Color-int
     * @param colorString String累心的颜色资源 ，如："#3700B3"
     */
    public static int string2Int(@NonNull String colorString) {
        return Color.parseColor(colorString);
    }

    /**
     * 把Color-int 转换为 color-string.
     * @param colorInt int类型的颜色
     */
    public static String int2RgbString(@ColorInt int colorInt) {
        colorInt = colorInt & 0x00ffffff;
        String color = Integer.toHexString(colorInt);
        while (color.length() < 6) {
            color = "0" + color;
        }
        return "#" + color;
    }

    /**
     * 把Color-int 转换为透明通道的 color-string.
     * @param colorInt int类型的颜色
     */
    public static String int2ArgbString(@ColorInt int colorInt) {
        String color = Integer.toHexString(colorInt);
        while (color.length() < 6) {
            color = "0" + color;
        }
        while (color.length() < 8) {
            color = "f" + color;
        }
        return "#" + color;
    }

    /**
     * 得到一个随机颜色
     */
    public static int getRandomColor() {
        return getRandomColor(true);
    }

    /**
     * 得到一个随机颜色
     * @param supportAlpha 是否支持透明度
     */
    public static int getRandomColor(final boolean supportAlpha) {
        int high = supportAlpha ? (int) (Math.random() * 0x100) << 24 : 0xFF000000;
        return high | (int) (Math.random() * 0x1000000);
    }

    /**
     * 根据颜色资源的id，返回一个与特定资源ID关联的颜色
     * @param id The desired resource identifier.
     */
    public static int getColor(@ColorRes int id) {
        return ContextCompat.getColor(AnApplication.getApplication(), id);
    }

    /**
     * 得到带透明度的组件颜色1，返回透明度的组件.
     * @param color The color.
     * @param alpha Alpha component \([0..255]\) of the color.
     */
    public static int setAlphaComponent(@ColorInt int color, @IntRange(from = 0x0, to = 0xFF) int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    /**
     * 得到带透明度的组件颜色2，返回透明度的组件
     * @param color The color.
     * @param alpha Alpha component \([0..1]\) of the color.
     */
    public static int getAlphaComponent(@ColorInt int color, @FloatRange(from = 0, to = 1) float alpha) {
        return (color & 0x00ffffff) | ((int) (alpha * 255.0f + 0.5f) << 24);
    }

    /**
     * 控件背景色选择器
     * @param idNormal  默认图片
     * @param idPressed 按压时图片
     * @return
     */
    public StateListDrawable setSelector(Context mContext, int idNormal, int idPressed) {
//        anBackLl.setBackground(setSelector(R.drawable.ic_color_org_normal, R.drawable.ic_color_org_gray_normal));
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = idNormal == -1 ? null : mContext.getResources().getDrawable(idNormal);
        Drawable pressed = idPressed == -1 ? null : mContext.getResources().getDrawable(idPressed);
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        bg.addState(new int[]{android.R.attr.state_enabled}, normal);
        bg.addState(new int[]{}, normal);
        return bg;
    }

    /**
     * 控件字体颜色选择器
     * @param mContext 当前上下文
     * @param normal   默认显示颜色
     * @param pressed  按压后显示的颜色
     * @return
     */
    public ColorStateList createColorStateList(Context mContext, int normal, int pressed) {
        int pressColor = mContext.getResources().getColor(pressed);
        int normalColor = mContext.getResources().getColor(normal);
        int[] colors = new int[]{pressColor, pressColor, normalColor, pressColor, pressColor, normalColor};
        int[][] states = new int[6][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{android.R.attr.state_focused};
        states[4] = new int[]{android.R.attr.state_window_focused};
        states[5] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }
}