package com.sunsta.bear.faster.callback;

import android.webkit.WebView;

/**
 * Created by qy on 2018/3/8.
 */

public interface OnWebViewListener {
    void onProgressChanged(WebView view, int newProgress);

    void shouldOverrideUrlLoading(WebView view, String url);

    void onLoadResource(WebView view, String url);

    void onPageFinished(WebView view, String url);

    void onReceivedError(WebView view, int errorCode, String description, String failingUrl);
}
