package com.sunsta.bear.engine;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.sunsta.bear.faster.FileUtils;
import com.sunsta.bear.faster.LADateTime;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.ThreadPool;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.faster.callback.OnLoadVideoImageListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

import static android.provider.MediaStore.Video.Thumbnails.MICRO_KIND;

/**
 * 请关注个人知乎bgwan， 在【an情景】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：媒体类帮助类，用于获取本地视频，网络视频的预览图，（前期1.0版本用于获取当前第一帧的，要获取中间帧使用getAtFrame(position)
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 1.0 |   2019/10/07            |   获取本地视频，网络视频的预览图，以及其它工具
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class AudioMediaEngine {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static Context context;

    /**
     * version (2)获取视频，文件，图片，文件夹大小的方法
     */

    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    public AudioMediaEngine(Context mContext) {
        context = mContext;
    }


    /**
     * 获取视频文件截图
     * //获取第一帧原尺寸图片
     * mmrc.getFrameAtTime();
     * <p>
     * //获取指定位置的原尺寸图片 注意这里传的timeUs是微秒,第一个参数是传入时间，只能是us(微秒) ，当时我传入的ms出错
     * <p>
     * 然后是第二个参数，先看看官方解释：
     * <p>
     * OPTION_CLOSEST    在给定的时间，检索最近一个帧,这个帧不一定是关键帧。
     * <p>
     * OPTION_CLOSEST_SYNC    在给定的时间，检索最近一个同步与数据源相关联的的帧（关键帧）。
     * <p>
     * OPTION_NEXT_SYNC  在给定时间之后检索一个同步与数据源相关联的关键帧。
     * <p>
     * OPTION_PREVIOUS_SYNC   顾名思义，同上
     * mmrc.getFrameAtTime(timeUs, option);
     * <p>
     * //获取指定位置指定宽高的缩略图
     * mmrc.getScaledFrameAtTime(timeUs, MediaMetadataRetrieverCompat.OPTION_CLOSEST, width, height);
     * <p>
     * //获取指定位置指定宽高并且旋转的缩略图
     * mmrc.getScaledFrameAtTime(timeUs, MediaMetadataRetrieverCompat.OPTION_CLOSEST, width, height, rotate);
     * ————————————————
     * 版权声明：本文为CSDN博主「暴走邻家」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/bzlj2912009596/article/details/80446256
     *
     * @param path 视频文件的路径
     * @return Bitmap 返回获取的Bitmap
     */


    /**
     * 要不要起一个presenter来完成网络下载的功能，并且配合alidd异步请求
     * @param ipFile 输入的视频文件路径
     * @param kind   获取视频预览图的监听回调，alidd场景命名规则
     * @param
     */
    public Bitmap getVideoThumbnail(@NonNull File ipFile, int kind, int width, int height) {
//        File ipFile = new File(Environment.getExternalStorageDirectory(), "sunst.mp4");
//        kind = MICRO_KIND;
//        width = 80;
//        height = 80;
        Bitmap bitmap = getVideoThumbnail(ipFile.getAbsolutePath(), kind, width, height);
        File filepath = bitmap2File(bitmap, "中国特种兵");//这里是将bitmap转为File，很多地方代码都能拷贝这一块
        return bitmap;
    }

    public Bitmap getVideoThumbnail(@NonNull File ipFile, int width, int height) {
//        File ipFile = new File(Environment.getExternalStorageDirectory(), "sunst.mp4");
        int kind = MICRO_KIND;
//        width = 80;
//        height = 80;
        Bitmap bitmap = getVideoThumbnail(ipFile.getAbsolutePath(), kind, width, height);
        return bitmap;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param kind      参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *                  其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
//如果指定的视频的宽高都大于了MICRO_KIND的大小，那么你就使用MINI_KIND就可以了
    public Bitmap getVideoThumbnail(String videoPath, int kind, int width, int height) {
//        wideoPath = "sdcard/sunst.mp4";
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        if (width > 0 && height > 0) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }

        return bitmap;
    }

    public Bitmap getVideoThumbnail(String videoPath, int width, int height) {
        return getVideoThumbnail(videoPath, MICRO_KIND, width, height);
    }

    /**
     * -------------------分割线----------------
     */
    public void fastGetVideoThumbnail() {

    }

    public void getVideoThumbnail(@NonNull String urlPath, @NonNull OnLoadVideoImageListener onLoadVideoImageListener) {
        extractThumbnail(urlPath, null, false, onLoadVideoImageListener);
    }

    //请求Headers，如果有需要可以添加
    public void getVideoThumbnail(@NonNull String urlPath, @NonNull HashMap<String, String> videoHttpHashMap, @NonNull OnLoadVideoImageListener onLoadVideoImageListener) {
        extractThumbnail(urlPath, videoHttpHashMap, false, onLoadVideoImageListener);
    }

    public void getVideoThumbnail(@NonNull String urlPath, boolean saveVideo, @NonNull OnLoadVideoImageListener onLoadVideoImageListener) {
        extractThumbnail(urlPath, null, saveVideo, onLoadVideoImageListener);
    }

    public void getVideoThumbnail(@NonNull String urlPath, @NonNull HashMap<String, String> videoHttpHashMap, boolean saveVideo, @NonNull OnLoadVideoImageListener onLoadVideoImageListener) {
        extractThumbnail(urlPath, videoHttpHashMap, saveVideo, onLoadVideoImageListener);
    }


    public void getVideoThumbnail(boolean saveVideo, @NonNull String urlPath, @NonNull HashMap<String, String> videoHttpHashMap, @NonNull OnLoadVideoImageListener onLoadVideoImageListener) {
        extractThumbnail(urlPath, videoHttpHashMap, saveVideo, onLoadVideoImageListener);
    }

    /**
     * 私有方法，
     */
    private void extractThumbnail(@NonNull String urlPath, HashMap<String, String> videoHttpHashMap, boolean saveVideo, OnLoadVideoImageListener onLoadVideoImageListener) {
        if (urlPath.startsWith("http")) {
            if (videoHttpHashMap == null) {
                videoHttpHashMap = new HashMap<>();
            }
            LoadVideoImageTask task = new LoadVideoImageTask(onLoadVideoImageListener, videoHttpHashMap, saveVideo);
            task.execute(urlPath);
        } else {
            LoadVideoImageTask task = new LoadVideoImageTask(onLoadVideoImageListener, saveVideo);
            task.execute(urlPath);
        }
    }


    /**
     * Create a file Uri for saving an image or video
     */
    public Uri getOutputMediaFileUri(Context context, int type) {
        //适配Android N
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", getOutputMediaFile(type));
        } else {
            if (getOutputMediaFile(type) != null) {
                return Uri.fromFile(getOutputMediaFile(type));
            }
            return null;
        }
    }

    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "image");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = LADateTime.getInstance().getCurrentTimeInString(LADateTime.PATTERN_DATE_TIME_FOLDER);
        File mediaFile = null;
        if (type == MEDIA_TYPE_IMAGE) {
            //storage/emulated/0/Pictures/image/IMG_20191210_230337.jpg
            mediaFile = FileUtils.INSTANCE.touchFile(mediaStorageDir.getPath(), "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = FileUtils.INSTANCE.touchFile(mediaStorageDir.getPath(), "VID_" + timeStamp + ".mp4");
        }
        return mediaFile;
    }


    public static class LoadVideoImageTask extends AsyncTask<String, Integer, File> {
        private OnLoadVideoImageListener listener;
        private HashMap<String, String> nativeHashMap;
        private Boolean nativeSaveVideo;

        public LoadVideoImageTask(@NonNull OnLoadVideoImageListener listener, boolean saveVideo) {
            this.listener = listener;
            this.nativeSaveVideo = saveVideo;
        }

        public LoadVideoImageTask(@NonNull OnLoadVideoImageListener listener, @NonNull HashMap<String, String> videoHttpHashMap, boolean saveVideo) {
            this.listener = listener;
            this.nativeHashMap = videoHttpHashMap;
            this.nativeSaveVideo = saveVideo;
        }

        @Override
        protected File doInBackground(String... params) {
            File resultFile = null;
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                String urlPath = params[0];
                if (!TextUtils.isEmpty(urlPath)) {
                    if (urlPath.startsWith("http"))
                        mmr.setDataSource(urlPath, nativeHashMap);//网络视频第一帧图片
                    else {
                        mmr.setDataSource(urlPath);//本地视频第一帧图片
                    }
                    Bitmap bitmap = mmr.getFrameAtTime();

                    if (nativeSaveVideo) {
                        //todo ,待完成Retrofit上传，下载文件能力以后加入保存本地视频的能力，是否断点续传
                    }

                    //保存图片
                    resultFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                    if (resultFile.exists()) {
                        resultFile.delete();
                    }
                    try {
                        FileOutputStream out = new FileOutputStream(resultFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mmr.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultFile;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (listener != null) {
                listener.onLoadImage(file);
            }
        }
    }

    /**
     * Bitmap保存成File
     * @param bitmap input bitmap
     * @param name   output file's name
     * @return String output file's path
     */
    public File bitmap2File(Bitmap bitmap, String name) {
        File resultFile = new File(Environment.getExternalStorageDirectory() + name + ".jpg");
        if (resultFile.exists()) resultFile.delete();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(resultFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            return null;
        }
        return resultFile;
    }

    /**
     *
     * --------分割线------------
     * */
    /**
     * 获取文件指定文件的指定单位的大小
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LaLog.e(ValueOf.logLivery("获取文件大小失败!"));
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LaLog.e(ValueOf.logLivery("获取文件大小失败!"));
        }
        return FormetFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            LaLog.e(ValueOf.logLivery("获取文件大小不存在!"));
        }
        return size;
    }

    /**
     * 获取指定文件夹
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 1. getFrameAtTime方法获取的是视频中具有代表性的一帧，并不是首帧，原注释是“This method finds a representative frame at any time position if possible, and returns it as a bitmap.”，获取首帧需要使用getFrameAtTime(0)。
     * <p>
     * 2. 使用setDataSource方法时，如果只传递FileDescriptor一个参数运行时会报错，必须传递三个参数。
     * <p>
     * 3. 代码中的a.mp4文件在assets目录中（Android Studio添加assets目录方法：在模块名字上右键 | New | Folder | Assets Folder）。
     */
    public void getSuperMoreData(Context context, String fileName) {
        try {
            // 获取预览图
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            AssetFileDescriptor afd = context.getAssets().openFd(fileName);
//            mmr.setDataSource(afd.getFileDescriptor()); // failed
            mmr.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            Bitmap previewBitmap = mmr.getFrameAtTime();

            // 缩放
            int PREVIEW_VIDEO_IMAGE_HEIGHT = 300; // Pixels
            int videoWidth = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int videoHeight = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            int videoViewWidth = PREVIEW_VIDEO_IMAGE_HEIGHT * videoWidth / videoHeight;
            int videoViewHeight = PREVIEW_VIDEO_IMAGE_HEIGHT;
            Bitmap resultBitmap = Bitmap.createScaledBitmap(previewBitmap, videoViewWidth, videoViewHeight, true);


            // 获取时长
            String strDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int duration = Integer.parseInt(strDuration) / 1000;
            String resultOfDate = String.format("%d:%02d", duration / 60, duration % 60);//时间长度

            mmr.release();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注意需要：<uses-permission android:name="android.permission.VIBRATE"/>
     */
    public void playBeep(boolean opened, long[] DURATIONS) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        ThreadPool.getInstance().getThreadPoolExecutor().execute(() -> {
            if (vibrator != null) {
                if (opened) {
                    if (DURATIONS.length == 1) {
                        vibrator.vibrate(DURATIONS[0]);
                    } else {
                        vibrator.vibrate(DURATIONS, -1);//这里的-1是指震动不连续
                    }
                } else {
                    vibrator.cancel();
                }
            }
        });
    }

    public void playBeepAgain(boolean opened) {
        playBeep(opened, new long[]{96L, 169L, 96L, 196L});
    }

    public void playBeepAgain() {
        playBeepAgain(true);
    }

    public void playBeep(boolean opened, long DURATION) {
        playBeep(opened, new long[]{DURATION});
    }

    public void playBeep(boolean opened) {
        playBeep(opened, 200L);
    }

    public void playBeep() {
        playBeep(true, 200L);
    }
}