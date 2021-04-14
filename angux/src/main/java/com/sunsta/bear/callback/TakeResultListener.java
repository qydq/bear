package com.sunsta.bear.callback;

import com.sunsta.bear.model.entity.TResult;


/**
 * 拍照结果监听接口
 */
public interface TakeResultListener {
    void takeSuccess(TResult result);

    void takeFail(TResult result, String msg);

    void takeCancel();
}