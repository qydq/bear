package com.sunsta.bear.contract;

import androidx.annotation.NonNull;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.faster.FileUtils;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.listener.DownloaderListener;
import com.sunsta.bear.model.entity.ResponseDownloader;
import com.sunsta.bear.presenter.BaseInternetApi;
import com.sunsta.bear.presenter.BaseInternetFileApi;
import com.sunsta.bear.callback.OnStatusListener;
import com.sunsta.bear.presenter.net.CustomizeException;
import com.sunsta.bear.presenter.net.DownloadProgressInterceptor;
import com.sunsta.bear.presenter.net.InternetClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by qydq on 2018/1/17.
 * github.com/qydq/ali-aw-base
 */
public class DownloaderBackground {
    public static void getHtml(String BASE_URL,
                               @NonNull String requestURL,
                               @NonNull final OnStatusListener callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).build();
        final BaseInternetApi service = retrofit.create(BaseInternetApi.class);
        Call<ResponseBody> call = service.tReallyGetHtmlT(requestURL);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    try {
                        String result = Objects.requireNonNull(responseBody).string();
                        callback.success(result);
                    } catch (IOException e) {
                        LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.failure(t.getMessage());
                t.printStackTrace();
            }
        });
    }


    /**
     * 利用InternetClient去下载文件，名字先忽略，原理是Retrofit结合RxJava
     */
    public static void downloadAPK(@NonNull ResponseDownloader downloader, DownloaderListener listener, Observer<InputStream> observerIp) {
        DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(listener, downloader);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(AnConstants.file_download_timeout, TimeUnit.SECONDS)
                .build();
        try {
            InternetClient.getInstance().setOkhttpClient(client);
            Retrofit retrofit = InternetClient.getInstance().getRetrofit();
            retrofit.create(BaseInternetFileApi.class)
                    .observableReallyDownload(downloader.getUrl())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .map(ResponseBody::byteStream)
                    .doOnNext(inputStream -> {
                        try {
                            FileUtils.INSTANCE.writeFile(inputStream, downloader.getDownloadResultFile());
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new CustomizeException(e.getMessage(), e);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observerIp);
        } catch (Exception e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        }
    }
}