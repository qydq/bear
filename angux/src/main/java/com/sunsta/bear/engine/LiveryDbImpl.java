package com.sunsta.bear.engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.model.entity.ResponseDownloader;

import java.util.ArrayList;
import java.util.List;

import static com.sunsta.bear.engine.LiveryDbEngine.TAB_THREADINFO;


public class LiveryDbImpl implements IliveryDb {
    private LiveryDbEngine liveryDbEngine;

    public LiveryDbImpl(Context context) {
        this.liveryDbEngine = LiveryDbEngine.getInstance(context);
    }

    @Override
    public synchronized void insertDownloader(ResponseDownloader dao) {
        SQLiteDatabase db = liveryDbEngine.getReadableDatabase();
        ResponseDownloader responseDownloader = queryDownloadWithId(dao.getId());
        ContentValues values = new ContentValues();
        values.put("_id", dao.getId());
        values.put("url", dao.getUrl());
        values.put("fileName", dao.getFileName());
        values.put("length", dao.getLength());
        values.put("finishedLength", dao.getFinishedLength());
        values.put("progress", dao.getProgress());
        values.put("finished", dao.getFinished());
        values.put("downloadPath", dao.getDownloadPath());
        if (responseDownloader == null) {
            db.insert(TAB_THREADINFO, null, values);
        } else {
            db.replace(TAB_THREADINFO, null, values);
        }
//        db.close();
    }

    @Override
    public synchronized void deleteDownloader(String url) {
        SQLiteDatabase db = liveryDbEngine.getReadableDatabase();
        db.delete(TAB_THREADINFO, "url = ?", new String[]{url});
//        db.close();
    }

    /*
     * 更新下载：下载成功
     * */
    @Override
    public synchronized void updateSuccess(String url, int _id, long finishedLength) {
        updateLoding(url, _id, finishedLength, 100, 1);
    }

    /*
     * 更新下载：下载失败
     * */
    @Override
    public synchronized void updateFaiure(String url, int _id) {
        updateLoding(url, _id, 0, 0, 0);
    }

    /**
     * 更新下载，【当前断点已下载的长度】，【当前下载的进度】，【下载文件是否完成】
     */
    @Override
    public synchronized void updateLoding(String url, int _id, long finishedLength, int progress, int finished) {
        SQLiteDatabase db = liveryDbEngine.getReadableDatabase();
        try {
            db.execSQL("update " + TAB_THREADINFO + " set finishedLength = ? , progress = ? , finished = ? where url = ? and _id = ? ", new Object[]{finishedLength, progress, finished, url, _id});
//            db.close();
        } catch (SQLiteException sqliteException) {
            LaLog.e(ValueOf.logLivery(sqliteException.getMessage()));
        }
    }

    /**
     * 更新下载，【保存的名字N】，【下载的最终路径P】，【下载文件的大小L】
     */
    @Override
    public synchronized void updateNpl(String url, int _id, String fileName, String downloadPath, long length) {
        SQLiteDatabase db = liveryDbEngine.getReadableDatabase();
        try {
            db.execSQL("update " + TAB_THREADINFO + " set fileName = ? , downloadPath = ? , length = ? where url = ? and _id = ? ", new Object[]{fileName, downloadPath, length, url, _id});
//            db.close();
        } catch (SQLiteException sqliteException) {
            LaLog.e(ValueOf.logLivery(sqliteException.getMessage()));
        }
    }

    @Override
    public synchronized List<ResponseDownloader> queryDownloadWithUrl(String url) {
        SQLiteDatabase db = liveryDbEngine.getReadableDatabase();
        List<ResponseDownloader> list = new ArrayList<>();
        try {
            Cursor cursor = db.query(TAB_THREADINFO, null, "url = ?", new String[]{url}, null, null, null);
            while (cursor.moveToNext()) {
                list.add(queryDataWithCursor(cursor));
            }
            cursor.close();
//            db.close();
        } catch (SQLiteException sqliteException) {
            LaLog.e(ValueOf.logLivery(sqliteException.getMessage()));
            return null;
        }

        return list;
    }

    @Override
    public synchronized ResponseDownloader queryDownloadWithId(int _id) {
        SQLiteDatabase db = liveryDbEngine.getReadableDatabase();
        ResponseDownloader mod = null;
        String sql = "select * from " + TAB_THREADINFO + " where _id = ? ";
        try {
            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(_id)});
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    mod = queryDataWithCursor(cursor);
                }
                cursor.close();
            }
        } catch (SQLiteException sqliteException) {
            LaLog.e(ValueOf.logLivery(sqliteException.getMessage()));
            return mod;
        }
//        db.close();
        return mod;
    }

    private ResponseDownloader queryDataWithCursor(Cursor cursor) {
        ResponseDownloader mod = new ResponseDownloader();
        mod.setId(cursor.getInt(cursor.getColumnIndex("_id")));
        mod.setUrl(cursor.getString(cursor.getColumnIndex("url")));
        mod.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
        mod.setLength(cursor.getLong(cursor.getColumnIndex("length")));
        mod.setFinishedLength(cursor.getLong(cursor.getColumnIndex("finishedLength")));
        mod.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
        mod.setProgress(cursor.getInt(cursor.getColumnIndex("progress")));
        mod.setDownloadPath(cursor.getString(cursor.getColumnIndex("downloadPath")));
        return mod;
    }

    @Override
    public List<ResponseDownloader> queryAllDownloader() {
        List<ResponseDownloader> list = new ArrayList<>();
        try {
            SQLiteDatabase db = liveryDbEngine.getReadableDatabase();
            Cursor cursor = db.query(TAB_THREADINFO, null, null, null, null, null, null);
            if (cursor == null || cursor.isAfterLast()) {
                return null;
            }
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                //TODO: change cursor to domain object.
                list.add(queryDataWithCursor(cursor));
            }
            cursor.close();
            db.close();
        } catch (SQLiteException sqliteException) {
            LaLog.e(ValueOf.logLivery(sqliteException.getMessage()));
            return null;
        }
        return list;
    }

    @Override
    public boolean isExists(String url, int _id) {
        SQLiteDatabase db = liveryDbEngine.getReadableDatabase();
        Cursor cursor = db.query(TAB_THREADINFO, null, "url = ? and _id = ?", new String[]{url, _id + ""}, null, null, null);
        boolean exists = cursor.moveToNext();
        db.close();
        cursor.close();
        return exists;
    }
}