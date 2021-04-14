package com.sunsta.bear.presenter.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.sunsta.bear.AnConstants;
import com.sunsta.bear.faster.DataService;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.model.entity.ResponseNetErrorMode;

import java.io.IOException;

import io.reactivex.functions.Consumer;
import retrofit2.HttpException;

public abstract class InternetException extends Throwable implements Consumer<Throwable> {
    private int errorCode = 0;
    private String errorMsg = "";

    @Override
    public void accept(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            errorCode = httpException.code();
            try {
                errorMsg = httpException.response().errorBody().string();
            } catch (IOException e1) {
                e1.printStackTrace();
                errorMsg = "当前网络异常，请稍候再试";
                LaLog.e(AnConstants.VALUE.LOG_LIVERY_EXCEPTION + "- errorMsg=" + errorMsg);
                onError(errorCode, httpException.message());
                return;
            }
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, "throwable.getMessage()" + "- errorCode=" + errorCode + ";msg=" + errorMsg));
            Gson gson = new Gson();
            if (!TextUtils.isEmpty(errorMsg)) {
                if (DataService.getInstance().checkJson(gson, errorMsg)) {
                    try {
                        ResponseNetErrorMode result = gson.fromJson(errorMsg, ResponseNetErrorMode.class);
                        onError(errorCode, result.getMsg());
                        LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, "Internet JSONParseException", "message is " + result.getMsg()));
                    } catch (Exception e) {
                        LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, "Internet JSONParseException", "message is " + e.getMessage()));
                        onError(-1000, errorMsg + "##" + e.getMessage());
                    }
                    return;
                } else {
                    onError(errorCode, errorMsg);
                    return;
                }
            }

            String throwableMessage = throwable.getMessage();
            if (!TextUtils.isEmpty(throwableMessage)) {
                if (DataService.getInstance().checkJson(gson, throwableMessage)) {
                    try {
                        ResponseNetErrorMode result = gson.fromJson(throwableMessage, ResponseNetErrorMode.class);
                        onError(errorCode, result.getMsg());
                        LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, "Internet JSONParseException", "message is " + result.getMsg()));
                    } catch (Exception e) {
                        LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, "Internet JSONParseException", "message is " + e.getMessage()));
                        onError(-1000, "Internet ParseException=" + e.getMessage());
                    }
                    return;
                } else {
                    onError(errorCode, throwableMessage);
                    return;
                }
            } else {
                onError(errorCode, "未知异常");
                return;
            }
        } else {
            if (ValueOf.intercept) {
                ValueOf.intercept = false;
            } else {
                LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, "throwable.getMessage()" + throwable.getMessage()));
            }
            onError(-2000, throwable.getMessage());
        }
    }


    public int getErrCode() {
        return errorCode;
    }

    public abstract void onError(int code, String msg);
}