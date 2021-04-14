package com.sunsta.bear.faster;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sunsta.bear.AnConstants;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2016/8/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 2.0 |   2019年7月2日11:44             |   枚举单列类验证android开发中数据的合法性以及转化数据服务
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class LADateTime {
    /**
     * yyyy-MM-dd HH:mm:ss标准日期时间格式，默认是英文格式，默认返回format就是此类
     * <p>
     * 其中时间显示是12小时制的，如果需要显示24小时制，只需将hh换成kk
     * <p>
     * millisecond:毫秒; 千分之一秒
     * millis -- 这是以毫秒为单位的休眠时间
     * nanos -- 这是0-999999附加的纳秒睡眠时间
     * <p>
     * 一般的：setDateFormat()这种命令格式
     */
    private static final String TAG = "LADateTime";

    public static final String PATTERN_DATE_TIME_WEEK = "yyyy-MM-dd hh:mm, EE";
    public static final String PATTERN_DATE_TIME_YMDHMS_ZH = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE_TIME_FOLDER = "yyyyMMdd_HHmmssSS";
    public static final String PATTERN_DATE_TIME_FILE = "yyyyMMdd_HH:mm:ssSS";
    public static final String PATTERN_DATE = "yyyy/MM/dd";
    public static final SimpleDateFormat FORMAT_EN_TIME_HMS = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    public static final SimpleDateFormat FORMAT_EN_TIME_HMSS = new SimpleDateFormat("HH:mm:ss SSS", Locale.ENGLISH);
    public static final SimpleDateFormat FORMAT_EN_TIME_MS = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
    public static final SimpleDateFormat FORMAT_EN_TIME_H = new SimpleDateFormat("HH", Locale.ENGLISH);
    public static final SimpleDateFormat FORMAT_EN_DATE_YMD = new SimpleDateFormat(PATTERN_DATE, Locale.ENGLISH);
    public static final SimpleDateFormat DEFAUTL_DATE_TIME_FORMAT = new SimpleDateFormat(DEFAULT_DATE_TIME_PATTERN, LALocale.getDefault());
    public static final SimpleDateFormat FORMAT_EN_DATE_TIME_YMDHMSS = new SimpleDateFormat("yyyyMMdd-HH:mm:ss:SSS", Locale.ENGLISH);
    public static final SimpleDateFormat FORMAT_EN_DATE_TIME_YMDHM = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.ENGLISH);
    public static final SimpleDateFormat FORMAT_EN_DATE_TIME_YMDHMS_POINT = new SimpleDateFormat("yyyy.MM.dd. HH:mm:ss", Locale.ENGLISH);

    private LADateTime() {
    }

    public static LADateTime getInstance() {
        return LaEnumDateTime.INSTANCE.getInstance();
    }

    private enum LaEnumDateTime {
        INSTANCE;
        private LADateTime laInstance;

        LaEnumDateTime() {
            laInstance = new LADateTime();
        }

        private LADateTime getInstance() {
            return laInstance;
        }
    }

    /**
     * 判断系统使用的是24小时制还是12小时制
     */
    public boolean is24HourFormat(Context mContext) {
        return DateFormat.is24HourFormat(mContext);
    }

    /**
     * 判断系统的时区是否是自动获取
     */
    public boolean isTimeZoneAuto(Context mContext) {
        try {
            return Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME_ZONE) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断系统的时间是否自动获取的
     */
    public boolean isDateTimeAuto(Context mContext) {
        try {
            return Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置系统的时区是否自动获取
     */
    public void setAutoTimeZone(Context mContext, int checked) {
        Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, checked);
    }

    /**
     * 设置系统的时间是否需要自动获取
     */
    public void setAutoDateTime(int checked, Context mContext) {
        Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME, checked);
    }

    /**
     * 设置时间显示格式，如果为12则修改为24(必须要这个权限才可以)
     * ps:this permission: android.permission.WRITE_SETTINGS.
     */
    public void setTimeFormat(Context mContext) {
        if (is24HourFormat(mContext)) {
            set12Format(mContext);
        } else {
            set24Format(mContext);
        }
    }

    public void set24Format(Context mContext) {
        Settings.System.putString(mContext.getContentResolver(), Settings.System.TIME_12_24, "24");
    }

    public void set12Format(Context mContext) {
        Settings.System.putString(mContext.getContentResolver(), Settings.System.TIME_12_24, "12");
    }

    /**
     * 设置日期显示格式，android手表上设置(######)ok
     * <p>
     * 参数可以传：{@link #PATTERN_DATE}
     * ps:this permission: android.permission.WRITE_SETTINGS.
     */
    public boolean setDateFormatInResolver(Context mContext, String pattern) {
        ContentResolver cv = mContext.getContentResolver();
        String getDateFormate = Settings.System.getString(cv, Settings.System.DATE_FORMAT);
        Log.d("sunst88" + TAG, "getDateFormate=" + getDateFormate);
        return Settings.System.putString(cv, Settings.System.DATE_FORMAT, pattern);
    }

    /**
     * 设置日期显示会实例化一个DateFormat实例，这个实例在format需要三个参数，(######)
     * 时间,Locale(指定使用那个语言字串，默认是系统语言),显示模式(format string)：
     * <p>######)
     * 结果：pattern = PATTERN_DATE_TIME_WEEK
     * <p>
     * return=1970-01-01 08:00, 周四
     */
    public String setDateFormatInClock(String pattern) {
        long time = SystemClock.currentThreadTimeMillis();
        Log.d("sunst888" + TAG, "into setDateFormatInClock : time=" + time);
        return getSimpleDateFormat(pattern, new Locale("zh")).format(getDate(time));
    }

    /**
     * 设置日期显示格式(######)
     * <item>MM-dd-yyyy</item>
     * <item>dd-MM-yyyy</item>
     * <item>yyyy-MM-dd</item>
     * <item>EE-MMM-d-yyyy</item>
     * <item>EE-d-MMM-yyyy</item>
     * <item>yyyy-MMM-d-EE</item>
     * <p>
     * return =time=01-13-2019
     */
    public String setDateFormat(String pattern, Locale local) {
        return getSimpleDateFormat(pattern, local).format(getCalendar().getTime());
    }

    /**
     * 设置日期时间需要权限 (######)
     * <uses-permission android:name="android.permission.SET_TIME" />
     */
    public boolean setDateTime(long millis) {
        return SystemClock.setCurrentTimeMillis(millis);
    }

    /**
     * 参考系统Settings中的源码--设置系统日期(######)
     */
    public void setSysDate(Context mContext, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) Objects.requireNonNull(mContext.getSystemService(Context.ALARM_SERVICE))).setTime(when);
        }
    }

    public void setDate(int year, int month, int day) {
        Calendar calendar = getCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long when = calendar.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
        long now = Calendar.getInstance().getTimeInMillis();
        Log.d("sunst88" + TAG, "when" + when + "now" + now);
        if (now - when > 1000) {
            Log.d("sunst88" + TAG, "failed to set Date.");
        }
    }

    public void setTime(int hour, int minute) {
        Calendar calendar = getCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        long when = getCalendar().getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
        long now = Calendar.getInstance().getTimeInMillis();
        Log.d("sunst88" + TAG, "when" + when + "now" + now);
        if (now - when > 1000) {
            Log.d("sunst88" + TAG, "failed to set Time.");
        }
    }

    /**
     * 参考系统Settings中的源码--设置系统时间 (######)
     */
    public void setSysTime(Context mContext, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) Objects.requireNonNull(mContext.getSystemService(ALARM_SERVICE))).setTime(when);
        }
    }

    /**
     * 高级方法，需要root和su级别用户，适用于我们OOBE(######)
     * <p>
     * 20181023.112800"; //测试的设置的时间【时间格式 yyyyMMdd.HHmmss
     */
    public void setDateTimeWithCommand(String datetime) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s " + datetime + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置系统时区(######)
     * timeZone = "GMT+8:00"
     * timeZone = "Asia/Shanghai"
     */
    public void setTimeZoneInCalendar(String timeZone) {
        getCalendar().setTimeZone(getTimeZone(timeZone));
    }

    /**
     * 设置系统时区(######)
     * TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
     */
    public void setTimeZoneInCalendar(TimeZone tz) {
        getCalendar().setTimeZone(tz);
        int year = getCalendar().get(Calendar.YEAR);
        int month = getCalendar().get(Calendar.MONTH) + 1;
        int day = getCalendar().get(Calendar.DAY_OF_MONTH);
        int hour = getCalendar().get(Calendar.HOUR_OF_DAY);
        int minute = getCalendar().get(Calendar.MINUTE);
        Log.d("sunst88" + TAG, year + "年" + month + "月" + day + "日 " + hour + ":" + minute);
    }

    /**
     * 获取Calendar(######)
     */
    public Calendar getCalendar() {
        return Calendar.getInstance();
    }

    /**
     * 输出：
     * 2013年01月10日 10时54分14秒
     * 1357786454640
     */
    public void test2() {
        SimpleDateFormat sdf = new SimpleDateFormat("", LALocale.SIMPLIFIED_CHINESE);
        sdf.applyPattern(PATTERN_DATE_TIME_YMDHMS_ZH);
        System.out.println(sdf.format(System.currentTimeMillis()));
        System.out.println(System.currentTimeMillis());

    }

    /**
     * test2中没有时区，很多时候时间是跟时区关联的，TimeZone类。
     * 显示2016-05-12 14:22:13。如果改GMT-8，则是本地北京时间，比GMT-0快8个小时。所以GMT-0时间的秒数，加上28800即是北京时间的秒数。
     */
    public void test3() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        TimeZone pst = TimeZone.getTimeZone("Etc/GMT-0");
        Date curDate = new Date();
        dateFormatter.setTimeZone(pst);
        System.out.println(dateFormatter.format(curDate));
    }

    /**
     * 设置时区(######)
     */
    public void setTimeZoneInFormat(TimeZone tz) {
        /*设置前打印*/
        Log.d("sunst888" + TAG, "setTimeZoneInFormat1: id=" + getDefaultTimeZoneId() + "name ==" + getDefaultTimeZoneName() + "offset=" + getDefaultTimeZoneOffset());
        SimpleDateFormat.getDateTimeInstance().setTimeZone(tz);
        /*设置后打印*/
        Log.d("sunst888" + TAG, "setTimeZoneInFormat2: id=" + getDefaultTimeZoneId() + "name ==" + getDefaultTimeZoneName() + "offset=" + getDefaultTimeZoneOffset());
    }

    /**
     * 设置时区(######)
     */
    public void setTimeZoneInFormat(String tz) {
        /*设置前打印*/
        Log.d("sunst888" + TAG, "setTimeZoneInFormat1: id=" + getDefaultTimeZoneId() + "name ==" + getDefaultTimeZoneName() + "offset=" + getDefaultTimeZoneOffset());
        SimpleDateFormat.getDateTimeInstance().setTimeZone(getTimeZone(tz));
        /*设置后打印*/
        Log.d("sunst888" + TAG, "setTimeZoneInFormat2: id=" + getDefaultTimeZoneId() + "name ==" + getDefaultTimeZoneName() + "offset=" + getDefaultTimeZoneOffset());
    }

    /**
     * 设置时区:AlarmManager(######)ok
     * <p> android6.0（M）以上版本对于Olson ID做了修改
     * not gmt+8，should be america/atikokan
     */
    public void setTimeZoneInAlarmManger(Context mContext, String timeZone) {
        AlarmManager alarm = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        assert alarm != null;
        Objects.requireNonNull(alarm).setTimeZone(timeZone);
    }

    /**
     * 设置时间:AlarmManager(######)ok
     */
    public void setTime(Context mContext, long millis) {
        AlarmManager alarm = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        assert alarm != null;
        Objects.requireNonNull(alarm).setTime(millis);
    }

    /**
     * 设置系统临时时区 (######)
     */
    public void setDefaultTimeZone(String timeZone) {
        TimeZone.setDefault(getTimeZone(timeZone));
    }

    /**
     * 设置系统临时时区 (######)
     */
    public void setDefaultTimeZone(TimeZone timeZone) {
        TimeZone.setDefault(timeZone);
    }

    /**
     * 获取系统当前的时区名称 (######)
     * 返回：中国标准时间
     */
    public String getDefaultTimeZoneName() {
        return getDefaultTimeZone().getDisplayName();
    }

    /**
     * 获取系统当前的时区offset 时差，返回值毫秒(######)
     * 返回：中国标准时间
     */
    public int getDefaultTimeZoneOffset() {
        return getDefaultTimeZone().getRawOffset();
    }

    /**
     * 获取系统当前的时区 (######)
     * 返回：中国标准时间
     */
    public TimeZone getDefaultTimeZone() {
        return TimeZone.getDefault();
    }

    /**
     * 获取的时区 (######)
     * 返回：中国标准时间
     */
    public TimeZone getTimeZone(String timeZone) {
        return TimeZone.getTimeZone(timeZone);
    }

    /**
     * 获取系统当前的时区ID (######)
     * 返回：getID()=Asia/Shanghai
     */
    public String getDefaultTimeZoneId() {
        return getDefaultTimeZone().getID();
    }

    /**
     * 超级方法：获取系统当前TimeZone偏移量 (######)
     * <p>
     * 以GMT + 05：30格林尼治偏移量返回
     */
    public String getCurrentGMTOffset() {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
        @SuppressLint("DefaultLocale") String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        return "GMT" + (offsetInMillis >= 0 ? "+" : "-") + offset;
    }

    /*todo*/
    public String timezone1() {
        TimeZone tz = TimeZone.getDefault();
        String gmt1 = (TimeZone.getTimeZone(tz.getID()).getDisplayName(false, TimeZone.SHORT));
        Log.d("sunst888", "timezone1:gmt1=" + gmt1);
        String gmt2 = TimeZone.getTimeZone(tz.getID()).getDisplayName(false, TimeZone.LONG);
        Log.d("sunst888", "timezone1:gmt2=" + gmt2);
        return gmt1;
    }

    public long timezone2() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();

        long hours = TimeUnit.MILLISECONDS.toHours(mGMTOffset);
        float minutes = (float) TimeUnit.MILLISECONDS.toMinutes(mGMTOffset - TimeUnit.HOURS.toMillis(hours)) / 3600000;
        Log.d("sunst888", "timezone2:hours=" + hours);
        Log.d("sunst888", "timezone2:minutes=" + minutes);
        long result = TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
        Log.d("sunst888", "timezone2:GMT offset is %s hours=" + result);
        return result;
    }

    /**
     * 一行解决方案是使用Z符号
     * pattern可以是：
     * Z/ZZ/ZZZ：-0800
     * ZZZZ：GMT-08：00
     * ZZZZZ：-08：00
     * 如果传Z:可能打印：GMT + 05:30
     */
    public String timezone3(String pattern) {
        String result = new SimpleDateFormat(pattern, Locale.getDefault()).format(getCurrentTimeStamp());
        Log.d("sunst888", "timezone3:result=" + result);
        return result;
    }

    /**
     * 可能返回+0530
     */
    public String timezone4() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        String timeZone = new SimpleDateFormat("Z", Locale.getDefault()).format(calendar.getTime());
        Log.d("sunst888", "timezone4:timeZone=" + timeZone);
        return timeZone.substring(0, 3) + ":" + timeZone.substring(3, 5);
    }

    /**
     * 这个解决方案的好处是避免了大量的模数学，字符串生成和解析。
     */
    public void timezone5() {
        ZoneOffset myOffset = ZonedDateTime.now().getOffset();
        ZoneOffset myOffset2 = ZoneOffset.from(ZonedDateTime.now());
        Log.d("sunst888", "timezone5:result=" + myOffset.getId());// should print "+HH:MM"
        Log.d("sunst888", "timezone5:result=" + myOffset2.getId());// should print "+HH:MM"
    }

//    如何应用时区和夏令时
//    public String applyGMTOffsetDST(long time) {
//        // Works out adjustments for timezone and daylight saving time
//        Calendar mCalendar = new GregorianCalendar();
//        TimeZone mTimeZone = mCalendar.getTimeZone();
//        boolean dstBool = mTimeZone.getDefault().inDaylightTime(new Date());
//        // add ali hour if DST active
//        if (dstBool == true) {
//            time = time + secondsPerHour;
//        }
//        // add offest hours
//        int mGMTOffset = mTimeZone.getRawOffset();
//        if (mGMTOffset > 0) {
//            long offsetSeconds = secondsPerHour * mGMTOffset;
//            time = time + offsetSeconds;
//        }
//        return time;
//    }

    /**
     * 获取GMT+8所有时区(也就是//东八区时区/北京时间) (######)
     */
    public void printAllBeijingTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        Log.d("sunst888" + TAG, "dateTime :getRawOffset()=" + tz.getRawOffset());
        Log.d("sunst888" + TAG, "dateTime :getID()=" + tz.getID());
        Log.d("sunst888" + TAG, "-------------------------");
        int rawOffset = 8;
        String[] ids = TimeZone.getAvailableIDs(rawOffset * 60 * 60 * 1000);
        for (String id : ids) {
            tz = TimeZone.getTimeZone(id);
            Log.d("sunst888" + TAG, "dateTime :getRawOffset()=" + tz.getRawOffset());
            Log.d("sunst888" + TAG, "dateTime :getID()=" + tz.getID());
        }
    }

    /**
     * GMT(格林威治标准时间)转换当前北京时间
     * 比如：1526217409 -->2018/5/13 21:16:49 与北京时间相差8个小时，调用下面的方法，是在1526217409加上8*3600秒
     * @return 北京标准时间
     */
    public String stamp2BeijingGMT(String timeStamp) {
        long lt = Long.parseLong(timeStamp) + 8 * 3600;
        return DEFAUTL_DATE_TIME_FORMAT.format(lt * 1000);
    }

    /**
     * 将时间转换为默认格式时间(######)
     */
    public String date2DefaultDateTime(Date date, String pattern) {
        return getDefaultSimpleDateFormat(pattern).format(date);
    }

    /**
     * 将时间转换为时间戳 (######)
     */
    public int date2Stamp(Date time) {
        return (int) (time.getTime() / 1000);
    }

    /**
     * 将时间转换为时间戳 (######)
     */
    public String date2Stamp(String dateTime) throws ParseException {
        Date date = DEFAUTL_DATE_TIME_FORMAT.parse(dateTime);
        assert date != null;
        long ts = date.getTime();
        return String.valueOf(ts);
    }

    /**
     * 将时间戳转换为时间(######)
     */
    public String timeStamp2String(long timeStamp) {
        return DEFAUTL_DATE_TIME_FORMAT.format(timeStamp * 1000);
    }

    /**
     * 将时间戳转换为时间 (######)
     */
    public String timeStamp2String(String timeStamp) {
        long timeInMillis = Long.valueOf(timeStamp);
        return DEFAUTL_DATE_TIME_FORMAT.format(getDate(timeInMillis));
    }

    /**
     * 将时间戳转换为时间 (######)
     */
    public String timeStamp2String(long timeStamp, String pattern) {
        return getDefaultSimpleDateFormat(pattern).format(timeStamp * 1000);
    }

    /**
     * 以下为得到SimpleDateFormat对象(######)
     */
    public SimpleDateFormat getDefaultSimpleDateFormat() {
        return new SimpleDateFormat(DEFAULT_DATE_TIME_PATTERN, LALocale.getDefault());
    }

    public SimpleDateFormat getDefaultSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern, LALocale.getDefault());
    }

    public SimpleDateFormat getDefaultSimpleDateFormat(Locale locale) {
        return new SimpleDateFormat(DEFAULT_DATE_TIME_PATTERN, locale);
    }

    public SimpleDateFormat getSimpleDateFormat(String pattern, Locale locale) {
        return new SimpleDateFormat(pattern, locale);
    }

    /**
     * 以下两个方法为获取精确到秒的时间戳(#######)
     * 通过String.substring()方法将最后的三位去掉
     * <p>
     * Date date = new Date(System.currentTimeMillis());
     */
    public int getSecondTimestampInSubstring(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0, length - 3));
        } else {
            return 0;
        }
    }

    /**
     * 通过整除将最后的三位去掉
     */
    public int getSecondTimestamp(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.valueOf(timestamp);
    }

    /**
     * get current secondtimestamp (#######)
     */
    public int getCurrentSecondTimestamp() {
        return getSecondTimestamp(getCurrentDate());
    }

    /**
     * get current date (#######)
     */
    public Date getCurrentDate() {
        return new Date(getCurrentTimeStamp());
    }

    /**
     * get date (#######)
     */
    public Date getDate(long timeInMillis) {
        return new Date(timeInMillis);
    }

    /**
     * get current time in milliseconds (#######)
     */
    public long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * long time to int (#######)
     */
    public int getCurrentHourInInt(long timeInMillis, SimpleDateFormat dateFormat) {
        String date = dateFormat.format(getDate(timeInMillis));
        int time = 0;
        if (!TextUtils.isEmpty(date)) {
            time = Integer.parseInt(date);
        }
        return time;
    }

    /**
     * long time to string (#######)
     */
    public String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(getDate(timeInMillis));
    }

    /**
     * long time to long (#######)
     */
    public long getCurrentDateInLong(long timeInMillis, SimpleDateFormat dateFormat) {
        String date = dateFormat.format(getDate(timeInMillis));
        long time = 0;
        if (!TextUtils.isEmpty(date)) {
            time = Long.parseLong(date);
        }
        return time;
    }

    /**
     * long time to string, format is {@link #DEFAUTL_DATE_TIME_FORMAT} (#######)
     */
    public String getTimeInDefaultFormat(long timeInMillis) {
        return getTime(timeInMillis, DEFAUTL_DATE_TIME_FORMAT);
    }

    /**
     * get current time in milliseconds (#######)
     */
    public String getCurrentTimeInString() {
        return getTimeInDefaultFormat(getCurrentTimeStamp());
    }

    /**
     * get current time in milliseconds (#######)
     */
    public String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeStamp(), dateFormat);
    }

    /**
     * get current time in milliseconds (#######)
     */
    public String getCurrentTimeInString(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 根据不同时区，转换时间 (#######)
     */
    public Date transDateInTimeZone(Date date, TimeZone oldZone, TimeZone newZone) {
        Date finalDate = null;
        if (date != null) {
            int timeOffset = oldZone.getOffset(date.getTime())
                    - newZone.getOffset(date.getTime());
            finalDate = new Date(date.getTime() - timeOffset);
        }
        return finalDate;
    }

    /**
     * 使用用户格式提取字符串日期
     * @param strDate 日期字符串 todo:这里存疑
     * @param pattern 日期格式
     * @return Date
     */
    public Date parse(@NonNull String strDate, @NonNull String pattern) {
        try {
            return getDefaultSimpleDateFormat(pattern).parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用用户格式格式化日期
     * @param date    日期 (#######)
     * @param pattern 日期格式
     * @return String转换后数据
     */
    public String format(Date date, String pattern) {
        return getDefaultSimpleDateFormat(pattern).format(date);
    }

    /**
     * 得到当前短日期时间(#######)
     * @return 如：(Point)2019年7月2日15:27
     */
    public String getShotDateTimeWithDate() {
        return FORMAT_EN_DATE_TIME_YMDHM.format(getCurrentDate());
    }

    /**
     * 得到当前长日期和时间 (#######)
     * @return 如：(Point)2019年7月2日15:30:58
     */
    public String getLongDateTimeWithDate() {
        return FORMAT_EN_DATE_TIME_YMDHMS_POINT.format(getCurrentDate());
    }

    /**
     * 得到当前长日期和时间 (#######)
     * @return 如：2019年7月2日15:30:58
     */
    public String getLongDateTime() {
        return DEFAUTL_DATE_TIME_FORMAT.format(getCurrentTimeStamp());
    }

    /**
     * 得到当前长日期和时间 (#######)
     * @return 如：2019年7月2日15:30:58
     */
    public String getLongDateTimeWithInstance() {
        return SimpleDateFormat.getDateTimeInstance().format(getCurrentTimeStamp());
    }

    /**
     * 得到当前长时间 (#######)
     * @return 如：15:33:57
     */
    public String getCurrentLongTime() {
        return FORMAT_EN_TIME_HMSS.format(getCurrentDate());
    }

    /**
     * 得到当前日期 (#######)
     * @return 如：2019-6-17
     */
    public String getDateWithInstance() {
        return SimpleDateFormat.getDateInstance().format(getCurrentDate());
    }

    /**
     * 获取当前Date对象 (#######)
     * @return Date    返回类型
     */
    public Date getDateWithCalendar() {
        Calendar c = getCalendar();
        Date date = new Date();
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day);
        return c.getTime();
    }

    /**
     * 计算秒有多少小时
     * @param second
     */
    public int getHours(long second) {
        int h = 00;
        if (second > 3600) {
            h = (int) (second / 3600);
        }
        return h;
    }

    /**
     * 计算秒有多少分
     * @param second
     */
    public int getMins(long second) {
        int d = 00;
        long temp = second % 3600;
        if (second > 3600) {
            if (temp != 0) {
                if (temp > 60) {
                    d = (int) (temp / 60);
                }
            }
        } else {
            d = (int) (second / 60);
        }
        return d;
    }

    /**
     * 计算秒有多少秒
     * @param second
     */
    public int getSeconds(long second) {
        int s = 0;
        long temp = second % 3600;
        if (second > 3600) {
            if (temp != 0) {
                if (temp > 60) {
                    if (temp % 60 != 0) {
                        s = (int) (temp % 60);
                    }
                } else {
                    s = (int) temp;
                }
            }
        } else {
            if (second % 60 != 0) {
                s = (int) (second % 60);
            }
        }
        return s;
    }

    /**
     * 时间转换成 00:00:00 格式
     * @param second 秒
     */
    public String timeConvertHMS(int second) {
        int hour = 0;
        int minutes = 0;
        int sencond = 0;
        hour = getHours(second);
        minutes = getMins(second);
        sencond = getSeconds(second);
        return (hour < 10 ? ("0" + hour) : hour) + ":" + (minutes < 10 ? ("0" + minutes) : minutes) + ":" + (sencond < 10 ? ("0" + sencond) : sencond);
    }

    /**
     * 时间转换成 00:00 格式
     * @param second 秒
     */
    public String timeConvertMS(int second) {
        int minutes = 0;
        int sencond = 0;
        minutes = getMins(second);
        sencond = getSeconds(second);
        return (minutes < 10 ? ("0" + minutes) : minutes) + ":" + (sencond < 10 ? ("0" + sencond) : sencond);
    }

    /**
     * 时间转换成 00 格式
     * @param second 秒
     */
    public String timeConvertSecond(int second) {
        int sencond = 0;
        sencond = getSeconds(second);
        return sencond < 10 ? ("0" + sencond) : sencond + AnConstants.EMPTY;
    }

    /**
     * 时间转换成 xx时xx分xx秒 格式
     * @param second 秒
     */
    public String timeConvertHMS_zh(int second) {
        return getHours(second) + "时" + getMins(second) + "分" + getSeconds(second) + "秒";
    }

    public String timeConvertMS_zh(int second) {
        return getMins(second) + "分" + getSeconds(second) + "秒";
    }

    public String timeConvertSecond_zh(int second) {
        return getSeconds(second) + "秒";
    }
}