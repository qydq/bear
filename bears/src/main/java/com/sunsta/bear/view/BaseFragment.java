package com.sunsta.bear.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.config.LoadingConfig;
import com.sunsta.bear.contract.DayNightTheme;
import com.sunsta.bear.faster.DayNightHelper;
import com.sunsta.bear.faster.EasyPermission;
import com.sunsta.bear.faster.LADialog;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.LoadingDialog;
import com.sunsta.bear.faster.SPUtils;
import com.sunsta.bear.faster.ToastUtils;
import com.sunsta.bear.immersion.ImmersiveManage;
import com.sunsta.bear.model.entity.ResponseDayNightMode;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：所有Fragment的基类
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2016/08/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 3.0 |   2020/03/29           |   an系列网络请求的Fragment基类
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public abstract class BaseFragment extends Fragment implements OnTouchListener, DayNightTheme {
    /**
     * 所有网络请求的一个被观察者
     */
    public CompositeDisposable mCompositeDisposable;
    /**
     * 子类默认使用的日志输出标签
     */
    protected String TAG = this.getClass().getSimpleName();

    protected Context mContext = null;
    protected View view;

    private TimerListener timerListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mContext = inflater.getContext();
        setTimerListener(this::onLaunchedTimer);
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        /*
         * 1/2/3为Livery提供,除此之外为系统原生兼容
         * */
        if (currentMode == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED) {
            int dayNightModeCode = getDayNightMode().getCode();
            if (dayNightModeCode == ResponseDayNightMode.NIGHT.getCode()) {
                setNightTheme();
                fitStatusBar(false, true);//设置状态栏颜色为白色
            } else if (dayNightModeCode == ResponseDayNightMode.DAY.getCode()) {
                setDayTheme();
                fitStatusBar(true, true);//设置状态栏颜色为黑色
            } else {
                followSystemTheme();
            }
        } else {
            if (currentMode == AppCompatDelegate.MODE_NIGHT_NO) {
                setDayTheme();
                fitStatusBar(true, true);//设置状态栏颜色为黑色
            } else if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                setNightTheme();
                fitStatusBar(false, true);//设置状态栏颜色为白色
            } else {
                followSystemTheme();
            }
        }
        initDayNightTheme();

        view = inflater.inflate(getLayoutId(), null);
        init(savedInstanceState);

        TAG = BaseFragment.class.getSimpleName();
        return view;
    }

    private void initDayNightTheme() {
        DayNightHelper.setDayNightTheme((Activity) mContext);
    }

    @Override
    public void setDayTheme() {
        SPUtils.getInstance().putString(AnConstants.KEY.DAYNIGHT_MODE, ResponseDayNightMode.DAY.getName());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void setNightTheme() {
        SPUtils.getInstance().putString(AnConstants.KEY.DAYNIGHT_MODE, ResponseDayNightMode.NIGHT.getName());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    public void followSystemTheme() {
        SPUtils.getInstance().putString(AnConstants.KEY.DAYNIGHT_MODE, ResponseDayNightMode.SYSTEM.getName());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                fitStatusBar(false, true);//设置状态栏颜色为白色
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                fitStatusBar(true, true);//设置状态栏颜色为黑色
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                LaLog.d("UI_MODE_NIGHT_UNDEFINED");
                break;
        }
    }

    /**
     * finish check the preference and rb ; is ok
     */
    public ResponseDayNightMode getDayNightMode() {
        String mode = SPUtils.getInstance().getString(AnConstants.KEY.DAYNIGHT_MODE, ResponseDayNightMode.SYSTEM.getName());
        return ResponseDayNightMode.valueOf(mode);
    }

    /**
     * 将 {@link Disposable} 添加到 {@link CompositeDisposable} 中统一管理
     * 可在 Fragment#onDestroy() 中使用 #unDispose() 停止正在执行的 RxJava 任务, 避免内存泄漏(框架已自行处理)
     * @param disposable
     */
    public void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);//将所有 Disposable 放入集中处理
    }

    /**
     * 停止集合中正在执行的 RxJava 任务
     */
    public void unDispose(boolean isDestroy) {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear(); // 保证 Activity 结束时取消所有正在执行的订阅
        }
        if (isDestroy) {
            this.mCompositeDisposable = null;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.setOnTouchListener(this);// 拦截触摸事件，防止内存泄露下去
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 简化Toast。
     */
    protected void showToast(final String msg) {
        runOnUiThread(() -> ToastUtils.s(mContext, msg));
    }

    protected void showToastInCenter(final String msg) {
        runOnUiThread(() -> ToastUtils.sc(mContext, msg));
    }

    protected void showSnackbar(final View ytipsView, final String msg) {
        runOnUiThread(() -> Snackbar.make(ytipsView, msg, Snackbar.LENGTH_SHORT).show());
    }


    public void showLoading(int style) {
        showLoading(style, getString(R.string.loading));
    }

    public void showLoading(int style, int backgroundFrame, String loadingContent) {
        showLoading(style, backgroundFrame, loadingContent, true);
    }

    public void showLoading(int style, int backgroundFrame) {
        showLoading(style, backgroundFrame, getString(R.string.loading));
    }

    public void showLoading(int style, String loadingContent) {
        showLoading(style, 0, loadingContent, true);
    }

    public void showLoading(String loadingContent) {
        showLoading(0, 0, loadingContent, true);
    }

    public void showLoading(String loadingContent, boolean cancelable) {
        showLoading(0, 0, loadingContent, cancelable);
    }

    public void showLoading(boolean cancelable) {
        showLoading(0, 0, getString(R.string.loading), cancelable);
    }

    public void showLoading() {
        showLoading(0, 0, getString(R.string.loading), true);
    }

    public void showLoading(int style, int backgroundFrame, String loadingContent, boolean cancelable) {
        LoadingConfig config = new LoadingConfig();
        config.setContent(getString(R.string.loading));
        config.setFixedDistance(true);
        config.setDialogClassify(style);
        config.setBackgroundFrame(backgroundFrame);
        config.setGravity(Gravity.CENTER);
        config.setContent(loadingContent);
        config.setLastPoint(true);
        config.setFullWidthScreen(false);
        config.setCancelable(cancelable);
        config.setBackgroundDimEnabled(true);
        LoadingDialog.showLoading((Activity) mContext, config);
    }


    public void dismissLoadding() {
        LoadingDialog.dismiss();
        LADialog.INSTANCE.cancelDialog();
    }

    public interface TimerListener {
        void onLaunchedTimer();
    }

    private void setTimerListener(TimerListener timerListener) {
        this.timerListener = timerListener;
    }

    public void onLaunchedTimer() {
        // your owner implement
    }

    public void launchTimer(int delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != timerListener)
                    timerListener.onLaunchedTimer();
            }
        }, delayMillis);
    }

    /**
     * 设置沉浸式状态栏
     * @param lightMode  是否浅色模式,浅色时状态栏文字图标是黑色，其他情况下是白色
     * @param fullScreen 是否填充满屏幕 false时会把状态栏的高度留出来
     */
    protected void fitStatusBar(boolean lightMode, boolean fullScreen) {
        ImmersiveManage.fitStatusBar(getActivity(), lightMode, fullScreen);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unDispose(true);
    }

    /**
     * 通过id获取当前view控件，需要在onViewCreated()之后的生命周期调用
     */
    protected <T extends View> T findViewById(@IdRes int id) {
        if (getView() != null) {
            return getView().findViewById(id);
        } else {
            return view.findViewById(id);
        }
    }

    /**
     * 拦截触摸事件，防止内存泄露下去
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    public abstract int getLayoutId();

    protected abstract void init(Bundle savedInstanceState);


    protected void runOnUiThread(Runnable r) {
        this.getActivity().runOnUiThread(r);
    }

}