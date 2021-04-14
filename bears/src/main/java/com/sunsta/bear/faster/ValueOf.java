package com.sunsta.bear.faster;

import android.text.TextUtils;

import com.sunsta.bear.AnConstants;

/**
 * @author：luck
 * @date：2019-11-12 14:27
 * @describe：类型转换工具类
 */
public class ValueOf {
    public static boolean intercept = false;

    public static String toString(Object o) {
        String value = "";
        try {
            value = o.toString();
        } catch (Exception e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        }
        return value;
    }


    public static double toDouble(Object o) {

        return toDouble(o, 0);
    }

    public static double toDouble(Object o, int defaultValue) {
        if (o == null) {
            return defaultValue;
        }

        double value;
        try {
            value = Double.valueOf(o.toString().trim());
        } catch (Exception e) {
            value = defaultValue;
        }

        return value;
    }

    public static long toLong(Object o, long defaultValue) {
        if (o == null) {
            return defaultValue;
        }
        long value = 0;
        try {
            String s = o.toString().trim();
            if (s.contains(".")) {
                value = Long.valueOf(s.substring(0, s.lastIndexOf(".")));
            } else {
                value = Long.valueOf(s);
            }
        } catch (Exception e) {
            value = defaultValue;
        }


        return value;
    }

    public static long toLong(Object o) {
        return toLong(o, 0);
    }


    public static float toFloat(Object o, long defaultValue) {
        if (o == null) {
            return defaultValue;
        }
        float value = 0;
        try {
            String s = o.toString().trim();
            value = Float.valueOf(s);
        } catch (Exception e) {
            value = defaultValue;
        }


        return value;
    }

    public static float toFloat(Object o) {
        return toFloat(o, 0);
    }


    public static int toInt(Object o, int defaultValue) {
        if (o == null) {
            return defaultValue;
        }
        int value;
        try {
            String s = o.toString().trim();
            if (s.contains(".")) {
                value = Integer.valueOf(s.substring(0, s.lastIndexOf(".")));
            } else {
                value = Integer.valueOf(s);
            }
        } catch (Exception e) {
            value = defaultValue;
        }

        return value;
    }

    public static int toInt(Object o) {
        return toInt(o, 0);
    }

    public static boolean toBoolean(Object o) {
        return toBoolean(o, false);

    }


    public static boolean toBoolean(Object o, boolean defaultValue) {
        if (o == null) {
            return false;
        }
        boolean value;
        try {
            String s = o.toString().trim();
            if ("false".equals(s.trim())) {
                value = false;
            } else {
                value = true;
            }
        } catch (Exception e) {
            value = defaultValue;
        }

        return value;
    }


    public static <T> T to(Object o, T defaultValue) {
        if (o == null) {
            return defaultValue;
        }
        T value = (T) o;
        return (T) value;
    }

    /**
     * @param liveryType 错误或者提示类型，参考：LOG_LIVERY_EXCEPTION value
     * @param markTagof  具体异常或者提示的关键信息点，参考：Java.lang.SecurityException
     * @param suggestion 当产生该类问题给出的建议，参考：Need to declare android.permission.WRITE_EXTERNAL_STORAGE to call this api in your AndroidManifest.xml
     *                   2020-10-29 15:31:31.332 1244-1244/com.sunsta.liffang E/dota: savelogfiler==LOG_LIVERY_EXCEPTION:
     *                   Java.lang.SecurityException ：
     *                   Need to declare android.permission.WRITE_EXTERNAL_STORAGE to call this api in your AndroidManifest.xml
     */
    public static String logLivery(String liveryType, String markTagof, String suggestion) {
        if (intercept) {
            return AnConstants.EMPTY;
        }
        String filterLog = liveryType + "\n" + markTagof + "\n" + suggestion;
        String logFilter = SPUtils.getInstance().getString(AnConstants.KEY.LOG_FILTER);
        if (!SPUtils.getInstance().getBoolean(AnConstants.KEY.LOG_ENABLE, false)) {
            logFilter = AnConstants.EMPTY;
        }
        String logBuilder = TextUtils.isEmpty(logFilter) ? filterLog : logFilter + "_" + filterLog;
        return logBuilder.trim();
    }

    public static String logLivery(String liveryType, String markof) {
        return logLivery(liveryType, markof, AnConstants.EMPTY);
    }

    public static String logLivery(String markof) {
        if (intercept) {
            return AnConstants.EMPTY;
        }
        String logFilter = SPUtils.getInstance().getString(AnConstants.KEY.LOG_FILTER);
        if (!SPUtils.getInstance().getBoolean(AnConstants.KEY.LOG_ENABLE, false)) {
            logFilter = AnConstants.EMPTY;
        }
        String logBuilder = TextUtils.isEmpty(logFilter) ? AnConstants.VALUE.LOG_LIVERY + markof : logFilter + "_" + AnConstants.VALUE.LOG_LIVERY + markof;
        return logBuilder.trim();
    }
}