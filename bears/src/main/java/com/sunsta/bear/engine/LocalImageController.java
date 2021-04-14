package com.sunsta.bear.engine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sunsta.bear.faster.ThreadPool;

import java.io.File;


/**
 * 本地图片加载控制器
 */

public class LocalImageController extends BaseImageController {

    private String imagePath;

    public LocalImageController(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    protected void loadImage(final int viewWidth) {
        if (imagePath.isEmpty() || !new File(imagePath).exists()) {
            return;
        }
        ThreadPool.getInstance().getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                loadLocalImage(imagePath, viewWidth);
            }
        });
    }

    /**
     * 加载本地图片
     *
     * @param imagePath
     * @param viewWidth
     */
    private void loadLocalImage(String imagePath, int viewWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, options);

        int outWidthPx = options.outWidth;
        int outHeightPx = options.outHeight;

        float scale = 1.0f * viewWidth / outWidthPx;
        int scaleWidth = (int) (scale * outWidthPx);
        int scaleHeight = (int) (scale * outHeightPx);

        options.inSampleSize = calculateInSampleSize(outWidthPx, outHeightPx, scaleWidth, scaleHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        // 目标bitmap
        targetBitmap = Bitmap.createScaledBitmap(bmp, scaleWidth, scaleHeight, true);
        bmp = null;
        callback.onProcessFinished(scaleWidth, scaleHeight);

        isProcessing = false;
    }
}
