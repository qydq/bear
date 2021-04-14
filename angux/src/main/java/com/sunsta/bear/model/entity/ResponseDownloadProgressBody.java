package com.sunsta.bear.model.entity;

import com.sunsta.bear.listener.DownloaderListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * ResponseBody for download
 * Created by sunst on 16/5/11.
 */
public class ResponseDownloadProgressBody extends ResponseBody {

    private ResponseBody responseBody;
    private DownloaderListener progressListener;
    private ResponseDownloader downloader;
    private BufferedSource bufferedSource;

    public ResponseDownloadProgressBody(ResponseBody responseBody, DownloaderListener progressListener, ResponseDownloader downloader) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        this.downloader = downloader;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                if (null != progressListener) {
                    if (bytesRead == -1) {
                        progressListener.onSuccess(downloader);
                    } else {
                        int progress = (int) ((bytesRead * 100) / responseBody.contentLength());
//                        int progress = (int) ((responseBody.contentLength() / totalBytesRead) * 100);
                        downloader.setProgress(progress);
                        progressListener.onProgress(downloader);
                    }
                }
                return bytesRead;
            }
        };

    }
}
