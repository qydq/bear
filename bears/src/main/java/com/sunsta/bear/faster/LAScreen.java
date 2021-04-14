package com.sunsta.bear.faster;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：这个Context哪来的我们不能确定，很大的可能性，你在某个Activity里面为了方便，直接传了个this;这样问题就来了，
 * 我们的这个类中的sInstance是一个static且强引用的，在其内部引用了一个Activity作为Context，
 * 也就是说，我们的这个Activity只要我们的项目活着，就没有办法进行内存回收。而我们的Activity的生命周期肯定没这么长，所以造成了内存泄漏。
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 2.0 |  2015/11/27             |   LaScreen屏幕工具类
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class LAScreen {

    public static int height;
    public static int width;
    private Context context;

    private static LAScreen instance;

    private LAScreen(Context context) {
        this.context = context;
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
    }

    //    fix :解决了内存泄漏的问题，因为我们引用的是一个ApplicationContext，它的生命周期和我们的单例对象一致
    //具体参考 https://blog.csdn.net/lmj623565791/article/details/40481055
    public static synchronized LAScreen getInstance(Context context) {
        if (instance == null) {
            instance = new LAScreen(context.getApplicationContext());
        }
        return instance;
    }


    /**
     * 得到手机屏幕的宽度, pix单位
     */
    public int getScreenWidth() {
        return width;
    }

    /**
     * 得到手机屏幕的宽度, pix单位
     */
    public int getScreenHeight() {
        return height;
    }

}