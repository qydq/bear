package com.sunsta.bear.presenter.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.faster.DataService;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.StringUtils;
import com.sunsta.bear.faster.ValueOf;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public abstract class InternetException extends Throwable implements Consumer<Throwable> {
    private int code = 0;//0默认（网络超时，安全证书异常），-1000JSON解析错误，-2000未知异常
    private String serverMessage = "";
    private String liveryMessage = "";

    @Override
    public void accept(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException) {
            liveryMessage = StringUtils.getString(R.string.an_socket_connect_timeout);
        } else if (throwable instanceof ConnectException) {
            liveryMessage = StringUtils.getString(R.string.an_net_connect_timeout);
        } else if (throwable instanceof SSLHandshakeException) {
            liveryMessage = StringUtils.getString(R.string.an_net_ssl_exception);
        } else if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            code = httpException.code();
            if (code == 503) {
                liveryMessage = StringUtils.getString(R.string.an_server_not_available);
            } else if (code == 504) {
                liveryMessage = StringUtils.getString(R.string.an_net_server_error);
            } else if (code == 400) {
                liveryMessage = StringUtils.getString(R.string.an_request_parameter_error);
            } else if (code == 404) {
                liveryMessage = StringUtils.getString(R.string.an_net_address_not_exist);
            } else if (code == 405) {
                liveryMessage = StringUtils.getString(R.string.an_request_method_error);
            } else {
                liveryMessage = StringUtils.getString(R.string.an_net_error);
            }
            try {
                ResponseBody rb = Objects.requireNonNull(httpException.response()).errorBody();
                serverMessage = Objects.requireNonNull(rb).string();
            } catch (Exception ex) {
                ex.printStackTrace();
                liveryMessage = "当前网络异常，请稍候再试";
            }
        } else if (throwable instanceof UnknownHostException) {
            liveryMessage = StringUtils.getString(R.string.an_unknown_host_exception);
        } else if (throwable instanceof JsonParseException
                || throwable instanceof JSONException) {
            code = -1000;
            liveryMessage = StringUtils.getString(R.string.an_json_parse_exception);
            serverMessage = LoggingInterceptor.exceptionBody;
        } else {
            code = -2000;
            liveryMessage = "未知异常";
            if (ValueOf.intercept) {
                ValueOf.intercept = false;
            }
        }
        if (TextUtils.isEmpty(serverMessage)) {
            serverMessage = throwable.getLocalizedMessage();
            if (TextUtils.isEmpty(serverMessage)) {
                serverMessage = throwable.getMessage();
            }
        }
        String result = liveryMessage;
        if (!TextUtils.isEmpty(serverMessage)) {
            LaLog.e(AnConstants.VALUE.LOG_LIVERY_EXCEPTION + "：InternetException：Info：" + liveryMessage);
            Gson gson = new Gson();
            //json数据返回回去
            if (DataService.getInstance().checkJson(gson, serverMessage)) {
                result = liveryMessage + "#" + serverMessage;
                LaLog.e(AnConstants.VALUE.LOG_LIVERY_EXCEPTION + "：JSON data：" + serverMessage);
//                ResponseNetErrorMode error = new ResponseNetErrorMode();
//                error = gson.fromJson(liveryMessage, ResponseNetErrorMode.class);
            } else {
                LaLog.e(AnConstants.VALUE.LOG_LIVERY_EXCEPTION + "：InternetException：More Info：" + serverMessage);
//                result = liveryMessage + "#" + serverMessage;
                result = liveryMessage;
            }
        } else {
            LaLog.e(AnConstants.VALUE.LOG_LIVERY_EXCEPTION + "InternetException：Info：" + liveryMessage);
        }
        onError(code, result);
    }


    public int getErrCode() {
        return code;
    }

    public abstract void onError(int code, String msg);
}