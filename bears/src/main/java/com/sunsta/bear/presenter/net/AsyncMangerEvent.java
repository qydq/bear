package com.sunsta.bear.presenter.net;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class AsyncMangerEvent {
    private static AsyncMangerEvent instance;
    /**
     * ConcurrentHashMap: 线程安全集合
     * Subject 同时充当了Observer和Observable的角色
     */
    private ConcurrentHashMap<Object, List<Subject>> subjectConCurrentHashMap = new ConcurrentHashMap<>();

    public static synchronized AsyncMangerEvent getInstance() {
        if (null == instance) {
            instance = new AsyncMangerEvent();
        }
        return instance;
    }

    private AsyncMangerEvent() {
    }

    /**
     * 订阅事件源
     */
    @SuppressLint("CheckResult")
    public AsyncMangerEvent onEvent(Observable<?> observable, Consumer<Object> consumer) {
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer, Throwable::printStackTrace);
        return getInstance();
    }

    /**
     * 注册事件源
     */
    public <T> Observable<T> register(@NonNull Object tag) {
        List<Subject> subjectList = subjectConCurrentHashMap.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            subjectConCurrentHashMap.put(tag, subjectList);
        }

        Subject<T> subject = PublishSubject.create();
        subjectList.add(subject);
        // LaLog.d("register" + tag + " size:" + subjectList.size());
        return subject;
    }

    /**
     * 取消整个tag的监听
     */
    public void unregister(@NonNull Object tag) {
        List<Subject> subjectList = subjectConCurrentHashMap.get(tag);
        if (null != subjectList) {
            subjectConCurrentHashMap.remove(tag);
        }
    }

    /**
     * 取消tag里某个observable的监听
     *
     * @param tag        key
     * @param observable 要删除的observable
     */
    public AsyncMangerEvent unregister(@NonNull Object tag, @NonNull Observable<?> observable) {
        if (null == observable) {
            return getInstance();
        }
        List<Subject> subjectList = subjectConCurrentHashMap.get(tag);
        if (null != subjectList) {
            // 从subjectList中删去observable
            subjectList.remove((Subject<?>) observable);
            // 若此时subjectList为空则从subjectConCurrentHashMap中删去
            if (isEmpty(subjectList)) {
                subjectConCurrentHashMap.remove(tag);
            }
        }
        return getInstance();
    }

    /**
     * 触发事件
     */
    public void post(@NonNull Object content) {
        post(content.getClass().getName(), content);
    }

    /**
     * 触发事件
     */
    public void post(@NonNull Object tag, @NonNull Object content) {
        List<Subject> subjectList = subjectConCurrentHashMap.get(tag);
        if (!isEmpty(subjectList)) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
            }
        }
    }

    /**
     * 判断集合是否为空
     */
    private boolean isEmpty(Collection<Subject> collection) {
        return null == collection || collection.isEmpty();
    }
}