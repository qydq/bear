package com.sunsta.bear.engine;

import com.sunsta.bear.model.entity.ResponseDownloader;

import java.util.List;


public interface IliveryDb {
    void insertDownloader(ResponseDownloader dao);

    void deleteDownloader(String url);

    void updateNpl(String url, int _id, String fileName, String downloadPath, long length);

    void updateLoding(String url, int _id, long finishedLength, int progress, int finished);

    void updateFaiure(String url, int _id);

    void updateSuccess(String url, int _id, long finishedLength);

    List<ResponseDownloader> queryDownloadWithUrl(String url);

    ResponseDownloader queryDownloadWithId(int url);

    List<ResponseDownloader> queryAllDownloader();

    boolean isExists(String url, int _id);
}