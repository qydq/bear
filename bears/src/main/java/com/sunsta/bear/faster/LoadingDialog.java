package com.sunsta.bear.faster;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.config.LoadingConfig;
import com.sunsta.bear.engine.GlideEngine;
import com.sunsta.bear.immersion.ColorDrawer;
import com.sunsta.bear.immersion.RichTextView;
import com.sunsta.bear.listener.DownloaderListener;
import com.sunsta.bear.listener.OnSmartClickListener;
import com.sunsta.bear.model.entity.ResponseDownloader;
import com.sunsta.bear.presenter.net.InternetClient;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class LoadingDialog {
    private static Activity mActivity;
    private static Dialog dialog;
    private static AnimationDrawable animationDrawable;
    private static TextView tvProgress, tvSpeed;
    private static ImageView ivCancle;
    private static ProgressBar mProgress;
    private static LinearLayout llBottom;

    private static boolean interceptFlag = false;
    private static final int DOWN_UPDATE = 1;//正在下载更新进入
    private static final int DOWN_OVER = 2;//下载完成，即下载成功，安装apk
    private static final int INSTALL_APP = 3;//下载完成，如果是apk则安装apk (是否是apk文件，然后去安装，不用再放到这里处理，统一到下载文件的核心地方处理)

    private static Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == NetBroadcastReceiverUtils.NETWORK_SPEED) {
                if (tvSpeed != null) {
                    tvSpeed.setText("当前网速： " + msg.obj.toString());
                }
            } else {
                LoadingConfig config = (LoadingConfig) msg.obj;
                switch (msg.what) {
                    case DOWN_UPDATE:
                        downloadUpdateStatus(msg.arg1);
                        break;
                    case DOWN_OVER:
                        finishDownload(config);
                        break;
                }
            }
            return false;
        }
    });

    private static void downloadUpdateStatus(int percent) {
        if (mProgress != null) {
            mProgress.setProgress(percent);
        }
        if (tvProgress != null) {
            tvProgress.setText(percent + "%");
        }
    }

    private static void finishDownload(LoadingConfig config) {
        if (llBottom != null) {
            llBottom.setVisibility(View.GONE);
        }
        if (mProgress != null) {
            mProgress.setProgress(100);
        }
        interceptFlag = false;
        if (tvProgress != null) {
            tvProgress.setText("下载成功：\n" + config.getDownloadPath());
        }
        if (ivCancle != null) {
            ivCancle.setVisibility(View.VISIBLE);
        }
        if (config.isAutoCloseDialog()) {
            ToastUtils.s(mActivity, "已保存：" + config.getDownloadPath());
            dismiss();
        }
    }

    public static void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (null != animationDrawable && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        if (tvProgress != null) {
            tvProgress = null;
        }
        if (mProgress != null) {
            mProgress = null;
        }
        if (llBottom != null) {
            ivCancle = null;
        }
        if (llBottom != null) {
            llBottom = null;
        }
        if (mActivity != null) {
            mActivity = null;
        }
        interceptFlag = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 规定：0，默认的加载框
     * 1：表示：可以配置资源的加载框，（含很多类型）
     */
    public static void showLoading(Activity activity, LoadingConfig config) {
        AlertDialog.Builder builder;
        View view;
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (config.isBackgroundDimEnabled()) {
            if (config.isFullWidthScreen()) {
                builder = new AlertDialog.Builder(activity, R.style.an_dialog_loading_dim);//有阴影，全屏
            } else {
                builder = new AlertDialog.Builder(activity, R.style.an_dialog);//有阴影，跟随内容
            }
        } else {
            if (config.isFullWidthScreen()) {
                builder = new AlertDialog.Builder(activity, R.style.an_dialog_loading_nodim);//无阴影，全屏
            } else {
                builder = new AlertDialog.Builder(activity, R.style.an_dialog);//无阴影，跟随内容
            }
        }
        if (config.getDialogClassify() == 1) {
            view = ViewUtils.getInstance().getInflaterView(activity, R.layout.base_dialog_loading_standard);
        } else if (config.getDialogClassify() == 2) {
            view = ViewUtils.getInstance().getInflaterView(activity, R.layout.base_dialog_loading_animation);
        } else if (config.getDialogClassify() == 3) {
            view = ViewUtils.getInstance().getInflaterView(activity, R.layout.base_dialog_loading_progressbar);
        } else {
            view = ViewUtils.getInstance().getInflaterView(activity, R.layout.item_dialog_loading);
        }
        mActivity = activity;
        builder.setView(view);
        dialog = builder.create();
//        dialog.setTitle("我是测试");
        /*
         * (1).findViewById
         * */
        RelativeLayout frameLayout = view.findViewById(R.id.frameLayout);
        RelativeLayout action0 = view.findViewById(R.id.action0);
        TableLayout actionTabeLayout = view.findViewById(R.id.actionTabeLayout);
        RelativeLayout action1 = view.findViewById(R.id.action1);
        tvProgress = view.findViewById(R.id.tvText);
        TextView tvPoint = view.findViewById(R.id.tvPoint);
        ProgressBar progressBar = view.findViewById(R.id.anProgressBar);
        ImageView ivPrimary = view.findViewById(R.id.ivPrimary);//need check
        tvProgress.setText(DataService.getInstance().defaultEmpty(config.getContent(), AnConstants.EMPTY));
        /*
         * (2).window对话框判断
         * */
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            if (config.isBackgroundAlpha()) {
                layoutParams.alpha = 0.96f;
            }
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
            if (config.getDialogClassify() == 1) {
                window.setGravity(Gravity.TOP | Gravity.START);
                if (config.getGravity() == Gravity.CENTER) {
                    window.setGravity(Gravity.CENTER);
                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) action1.getLayoutParams();
                    params2.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                    params2.addRule(RelativeLayout.BELOW, R.id.action0); //or params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    action1.setLayoutParams(params2);
                }
            } else {
                window.setGravity(config.getGravity());
            }
        }

        /*
         * (3).不同的加载框
         * */
        if (config.isBackgroundDimEnabled()) {
            if (config.getDialogClassify() == 1 || config.getDialogClassify() == 2 || config.getDialogClassify() == 3) {
                if (action1 != null) {
                    if (config.getBackgroundFrame() != 0) {
                        action1.setBackgroundResource(config.getBackgroundFrame());
                    } else {
                        action1.setBackgroundResource(R.drawable.in_shape_dialog);
                    }
                }
            } else {
                if (config.getBackgroundFrame() != 0) {
                    frameLayout.setBackgroundResource(config.getBackgroundFrame());
                } else {
                    frameLayout.setBackgroundResource(R.drawable.in_shape_dialog);
                }
            }
            if (!config.isRandomColor()) {
                if (tvPoint != null) {
                    RichTextView.setTextColor(tvPoint, R.color.anProgressCenterColor2);
                }
                RichTextView.setTextColor(tvProgress, R.color.anProgressCenterColor2);
                if (progressBar != null) {
                    progressBar.setIndeterminateTintList(RichTextView.getColorStateList(R.color.anProgressCenterColor2));
                }
            }
        } else {
            if (config.getDialogClassify() == 1 || config.getDialogClassify() == 2 || config.getDialogClassify() == 3) {
                if (action1 != null) {
                    if (config.getBackgroundFrame() != 0) {
                        action1.setBackgroundResource(config.getBackgroundFrame());
                    } else {
                        action1.setBackgroundResource(R.drawable.base_bg_loading);
                    }
                }
            } else {
                if (config.getBackgroundFrame() != 0) {
                    frameLayout.setBackgroundResource(config.getBackgroundFrame());
                } else {
                    frameLayout.setBackgroundResource(R.drawable.base_bg_loading);
                }
            }
            if (!config.isRandomColor()) {
                RichTextView.setTextColor(tvProgress, R.color.ColorWhite);
                if (tvPoint != null) {
                    RichTextView.setTextColor(tvPoint, R.color.ColorWhite);
                }
                RichTextView.setTextColor(tvProgress, R.color.ColorWhite);
                if (progressBar != null) {
                    progressBar.setIndeterminateTintList(RichTextView.getColorStateList(R.color.ColorWhite));
                }
            }
        }

        if (config.isRandomColor()) {
            if (tvPoint != null) {
                tvPoint.setTextColor(ColorDrawer.getRandomColor());
//                RichTextView.setTextColor(tvPoint, ColorDrawer.getRandomColor());
            }
            tvProgress.setTextColor(ColorDrawer.getRandomColor());
//            RichTextView.setTextColor(textView, ColorDrawer.getRandomColor());
//            if (progressBar != null) {
//                progressBar.setIndeterminateTintList(RichTextView.getColorStateList(ColorDrawer.getRandomColor()));
//            }
        }
        if (config.getDialogClassify() == 1) {
            if (ivPrimary != null) {
                if (config.getAnimationIvId() != 0) {
                    GlideEngine.getInstance().loadImage(config.getAnimationIvId(), ivPrimary);
//                    ivPrimary.setImageResource(config.getAnimationIvId());
                }
                Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(activity, R.anim.base_anim_dialog_loading);
                ivPrimary.startAnimation(hyperspaceJumpAnimation);
            }
            /*patch，classify==1不可用的全屏设置*/
            if (config.isFullWidthScreen() && action1 != null) {
                ViewGroup.LayoutParams params3 = action1.getLayoutParams();
                params3.width = ScreenUtils.getDeviceWidth(activity); //这里需要加上一个offset
                params3.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                action1.setLayoutParams(params3);
            }
        } else if (config.getDialogClassify() == 2) {
            animationDrawable = ViewUtils.getInstance().rotateIvFrame(ivPrimary);
        }
        String content = tvProgress.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            tvProgress.setVisibility(View.GONE);
        }
        ViewGroup.LayoutParams distanceParams = tvProgress.getLayoutParams();
        if (config.isFixedDistance() && config.getDialogClassify() != 1) {
            float resultWidth = Layout.getDesiredWidth(content, 0, content.length(), tvProgress.getPaint());//原始内容宽度
            distanceParams.width = (int) resultWidth;
            distanceParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            tvProgress.setLayoutParams(distanceParams);
        }
        if (config.getDialogClassify() == 1 && config.isFixedDistance()) {
            if (!TextUtils.isEmpty(content)) {
                float resultWidth = Layout.getDesiredWidth(content, 0, content.length(), tvProgress.getPaint()) + ScreenUtils.dp2px(30);
                distanceParams.width = (int) resultWidth;
                distanceParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                tvProgress.setLayoutParams(distanceParams);
            }
        }

        if (config.getDialogClassify() == 0) {
            if (config.isLastPoint()) {
                sequencePoint(tvPoint);
            } else {
                if (tvPoint != null)
                    tvPoint.setVisibility(View.GONE);
                RichTextView.repatSequenceText(tvProgress, content);
            }
        } else {
            RichTextView.repatSequenceText(tvProgress, content);
        }

        if (frameLayout != null) {
            frameLayout.setOnClickListener(view1 -> {
                if (config.isCancelable()) {
                    dismiss();
                }
            });
        }

        if (action1 != null) {
            action1.setOnClickListener(view12 -> {
            });
        }
        if (actionTabeLayout != null) {
            actionTabeLayout.setOnClickListener(view12 -> {
            });
        }
        dialog.setCancelable(config.isCancelable());
        dialog.show();
    }

    private static void sequencePoint(TextView tvPoint) {
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (aLong % 3 == 0) {
                            tvPoint.setText("•");
                        } else if (aLong % 3 == 1) {
                            tvPoint.setText("••");
                        } else {
                            tvPoint.setText("•••");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * （1）默认下载对话框，不含有取消,暂停按钮，默认下载的时候不能取消对话框
     */
    public static void download(@NonNull Activity activity, @NonNull String url) {
        download(activity, url, false);
    }

    public static void download(@NonNull Activity activity, @NonNull String url, boolean autoCancle) {
        download(activity, url, AnConstants.EMPTY, AnConstants.EMPTY, autoCancle, false, 1);
    }

    public static void downloadOfConfig(@NonNull Activity activity, LoadingConfig dialogDownloader) {
        preperDownload(activity, dialogDownloader);
    }

    /**
     * （2）默认带FileName下载对话框，不含有取消,暂停按钮，默认下载的时候不能取消对话框
     */
    public static void downloadByName(@NonNull Activity activity, @NonNull String url, @NonNull String fileName) {
        downloadByName(activity, url, fileName, false);
    }

    public static void downloadByName(@NonNull Activity activity, @NonNull String url, @NonNull String fileName, boolean autoCancle) {
        download(activity, url, fileName, AnConstants.EMPTY, autoCancle, false, 1);
    }

    public static void downloadByNameOfSystem(@NonNull Activity activity, @NonNull String url, @NonNull String fileName) {
        downloadByNameOfSystem(activity, url, fileName, false);
    }

    public static void downloadByNameOfSystem(@NonNull Activity activity, @NonNull String url, @NonNull String fileName, boolean autoCancle) {
        download(activity, url, fileName, AnConstants.EMPTY, autoCancle, true, 1);
    }

    /**
     * （3）默认带downloadPath下载对话框，不含有取消,暂停按钮，默认下载的时候不能取消对话框
     */
    public static void downloadByPath(@NonNull Activity activity, @NonNull String url, @NonNull String absPath) {
        downloadByPath(activity, url, absPath, false);
    }

    public static void downloadByPath(@NonNull Activity activity, @NonNull String url, @NonNull String absPath, boolean autoCancle) {
        download(activity, url, AnConstants.EMPTY, absPath, autoCancle, false, 1);
    }

    public static void downloadByPathOfSystem(@NonNull Activity activity, @NonNull String url, @NonNull String absPath) {
        downloadByPathOfSystem(activity, url, absPath, false);
    }

    public static void downloadByPathOfSystem(@NonNull Activity activity, @NonNull String url, @NonNull String absPath, boolean autoCancle) {
        download(activity, url, AnConstants.EMPTY, absPath, autoCancle, true, 1);
    }

    /**
     * （4）默认带downloadPath暂停下载对话框，含有取消,暂停按钮，默认下载的时候不能取消对话框
     */
    public static void pauseDownloadByName(@NonNull Activity activity, @NonNull String url, @NonNull String fileName) {
        pauseDownloadByName(activity, url, fileName, false);
    }

    public static void pauseDownloadByName(@NonNull Activity activity, @NonNull String url, @NonNull String fileName, boolean autoCancle) {
        download(activity, url, fileName, AnConstants.EMPTY, autoCancle, false, 2);
    }

    public static void pauseDownloadByNameOfSystem(@NonNull Activity activity, @NonNull String url, @NonNull String fileName) {
        pauseDownloadByNameOfSystem(activity, url, fileName, false);
    }

    public static void pauseDownloadByNameOfSystem(@NonNull Activity activity, @NonNull String url, @NonNull String fileName, boolean autoCancle) {
        download(activity, url, fileName, AnConstants.EMPTY, autoCancle, true, 2);
    }

    /**
     * （5）默认带downloadPath暂停下载对话框，含有取消,暂停按钮，默认下载的时候不能取消对话框
     */
    public static void pauseDownloadByPath(@NonNull Activity activity, @NonNull String url, @NonNull String absPath) {
        pauseDownloadByPath(activity, url, absPath, false);
    }

    public static void pauseDownloadByPath(@NonNull Activity activity, @NonNull String url, @NonNull String absPath, boolean autoCancle) {
        download(activity, url, AnConstants.EMPTY, absPath, autoCancle, false, 2);
    }

    public static void pauseDownloadByPathOfSystem(@NonNull Activity activity, @NonNull String url, @NonNull String absPath) {
        pauseDownloadByPathOfSystem(activity, url, absPath, false);
    }

    public static void pauseDownloadByPathOfSystem(@NonNull Activity activity, @NonNull String url, @NonNull String absPath, boolean autoCancle) {
        download(activity, url, AnConstants.EMPTY, absPath, autoCancle, true, 2);
    }

    /**
     * （6）默认带loading对话框，通过名字
     */
    public static void LoadingConfigByName(@NonNull Activity activity, @NonNull String url, @NonNull String fileName) {
        LoadingConfigByName(activity, url, fileName, false);
    }

    public static void LoadingConfigByName(@NonNull Activity activity, @NonNull String url, @NonNull String fileName, boolean autoCancle) {
        download(activity, url, fileName, AnConstants.EMPTY, autoCancle, false, -1);
    }

    public static void LoadingConfigByNameOfSystem(@NonNull Activity activity, @NonNull String url, @NonNull String fileName) {
        LoadingConfigByNameOfSystem(activity, url, fileName, false);
    }

    public static void LoadingConfigByNameOfSystem(@NonNull Activity activity, @NonNull String url, @NonNull String fileName, boolean autoCancle) {
        download(activity, url, fileName, AnConstants.EMPTY, autoCancle, true, -1);
    }

    /**
     * （7）默认带loading对话框，通过absPath
     */
    public static void LoadingConfigByPath(@NonNull Activity activity, @NonNull String url, @NonNull String absPath) {
        LoadingConfigByPath(activity, url, absPath, false);
    }

    public static void LoadingConfigByPath(@NonNull Activity activity, @NonNull String url, @NonNull String absPath, boolean autoCancle) {
        download(activity, url, AnConstants.EMPTY, absPath, autoCancle, false, -1);
    }

    public static void LoadingConfigByPathOfSystem(@NonNull Activity activity, @NonNull String url, @NonNull String absPath) {
        LoadingConfigByPathOfSystem(activity, url, absPath, false);
    }

    public static void LoadingConfigByPathOfSystem(@NonNull Activity activity, @NonNull String url, @NonNull String absPath, boolean autoCancle) {
        download(activity, url, AnConstants.EMPTY, absPath, autoCancle, true, -1);
    }

    /**
     * 封装下载文件数据的参数
     */
    private static void download(@NonNull Activity activity, @NonNull String url, String fileName, String absPath, boolean autoCancle, boolean appSystem, int type) {
        LoadingConfig dialogDownloader = new LoadingConfig();
        dialogDownloader.setUrl(url);
        dialogDownloader.setType(type);
        dialogDownloader.setDownloadPath(absPath);
        dialogDownloader.setFileName(fileName);
        dialogDownloader.setAppSystem(appSystem);
        dialogDownloader.setCancelable(autoCancle);
        downloadOfConfig(activity, dialogDownloader);
    }

    private static void preperDownload(Activity activity, LoadingConfig dialogDownloader) {
        if (NetBroadcastReceiverUtils.isConnectedToInternet(activity)) {
            mActivity = activity;
            if (dialogDownloader.getType() != -1) {
                showConfimDialog(activity, dialogDownloader);
            } else {
                /*
                 * 显示LoadingDialog
                 * */
                LoadingConfig config = new LoadingConfig();
                config.setFixedDistance(false);
                config.setGravity(Gravity.CENTER);
                config.setLastPoint(true);
                config.setFullWidthScreen(false);
                config.setCancelable(false);
                config.setBackgroundDimEnabled(true);
                showLoading(activity, config);
                if (tvProgress != null) {
                    tvProgress.setVisibility(View.VISIBLE);
                }
            }

            ResponseDownloader mod = new ResponseDownloader();
            mod.setDownloadPath(dialogDownloader.getDownloadPath());
            mod.setFileName(dialogDownloader.getFileName());
            mod.setId(-1);
            mod.setName(dialogDownloader.getName());
            mod.setUrl(dialogDownloader.getUrl());
            InternetClient.getInstance().startDownload(mod, new DownloaderListener() {
                @Override
                public void onProgress(ResponseDownloader downloader) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();//更新进度
                        message.what = DOWN_UPDATE;
                        message.arg1 = downloader.getProgress();
                        message.obj = dialogDownloader;
                        mHandler.sendMessage(message);
                    }
                }

                @Override
                public void onPaused() {

                }

                @Override
                public void onCanceled() {

                }

                @Override
                public void onSuccess(ResponseDownloader downloader) {
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();//更新进度
                        message.what = DOWN_OVER;
                        message.arg1 = downloader.getProgress();
                        message.obj = dialogDownloader;
                        mHandler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(String message) {
                    LaLog.e(message);
                }
            });
            new NetBroadcastReceiverUtils().startShowNetSpeed(mActivity, mHandler);//网速监听
        } else {
            LaLog.e("Network Error ：\nThe network is not connected. Please check your network settings");
        }
    }

    /**
     * 对话框样式(1)，默认【取消，确定】,默认可以取消
     */
    public static void showConfimDialog(Activity activity, String content, OnSmartClickListener<String> onSmartClickRightListener) {
        showConfimDialog(activity, content, true, onSmartClickRightListener);
    }

    public static void showConfimDialog(Activity activity, String content, boolean cancelble, OnSmartClickListener<String> onSmartClickRightListener) {
        systemConfimDialog(activity, content, activity.getString(R.string.an_cancel), activity.getString(R.string.an_confirm), cancelble, onSmartClickRightListener);
    }

    /**
     * 对话框样式(2)，配置一个按钮的情景，默认可以取消
     */
    public static void showConfimDialog(Activity activity, String content, String btnText, OnSmartClickListener<String> onSmartClickRightListener) {
        showConfimDialog(activity, content, btnText, true, onSmartClickRightListener);
    }

    public static void showConfimDialog(Activity activity, String content, String btnText, boolean cancelble, OnSmartClickListener<String> onSmartClickRightListener) {
        systemConfimDialog(activity, content, AnConstants.EMPTY, btnText, cancelble, onSmartClickRightListener);
    }

    private static void systemConfimDialog(Activity activity, String content, String leftBtnContext, String rightBtnText, boolean cancelable, OnSmartClickListener<String> onSmartClickRightListener) {
        showConfimDialog(activity, content, leftBtnContext, rightBtnText, cancelable, null, onSmartClickRightListener);
    }

    /**
     * 对话框样式(3)，配置两个按钮的场景，默认可以取消
     */
    public static void showConfimDialog(Activity activity, String content, String leftBtnContext, String rightBtnText, OnSmartClickListener<String> onSmartClickLeftListener, OnSmartClickListener<String> onSmartClickRightListener) {
        showConfimDialog(activity, content, leftBtnContext, rightBtnText, true, onSmartClickLeftListener, onSmartClickRightListener);
    }

    public static void showConfimDialog(Activity activity, String content, String leftBtnContext, String rightBtnText, boolean cancelable, OnSmartClickListener<String> onSmartClickLeftListener, OnSmartClickListener<String> onSmartClickRightListener) {
        showConfimDialog(activity, AnConstants.EMPTY, content, leftBtnContext, rightBtnText, cancelable, onSmartClickLeftListener, onSmartClickRightListener);
    }

    /**
     * 有标题的对话框(4)，默认可以取消
     */
    public static void showConfimDialog(Activity activity, String title, String content, String leftBtnContext, String rightBtnText, OnSmartClickListener<String> onSmartClickLeftListener, OnSmartClickListener<String> onSmartClickRightListener) {
        showConfimDialog(activity, title, content, leftBtnContext, rightBtnText, true, onSmartClickLeftListener, onSmartClickRightListener);
    }

    public static void showConfimDialog(Activity activity, String title, String content, String leftBtnContext, String rightBtnText, boolean cancelable, OnSmartClickListener<String> onSmartClickLeftListener, OnSmartClickListener<String> onSmartClickRightListener) {
        LoadingConfig config = new LoadingConfig();
        config.setLeftBtnContext(leftBtnContext);
        config.setRightBtnText(rightBtnText);
        if (!TextUtils.isEmpty(title)) {
            config.setTitle(title);
        }
        config.setContent(content);
        config.setCancelable(cancelable);
        showConfimDialog(activity, config, onSmartClickLeftListener, onSmartClickRightListener);
    }

    //提供给preparDownloader使用
    public static void showConfimDialog(Activity activity, LoadingConfig config) {
        showConfimDialog(activity, config, null, null);
    }

    public static void showConfimDialog(Activity activity, LoadingConfig config, OnSmartClickListener<String> onSmartClickLeftListener, OnSmartClickListener<String> onSmartClickRightListener) {
        dialog = new Dialog(activity, R.style.an_dialog);
        dialog.setCancelable(config.isCancelable());
        dialog.show();
        View view = ViewUtils.getInstance().getInflaterView(activity, R.layout.base_dialog_confirm);
        dialog.setContentView(view);
        /*
         * 检查content是否有内容，如果有内容显示出来，否则限制progressbar
         * */
        TextView tvText = view.findViewById(R.id.tvText);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        View patchView1 = view.findViewById(R.id.patchView1);
        View patchView2 = view.findViewById(R.id.patchView2);
        LinearLayout llProgress = view.findViewById(R.id.llProgress);
        LinearLayout actionL0 = view.findViewById(R.id.actionL0);
        llBottom = view.findViewById(R.id.llBottom);
        ivCancle = view.findViewById(R.id.ivCancle);
        Button btnRight = view.findViewById(R.id.btnRight);
        Button btnLeft = view.findViewById(R.id.btnLeft);

        if (!TextUtils.isEmpty(config.getRightBtnText())) {
            RichTextView.setRichText(btnRight, config.getRightBtnText());
        }
        RelativeLayout frameLayout = view.findViewById(R.id.frameLayout);
        if (config.getBackgroundFrame() != 0) {
            frameLayout.setBackgroundResource(config.getBackgroundFrame());
        }
        if (!TextUtils.isEmpty(config.getTitle())) {
            tvTitle.setVisibility(View.VISIBLE);
            patchView1.setVisibility(View.VISIBLE);
            patchView2.setVisibility(View.VISIBLE);
            RichTextView.setRichText(tvTitle, config.getTitle());
            RichTextView.setTextSize(tvTitle, R.dimen.an_font_dialog_title);
        } else {
            tvTitle.setVisibility(View.INVISIBLE);
            patchView1.setVisibility(View.GONE);
            patchView2.setVisibility(View.VISIBLE);
        }
        if (config.getGravity() == Gravity.BOTTOM) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) actionL0.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, R.id.actionL0);
            actionL0.setLayoutParams(params);
        }
        ivCancle.setOnClickListener(v -> dismiss());
        frameLayout.setOnClickListener(v -> {
            if (config.isCancelable()) {
                dismiss();
            }
        });
        if (config.getRightIvResouce() != 0) {
            ivCancle.setVisibility(View.VISIBLE);
            ivCancle.setImageResource(config.getRightIvResouce());
        } else {
            ivCancle.setVisibility(View.GONE);
        }
        if (config.getLeftBackground() != 0) {
            btnLeft.setBackgroundResource(config.getLeftBackground());
        } else {
            btnLeft.setBackgroundResource(R.drawable.in_selector_dialog_left);
        }
        if (config.getRightBackground() != 0) {
            btnRight.setBackgroundResource(config.getRightBackground());
        } else {
            btnRight.setBackgroundResource(R.drawable.in_selector_dialog_right);
        }

        if (config.getMaxLine() != 0) {
            tvText.setMaxLines(config.getMaxLine());
        }
        if (config.getContentGravity() != 0) {
            tvText.setGravity(config.getContentGravity());
        } else {
            tvText.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        if (config.getType() == 0) {
            llProgress.setVisibility(View.GONE);
            tvText.setVisibility(View.VISIBLE);
            RichTextView.setRichText(tvText, config.getContent());
            if (TextUtils.isEmpty(config.getLeftBtnContext())) {
                btnLeft.setVisibility(View.GONE);
                if (config.getRightBackground() != 0) {
                    btnRight.setBackgroundResource(config.getRightBackground());
                } else {
                    btnRight.setBackgroundResource(R.drawable.in_selector_dialog_center);
                }
            } else {
                btnLeft.setVisibility(View.VISIBLE);
                RichTextView.setRichText(btnLeft, config.getLeftBtnContext());
                btnLeft.setOnClickListener(v -> {
                    dismiss();
                    if (onSmartClickLeftListener != null) {
                        onSmartClickLeftListener.onSmartClick(btnLeft.getText().toString().trim() + "#" + config.getContent());
                    }
                });
                //如果有取消了，再设置取消没有意义
                String leftBtnText = config.getLeftBtnContext();
                if (leftBtnText.equals("取消") || leftBtnText.equals("cancel") || leftBtnText.equals("Cancel")) {
                    ivCancle.setVisibility(View.GONE);
                } else {
                    ivCancle.setVisibility(View.VISIBLE);
                }
            }
            btnRight.setOnClickListener(v -> {
                dismiss();
                if (onSmartClickRightListener != null) {
                    onSmartClickRightListener.onSmartClick(btnRight.getText().toString().trim() + "#" + config.getContent());
                }
            });
        } else {
            //对于下载文件的dialog只有下载暂停，或者取消这么一个按钮【即要么是暂停，要么是取消】，没有两个按钮同时出现的情况
            tvText.setVisibility(View.GONE);
            llProgress.setVisibility(View.VISIBLE);
            tvProgress = view.findViewById(R.id.tvProgress);
            tvSpeed = view.findViewById(R.id.tvSpeed);
            mProgress = view.findViewById(R.id.innerProgressBar);
            ivCancle.setOnClickListener(v -> dismiss());
            btnLeft.setVisibility(View.GONE);
            btnRight.setBackgroundResource(R.drawable.in_selector_dialog_center);
            if (config.getType() == 1) {
                if (TextUtils.isEmpty(config.getRightBtnText()) && TextUtils.isEmpty(config.getLeftBtnContext())) {
                }
                llBottom.setVisibility(View.GONE);
            } else {
                btnRight.setBackgroundResource(R.drawable.in_selector_dialog_center);
                btnRight.setText("暂停");
                btnRight.setOnClickListener(v -> {
                    if (onSmartClickRightListener != null) {
                        onSmartClickRightListener.onSmartClick(btnRight.getText().toString().trim() + "#" + config.getContent());
                    }
                    if (interceptFlag) {
                        interceptFlag = false;
                        btnRight.setText("暂停");
                    } else {
                        interceptFlag = true;
                        btnRight.setText("继续");
                    }
                });
            }
        }
    }
}