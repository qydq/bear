package com.sunsta.bear.engine;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sunsta.bear.AnConstants;

public class LiveryDbEngine extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    public static final String TAB_THREADINFO = "liveryDownloadInfo";
    private static final String SQL_CREATE = "create table " + TAB_THREADINFO + "(_id integer primary key autoincrement, "
            + "url text, fileName text,length integer, finishedLength integer, progress integer, finished integer, downloadPath text)";
    private static final String SQL_DROP = "drop table if exists " + TAB_THREADINFO;
    private static LiveryDbEngine instance = null;

    private LiveryDbEngine(Context context) {
        super(context, AnConstants.FILE_DB, null, VERSION);
    }

    public static LiveryDbEngine getInstance(Context context) {
        if (instance == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (instance == null) {
                    // be sure to call getApplicationContext() to avoid memory leak
                    instance = new LiveryDbEngine(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }
}