package com.sunsta.bear.model.entity;

import com.sunsta.bear.AnConstants;

import java.io.File;
import java.io.Serializable;


/**
 * 向抛出出一个请求的接口
 * Created by qydq on 2018/1/17.
 * github.com/qydq/ali-aw-base
 * 下载文件有几种状态回调：对应于DownloadCallBack ,对应：finished字段
 * <p>
 * 下载文件几种状态监听，对应于DownloadListener
 * 0,初始化状态，下载开始，准备下载，progress默认0
 * 1，【下载成功，】只有下载完成时，该值才会标记为下载完成1,默认0 ,progress=100
 * 2，正在下载，对应到进度，百分比 onProgress,progress动态
 * 3，取消下载onCancel ,progress=0
 * 4，暂停下载onPause,progress动态
 * 469【下载失败，】下载失败，给出原因,progress=0（如果遇到用户主动删除下载的文件，去对比的时候，发现文件被删除则数据库应该把该值重置为0【动态计算】）
 * <p>
 */

/**
 * 数据库中保存的字段，需要对外提供（以维护下载的状态）
 * 下载之前初始化的时候，数据库中就已经存有了【id】，【url】
 * 对于fileName,downloadPath：分两种情况，如果是外面指定了这两个值，那么则使用这两个值，否则2：则需要根据id，url来存入当前下载的名字，和downloadPath
 * 如果指定了downloadPath则不需要指定fileName了，会自动存入数据库名字
 * 如果未指定downloadPath，指定fileName，那么生成的文件则采用fileName，形如[lifangfang.mp4]
 * 如果未指定downloadPath，未指定fileName，那么生成的文件，则根据下载url后面的文件的后缀指定
 */
public class ResponseDownloader implements Serializable {
    private static final long serialVersionUID = 1704750782192216827L;
    private int id = -1;//已知，如果id不为-1表示需要存如数据库
    private int position = 0;//可选，对于类似于优酷列表的实体位置
    private String url;//已知
    private String name = AnConstants.EMPTY;//可选，下载内容的名字，如果为空则会与fileName相同
    private String fileName = AnConstants.EMPTY;//可以为空【是否自动加上事件前缀或后缀暂不考虑】
    private String size;//总文件大小，对应length，单位KB,MB，动态计算
    private String finishedSize;//当前下载的文件大小，对应finishedLength，单位KB,MB，动态计算
    private long length;//可以计算
    private long finishedLength = 0;//当前以及下载的长度，默认从0开始下载，【动态计算】
    private int progress;//当前下载的进度，【动态计算】
    private int writeByte = 1024;//可选，设置每次下载的字节数
    private int finished = 0;//只有下载完成时，该值才会标记为下载完成1,默认0,-1表示下载失败，（如果遇到用户主动删除下载的文件，去对比的时候，发现文件被删除则应该把该值重置为0【动态计算】，2表示当前暂停，3表示用户取消了下载
    private String downloadPath;//可以为空，最后需要指定
    private File downloadResultFile;//不能指定该值，只能计算
    private boolean openForeground = false;//是否开启前台进程下载，默认false,关闭前台执行，该任务为串行执行, =ture则打开前台执行， 该任务为并行执行
    private boolean openDatabase = false;//是否打开数据库存储
    private boolean openNotification = false;//下载的时候是否打开后台通知
    private String downloadMessage;//保留下载成功失败的提示信息
    private boolean appSystem = false;//文件是否保存在app内部存储目录###
    private boolean nameOfTime = false;//当fileName有值的时候是否，timeOfName，加上后缀如：sunstaliffvery_20200921_22:49:13.apk###
    private boolean autoOpen = false;//是否根据文件主动打开或者安装，在主动安装的时候，如果有对话框需要先关闭对话框###
    /**
     * 这里的逻辑修改为：先判断是否是downloadService，再判断是否是useRetrofit使用retrofit下载
     */
    private boolean downloadService = false;//是否开启一个Service下载文件，最后的结果可以通过广播监听###

    public ResponseDownloader() {
        super();
    }

    public ResponseDownloader(int id, String url, String fullPath) {
        this.id = id;
        this.url = url;
        this.downloadPath = fullPath;
    }

    public ResponseDownloader(String url) {
        this.url = url;
    }

    public ResponseDownloader(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean database() {
        return openDatabase;
    }

    public void openDatabase() {
        this.openDatabase = true;
    }

    public boolean notification() {
        return openNotification;
    }

    public void openNotification() {
        this.openNotification = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFinishedSize() {
        return finishedSize;
    }

    public void setFinishedSize(String finishedSize) {
        this.finishedSize = finishedSize;
    }

    public long getFinishedLength() {
        return finishedLength;
    }

    public void setFinishedLength(long finishedLength) {
        this.finishedLength = finishedLength;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public boolean foreground() {
        return openForeground;
    }

    public void openForeground() {
        this.openForeground = true;
    }

    public String getDownloadMessage() {
        return downloadMessage;
    }

    public void setDownloadMessage(String downloadMessage) {
        this.downloadMessage = downloadMessage;
    }

    public boolean isAppSystem() {
        return appSystem;
    }

    public void setAppSystem(boolean appSystem) {
        this.appSystem = appSystem;
    }

    public boolean isNameOfTime() {
        return nameOfTime;
    }

    public void setNameOfTime(boolean nameOfTime) {
        this.nameOfTime = nameOfTime;
    }

    public boolean isAutoOpen() {
        return autoOpen;
    }

    public void setAutoOpen(boolean autoOpen) {
        this.autoOpen = autoOpen;
    }

    public boolean isDownloadService() {
        return downloadService;
    }

    public void setDownloadService(boolean downloadService) {
        this.downloadService = downloadService;
    }

    public int getWriteByte() {
        return writeByte;
    }

    public void setWriteByte(int writeByte) {
        this.writeByte = writeByte;
    }

    public File getDownloadResultFile() {
        return downloadResultFile;
    }

    public void setDownloadResultFile(File downloadResultFile) {
        this.downloadResultFile = downloadResultFile;
    }

    @Override
    public String toString() {
        return "ResponseDownloader{" +
                "id=" + id +
                ", position=" + position +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                ", size='" + size + '\'' +
                ", finishedSize='" + finishedSize + '\'' +
                ", length=" + length +
                ", finishedLength=" + finishedLength +
                ", progress=" + progress +
                ", finished=" + finished +
                ", writeByte=" + writeByte +
                ", downloadPath='" + downloadPath + '\'' +
                ", openForeground=" + openForeground +
                ", openDatabase=" + openDatabase +
                ", openNotification=" + openNotification +
                ", downloadMessage='" + downloadMessage + '\'' +
                ", appSystem=" + appSystem +
                ", nameOfTime=" + nameOfTime +
                ", autoOpen=" + autoOpen +
                ", downloadService=" + downloadService +
                '}';
    }
}