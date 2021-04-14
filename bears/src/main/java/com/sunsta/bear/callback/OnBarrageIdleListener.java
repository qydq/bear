package com.sunsta.bear.callback;

import com.sunsta.bear.layout.INABarrageView;

/**
 * 弹幕空闲Nms后回调，在此函数设置循环数组
 * should set loop queue here!!!
 */
public interface OnBarrageIdleListener {
    void onIdle(long idleTimeMs, INABarrageView view);
}