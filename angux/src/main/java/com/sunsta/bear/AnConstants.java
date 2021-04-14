package com.sunsta.bear;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【Aliff框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：an系列Aliffdd框架中的常量统一管理
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2016/8/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 4.0 |   2019/10/07           |   常量类修改为接口Interface，并且与base_fileprovider_takephoto.xml，并且借助aili项目重新整理定义所有的常量
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public interface AnConstants {
    String TAG = "AnConstants";
    /*Aliff folder*/
    /*
     * Aliff/
     * Aliff/record/
     * Aliff/movie/
     * Aliff/video/
     * Aliff/shps/
     * Aliff/apk/
     * Aliff/music/
     * Aliff/download/
     * Aliff/public/
     * Aliff/log/
     * Aliff/images/
     * Aliff/photo/
     * Aliff/temp/
     *
     * */
    String EMPTY = "";
    String LIVERY_NAME = "livery";


    /**
     * （1）。专属于，Aliff系列文件类别
     */
    String FOLDER_ROOT = "Aliff";//主目录文件名

    String FOLDER_MOVIE = "movie";//视频文件目录名
    String FOLDER_IMAGES = "image";//存放图片文件名

    String FOLDER_MUSIC = "music";//存放音乐文件名
    String FOLDER_DOWNLOAD = "download";//下载文件目录名
    String FOLDER_PUBLIC = "public";//公共文件夹名
    String FOLDER_APK = "apk";//存放apk文件的目录名
    String FOLDER_LOG = "log";//公共log文件目录名
    String FOLDER_TEMP = "temp";//公共temp文件目录名
    String FOLDER_CACHE = "cache";//公共Cache文件目录名
    String FOLDER_RECORD = "record";//公共record文件目录名

    String FILE_DB = "Aliff.db";//数据库文件名
    String FILE_SUFFIX_PNG = ".PNG";//png后缀图片
    String FILE_SUFFIX_JPEG = ".JPEG";//jpeg后缀图片
    String FILE_SUFFIX_APK = ".apk";//jpeg后缀图片
    String FILE_SUFFIX_JPG = ".jpg";//jpg后缀图片
    String PREFERENCE_NAME = "sp_public";//存放sp的名字，不是文件名（ sp，文件直接放在根目录Aliff下面


    String MEDIA_RELATIVE_PATH = "media_relative_path";//报错解决
    String MEDIACOLUMNS_DURATION = "mediacolumns_duration";//报错解决
    String BUCKET_DISPLAY_NAME = "bucket_display_name";//报错解决


    /**
     * （2）。专属于，An系列ConfigData配置类别
     */
    String default_log_filter = "--sunst888--";//过滤所有日志的标志
    int default_timeout = 5;//请求超时时间
    int default_timedelay = 10;//通用延迟时间
    int ad_download_timeout = 10;//下载广告请求超时时间
    int ad_default_time = 3;//广告时间
    int file_download_timeout = 15;//下载文件请求超时时间,apk,文件，15秒就要下载完成，否则用户体验极差
    boolean default_daynight = false;//是否是夜间模式
    /*广告有两种显示模式*/
    boolean ad_show_status = false;//是否是开启广告，false默认不开启广告
    boolean ad_show_preview = false;//开启广告，是否显示第一种类型的广告，默认显示第二种类型的广告
    int ad_show_type = 0;//广告类型，0表示普通图片，1表示GIF图片，2表示视频，默认0。
    int ERROR_VIEW_TYPE = 0;


    /**
     * (3)。专属于，部分自定义权限类，具体在LaPermission中
     */
    String PERMISSION_MICROPHONE = "android.permission-group.MICROPHONE";
    String PERMISSION_PHONE = "android.permission-group.PHONE";


    /**
     * （4）。专属于，公共部分
     * */
    /**
     * retrofit使用参考下面博客。
     * http://blog.csdn.net/carson_ho/article/details/73732076
     * Retrofit把网络请求的URL分成了两部分：一部分放在Retrofit对象里，另一部分放在网络请求接口里
     * 如果接口里的url是一个完整的网址，那么放在Retrofit对象里的URL可以忽略
     * Exg : http://fy.iciba.com/ajax.php?a=fy&f=auto&t=auto&w=hello%20world
     * 注解上的url为path = ajax.php?a=fy&f=auto&t=auto&w=hello%20world;
     * baseUrl=http://fy.iciba.com/
     * 如果path是一个完整网址，则retofit的baseUrl可以忽略
     */
    String PUBLIC_THREAD_NAME = "Aliff_thread";//线程操作的资源常量
    String PUBLIC_BASE_URL = "https://qydq.github.io/";
    String PUBLIC_STATUS_SCTLNK = "lkssl";


    /*
     * 统一管理存储键值对的键，一般用于Intent传递数据
     * */
    interface EXTRA {
        String DOWNLOADER = "ResponseDownloader";//下载实体类
        String APK_INTALLPATH = "apkintallpath";//apk安装路径
        String FIRST_INTOAPP = "first_into_app";//是否第一次进入app
        String FIRST_INTOAPP_CHECKBOX = "checkbox";//是否第一次进入选择checkbox
        String APP_RUNNING_TIME = "app_running_time";//app运行时间
        String BASE_URL = "baseUrl";//app运行时间
        String APP_TITLE = "title";//标题
        String APP_URL_MORE = "url";//链接地址或者是更多
        String APP_WEB_SHOW_MORE = "showMore";//是否显示更多web正在跳转字样
        String APP_WEB_SHOW_RIGHTBAR = "showRightBar";//是否显示右边的字样
        String DOWNLOAD_STATUS = "download_status";//下载文件状态
        String DOWNLOAD_DATA = "download_data";//下载文件数据
    }

    interface KEY {
        String APP_AN = "use_anapp";
        String HTTP_REQUEST_URL = "http_request_url";
        String LOG_FILTER = "log_filter";
        String LOG_ENABLE = "log_enable";
        String TOKEN_VALUE = "token_value";
        String TOKEN_NAME = "token_key";
        String CHANNEL_ID = "channelID";
        String CHANNEL_NAME = "channelNAME";
        String CHANNEL_LEVEL = "channelLEVEL";
        String APP_ICON = "app_icon";
        String DAYNIGHT_MODE = "daynight_mode";//深色主题配置
    }

    interface VALUE {
        String LOG_LIVERY_EXCEPTION = "LOG_LIVERY_EXCEPTION";
        String LOG_LIVERY_ERROR = "LOG_LIVERY_ERROR";
        String LOG_LIVERY = "LOG_LIVERY";
        String LOG_FASTER = "LIVERY_LOG_FASTER : ";
        String LOG_HTTP_DOWNLOAD = "LIVERY_LOG_DOWANLOAD : ";
        String HTTP_REQUEST_URL = "https://github.com/qydq/api/";
    }

    interface CONFIG {
        String default_encode = "UTF-8";//UTF8编码===StandardCharsets.UTF_8
    }

    interface ACTION {
        String ACTION_DOWNLOAD_MESSAGE_PROGRESS = "downloadService";
        String DOWNLOAD_ONLOADING = "onLoading";
        String DOWNLOAD_ONPAUSED = "onPaused";
        String DOWNLOAD_ONCANCELED = "onCanceled";
        String DOWNLOAD_ONSUCCESS = "onSuccess";
        String DOWNLOAD_ONFAILURE = "onFailure";

        String ACTION_DOWNLOADER = "ACTION_DOWNLOADER";
        String DOWNLOADER = "downloader";
    }

    /*
     * 网络请求地址*/
    interface URL {
        String BASE_URL = "https://github.com/qydq/api/";
        String BASE_URL_HTTS = "https://should/";
        String BASE_URL_HTTP = "http://should/";
        String AILI_UPDATE_PAGE = PUBLIC_BASE_URL + "raw/chat/sample/update.html";//aili下载更新的page页面，可以用Web页面加载出来。
        String AILI_UPDATE_APK = PUBLIC_BASE_URL + "raw/chat/sample/sample-debug.apk";//真实apk下载地址。
        String AILI_UPDATE_API = PUBLIC_BASE_URL + "raw/chat/sample/update.html";//aili（安妮，爱李）apk更新的接口（包含版本信息，下载连接），这里可以是否在后台下载，保存到本地作为离线推送。
    }


    interface CAPTURE {
        int REQUEST_PERMISS = 2;

        /**
         * request Code 裁剪照片
         **/
        int RC_CROP = 1001;
        /**
         * request Code 从相机获取照片并裁剪
         **/
        int RC_PICK_PICTURE_FROM_CAPTURE_CROP = 1002;
        /**
         * request Code 从相机获取照片不裁剪
         **/
        int RC_PICK_PICTURE_FROM_CAPTURE = 1003;
        /**
         * request Code 从文件中选择照片
         **/
        int RC_PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL = 1004;
        /**
         * request Code 从文件中选择照片并裁剪
         **/
        int RC_PICK_PICTURE_FROM_DOCUMENTS_CROP = 1005;
        /**
         * request Code 从相册选择照
         **/
        int RC_PICK_PICTURE_FROM_GALLERY_ORIGINAL = 1006;
        /**
         * request Code 从相册选择照片并裁剪
         **/
        int RC_PICK_PICTURE_FROM_GALLERY_CROP = 1007;
        /**
         * request Code 选择多张照片
         **/
        int RC_PICK_MULTIPLE = 1008;

        /**
         * requestCode 请求权限
         **/
        int PERMISSION_REQUEST_TAKE_PHOTO = 2000;
    }
}