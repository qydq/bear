package com.sunsta.bear.contract;

import com.sunsta.bear.presenter.BasePresenter;
import com.sunsta.bear.view.BaseView;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：夜间模式设置主题契约类
 * <a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以通过关注我的知乎获取更详细的信息</a>
 * <h3>版权声明：(C) 2016 The Android Developer Sunst</h3>
 * <br>创建日期：2016/12/7
 * <br>邮件email：qyddai@gmail.com
 * <br>个人Github：https://qydq.github.io
 * <p>--#---- Revision History:  --- >  : |version|date|updateinfo|----#--
 * @author sunst
 * @version 1.0 |   2016/12/17           |   夜间模式设置主题契约类
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public interface TaskDayNightContract {
    interface View extends BaseView<Presenter> {
        void _initTheme();//初始化主题背景

        void initDayModel();//主题切换设置白天模式

        void initNightModel();//主题切换设置夜间模式

        void changeThemeByZhiHu();//使用知乎的套路来改变主题背景

        void refreshUI();//设置模式后刷新UI

        void showAnimation();//设置模式启动动画

    }

    interface Presenter extends BasePresenter {
        boolean isDay();//是白天模式

        boolean isNight();//是夜晚模式

        boolean setDayModel();//设置白天模式

        boolean setNightModel();//设置夜晚模式
    }

}