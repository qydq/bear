package com.sunsta.bear.presenter.net;

import com.sunsta.bear.model.entity.ResponseResultMode;
import com.sunsta.bear.faster.LaLog;

import org.reactivestreams.Subscriber;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import io.reactivex.functions.Consumer;


public abstract class InternetSubscriber<T> implements Subscriber<ResponseResultMode<T>>, Consumer {
    @Override
    public void onNext(ResponseResultMode<T> result) {
        if (result.isSuccess()) {
            onSuccess(result.getResult());
        } else {
            onError(result.code, result.getMsg());
        }
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        //在这里做全局的错误处理
        if (e instanceof ConnectException ||
                e instanceof SocketTimeoutException ||
                e instanceof TimeoutException) {
            onError(-9999, "网络错误");
        } else if (e instanceof InternetException) {
            //自定义的ResultException
            //由于返回200,300返回格式不统一的问题，自定义GsonResponseBodyConverter凡是300的直接抛异常
            onError(((InternetException) e).getErrCode(), "JustNetException");
            LaLog.e("---------errorCode-------" + ((InternetException) e).getErrCode());
        }
    }


    public abstract void onSuccess(T t);

    public abstract void onError(int errorCode, String errorMsg);
}

