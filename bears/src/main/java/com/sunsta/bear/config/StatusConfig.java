package com.sunsta.bear.config;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;

import java.io.Serializable;

public class StatusConfig implements Serializable {
    @ColorRes
    private int background;//action0的颜色值
    private String lightContent;//轻量级提示内容=如："网络错误，请检查网络设置"
    private String clickContent;//点击提示内容=如"查看更多>";
    @DrawableRes
    private int ivErrorDrawableRes;//ivError的图片资源(默认优先)
    private String ivLinkUrl;//ivError网络资源，可加载动画
    private int distanceToTrigger;//tvPatch距离顶部的触发距离 , default value is 0，表示居中
    private boolean ivErrorFixed = false;//是否图片固定高度宽度，默认false，如果固定则会设置ivErrorHeight,ivErrorWidth为默认值（默认优先级最高）
    private boolean enableClickColor = true;//点击的颜色，是否是选中有点击效果，默认true
    @DimenRes
    private int ivErrorHeight;//ivError的图片资源的高度
    @DimenRes
    private int ivErrorWidth;//ivError的图片资源的宽度
    @ColorRes
    private int lightColor;//轻量级提示内容颜色
    @ColorRes
    private int clickColor = 0;//点击提示内容颜色

    public int getBackground() {
        return background;
    }

    public void setBackground(@ColorRes int background) {
        this.background = background;
    }

    public String getLightContent() {
        return lightContent;
    }

    public void setLightContent(String lightContent) {
        this.lightContent = lightContent;
    }

    public String getClickContent() {
        return clickContent;
    }

    public void setClickContent(String clickContent) {
        this.clickContent = clickContent;
    }

    public int getIvErrorDrawableRes() {
        return ivErrorDrawableRes;
    }

    public void setIvErrorDrawableRes(@DrawableRes int ivErrorDrawableRes) {
        this.ivErrorDrawableRes = ivErrorDrawableRes;
    }

    public boolean isEnableClickColor() {
        return enableClickColor;
    }

    public void setEnableClickColor(boolean enableClickColor) {
        this.enableClickColor = enableClickColor;
    }

    public String getIvLinkUrl() {
        return ivLinkUrl;
    }

    public void setIvLinkUrl(String ivLinkUrl) {
        this.ivLinkUrl = ivLinkUrl;
    }

    public int getDistanceToTrigger() {
        return distanceToTrigger;
    }

    public void setDistanceToTrigger(int distanceToTrigger) {
        this.distanceToTrigger = distanceToTrigger;
    }

    public boolean isIvErrorFixed() {
        return ivErrorFixed;
    }

    public void setIvErrorFixed(boolean ivErrorFixed) {
        this.ivErrorFixed = ivErrorFixed;
    }

    public int getIvErrorHeight() {
        return ivErrorHeight;
    }

    public void setIvErrorHeight(@DimenRes int ivErrorHeight) {
        this.ivErrorHeight = ivErrorHeight;
    }

    public int getIvErrorWidth() {
        return ivErrorWidth;
    }

    public void setIvErrorWidth(@DimenRes int ivErrorWidth) {
        this.ivErrorWidth = ivErrorWidth;
    }

    public int getLightColor() {
        return lightColor;
    }

    public void setLightColor(@ColorRes int lightColor) {
        this.lightColor = lightColor;
    }

    public int getClickColor() {
        return clickColor;
    }

    public void setClickColor(@ColorRes int clickColor) {
        this.clickColor = clickColor;
    }
}