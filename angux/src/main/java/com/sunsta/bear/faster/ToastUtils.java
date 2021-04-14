package com.sunsta.bear.faster;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.sunsta.bear.AnApplication;
import com.sunsta.bear.R;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：Toast工具类 ，新增防止点击跳到其它界面
 * </p>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2018/3/28 下午4:10
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 2.0 | 2020/03/27/11:44 | 优化新增的自定义的Toast工具
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public final class ToastUtils {
    private static Toast toast;
    /**
     * Prevent continuous click, jump two pages
     */
    private static long lastToastTime;
    private final static long TIME = 1500;

    /**
     * this toast fix time multiple show problem
     */
    public static void s(Context context, String txt) {
        if (!shouldShow()) {
            Toast.makeText(context.getApplicationContext(), txt, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * this toast will in center of windows
     */
    public static void sc(Context mContext, String txt) {
        toast = Toast.makeText(mContext, txt, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 10);
        if (!shouldShow()) {
            toast.show();
        }
    }

    /**
     * 以下为自定义Toast，可以设置自定义图标
     */
    public static void showSelfToast(CharSequence txt) {
        next(txt, 0, Toast.LENGTH_SHORT);
    }

    public static void showSelfToast(CharSequence txt, @DrawableRes int imageRes) {
        next(txt, imageRes, Toast.LENGTH_SHORT);
    }

    public static void showSelfLongToast(CharSequence txt) {
        next(txt, 0, Toast.LENGTH_LONG);
    }

    public static void showSelfLongToast(CharSequence txt, @DrawableRes int imageRes) {
        next(txt, imageRes, Toast.LENGTH_LONG);
    }

    private static void next(CharSequence tvStr, int iconRes, int duration) {
        if (toast == null) {
            toast = new Toast(AnApplication.getApplication());
        }
        View view = LayoutInflater.from(AnApplication.getApplication()).inflate(R.layout.an_item_toast, null);
        TextView tvToast = view.findViewById(R.id.tvToast);
        tvToast.setText(tvStr);
        ImageView ivToast = view.findViewById(R.id.ivToast);
        if (iconRes > 0) {
            ivToast.setVisibility(View.VISIBLE);
            ivToast.setImageResource(iconRes);
        } else {
            ivToast.setVisibility(View.GONE);
        }
        toast.setView(view);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void cancel() {
        if (null != toast) {
            toast.cancel();
        }
    }

    /**
     * this is fix time multiple show problem
     */
    private static boolean shouldShow() {
        long time = System.currentTimeMillis();
        if (time - lastToastTime < TIME) {
            return true;
        }
        lastToastTime = time;
        return false;
    }

    /**
     * this toast use in service or some broadcast avoid
     * java.lang.RuntimeException: Can't toast on a thread that has not called Looper.prepare()
     */
    public static void loopToast(@NonNull Context context, String txt) {
        Looper.prepare();
        Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}