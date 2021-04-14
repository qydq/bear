package com.sunsta.bear.layout;

import android.view.View;

/**
 * <h2>请关注个人知乎Bgwan， 在【an系列】专栏会有本【livery框架】的使用案例（20190922-正在持续更新中...</h2>
 * 中文描述：Created by qydq on 16/5/24. * <br/>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期：20116/5/24
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 1.0 |   2016/5/24           |   Created by qydq on 16/5/24.
 */
public class Instrument {
    private static Instrument mInstrument;

    public static Instrument getInstance() {
        if (mInstrument == null) {
            mInstrument = new Instrument();
        }
        return mInstrument;
    }

    public float getTranslationY(View view) {
        if (view == null) {
            return 0;
        }
        return view.getTranslationY();
    }

    public void slidingByDelta(View view, float delta) {
        if (view == null) {
            return;
        }
        view.clearAnimation();
        view.setTranslationY(delta);
    }

    public void slidingToY(View view, float y) {
        if (view == null) {
            return;
        }
        view.clearAnimation();
        view.setY(y);
    }

    public void reset(View view, long duration) {
        if (view == null) {
            return;
        }
        view.clearAnimation();
        android.animation.ObjectAnimator.ofFloat(view, "translationY", 0F).setDuration(duration).start();
    }

    public void smoothTo(View view, float y, long duration) {
        if (view == null) {
            return;
        }
        view.clearAnimation();
        android.animation.ObjectAnimator.ofFloat(view, "translationY", y).setDuration(duration).start();
    }
}
