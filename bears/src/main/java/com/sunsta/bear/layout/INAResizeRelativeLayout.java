package com.sunsta.bear.layout;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：两边都是矩形的圆角视图
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 1.0 |   2016/12/21           |   两边都是矩形的圆角视图。
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class INAResizeRelativeLayout extends RelativeLayout {

    public static final int HIDE = 0;
    public static final int SHOW = 1;

    private Handler mainHandler = new Handler();

    public INAResizeRelativeLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public INAResizeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onSizeChanged(int w, final int h, int oldw, final int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (oldh - h > 48) {
                    keyBordStateListener.onStateChange(SHOW);
                } else {
                    if (keyBordStateListener != null) {
                        keyBordStateListener.onStateChange(HIDE);
                    }
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private KeyBordStateListener keyBordStateListener;

    public void setKeyBordStateListener(KeyBordStateListener keyBordStateListener) {
        this.keyBordStateListener = keyBordStateListener;
    }


    public interface KeyBordStateListener {
        public void onStateChange(int state);
    }
}