package com.sunsta.bear.engine;

import android.graphics.Bitmap;

import com.sunsta.bear.callback.ProcessCallback;


public interface IController {
    void process(int viewWidth);

    Bitmap getTargetBitmap();

    void setProcessCallback(ProcessCallback callback);
}
