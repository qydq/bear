package com.sunsta.bear.faster;


import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：国际化相关类
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 2.0 |   2019/7/9 18:53             |   增加国际化语言相关初始化
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class LALocale {
    private static final String TAG = "LocaleUtils";
    /**
     * 中文
     */
    public static final Locale CHINESE = Locale.CHINESE;

    /**
     * 中文
     */
    public static final Locale SIMPLIFIED_CHINESE = Locale.SIMPLIFIED_CHINESE;

    /**
     * 繁体
     */
    public static final Locale TRADITIONAL_CHINESE = Locale.TRADITIONAL_CHINESE;
    /**
     * 英文
     */
    public static final Locale ENGLISH = Locale.ENGLISH;
    /**
     * 俄文
     */
    public static final Locale LOCALE_RUSSIAN = new Locale("ru");
    /**
     * 保存SharedPreferences的文件名
     */
    private static final String LOCALE_FILE = "LOCALE_FILE";
    /**
     * 保存Locale的key
     */
    private static final String LOCALE_KEY = "LOCALE_KEY";

    /**
     * 获取当前的Locale
     * @param context Context
     * @return Locale
     */
    public static Locale getCurrentLocale(Context context) {
        Locale _Locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0有多语言设置获取顶部的语言
            _Locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            _Locale = context.getResources().getConfiguration().locale;
        }
        return _Locale;
    }

    public static Locale getDefault() {
        return Locale.getDefault();
    }

    /**
     * 更新Locale
     * @param context   Context
     * @param newLocale New User Locale
     */
    public static void updateLocale(Context context, Locale newLocale) {
        if (needUpdateLocale(context, newLocale)) {
            //可以试着加默认的设置
            Locale.setDefault(newLocale);

            Configuration configuration = context.getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(newLocale);
            } else {
                configuration.locale = newLocale;
            }
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            context.getResources().updateConfiguration(configuration, displayMetrics);
        }
    }

    /**
     * 判断需不需要更新
     * @param pContext       Context
     * @param pNewUserLocale New User Locale
     * @return true / false
     */
    public static boolean needUpdateLocale(Context pContext, Locale pNewUserLocale) {
        return pNewUserLocale != null && !getCurrentLocale(pContext).equals(pNewUserLocale);
    }

    /**
     * 获取当前系统支持的语言
     */
    public static List<String> getSystemLanguage() {
        Locale[] locales = Locale.getAvailableLocales();
        List<String> lists = new ArrayList<>();
        for (Locale locale : locales) {
            // （国家/地区，中国/香港）
            String lang = locale.getLanguage() + "-" + locale.getCountry();
            Log.d(TAG, "getSystemLanguage=" + lang);
            lists.add(lang);
        }
        return lists;
    }

    /**
     * 获取当前系统使用语言
     * @param context Context
     */
    public static void language(Context context) {
        Locale locale;
        /*使用 locale = Locale.getDefault(); 不需要考虑接口deprecated问题*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
//        String lang = locale.getLanguage();
        // （国家/地区，中国/香港）
        String lang = locale.getLanguage() + "-" + locale.getCountry();
        Log.d(TAG, lang);

        Log.d(TAG, "--------------------------------");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = context.getResources().getConfiguration().getLocales();
            for (int i = 0; i < localeList.size(); i++) {
                Log.d(TAG, i + " >1> " + localeList.get(i).getLanguage() + "-" + localeList.get(i).getCountry());
            }

            LocaleList localeList2 = LocaleList.getAdjustedDefault();
            for (int i = 0; i < localeList2.size(); i++) {
                Log.d(TAG, i + " >2> " + localeList2.get(i).getLanguage() + "-" + localeList2.get(i).getCountry());
            }

            LocaleList localeList3 = LocaleList.getDefault();
            for (int i = 0; i < localeList3.size(); i++) {
                Log.d(TAG, i + " >3> " + localeList3.get(i).getLanguage() + "-" + localeList3.get(i).getCountry());
            }

            LocaleList localeList4 = LocaleList.getEmptyLocaleList();
            for (int i = 0; i < localeList4.size(); i++) {
                Log.d(TAG, i + " >4> " + localeList4.get(i).getLanguage() + "-" + localeList4.get(i).getCountry());
            }
        }
    }

    /**
     * 增加一个ok
     */
    public static void changeLanguage(Locale locale, boolean isUpdate) {
        try {
            Class<?> clzIActMag = Class.forName("android.app.IActivityManager");
            Class<?> clzActMagNative = Class.forName("android.app.ActivityManagerNative");
            Method mtdActMagNative$getDefault = clzActMagNative.getDeclaredMethod("getDefault");
            Object objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
            Method mtdIActMag$getConfiguration = clzIActMag.getDeclaredMethod("getConfiguration");
            Configuration config = (Configuration) mtdIActMag$getConfiguration.invoke(objIActMag);
            if (null != locale) {
                config.locale = locale;
            }
            if (isUpdate) {
                Class<?>[] clzParams = {Configuration.class};
                Method mtdIActMag$updateConfiguration = clzIActMag.getDeclaredMethod("updateConfiguration", clzParams);
                mtdIActMag$updateConfiguration.invoke(objIActMag, config);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 改变系统语言,android 6.0  ok
     * @param locale LocaleList
     */
    public static void changeSystemLanguage(Locale locale) {
        if (locale != null) {
            try {
                Class classActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
                Method getDefault = classActivityManagerNative.getDeclaredMethod("getDefault");
                Object objIActivityManager = getDefault.invoke(classActivityManagerNative);
                Class classIActivityManager = Class.forName("android.app.IActivityManager");
                Method getConfiguration = classIActivityManager.getDeclaredMethod("getConfiguration");
                Configuration config = (Configuration) getConfiguration.invoke(objIActivityManager);
                assert config != null;
                config.setLocale(locale);
                //config.userSetLocale = true;
                Class clzConfig = Class.forName("android.content.res.Configuration");
                Field userSetLocale = clzConfig
                        .getField("userSetLocale");
                userSetLocale.set(config, true);
                Class[] clzParams = {Configuration.class};
                Method updateConfiguration = classIActivityManager.getDeclaredMethod("updateConfiguration", clzParams);
                updateConfiguration.invoke(objIActivityManager, config);
                BackupManager.dataChanged("com.android.providers.settings");
            } catch (Exception e) {
                Log.d(TAG, "changeSystemLanguage1: " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * 改变系统语言,android 8.0
     * @param locale LocaleList
     *               <p>
     *               <p>
     *               LocaleList locale = new LocaleList(LocaleUtils.LOCALE_CHINESE);
     */
    public static void changeSystemLanguage(LocaleList locale) {
        if (locale != null) {
            try {
                Class classActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
                Method getDefault = classActivityManagerNative.getDeclaredMethod("getDefault");
                Object objIActivityManager = getDefault.invoke(classActivityManagerNative);
                Class classIActivityManager = Class.forName("android.app.IActivityManager");
                Method getConfiguration = classIActivityManager.getDeclaredMethod("getConfiguration");
                Configuration config = (Configuration) getConfiguration.invoke(objIActivityManager);
                config.setLocales(locale);
                Class[] clzParams = {Configuration.class};
                Method updateConfiguration = classIActivityManager.getDeclaredMethod("updatePersistentConfiguration", clzParams);
                updateConfiguration.invoke(objIActivityManager, config);
                BackupManager.dataChanged("com.android.providers.settings");
            } catch (Exception e) {
                Log.d(TAG, "changeSystemLanguage2: " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * android如果失败可以尝试最新方法，下面ok
     */
    public static void newSystemLanguage(Locale locale) {
        Class amnClass = null;
        try {
            amnClass = Class.forName("android.app.ActivityManagerNative");
            Object amn = null;
            Configuration config = null;
            // amn = ActivityManagerNative.getDefault();
            Method methodGetDefault = amnClass.getMethod("getDefault");
            methodGetDefault.setAccessible(true);
            amn = methodGetDefault.invoke(amnClass);

            // config = amn.getConfiguration();
            Method methodGetConfiguration = amnClass.getMethod("getConfiguration");
            methodGetConfiguration.setAccessible(true);
            config = (Configuration) methodGetConfiguration.invoke(amn);

            // config.userSetLocale = true;
            Class configClass = config.getClass();
            Field f = configClass.getField("userSetLocale");
            f.setBoolean(config, true);

            // set the locale to the new value
            config.locale = locale;
            config.setLocale(locale);
            // amn.updateConfiguration(config);
            Method methodUpdateConfiguration = amnClass.getMethod("updateConfiguration", Configuration.class);
            methodUpdateConfiguration.setAccessible(true);
            methodUpdateConfiguration.invoke(amn, config);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * android6.0含BackupManager  ，下面ok
     */
    public static void setSystemLanguage(Locale locale) {
        try {
            Object objIActMag;
            Class clzIActMag = Class.forName("android.app.IActivityManager");
            Class clzActMagNative = Class.forName("android.app.ActivityManagerNative");
            Method mtdActMagNative$getDefault = clzActMagNative.getDeclaredMethod("getDefault");
            objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
            Method mtdIActMag$getConfiguration = clzIActMag.getDeclaredMethod("getConfiguration");
            Configuration config = (Configuration) mtdIActMag$getConfiguration.invoke(objIActMag);
            config.locale = locale;
            Class clzConfig = Class.forName("android.content.res.Configuration");
            Field userSetLocale = clzConfig.getField("userSetLocale");
            userSetLocale.set(config, true);
            Class[] clzParams = {Configuration.class};
            Method mtdIActMag$updateConfiguration = clzIActMag.getDeclaredMethod("updateConfiguration", clzParams);
            mtdIActMag$updateConfiguration.invoke(objIActMag, config);
            BackupManager.dataChanged("com.android.providers.settings");
        } catch (Exception e) {
            Log.d(TAG, "changeSystemLanguage4: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void surperLanguage() {
//        Locale locale = Locale.CHINA;//中文
        Locale newLocale = new Locale("zh", "TW");
        final LocaleList localeList = new LocaleList(newLocale);

//        newSystemLanguage(newLocale);
//        setSystemLanguage(newLocale);
//        changeLanguage(newLocale, true);
//        changeSystemLanguage(newLocale);
        changeSystemLanguage(localeList);
    }

    /**
     * 点击确认，保存当前设置的语言，并且设置系统的语言属性，以及保存进入当前这个页面
     */
    public static void saveUserLocale(Context context, String saveLanguage) {
        if (SPUtils.getInstance().putString(SPUtils.LA_DEFAULT_KEY, saveLanguage)) {
            Log.d("sunst88" + TAG, "数据已经保存");
        }
    }
}