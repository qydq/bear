package com.sunsta.bear.view.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.faster.DataService;
import com.sunsta.bear.faster.webview.NestedJsWebView;
import com.sunsta.bear.layout.INAStatusLayout;
import com.sunsta.bear.view.ParallaxActivity;

import static com.sunsta.bear.AnConstants.EXTRA.APP_TITLE;
import static com.sunsta.bear.AnConstants.EXTRA.APP_URL_MORE;
import static com.sunsta.bear.AnConstants.EXTRA.APP_WEB_SHOW_MORE;
import static com.sunsta.bear.AnConstants.EXTRA.APP_WEB_SHOW_RIGHTBAR;

public class AliWebActivity extends ParallaxActivity {
    private ProgressBar pg1;
    private NestedJsWebView webView;
    private ProgressBar anPb;
    private TextView tvTip;
    private RelativeLayout mParentLayout;
    private PopupWindow popupWindow;
    private int mParentWidth;
    private int mParentHeight;
    private String url;
    private boolean isErrorOccur = false;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.an_activity_web);
        if (getIntent() == null) {
            return;
        }
        url = getIntent().getStringExtra(APP_URL_MORE);
        String title = getIntent().getStringExtra(APP_TITLE);
        boolean showMore = getIntent().getBooleanExtra(APP_WEB_SHOW_MORE, true);
        boolean showRightBar = getIntent().getBooleanExtra(APP_WEB_SHOW_RIGHTBAR, true);
        setAppBarTitle(title);

        webView = findViewById(R.id.webView);
        webView.loadUrl(url);

        pg1 = inaBarlayout.getTopPb();
        anPb = inaBarlayout.getCenterPb();
        tvTip = findViewById(R.id.tvTip);
        if (showMore) {
            tvTip.setText("???????????????????????????\n\n" + url);
        } else {
            tvTip.setText(AnConstants.EMPTY);
        }
        webView.setWebViewClient(new WebViewClient() {
            //??????shouldOverrideUrlLoading????????????????????????
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO ???????????????????????????
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                //???javascript?????????????????????404????????????;;;??????????????????
                if (request.isForMainFrame()) {// ?????????6.0????????????????????????????????????????????????main frame?????????.
                    isErrorOccur = true;
                    getInaStatusLayout().setLightContent("?????????????????????????????????????????????\n???????????????????????????????????????????????????.")
                            .setOnIvClickListener(new INAStatusLayout.OnIvClickListener() {
                                @Override
                                public void onClick() {
                                    showToast("????????????");
                                }
                            }).trigger(INAStatusLayout.ErrorState.ERROR);
                    webView.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }
//                String data = "Page NO FOUND???";
//                view.loadUrl("javascript:document.body.innerHTML=\"" + data + "\"");
            }
        });
        WebSettings seting = webView.getSettings();
        seting.setJavaScriptEnabled(true);//??????webview??????javascript??????
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    pg1.setVisibility(View.GONE);//??????????????????????????????
                    anPb.setVisibility(View.GONE);
                    if (!isErrorOccur) {
                        getInaStatusLayout().trigger();
                        webView.setVisibility(View.VISIBLE);
                    }
                    tvTip.setVisibility(View.INVISIBLE);
                } else {
                    pg1.setVisibility(View.VISIBLE);//????????????????????????????????????
                    anPb.setVisibility(View.VISIBLE);
                    pg1.setProgress(newProgress);//???????????????
                }
            }
        });

        if (showRightBar) {
            inaBarlayout.setOnRRightLlClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopWinds();
                }
            });
        } else {
            getInaBarlayout().setRRightLlVisibility(View.GONE);
        }
    }

    private void showPopWinds() {
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popView = mLayoutInflater.inflate(R.layout.an_item_popwinds_webview, null);
        mParentLayout = findViewById(R.id.tab_title);
        mParentWidth = mParentLayout.getWidth();
        mParentHeight = mParentLayout.getHeight();
        popupWindow = new PopupWindow(popView, mParentWidth / 2, mParentWidth, false);
//                final??PopupWindow??popupWindow??=??new??PopupWindow(contentview,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        realPopWindow.setAnimationStyle(R.style.myanimation);
        popupWindow.showAsDropDown(inaBarlayout.getRightLayout(), 0, 5);
//        realPopWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                DataService.getInstance().setBackgroundAlpha(1.0f, AliWebActivity.this);
            }
        });
        DataService.getInstance().setBackgroundAlpha(0.5f, AliWebActivity.this);
        popView.findViewById(R.id.tvYtips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(url);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }
        });
    }
}