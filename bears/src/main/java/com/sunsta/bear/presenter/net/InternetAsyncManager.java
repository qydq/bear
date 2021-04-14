package com.sunsta.bear.presenter.net;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：BiniRxManger是基于rxjava 和rxandroid的使用优化封装，便于快捷开发
 * <a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以通过关注我的知乎获取更详细的信息</a>
 * <h3>版权声明：(C) 2016 The Android Developer Sunst</h3>
 * <br>创建日期：2019/12/04
 * <br>邮件email：qyddai@gmail.com
 * <br>个人Github：https://qydq.github.io
 * <p>--#---- Revision History:  --- >  : |version|date|updateinfo|----#--
 * @author sunst
 * @version 1.0 |   2019/12/04           |   BiniRxManger是基于rxjava 和rxandroid的使用优化封装，便于快捷开发
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class InternetAsyncManager {
    public AsyncMangerEvent nativeRxEvent = AsyncMangerEvent.getInstance();
    /**
     * 管理观察源
     */
    private Map<String, Observable<?>> observalbeMap = new HashMap<>();
    /**
     * 管理订阅者
     */
    private CompositeDisposable mCompositeSubscription = new CompositeDisposable();

    // 注册
    public void on(String eventName, Consumer<Object> consumer) {
        Observable<?> mObservable = nativeRxEvent.register(eventName);
        observalbeMap.put(eventName, mObservable);
        mCompositeSubscription
                .add(mObservable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(consumer, Throwable::printStackTrace));
    }

    /**
     * 添加订阅者到mCompositeSubscription
     *
     * @param m 要添加的订阅者
     */
    public void add(Disposable m) {
        mCompositeSubscription.add(m);
    }

    /**
     * 取消所有注册
     */
    public void clear() {
        // 取消订阅
        mCompositeSubscription.dispose();
        for (Map.Entry<String, Observable<?>> entry : observalbeMap.entrySet()) {
            // 取消注册
            nativeRxEvent.unregister(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 触发事件
     */
    public void post(Object tag, Object content) {
        nativeRxEvent.post(tag, content);
    }
}