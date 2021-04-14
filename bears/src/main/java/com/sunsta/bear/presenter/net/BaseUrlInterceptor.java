package com.sunsta.bear.presenter.net;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.ValueOf;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.sunsta.bear.AnConstants.URL.BASE_URL;

public class BaseUrlInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl oldHttpUrl = request.url();
        Request.Builder builder = request.newBuilder();
        List<String> requestHead = request.headers("BaseUrl");
        if (requestHead.size() > 0) {
            builder.removeHeader("BaseUrl");
            String headerValue = requestHead.get(0);
            HttpUrl turnBaseURL;
            if ("pythonType".equals(headerValue)) {
                turnBaseURL = HttpUrl.parse(BASE_URL);
            } else if ("phpType".equals(headerValue)) {
                turnBaseURL = HttpUrl.parse("https://your_owner_php_type_address/");
            } else if ("JavaServer".equals(headerValue)) {
                turnBaseURL = HttpUrl.parse("https://your_owner_java_server_address/");
            } else {
                turnBaseURL = oldHttpUrl;
            }
//            LaLog.d(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY, "httpUrl=" + turnBaseURL));
            HttpUrl currentURL = oldHttpUrl.newBuilder().scheme(turnBaseURL.scheme()).host(turnBaseURL.host()).port(turnBaseURL.port()).build();
            Request currentRequest = builder.url(currentURL).build();
            return chain.proceed(currentRequest);
        } else {
            return chain.proceed(request);
        }

    }
}