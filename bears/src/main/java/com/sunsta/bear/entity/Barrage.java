package com.sunsta.bear.entity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

import androidx.annotation.DimenRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.sunsta.bear.model.LevelMode;
import com.sunsta.bear.model.adapter.BarrageDataAdapter;

import java.io.Serializable;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：视频播放弹幕控制的实体对象
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2017/8/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 *
 * @author sunst // sunst0069
 * @version 3.0 |   2020/04/07          |   增加more数据
 * @link 知乎主页：href="https://zhihu.com/people/qydq
 * <p> ---Revision History:  ->  : |version|date|updateinfo| ---------
 */
public class Barrage implements Serializable {
    private int id;//弹幕id值（可选）
    private int background;//the barrage background, refs this : R.drawable.base_bg_pressed
    private int markBackground;//the markBackground default is 0
    private String content;//the barrage main content , support richtext

    private int lightIvId;//ivLight's head resource , default value is 0
    private int primaryIvId;//ivPrimary 's head resource , default value is 0
    private int markIvId;//ivMark 's head resource , default value is 0
    private int circleIvId;//ivCircle 's head resource , default value is 0
    private int gifIvId;//gifIvId 's head resource , default value is 0
    private int gifIvId2;//gifIvId 's head resource , default value is 0
    private int gifIvId3;//gifIvId 's head resource , default value is 0
    private String primaryLink;//ivPrimary's head resource , （primaryIvId, link is firstly
    private String lightLink;//ivLight's head resource , （lightIvId, link is firstly
    private String markLink;//ivMark's head resource , （markIvId, link is firstly
    private String circleLink;//ivCircle's head resource , （markIvId, link is firstly

    private int textPrimaryColor;//the barrage main text color , also it can be selector list
    private int textLightColor = 0;//the barrage main text color , it can be selector list
    @DimenRes
    private int textPrimarySize;//textPrimarySize is sp , like this ,R.dimen.font_size

    @DimenRes
    private int barrageHeight = 0;//the barrageHeight
    private int barrageSpeed = 0;//the barrageSpeed
    @LayoutRes
    private int barrageLayout = 0;//the barragelayout ,default 0 is default layout
    private int ivPrimaryRadius = 10;//default is 10

    private int patchBarrageWidth = 0;// if fillBarrageWidth is true ,you can use patchBarrageWidth fix some width space, dp unite
    private String userName;//save name
    private String type;//the barrage type refs this:BarrageType.IMAGE_TEXT
    private boolean fillBarrageWidth = false;//filling barrage with primary content size
    private boolean fillGifWidth = false;//filling barrage with ivGif content size
    private boolean accelerate = false;//accelerate Interpolator use of barrage
    private boolean barrageFloat = false;//hover recoil only when set barrageHover time
    private long floatSpeed = 0L;//default is 0,only in floatTime，the value is ok
    private int floatTime = 0;//the floatTime is when barrage fly in screen size floatTime start ,defalt value is 0
    private float floatTransX = 0f;//the floattransX when barrage float the distance
    private ValueAnimator barrageAnimator = null;//the barrage valueanimator
    private ObjectAnimator floatAnimator = null;//the animator use in float animator


    /**
     * save level data ，this can use more info tips
     */
    private LevelMode level;

    public ObjectAnimator getFloatAnimator() {
        return floatAnimator;
    }

    public void setFloatAnimator(ObjectAnimator floatAnimator) {
        this.floatAnimator = floatAnimator;
    }

    public float getFloatTransX() {
        return floatTransX;
    }

    public void setFloatTransX(float floatTransX) {
        this.floatTransX = floatTransX;
    }


    public ValueAnimator getBarrageAnimator() {
        return barrageAnimator;
    }

    public void setBarrageAnimator(ValueAnimator barrageValueAnimator) {
        this.barrageAnimator = barrageValueAnimator;
    }

    public boolean isFillBarrageWidth() {
        return fillBarrageWidth;
    }

    public void setFillBarrageWidth(boolean fillBarrageWidth) {
        this.fillBarrageWidth = fillBarrageWidth;
    }

    public boolean isAccelerate() {
        return accelerate;
    }

    public void setAccelerate(boolean accelerate) {
        this.accelerate = accelerate;
    }

    public int getTextPrimarySize() {
        return textPrimarySize;
    }

    public void setTextPrimarySize(@DimenRes int textPrimarySize) {
        this.textPrimarySize = textPrimarySize;
    }

    public int getBarrageSpeed() {
        return barrageSpeed;
    }

    public void setBarrageSpeed(int barrageSpeed) {
        this.barrageSpeed = barrageSpeed;
    }

    public int getMarkBackground() {
        return markBackground;
    }

    public void setMarkBackground(int markBackground) {
        this.markBackground = markBackground;
    }

    public int getMarkIvId() {
        return markIvId;
    }

    public void setMarkIvId(int markIvId) {
        this.markIvId = markIvId;
    }

    public int getBarrageLayout() {
        return barrageLayout;
    }

    public void setBarrageLayout(@LayoutRes int barrageLayout) {
        this.barrageLayout = barrageLayout;
    }

    public int getBarrageHeight() {
        return barrageHeight;
    }

    public void setBarrageHeight(int barrageHeight) {
        this.barrageHeight = barrageHeight;
    }

    public int getPatchBarrageWidth() {
        return patchBarrageWidth;
    }

    public void setPatchBarrageWidth(int patchBarrageWidth) {
        this.patchBarrageWidth = patchBarrageWidth;
    }

    public boolean isFloat() {
        return barrageFloat;
    }

    public void setBarrageFloat(boolean barrageFloat) {
        this.barrageFloat = barrageFloat;
    }

    public long getBarrageFloatSpeed() {
        return floatSpeed;
    }

    public void setBarrageFloatSpeed(long barrageHoverSpeed) {
        this.floatSpeed = barrageHoverSpeed;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getTextPrimaryColor() {
        return textPrimaryColor;
    }

    public void setTextPrimaryColor(int textPrimaryColor) {
        this.textPrimaryColor = textPrimaryColor;
    }

    public int getTextLightColor() {
        return textLightColor;
    }

    public void setTextLightColor(int textLightColor) {
        this.textLightColor = textLightColor;
    }

    public LevelMode getLevel() {
        return level;
    }

    public void setLevel(LevelMode level) {
        this.level = level;
    }

    public Barrage(String type) {
        this.type = type;
    }

    public Barrage(int id, String type, @NonNull String content) {
        this.id = id;
        this.content = content;
        this.type = type;
    }

    public int getFloatTime() {
        return floatTime;
    }

    public void setFloatTime(int hoverTime) {
        this.floatTime = hoverTime;
    }

    public Barrage(int id, @NonNull String content) {
        new Barrage(id, BarrageDataAdapter.BarrageType.TEXT, content);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLightIvId() {
        return lightIvId;
    }

    public void setLightIvId(int lightIvId) {
        this.lightIvId = lightIvId;
    }

    public int getPrimaryIvId() {
        return primaryIvId;
    }

    public void setPrimaryIvId(int primaryIvId) {
        this.primaryIvId = primaryIvId;
    }

    public String getPrimaryLink() {
        return primaryLink;
    }

    public void setPrimaryLink(String primaryLink) {
        this.primaryLink = primaryLink;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCircleIvId() {
        return circleIvId;
    }

    public void setCircleIvId(int circleIvId) {
        this.circleIvId = circleIvId;
    }

    public int getGifIvId() {
        return gifIvId;
    }

    public void setGifIvId(int gifIvId) {
        this.gifIvId = gifIvId;
    }

    public int getGifIvId2() {
        return gifIvId2;
    }

    public void setGifIvId2(int gifIvId2) {
        this.gifIvId2 = gifIvId2;
    }


    public int getGifIvId3() {
        return gifIvId3;
    }

    public void setGifIvId3(int gifIvId3) {
        this.gifIvId3 = gifIvId3;
    }

    public String getLightLink() {
        return lightLink;
    }

    public void setLightLink(String lightLink) {
        this.lightLink = lightLink;
    }

    public String getMarkLink() {
        return markLink;
    }

    public void setMarkLink(String markLink) {
        this.markLink = markLink;
    }

    public String getCircleLink() {
        return circleLink;
    }

    public void setCircleLink(String circleLink) {
        this.circleLink = circleLink;
    }

    public int getIvPrimaryRadius() {
        return ivPrimaryRadius;
    }

    public void setIvPrimaryRadius(int ivPrimaryRadius) {
        this.ivPrimaryRadius = ivPrimaryRadius;
    }

    public boolean isFillGifWidth() {
        return fillGifWidth;
    }

    public void setFillGifWidth(boolean fillGifWidth) {
        this.fillGifWidth = fillGifWidth;
    }
}