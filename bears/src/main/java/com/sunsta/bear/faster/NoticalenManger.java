package com.sunsta.bear.faster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.callback.OnStatusListener;

import java.util.Calendar;

import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static com.sunsta.bear.AnConstants.VALUE.LOG_LIVERY_EXCEPTION;

public class NoticalenManger {
    private Context mContext;
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder notificationCompatBuilder;
    private static final int PROGRESS_REQUEST_CODE = 100;
    private int APP_ICON = R.drawable.ic_color_menu_default;
    private static String CALENDER_URL = "content://com.android.calendar/calendars";
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = AnConstants.LIVERY_NAME;
    private static String CALENDARS_ACCOUNT_NAME = "qyddai@gmail.com";
    private static String CALENDARS_ACCOUNT_TYPE = "LiveryType";
    private static String CALENDARS_DISPLAY_NAME = "LiveryDisplayName";

    public NoticalenManger(Context context) {
        this.mContext = context;
        notificationManagerCompat = NotificationManagerCompat.from(mContext);
        notificationCompatBuilder = new NotificationCompat.Builder(mContext, getNotifyChannel());
    }

    public NotificationManagerCompat getNotificationManagerCompat() {
        return notificationManagerCompat;
    }

    public NotificationCompat.Builder getNotificationCompatBuilder() {
        return notificationCompatBuilder;
    }

    /**
     * android 8.0以上获取通知的渠道
     */
    public String getNotifyChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelID = SPUtils.getInstance().getString(AnConstants.KEY.CHANNEL_ID, StringUtils.getString(R.string.notice_channel_id));
            String channelNAME = SPUtils.getInstance().getString(AnConstants.KEY.CHANNEL_NAME, StringUtils.getString(R.string.notice_channel_name));
            int level = SPUtils.getInstance().getInt(AnConstants.KEY.CHANNEL_LEVEL, NotificationManagerCompat.IMPORTANCE_MAX);
            APP_ICON = SPUtils.getInstance().getInt(AnConstants.KEY.APP_ICON, R.drawable.ic_color_menu_default);
            NotificationChannel notificationChannel = new NotificationChannel(channelID, channelNAME, level);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
//            mNotificationManager.createNotificationChannel(notificationChannel);
            notificationManagerCompat.createNotificationChannel(notificationChannel);
            return channelID;
        } else {
            return AnConstants.EMPTY;
        }
    }

    private PendingIntent getPendingIntent(Class<?> cls) {
        Intent intent = new Intent();
        if (cls != null) {
            intent.setClass(mContext, cls);
        }
        return PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void autoOpenNotificationSetting() {
        autoOpenNotificationSetting(false);
    }

    public boolean areNotificationsEnabled() {
        return notificationManagerCompat != null && notificationManagerCompat.areNotificationsEnabled();
    }

    /**
     * 自动打开设置通知权限
     * @param localOpenStatus 本地是否设置过标志
     */
    public void autoOpenNotificationSetting(boolean localOpenStatus) {
        if (localOpenStatus) {
            return;
        }
        if (!areNotificationsEnabled()) {
            Intent intent = new Intent();
            try {
//                根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
//                小米6 - MIUI9 .6 - 8.0 .0 系统，是个特例，通知设置界面只能控制 "允许使用通知圆点"——然而这个玩意并没有卵用.
                if ("MI 6".equals(Build.MODEL)) {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                    intent.setData(uri);
                    intent.setAction("com.android.settings/.SubSettings");
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
//                        这种方案适用于 API 26, 即8 .0（含8 .0）以上可以用
                        intent.putExtra(EXTRA_APP_PACKAGE, mContext.getPackageName());
                        intent.putExtra(Settings.EXTRA_CHANNEL_ID, mContext.getApplicationInfo().uid);
                    } else {
//                        这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                        intent.putExtra("app_package", mContext.getPackageName());
                        intent.putExtra("app_uid", mContext.getApplicationInfo().uid);
                    }
                }
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                /*
                 *  出现异常则跳转到应用设置界面：锤子坚果3——OC105||https://blog.csdn.net/ysy950803/article/details/71910806
                 * */
                EasyPermission.openAppSettingsScreen(mContext);
            }
        }
    }

    public void addCalendarEvent(@NonNull String title, String description, long remindTime, int previousDate) {
        addCalendarEvent(title, description, remindTime, previousDate, null);
    }

    public void addCalendarEvent(@NonNull String title, long remindTime) {
        addCalendarEvent(title, AnConstants.EMPTY, remindTime, null);
    }

    public void addCalendarEvent(@NonNull String title, String description, long remindTime) {
        addCalendarEvent(title, description, remindTime, null);
    }

    public void addCalendarEvent(@NonNull String title, String description, long remindTime, OnStatusListener listener) {
        addCalendarEvent(title, description, remindTime, 0, listener);
    }

    public void addCalendarEvent(@NonNull String title, String description, long remindTime, int previousDate, OnStatusListener listener) {
        addCalendarEvent(title, description, remindTime, 0, previousDate, listener);
    }

    /**
     * 添加一个日历相关的通知事件
     * @param remindTime       添加日历的开始时间戳
     * @param previousDate     预览日期，参考值，0，1，2
     * @param addRemindEndTime 设置终止时间，开始时间加10分钟 ，默认开始加10分钟
     */
    public void addCalendarEvent(@NonNull String title, String description, long remindTime, long addRemindEndTime, int previousDate, OnStatusListener listener) {
        if (!EasyPermission.hasPermissions(mContext, EasyPermission.GROUP_PERMISSONS_CALENDAR)) {
            LaLog.e(ValueOf.logLivery(LOG_LIVERY_EXCEPTION, "android.group.PermissionException ：", "Need to declare android.permission.PERMISSION_READ_CALENDAR to call this api in your AndroidManifest.xml"));
            if (listener != null) {
                listener.failure("android.group.PermissionException ");
            }
            return;
        }
        int calId = checkAndAddCalendarAccount(); //获取日历账户的id
        if (calId < 0) {
            if (listener != null) {
                listener.failure("获取账户id失败，添加日历事件失败");
            }
        } else {
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(remindTime);//设置开始时间
            long start = mCalendar.getTime().getTime();
            if (addRemindEndTime == 0) {
                addRemindEndTime = 10 * 60 * 1000;
            }
            mCalendar.setTimeInMillis(start + addRemindEndTime);
            long end = mCalendar.getTime().getTime();
            ContentValues event = new ContentValues();
            event.put(CalendarContract.Events.TITLE, title);
            if (!TextUtils.isEmpty(description)) {
                event.put(CalendarContract.Events.DESCRIPTION, description);
            }
            event.put(CalendarContract.Events.CALENDAR_ID, calId); //插入账户的id
            event.put(CalendarContract.Events.DTSTART, start);
            event.put(CalendarContract.Events.DTEND, end);
            event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
            event.put(CalendarContract.Events.EVENT_TIMEZONE, LADateTime.getInstance().getDefaultTimeZoneId());//时区很重要，设置时区
            Uri newEvent = mContext.getContentResolver().insert(Uri.parse(CALENDER_EVENT_URL), event); //添加事件
            if (newEvent == null) {
                if (listener != null) {
                    listener.failure("添加日历事件失败");
                }
            } else {
                //事件提醒的设定
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
                values.put(CalendarContract.Reminders.MINUTES, previousDate * 24 * 60);// 提前previousDate天有提醒
                values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                Uri uri = mContext.getContentResolver().insert(Uri.parse(CALENDER_REMINDER_URL), values);
                if (uri == null) {
                    if (listener != null) {
                        listener.failure("添加事件提醒失败");
                    }
                } else {
                    if (listener != null) {
                        listener.success(AnConstants.EMPTY);
                    }
                }
            }
        }
    }

    /**
     * 删除日历事件
     */
    public void deleteCalendarEvent(@NonNull String title) {
        deleteCalendarEvent(title, null);
    }

    public void deleteCalendarEvent(@NonNull String title, OnStatusListener listener) {
        if (!EasyPermission.hasPermissions(mContext, EasyPermission.GROUP_PERMISSONS_CALENDAR)) {
            LaLog.e(ValueOf.logLivery(LOG_LIVERY_EXCEPTION, "android.group.PermissionException ：", "Need to declare android.permission.PERMISSION_READ_CALENDAR to call this api in your AndroidManifest.xml"));
            if (listener != null) {
                listener.failure("android.group.PermissionException");
            }
            return;
        }
        Cursor eventCursor = mContext.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
        try {
            if (eventCursor == null) {
                listener.failure("查询返回空值");
                return;
            }
            if (eventCursor.getCount() > 0) {
//                遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDER_EVENT_URL), id);
                        int rows = mContext.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) {
                            if (listener != null) {
                                listener.failure("事件删除失败");
                            }
                            return;
                        }
                    }
                }
                listener.success(AnConstants.EMPTY);
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.failure(e.getClass().toString());
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    /**
     * 显示一条自然通知
     */
    public void showNotification(@NonNull String title, @NonNull String content, Class c) {
        notificationCompatBuilder.setContentTitle(title)
                .setContentText(content)
                .setContentIntent(getPendingIntent(c))
                .setSmallIcon(APP_ICON)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)//设置使用系统默认的声音、默认震动
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        notificationManagerCompat.notify((int) System.currentTimeMillis(), notificationCompatBuilder.build());
//        notificationManagerCompat.notify(1, notificationCompatBuilder.build());
    }

    /**
     * 显示进度条通知
     */
    public void updateProgress(@NonNull String title, int progress, Class c) {
        notifyProgress(title, AnConstants.EMPTY, progress, c, false, false);
    }

    public void updateProgress(@NonNull String title, int progress) {
        updateProgress(title, progress, null);
    }

    public void updateProgressOfIndeterminate(@NonNull String title, int progress, Class c) {
        notifyProgress(title, AnConstants.EMPTY, progress, c, true, false);
    }

    public void updateProgressOfIndeterminate(@NonNull String title, int progress) {
        updateProgressOfIndeterminate(title, progress, null);
    }

    /**
     * 参考Google浏览器下载文件更新：【下载完成.48MB】
     */
    public void finishProgress(@NonNull String title, @NonNull String content, Class c) {
        notifyProgress(title, content, 100, c, true, true);
    }

    public void finishProgress(@NonNull String title, @NonNull String content) {
        finishProgress(title, content, null);
    }

    private void notifyProgress(@NonNull String title, String content, int progress, Class c, boolean indeterminate, boolean isFinish) {
        notificationCompatBuilder.setContentTitle(title)
                .setContentText(AnConstants.EMPTY)//清空上一次的描述
                .setContentIntent(getPendingIntent(c))
                .setSmallIcon(APP_ICON)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .addAction(R.drawable.ic_ab_back, "按钮", getPendingIntent(c))
                .setAutoCancel(true);
//        Issue the initial notification with zero progress
        notificationCompatBuilder.setProgress(100, progress, indeterminate);
        notificationCompatBuilder.setOnlyAlertOnce(true);//通知只会在通知首次出现时打断用户（通过声音、振动或视觉提示），而之后更新则不会再打断用户。
        notificationManagerCompat.notify(PROGRESS_REQUEST_CODE, notificationCompatBuilder.build());
        if (isFinish) {
            cancelNotify();
            notificationCompatBuilder.setContentText(content).setProgress(0, progress, false);
            notificationManagerCompat.notify(PROGRESS_REQUEST_CODE, notificationCompatBuilder.build());
        }
    }

    public void cancelNotify() {
        notificationManagerCompat.cancelAll();
    }

    /**
     * 要使该图片仅在通知收起时显示为缩略图，请调用 setLargeIcon() 并传入该图片，同时调用 BigPictureStyle.bigLargeIcon() 并传入 null，这样大图标就会在通知展开时消失：
     * @param bitmap 参考：BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_big_circle))
     */
    public void showBigNotification(@NonNull String title, @NonNull String content, Bitmap bitmap, Class c) {
        notificationCompatBuilder.setContentTitle(title)
                .setContentText(content)
                .setContentIntent(getPendingIntent(c))
                .setSmallIcon(APP_ICON)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap))
                .setAutoCancel(true);
        notificationManagerCompat.notify(PROGRESS_REQUEST_CODE, notificationCompatBuilder.build());
    }

    public void showBigNotification(@NonNull String title, @NonNull String content, Bitmap bitmap) {
        showBigNotification(title, content, bitmap, null);
    }

    public void showSmallNotification(@NonNull String title, @NonNull String content, Bitmap smallBitmap, Class c) {
        notificationCompatBuilder.setContentTitle(title)
                .setContentText(content)
                .setContentIntent(getPendingIntent(c))
                .setSmallIcon(APP_ICON)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(smallBitmap)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(smallBitmap)
                        .bigLargeIcon(null))
                .setAutoCancel(true);
        notificationManagerCompat.notify(PROGRESS_REQUEST_CODE, notificationCompatBuilder.build());
    }

    public void showSmallNotification(@NonNull String title, @NonNull String content, Bitmap smallBitmap) {
        showSmallNotification(title, content, smallBitmap, null);
    }

    /**
     * 如果您不希望使用标准通知图标和标题装饰通知，请按照上述步骤使用 showBigNotification()，但不要调用 setStyle()。
     */
    public RemoteViews createSelfNotification(Context mContext, Class<?> cls, @LayoutRes int contentView, @LayoutRes int bigContentView, int viewId) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), contentView);
        RemoteViews bigContentViews = new RemoteViews(mContext.getPackageName(), bigContentView);
        notificationCompatBuilder.setSmallIcon(APP_ICON)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(bigContentViews)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true);

        Intent intentStart = new Intent(mContext, cls);
        intentStart.setAction(AnConstants.ACTION.ACTION_DOWNLOADER);
        intentStart.putExtra(AnConstants.ACTION.DOWNLOADER, "entity");
        PendingIntent servicePendingIntent = PendingIntent.getService(mContext, 0, intentStart, 0);
        remoteViews.setOnClickPendingIntent(viewId, servicePendingIntent);
        remoteViews.setTextViewText(viewId, "LiveryNotification");

        notificationCompatBuilder.setCustomContentView(remoteViews);
        notificationManagerCompat.notify((int) System.currentTimeMillis(), notificationCompatBuilder.build());
        return remoteViews;
    }

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int checkAndAddCalendarAccount() {
        int oldId = checkCalendarAccount();
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount();
            if (addId >= 0) {
                return checkCalendarAccount();
            } else {
                return -1;
            }
        }
    }

    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    private int checkCalendarAccount() {
        Cursor userCursor = mContext.getContentResolver().query(Uri.parse(CALENDER_URL), null, null, null, null);
        try {
            if (userCursor == null) { //查询返回空值
                return -1;
            }
            int count = userCursor.getCount();
            if (count > 0) { //存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */
    private long addCalendarAccount() {
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, LADateTime.getInstance().getDefaultTimeZoneId());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();
        Uri result = mContext.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }
}