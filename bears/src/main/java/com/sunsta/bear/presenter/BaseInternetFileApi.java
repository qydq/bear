package com.sunsta.bear.presenter;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by qydq on 2017/11/23.
 * github.com/qydq/ali-aw-base
 * downloadFileWithFixedRepos
 * 如果你下载的文件很大,则使用@Streaming 定义Request
 * downloadFileDynamicRepos
 * 下载单一的文件，传入文件的地址，或者String下载路径URL的数组
 */

public interface BaseInternetFileApi {
    String naticePath = "blog/{id}";//请求的路径。


    //option 1: a resource relative to your base URL
    @Streaming
    @GET("/uploads/allimg/160726/7730-160H6114H5.jpg")
    Call<ResponseBody> downloadFileWithFixedRepos();

    //option 2:using a dynamic URL
    @Streaming
    @GET
    Call<ResponseBody> downloadFileDynamicRepos(@Url String IMAGE_URL);

    @Streaming
    @GET
    Call<ResponseBody> downloadFileDynamicRepos(@Url String[] IMAGE_URLS);

    @Streaming
    @GET
    Observable<ResponseBody> observableReallyDownload(@Url String fullUrl);


    /**
     * 以下为新添加文件
     * */
    /**
     * （4）上传文件。
     */
    @Multipart
    @POST("{pathUrl}")
    <T> Observable<ResponseBody> uploadFiles(@Path("pathUrl") String pathUrl,
                                             @Path("headers") Map<String, String> headers,
                                             @Part("filename") String filename,
                                             @PartMap() Map<String, RequestBody> maps);

    @Multipart
    <T> Observable<ResponseBody> uploadFiles(@Path("headers") Map<String, String> headers,
                                             @Part("filename") String filename,
                                             @PartMap() Map<String, RequestBody> maps);

    /**
     * （5）下载文件。
     */
    @Streaming
    @GET
    <T> Observable<ResponseBody> tReallyDownloadFile(@Url String fullUrl);

    //option 1: a resource relative to your base URL
    @Streaming
    @GET(naticePath)
    <T> Call<ResponseBody> tDownloadFileWithFixedRepos();

    //option 2:using a dynamic URL
    @Streaming
    @GET
    <T> Call<ResponseBody> tDownloadFileDynamicRepos(@Url String IMAGE_URL);

    @Streaming
    @GET
    <T> Call<ResponseBody> tDownloadFileDynamicRepos(@Url String[] IMAGE_URLS);

}