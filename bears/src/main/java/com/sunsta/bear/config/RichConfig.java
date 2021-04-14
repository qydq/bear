package com.sunsta.bear.config;

import android.text.SpannableStringBuilder;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

import com.sunsta.bear.immersion.ColorDrawer;
import com.sunsta.bear.listener.OnSmartClickListener;

import java.io.Serializable;

/**
 * 为控制方法数量，不再提供字符串颜色资源设置本SpannableBuilder的颜色
 */
public class RichConfig implements Serializable {
    private static final long serialVersionUID = 2547012537213355594L;

    public SpannableStringBuilder getSpannableBuilder(@NonNull TextView textView, @NonNull String content, OnSmartClickListener<String> onSmartClickListener) {
        return null;
    }

    @ColorInt
    private int markBackground;//round标记的背景颜色 ， 参考：0，R.color.ColorGold，"FFD700"金黄色（说明：0表示为透明的颜色，一般不建议设置，因为没意义
    @ColorInt
    private int markColor;//round标记的字体颜色 ，参考：0，R.color.ColorWhite,"FFFFFF"白色(说明：0则表示跟随textView的颜色
    @ColorInt
    private int lightColor;//“markLight复制”，“markLink进入”字体颜色 ，参考：0，R.color.ColorRed，"#FF0000"类红色(说明：0则表示跟随textView的颜色
    @ColorInt
    private int linkColor;//https://zhihu.com/people/qydq【链接的颜色】，参考：0，R.color.an_color_holo_light_blue，"0000FF"类蓝色（说明：0表示跟随textView的颜色
    private String[] markFilterKey;//关键词数组 ，参考：String[] markFilterKey = {"wechat", "qq", "url", "weibo"}; （说明：该值可为空，在点击事件中会回调回去该类型的字符串
    private String markLight = "(点击复制)";//如果有标记的值，则表示有light的提示字段；（说明：为了区别，该字段字体比原textView小1dp
    private String markLink = "(点击进入)";//如有有标记的值，则表示有link的提示字段；（说明：为了区别，该字段比原textView小1dp
    private boolean isLineBreak;//是否根据关键词换行，默认不换行

    @ColorInt
    private int suffixBackground;//前缀或后缀，如【提示】标记的背景颜色 ，参考：0，R.color.ColorLightskyblue，"#3700B3"类蓝色（说明：0表示为透明的颜色，一般不建议设置，因为没意义
    @ColorInt
    private int suffixColor;//前缀或，后缀如【提示】的标记字体颜色 ，参考：0 ，R.color.ColorWhite ，"#FFFFFF" 白色(说明：0则表示跟随textView的颜色

    private String suffixName;//前缀或后缀，如【提示】字符串 ，参考：♀㊚提▲☎♈示☐⚠✉ （说明：该值支持特殊字符串符号，当suffixName为空时，则不需要suffixBackground与suffixColor

    /**
     * //前缀默认true，后缀false 说明：
     * （1）当suffixName有值时，并且firstSuffix为true，则设置【置顶】XXX ；
     * （2）当suffixName有值时，并且firstSuffix为false，则形如XXX【置顶】；
     * （3）当suffixName无值时，则不需要firstSuffix，该值无效
     */
    private boolean firstSuffix = true;

    private boolean httpsLink = true;//对于有的链接的请求，是否开启https地址返回，（说明：如地址无http标志,则默认返回带https的链接，如：https://zhihu.com/people/qydq

    private int radius;//圆角的大小（注意：单位必须设置px单位）

    private String copyTips;//点击复制，或者进入的提示，如默认xxx已复制（提示，如果不想要提示，则可以复写监听事件

    public String[] getMarkFilterKey() {
        return markFilterKey;
    }

    public void setMarkFilterKey(String[] markFilterKey) {
        this.markFilterKey = markFilterKey;
    }

    public String getMarkLight() {
        return markLight;
    }

    public void setMarkLight(String markLight) {
        this.markLight = markLight;
    }

    public String getMarkLink() {
        return markLink;
    }

    public void setMarkLink(String markLink) {
        this.markLink = markLink;
    }

    public boolean isLineBreak() {
        return isLineBreak;
    }

    public void setLineBreak(boolean lineBreak) {
        isLineBreak = lineBreak;
    }

    public String getSuffixName() {
        return suffixName;
    }

    public void setSuffixName(String suffixName) {
        this.suffixName = suffixName;
    }

    public boolean isFirstSuffix() {
        return firstSuffix;
    }

    public void setFirstSuffix(boolean firstSuffix) {
        this.firstSuffix = firstSuffix;
    }

    public boolean isHttpsLink() {
        return httpsLink;
    }

    public void setHttpsLink(boolean httpsLink) {
        this.httpsLink = httpsLink;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getCopyTips() {
        return copyTips;
    }

    public void setCopyTips(String copyTips) {
        this.copyTips = copyTips;
    }

    public void setMarkBackground(@ColorRes int markBackground) {
        this.markBackground = ColorDrawer.getColor(markBackground);
    }

    public void setMarkBackgroundChar(String markBackgroundChar) {
        this.markBackground = ColorDrawer.string2Int(markBackgroundChar);
    }

    public void setMarkColor(@ColorRes int markColor) {
        this.markColor = ColorDrawer.getColor(markColor);
    }

    public void setMarkColorChar(String markColorChar) {
        this.markColor = ColorDrawer.string2Int(markColorChar);
    }

    public void setLightColor(@ColorRes int lightColor) {
        this.lightColor = ColorDrawer.getColor(lightColor);
    }

    public void setLightColorChar(String lightColorChar) {
        this.lightColor = ColorDrawer.string2Int(lightColorChar);
    }

    public void setLinkColor(@ColorRes int linkColor) {
        this.linkColor = ColorDrawer.getColor(linkColor);
    }

    public void setLinkColorChar(String linkColorChar) {
        this.linkColor = ColorDrawer.string2Int(linkColorChar);
    }

    public void setSuffixBackground(@ColorRes int suffixBackground) {
        this.suffixBackground = ColorDrawer.getColor(suffixBackground);
    }

    public void setSuffixBackgroundChar(String suffixBackgroundChar) {
        this.suffixBackground = ColorDrawer.string2Int(suffixBackgroundChar);
    }

    public void setSuffixColor(@ColorRes int suffixColor) {
        this.suffixColor = ColorDrawer.getColor(suffixColor);
    }

    public void setSuffixColorChar(String suffixColorChar) {
        this.suffixColor = ColorDrawer.string2Int(suffixColorChar);
    }

    public int getMarkBackground() {
        return markBackground;
    }

    public int getMarkColor() {
        return markColor;
    }

    public int getLightColor() {
        return lightColor;
    }

    public int getLinkColor() {
        return linkColor;
    }

    public int getSuffixBackground() {
        return suffixBackground;
    }

    public int getSuffixColor() {
        return suffixColor;
    }
}