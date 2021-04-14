package com.sunsta.bear.faster;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：AndroidTranslucentBar，状态栏透明背景设置，加锁的懒汉，线程安全单例模式参考
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 1.0 |   2016/8/17           |   AndroidTranslucentBar
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class AndroidTranslucentBar extends Activity {
    private static AndroidTranslucentBar instance;

    public synchronized static AndroidTranslucentBar getInstance() {
        if (null == instance) {
            instance = new AndroidTranslucentBar();
        }
        return instance;
    }

    public void setTranslucentBar(Window mWindow) {
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
}
