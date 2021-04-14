package com.sunsta.bear.presenter.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.sunsta.bear.BuildConfig;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class BaseInterceptor implements Interceptor {
    private static final String ALI_TOKEN = "sunst.alidd.forever-just";

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String method = request.method();//请求方式
        //get請求
        if ("GET".equals(method)) {
            HttpUrl.Builder httpBuilder = request.url().newBuilder();
            //请求参数添加version和token
            httpBuilder.addQueryParameter("version", BuildConfig.VERSION_NAME);
            httpBuilder.addQueryParameter("token", ALI_TOKEN);
            //重新构建Request
            request = request.newBuilder().url(httpBuilder.build()).build();
        }
        //post請求
        if ("POST".equals(method)) {
            if (request.body() instanceof FormBody) {
                //表单形式提交
                FormBody.Builder bodyBuilder = new FormBody.Builder();
                FormBody formBody = (FormBody) request.body();
                //把原来的参数添加到新的构造器，（因为没找到直接添加，所以就new新的）
                for (int i = 0; i < formBody.size(); i++) {
                    bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                }
                //请求参数添加version和token
                formBody = bodyBuilder.addEncoded("version", BuildConfig.VERSION_NAME).build();
                //重新构建Request
                request = request.newBuilder().post(formBody).build();

            } else if (request.body() instanceof MultipartBody) {
                //请求体为MultipartBody的实现
                MultipartBody oldBodyMultipart = (MultipartBody) request.body();
                List<MultipartBody.Part> oldPartList = oldBodyMultipart.parts();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                //将原有的数据加入进去
                for (MultipartBody.Part part : oldPartList) {
                    builder.addPart(part);
                }
                Request.Builder requestBuilder = request.newBuilder();
                //请求参数添加version和token
                builder.addFormDataPart("version", BuildConfig.VERSION_NAME);
//                String mToken = NativeUserMode.getInstance().getAuthToken();
//                if(TextUtils.isEmpty(mToken)){
//                    mToken = ALI_TOKEN;
//                }
                if (!TextUtils.isEmpty("sunst")) {
                    requestBuilder.addHeader("Authorization", ALI_TOKEN);
                }
                //重新构建Request
                request = requestBuilder.post(builder.build()).build();
            } else {
                //默认json格式数据提交
                Buffer buffer = new Buffer();
                request.body().writeTo(buffer);
                String oldParams = buffer.readUtf8(); //读取传入的json字符串
                buffer.close();
                if (!TextUtils.isEmpty(oldParams)) {
                    //请求参数添加version和token
                    Map hashMap = new Gson().fromJson(oldParams, Map.class);
                    hashMap.put("version", BuildConfig.VERSION_NAME);
                    String newJsonParams = new Gson().toJson(hashMap);
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    Request.Builder builder = request.newBuilder();
                    // 有的请求不用添加token请求头, 如登陆界面不带token
//                    builder.addHeader("Authorization", ALI_TOKEN);
                    request = builder.post(RequestBody.create(JSON, newJsonParams)).build();

                }
            }
        }

        return chain.proceed(request);
    }
}