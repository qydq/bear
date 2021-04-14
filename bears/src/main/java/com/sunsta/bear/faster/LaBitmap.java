package com.sunsta.bear.faster;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.sunsta.bear.AnApplication;
import com.sunsta.bear.AnConstants;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 请关注个人知乎bgwan， 在【an情景】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：takeLa工具类中对Bitmap的操作(@IdRes) && 2.0 fix警告: [deprecation] Options中的inPurgeable已过时 完成既定任务AR20180117SGG
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 2.0 |   2019/12/11           |   saveAvator新增参数，保存bitmap的时候返回文件File而不再是void
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public enum LaBitmap {
    INSTANCE;

    // type
    public enum TYPE {
        PNG, JPG
    }

    private String resultPath = null;

    /**
     * 获取LaBitmap保存头像的位置
     */
    public String getAvatarDir() {
        return resultPath;
    }

    public File saveJPGAvatar(@NonNull Context context, @NonNull Bitmap bitmap, String dir, @NonNull String picName) {
        return saveAvatar(context, TYPE.JPG, bitmap, dir, picName, 80);
    }

    public File saveJPGAvatar(@NonNull Context context, @NonNull Bitmap bitmap, String dir, @NonNull String picName, int quality) {
        return saveAvatar(context, TYPE.JPG, bitmap, dir, picName, quality);
    }

    public File saveJPGAvatar(@NonNull Context context, @NonNull Bitmap bitmap, @NonNull String picName) {
        return saveAvatar(context, TYPE.JPG, bitmap, null, picName, 80);
    }

    public File saveJPGAvatar(@NonNull Context context, @NonNull Bitmap bitmap, @NonNull String picName, int quality) {
        return saveAvatar(context, TYPE.JPG, bitmap, null, picName, quality);
    }

    public File savePNGAvatar(@NonNull Context context, @NonNull Bitmap bitmap, String dir, @NonNull String picName) {
        return saveAvatar(context, TYPE.PNG, bitmap, dir, picName, 100);
    }

    public File savePNGAvatar(@NonNull Context context, @NonNull Bitmap bitmap, @NonNull String picName) throws IOException {
        return saveAvatar(context, TYPE.PNG, bitmap, null, picName, 100);
    }

    /**
     * 保存头像，默认保存在根目录下an/Picture中
     * @param bitmap  bitmap对象
     * @param picName 照片名字
     */
    public File saveAvatar(@NonNull Bitmap bitmap, @NonNull String picName) {
        return saveAvatar(null, TYPE.JPG, bitmap, null, picName, 80);
    }

    /**
     * 保存头像文件，头像文件一般比较小直接保存
     * @param bitmap  需要保存的Bitmap对象
     * @param picName 保存的文件名字
     * @param type    保存文件格式
     * @param dir     保存的路径，通过getAvatarPath可以得到路径
     * @param context 上下文对象
     */
    private File saveAvatar(Context context,
                            @NonNull TYPE type,
                            Bitmap bitmap,
                            @NonNull String dir,
                            @NonNull String picName,
                            int quality) {
        File resultFile;
        if (context != null) {
            if (TextUtils.isEmpty(dir)) {
                resultPath = File.separator + AnConstants.FOLDER_IMAGES + File.separator;
                resultFile = FileUtils.INSTANCE.fouceTouchCacheFile(context, getAvatarDir(), picName, false);
            } else {
                resultPath = dir;
                resultFile = FileUtils.INSTANCE.fouceTouchCacheFile(context, dir, picName, false);
            }
        } else {
            resultPath = File.separator + AnConstants.FOLDER_ROOT + File.separator + AnConstants.FOLDER_IMAGES + File.separator;
            resultFile = FileUtils.INSTANCE.fouceTouchFile(picName);
        }

        BufferedOutputStream bos;
        try {
            FileOutputStream fos = new FileOutputStream(resultFile);
            if (type == TYPE.JPG) {
                bos = new BufferedOutputStream(fos);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                bos.flush();
                bos.close();
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultFile;
    }

    public File saveJPGBitmap(Context context, Bitmap bitmap, @NonNull String picName, int quality) {
        return saveBitmap(context, TYPE.JPG, bitmap, null, picName, quality);
    }

    public File savePNGBitmap(Context context, Bitmap bitmap, @NonNull String picName, int quality) {
        return saveBitmap(context, TYPE.PNG, bitmap, null, picName, quality);
    }

    public File saveJPGBitmap(Context context, Bitmap bitmap, @NonNull String picName) {
        return saveBitmap(context, TYPE.JPG, bitmap, null, picName, 80);
    }

    public File savePNGBitmap(Context context, Bitmap bitmap, @NonNull String picName) {
        return saveBitmap(context, TYPE.PNG, bitmap, null, picName, 80);
    }

    public File saveBitmap(Context context, @NonNull TYPE type, Bitmap bitmap, @NonNull String dir, @NonNull String picName, int quality) {
        File resultFile;
        if (context != null) {
            if (TextUtils.isEmpty(dir)) {
                resultPath = File.separator + AnConstants.FOLDER_IMAGES + File.separator;
                resultFile = FileUtils.INSTANCE.fouceTouchCacheFile(context, getAvatarDir(), picName, false);
            } else {
                resultPath = dir;
                resultFile = FileUtils.INSTANCE.fouceTouchCacheFile(context, dir, picName, false);
            }
        } else {
            resultPath = File.separator + AnConstants.FOLDER_ROOT + File.separator + AnConstants.FOLDER_IMAGES + File.separator;
            resultFile = FileUtils.INSTANCE.fouceTouchFile(picName);
        }

        BufferedOutputStream bos;
        try {
            FileOutputStream fos = new FileOutputStream(resultFile);
            if (type == TYPE.JPG) {
                bos = new BufferedOutputStream(fos);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                bos.flush();
                bos.close();
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, fos);
            }
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultFile;
    }

    /**
     * 首先保存图片 创建文件夹,保存图片到相册
     */
    public void saveBitmap2Gallery(Context context, @NonNull Bitmap bmp, @NonNull String name) {
        File fileStorage = new File(Environment.getExternalStorageDirectory(), name);
        if (!fileStorage.exists()) {
            fileStorage.mkdir();
        }
        String fileName = name + System.currentTimeMillis() + AnConstants.FILE_SUFFIX_JPEG;
        File file = new File(fileStorage, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
        //插入到系统图库
        String path = file.getAbsolutePath();
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        // 图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * 通过网络路径获取图片Byte数组
     * @param imageURL 网络图片地址
     * @return byte 字节数组
     * @throws IOException
     */
    public byte[] getByteOfURL(String imageURL) throws IOException {
        URL url = new URL(imageURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");   //设置请求方法为GET
        conn.setReadTimeout(5 * 1000);    //设置请求过时时间为5秒
        InputStream inputStream = conn.getInputStream();   //通过输入流获得图片数据
        return readInputStream(inputStream);
    }

    /**
     * 得到流数据
     * @param inputStream
     * @return 字节
     * @throws IOException
     */
    public byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 读取本地资源的图片
     * @param context
     * @param resId
     * @return 本地图片的Bitmap对象
     */
    public Bitmap readBitmapById(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /***
     * @param context
     * @param drawableId
     * @return根据资源文件获取Bitmap
     */
    public Bitmap readBitmapById(Context context, int drawableId,
                                 int screenWidth, int screenHight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inInputShareable = true;
        options.inPurgeable = true;
        InputStream stream = context.getResources().openRawResource(drawableId);
        Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
        return getBitmap(bitmap, screenWidth, screenHight);
    }

    /***
     * @param bitmap
     * @param screenWidth
     * @param screenHight
     * @return 等比例压缩图片
     */
    public Bitmap getBitmap(Bitmap bitmap, int screenWidth, int screenHight) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale = (float) screenWidth / w;
        // 保证图片不变形.
        matrix.postScale(scale, scale);
        // w,h是原图的属性.
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * @param drawable
     * @return 返回Bitmap对象，
     * @把drawable转化成bitmap对象
     */
    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565); //按指定参数创建一个空的Bitmap对象
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * @param bitmap
     * @return 把bitmap转化为drawable
     */
    public Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * @param bitmap
     * @param roundPx
     * @return得到圆角图片bitmap对象。
     */
    public Bitmap createRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * @param bitmap
     * @return获得带倒影的图片方法
     */
    public Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap,
                0, height / 2, width, height / 2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap,
                deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,
                bitmap.getHeight(), 0, bitmapWithReflection.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
// Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
// Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /**
     * @param src
     * @param watermark
     * @return图片水印的生成方法
     */
    public Bitmap createWatermarkBitmap(Bitmap src, Bitmap watermark) {
        String tag = "createBitmap";
        Log.d(tag, "create a new bitmap");
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        int ww = watermark.getWidth();
        int wh = watermark.getHeight();
        //create the new blankbitmap
        Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);//创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        //draw src into
        cv.drawBitmap(src, 0, 0, null);//在0，0坐标开始画入src
        //draw watermark into
        cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);//在src的右下角画入水印
        //save all clip
//        cv.save(Canvas.ALL_SAVE_FLAG);//保存
        cv.save();//最新android已经移除需要参数的场景
        //store
        cv.restore();//存储
        return newb;
    }

    /**
     * @param bitmap
     * @param w
     * @param h
     * @return、//放大缩小图片
     */
    public Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
//获得原始图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
//计算缩放比（目标宽高/原始宽高）
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出  
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbmp;
    }

    //设置收缩的图片，scale为收缩比例
    //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
    //这里缩小了1/2,但图片过大时仍然会出现加载不了,但系统中一个BITMAP最大是在10M左右,
    // 我们可以根据BITMAP的大小根据当前的比例缩小,即如果当前是15M,那如果定缩小后是6M,那么SCALE= 15/6
    public Bitmap zoomBitmap(Bitmap photo, int SCALE) {
        if (photo != null) {
            Bitmap smallBitmap = zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
            //释放原始图片占用的内存，防止out of memory异常发生
            photo.recycle();
            return smallBitmap;
        }
        return null;
    }

    //为了避免OOM异常，最好在解析每张图片的时候都先检查一下图片的大小
//    预估一下加载整张图片所需占用的内存。
//    为了加载这一张图片你所愿意提供多少内存。
//    用于展示这张图片的控件的实际大小。
//    当前设备的屏幕尺寸和分辨率
//    将BitmapFactory.Options连同期望的宽度和高度一起传递到到calculateInSampleSize方法
    public Bitmap decodeBitmapFromResource(Resources res, int resId,
                                           int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * @param path      绝对路径
     * @param reqWidth
     * @param reqHeight
     * @return
     * @ 为了避免OOM异常，最好在解析每张图片的时候都先检查一下图片的大小，从已知路径返回一张不会OOM的图片
     */
    public Bitmap decodeBitmapFromPath(String path,
                                       int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        File file = new File(path);
        if (file.exists())
            BitmapFactory.decodeFile(path, options);
        else
            return null;
        // 计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    //为上面方法服务
    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 把View绘制到Bitmap上
     * @param comBitmap 需要绘制的View
     * @param width     该View的宽度
     * @param height    该View的高度
     * @return 返回Bitmap对象
     * add by csj 13-11-6
     */
    public Bitmap getViewBitmap(View comBitmap, int width, int height) {
        Bitmap bitmap = null;
        if (comBitmap != null) {
            comBitmap.clearFocus();
            comBitmap.setPressed(false);

            boolean willNotCache = comBitmap.willNotCacheDrawing();
            comBitmap.setWillNotCacheDrawing(false);

            // Reset the drawing cache background color to fully transparent
            // for the duration of this operation
            int color = comBitmap.getDrawingCacheBackgroundColor();
            comBitmap.setDrawingCacheBackgroundColor(0);
            float alpha = comBitmap.getAlpha();
            comBitmap.setAlpha(1.0f);

            if (color != 0) {
                comBitmap.destroyDrawingCache();
            }

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            comBitmap.measure(widthSpec, heightSpec);
            comBitmap.layout(0, 0, width, height);

            comBitmap.buildDrawingCache();
            Bitmap cacheBitmap = comBitmap.getDrawingCache();
            if (cacheBitmap == null) {
                Log.e("view.ProcessImageToBlur", "failed getViewBitmap(" + comBitmap + ")",
                        new RuntimeException());
                return null;
            }
            bitmap = Bitmap.createBitmap(cacheBitmap);
            // Restore the view
            comBitmap.setAlpha(alpha);
            comBitmap.destroyDrawingCache();
            comBitmap.setWillNotCacheDrawing(willNotCache);
            comBitmap.setDrawingCacheBackgroundColor(color);
        }
        return bitmap;
    }

    public Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("Folder", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    public Bitmap convertViewToBitmap(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache(true);
    }

    //把布局变成Bitmap
    public Bitmap convertViewToBitmap2(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);

        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;
    }


    //获取状态栏高度
    public int getStatusBarHeight() {
        Context context = AnApplication.getApplication();
        int result = ScreenUtils.dp2px(20);
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //获取导航栏高度
    public int getNavigationBarHeight(Context context) {
        int result = 0;
        int resourceId = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    //截图，截取单个View
    public Bitmap screenshot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        return bitmap;
    }

    //截图，截取单个View
    public Bitmap screenshot(View view, int number) {
        Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.RGB_565);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        view.draw(c);
        return getRoundedCornerBitmap(bmp, number);
    }

    //Activity截屏
    public Bitmap activityShot(Activity activity) {
        /*获取windows中最顶层的view*/
        View view = activity.getWindow().getDecorView();
//允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
//获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        WindowManager windowManager = activity.getWindowManager();
//获取屏幕宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
//去掉状态栏
// Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeight, width, height - statusBarHeight);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width, height);
//销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 截取scrollview的屏幕(ListView等方法类似)
     * @param viewGroup
     */
    public Bitmap getBitmapByViewGroup(ViewGroup viewGroup) {
        int h = 0;
        Bitmap bitmap = null;
// 获取listView实际高度
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            h += viewGroup.getChildAt(i).getHeight();
        }
// 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(viewGroup.getWidth(), h, Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        viewGroup.draw(canvas);
        return bitmap;
    }

    /**
     * @param bitmap  待裁剪图片
     * @param roundDp 圆角大小，单位dp
     * @return 已裁剪图片
     */
    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundDp) {
        float roundPx = ScreenUtils.dp2px(roundDp);
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 自定义Bitmap 的背景颜色
     * <p>
     * getResouce.getColor
     */
    @SuppressLint("ResourceAsColor")
    public Bitmap drawBackgroundBitmap(@ColorRes int color, @Nullable Bitmap orginBitmap) {
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(orginBitmap.getWidth(), orginBitmap.getHeight(), orginBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), paint);
        canvas.drawBitmap(orginBitmap, 0, 0, paint);
        return bitmap;
    }

    //likebutton add
    public Drawable resizeDrawable(Context context, Drawable drawable, int width, int height) {
        Bitmap bitmap = getBitmap(drawable, width, height);
        return new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, width, height, true));
    }

    public Bitmap getBitmap(Drawable drawable, int width, int height) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat) {
            return getBitmap((VectorDrawableCompat) drawable, width, height);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable, width, height);
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
    }

    @TargetApi(21)
    private Bitmap getBitmap(VectorDrawable vectorDrawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private Bitmap getBitmap(VectorDrawableCompat vectorDrawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
}