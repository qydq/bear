package com.sunsta.bear.faster;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.sunsta.bear.AnConstants;

import java.lang.reflect.Field;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：尺寸相关工具类
 * * 1.dpi转px
 * * 2.px转dp
 * * 3.sp转px
 * * 4.px转sp
 * * 5.onCreate中强行获取View的宽高
 * * 6.获取屏幕的宽度px
 * * 7.获取屏幕的高度px
 * * 8.获取状态栏高度
 * * 9.获取状态栏高度＋标题栏(ActionBar)高度
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2016/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 2.0 |  2019/7/21             |   增加获取屏幕相关尺寸方法
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class ScreenUtils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
//        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
//        final float scale = Resources.getSystem().getDisplayMetrics().density;
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * dp 转 px
     * @param dp
     * @return
     */
    public static int dp2px(int dp) {
        // 1px = 1dp * (dpi / 160)
//        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int dpi = metrics.densityDpi;
        return (int) (dp * (dpi / 160f) + 0.5f);
    }

    /**
     * px 转 dp
     * @param px
     * @return
     */
    public static int px2dp(int px) {
//        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int dpi = metrics.densityDpi;
        return (int) (px * 160f / dpi + 0.5f);
    }

    //dpi转px
    public static float dp2px(float dpi) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpi, metrics);
    }

    //px转dp
    public static float px2dp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, metrics);
    }

    //sp转px
    public static float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }

    //px转sp
    public static float px2sp(float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    //onCreate中强行获取View的宽高
    public static int[] forceGetViewSize(View view) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return new int[]{widthMeasureSpec, heightMeasureSpec};
    }

    //获取屏幕的宽度px
    public static int getDeviceWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.widthPixels;
    }

    //获取屏幕的高度px
    public static int getDeviceHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.heightPixels;
    }

    //获取状态栏高度＋标题栏(ActionBar)高度
    public static int getTopBarHeight(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.heightPixels - getStatusBarHeightByReflect(context);
    }

    public static int getStatusBarHeightByReflect(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            statusBarHeight = context.getApplicationContext().getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        }
        return statusBarHeight == 0 ? dip2px(25) : statusBarHeight;
    }

    //获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setLayoutParamsHeight(@NonNull View paramsView, int dpHeight) {
        setLayoutParams(paramsView, 0, dpHeight);
    }

    public static void setLayoutParamsWidth(@NonNull View paramsView, int dpWidth) {
        setLayoutParams(paramsView, dpWidth, 0);
    }

    /**
     * 设置宽度为wrap_content（说明：如果是设置宽高都为wrap_或者match，那何必调用此方法呢）
     */
    public static void setLayoutParamsWidthWrap(@NonNull View paramsView, int dpHeight) {
        setLayoutParams(paramsView, -2, dpHeight);
    }

    public static void setLayoutParamsWidthMatch(@NonNull View paramsView, int dpHeight) {
        setLayoutParams(paramsView, -1, dpHeight);
    }

    public static void setLayoutParamsHeightWrap(@NonNull View paramsView, int dpWidth) {
        setLayoutParams(paramsView, dpWidth, -2);
    }

    public static void setLayoutParamsHeightMatch(@NonNull View paramsView, int dpHeight) {
        setLayoutParams(paramsView, dpHeight, -1);
    }

    /**
     * @param paramsView 设置高度，宽度的布局
     * @param dpWidth    宽度(单位dp) (如果宽度=-2，则表示wrap_content；-1：表示match_parent；0：表示不需要设置宽度）
     * @param dpHeight   高度dp（如果高度=-2，则表示wrap_content：-1：表示match_parent；0：表示不需要设置高度)
     */
    public static void setLayoutParams(@NonNull View paramsView, int dpWidth, int dpHeight) {
        ViewGroup.LayoutParams params = paramsView.getLayoutParams();
        if (dpWidth != 0) {
            if (dpWidth == -2) {
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else if (dpWidth == -1) {
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                params.width = ScreenUtils.dp2px(dpWidth);
            }
        }
        if (dpHeight != 0) {
            if (dpHeight == -2) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else if (dpHeight == -1) {
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                params.height = ScreenUtils.dp2px(dpHeight);
            }
        }
        paramsView.setLayoutParams(params);
    }

    public static void setLayoutParamsHeight(@NonNull View observerView, @NonNull View paramsView) {
        observerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setLayoutParamsHeight(paramsView, observerView.getHeight());
                observerView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    public static void setLayoutParamsWidth(@NonNull View observerView, @NonNull View paramsView) {
        observerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setLayoutParamsWidth(paramsView, observerView.getWidth());
                observerView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    public static void setLayoutParamsWidthWrap(@NonNull View observerView, @NonNull View paramsView) {
        observerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setLayoutParamsWidthWrap(paramsView, observerView.getHeight());
                observerView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    public static void setLayoutParamsWidthMatch(@NonNull View observerView, @NonNull View paramsView) {
        observerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setLayoutParamsWidthMatch(paramsView, observerView.getHeight());
                observerView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    public static void setLayoutParamsHeightWrap(@NonNull View observerView, @NonNull View paramsView) {
        observerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setLayoutParamsHeightWrap(paramsView, observerView.getWidth());
                observerView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    public static void setLayoutParamsHeightMatch(@NonNull View observerView, @NonNull View paramsView) {
        observerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setLayoutParamsHeightMatch(paramsView, observerView.getWidth());
                observerView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    public static void setLayoutParams(@NonNull View observerView, @NonNull View paramsView) {
        observerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setLayoutParams(paramsView, observerView.getWidth(), observerView.getHeight());
                observerView.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }
}