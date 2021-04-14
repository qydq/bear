package com.sunsta.bear.presenter.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.sunsta.bear.AnConstants;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.model.entity.ResponseNetErrorMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class NetBodyResponse<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    public NetBodyResponse(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        if (!TextUtils.isEmpty(response) && null != adapter) {
            return adapter.fromJson(response);
        } else {
            //抛一个自定义ResultException 传入失败时候的状态码，和信息
            ResponseNetErrorMode errorResponse = null;
            try {
                errorResponse = gson.fromJson(response, ResponseNetErrorMode.class);
                throw new ExecutionException("---数据转换失败---" + errorResponse.getMsg(), new Throwable());
            } catch (ExecutionException e) {
                LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
            }
            return (T) errorResponse;
        }
//        return taskDetail(response);
    }

    private T taskDetail(String response) throws IOException {
        //先将返回的json数据解析到Response中，如果code==200，则解析到我们的实体基类中，否则抛异常
        if (verifyResponse(response)) {
            JsonReader responseReader = gson.newJsonReader(new StringReader(response));
            return adapter.read(responseReader);
        } else {
// {"code":400,"data":null,"msg":"手机号已经被注册"}
// {"code": 200,"data": true,"msg": "OK" }
            JSONObject obj = null;
            try {
                obj = new JSONObject(response);
                if (obj.isNull("data")) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(String.class, new NetBodyString());
                    Gson buildGson = gsonBuilder.create();
//                    ErrorMsg errorResponse = buildGson.fromJson(response, ErrorMsg.class);
//{"code":400,"msg":"手机号已经被注册"}
//                    String containNullResult = buildGson.toJson(errorResponse);
//                    JsonReader responseReader = buildGson.newJsonReader(new StringReader(containNullResult));
//                    return adapter.read(responseReader);
                }
            } catch (JSONException e) {
                LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
            }
            return adapter.fromJson(response);
        }
    }

    private boolean verifyResponse(String body) {
        boolean result = false;
        try {
            JSONObject responseJSONObject = new JSONObject(body);
            int resultCode = responseJSONObject.getInt("code");
            if (resultCode == 200) {
                result = true;
            } else {
                result = false;
            }
        } catch (JSONException e) {
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
        }
        return result;
    }
}