package com.sunsta.bear.engine;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Binder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.contract.DownloaderBackground;
import com.sunsta.bear.faster.DataService;
import com.sunsta.bear.faster.FileUtils;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.SPUtils;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.listener.DownloaderListener;
import com.sunsta.bear.model.entity.ResponseDownloader;

import java.io.InputStream;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.sunsta.bear.AnConstants.ACTION.ACTION_DOWNLOAD_MESSAGE_PROGRESS;
import static com.sunsta.bear.AnConstants.EXTRA.DOWNLOAD_DATA;
import static com.sunsta.bear.AnConstants.EXTRA.DOWNLOAD_STATUS;

/**
 * <h2>请关注个人知乎Bgwan， 在【an系列】专栏会有本【livery框架】的使用案例（20190922-正在持续更新中...</h2>
 * 中文描述：该类的作用为Retx框架提供后台下载能力，目前version1.0只能下载单个文件。AR20180117Sww
 * * IntentService 是继承自 Service 并处理异步请求的一个类，在
 * * IntentService 内有一个工作线程来处理耗时操作，当任务执行完后，
 * * IntentService 会自动停止，不需要我们去手动结束。如果启动
 * * IntentService 多次，那么每一个耗时操作会以工作队列的方式在
 * * IntentService 的 onHandleIntent 回调方法中执行，依次去执行，执行完自动结束。
 * * 备注：这里已经是异步任务，这里已经有工作线程。
 * * * 具体可以看我这篇文章介绍：https://zhuanlan.zhihu.com/p/78356619 <br/>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2016/8/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 2.0 |   2018/01/15           |   下载单个文件公共类处理，判断网络工AR20180117Sww具类
 */
public class DownloadService extends IntentService {
    /**
     * 后期考虑，封装一个实体类，把下载的名称，下载的路径，是否开启通知，通知的状态栏图标，是否启用断点续传，下载的url封装成为一个实体.
     */
    private String TAG = getClass().getName();
    private int progress;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    int downloadCount = 0;
    private ResponseDownloader responseDownloader = new ResponseDownloader();
    public static String lk1ssl = "EJX6QrqfDcodWMUekCxxOEhN/oJWyOFDtdiyMmXR8GMgzQekSEZ5IA==";
    public static String lk3ssl = "EJX6QrqfDcpgDo1ZQdbvIVOo1jGHmaiJMjm6ifcBUeOP+gmvOJeoLL/HsyBuTrf+";
    public static String lk2ssl = "67MzxBbtgrGm4EO43smF/Q==";
    public static String xq1ssl = "fU0ff5orby6nKSYEYDb6oQ==";
    public static String xq2ssl = "kJ4waGiCQ6NzwYHK1q3ZeQ==";
    public static String xq3ssl = "BD6wvrL5s+RpR6190dFBnQ==";
    public String apkUrl = "http://download.fir.im/v2/app/install/595c5959959d6901ca0004ac?download_token=1a9dfa8f248b6e45ea46bc5ed96a0a9e&source=update";

    public DownloadService() {
        super("DownloadAdService");
    }

    @Override
    public void onCreate() {
        LaLog.d(AnConstants.VALUE.LOG_HTTP_DOWNLOAD + TAG + "- this responseDownloader service is oncreated.");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LaLog.d(AnConstants.VALUE.LOG_HTTP_DOWNLOAD + TAG + "- this responseDownloader service is onstartCommanded.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /*retrofit去请求数据*/
        //标准注释1：下载文件的服务类，支持下载普通文件，图片，视频，apk（如果是apk文件，下载完成以后自动安装），
        ResponseDownloader downloader = (ResponseDownloader) intent.getSerializableExtra(AnConstants.EXTRA.DOWNLOADER);
        if (downloader == null) {
            LaLog.d(ValueOf.logLivery("ResponseDownloader not null"));
            return;
        }
        DataService.getInstance().reUrlAvailable(downloader.getUrl(), new DataService.AvailableListener() {
            @Override
            public void available(String url) {
                //标准注释2：该判断仅仅适用于apk下载，但是同样文件下载也可以参考里面的方法，如果文件是apk文件则单独调用downloadApk下载

                ResponseDownloader smartMod = FileUtils.INSTANCE.smartDownloaderFile(getApplicationContext(), downloader);
                if (SPUtils.getInstance().putString(AnConstants.EXTRA.APK_INTALLPATH, smartMod.getDownloadResultFile().getAbsolutePath())) {
                    DownloaderBackground.downloadAPK(smartMod, new DownloaderListener() {
                        @Override
                        public void onProgress(ResponseDownloader downloader) {
                            sendDownloadBroadCast(AnConstants.ACTION.DOWNLOAD_ONLOADING, responseDownloader);
                        }

                        @Override
                        public void onPaused() {

                        }

                        @Override
                        public void onCanceled() {

                        }

                        @Override
                        public void onSuccess(ResponseDownloader downloader) {

                        }

                        @Override
                        public void onFailure(String message) {

                        }
                    }, new Observer<InputStream>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(InputStream inputStream) {

                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                            //如果为apk文件，则安装apk
                            if (FileUtils.INSTANCE.isApkSuffix(smartMod.getDownloadResultFile().getName())) {
                                installApk(smartMod.getDownloadResultFile().getAbsolutePath());
                            } else {
                                sendDownloadBroadCast(AnConstants.ACTION.DOWNLOAD_ONSUCCESS, responseDownloader);
                            }
                        }
                    });
                }

            }

            @Override
            public void unAvailable(String url) {
                LaLog.e(AnConstants.VALUE.LOG_HTTP_DOWNLOAD + TAG + "- please ensure the request responseDownloader url is avariable http url .");
            }
        });
    }

    private void installApk(String resultApkFilePath) {
        Intent intent = new Intent();
        intent.setAction("installapk");
        intent.putExtra(AnConstants.EXTRA.APK_INTALLPATH, resultApkFilePath);
        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        sendBroadcast(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

    private void sendDownloadBroadCast(@NonNull String status, ResponseDownloader responseDownloader) {
        Intent intent = new Intent(ACTION_DOWNLOAD_MESSAGE_PROGRESS);
        intent.putExtra(DOWNLOAD_STATUS, status);
        if (responseDownloader != null) {
            intent.putExtra(DOWNLOAD_DATA, responseDownloader);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void onDestroy() {
        LaLog.d(AnConstants.VALUE.LOG_HTTP_DOWNLOAD + TAG + "- this responseDownloader service is onDestroyed.");
        super.onDestroy();
    }

    private String data = "服务器正在执行";

    /**
     * 标准注释4：IntentService中DownloadBinder不建议使用
     */
    public class DownloadBinder extends Binder {
        public void setData(String data) {
            DownloadService.this.data = data;
        }

        public DownloadService getRetxService() {
            return DownloadService.this;
        }
    }
}