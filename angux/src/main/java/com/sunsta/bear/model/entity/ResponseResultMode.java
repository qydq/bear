package com.sunsta.bear.model.entity;

import java.io.Serializable;

public class ResponseResultMode<T> implements Serializable {
    public int code;
    private boolean isSuccess;
    private T result;
    private String msg;
    private T data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public T getResult() {
        return result;
    }


    public void setResult(T result) {
        this.result = result;

    }

    public boolean isSuccess() {
        return code == 200;
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}