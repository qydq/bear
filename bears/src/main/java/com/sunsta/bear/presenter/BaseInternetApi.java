package com.sunsta.bear.presenter;

import com.sunsta.bear.model.ReplySSLMode;
import com.sunsta.bear.model.entity.ResponseResultMode;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by staryumou on 2018/3/18.
 * <p>
 * ApiService说明：接口写完，但是还没有测试。
 * 注意1：retrofit2.0后：BaseUrl要以/结尾；@GET 等请求不要以/开头；@Url: 可以定义完整url，不要以 / 开头。
 * 注意2：请求的URL可以根据函数参数动态更新。一个可替换的区块为用 {  和  } 包围的字符串，
 * 而函数参数必需用  @Path 注解表明，并且注解的参数为同样的字符串
 */

public interface BaseInternetApi<E> {
    //    String RetPath = "/uploads/allimg/160726/7730-160H6114H5.jpg";
    String naticePath = "blog/{id}";//请求的路径。

    // 访问的API是：https://api.github.com/users/{user}/repos
    // 在发起请求时， {user} 会被替换为方法的第一个参数 user（被@Path注解作用）


    /**
     * method 表示请的方法，不区分大小写
     * path表示路径 ,如users/{user}。
     * hasBody表示是否有请求体
     */

    //initBaseUrl = https://github.com/ ()

    //initParm = {replace}

    //String replace = "qydq/api/bgwan/0609"

    //final request url = https://github.com/qydq/api/bgwan/0609
    @HTTP(method = "GET", path = "{pathUrl}")
    Call<ResponseBody> callHttpGetWith(@Path("pathUrl") String pathUrl);

    @HTTP(method = "POST", path = "{pathUrl}")
    Call<ResponseBody> callHttpPostWith(@Path("pathUrl") String pathUrl);

    @HTTP(method = "GET", path = "{pathUrl}", hasBody = true)
    Call<ResponseBody> callHttpBodyGetWith(@Path("pathUrl") String pathUrl);

    @HTTP(method = "POST", path = "{pathUrl}", hasBody = true)
    Call<ResponseBody> callHttpBodyPostWith(@Path("pathUrl") String pathUrl);

    @GET
    Call<ResponseBody> callGet(@Url String fullUrl);

    @GET
    Call<E> callGets(@Url String fullUrl);

    @GET("{pathUrl}")
    Call<ResponseBody> callGetWith(@Path("pathUrl") String pathUrl);

    @POST
    Call<ResponseBody> callPost(@Url String fullUrl);

    @POST("{pathUrl}")
    Call<ResponseBody> callPostWith(@Path("pathUrl") String pathUrl);

    @Streaming
    @GET
    Call<ResponseBody> callReallyGetHtml(@Url String fullUrl);


    /**
     * 2.t系列正常的网络请求
     */

    /**
     * （1）简单get post请求
     */
    @GET
    <T> Call<ResponseBody> tGet(@Url String fullUrl);

    @GET("{pathUrl}")
    <T> Call<ResponseBody> tGetWith(@Path("pathUrl") String pathUrl);

    @POST
    <T> Call<ResponseBody> tPost(@Url String fullUrl);

    @POST("{pathUrl}")
    <T> Call<ResponseBody> tPostWith(@Path("pathUrl") String pathUrl);

    /**
     * （2）带参数的网络请求
     * 1.pathUrl = "qydq/api/bgwan/0609"
     * 2.more = "md5xxx"
     * 3.id = 2019
     * <p>
     * final request url = https://github.com/qydq/api/bgwan/0609/md5xxx?id=2019
     */
    @GET("{pathUrl}")
    <T> Observable<ResponseBody> tGetDataWithId(@Path("pathUrl") String pathUrl, @Query("id") String id);

    @POST("{pathUrl}")
    <T> Observable<ResponseBody> tPostDataWithId(@Path("pathUrl") String pathUrl, @Query("id") String id);

    @GET("{pathUrl}")
    <T> Observable<ResponseBody> tGetDataWithMaps(@Path("pathUrl") String pathUrl, @QueryMap Map<String, String> maps);

    @POST("{pathUrl}")
    <T> Observable<ResponseBody> tPostDataWithMaps(@Path("pathUrl") String pathUrl, @QueryMap Map<String, String> maps);

    /**
     * （3）Retrofit对一个简单的html页面进行网络请求
     */

    @Streaming
    @GET
    <T> Call<ResponseBody> tReallyGetHtmlT(@Url String fullUrl);

    @Streaming
    @POST
    <T> Call<ResponseBody> tReallyPostHtml(@Url String fullUrl);


    /**
     * 3.observable请求模块
     */
    @POST
    Observable<ResponseBody> observablePost();

    @GET
    Observable<ResponseBody> observableGet();

    @POST
    Observable<ResponseBody> observableReallyPost(@Url String fullUrl);

    @GET
    Observable<ResponseBody> observableReallyGet(@Url String fullUrl);

    @GET
    Observable<ResponseResultMode<ReplySSLMode>> observableSSl(@Url String fullUrl);

    @POST
    Observable<ResponseBody> observableReallyPost(@Url String fullUrl, @QueryMap Map<String, String> maps);

    @GET
    Observable<ResponseBody> observableReallyGet(@Url String fullUrl, @QueryMap Map<String, String> maps);

    @POST
    Observable<ResponseBody> observablePost(@QueryMap Map<String, String> maps);

    @GET
    Observable<ResponseBody> observableGet(@QueryMap Map<String, String> maps);

    @POST("{pathUrl}")
    Observable<ResponseBody> observablePost(@Path("pathUrl") String pathUrl);

    @GET("{pathUrl}")
    Observable<ResponseBody> observableGet(@Path("pathUrl") String pathUrl);

    @POST("{pathUrl}")
    Observable<ResponseBody> observablePostWith(@Path("pathUrl") String pathUrl, @QueryMap Map<String, String> maps);

    @GET("{pathUrl}")
    Observable<ResponseBody> observableGetWith(@Path("pathUrl") String pathUrl, @QueryMap Map<String, String> maps);


    @Multipart
    @POST("{pathUrl}")
    Observable<ResponseBody> observableUpLoadFile(@Path("pathUrl") String pathUrl, @Part("imageName") RequestBody avatar);
}