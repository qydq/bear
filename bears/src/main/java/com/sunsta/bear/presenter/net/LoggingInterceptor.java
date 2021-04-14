package com.sunsta.bear.presenter.net;

import android.text.TextUtils;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.faster.DataService;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.ValueOf;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class LoggingInterceptor implements Interceptor {
    private final Charset UTF8 = Charset.forName(AnConstants.CONFIG.default_encode);
    public static String exceptionBody;

    @androidx.annotation.NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        exceptionBody = AnConstants.EMPTY;
        Request request = chain.request();
        if (chainFilterLogger(request)) {
            return chain.proceed(request);
        }
        RequestBody requestBody = request.body();
        String param = null;

        if (requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            param = buffer.readString(charset);
        }
        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            String body = null;
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    e.printStackTrace();
                }
            }
            body = buffer.clone().readString(charset);
            exceptionBody = body;
            String responseMessage = "收到响应response:{code:" + response.code() +
                    ",message:" + DataService.getInstance().defaultEmpty(response.message(), "no data") + ",time:" + tookMs + "}";
            String requestUrl = "请求url:" + response.request().url();
            String requestParam = "请求param:" + param;
            if (!TextUtils.isEmpty(param)) {
                LaLog.d(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY, requestUrl + "\n" + responseMessage, requestParam));
            } else {
                LaLog.d(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY, requestUrl + "\n" + responseMessage));
            }
            if (!TextUtils.isEmpty(body)) {
                if (body.length() > 1024 * 8) {
                    String body1 = body.substring(0, 1024 * 8);
                    String body2 = body.substring(1024 * 8);
                    LaLog.d(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY, "数据body:" + body1));
                    if (TextUtils.isEmpty(body2)) {
                        LaLog.d(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY, body2));
                    }
                } else {
                    LaLog.d(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY, "数据body:" + body));
                }
            }
        }
        return response;
    }

    private boolean chainFilterLogger(Request request) {
        String url = request.url().url().toString();
        return url.contains("urOwnerFilter/sku/skuCode");
    }
}