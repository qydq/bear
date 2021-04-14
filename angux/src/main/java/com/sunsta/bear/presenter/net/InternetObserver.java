package com.sunsta.bear.presenter.net;

import android.text.TextUtils;

import androidx.multidex.BuildConfig;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.StringUtils;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.callback.OnStatusListener;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.HttpException;

public class InternetObserver extends DisposableObserver<ResponseBody> {
    private String TAG = getClass().getName();
    private OnStatusListener mListener;

    public InternetObserver(OnStatusListener listener) {
        mListener = listener;
    }

    public InternetObserver request(Observable observable) {
        InternetClient.getInstance().onSubscribe(observable, this);
        return this;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onComplete() {

    }

    /**
     * 对错误进行统一处理
     */
    @Override
    public void onError(Throwable e) {
        try {
            String errorMessage;
            if (e instanceof SocketTimeoutException) {
                errorMessage = StringUtils.getString(R.string.an_socket_connect_timeout);
            } else if (e instanceof ConnectException) {
                errorMessage = StringUtils.getString(R.string.an_net_connect_timeout);
            } else if (e instanceof SSLHandshakeException) {
                errorMessage = StringUtils.getString(R.string.an_net_ssl_exception);
            } else if (e instanceof HttpException) {
                int code = ((HttpException) e).code();
                if (code == 504) {
                    errorMessage = StringUtils.getString(R.string.an_net_server_error);
                } else if (code == 404) {
                    errorMessage = StringUtils.getString(R.string.an_net_address_not_exist);
                } else {
                    errorMessage = StringUtils.getString(R.string.an_net_error);
                }
            } else if (e instanceof UnknownHostException) {
                errorMessage = StringUtils.getString(R.string.an_unknown_host_exception);
            } else {
                errorMessage = "other error:" + e.getMessage();
            }
            mListener.failure(errorMessage);
            LaLog.e(ValueOf.logLivery(TAG + AnConstants.VALUE.LOG_LIVERY_ERROR, errorMessage));
        } catch (Exception ex) {
            ex.printStackTrace();
            LaLog.e(ValueOf.logLivery(TAG + AnConstants.VALUE.LOG_LIVERY_EXCEPTION, ex.getMessage()));
        }
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        String result = parseResponse(responseBody);
        if (TextUtils.isEmpty(result)) {
            LaLog.e(ValueOf.logLivery(TAG + AnConstants.VALUE.LOG_LIVERY_ERROR, StringUtils.getString(R.string.an_string_parse_error)));
            mListener.failure(StringUtils.getString(R.string.an_string_parse_error));
        } else {
            try {
                if (BuildConfig.DEBUG) {
                    LaLog.d(ValueOf.logLivery(TAG + AnConstants.VALUE.LOG_LIVERY, result));
                }
                mListener.success(result);
            } catch (Exception e) {
//                e.printStackTrace();
                LaLog.e(ValueOf.logLivery(TAG + AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
                mListener.failure(StringUtils.getString(R.string.an_string_parse_error));
            } finally {
                responseBody.close();
            }
        }
    }

    private String parseResponse(ResponseBody responseBody) {
        String result = AnConstants.EMPTY;
        if (responseBody != null) {
            Charset UTF8 = Charset.forName(AnConstants.CONFIG.default_encode);
            BufferedSource source = responseBody.source();
            try {
                source.request(Long.MAX_VALUE);
            } catch (IOException e) {
                responseBody.close();
                e.printStackTrace();
            }
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            result = buffer.clone().readString(charset);
        }
        return result;
    }
}