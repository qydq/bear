package com.sunsta.bear.contract;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.engine.IliveryDb;
import com.sunsta.bear.engine.LiveryDbImpl;
import com.sunsta.bear.faster.DataService;
import com.sunsta.bear.faster.FileUtils;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.ThreadPool;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.listener.DownloaderListener;
import com.sunsta.bear.model.entity.ResponseDownloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloaderForeground {
    private Context mContext;
    private ResponseDownloader mod;
    private IliveryDb mDao;
    public boolean mIsPause = false;
    private DownloaderListener listener;

    public DownloaderForeground(Context mContext, ResponseDownloader mod, DownloaderListener listener) {
        super();
        this.mContext = mContext;
        this.mod = mod;
        this.listener = listener;
        this.mDao = new LiveryDbImpl(mContext);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            ResponseDownloader downloader = (ResponseDownloader) message.obj;
            if (downloader != null) {
                if (downloader.getFinished() == 869) {
                    mod.setDownloadMessage(downloader.getDownloadMessage());
                    if (listener != null) {
                        listener.onFailure(downloader.getDownloadMessage());
                    }
                } else if (downloader.getFinished() == 1) {
                    if (listener != null) {
                        mod.setDownloadMessage("onSuccess");
                        listener.onSuccess(mod);
                    }
                } else if (downloader.getFinished() == 2) {
                    if (listener != null) {
                        listener.onProgress(mod);
                    }
                } else if (downloader.getFinished() == 3) {
                    mod.setDownloadMessage("onCanceled");
                    if (listener != null) {
                        listener.onCanceled();
                    }
                } else if (downloader.getFinished() == 4) {
                    mod.setDownloadMessage("onPaused");
                    if (listener != null) {
                        listener.onPaused();
                    }
                }
                sendBroadcastDownload(downloader);
            }
            return false;
        }
    });

    private synchronized void handlerDownloader(ResponseDownloader mod) {
        Message message = mHandler.obtainMessage();
        message.obj = mod;
        mHandler.sendMessage(message);
    }

    private synchronized void downloadError(ResponseDownloader mod) {
        mod.setFinished(-1);
        handlerDownloader(mod);
        LaLog.e(ValueOf.logLivery(mod.getDownloadMessage()));
    }

    private synchronized void sendBroadcastDownload(ResponseDownloader mod) {
        Intent intent = new Intent(AnConstants.ACTION.ACTION_DOWNLOADER);
        intent.putExtra(AnConstants.ACTION.DOWNLOADER, mod);
        mContext.sendBroadcast(intent);
    }

    public void download() {
        String url = mod.getUrl();
        ThreadPool.getInstance().getThreadPoolExecutor().execute(() -> {
            File downloadResultFile;
            long finishedLength = 0;//记录下载文件的长度
            long contentLength = 0;
            /*
             * （1）：计算下载文件的大小
             * */
            try {
                contentLength = FileUtils.INSTANCE.getContentLength(mContext, url);
                if (contentLength == -1) {
                    mod.setFinished(869);
                    mod.setDownloadMessage("网络错误，请检查网络设置");
                    downloadError(mod);
                    return;
                }
            } catch (IOException e) {
                mod.setFinished(869);
                mod.setDownloadMessage("网络错误，请检查网络设置" + e.getMessage());
                downloadError(mod);
                return;
            }

            /*
             * (2)：首先判断是否指定了存放下载文件的路径
             * 在这里更新数据库【保存的名字】，【下载的最终路径】，【下载文件的大小】
             * */
            ResponseDownloader smartMod = FileUtils.INSTANCE.smartDownloaderFile(mContext, mod);
            mod = smartMod;
            downloadResultFile = smartMod.getDownloadResultFile();
            if (downloadResultFile != null) {
                contentLength = mod.getLength();

                /*
                 * （3）：更新数据库
                 * */
                if (mod.getId() >= 0) {
                    mDao.updateNpl(url, mod.getId(), mod.getFileName(), mod.getDownloadPath(), contentLength);
                }

                /*
                 * (4)：计算当前断点续传的长度，（这里有一个坑，原本我们记录数据库下载的长度可能和当前文件的长度不一致，导致下载的内容会缺失，所有这里直接按实际文件的长度下载）
                 * 假设用户手动删除了文件，那么长度为0，则重新开始下载，则在这里需要更新数据库下载成功的标志为0，
                 * */
                finishedLength = FileUtils.INSTANCE.getFileLength(downloadResultFile);
//                (废弃) 这里第2中方法需要从数据库里面拿到Length
//                finishedLength = queryDbFinishedLength(fileName);

                /*
                 * (5)：这里处理一些下载逻辑，0表示下载失败，1表示下载成功
                 * */
                if (finishedLength == -1) {
                    if (mod.getId() >= 0) {
                        mDao.updateFaiure(url, mod.getId());
                    }
                    finishedLength = 0;
                }
                if (contentLength == 0) {
                    mod.setFinished(869);
                    if (mod.getId() >= 0) {
                        mDao.updateFaiure(url, mod.getId());
                    }
                    mod.setDownloadMessage("下载失败");
                    downloadError(mod);
                } else if (contentLength == finishedLength) {
                    mod.setFinished(1);
                    if (mod.getId() >= 0) {
                        mDao.updateSuccess(url, mod.getId(), contentLength);
                    }
                    mod.setProgress(100);
                    handlerDownloader(mod);
                }


                /*
                 * （6）：从这里开始去真正的下载文件，Livery框架提供OkhhttpClient下载方式，区别于LodingDialog
                 * */
                HttpURLConnection conn = null;
                RandomAccessFile raf = null;
                InputStream is = null;
                try {
                    URL httpUrl = new URL(url);
                    conn = (HttpURLConnection) httpUrl.openConnection();
                    conn.setConnectTimeout(5 * 1000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Range", "bytes=" + finishedLength + "-" + mod.getLength());

                    raf = new RandomAccessFile(downloadResultFile, "rwd");
                    raf.seek(finishedLength);

                    int code = conn.getResponseCode();

                    if (code == HttpURLConnection.HTTP_PARTIAL) {
                        is = conn.getInputStream();
                        byte[] bt = new byte[1024];
                        int total = 0;
                        int len = -1;
                        long time = System.currentTimeMillis();
                        mod.setFinished(2);
                        while ((len = is.read(bt)) != -1) {
                            total += len;
                            raf.write(bt, 0, len);
                            finishedLength += len;
                            mod.setFinishedLength(finishedLength);
                            mod.setFinishedSize(DataService.getInstance().getDataSize(finishedLength));
                            int progress = 0;//计算已经下载的百分比

                            if (System.currentTimeMillis() - time > 1000) {
//                            progress = (int) (((float) mod.getFinishedLength() / mod.getLength()) * 100);
//                            progress = (int) (((float) mod.getFinishedLength() / conn.getContentLength()) * 100);
                                //计算已经下载的百分比
                                progress = (int) ((total + mod.getFinishedLength()) * 100 / conn.getContentLength());

                                time = System.currentTimeMillis();
                                mod.setProgress(progress);
                                handlerDownloader(mod);
                            }
                            if (mod.getFinished() == 4) {
                                if (mod.getId() >= 0) {
                                    mDao.updateLoding(url, mod.getId(), mod.getFinishedLength(), progress, 4);
                                }
                                mod.setFinished(4);
                                handlerDownloader(mod);
                                return;
                            }
                        }
                    }
                    mod.setFinished(1);
                    handlerDownloader(mod);
                } catch (Exception e) {
                    e.printStackTrace();
                    mod.setFinished(869);
                    if (mod.getId() >= 0) {
                        mDao.updateFaiure(url, mod.getId());
                    }
                    mod.setDownloadMessage(e.getMessage());
                    downloadError(mod);
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (raf != null) {
                            raf.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void pauseDownload() {
        mIsPause = true;
    }
}