package com.sunsta.bear.faster;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.model.entity.ResponseDayNightMode;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：夜间模式帮助类。
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2016/09/19
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 2.0 |   2019/06/09           |   去除17年的夜间模式实现方法，现在修改为配置xml主题
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class DayNightHelper {

    public final static String MODE = AnConstants.KEY.DAYNIGHT_MODE;

    private SharedPreferences mSharedPreferences;

    public DayNightHelper(SharedPreferences sp) {
        this.mSharedPreferences = sp;
    }

    /**
     * 保存模式设置
     * @param mode
     * @return
     */
    public boolean setMode(ResponseDayNightMode mode) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(MODE, mode.getName());
        return editor.commit();
    }

    /**
     * 夜间模式
     * @return
     */
    public boolean isNight() {
        String mode = mSharedPreferences.getString(MODE, ResponseDayNightMode.DAY.getName());
        if (ResponseDayNightMode.NIGHT.getName().equals(mode)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 日间模式
     * @return
     */
    public boolean isDay() {
        String mode = mSharedPreferences.getString(MODE, ResponseDayNightMode.DAY.getName());
        if (ResponseDayNightMode.DAY.getName().equals(mode)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 刷新 StatusBar
     */
    public static void refreshStatusBar(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = activity.getTheme();
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            /*fix Error:(77, 75) 警告: [deprecation] Resources中的getColor(int)已过时*/
//            activity.getWindow().setStatusBarColor(activity.getResources().getColor(typedValue.resourceId));
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, typedValue.resourceId));
        }
    }

    /**
     * 设置统一主题
     */
    public static void setDayNightTheme(@NonNull Activity activity) {
        activity.setTheme(R.style.DayNightTheme);
    }
}
