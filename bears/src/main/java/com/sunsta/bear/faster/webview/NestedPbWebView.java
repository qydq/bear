package com.sunsta.bear.faster.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.WebViewHelper;
import com.sunsta.bear.faster.callback.OnWebViewListener;
import com.sunsta.bear.faster.bluetooth.LaBluetoothJs;

/**
 * 说明：作者,qy（晴雨）该WebView重载后有两个功能，
 * 1，NestedScrool冲突事件解决；2，配合ProgressBar 的MaterailDesign
 * Created by s8632912  on 2017/11/9.
 * github.com/qydq/ali-aw-base
 */
public class NestedPbWebView extends WebView implements NestedScrollingChild {
    /*
    * 功能2。
    * */
    private NestProgressBar progressBar;//进度条的矩形（进度线）
    private Handler handler;
    private WebView mWebView;
    private Context mContext;
    private long startTime;

    private OnWebViewListener WebTakeCallBack;
    private WebViewHelper WebTake;

    /**
     * 刷新界面（此处为加载完成后进度消失）
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
        }
    };

    /*
    * 功能1
    * */
    public static final String TAG = NestedPbWebView.class.getSimpleName();

    private int mLastMotionY;

    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];

    private int mNestedYOffset;


    private NestedScrollingChildHelper mChildHelper;

    public void setWebTakeCallBack(@NonNull OnWebViewListener WebTakeCallBack) {
        this.WebTakeCallBack = WebTakeCallBack;
    }

    public NestedPbWebView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public NestedPbWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public NestedPbWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        //实例化进度条
        progressBar = new NestProgressBar(mContext);
        //设置进度条的size
        progressBar.setLayoutParams(new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //刚开始时候进度条不可见
        progressBar.setVisibility(GONE);
        //把进度条添加到webView里面
        addView(progressBar);
        //初始化handle
        handler = new Handler();
        mWebView = this;

        WebTake = new WebViewHelper.Builder(this).
                initSettings().supportVideo().supportShrink()
                .setWebClient(new MyWebClient())
                .setWebChromeClient(new MyWebChromeClient())
                .build();
        WebTakeCallBack = WebTake.getWebTakeCallBack();
    }

    private class MyWebChromeClient extends WebChromeClient {
//        @Override
//        public Bitmap getDefaultVideoPoster() {
//            if (this == null) {
//                return null;
//            }
//
//            //这个地方是加载h5的视频列表 默认图   点击前的视频图
//            return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        }

        @Override
        public void onShowCustomView(View view,
                                     WebChromeClient.CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
//            if (mCustomView != null) {
//                onHideCustomView();
//                return;
//            }
            LaLog.d(TAG, "MyWebChromeClient" + "#onShowCustomView#");
        }

        @Override
        public void onHideCustomView() {
            // 1. Remove the custom view
            LaLog.d(TAG, "MyWebChromeClient" + "#onHideCustomView#");

            // 2. Restore the state to it's original form

            // 3. Call the custom view callback
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            LaLog.d(TAG, "MyWebChromeClient" +
                    "&onProgressChanged$newProgress=" + newProgress);
            if (newProgress == 100) {
//                progressBar.setProgress(100);
                handler.postDelayed(runnable, 200);//0.2秒后隐藏进度条
            } else if (progressBar.getVisibility() == GONE) {
                progressBar.setVisibility(VISIBLE);
            }
            //设置初始进度10，这样会显得效果真一点，总不能从1开始吧
            if (newProgress < 10) {
                newProgress = 10;
            }
            //不断更新进度
            progressBar.setProgress(newProgress);
            if (WebTakeCallBack != null)
                WebTakeCallBack.onProgressChanged(view, newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    private class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LaLog.d(TAG, "#MyWebClient#shouldOverrideUrlLoading#url=" + url);
            mWebView.loadUrl(url);
            if (WebTakeCallBack != null)
                WebTakeCallBack.shouldOverrideUrlLoading(view, url);
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            if (WebTakeCallBack != null)
                WebTakeCallBack.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 关闭图片加载阻塞
            LaLog.d(TAG, "#MyWebClient#onPageFinished#url=" + url);
            WebTake.setLoadsImagesAutomatically();

            long endTime = System.currentTimeMillis(); //获取结束时间
            LaLog.d(TAG, "#MyWebClient" +
                    "#onPageFinished#程序运行时间=" + (endTime - startTime) + "ms");
            if (WebTakeCallBack != null)
                WebTakeCallBack.onPageFinished(view, url);

            LaBluetoothJs.initLaJsBlluetooth(getContext(), mWebView);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            startTime = System.currentTimeMillis();//获取开始时间
            if (WebTakeCallBack != null)
                WebTakeCallBack.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (WebTakeCallBack != null)
                WebTakeCallBack.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
            NestedPbWebView.this.requestFocus();
            NestedPbWebView.this.requestFocusFromTouch();
        }
    }

    /*----------分割线*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;

        MotionEvent trackedEvent = MotionEvent.obtain(event);

        final int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }

        int y = (int) event.getY();

        event.offsetLocation(0, mNestedYOffset);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                result = super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastMotionY - y;

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    trackedEvent.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }

                int oldY = getScrollY();
                mLastMotionY = y - mScrollOffset[1];
                if (deltaY < 0) {
                    int newScrollY = Math.max(0, oldY + deltaY);
                    deltaY -= newScrollY - oldY;
                    if (dispatchNestedScroll(0, newScrollY - deltaY, 0, deltaY, mScrollOffset)) {
                        mLastMotionY -= mScrollOffset[1];
                        trackedEvent.offsetLocation(0, mScrollOffset[1]);
                        mNestedYOffset += mScrollOffset[1];
                    }
                }

                trackedEvent.recycle();
                result = super.onTouchEvent(trackedEvent);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                result = super.onTouchEvent(event);
                break;
        }
        return result;
    }

    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
