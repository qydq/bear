package com.sunsta.bear.faster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.sunsta.bear.faster.callback.OnWebViewListener;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：
 * 1. 为了让webview点击link能够下载，需要添加 {@link DownloadListener}
 * 2. WebView 重定向判断可以用 {@link WebView#getHitTestResult()}
 * 3. HTTPS 页面中的 HTTP 元素不显示，如图片，可以用 {@link WebSettings#setMixedContentMode(int)}
 * 4. Webview 开启硬件加速会出现各种坑，可以在
 * { WebViewClient#onPageStarted(WebView, String, android.graphics.Bitmap)}
 * 中关闭硬件加速，在
 * { WebViewClient#onPageFinished(WebView, String)}
 * 中开启硬件加速
 * 5.WebTake和对应自定义的WebView中都可以设置setWebTakeCallBack
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 1.0 |   2016/4/9           |   修改WebView为Builder设计模式配置
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class WebViewHelper {
    private WebView lWebView;
    private final WebSettings lWebSettings;

    //WebTake中需要监听的WebView的回掉
    private OnWebViewListener WebTakeCallBack;

    private WebViewHelper(Builder builder) {
        this.lWebView = builder.bWebView;
        this.lWebSettings = builder.bWebSettings;
    }

    public static class Builder {
        private WebView bWebView;
        private WebSettings bWebSettings;
        private boolean blockNetworkImage = false;

        //NonNull后不需要再对webView判空
        public Builder(@NonNull WebView bWebView) {
            this.bWebView = bWebView;
            this.bWebSettings = bWebView.getSettings();
        }

        public WebViewHelper build() {
            return new WebViewHelper(this);
        }

        public Builder initSettings() {
            baseSettings();
            return this;
        }

        /*播放视频的关键代码，gif图片待测试*/
        public Builder supportVideo() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            } else {
                bWebSettings.setPluginState(WebSettings.PluginState.ON);
            }
            // Enable remote debugging via chrome://inspect
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                bWebView.setWebContentsDebuggingEnabled(true);
            }
            bWebSettings.setDisplayZoomControls(false);
            bWebSettings.setMediaPlaybackRequiresUserGesture(false);
            //如果没有这两行代码，html5 的video空间显示一块黑屏，不能播放，经过尝试没有不能显示
            bWebView.buildDrawingCache(true);
            bWebView.setDrawingCacheEnabled(true);
            bWebView.buildLayer();

            return this;
        }

        /*开启WebView缩放*/
        public Builder supportShrink() {
            bWebSettings = bWebView.getSettings();
            bWebSettings.setSupportZoom(true);// 支持缩放
            bWebSettings.setSupportMultipleWindows(true);
            // 屏幕自适应网页,如果没有这个，在低分辨率的手机上显示可能会异常
            bWebSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
            //设置此属性，可任意比例缩放
            bWebSettings.setUseWideViewPort(true);
            bWebSettings.setLoadWithOverviewMode(true);
            // 支持双指缩放,隐藏缩放按钮
            bWebSettings.setBuiltInZoomControls(true);
            return this;
        }

        /*WebView下载设置*/
        public Builder downloadListener(DownloadListener downloadListener) {
            bWebView.setDownloadListener(downloadListener);
            return this;
        }

        // 关闭硬件加速
        public Builder closeHardwareAcceleration() {
            bWebView.setLayerType(View.LAYER_TYPE_NONE, null);
            return this;
        }

        /*智能开启硬件加速*/
        public Builder hardwareAccelerationSetting() {
            if (bWebView.getHeight() < 4096 && bWebView.getWidth() < 4096) {
                bWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                bWebView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
            return this;
        }


        //过渡期前将WebView的硬件加速临时关闭，过渡期后再开启,webView渲染页面更加快速，拖动也更加顺滑。
        // 但有个副作用就是容易会出现页面加载白块同时界面闪烁现象,如果Application和Activity都声明了
        // HardwareAccelerate，但是由于某些特殊原因，一些View不需要硬件加速，那么在View里面设置：
//         mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);如dontAcceleration方法
        /*动态硬件加速*/
        public Builder accelerationWindow(@NonNull Window window) {
            window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            return this;
        }

        /**
         * 设置某个视图不开启硬件加速，目的是为了fix有的页面加载空白。
         */
        public Builder dontAcceleration(@NonNull View view) {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            return this;
        }

        /**
         * 设置是否开启缓存，的缓存模式。
         * 默认是，这里使用NetBroadcastReceiverUtils工具类，
         * 如果有网，LOAD_CACHE_ONLY.
         * 如果没有网，LOAD_CACHE_ELSE_NETWORK。
         */
        public Builder setCacheMode(@NonNull Context context) {
            if (bWebSettings != null) {
                if (!NetBroadcastReceiverUtils.isConnectedToInternet(context)) {
                    bWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
                } else {
                    bWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                }
            }
            return this;
        }

        /**
         * 提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，再进行加载图片
         */
        public Builder pictureSpeed() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                bWebSettings.setLoadsImagesAutomatically(true);
            } else {
                bWebSettings.setLoadsImagesAutomatically(false);
            }
            return this;
        }

        public Builder loadUrl(@NonNull String requestURL) {
            bWebView.loadUrl(requestURL);
            return this;
        }

        public Builder setWebClient(@NonNull WebViewClient bWebClient) {
            bWebView.setWebViewClient(bWebClient);
            return this;
        }

        public Builder setWebChromeClient(@NonNull WebChromeClient bWebChromeClient) {
            bWebView.setWebChromeClient(bWebChromeClient);
            return this;
        }

        public Builder setBlockNetworkImage(Boolean blockNetworkImage) {
            this.blockNetworkImage = blockNetworkImage;
            bWebSettings.setBlockNetworkImage(blockNetworkImage);//开启缓存机制
            return this;
        }


        @SuppressLint("SetJavaScriptEnabled")
        private void baseSettings() {
            bWebView.requestFocusFromTouch();

            //使可以调用JavaScript功能。
            bWebSettings.setJavaScriptEnabled(true);
            //漏洞原理当用户选择保存在WebView中输入的用户名和密码，
            // 则会被明文保存到应用数据目录的databases/webview.db中。
            // 如果手机被root就可以获取明文保存的密码，造成用户的个人敏感数据泄露
            bWebSettings.setSavePassword(false);
            bWebSettings.setSaveFormData(true);// 保存表单数据
            bWebSettings.setDefaultTextEncodingName("UTF-8");
            bWebSettings.setTextSize(WebSettings.TextSize.NORMAL);

            //这句话必须保留，否则无法播放优酷视频网页。。其他的可以
            bWebSettings.setDomStorageEnabled(true);
            bWebSettings.setDatabaseEnabled(true);
            bWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            bWebSettings.setAppCacheEnabled(true);
            bWebSettings.setAppCacheMaxSize(16 * 1024 * 1024);

            //窗口相关设置
            bWebSettings.setAllowFileAccess(true);//设置支持文件流
            bWebSettings.setAllowFileAccessFromFileURLs(false);
            bWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
            bWebSettings.setAllowUniversalAccessFromFileURLs(false);
            bWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//设置提高渲染的优先级

            /*
             * 用WebView显示图片，可使用这个参数 设置网页布局类型：
             * 1、LayoutAlgorithm.NARROW_COLUMNS ：适应内容大小
             * 2、LayoutAlgorithm.SINGLE_COLUMN : 适应屏幕，内容将自动缩放
             * */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                bWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
//                bWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            } else {
                bWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//                bWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            }

            bWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            bWebView.removeJavascriptInterface("accessibility");
            bWebView.removeJavascriptInterface("accessibilityTraversal");

            if (blockNetworkImage) {
                bWebSettings.setBlockNetworkImage(true);
            } else {
                bWebSettings.setBlockNetworkImage(false);
            }
        }
    }

    public void loadUrl(@NonNull String requestURL) {
        lWebView.loadUrl(requestURL);
    }

    public void setWebClient(@NonNull WebViewClient bWebClient) {
        lWebView.setWebViewClient(bWebClient);
    }

    public void setWebChromeClient(@NonNull WebChromeClient bWebChromeClient) {
        lWebView.setWebChromeClient(bWebChromeClient);
    }

    public void setWebTakeCallBack(@NonNull OnWebViewListener WebTakeCallBack) {
        this.WebTakeCallBack = WebTakeCallBack;
    }

    public OnWebViewListener getWebTakeCallBack() {
        return WebTakeCallBack;
    }

    /**
     * 设置图片阻塞模式。
     */
    public void setBlockNetWorkImage(boolean netWorkImage) {
        lWebSettings.setBlockNetworkImage(false);
    }

    /**
     * 一般在onPageFinished中，关闭图片加载阻塞
     */
    public void setLoadsImagesAutomatically() {
        if (!lWebSettings.getLoadsImagesAutomatically()) {
            lWebSettings.setLoadsImagesAutomatically(true);
        }
        setBlockNetWorkImage(false);
    }

    /**
     * 关闭硬件加速
     */
    public void closeHardwareAcceleration() {
        lWebView.setLayerType(View.LAYER_TYPE_NONE, null);
    }

    /**
     * 释放WebView的至软
     */
    public void releaseWebViewResource() {
        lWebView.removeAllViews();
        ((ViewGroup) lWebView.getParent()).removeView(lWebView);
        lWebView.setTag(null);
        lWebView.clearHistory();
        lWebView.destroy();
        lWebView = null;
    }
    /*-----------------生命周期管理-----------*/

    /**
     * 一般的在初始化WebView的时候关闭硬件加速。
     * 在onPageFinished中开启硬件加速。
     */
    public void onPageStarted() {
        closeHardwareAcceleration();
    }

    /*智能开启硬件加速*/
    public void onPageFinished() {
        if (lWebView.getHeight() < 4096 && lWebView.getWidth() < 4096) {
            lWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            lWebView.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    public void onDestory() {
        releaseWebViewResource();
    }
}
