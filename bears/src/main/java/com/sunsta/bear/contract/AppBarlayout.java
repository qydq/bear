package com.sunsta.bear.contract;

import android.content.Context;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import com.google.android.material.appbar.AppBarLayout;

/**
 * @author sunsta
 * @date 2021/4/14
 * @des livery框架唯一AppBarlayout对外提供计算后的alpha值
 */
public class AppBarlayout extends AppBarLayout {
    private float appBarAlpha;

    public AppBarlayout(@NonNull Context context) {
        super(context);
    }

    public float getAppBarAlpha() {
        return appBarAlpha;
    }

    public void setAppBarAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
        this.appBarAlpha = appBarAlpha;
    }
}