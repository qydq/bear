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
package com.sunsta.bear;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.multidex.MultiDex;

import com.sunsta.bear.faster.FileUtils;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.SPUtils;
import com.sunsta.bear.faster.ValueOf;

import java.lang.reflect.InvocationTargetException;

import io.reactivex.plugins.RxJavaPlugins;

/**
 * <h2>请关注个人知乎Bgwan， 在【an系列】专栏会有本【livery框架】的使用案例（20190922-正在持续更新中...</h2>
 * 中文描述：初始版本-应用主类 * <br/>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2016/8/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 2.0 |   2016/8/18           |   初始版本-应用主类
 */
/*特别提示，可以继承MultiDexApplication*/
public class AnApplication extends Application {
    private static AnApplication instance;
    public static SharedPreferences sp;


    private static AnApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
        }
//        if (instance == null) {
//            instance = new AnApplication();
//        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            sp = FileUtils.INSTANCE.getSharedPreferences(this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sp = FileUtils.INSTANCE.getDefaultSharedPreferences(this);
        }

        /**
         * mark : fix error : [standard]
         *
         * fix the wrong：RxJava OnErrorNotImplementedException
         * 正常网络环境下错误很难复现，但是当项目上线用户基数变大，appp使用场景丰富，各种网络状况，在bugly上会报错，所以这里建议项目继承AnApplication
         * */
        RxJavaPlugins.setErrorHandler(throwable -> {
            SPUtils.getInstance().putBoolean(AnConstants.KEY.APP_AN, true);
            throwable.printStackTrace();
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, throwable.getClass().toString(), throwable.getMessage()));
        });
        setTheme(R.style.DayNightTheme);//初始化默认设置白天主题，防止到不到anBackgroundColor
    }

    public static void alispeak(int type, String worls) {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }

    public static Application getApplication() {
        return getInstance() == null ? getApplicationByReflect() : getInstance();
    }
}
