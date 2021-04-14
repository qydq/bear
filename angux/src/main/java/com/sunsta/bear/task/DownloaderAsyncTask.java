package com.sunsta.bear.task;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.engine.IliveryDb;
import com.sunsta.bear.engine.LiveryDbImpl;
import com.sunsta.bear.faster.DataService;
import com.sunsta.bear.faster.EasyPermission;
import com.sunsta.bear.faster.FileUtils;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.SPUtils;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.listener.DownloaderListener;
import com.sunsta.bear.model.entity.ResponseDownloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.sunsta.bear.AnConstants.VALUE.LOG_LIVERY_EXCEPTION;

public class DownloaderAsyncTask extends AsyncTask<String, ResponseDownloader, Integer> {
    private DownloaderListener listener;
    private Context mContext;
    private int lastProgress;
    private RandomAccessFile randomAccessFile;
    private IliveryDb mDao;
    private ResponseDownloader mod;

    public DownloaderAsyncTask(Context mContext, ResponseDownloader mod, DownloaderListener listener) {
        this.listener = listener;
        this.mContext = mContext;
        this.mDao = new LiveryDbImpl(mContext);
        this.mod = mod;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        InputStream inputStream = null;
        File downloadResultFile;
        long finishedLength = 0;//记录下载文件的长度
        long contentLength = 0;
        String url = strings[0];

        /*
         * (1)：首先判断是否指定了存放下载文件的路径 (这里创建文件的方式，也需要统一为一个方法，最好放到FileUtils中)
         * 在这里更新数据库【保存的名字】，【下载的最终路径】，【下载文件的大小】
         * */
        ResponseDownloader smartMod = FileUtils.INSTANCE.smartDownloaderFile(mContext, mod);
        mod = smartMod;
        downloadResultFile = smartMod.getDownloadResultFile();
        if (downloadResultFile != null) {
            contentLength = mod.getLength();
            /*
             * （2）：更新数据库
             * */
            if (mod.getId() >= 0) {
                mDao.updateNpl(url, mod.getId(), mod.getFileName(), mod.getDownloadPath(), contentLength);
            }
            /*
             * (3)：计算当前断点续传的长度，（这里有一个坑，原本我们记录数据库下载的长度可能和当前文件的长度不一致，导致下载的内容会缺失，所有这里直接按实际文件的长度下载）
             * 假设用户手动删除了文件，那么长度为0，则重新开始下载，则在这里需要更新数据库下载成功的标志为0，
             * */
            finishedLength = FileUtils.INSTANCE.getFileLength(downloadResultFile);
//            (废弃) 这里第2中方法需要从数据库里面拿到Length
//            finishedLength = queryDbFinishedLength(fileName);



            /*
             * (4)：这里处理一些下载逻辑，869表示下载失败，1表示下载成功
             * */
            if (finishedLength == -1) {
                if (mod.getId() >= 0) {
                    mDao.updateFaiure(url, mod.getId());
                }
                finishedLength = 0;
            }
            if (contentLength == 0) {
                mod.setFinished(0);
                if (mod.getId() >= 0) {
                    mDao.updateFaiure(url, mod.getId());
                }
                mod.setProgress(0);
                mod.setFinished(869);
                return 869;
            } else if (contentLength == finishedLength) {
                mod.setFinished(1);
                if (mod.getId() >= 0) {
                    mDao.updateSuccess(url, mod.getId(), contentLength);
                }
                mod.setProgress(100);
                return 1;
            }

            /*
             * （5）：从这里开始去真正的下载文件，Livery框架提供OkhhttpClient下载方式，区别于LodingDialog
             * */
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    //断点下载，指定从那个字节开始下载
                    .addHeader("RANGE", "bytes=" + finishedLength + "-")
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                ResponseBody body = response.body();
                if (body != null) {
                    inputStream = body.byteStream();
//                    randomAccessFile = new RandomAccessFile(downloaddownloadResultFile, "rw");
                    randomAccessFile = new RandomAccessFile(downloadResultFile, "rwd");
                    randomAccessFile.seek(finishedLength);//跳过已下载的字节
                    byte[] bytes = new byte[1024];
                    int total = 0;
                    int len;
                    mod.setFinished(2);
                    while ((len = inputStream.read(bytes)) != -1) {
                        if (mod.getFinished() == 3) {
                            mod.setDownloadMessage("onCanceled");
                            return 3;
                        } else if (mod.getFinished() == 4) {
                            mod.setDownloadMessage("onPaused");
                            return 4;
                        } else {
                            total += len;
                            randomAccessFile.write(bytes, 0, len);
                            long currentFinishedLenght = total + finishedLength;
                            //计算已经下载的百分比
                            int progress = (int) ((currentFinishedLenght) * 100 / contentLength);
                            mod.setFinishedLength(currentFinishedLenght);
                            mod.setFinishedSize(DataService.getInstance().getDataSize(currentFinishedLenght));
                            mod.setProgress(progress);
                            if (mod.getId() >= 0) {
                                mDao.updateLoding(url, mod.getId(), mod.getFinishedLength(), mod.getProgress(), 2);
                            }
                            if (progress == 100) {
                                mod.setDownloadMessage("onSuccess");
                                if (mod.getId() >= 0) {
                                    mDao.updateSuccess(url, mod.getId(), mod.getLength());
                                }
                            }
                            publishProgress(mod);
                        }
                    }
                    body.close();
                    mod.setFinished(1);
                    return 1;
                }
            } catch (IOException e) {
                LaLog.e(ValueOf.logLivery(LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
                mod.setDownloadMessage(e.getMessage());
                mod.setFinished(869);
                return 869;
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                    if (mod.getFinished() == 3) {
//                        mod.setFinished(0);
                        FileUtils.INSTANCE.delete(downloadResultFile.getAbsolutePath());
                    }
                } catch (IOException e) {
                    mod.setDownloadMessage(e.getMessage());
                    mod.setFinished(869);
                    LaLog.e(ValueOf.logLivery(LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
                }
            }
            return 869;
        }
        return 869;
    }

    @Override
    protected void onProgressUpdate(ResponseDownloader... values) {
        ResponseDownloader mod = values[0];
        if (mod != null) {
            if (mod.getProgress() > lastProgress) {
                if (listener != null) {
                    listener.onProgress(mod);
                }
                lastProgress = mod.getProgress();
            }
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            case 869:
                if (listener != null) {
                    listener.onFailure(mod.getDownloadMessage());
                }
                break;
            case 1:
                if (listener != null) {
                    listener.onSuccess(mod);
                }
                if (mod.isAutoOpen()) {
                    if (FileUtils.INSTANCE.isApkSuffix(mod.getFileName())) {
//                        下载完成通知安装
                        if (EasyPermission.hasPermissions(mContext, Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
                            if (SPUtils.getInstance().putString(AnConstants.EXTRA.APK_INTALLPATH, mod.getDownloadPath())) {
                                LaLog.d(ValueOf.logLivery("Apk_installpath path save success"));
                            }
                            installApk(mContext);
                        } else {
                            LaLog.e(ValueOf.logLivery(LOG_LIVERY_EXCEPTION, "Java.lang.SecurityException ：", "Need to declare android.permission.REQUEST_INSTALL_PACKAGES to call this api in your AndroidManifest.xml"));
                        }
                    }
                }
                break;
            case 3:
                if (listener != null) {
                    listener.onCanceled();
                }
                break;
            case 4:
                if (listener != null) {
                    listener.onPaused();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 广播通知安装apk ,todo 需要统一起来
     */
    private void installApk(Context mContext) {
        Intent intent = new Intent();
        intent.setAction("installapk");
        intent.putExtra(AnConstants.EXTRA.APK_INTALLPATH, mod.getDownloadPath());
        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        mContext.sendBroadcast(intent);
    }

    public void pauseDownload() {
        if (mod != null) {
            mod.setFinished(4);
        }
    }

    public void cancelDownload() {
        if (mod != null) {
            mod.setFinished(3);
        }
    }
}