package com.sunsta.bear.listener;

import com.sunsta.bear.model.entity.ResponseDownloader;

public interface DownloaderListener {
    void onProgress(ResponseDownloader downloader);//正在下载，返回回来百分比，和下载实体对象

    void onPaused();//暂停下载

    void onCanceled();//取消下载

    void onSuccess(ResponseDownloader downloader);//下载成功

    void onFailure(String message);//下载失败
}