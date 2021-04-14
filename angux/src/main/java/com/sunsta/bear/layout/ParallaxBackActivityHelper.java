package com.sunsta.bear.layout;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.View;

import java.util.Stack;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：回退Activity帮助类
 * <a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以通过关注我的知乎获取更详细的信息</a>
 * <h3>版权声明：(C) 2016 The Android Developer Sunst</h3>
 * <br>创建日期：2017/3/23
 * <br>邮件email：qyddai@gmail.com
 * <br>个人Github：https://qydq.github.io
 * <p>--#---- Revision History:  --- >  : |version|date|updateinfo|----#--
 * @author sunst
 * @version 1.0 |   2017/3/23           |   回退Activity帮助类。
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class ParallaxBackActivityHelper {
    private static final Stack<ParallaxBackActivityHelper> sActivities = new Stack<>();

    public Activity getActivity() {
        return mActivity;
    }

    private Activity mActivity;

    private ParallaxBackLayout mParallaxBackLayout;

    public ParallaxBackActivityHelper(Activity activity) {
        mActivity = activity;
        mParallaxBackLayout = new ParallaxBackLayout(mActivity);
        sActivities.push(this);
    }

    public boolean hasSecondActivity() {
        return sActivities.size() >= 2;
    }

    public void onPostCreate() {
        mParallaxBackLayout.attachToActivity(this);
    }

    public void onActivityDestroy() {
        sActivities.remove(this);
    }

    public ParallaxBackActivityHelper getSecondActivity() {
        if (sActivities.size() >= 2)
            return sActivities.elementAt(sActivities.size() - 2);
        return null;
    }

    public void drawThumb(Canvas canvas) {
        View decorChild = getBackLayout().getContentView();
        decorChild.draw(canvas);
    }

    /*fix need cast findview by id 晴雨2018*/
    public <T extends View> T findViewById(int id) {
        if (mParallaxBackLayout != null) {
            return mParallaxBackLayout.findViewById(id);
        }
        return null;
    }

    public void scrollToFinishActivity() {
        getBackLayout().scrollToFinishActivity();
    }

    public void setBackEnable(boolean enable) {
        getBackLayout().setEnableGesture(enable);
    }

    public ParallaxBackLayout getBackLayout() {
        return mParallaxBackLayout;
    }
}
