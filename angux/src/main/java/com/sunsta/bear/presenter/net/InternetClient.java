package com.sunsta.bear.presenter.net;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sunsta.bear.AnApplication;
import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.contract.DownloaderForeground;
import com.sunsta.bear.engine.DownloadService;
import com.sunsta.bear.engine.IliveryDb;
import com.sunsta.bear.engine.LiveryDbImpl;
import com.sunsta.bear.faster.Convert;
import com.sunsta.bear.faster.DataService;
import com.sunsta.bear.faster.LALocale;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.NetBroadcastReceiverUtils;
import com.sunsta.bear.faster.SPUtils;
import com.sunsta.bear.faster.StringUtils;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.listener.DownloaderListener;
import com.sunsta.bear.model.entity.ResponseDownloader;
import com.sunsta.bear.presenter.BaseInternetApi;
import com.sunsta.bear.task.DownloaderAsyncTask;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sunsta.bear.AnConstants.ACTION.ACTION_DOWNLOAD_MESSAGE_PROGRESS;
import static com.sunsta.bear.AnConstants.ACTION.DOWNLOAD_ONCANCELED;
import static com.sunsta.bear.AnConstants.ACTION.DOWNLOAD_ONFAILURE;
import static com.sunsta.bear.AnConstants.ACTION.DOWNLOAD_ONLOADING;
import static com.sunsta.bear.AnConstants.ACTION.DOWNLOAD_ONPAUSED;
import static com.sunsta.bear.AnConstants.ACTION.DOWNLOAD_ONSUCCESS;
import static com.sunsta.bear.AnConstants.EXTRA.DOWNLOAD_DATA;
import static com.sunsta.bear.AnConstants.EXTRA.DOWNLOAD_STATUS;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：bininetclient是基于Retroft网络请求框架的二次封装，方便大家使用请求网络
 * <a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以通过关注我的知乎获取更详细的信息</a>
 * <h3>版权声明：(C) 2016 The Android Developer Sunst</h3>
 * <br>创建日期：2019/12/04
 * <br>邮件email：qyddai@gmail.com
 * <br>个人Github：https://qydq.github.io
 * <p>--#---- Revision History:  --- >  : |version|date|updateinfo|----#--
 * @author sunst
 * @version 3.0 |   2019/12/28           |   变换能力添加，统一标准请求，对外放置到base
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class InternetClient<E> extends Convert<E> {
    private String TAG = "LiveryException：com.sunsta.livery.Livery";
    //    private String TAG = getClass().getName();
    private static Retrofit actualRetrofit;
    private static Retrofit tempRetrofit;
    private static Context actualContext;
    private static IliveryDb mDao = null;
    private volatile static InternetClient internetClient = null;
    private static OkHttpClient okHttpClient;
    private static BaseInternetApi internetApi;
    private static boolean actualAutoGson = false;
    private DownloaderAsyncTask downloaderAsyncTask;

    private E createApi;

    private static final int DEFAULT_CONNECT_TIMEOUT = 30;
    private static final int DEFAULT_READ_TIMEOUT = 40;
    private static final int DEFAULT_CACHE_SIZE = 1024 * 1024 * 960;//官方参考

    /**
     * (1)：初始化客户端相关操作
     */

    public static InternetClient getInstance() {
        if (internetClient == null) {
            synchronized (InternetClient.class) {
                if (internetClient == null) {
                    internetClient = new InternetClient();
                }
            }
        }
        return internetClient;
    }

    private InternetClient() {
        if (null == actualContext) {
            actualContext = AnApplication.getApplication();
        }
        mDao = new LiveryDbImpl(actualContext.getApplicationContext());
    }

    /**
     * 默认不开启自定义自动Json转换功能
     * 设置请求的Base地址以及初始化okhttp
     */
    public void initialze(@Nullable String baseUrl) {
        doInitialze(baseUrl, actualAutoGson);
    }

    public void initialze(@Nullable String baseUrl, boolean autoConvertGson) {
        actualAutoGson = autoConvertGson;
        doInitialze(baseUrl, autoConvertGson);
    }

    private void doInitialze(@Nullable String baseUrl, boolean autoConvertGson) {
        if (null == actualContext) {
            actualContext = AnApplication.getApplication();
        }
        if (internetClient != null) {
            synchronized (InternetClient.class) {
                if (internetClient != null) {
                    SPUtils.getInstance().putString(AnConstants.KEY.HTTP_REQUEST_URL, baseUrl);//保存地址
                    File cacheFile = new File(actualContext.getCacheDir(), AnConstants.FOLDER_CACHE);
                    Cache cache = new Cache(cacheFile, DEFAULT_CACHE_SIZE);
                    okHttpClient = setOkHttpClient(mNativeInterceptor, new BaseUrlInterceptor(), cache);//本地okhttps
                }
            }
        }
        if (!SPUtils.getInstance().getBoolean(AnConstants.KEY.APP_AN, false)) {
            rxJavaPluginsErrorHandler();
        }
        DataService.getInstance().rxJavaPluginsPatch();
    }

    private void rxJavaPluginsErrorHandler() {
        RxJavaPlugins.setErrorHandler(throwable -> {
            throwable.printStackTrace();
            LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, throwable.getClass().toString(), throwable.getMessage()));
        });
    }

    public void setActualAutoGson(boolean actualAutoGson) {
        InternetClient.actualAutoGson = actualAutoGson;
    }

    public void enableLog() {
        enableLog(true);
    }

    public void enableLog(boolean enable) {
        enableLog(enable, AnConstants.EMPTY);
    }

    public void enableLog(boolean enable, String logFilter) {
        SPUtils.getInstance().putBoolean(AnConstants.KEY.LOG_ENABLE, enable);
        SPUtils.getInstance().putString(AnConstants.KEY.LOG_FILTER, logFilter);
    }

    public void setInterceptor(Interceptor urInterceptor) throws Exception {
        if (internetClient != null) {
            synchronized (InternetClient.class) {
                if (internetClient != null) {
                    okHttpClient = setOkHttpClient(urInterceptor, null, null);
                } else {
                    try {
                        throw new IllegalStateException(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
                    }
                }
            }
        } else {
            try {
                throw new IllegalStateException(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
            } catch (IllegalStateException e) {
                e.printStackTrace();
                LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
            }
        }
    }

    public void setOkhttpClient(OkHttpClient urOkHttpClient) throws Exception {
        if (internetClient != null) {
            synchronized (InternetClient.class) {
                if (internetClient != null) {
                    okHttpClient = urOkHttpClient;
                } else {
                    try {
                        throw new IllegalStateException(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
                    }
                }
            }
        } else {
            try {
                throw new IllegalStateException(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
            } catch (IllegalStateException e) {
                e.printStackTrace();
                LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
            }
        }
    }

    public void enableBaseInterceptor() throws Exception {
        if (internetClient != null) {
            synchronized (InternetClient.class) {
                if (internetClient != null) {
                    okHttpClient = setOkHttpClient(new BaseInterceptor(), null, null);
                } else {
                    try {
                        throw new IllegalStateException(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
                    }
                }
            }
        } else {
            try {
                throw new IllegalStateException(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
            } catch (IllegalStateException e) {
                e.printStackTrace();
                LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
            }
        }
    }


    /**
     * retrofit本质时okhttp，设置okhttp
     * @param nativeInterceptor 过滤器，不能为空
     * @param urlInterceptor    过滤器，可以为空
     * @param cache             缓存设置，可以为空，默认五缓存大小限制
     */
    private OkHttpClient setOkHttpClient(@NonNull Interceptor nativeInterceptor, @Nullable Interceptor urlInterceptor, @Nullable Cache cache) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(nativeInterceptor);
        if (null != urlInterceptor) {
            builder.addInterceptor(urlInterceptor);
        }
        builder.addInterceptor(new LoggingInterceptor());
        if (null != cache) {
            builder.cache(cache);
        }
//        builder.retryOnConnectionFailure(true);// used in download
        builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS);
        return builder.build();
    }


    /**
     * (3).得到Retrofit相关操作
     */
    public Retrofit getRetrofit() {
        actualAutoGson = false;
        String BASE_URL = SPUtils.getInstance().getString(AnConstants.KEY.HTTP_REQUEST_URL);
        return doGetRetrofit(BASE_URL);
    }

    public Retrofit getRetrofit(@NonNull String baseUrl) {
        actualAutoGson = false;
        return doGetRetrofit(baseUrl);
    }

    /*得到转换成工厂类的retrofit*/
    public Retrofit getConverterRetrofit(@NonNull String baseUrl) {
        actualAutoGson = true;
        return doGetRetrofit(baseUrl);
    }

    public Retrofit getConverterRetrofit() {
        actualAutoGson = true;
        return getRetrofit();
    }

    private Retrofit doGetRetrofit(@NonNull String baseUrl) {
        String BASE_URL = SPUtils.getInstance().getString(AnConstants.KEY.HTTP_REQUEST_URL);
        if (TextUtils.isEmpty(BASE_URL) || null == okHttpClient) {
            try {
                throw new IllegalStateException(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } else {
            if (!baseUrl.equals(BASE_URL)) {
                tempRetrofit = null;
                synchronized (InternetClient.class) {
                    if (tempRetrofit == null) {
                        tempRetrofit = new Retrofit.Builder()
                                .client(okHttpClient)
                                .baseUrl(baseUrl)
                                .addConverterFactory(actualAutoGson ? NetNetConverter.create() : GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .build();
                    }
                }
                return tempRetrofit;
            } else {
                if (actualRetrofit == null) {
                    synchronized (InternetClient.class) {
                        if (actualRetrofit == null) {
                            actualRetrofit = new Retrofit.Builder()
                                    .client(okHttpClient)
                                    .baseUrl(baseUrl)
                                    .addConverterFactory(actualAutoGson ? NetNetConverter.create() : GsonConverterFactory.create())
                                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                    .build();
                        }
                    }
                }
            }
        }
        return actualRetrofit;
    }

    //重置保存的Url地址
    public void resetBaseUrl(@NonNull String baseUrl) {
        SPUtils.getInstance().putString(AnConstants.KEY.HTTP_REQUEST_URL, baseUrl);
    }

    public void setToken(@NonNull String tokenName, @NonNull String tokenValue) {
        SPUtils.getInstance().putString(AnConstants.KEY.TOKEN_NAME, tokenName);
        SPUtils.getInstance().putString(AnConstants.KEY.TOKEN_VALUE, tokenValue);
    }

    public void setToken(@NonNull String tokenValue) {
        SPUtils.getInstance().putString(AnConstants.KEY.TOKEN_VALUE, tokenValue);
    }


    public void clearToken() {
        SPUtils.getInstance().putString(AnConstants.KEY.TOKEN_NAME, AnConstants.EMPTY);
        SPUtils.getInstance().putString(AnConstants.KEY.TOKEN_VALUE, AnConstants.EMPTY);
    }

    /**
     * (4).得到请求的Api相关操作
     */
    public <S> S getService(Class<S> service) {
        return getRetrofit().create(service);
    }

    public <S> S getService(String baseUrl, Class<S> service) {
        return getRetrofit(baseUrl).create(service);
    }

    public <T> E api(@NonNull String baseUrl, final @NonNull Class<E> creatApi) {
        if (createApi == null) {
            synchronized (InternetClient.class) {
                if (createApi == null) {
                    createApi = getRetrofit(baseUrl).create(creatApi);
                }
            }
        }
        return createApi;
    }

    public <T> E api(final Class<E> creatApi) {
        if (createApi == null) {
            synchronized (InternetClient.class) {
                if (createApi == null) {
                    createApi = getRetrofit().create(creatApi);
                }
            }
        }
        return createApi;
    }

    public BaseInternetApi obtainBaseApi() {
        String BASE_URL = SPUtils.getInstance().getString(AnConstants.KEY.HTTP_REQUEST_URL);
        if (TextUtils.isEmpty(BASE_URL) || null == okHttpClient) {
            try {
                throw new IllegalStateException(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
            } catch (IllegalStateException e) {
                e.printStackTrace();
                LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
            }
        } else {
            if (internetApi == null) {
                synchronized (InternetClient.class) {
                    if (internetApi == null) {
                        internetApi = getService(BaseInternetApi.class);
                    }
                }
            }
        }
        return internetApi;
    }

    public BaseInternetApi obtainBaseApi(@NonNull String baseUrl) {
        String BASE_URL = SPUtils.getInstance().getString(AnConstants.KEY.HTTP_REQUEST_URL);
        if (TextUtils.isEmpty(BASE_URL) || null == okHttpClient) {
            try {
                throw new IllegalStateException(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
            } catch (IllegalStateException e) {
                e.printStackTrace();
                LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, TAG, "Need to add Livery.instance().initialze(String) in your XXXApplication before call Livery api."));
            }
        } else {
            if (internetApi == null) {
                synchronized (InternetClient.class) {
                    if (internetApi == null) {
                        internetApi = getService(baseUrl, BaseInternetApi.class);
                    }
                }
            }
        }
        return internetApi;
    }

    public <T> E typeApi(@NonNull String baseUrl, @NonNull E apiType) {
        if (createApi == null) {
            synchronized (InternetClient.class) {
                if (createApi == null) {
                    createApi = getRetrofit(baseUrl).create(getTClass(apiType));
                }
            }
        }
        return createApi;
    }

    public <T> E typeApi(@NonNull E apiType) {
        if (createApi == null) {
            synchronized (InternetClient.class) {
                if (createApi == null) {
                    createApi = getRetrofit().create(getTClass(apiType));
                }
            }
        }
        return createApi;
    }

    private Interceptor mNativeInterceptor = chain -> {
        Request original = chain.request();
        StringBuilder language = new StringBuilder();
        language.append(LALocale.getDefault().getLanguage());
        if (!TextUtils.isEmpty(LALocale.getDefault().getCountry())) {
            language.append("-").append(LALocale.getDefault().getCountry());
        }
        language.append(",").append(LALocale.getDefault().getLanguage());
        language.append(";q=0.9");
        String nativeTokenKey = SPUtils.getInstance().getString(AnConstants.KEY.TOKEN_NAME, "token");
        String nativeTokenValue = SPUtils.getInstance().getString(AnConstants.KEY.TOKEN_VALUE);
        if (TextUtils.isEmpty(nativeTokenValue)) {
            LaLog.d(ValueOf.logLivery("用户未登陆/登陆状态已失效"));
        } else {
            LaLog.d(ValueOf.logLivery("tokenKey=" + nativeTokenKey + ";tokeyValue=" + nativeTokenValue));
        }
        Request.Builder requestBuilder = original.newBuilder().addHeader("Accept-Language", language.toString());
        if (TextUtils.isEmpty(nativeTokenKey)) {
            nativeTokenKey = "token";
        }
        if (!TextUtils.isEmpty(nativeTokenValue)) {
            requestBuilder.addHeader(nativeTokenKey, nativeTokenValue);
        }
        Request request = requestBuilder.build();
        return chain.proceed(request);
    };

    /**
     * (5).缓存设置
     */
    private Interceptor mCacheInterceptor = chain -> {
        Request request = chain.request();
        CacheControl cacheControl = request.cacheControl();
        int maxStaleSeconds = cacheControl.maxStaleSeconds();
        if (!NetBroadcastReceiverUtils.isConnectedToInternet(actualContext) && maxStaleSeconds > 0) {
            LaLog.i(request.url().toString() + ", " + cacheControl.toString());
            CacheControl nativeCache = new CacheControl.Builder()
                    .onlyIfCached().maxStale(maxStaleSeconds, TimeUnit.SECONDS).build();//有效时间内缓存大小的控制数据
            request = request.newBuilder().cacheControl(nativeCache).build();
        } else {
            request = request.newBuilder().removeHeader("Cache-Control")
                    .removeHeader("Pragma").cacheControl(CacheControl.FORCE_NETWORK).build();
        }
        Response response = chain.proceed(request);
        return response.newBuilder().build();
    };

    /**
     * (6)设置订阅线程的被观察者模式
     */
    public <T> void onSubscribe(Observable<T> o, DisposableObserver<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    /**
     * (7)将 {@link Disposable} 添加到 {@link InternetAsyncManager} 中统一管理
     * 依附Livery框架AliActivity#onDestroy() 中会自动使用 #unDispose() 停止正在执行的 RxJava 任务, 避免内存泄漏(框架已自行处理)
     * @param disposable 冷启动模式
     */
    public void addDispose(Disposable disposable) {
        InternetAsyncManager apiManager = new InternetAsyncManager();
        apiManager.add(disposable);
    }


    /**
     * (8)下载文件
     */
    public void startDownload(@NonNull String url, DownloaderListener downloaderListener) {
        startDownload(new ResponseDownloader(url), downloaderListener, false);
    }

    public void startDownload(@NonNull String url) {
        startDownload(new ResponseDownloader(url), null, false);
    }

    public void startDownload(@NonNull ResponseDownloader mod, DownloaderListener downloaderListener) {
        startDownload(mod, downloaderListener, false);
    }

    /**
     * @param openParallel true表示并行执行，默认串行false
     */
    public void startDownload(@NonNull ResponseDownloader mod, DownloaderListener downloaderListener, boolean openParallel) {
        if (mod.getId() >= 0) {
            mDao.insertDownloader(mod);
        }
        if (!NetBroadcastReceiverUtils.isConnectedToInternet(actualContext)) {
            if (downloaderListener != null) {
                downloaderListener.onFailure(StringUtils.getString(R.string.an_no_connect_network));
            }
            return;
        }
        if (mod.isDownloadService()) {
            //todo 启动一个Service去下载文件，下载的过程会通过广播数据回掉回来
            downloadFileInService(mod.getUrl());
        } else {
            if (openParallel) {
                mod.openForeground();
            }
            if (mod.foreground()) {
                DownloaderForeground foregroundTask = new DownloaderForeground(actualContext, mod, downloaderListener);
                foregroundTask.download();
            } else {
                //每启动一个下载则，独立一个任务
                downloaderAsyncTask = new DownloaderAsyncTask(actualContext, mod, downloaderListener);
                downloaderAsyncTask.execute(mod.getUrl());
            }
        }
    }

    public void pauseDownload() {
        if (downloaderAsyncTask != null) {
            downloaderAsyncTask.pauseDownload();
        }
    }

    public void cancelDownload() {
        if (downloaderAsyncTask != null) {
            downloaderAsyncTask.cancelDownload();
        }
    }

    public void pauseDownload(int id) {

    }

    public List<ResponseDownloader> queryDownloadWithUrl(String url) {
        return mDao.queryDownloadWithUrl(url);
    }

    public ResponseDownloader queryDownloadWithId(int id) {
        return mDao.queryDownloadWithId(id);
    }

    public List<ResponseDownloader> queryAllDownloader() {
        return mDao.queryAllDownloader();
    }

    /**
     * 如果是一些异常情况，如用户删除了本文件，则需要主动维护数据库的内容
     */
    public void updateFaiure(String url, int _id) {
        mDao.updateLoding(url, _id, 0, 0, 0);
    }

    /**
     * (9)下载apk文件，文件，如果是apk文件则自动安装
     * <p>
     * 下载apk文件必须传一个完整的Url路径，apkName一定要包含名字.apk后缀
     * 默认apk文件下载完成以后会自动安装,
     * pwdPath路径可以传，也可不传，如果默认不传则下载后的apk存在的路径为：
     * /storage/emulated/0/Android/data/com.xxx.xxx/files/download/apkname
     */
    public void downloadFileInService(@NonNull String apkFullUrl) {
        Intent intent = new Intent(actualContext, DownloadService.class);
        intent.putExtra(AnConstants.EXTRA.DOWNLOADER, apkFullUrl);
        actualContext.startService(intent);
    }

    public void downloadFileInService(@NonNull String apkFullUrl, String apkName) {
        downloadFileInService(apkFullUrl, apkName);
    }

    /**
     * todo : service==119
     * 启动一个IntentService去下载一个文件，不必注意activity的泄漏问题， service会自动处理声明周期函数
     */
    public void downloadFileInService(@NonNull Activity activity, ResponseDownloader downloader) {
        registerReceiver(activity);
        Intent intent = new Intent(activity, DownloadService.class);
        intent.putExtra(AnConstants.EXTRA.DOWNLOADER, downloader);
        activity.startService(intent);
    }

    private LocalBroadcastManager internetBroadcastManger;

    private void registerReceiver(@NonNull Activity activity) {
        internetBroadcastManger = LocalBroadcastManager.getInstance(activity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DOWNLOAD_MESSAGE_PROGRESS);
        internetBroadcastManger.registerReceiver(downloadReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        if (internetBroadcastManger != null) {
            internetBroadcastManger.unregisterReceiver(downloadReceiver);
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(ACTION_DOWNLOAD_MESSAGE_PROGRESS)) {
                    String status = intent.getStringExtra(DOWNLOAD_STATUS);
                    if (!TextUtils.isEmpty(status)) {
                        ResponseDownloader responseDownloader = intent.getParcelableExtra(DOWNLOAD_DATA);
                        if (status.equals(DOWNLOAD_ONLOADING)) {
                            if (responseDownloader != null) {
                                LaLog.d("Aliff--" + responseDownloader.toString());
                            }
                        } else if (status.equals(DOWNLOAD_ONPAUSED)) {
                            LaLog.d("Aliff--" + responseDownloader.toString());
                        } else if (status.equals(DOWNLOAD_ONCANCELED)) {
                            LaLog.d("Aliff--" + responseDownloader.toString());
                        } else if (status.equals(DOWNLOAD_ONSUCCESS)) {
                            LaLog.d("Aliff--" + responseDownloader.toString());
                        } else if (status.equals(DOWNLOAD_ONFAILURE)) {
                            if (responseDownloader != null) {
                                LaLog.d("Aliff--" + responseDownloader.toString());
                            }
                        }
                    }
                }
            }
        }
    };

    private void cleanTempRetrofit() {
        tempRetrofit = null;
//        actualContext =null;
    }
}