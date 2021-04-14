/*
 * Copyright (C) 2016 The Android Developer sunst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on ali "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sunsta.bear.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.sunsta.bear.R;
import com.sunsta.bear.config.LoadingConfig;
import com.sunsta.bear.faster.ActivityTaskManager;
import com.sunsta.bear.faster.EasyPermission;
import com.sunsta.bear.faster.LADialog;
import com.sunsta.bear.faster.ViewUtils;
import com.sunsta.bear.faster.LaBitmap;
import com.sunsta.bear.faster.LoadingDialog;
import com.sunsta.bear.faster.NetBroadcastReceiver;
import com.sunsta.bear.faster.NetBroadcastReceiverUtils;
import com.sunsta.bear.faster.ToastUtils;
import com.sunsta.bear.immersion.ImmersiveManage;
import com.sunsta.bear.layout.INAStatusLayout;
import com.sunsta.bear.layout.INABarLayout;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 * <h2>请关注个人知乎Bgwan， 在【an系列】专栏会有本【livery框架】的使用案例（20190922-正在持续更新中...</h2>
 * 中文描述：an系列alidd框架，所有Activity的基类 * <br/>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2016/8/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 *
 * @author sunst // sunst0069
 * @version 4.0（可选） |   2020/03/19          |   新增网络请求，统一最低api21
 */
public abstract class BaseActivity extends AppCompatActivity implements NetBroadcastReceiver.NetEvevt {
    /**
     * 所有网络请求的一个被观察者
     */
    public CompositeDisposable mCompositeDisposable;

    /**
     * 基类(Activity)所使用的TAG标签
     */
    protected String TAG = this.getClass().getSimpleName();

    /**
     * 网路监听广播
     */
    public static boolean firstNoInspectNet = false;
    public static NetBroadcastReceiver.NetEvevt evevt;

    //android.net.conn.CONNECTIVITY_CHANGE广播无法接收的问题,Android 7.0 为了后台优化，推荐使用 JobScheduler 代替 BroadcastReceiver 来监

    /**
     * 网络类型
     */
    protected int netModel;

    //OPPO手机5.x自定义的用来控制状态栏反色的
    public static final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;
    protected Context mContext;

    protected boolean mFullScreen = true;

    protected INABarLayout inaBarlayout;
    protected INAStatusLayout inaStatusLayout;
    private NetBroadcastReceiver networkChangeReceiver;
    private TimerListener timerListener;
    private ScheduleListener scheduleListener;
    private Timer mTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置标题栏no title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 将其子activity添加到activity采集器
        ActivityTaskManager.getInstance().pushActivity(this);
        //网络变化监听相关
        firstNoInspectNet = true;
        evevt = this;

        inspectNet();

        mContext = this;
        setTimerListener(this::onLaunchedTimer);
        setScheduleListener(this::onScheduledTimer);
        //以下要不要都可以
        fitStatusBar(true, mFullScreen);

        if (mFullScreen) {
            changeStatusBarStyle(Color.parseColor("#00000000"));
        } else {
            changeStatusBarStyle(Color.TRANSPARENT);
        }
        //移除：java.lang.IllegalStateException: Only fullscreen activities can request orientation  可能报错
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        /*todo, 智能机器人语音识别模块在这里回调，这里应该考虑异步操作，alispeak为抽象方法，ali表示爱李芳芳*/
//        int type = 19930609;
//        String words = "李芳芳我爱你";
//        alispeak(type, words);
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);
    }

    /**
     * 初始化时判断有没有网络
     */
    public boolean inspectNet() {
        //这里动态注册广播，监听网络变化

        //fix android 7.0 以后的省电
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetBroadcastReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);//注册广播接收器，接收CONNECTIVITY_CHANGE这个广播

        this.netModel = NetBroadcastReceiverUtils.getNetworkState(this);
        return isNetConnect();
    }

    /**
     * 网络变化之后的类型
     *
     * @param netModel 网络类型netModel
     */
    @Override
    public void onNetChange(int netModel) {
        // TODO Auto-generated method stub
        this.netModel = netModel;
        isNetConnect();
    }

    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect() {
        if (netModel == 1) {
            return true;
        } else if (netModel == 0) {
            return true;
        } else if (netModel == -1) {
            return false;
        }
        return false;
    }

    /**
     * 将 {@link Disposable} 添加到 {@link CompositeDisposable} 中统一管理
     * 可在 Activity#onDestroy() 中使用 #unDispose() 停止正在执行的 RxJava 任务, 避免内存泄漏(框架已自行处理)
     *
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
    protected void onDestroy() {
        super.onDestroy();
        // 将其子activity从activity采集器中移除
        ActivityTaskManager.getInstance().removePopActivity(this);
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        unDispose(true);
    }

    /**
     * 可以覆盖以下方法,设置动画
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.aim_common_right_in,
                R.anim.aim_common_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.aim_common_right_in,
                R.anim.aim_common_zoom_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.aim_common_left_in,
                R.anim.aim_common_right_out);
    }

    public String getViewValue(View view) {
        if (view instanceof EditText) {
            return ((EditText) view).getText().toString();
        } else if (view instanceof TextView) {
            return ((TextView) view).getText().toString();
        }
        return null;
    }

    /**
     * 设置沉浸式状态栏
     *
     * @param lightMode  是否浅色模式,浅色时状态栏文字图标是黑色，其他情况下是白色
     * @param fullScreen 是否填充满屏幕 false时会把状态栏的高度留出来
     */
    protected void fitStatusBar(boolean lightMode, boolean fullScreen) {
        ImmersiveManage.fitStatusBar(this, lightMode, fullScreen);
    }

    protected void fitNavigationBar(int colorId) {
        ImmersiveManage.fitNavigationBar(this, colorId);
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        if (resources != null) {
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            if (displayMetrics.scaledDensity != displayMetrics.density) {
                displayMetrics.scaledDensity = displayMetrics.density;
            }
        }
        return resources;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Change the style of the status bar.
     *
     * @param color 颜色
     */
    protected void changeStatusBarStyle(int color) {
        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        View statusBarView = new View(mContext);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LaBitmap.INSTANCE.getStatusBarHeight());
        statusBarView.setBackgroundColor(color);
        decorView.addView(statusBarView, lp);
    }

    /**
     * 设置全屏的状态
     *
     * @param fullScreen 是否是全屏显示
     */
    protected void changeFullScreenState(boolean fullScreen) {
        mFullScreen = fullScreen;
    }

    /**
     * 如果项目中有用到Toolbar，则可以调用这个方法初始化
     *
     * @param toolbar           toolbar
     * @param isHomeAsUpEnabled ishomeasupenable
     */
    protected void initToolbar(Toolbar toolbar, boolean isHomeAsUpEnabled) {
        toolbar.setPadding(toolbar.getPaddingLeft(), (int) getResources().getDimension(R.dimen.AppBarPatchMarginTop), toolbar.getPaddingRight(), toolbar.getPaddingBottom());
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(isHomeAsUpEnabled);
    }

    /**
     * 如果布局文件中用了inaBarlayout，可以直接调用这个方法设置标题
     *
     * @param title 标题内容
     */
    protected void setAppBarTitle(String title) {
        inaBarlayout = findViewById(R.id.inaBarlayout);
        if (inaBarlayout != null) {
            inaBarlayout.setTitle(title);
        }
    }

    public INABarLayout getInaBarlayout() {
        if (inaBarlayout == null) {
            inaBarlayout = findViewById(R.id.inaBarlayout);
        }
        return inaBarlayout;
    }

    protected INAStatusLayout getInaStatusLayout() {
        if (inaStatusLayout == null) {
            inaStatusLayout = findViewById(R.id.inaStatusLayout);
        }
        return inaStatusLayout;
    }

    /**
     * 隐藏软键盘
     */
    protected void hideSoftKeyboard() {
        ViewUtils.getInstance().hideSoftKeyboard(this);
    }

    /**
     * 应该返回到上个界面，should how much i love u
     *
     * @param view 返回点击View
     */
    public void back(View view) {
        hideSoftKeyboard();
        finish();
    }

    @Override
    public void onBackPressed() {
        hideSoftKeyboard();
        finish();
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
        runOnUiThread(() -> {
            Snackbar.make(ytipsView, msg, Snackbar.LENGTH_SHORT).show();
//                setAction("Undo",
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Toast.makeText(getApplication(), "请输入内容后再试试", Toast.LENGTH_SHORT).show();
//                            }
//                        })
        });
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
        LoadingDialog.showLoading(this, config);
    }

    public void dismissLoadding() {
        LoadingDialog.dismiss();
        LADialog.INSTANCE.cancelDialog();
    }

    public interface TimerListener {
        void onLaunchedTimer();
    }

    public interface ScheduleListener {
        void onScheduledTimer();
    }

    private void setTimerListener(TimerListener timerListener) {
        this.timerListener = timerListener;
    }

    private void setScheduleListener(ScheduleListener scheduleListener) {
        this.scheduleListener = scheduleListener;
    }

    public void onLaunchedTimer() {
        // your owner implement
    }

    public void onScheduledTimer() {
        // your owner implement
    }

    protected void launchTimer(int delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != timerListener)
                    timerListener.onLaunchedTimer();
            }
        }, delayMillis);
    }

    protected void scheduleTimer(int delayMillis) {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (null != scheduleListener)
                        scheduleListener.onScheduledTimer();
                }
            }, 0, delayMillis);
        }
    }

    protected void scheduleMillisTimer() {
        scheduleTimer(1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void setFinishOnTouchOutside(boolean finish) {
        super.setFinishOnTouchOutside(finish);
    }

    public abstract void onVoice(String type, String words);
}