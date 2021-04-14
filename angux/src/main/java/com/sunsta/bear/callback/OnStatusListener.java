package com.sunsta.bear.callback;

/**
 * 通用成功失败标志
 */
public interface OnStatusListener {
    void success(String reply);

    void failure(String error);
}