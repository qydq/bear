package com.sunsta.bear.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.contract.DayNightTheme;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.SPUtils;
import com.sunsta.bear.layout.ParallaxBackActivityHelper;
import com.sunsta.bear.layout.ParallaxBackLayout;
import com.sunsta.bear.model.entity.ResponseDayNightMode;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * </p><p>
 * * 替代EventBus及RxBus
 * * 代码源自：Android消息总线的演进之路：用LiveDataBus替代RxBus、EventBus
 * * https://mp.weixin.qq.com/s?__biz=MjM5NjQ5MTI5OA==&mid=2651748475&idx=4&sn=8feb14dd49ce79726ecf12eb6c243740&chksm=bd12a1368a652820df7c556182d3494d84ae38d4aee4e84c48c227aa5083ebf2b1a0150cf1b5&mpshare=1&scene=1&srcid=1010fzmNILeVVxi5HsAG8R17#rd
 * *
 * * 基本使用：
 * * 注册订阅：
 * * LiveDataBus.get().getChannel("key_test", Boolean.class)
 * *         .observe(this, new Observer<Boolean>() {
 * *             @Override
 * *             public void onChanged(@Nullable Boolean aBoolean) {
 * *             }
 * *         });
 * * 发送消息：
 * * LiveDataBus.get().getChannel("key_test").setValue(true);
 * </p>
 * 中文描述：mvp的BaseView的接口类
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 *
 * @author sunst // sunst0069
 * @version 1.0 |   2017年3月23日15:42:16         |   mvp的BaseView的接口类
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public abstract class ParallaxActivity extends BaseActivity implements DayNightTheme {
    private ParallaxBackActivityHelper mHelper;
    protected Context mContext;
    protected Window mWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO 固件化的操作
        super.onCreate(savedInstanceState);
        //环信集成功能，暂未开启。
//		EMChat.getInstance().init(this.getApplicationContext());
        mContext = this;
        mHelper = new ParallaxBackActivityHelper(this);
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
        setTheme(R.style.DayNightTheme);
        /*
         * LAUi 设置状态栏属性，后期修改该类为单例模式 的枚举类型
         * */
        mWindow = getWindow();
        //初始化视图
        initView(savedInstanceState);

        inaBarlayout = getInaBarlayout();
        inaStatusLayout = getInaStatusLayout();
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    /*fix need cast findview by id 晴雨2018*/
    public <T extends View> T findViewById(@IdRes int id) {
        View view = getDelegate().findViewById(id);
        if (view == null && mHelper != null)
            return mHelper.findViewById(id);
        return getDelegate().findViewById(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper.onActivityDestroy();
    }

    public ParallaxBackLayout getBackLayout() {
        return mHelper.getBackLayout();
    }

    public void setBackEnable(boolean enable) {
        mHelper.setBackEnable(enable);
    }

    public void scrollToFinishActivity() {
        mHelper.scrollToFinishActivity();
    }

    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            scrollToFinishActivity();
        }
    }

    /**
     * 页面voice监听
     */
    @Override
    public void onVoice(String type, String words) {

    }

    /**
     * 各种对象、组件的初始化
     */
    protected abstract void initView(Bundle savedInstanceState);
}