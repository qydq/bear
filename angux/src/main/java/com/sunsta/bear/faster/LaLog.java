package com.sunsta.bear.faster;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sunsta.bear.AnApplication;
import com.sunsta.bear.AnConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ConcurrentModificationException;

/**
 * <h2>请关注个人知乎Bgwan， 在【an系列】专栏会有本【livery框架】的使用案例（20190922-正在持续更新中...</h2>
 * 中文描述：统一所有日志管理 ，完成AR20180117Sww * <br/>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2018/01/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 2.0 |   2020/10/31             |   修改打印内容的max_lenth为1024*8
 */
public class LaLog {
    public static final String defaultTag = "dota";
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    /**
     * 下面这个变量定义日志级别
     */
    public static final int LEVEL = VERBOSE;

    //如果保存日志，则用&&区分，方便查找
    private static final String LOGPRE_SPLIT = "&&";
    private static final String LOGPRE_APPVER = AnConstants.FOLDER_LOG;

    /**
     * 每次打印字符串的最大长度
     */
    private static final int MAX_LENTH = 1024 * 8;

    /**
     * 每个log文件最大1M
     */
    private static int MAX_SIZE = 1024 * 1024;

    /**
     * 最大log文件数量
     */
    private static int MAC_ACCOUNT = 5;


    /**
     * 缓存大小 128k字节
     */
    private static final long MAX_BUFFER_CACHE = 128 * 1024;

    /* 6sec */
    private static final long LOGFILE_DELAY = 6 * 1000;
    /* 5mins */
    private static final long DELAY_TIME = 5 * 60 * 1000;

    /* 处理消息 */
    private static final int MSG_SAVE_LOG = 1;
    private static final int MSG_SAVE_EXIT = 2;

    private static StringBuilder anLogBuffer;
    private static Handler anLogHandler;


    /**
     * debug编译下打印信息Log
     * @param defaultTag defaultTag 提示一般为类名
     * @param msg        打印信息
     */
    public static void d(@NonNull String defaultTag, Object... msg) {
        d(true, defaultTag, msg);
    }

    /**
     * debug编译下打印信息Log
     * @param defaultTag defaultTag 提示一般为类名
     * @param isSaveLog  是否保存
     * @param msg        打印信息
     */
    public static void d(boolean isSaveLog, @NonNull String defaultTag, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        if (!TextUtils.isEmpty(text)) {
            int len = text.length();
            if (len <= MAX_LENTH) {
                Log.d(defaultTag, text);
            } else {
                do {
                    Log.d(defaultTag, text.substring(0, MAX_LENTH));
                    text = text.substring(MAX_LENTH);
                    len = text.length();
                } while (len > MAX_LENTH);
                if (len > 0) {
                    Log.d(defaultTag, text);
                }
            }
        }

        if (isSaveLog) {
            saveLog(defaultTag, msg);
        }
    }


    public static void v(String defaultTag, String msg) {
        if (LEVEL <= VERBOSE) {
            Log.v(defaultTag, msg);
        }
    }

    public static void d(String defaultTag, String msg) {
        if (LEVEL <= DEBUG) {
            Log.d(defaultTag, msg);
        }
    }

    public static void i(String defaultTag, String msg) {
        if (LEVEL <= INFO) {
            Log.i(defaultTag, msg);
        }
    }

    public static void w(String defaultTag, String msg) {
        if (LEVEL <= WARN) {
            Log.w(defaultTag, msg);
        }
    }

    public static void e(String defaultTag, String msg) {
        if (LEVEL <= ERROR) {
            Log.e(defaultTag, msg);
        }
    }

    public static void v(String msg) {
        if (LEVEL <= VERBOSE) {
            Log.v(defaultTag, msg);
        }
    }

    public static void d(String msg) {
        if (LEVEL <= DEBUG) {
            Log.d(defaultTag, msg);
        }
    }

    public static void i(String msg) {
        if (LEVEL <= INFO) {
            Log.i(defaultTag, msg);
        }
    }

    public static void w(String msg) {
        if (LEVEL <= WARN) {
            Log.w(defaultTag, msg);
        }
    }

    public static void e(String msg) {
        if (LEVEL <= ERROR) {
            Log.e(defaultTag, msg);
        }
    }

    public static void m(String msg) {
        String methodName = new Exception().getStackTrace()[1].getMethodName();
        Log.v(defaultTag, methodName + ":    " + msg);
    }

    public static void m(int msg) {
        String methodName = new Exception().getStackTrace()[1].getMethodName();
        Log.v(defaultTag, methodName + ":    " + msg + "");
    }

    public static void m() {
        String methodName = new Exception().getStackTrace()[1].getMethodName();
        Log.v(defaultTag, methodName);
    }

    public static void v(int msg) {
        v(msg + "");
    }

    public static void d(int msg) {
        d(msg + "");
    }

    public static void i(int msg) {
        i(msg + "");
    }

    public static void w(int msg) {
        w(msg + "");
    }

    public static void e(int msg) {
        e(msg + "");
    }

    /**
     * 打印warn级别的log
     * @param defaultTag defaultTag 提示一般为类名
     * @param msg        打印信息
     */
    public static void w(String defaultTag, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        int len = text.length();
        if (len <= MAX_LENTH) {
            Log.w(defaultTag, text);
        } else {
            do {
                Log.w(defaultTag, text.substring(0, MAX_LENTH));
                text = text.substring(MAX_LENTH);
                len = text.length();
            } while (len > MAX_LENTH);
            if (len > 0) {
                Log.w(defaultTag, text);
            }
        }
    }

    /**
     * 打印error级别的log
     * @param defaultTag
     * @param msg
     */
    public static void e(String defaultTag, Object... msg) {
        e(true, defaultTag, msg);
    }

    /**
     * 打印error级别的log
     * @param defaultTag
     * @param msg
     */
    public static void e(boolean isSaveLog, String defaultTag, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        int len = text.length();
        if (len <= MAX_LENTH) {
            Log.e(defaultTag, text);
        } else {
            do {
                Log.e(defaultTag, text.substring(0, MAX_LENTH));
                text = text.substring(MAX_LENTH);
                len = text.length();
            } while (len > MAX_LENTH);
            if (len > 0) {
                Log.e(defaultTag, text);
            }
        }

        if (isSaveLog) {
            saveLog(defaultTag, msg);
        }
    }

    /**
     * 打印敏感信息，用户信息级别的log
     * @param defaultTag 标签
     * @param msg        日志内容
     */
    public static void s(boolean isSaveLog, @NonNull String defaultTag, @NonNull Object... msg) {
        d(isSaveLog, defaultTag, msg);
    }

    /**
     * 保存log到文件
     * @param defaultTag defaultTag
     * @param msg        消息
     */
    public static void saveLog(@NonNull String defaultTag, @NonNull Object... msg) {
        String timePrefix = DataService.getInstance().getTimePrefix();
        String text = buildMessage(msg, timePrefix, defaultTag + LOGPRE_SPLIT, LOGPRE_APPVER);
        writeAnlogToDisk(text);
    }

    private static String buildMessage(@NonNull Object[] msg, @NonNull String... prefix) {
        StringBuilder sb = new StringBuilder();
        try {
            for (String p : prefix) {
                sb.append(p);
            }
            for (Object m : msg) {
                sb.append(m != null ? m : "");
            }
        } catch (ConcurrentModificationException e) {
//            Log.e(defaultTag, e.getMessage());
            return "";
        }
        return sb.toString();
    }


    /**
     * 向文件中写文本内容
     * @param file
     * @param content
     * @param append
     */
    public static void writeFile(File file, String content, boolean append) {
        if (null == file || null == content) {
            return;
        }
        byte[] bytes = null;
        try {
            bytes = content.getBytes(AnConstants.CONFIG.default_encode);
        } catch (UnsupportedEncodingException e) {
            w(defaultTag, "write file failed: ", e.getMessage());
        }
        if (bytes != null) {
            writeFile(file, bytes, append);
        }
    }

    /**
     * 向文件中写二进制内容
     * @param file
     * @param content
     * @param append
     */
    public static void writeFile(File file, byte[] content, boolean append) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, append);
            out.write(content);
            out.flush();
        } catch (IOException e) {
            w(defaultTag, "write file failed: ", e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e(false, defaultTag, "close failed: ", e.getMessage());
                }
            }
        }
    }

    private static File getLogFile() {
        if (isDebug(AnApplication.getApplication())) {
            MAX_SIZE = 10 * 1024 * 1024;
            MAC_ACCOUNT = 10 * 5;
        }
        String logFolder = FileUtils.INSTANCE.getPwdLogFolder();

        File resultFile = FileUtils.INSTANCE.fouceTouchFile(logFolder, "0.log");
        if (resultFile.exists() && resultFile.length() > MAX_SIZE) {
            File tmp = new File(logFolder + (MAC_ACCOUNT - 1) + ".log");
            if (!(tmp.exists() & tmp.delete())) {
                Log.e(defaultTag, "delete log file failed");
            }
            for (int i = MAC_ACCOUNT - 2; i >= 0; i--) {
                tmp = new File(logFolder + i + ".log");
                if (tmp.exists()) {
                    if (!tmp.renameTo(new File(logFolder + (i + 1) + ".log"))) {
                        Log.e(defaultTag, "rename log file failed");
                        return null;
                    }
                }
            }
        }
        return resultFile;
    }


    private static void writeAnlogToDisk(String text) {
        synchronized (LaLog.class) {
            if (anLogBuffer == null) {
                anLogBuffer = new StringBuilder(text);
            } else {
                anLogBuffer.append(text);
            }
            anLogBuffer.append("\n");

            boolean saveNow = anLogBuffer.length() >= MAX_BUFFER_CACHE;
            // schedule log file buffer saving
            if (anLogHandler == null) {
                HandlerThread thread = new HandlerThread("anLogfile_thread");
                thread.start();
                anLogHandler = new AnLogHandler(thread.getLooper());
                anLogHandler.sendEmptyMessageDelayed(MSG_SAVE_LOG, saveNow ? 0 : LOGFILE_DELAY);
            } else {
                anLogHandler.removeMessages(MSG_SAVE_EXIT);
                if (saveNow) {
                    anLogHandler.removeMessages(MSG_SAVE_LOG);
                    anLogHandler.sendEmptyMessage(MSG_SAVE_LOG);
                } else if (!anLogHandler.hasMessages(MSG_SAVE_LOG)) {
                    anLogHandler.sendEmptyMessageDelayed(MSG_SAVE_LOG, LOGFILE_DELAY);
                }
            }
        }
    }

    private static void flushAnlog() {
        synchronized (LaLog.class) {
            if (anLogBuffer == null) {
                return;
            }

            String text = anLogBuffer.toString();
            anLogBuffer = null;
            File file = getLogFile();
            if (file != null) {
                writeFile(file, text, true);
            } else {
                w(defaultTag, "get log file failed.");
            }
        }
    }

    private static boolean isDebug(@NonNull Context context) {
        if (context.getApplicationInfo() != null) {
            return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
        return false;
    }

    private static class AnLogHandler extends Handler {

        public AnLogHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (null == msg) {
                return;
            }
            switch (msg.what) {
                case MSG_SAVE_LOG:
                    synchronized (LaLog.class) {
                        anLogHandler.removeMessages(MSG_SAVE_LOG);
                        flushAnlog();
                        anLogHandler.sendEmptyMessageDelayed(MSG_SAVE_EXIT, DELAY_TIME);
                    }
                    break;

                case MSG_SAVE_EXIT:
                    synchronized (LaLog.class) {
                        if (!anLogHandler.hasMessages(MSG_SAVE_LOG)) {
                            anLogHandler.getLooper().quit();
                            anLogHandler = null;
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
