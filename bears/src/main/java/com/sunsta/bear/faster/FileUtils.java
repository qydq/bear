package com.sunsta.bear.faster;


import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sunsta.bear.AnApplication;
import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.listener.DownloaderListener;
import com.sunsta.bear.listener.OnSmartClickListener;
import com.sunsta.bear.model.entity.ResponseDownloader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.sunsta.bear.AnConstants.VALUE.LOG_LIVERY_EXCEPTION;
import static com.sunsta.bear.faster.EasyPermission.GROUP_PERMISSONS_STORAGE;
import static com.sunsta.bear.faster.LADateTime.PATTERN_DATE_TIME_FOLDER;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：如果涉及Storage代表sd卡，外置和内置，不是内存，如果是root则是根目录。**获得系统目录/system
 * 枚举类型的类LaStorageFile保证，该对象只有一个。完成AR20180115666。
 * 文件路径string&Directory，目录File&Dir， 文件名File&filename 是三个重要的概念。 默认规定，这个三个必须分别使用。
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：qydda on 2017/3/15.
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 3.0 |  2020/3/21             |   整合原LaStoreFile与FileUtils
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public enum FileUtils {
    INSTANCE;
    private static String TAG = "FileUtils";
    private static String DEFAULT_DISK_CACHE_DIR = "takephoto_cache";
    private static SimpleDateFormat sf = new SimpleDateFormat(PATTERN_DATE_TIME_FOLDER);

    private static final int SUCCESS = 1;
    private static final int FAILED = 0;
    private FileOperateCallback callback;
    private volatile boolean isSuccess;
    private String errorStr;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (callback != null) {
                if (msg.what == SUCCESS) {
                    callback.onSuccess();
                }
                if (msg.what == FAILED) {
                    callback.onFailed(msg.obj.toString());
                }
            }
        }
    };

    //201912文件下载上传

    /**
     * InputStrem 转byte[]
     * @param in
     * @return
     * @throws Exception
     */
    public byte[] readStreamToBytes(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 8];
        int length = -1;
        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }
        out.flush();
        byte[] result = out.toByteArray();
        in.close();
        out.close();
        return result;
    }

    /**
     * 写入文件
     * @param in   输入流
     * @param file 注意该文件一定不为空
     */
    public void writeFile(InputStream in, @NonNull File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte[1024 * 128];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
        out.close();
        in.close();
    }

    /**
     * 得到Bitmap的byte
     * @return
     * @author YOLANDA
     */
    public byte[] bmpToByteArray(Bitmap bmp) {
        if (bmp == null)
            return null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 80, output);

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        }
        return result;
    }

    public Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    /*
     * 根据view来生成bitmap图片，可用于截图功能
     */
    public Bitmap getViewBitmap(View v) {
        v.clearFocus(); //

        v.setPressed(false); //
        // 能画缓存就返回false

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }

        v.buildDrawingCache();

        Bitmap cacheBitmap = v.getDrawingCache();

        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view

        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;

    }

    /**
     * 从assets目录中复制文件到Sdcarc内容
     * @param assetName like this:zipFile = "hong.apk"
     */
    public void copyAssetFile(@NonNull Context context, final String assetName) {
        try {
            File resultApkFile = touchFile(getPwdOssFolder(), assetName);
            InputStream input = context.getAssets().open(assetName);
            FileOutputStream fos = new FileOutputStream(resultApkFile);
            byte[] buffer = new byte[500 * 1024];
            int byteCount = 0;
            int totalReadByte = 0;
            while ((byteCount = input.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                totalReadByte += byteCount;
            }
            fos.flush();//刷新缓冲区
            input.close();
            fos.close();
        } catch (Exception e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        }
    }

    /**
     * 从assets目录中复制整个文件夹内容，这里认为复制的过程是一个耗时过程
     */
    public void copyAssetFiles(@NonNull Context context, final String srcPath, final String sdPath) {
        new Thread(() -> {
            copyAssetsToDst(context, srcPath, sdPath);
            if (isSuccess)
                handler.obtainMessage(SUCCESS).sendToTarget();
            else
                handler.obtainMessage(FAILED, errorStr).sendToTarget();
        }).start();
    }

    /**
     * 从assets目录中复制整个文件夹内容
     * @param context Context 使用CopyFiles类的Activity
     * @param srcPath String  原文件路径  如：/aa
     * @param dstPath String  复制后路径  如：xx:/bb/cc
     */
    private void copyAssetsToDst(Context context, String srcPath, String dstPath) {
        try {
            String fileNames[] = context.getAssets().list(srcPath);
            if (fileNames.length > 0) {
                File file = new File(Environment.getExternalStorageDirectory(), dstPath);
                if (!file.exists()) file.mkdirs();
                for (String fileName : fileNames) {
                    if (!srcPath.equals("")) { // assets 文件夹下的目录
                        copyAssetsToDst(context, srcPath + File.separator + fileName, dstPath + File.separator + fileName);
                    } else { // assets 文件夹
                        copyAssetsToDst(context, fileName, dstPath + File.separator + fileName);
                    }
                }
            } else {
                File outFile = new File(Environment.getExternalStorageDirectory(), dstPath);
                InputStream is = context.getAssets().open(srcPath);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
            isSuccess = true;
        } catch (Exception e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
            errorStr = e.getMessage();
            isSuccess = false;
        }
    }

    public void setFileOperateCallback(FileOperateCallback callback) {
        this.callback = callback;
    }

    /*
     * 缓存大小。
     * */
    public String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    public void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir == null || dir.isDirectory()) {
            return true;
        }

        return dir.delete();
    }

    // 获取文件
    // Context.getExternalFilesDir() -->
    // SDCard/Android/data/你的应用的包名/files/目录，一般放一些长时间保存的数据
    // Context.getExternalCacheDir() -->
    // SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        }
        return size;
    }

    /**
     * 格式化单位
     * @param size
     * @return
     */
    public String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            // return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context
     */
    public void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context
     */
    public void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    public void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * 按名字清除本应用数据库 * * @param context * @param dbName
     */
    public void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context
     */
    public void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    public void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    // /** * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath */
    // public static void cleanCustomCache(String filePath) {
    // deleteFilesByDirectory(new File(filePath));
    // }
    //
    // /** * 清除本应用所有的数据 * * @param context * @param filepath */
    // public static void cleanApplicationData(Context context, String...
    // filepath) {
    // cleanInternalCache(context);
    // cleanExternalCache(context);
    // cleanDatabases(context);
    // cleanSharedPreference(context);
    // cleanFiles(context);
    // for (String filePath : filepath) {
    // cleanCustomCache(filePath);
    // }
    // }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory
     */
    private void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    public interface FileOperateCallback {
        void onSuccess();

        void onFailed(String error);
    }

    /**
     * Return whether the file exists.
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    /**
     * 判断文件是否存在
     */
    public boolean isFileExists(final String filePath) {
        return isFileExists(getFileByPath(filePath));
    }


    /**
     * Rename the file.
     * @param filePath The path of file.
     * @param newName  The new name of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public boolean rename(final String filePath, final String newName) {
        return rename(getFileByPath(filePath), newName);
    }

    /**
     * Return the file by path.
     * @param filePath The path of file.
     * @return the file
     */
    public File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    public boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Rename the file.
     * @param file    The file.
     * @param newName The new name of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public boolean rename(final File file, final String newName) {
        // file is null then return false
        if (file == null) return false;
        // file doesn't exist then return false
        if (!file.exists()) return false;
        // the new name is space then return false
        if (isSpace(newName)) return false;
        // the new name equals old name then return true
        if (newName.equals(file.getName())) return true;
        File newFile = new File(file.getParent() + File.separator + newName);
        // the new name of file exists then return false
        return !newFile.exists()
                && file.renameTo(newFile);
    }


    /**
     * 文件操作，写文件 , 后期考虑移除 ,或者结合DownloaderAsyncTask
     */
    public boolean writeResponseBodyToDisk(@NonNull File file, ResponseDownloader mod, ResponseBody body, DownloaderListener listener) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[mod.getWriteByte()];
                long total = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    LaLog.d(TAG, "Retrofit file download: " + fileSizeDownloaded + " of " + total);
                    mod.setFinishedLength(fileSizeDownloaded);
                    int progress = (int) (((float) fileSizeDownloaded / total) * 100);
                    mod.setProgress(progress);
                    listener.onProgress(mod);
                }
                mod.setFinishedLength(fileSizeDownloaded);
                mod.setProgress(100);
                listener.onSuccess(mod);
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


    public File getPhotoCacheDir(Context context, File file) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            File mCacheDir = new File(cacheDir, DEFAULT_DISK_CACHE_DIR);
            if (!mCacheDir.mkdirs() && (!mCacheDir.exists() || !mCacheDir.isDirectory())) {
                return file;
            } else {
                return new File(mCacheDir, file.getName());
            }
        }
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return file;
    }

    public String getFileProviderName(@Nullable Context context) {
        return context.getPackageName() + ".fileprovider";
    }

    public void delete(String path) {
        try {
            if (path == null) {
                return;
            }
            File file = new File(path);
            if (!file.delete()) {
                file.deleteOnExit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isGifForSuffix(String suffix) {
        return suffix != null && suffix.startsWith(".gif") || suffix.startsWith(".GIF");
    }

    /**
     * 是否是gif
     * @param mimeType
     * @return
     */
    public boolean isGif(String mimeType) {
        return mimeType != null && (mimeType.equals("image/gif") || mimeType.equals("image/GIF"));
    }

    /**
     * 是否是网络图片
     * @param path
     * @return
     */
    public boolean isHttp(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("http")
                    || path.startsWith("net")) {
                return true;
            }
        }
        return false;
    }


    public String extSuffix(InputStream input) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            return options.outMimeType.replace("image/", ".");
        } catch (Exception e) {
            return ".jpg";
        }
    }

    /**
     * 获取图片后缀
     * @param path
     * @return
     */
    public String getLastImgType(String path) {
        try {
            int index = path.lastIndexOf(".");
            if (index > 0) {
                String imageType = path.substring(index);
                switch (imageType) {
                    case ".png":
                    case ".PNG":
                    case ".jpg":
                    case ".jpeg":
                    case ".JPEG":
                    case ".WEBP":
                    case ".bmp":
                    case ".BMP":
                    case ".webp":
                    case ".gif":
                    case ".GIF":
                        return imageType;
                    default:
                        return ".png";
                }
            } else {
                return ".png";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ".png";
        }
    }


    /**
     * 获取图片mimeType
     * @param path
     * @return
     */
    public String getImageMimeType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last);
                return "image/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "image/jpeg";
        }
        return "image/jpeg";
    }

    /**
     * 是否是视频
     * @param mimeType
     * @return
     */
    public final static String MIME_TYPE_PREFIX_VIDEO = "video";

    public boolean eqVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_VIDEO);
    }

    /**
     * 是否是图片
     * @param mimeType
     * @return
     */
    public final static String MIME_TYPE_PREFIX_IMAGE = "image";

    public boolean eqImage(String mimeType) {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_IMAGE);
    }

    /**
     * 清空裁剪网络图时产生的临时文件
     */
    public void deleteHttpCropTemporaryFile(File file) {
        if (file != null) {
            file.delete();
        }
    }

    /**
     * @param fileName 文件名字，如：sunsta.apk
     * @return 返回类似于这样的字段：sunsta_20200921_22:49:13.apk
     */
    public String createFileNameOfTime(String fileName) {
        if (!TextUtils.isEmpty(fileName) && fileName.contains(".")) {
            int index = fileName.lastIndexOf(".");
            if (index > 0) {
                String lastSuffixName = fileName.substring(index);//后缀名
                String prefixName = fileName.substring(0, index);//前缀名
                fileName = getCreateFilePrefixName(prefixName) + lastSuffixName;
            }
        } else {
            fileName = getCreateFilePrefixName(fileName);
        }
        return fileName;
    }

    /**
     * @param fileName 文件名字，如：sunsta.apk
     * @return 返回类似于这样的字段：20200921_22:49:13_sunsta.apk
     */
    public String createFileNameOfTimeSuffix(String fileName) {
        if (!TextUtils.isEmpty(fileName) && fileName.contains(".")) {
            int index = fileName.lastIndexOf(".");
            if (index > 0) {
                String lastSuffixName = fileName.substring(index);//后缀名
                String prefixName = fileName.substring(0, index);//前缀名
                fileName = getCreateFileSuffixName(prefixName) + lastSuffixName;
            }
        } else {
            fileName = getCreateFileSuffixName(fileName);
        }
        return fileName;
    }

    public boolean isApkSuffix(@NonNull String fileName) {
        return AnConstants.FILE_SUFFIX_APK.equals(getEndSuffixWithPoint(fileName));
    }

    public boolean isMovieSuffix(@NonNull String fileName) {
        if (getEndSuffixWithPoint(fileName).equals(".mp4")) {
            return true;
        } else if (getEndSuffixWithPoint(fileName).equals(".avi")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPictureSuffix(@NonNull String fileName) {
        switch (getEndSuffixWithPoint(fileName)) {
            case ".png":
            case ".PNG":
            case ".jpg":
            case ".jpeg":
            case ".JPEG":
            case ".WEBP":
            case ".bmp":
            case ".BMP":
            case ".webp":
            case ".gif":
            case ".GIF":
                return true;
            default:
                return false;
        }
    }

    public String getEndSuffixWithPoint(@NonNull String fileName) {
        if (!TextUtils.isEmpty(fileName) && fileName.contains(".")) {
            int index = fileName.lastIndexOf(".");
            if (index > 0) {
                fileName = fileName.substring(index);//后缀名:如.apk, .mp4,.mp3
            }
        }
        return fileName;
    }

    /**
     * 根据时间戳创建文件名
     * @param prefix 前缀名
     */
    public String getCreateFilePrefixName(String prefix) {
        return prefix + "_" + LADateTime.getInstance().getCurrentTimeInString(LADateTime.PATTERN_DATE_TIME_FILE);
    }

    public String getCreateFileSuffixName(String suffixlost) {
        return LADateTime.getInstance().getCurrentTimeInString(LADateTime.PATTERN_DATE_TIME_FILE) + "_" + suffixlost;
    }

    public String getPwdTempFolder() {
        return getPwdRootFolder() + AnConstants.FOLDER_TEMP + File.separator;
    }

    public String getPwdRecordFolder() {
        return getPwdRootFolder() + AnConstants.FOLDER_RECORD + File.separator;
    }

    public String getPwdLogFolder() {
        return getPwdRootFolder() + AnConstants.FOLDER_LOG + File.separator;
    }

    public String getPwdApkFolder() {
        return getPwdRootFolder() + AnConstants.FOLDER_APK + File.separator;
    }

    public String getPwdPublicFolder() {
        return getPwdRootFolder() + AnConstants.FOLDER_PUBLIC + File.separator;
    }

    public String getPwdDownloadFolder() {
        return getPwdRootFolder() + AnConstants.FOLDER_DOWNLOAD + File.separator;
    }

    public String getPwdMusicFolder() {
        return getPwdRootFolder() + AnConstants.FOLDER_MUSIC + File.separator;
    }

    public String getPwdImageFolder() {
        return getPwdRootFolder() + AnConstants.FOLDER_IMAGES + File.separator;
    }

    public String getPwdMovieFolder() {
        return getPwdRootFolder() + AnConstants.FOLDER_MOVIE + File.separator;
    }

    public String getPwdOssFolder() {
        return getPwdRootFolder() + "oss" + File.separator;
    }

    public String getPwdRootFolder() {
        return File.separator + AnConstants.FOLDER_ROOT + File.separator;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, String.format(Locale.getDefault(), "getDataColumn: _data - [%s]", ex.getMessage()));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    try {
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUri, null, null);
                    } catch (NumberFormatException e) {
                        Log.i(TAG, e.getMessage());
                        return null;
                    }
                }

            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Copies one file into the other with the given paths.
     * In the event that the paths are the same, trying to copy one file to the other
     * will cause both files to become null.
     * Simply skipping this step if the paths are identical.
     */
    public boolean copyFile(FileInputStream fileInputStream, String outFilePath) throws IOException {
        if (fileInputStream == null) {
            return false;
        }
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = fileInputStream.getChannel();
            outputChannel = new FileOutputStream(new File(outFilePath)).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (fileInputStream != null) fileInputStream.close();
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }

    /**
     * Copies one file into the other with the given paths.
     * In the event that the paths are the same, trying to copy one file to the other
     * will cause both files to become null.
     * Simply skipping this step if the paths are identical.
     */
    public void copyFile(@NonNull String pathFrom, @NonNull String pathTo) throws IOException {
        if (pathFrom.equalsIgnoreCase(pathTo)) {
            return;
        }
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            inputChannel = new FileInputStream(new File(pathFrom)).getChannel();
            outputChannel = new FileOutputStream(new File(pathTo)).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }


    /**
     * 获取下载地址后缀/以后的字段作为下载名字
     */
    public String getfileName(@NonNull String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 获取网络文件的大小，当没有网络的时候返回-1，异步线程或子线程调用
     * 如果当前线程为主线程，则应该调用同名方法getContentLength(url,Smart).
     */
    public long getContentLength(@NonNull Context mContext, @NonNull String url) throws IOException {
        long contentLength = 0;
        if (NetBroadcastReceiverUtils.isConnectedToInternet(mContext)) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    contentLength = body.contentLength();
                    response.close();
                    return contentLength;
                }
            }
        } else {
            contentLength = -1;
        }
        return contentLength;
    }

    /**
     * 获取网络文件的大小，主线程种调用，如果当前线程已在后台任务，则应该调用同名方法
     * getContentLength(mContext,url).
     */
    public void getContentLength(@NonNull String url, OnSmartClickListener<Long> smartClickListener) {
        String LOG_LIVERY_EXCEPTION = "sda";
        String LOG_LIVERY_ERROR = "sdssa";
        if (NetBroadcastReceiverUtils.isConnectedToInternet(AnApplication.getApplication())) {
            ThreadPool.getInstance().getThreadPoolExecutor().execute(() -> {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            smartClickListener.onSmartClick(body.contentLength());
                            response.close();
                        }
                    }
                } catch (IOException e) {
                    LaLog.e(ValueOf.logLivery(LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
//                    e.printStackTrace();
                    smartClickListener.onSmartClick(-1L);
                }
            });
        } else {
            LaLog.e(ValueOf.logLivery(LOG_LIVERY_ERROR, StringUtils.getString(R.string.an_no_connect_network)));
            smartClickListener.onSmartClick(-1L);
        }
    }

    /**
     * 根据时间戳创建文件名
     * @param prefix 前缀名
     * @return
     */
    public String getCreateFileName(String prefix) {
        long millis = System.currentTimeMillis();
        return prefix + sf.format(millis);
    }

    /**
     * 根据时间戳创建文件名
     * @return
     */
    public String getCreateFileName() {
        long millis = System.currentTimeMillis();
        return sf.format(millis);
    }


    /**
     * 重命名相册拍照
     * @param fileName
     * @return
     */
    public String rename(String fileName) {
        String temp = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return new StringBuffer().append(temp).append("_").append(getCreateFileName()).append(suffix).toString();
    }

    public ResponseDownloader smartDownloaderFile(Context mContext, ResponseDownloader mod) {
        mod.setDownloadMessage("onPrepare");
        File downloadResultFile = mod.getDownloadResultFile();
        long contentLength = 0;
        String downloadPath = mod.getDownloadPath();
        String fileName = mod.getFileName();
        if (!TextUtils.isEmpty(downloadPath)) {
            if (EasyPermission.hasPermissions(mContext, GROUP_PERMISSONS_STORAGE)) {
                downloadResultFile = getFileByPath(downloadPath);
//                downloadResultFile = createFile(downloadPath);
                if (downloadResultFile != null) {
                    fileName = downloadResultFile.getName();
                    mod.setFileName(fileName);
                }
            } else {
                LaLog.e(ValueOf.logLivery(LOG_LIVERY_EXCEPTION, "Java.lang.SecurityException ：", "Need to declare android.permission.WRITE_EXTERNAL_STORAGE to call this api in your AndroidManifest.xml"));
                mod.setDownloadMessage("Java.lang.SecurityException ：");
                mod.setProgress(0);
                mod.setFinished(869);
            }
        } else {
            if (!TextUtils.isEmpty(fileName)) {
                if (mod.isAppSystem()) {
                    File outputFolder;
                    if (isPictureSuffix(fileName)) {
                        outputFolder = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    } else if (isMovieSuffix(fileName)) {
                        outputFolder = mContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
                    } else {
                        outputFolder = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    }
                    if (mod.isNameOfTime()) {
                        fileName = createFileNameOfTime(fileName);
                        mod.setFileName(fileName);
                    }
                    downloadResultFile = new File(outputFolder, fileName);
                } else {
                    if (EasyPermission.hasPermissions(mContext, GROUP_PERMISSONS_STORAGE)) {
                        if (mod.isNameOfTime()) {
                            fileName = createFileNameOfTime(fileName);
                            mod.setFileName(fileName);
                        }
                        downloadResultFile = smartCreateFile(fileName);
                    } else {
                        LaLog.e(ValueOf.logLivery(LOG_LIVERY_EXCEPTION, "Java.lang.SecurityException ：", "Need to declare android.permission.WRITE_EXTERNAL_STORAGE to call this api in your AndroidManifest.xml"));
                        mod.setDownloadMessage("Java.lang.SecurityException ：");
                        mod.setProgress(0);
                        mod.setFinished(869);
                    }
                }
            } else {
                if (EasyPermission.hasPermissions(mContext, GROUP_PERMISSONS_STORAGE)) {
                    downloadResultFile = smartCreateFile(mod.getUrl());
                    mod.setFileName(downloadResultFile.getName());
                } else {
                    LaLog.e(ValueOf.logLivery(LOG_LIVERY_EXCEPTION, "Java.lang.SecurityException ：", "Need to declare android.permission.WRITE_EXTERNAL_STORAGE to call this api in your AndroidManifest.xml"));
                    mod.setDownloadMessage("Java.lang.SecurityException ：");
                    mod.setProgress(0);
                    mod.setFinished(869);
                }
            }
        }

        if (mod.getFinished() != 869) {
            if (TextUtils.isEmpty(mod.getName())) {
                mod.setName(mod.getFileName());
            }
            mod.setDownloadMessage("onProgress");
        }
        /*
         * （3）：计算下载文件的大小
         * */
        try {
            contentLength = getContentLength(mContext, mod.getUrl());
            if (contentLength == -1) {
                mod.setDownloadMessage(StringUtils.getString(R.string.an_no_connect_network));
                mod.setProgress(0);
                mod.setFinished(869);
            }
        } catch (IOException e) {
            LaLog.e(ValueOf.logLivery(LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
            mod.setDownloadMessage(e.getMessage());
//            e.printStackTrace();
            mod.setProgress(0);
            mod.setFinished(869);
        }

        mod.setLength(contentLength);
        mod.setSize(DataService.getInstance().getDataSize(contentLength));
        if (downloadResultFile != null) {
            mod.setDownloadResultFile(downloadResultFile);
            if (TextUtils.isEmpty(downloadPath)) {
                downloadPath = downloadResultFile.getAbsolutePath();
            }
        }
        mod.setDownloadPath(downloadPath);
        return mod;
    }

    public File smartCreateFile(@NonNull String fullSmart) {
        if (fullSmart.startsWith("http")) {
            return smartFile(getfileName(fullSmart));
        } else {
            return smartFile(fullSmart);
        }
    }

    private File smartFile(String name) {
        String pwdPath;
        if (isPictureSuffix(name)) {
            pwdPath = getPwdImageFolder();
        } else if (isApkSuffix(name)) {
            pwdPath = getPwdApkFolder();
        } else if (isMovieSuffix(name)) {
            pwdPath = getPwdMovieFolder();
        } else {
            pwdPath = getPwdDownloadFolder();
        }
        return touchFile(pwdPath, name);
    }

    /**
     * 创建 单个 文件
     * @param filePath 待创建的文件路径
     * @return 结果码
     */
    public File createFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            if (filePath.endsWith(File.separator)) {
                // 以路径分隔符结束，说明是文件夹，则抛出异常
                try {
                    throw new IllegalStateException(TAG + "The file [ " + filePath + " ] can not be a directory");
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                //判断父目录是否存在
                File parentFile = file.getParentFile();
                if (parentFile != null) {
                    if (!parentFile.exists()) {
                        boolean status = parentFile.mkdirs();
                        if (!status) {
                            return null;
                        }
                    }
                }
                try {
                    if (file.createNewFile()) {
                        //创建文件成功
                        return file;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return file;
    }

    /**
     * 获取手机上需要保存的根路径String "/"，优先考虑外置SD卡。
     * @return 如果存在sd卡返回sd卡根路径，如果没有则返回Root根路径,
     * 返回根路径(String)skRootPath
     */
    public String getskRootPath() {
        if (checkExistRom()) {
            return getskStorageDirectory();
        } else {
            return getskRootDirectory();
        }
    }

    /**
     * 获取手机上需要保存的路径String，使用该方法首先要判空
     * @return boolean getEnvironmentPath
     */
    public String getskStorageDirectory() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取手机上需要保存的路径String，该方法不必判空。
     * @return boolean getskRootDirectory
     */
    public String getskRootDirectory() {
        return Environment.getRootDirectory().getPath();
    }

    /**
     * 获取根目录 "/cache-download"目录
     */
    public String getDownloadCacheDirectory() {
        return Environment.getDownloadCacheDirectory().getPath();
    }

    /**
     * 获取应用包名内缓存目录路径
     */
    public String getskCacheDirectory(Context context) {
        return context.getCacheDir().getPath();
    }

    /*
     * -----------------分割线-------------
     * */

    /**
     * getskRootFile
     * 获取手机上需要保存的根路径file，优先考虑外置SD卡。(file会一定存在)
     * @return File 文件，如果存在sd卡返回sd卡文件，如果没有则返回根路径File对象。
     */
    public File getskRootFile() {
        if (checkExistRom()) {
            return getskStorageDirectoryFile();//获取根目录
        } else {
            return getskRootDirectoryFile();
        }
    }

    /**
     * 获取手机上绝对路径需要保存的路径file，(改方法需要判空。)
     * @return "\"跟路径对象
     */
    public File getskStorageDirectoryFile() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * getskRootDirectoryFile
     * 获取手机上绝对路径需要保存的路径file，(该方法一定不会为空的。)
     * @return "\"根路径对象
     */
    public File getskRootDirectoryFile() {
        return Environment.getRootDirectory();
    }

    /**
     * 获取下载文件对象 File。
     */
    public File getDownloadCacheDirectoryFile() {
        return Environment.getDownloadCacheDirectory();
    }

    /**
     * 获取手机上缓存的路径file，(该方法一定不会为空的。)
     * android的缓存存储建议放在该目录下面，该目录的下面不需要权限。
     * <p>
     * @param context 上下文对象
     * @return /data/data/<application package>/cache目录。
     */
    public File getskCacheFile(@NonNull Context context) {
        return context.getCacheDir();
    }

    /**
     * 获取手机上缓存的路径file，(该方法一定不会为空的。)
     * android的存储建议放在该目录下面，该目录的下面不需要权限。
     * Context.getExternalCacheDir()方法,一般存放临时缓存数据
     * <p>
     * @param context 上下文对象
     * @return SDCard/Android/data/你的应用包名/cache/目录.
     */
    public File getskExternalCacheFile(@NonNull Context context) {
        return context.getExternalCacheDir();
    }

    // To be safe, you should check that the SDCard is mounted
// using Environment.getExternalStorageState() before doing this.
    public File getExternalStoragePublicDirectory(String type, String child) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(type), child);
// This location works best if you want the created images to be shared
// between applications and persist after your app has been uninstalled.
// Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return mediaStorageDir;
    }

    /**
     * 获取手机上缓存的路径file，(该方法一定不会为空的。)
     * android的存储建议放在该目录下面，该目录的下面不需要权限。
     * <p>
     * @param context 上下文对象
     * @return /data/data/<application package>/files目录。
     */
    public File getskFile(@NonNull Context context) {
        return context.getFilesDir();
    }

    /**
     * 获取手机上缓存的路径file，(该方法一定不会为空的。)
     * android的存储建议放在该目录下面，该目录的下面不需要权限。
     * Context.getExternalFilesDir()方法,一般放一些长时间保存的数据
     * <p>
     * @param context 上下文对象
     * @return SDCard/Android/data/你的应用的包名/files/
     */
    public File getskExternalFile(@NonNull Context context) {
        return context.getExternalFilesDir(null);
    }


    /**
     * 获取Integrate系列的SharedPreferences对象.
     * @param context 上下文对象
     * @return INA SharedPreferences
     */
    public SharedPreferences getSharedPreferences(@NonNull Context context) {
        return context.getSharedPreferences(AnConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 获取系统的SharedPreferences对象.
     * @param context 上下文对象
     * @return system SharedPreferences
     * like :com.aili.integrate_sharedpreferences
     */
    public SharedPreferences getDefaultSharedPreferences(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 判断同名方法内置SDcard是否存在（目的是为了兼容以前的版本）
     * @return boolean true存在，false不存在
     */
    public boolean checkExistRom() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断同名方法内置SDcard是否存在，兼容以前版本
     * @return boolean true存在，false不存在
     */
    public boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * linux获取外置SDcard的路径(mount linux的角度去获取，和上面方法有区别)
     * 得到mount外置sd卡的路径（简单参数）
     * @return String 外置SDCARD路径。
     */
    public String getEPathByMount() {
        StringBuilder sdCardStringBuilder = new StringBuilder();
        List<String> sdCardPathList = new ArrayList<String>();
        String sdcardpath = null;
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard")) {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory()) {
                        sdCardPathList.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        if (sdCardPathList != null) {
            for (String path : sdCardPathList) {
                sdCardStringBuilder.append(path);
            }
            sdcardpath = sdCardStringBuilder.toString();
        }
        return sdcardpath;
    }

    /**
     * android系统可通过Environment.getExternalStorageDirectory()获取存储卡的路径，
     * 但是现在有很多手机内置有一个存储空间，同时还支持外置sd卡插入，
     * 这样通过Environment.getExternalStorageDirectory()方法获取到的就是内置存储卡的位置，
     * 需要获取外置存储卡的路径就比较麻烦，这里借鉴网上的代码，稍作修改，
     * 在已有的手机上做了测试，效果还可以，当然也许还有其他的一些奇葩机型没有覆盖到。
     * <p>
     * E ，代表其它，E代表external外部
     * @return String 得到mount外置sd卡的路径（复杂参数）
     */
    public String getEEPathByMount() {
        String sdcard_path = null;
        String sd_default = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (sd_default.endsWith("/")) {
            sd_default = sd_default.substring(0, sd_default.length() - 1);
        }
        // 得到路径
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                } else if (line.contains("fuse") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sdcard_path;
    }
    /*
     * -----------------分割线-------------
     * */

    /**
     * 计算SD卡剩余容量和总容量(调用该方法需要判空。)
     * 以$符号区分总容量和剩余容量。
     * @return String 总容量&剩余容量
     */
    public String calculateStorage() {
        String txt = "";
        //判断是否有插入存储卡
        if (checkExistRom()) {
            File path = Environment.getExternalStorageDirectory();
            //取得sdcard文件路径
            StatFs statfs = new StatFs(path.getPath());
            //获取block的SIZE
            long blocSize = statfs.getBlockSize();
            //获取BLOCK数量
            long totalBlocks = statfs.getBlockCount();
            //己使用的Block的数量
            long availaBlock = statfs.getAvailableBlocks();
            String[] total = filesize(totalBlocks * blocSize);
            String[] availale = filesize(availaBlock * blocSize);
            //显示SD卡的容量信息
            txt = total[0] + total[1] + "$" + availale[0] + availale[1];
        }
        return txt;
    }

    /**
     * 计算内存总容量(调用该方法不需要判空。)
     * 以$符号区分总容量和剩余容量。
     * @return String 总容量and剩余容量
     */
    public String calculateRoot() {
        String txt = "";
        //判断是否有插入存储卡,这里严谨些还是判断sk
        if (!checkExistRom()) {
            File path = getskRootFile();
            //取得sdcard文件路径
            StatFs statfs = new StatFs(path.getPath());
            //获取block的SIZE
            long blocSize = statfs.getBlockSize();
            //获取BLOCK数量
            long totalBlocks = statfs.getBlockCount();
            //己使用的Block的数量
            long availaBlock = statfs.getAvailableBlocks();
            String[] total = filesize(totalBlocks * blocSize);
            String[] availale = filesize(availaBlock * blocSize);
            //显示SD卡的容量信息
            txt = total[0] + total[1] + "$" + availale[0] + availale[1];
        }
        return txt;
    }

    //返回数组，下标1代表大小，下标2代表单位 KB/MB
    private String[] filesize(long size) {
        String str = "";
        if (size >= 1024) {
            str = "KB";
            size /= 1024;
            if (size >= 1024) {
                str = "MB";
                size /= 1024;
            }
        }
        DecimalFormat formatter = new DecimalFormat();
        formatter.setGroupingSize(3);
        String result[] = new String[2];
        result[0] = formatter.format(size);
        result[1] = str;
        return result;
    }


    /**
     * 根目录下-创建文件目录
     * 在android6.0，需要动态获取操作文件的权限。
     * <p>
     * 说明：『& 两边都会判断』『&& 第一个不满足false，后面不会判断 』
     * 『| 两边都会判断』『|| 第一个满足true，后面不会判断 』
     * @param pwdFilepath pwd 文件目录(不包含文件名） like this's [建议/ali/apk/],[ali/apk/],[ali/apk]
     * @return 创建的文件对象
     */
    public File createDir(@NonNull String pwdFilepath) {
        File fileDirs = new File(getskRootFile(), pwdFilepath);
        if ((fileDirs.isDirectory() & fileDirs.exists()) || fileDirs.mkdirs())
            return fileDirs;
        return fileDirs;
    }

    /**
     * 文件夹
     * 在android6.0，需要动态获取操作文件的权限。
     * <p>
     * 说明：『& 两边都会判断』『&& 第一个不满足false，后面不会判断 』
     * 『| 两边都会判断』『|| 第一个满足true，后面不会判断 』
     * @param pwdFilepath pwd 文件目录(不包含文件名） like this's [建议sdcard/ali/apk/],[sdcard/ali/apk/]
     * @return 创建的文件对象
     */
    public File createFolder(String pwdFilepath) {
        File folder = new File(pwdFilepath);
        if (!folder.exists()) {
            if (!pwdFilepath.endsWith(File.separator)) {
                //不是以路径分隔符 "/" 结束，则添加路径分隔符 "/"
                pwdFilepath = pwdFilepath + File.separator;
                folder = new File(pwdFilepath);
            }
            if (folder.mkdirs()) {
            }
        }
        return folder;
    }

    /**
     * 创建文件目录，
     * 在android6.0，不需要动态获取操作文件的权限。
     * @param context      上下文对象
     * @param pwdFilepath  pwd 文件目录(不包含文件名） like this's [建议/ali/apk/],[ali/apk/],[ali/apk]
     * @param innalPackage innalPackage=true /data/data/packagename/file/pwdFile目录下创建文件。
     *                     =false/sdcard/Android/PackageName/file/目录。
     */
    public File createDir(@NonNull Context context, @NonNull String pwdFilepath, boolean innalPackage) {
        File fileDirs;
        if (innalPackage) {
            fileDirs = new File(getskFile(context), pwdFilepath);
        } else {
            fileDirs = new File(getskExternalFile(context), pwdFilepath);
        }
        if ((fileDirs.isDirectory() & fileDirs.exists()) || fileDirs.mkdirs())
            return fileDirs;
        return fileDirs;
    }

    /**
     * 创建文件目录，
     * 在android6.0，不需要动态获取操作文件的权限。
     * @param context      上下文对象
     * @param pwdFilepath  pwd 文件目录(不包含文件名） like this's [建议/ali/apk/],[ali/apk/],[ali/apk]
     * @param innalPackage innalPackage =true /data/data/packagename/cache/目录
     *                     innalPackage = fale /Android/data/你的应用包名/cache/目录.
     */
    public File createCacheDir(@NonNull Context context, @NonNull String pwdFilepath, @NonNull boolean innalPackage) {
        File fileDirs;
        if (innalPackage) {
            fileDirs = new File(getskCacheFile(context), pwdFilepath);
        } else {
            fileDirs = new File(getskExternalCacheFile(context), pwdFilepath);
        }
        if ((fileDirs.isDirectory() & fileDirs.exists()) || fileDirs.mkdirs())
            return fileDirs;
        return fileDirs;
    }


    /**
     * 在/ / / 目录下创建文件（文件不一定存在）
     * 在android6.0，需要动态获取操作文件的权限。
     * 如果不对文件输入输出流操作文件不存在
     * @param fileName pwd 文件目录(不包含文件名） like this's [建议/ali/apk/],[ali/apk/],[ali/apk]
     */
    public File touchFile(@NonNull String fileName) {
        File rootDir = getskRootFile();
        return new File(rootDir, fileName);
    }

    public File touchFile(@NonNull File skFile, @NonNull String fileName) {
        return new File(skFile, fileName);
    }

    /**
     * 创建文件目录，
     * 在android6.0，需要动态获取操作文件的权限。
     * 返回文件对象。
     * @param pwdFilepath pwd 文件目录(不包含文件名） like this's [建议/ali/apk/],[ali/apk/],[ali/apk]
     * @param fileName    创建的文件名
     */
    public File touchFile(@NonNull String pwdFilepath, @NonNull String fileName) {
        File fileDirs = createDir(pwdFilepath);
        return new File(fileDirs, fileName);
    }

    /**
     * 缓存目录创建文件。
     * 在android6.0，不需要动态获取权限
     * @param context      上下文对象
     * @param pwdFilepath  pwd 文件目录(不包含文件名） like this's [建议/ali/apk/],[ali/apk/],[ali/apk]
     * @param fileName     创建的文件名
     * @param innalPackage = true是/data/data/<application package>/cache目录下创建文件。
     *                     innalPackage = false/Android/data/你的应用包名/cache/目录下创建文件。
     * @return 创建文件的对象
     */
    public File touchCacheFile(@NonNull Context context, @NonNull String pwdFilepath, @NonNull String fileName, boolean innalPackage) {
        File fileDirs = createCacheDir(context, pwdFilepath, innalPackage);
        return new File(fileDirs, fileName);
    }

    /**
     * 删除文件再创建文件（文件一定存在）。
     */
    public File fouceTouchFile(@NonNull String fileName) {
        File newFile = new File(getskRootFile(), fileName);
        if (newFile.exists() && newFile.delete()) {
        }
        try {
            if (newFile.createNewFile()) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }

    public File fouceTouchFile(@NonNull File skFile, @NonNull String fileName) {
        File newFile = new File(skFile, fileName);
        if (newFile.exists() && newFile.delete()) {
        }
        try {
            if (newFile.createNewFile()) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }

    /**
     * 创建文件目录，删除文件再创建文件（文件一定存在）。
     * 在android6.0，需要动态获取操作文件的权限。
     * 返回文件对象。
     * @param pwdFilepath pwd 文件目录(不包含文件名） like this's [建议/ali/apk/],[ali/apk/],[ali/apk]
     * @param fileName    创建的文件名
     */
    public File fouceTouchFile(@NonNull String pwdFilepath, @NonNull String fileName) {
        File newFile = new File(createDir(pwdFilepath), fileName);
        if (newFile.exists() && newFile.delete()) {
        }
        try {
            if (newFile.createNewFile()) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }

    /**
     * 创建文件目录，删除文件再创建文件（文件一定存在）。
     * 在android6.0，不需要动态获取权限。
     * @param context      上下文对象
     * @param pwdFilepath  pwd 文件目录(不包含文件名） like this's [建议/ali/apk/],[ali/apk/],[ali/apk]
     * @param fileName     创建的文件名
     * @param innalPackage = true是/data/data/<application package>/cache目录下创建文件。
     *                     innalPackage = false/Android/data/你的应用包名/cache/目录下创建文件。
     * @return 创建文件的对象
     */
    public File fouceTouchCacheFile(@NonNull Context context, @NonNull String pwdFilepath, @NonNull String fileName, boolean innalPackage) {
        File newFile = new File(createCacheDir(context, pwdFilepath, innalPackage), fileName);
        if (newFile.exists() && newFile.delete()) {
        }
        try {
            if (newFile.createNewFile()) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }

    /**
     * 删除/ / / 目录下 的某一个文件
     * http://android.xsoftlab.net/reference/android/content/Context.html#getExternalFilesDir(java.lang.String)
     * @param fileName fileName like this [qy.jpg]
     * @return 返回true文件删除成功
     * 返回false删除失败，或者删除文件不存在。
     */
    public boolean deleteFile(@NonNull String fileName) {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File deleteFile = new File(getskRootFile(), fileName);
        if (deleteFile.exists()) {
            return deleteFile.delete();
        }
        return false;
    }

    /**
     * 删除/ / / 目录下 的某一个文件
     * http://android.xsoftlab.net/reference/android/content/Context.html#getExternalFilesDir(java.lang.String)
     * @param fileName    fileName like this [qy.jpg]
     * @param pwdFilepath 文件路径 like this[/ali/music/love/]
     * @return 返回true文件删除成功
     * 返回false删除失败，或者删除文件不存在。
     */
    public boolean deleteFile(@NonNull String pwdFilepath, @NonNull String fileName) {
        File deleteFile = new File(pwdFilepath + fileName);
        if (deleteFile.exists()) {
            return deleteFile.delete();
        }
        return false;
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的所有文件，
     * 如果传入的directory是个文件，将不做处理 * *
     * @param directory 文件目录
     */
    public void deleteFiles(@NonNull File directory) {
        if (directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    /**
     * Return the size of directory.
     * @param dirPath The path of directory.
     * @return the size of directory
     */
    public String getDirSize(final String dirPath) {
        return getDirSize(getFileByPath(dirPath));
    }

    /**
     * Return the size of directory.
     * @param dir The directory.
     * @return the size of directory
     */
    public String getDirSize(final File dir) {
        long len = getDirLength(dir);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    /**
     * Return the length of file.
     * @param filePath The path of file.
     * @return the length of file
     */
    public String getFileSize(final String filePath) {
        long len = getFileLength(filePath);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    /**
     * Return the length of file.
     * @param file The file.
     * @return the length of file
     */
    public String getFileSize(final File file) {
        long len = getFileLength(file);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    /**
     * Return the length of directory.
     * @param dirPath The path of directory.
     * @return the length of directory
     */
    public long getDirLength(final String dirPath) {
        return getDirLength(getFileByPath(dirPath));
    }

    /**
     * Return the length of directory.
     * @param dir The directory.
     * @return the length of directory
     */
    public long getDirLength(final File dir) {
        if (!isDir(dir)) return -1;
        long len = 0;
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    len += getDirLength(file);
                } else {
                    len += file.length();
                }
            }
        }
        return len;
    }

    /**
     * Return the length of file.
     * @param filePath The path of file.
     * @return the length of file
     */
    public long getFileLength(final String filePath) {
        boolean isURL = filePath.matches("[a-zA-z]+://[^\\s]*");
        if (isURL) {
            try {
                HttpsURLConnection conn = (HttpsURLConnection) new URL(filePath).openConnection();
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    return conn.getContentLength();
                }
                return -1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getFileLength(getFileByPath(filePath));
    }

    /**
     * Return the length of file.
     * @param file The file.
     * @return the length of file
     */
    public long getFileLength(final File file) {
        if (!isFile(file)) return -1;
        return file.length();
    }

    private String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < 1024) {
            return String.format(Locale.getDefault(), "%.3fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format(Locale.getDefault(), "%.3fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format(Locale.getDefault(), "%.3fMB", (double) byteNum / 1048576);
        } else {
            return String.format(Locale.getDefault(), "%.3fGB", (double) byteNum / 1073741824);
        }
    }

    /**
     * Return whether it is a directory.
     * @param dirPath The path of directory.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public boolean isDir(final String dirPath) {
        return isDir(getFileByPath(dirPath));
    }

    /**
     * Return whether it is a directory.
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public boolean isDir(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    /**
     * Return whether it is a file.
     * @param filePath The path of file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public boolean isFile(final String filePath) {
        return isFile(getFileByPath(filePath));
    }

    /**
     * Return whether it is a file.
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }
}