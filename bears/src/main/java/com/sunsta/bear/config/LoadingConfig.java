package com.sunsta.bear.config;

import androidx.annotation.DrawableRes;

import com.sunsta.bear.AnConstants;

import java.io.Serializable;

public class LoadingConfig implements Serializable {
    private static final long serialVersionUID = -6910578153718120914L;
    private boolean lastPoint;//默认只有loading1，具有该特性（最后面显示。。。）
    private boolean fixedDistance;//固定内容距离，默认是根据内容变化的，true的话需要计算
    private boolean backgroundDimEnabled;//背景是否有阴影
    private boolean fullWidthScreen;//是否是全屏
    private int backgroundFrame;//设置背景的资源
    private int gravity;//设置加载框的位置，只有在可配置图标的场景需要设置Gravity.TOP,Gravity.LEFT
    private int maxLine;//内容最大的行数
    private int contentGravity;//内容属性
    private boolean cancelable = true;//loading框是否可以取消，默认可以取消，true
    private int dialogClassify;//loading0,默认值0，左右布局的对话框，居中(半屏显示)，含point的效果，(默认是加载中)，位置固定
    private boolean backgroundAlpha;//是否背景可以设置alpha=0.96
    private boolean randomColor;//是否启用随机颜色，如果是启用随机颜色，则进度框，字体颜色为随机值
    private String content;//加载对话框的内容
    private int animationIvId;//ivPrimary 's head resource,only dialogClassfy =1 available , default value is R.mipmap.ic_color_share_url


    private int type = 0;//-1表示Loading下载对话框，0默认对话框(取消，确定），1下载文件的对话框；2下载文件带暂停，继续的对话框

    /*
     * （1）：如果downloadPath有值，【则appSystem无效】则最后的file为downloadPath产生的值
     * （1）：如果downloadPath有值，【则appSystem无效】则最后的file为downloadPath产生的值，fileName为计算的downloadPath的值，如果再得不到值，则为url计算的值
     * （2）：如果fileName有值，【A:默认情况appSystem=false是保存在Aliff目录下自动分类】
     * （2）：如果fileName有值，【B:appSystem=true是保存在data/data/packagename/files/】
     * （3）：如果fileName无值，最后计算的值为url计算的值，【A:默认情况appSystem=false是保存在Aliff目录下自动分类】
     * */
    /**
     * 用于对话框的下载文件，参考：ResponseDownloader
     */
    private String url;//已知
    private String name = AnConstants.EMPTY;//可选，下载内容的名字，如果为空则会与fileName相同
    private String fileName = AnConstants.EMPTY;//可以为空【是否自动加上事件前缀或后缀暂不考虑】
    private String downloadPath;//可以为空，最后需要指定，like this Aliff/movie/aliff0069.mp4

    private boolean appSystem = false;//文件是否保存在app内部存储目录###
    private boolean nameOfTime = false;//当fileName有值的时候是否，timeOfName，加上后缀如：sunstaliffvery_20200921_22:49:13.apk###
    private boolean autoOpen = false;//是否根据文件主动打开或者安装，在主动安装的时候，如果有对话框需要先关闭对话框###
    /**
     * 这里的逻辑修改为：先判断是否是downloadService，再判断是否是useRetrofit使用retrofit下载
     */
    private boolean useRetrofit = false;//是否启用retrofit下载模式，###
    private boolean downloadService = false;//是否开启一个Service下载文件，最后的结果可以通过广播监听###

    /**
     * 用于正常的对话框
     */
    private String leftBtnContext;
    private String rightBtnText;
    private String title = AnConstants.EMPTY;
    private String apkName;
    private boolean autoCloseDialog = false;//当执行完成是否需要主动关闭对话框
    private int leftBackground;//一般为0，如果是对话框，左边按钮的背景drawable不能设置为颜色
    private int rightBackground;//一般为0，如果是对话框，右边按钮的背景drawable不能设置为颜色
    @DrawableRes
    private int rightIvResouce;//right iv resource , default value is 0

    public int getMaxLine() {
        return maxLine;
    }

    public void setMaxLine(int maxLine) {
        this.maxLine = maxLine;
    }

    public int getContentGravity() {
        return contentGravity;
    }

    public void setContentGravity(int contentGravity) {
        this.contentGravity = contentGravity;
    }

    public boolean isRandomColor() {
        return randomColor;
    }

    public void setRandomColor(boolean randomColor) {
        this.randomColor = randomColor;
    }

    public int getBackgroundFrame() {
        return backgroundFrame;
    }

    public void setBackgroundFrame(int backgroundFrame) {
        this.backgroundFrame = backgroundFrame;
    }

    public int getAnimationIvId() {
        return animationIvId;
    }

    public void setAnimationIvId(int animationIvId) {
        this.animationIvId = animationIvId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isBackgroundAlpha() {
        return backgroundAlpha;
    }

    public void setBackgroundAlpha(boolean backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
    }

    public int getDialogClassify() {
        return dialogClassify;
    }

    public void setDialogClassify(int dialogClassify) {
        this.dialogClassify = dialogClassify;
    }

    public boolean isBackgroundDimEnabled() {
        return backgroundDimEnabled;
    }

    public void setBackgroundDimEnabled(boolean backgroundDimEnabled) {
        this.backgroundDimEnabled = backgroundDimEnabled;
    }

    public boolean isFullWidthScreen() {
        return fullWidthScreen;
    }

    public void setFullWidthScreen(boolean fullWidthScreen) {
        this.fullWidthScreen = fullWidthScreen;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public boolean isLastPoint() {
        return lastPoint;
    }

    public void setLastPoint(boolean lastPoint) {
        this.lastPoint = lastPoint;
    }

    public boolean isFixedDistance() {
        return fixedDistance;
    }

    public void setFixedDistance(boolean fixedDistance) {
        this.fixedDistance = fixedDistance;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
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

    public boolean isUseRetrofit() {
        return useRetrofit;
    }

    public void setUseRetrofit(boolean useRetrofit) {
        this.useRetrofit = useRetrofit;
    }

    public boolean isDownloadService() {
        return downloadService;
    }

    public void setDownloadService(boolean downloadService) {
        this.downloadService = downloadService;
    }

    public String getLeftBtnContext() {
        return leftBtnContext;
    }

    public void setLeftBtnContext(String leftBtnContext) {
        this.leftBtnContext = leftBtnContext;
    }

    public String getRightBtnText() {
        return rightBtnText;
    }

    public void setRightBtnText(String rightBtnText) {
        this.rightBtnText = rightBtnText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public boolean isAutoCloseDialog() {
        return autoCloseDialog;
    }

    public void setAutoCloseDialog(boolean autoCloseDialog) {
        this.autoCloseDialog = autoCloseDialog;
    }

    public int getLeftBackground() {
        return leftBackground;
    }

    public void setLeftBackground(int leftBackground) {
        this.leftBackground = leftBackground;
    }

    public int getRightBackground() {
        return rightBackground;
    }

    public void setRightBackground(int rightBackground) {
        this.rightBackground = rightBackground;
    }

    public int getRightIvResouce() {
        return rightIvResouce;
    }

    public void setRightIvResouce(int rightIvResouce) {
        this.rightIvResouce = rightIvResouce;
    }
}