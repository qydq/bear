package com.sunsta.bear.model.entity;

import android.text.TextUtils;

public class ResponseNetErrorMode<T> {
    public String code;
    private boolean isSuccess;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        if (!TextUtils.isEmpty(code)) {
            if (code.equals("failure")) {
                return false;
            } else if (code.equals("success") || code.equals("200")) {
                return true;
            }
        }
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}