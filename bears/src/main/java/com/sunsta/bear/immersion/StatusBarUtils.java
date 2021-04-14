package com.sunsta.bear.immersion;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.faster.LaBitmap;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.ValueOf;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import static com.sunsta.bear.view.BaseActivity.SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;

/**
 * @author：luck
 * @data：2018/3/28 下午1:01
 * @描述: 沉浸式
 */

public class StatusBarUtils {
    public static int DEFAULT_COLOR = 0;
    public static float DEFAULT_ALPHA = 0;
    public static final int MIN_API = 17;

    /**
     * 设置沉浸式状态栏
     * @param activity   上下文
     * @param lightMode  是否浅色模式,浅色时状态栏文字图标是黑色，其他情况下是白色
     * @param fullScreen 是否填充满屏幕 false时会把状态栏的高度留出来
     */
    public static void fitStatusBar(Activity activity, boolean lightMode, boolean fullScreen) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            View decorView = window.getDecorView();
//两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//设置状态栏内容是否反色,6.0以上使用标准参数
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (lightMode) {
                    option |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    option &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
            } else { //5.X oppo自定义的Flag
                if (lightMode) {
                    option |= SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
                } else {
                    option &= ~SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
                }
            }
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
//导航栏颜色也可以正常设置
// window.setNavigationBarColor(Color.TRANSPARENT);
        } else {
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
// int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            attributes.flags |= flagTranslucentStatus;
// attributes.flags |= flagTranslucentNavigation;
            window.setAttributes(attributes);
        }

//是否预留状态栏的高度
        if (!fullScreen) {
            ViewGroup rootView = window.getDecorView().findViewById(android.R.id.content);
            rootView.setPadding(0, LaBitmap.INSTANCE.getStatusBarHeight(), 0, 0);
        }
    }

    /**
     * 已修改，设置底部导航栏颜色，
     * <p>
     * @param colorId 颜色资源id
     */
    public static void fitNavigationBar(@NonNull Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setNavigationBarColor(activity.getResources().getColor(colorId));//底部导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    //<editor-fold desc="沉侵">
    public static void immersive(Activity activity) {
        immersive(activity, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public static void immersive(Activity activity, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        immersive(activity.getWindow(), color, alpha);
    }

    public static void immersive(Activity activity, int color) {
        immersive(activity.getWindow(), color, 1f);
    }

    public static void immersive(Window window) {
        immersive(window, DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public static void immersive(Window window, int color) {
        immersive(window, color, 1f);
    }

    public static void immersive(Window window, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (Build.VERSION.SDK_INT >= 21) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(mixtureColor(color, alpha));

            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setTranslucentView((ViewGroup) window.getDecorView(), color, alpha);
        }
    }
//</editor-fold>

    //<editor-fold desc="DarkMode">
    public static void darkMode(Activity activity, boolean dark) {
        if (isFlyme4Later()) {
            darkModeForFlyme4(activity.getWindow(), dark);
        } else if (isMIUI6Later()) {
            darkModeForMIUI6(activity.getWindow(), dark);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            darkModeForM(activity.getWindow(), dark);
        }
    }

    /**
     * 设置状态栏darkMode,字体颜色及icon变黑(目前支持MIUI6以上,Flyme4以上,Android M以上)
     */
    public static void darkMode(Activity activity) {
        darkMode(activity.getWindow(), DEFAULT_COLOR, DEFAULT_ALPHA);
    }

    public static void darkMode(Activity activity, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        darkMode(activity.getWindow(), color, alpha);
    }

    /**
     * 设置状态栏darkMode,字体颜色及icon变黑(目前支持MIUI6以上,Flyme4以上,Android M以上)
     */
    public static void darkMode(Window window, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (isFlyme4Later()) {
            darkModeForFlyme4(window, true);
            immersive(window, color, alpha);
        } else if (isMIUI6Later()) {
            darkModeForMIUI6(window, true);
            immersive(window, color, alpha);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            darkModeForM(window, true);
            immersive(window, color, alpha);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setTranslucentView((ViewGroup) window.getDecorView(), color, alpha);
        }
// if (Build.VERSION.SDK_INT >= 21) {
// window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// window.setStatusBarColor(Color.TRANSPARENT);
// } else if (Build.VERSION.SDK_INT >= 19) {
// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// }

// setTranslucentView((ViewGroup) window.getDecorView(), color, alpha);
    }

//------------------------->

    /**
     * android 6.0设置字体颜色
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private static void darkModeForM(Window window, boolean dark) {
// window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// window.setStatusBarColor(Color.TRANSPARENT);

        int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
        if (dark) {
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        window.getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    /**
     * 设置Flyme4+的darkMode,darkMode时候字体颜色及icon变黑
     * http://open-wiki.flyme.cn/index.php?title=Flyme%E7%B3%BB%E7%BB%9FAPI
     */
    public static boolean darkModeForFlyme4(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams e = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(e);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }

                meizuFlags.setInt(e, value);
                window.setAttributes(e);
                result = true;
            } catch (Exception var8) {
                Log.e("StatusBar", "darkIcon: failed");
            }
        }

        return result;
    }

    /**
     * 设置MIUI6+的状态栏是否为darkMode,darkMode时候字体颜色及icon变黑
     * http://dev.xiaomi.com/doc/p=4769/
     */
    public static boolean darkModeForMIUI6(Window window, boolean darkmode) {
        Class<? extends Window> clazz = window.getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            darkModeForM(window, darkmode);
        }
        return true;
    }

    /**
     * 判断是否Flyme4以上
     */
    public static boolean isFlyme4Later() {
        return Build.FINGERPRINT.contains("Flyme_OS_4")
                || Build.VERSION.INCREMENTAL.contains("Flyme_OS_4")
                || Pattern.compile("Flyme OS [4|5]", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find();
    }

    /**
     * 判断是否为MIUI6以上
     */
    public static boolean isMIUI6Later() {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method mtd = clz.getMethod("get", String.class);
            String val = (String) mtd.invoke(null, "ro.miui.ui.version.name");
            val = val.replaceAll("[vV]", "");
            int version = Integer.parseInt(val);
            return version >= 6;
        } catch (Exception e) {
            return false;
        }
    }
//</editor-fold>

    /**
     * 增加View的paddingTop,增加的值为状态栏高度
     */
    public static void setPadding(Context context, View view) {
        if (Build.VERSION.SDK_INT >= MIN_API) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)
     */
    public static void setPaddingSmart(Context context, View view) {
        if (Build.VERSION.SDK_INT >= MIN_API) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null && lp.height > 0) {
                lp.height += getStatusBarHeight(context);//增高
            }
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /**
     * 增加View的高度以及paddingTop,增加的值为状态栏高度.一般是在沉浸式全屏给ToolBar用的
     */
    public static void setHeightAndPadding(Context context, View view) {
        if (Build.VERSION.SDK_INT >= MIN_API) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.height += getStatusBarHeight(context);//增高
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                    view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /**
     * 增加View上边距（MarginTop）一般是给高度为 WARP_CONTENT 的小控件用的
     */
    public static void setMargin(Context context, View view) {
        if (Build.VERSION.SDK_INT >= MIN_API) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) lp).topMargin += getStatusBarHeight(context);//增高
            }
            view.setLayoutParams(lp);
        }
    }

    /**
     * 创建假的透明栏
     */
    public static void setTranslucentView(ViewGroup container, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        int mixtureColor = mixtureColor(color, alpha);
        View translucentView = container.findViewById(android.R.id.custom);
        if (translucentView == null && mixtureColor != 0) {
            translucentView = new View(container.getContext());
            translucentView.setId(android.R.id.custom);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(container.getContext()));
            container.addView(translucentView, lp);
        }
        if (translucentView != null) {
            translucentView.setBackgroundColor(mixtureColor);
        }
    }

    public static int mixtureColor(int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        int a = (color & 0xff000000) == 0 ? 0xff : color >>> 24;
        return (color & 0x00ffffff) | (((int) (a * alpha)) << 24);
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 24;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        } else {
            result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    result, Resources.getSystem().getDisplayMetrics());
        }
        return result;
    }


    /*-------分割线--------*/
    public static void setLightStatusBarAboveAPI23(Activity activity, boolean isMarginStatusBar
            , boolean isMarginNavigationBar, boolean isTransStatusBar, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setLightStatusBar(activity, isMarginStatusBar, isMarginNavigationBar, isTransStatusBar, dark);
        }
    }

    public static void setLightStatusBar(Activity activity, boolean dark) {
        setLightStatusBar(activity, false, false, false, dark);
    }

    public static void setLightStatusBar(Activity activity, boolean isMarginStatusBar
            , boolean isMarginNavigationBar, boolean isTransStatusBar, boolean dark) {
        switch (RomUtils.getLightStatausBarAvailableRomType()) {
            case RomUtils.AvailableRomType.MIUI:
                if (RomUtils.getMIUIVersionCode() >= 7) {
                    setAndroidNativeLightStatusBar(activity, isMarginStatusBar, isMarginNavigationBar, isTransStatusBar, dark);
                } else {
                    setMIUILightStatusBar(activity, isMarginStatusBar, isMarginNavigationBar, isTransStatusBar, dark);
                }
                break;

            case RomUtils.AvailableRomType.FLYME:
                setFlymeLightStatusBar(activity, isMarginStatusBar, isMarginNavigationBar, isTransStatusBar, dark);
                break;

            case RomUtils.AvailableRomType.ANDROID_NATIVE:
                setAndroidNativeLightStatusBar(activity, isMarginStatusBar, isMarginNavigationBar, isTransStatusBar, dark);
                break;

            case RomUtils.AvailableRomType.NA:
                // N/A do nothing
                break;
        }
    }


    private static boolean setMIUILightStatusBar(Activity activity, boolean isMarginStatusBar
            , boolean isMarginNavigationBar, boolean isTransStatusBar, boolean darkmode) {
        initStatusBarStyle(activity, isMarginStatusBar, isMarginNavigationBar);

        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            setAndroidNativeLightStatusBar(activity, isMarginStatusBar, isMarginNavigationBar, isTransStatusBar, darkmode);
        }
        return false;
    }

    private static boolean setFlymeLightStatusBar(Activity activity, boolean isMarginStatusBar
            , boolean isMarginNavigationBar, boolean isTransStatusBar, boolean dark) {
        boolean result = false;
        if (activity != null) {
            initStatusBarStyle(activity, isMarginStatusBar, isMarginNavigationBar);
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;

                if (RomUtils.getFlymeVersion() >= 7) {
                    setAndroidNativeLightStatusBar(activity, isMarginStatusBar, isMarginNavigationBar, isTransStatusBar, dark);
                }
            } catch (Exception e) {
                setAndroidNativeLightStatusBar(activity, isMarginStatusBar, isMarginNavigationBar, isTransStatusBar, dark);
            }
        }
        return result;
    }

    @TargetApi(11)
    private static void setAndroidNativeLightStatusBar(Activity activity, boolean isMarginStatusBar
            , boolean isMarginNavigationBar, boolean isTransStatusBar, boolean isDarkStatusBarIcon) {

        try {
            if (isTransStatusBar) {
                Window window = activity.getWindow();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (isMarginStatusBar && isMarginNavigationBar) {
                        //5.0版本及以上
                        if (isDarkStatusBarIcon && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        } else {
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                        }
                    } else if (!isMarginStatusBar && !isMarginNavigationBar) {

                        if (isDarkStatusBarIcon && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        } else {
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                        }


                    } else if (!isMarginStatusBar) {
                        if (isDarkStatusBarIcon && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        } else {
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                        }


                    } else {
                        //留出来状态栏 不留出来导航栏 没找到办法。。
                        return;
                    }
                }
            } else {
                View decor = activity.getWindow().getDecorView();
                if (isDarkStatusBarIcon && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    // We want to change tint color to white again.
                    // You can also record the flags in advance so that you can turn UI back completely if
                    // you have set other flags before, such as translucent or full screen.
                    decor.setSystemUiVisibility(0);
                }
            }
        } catch (Exception e) {
        }
    }

    private static void initStatusBarStyle(Activity activity, boolean isMarginStatusBar
            , boolean isMarginNavigationBar) {
        if (isMarginStatusBar && isMarginNavigationBar) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } else if (!isMarginStatusBar && !isMarginNavigationBar) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } else if (!isMarginStatusBar) {

            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } else {
            //留出来状态栏 不留出来导航栏 没找到办法。。
        }

    }
}