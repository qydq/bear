package com.sunsta.bear.presenter.net;

import androidx.annotation.NonNull;

import com.sunsta.bear.listener.DownloaderListener;
import com.sunsta.bear.model.entity.ResponseDownloadProgressBody;
import com.sunsta.bear.model.entity.ResponseDownloader;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Interceptor for download
 * Created by sunst on 16/5/11.
 */
public class DownloadProgressInterceptor implements Interceptor {

    private DownloaderListener listener;
    private ResponseDownloader downloader;

    public DownloadProgressInterceptor(DownloaderListener listener, ResponseDownloader downloader) {
        this.listener = listener;
        this.downloader = downloader;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder().body(new ResponseDownloadProgressBody(originalResponse.body(), listener, downloader)).build();
    }
}