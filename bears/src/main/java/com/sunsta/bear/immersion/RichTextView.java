package com.sunsta.bear.immersion;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.sunsta.bear.AnApplication;
import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.config.RichConfig;
import com.sunsta.bear.faster.DataService;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.ThreadPool;
import com.sunsta.bear.faster.ToastUtils;
import com.sunsta.bear.listener.OnSmartClickListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichTextView {
    private static int nextIndex;
    private static int length;

    public static void setSequenceText(@NonNull TextView textView, @NonNull String content) {
        setSequenceText(textView, content, false, 300);
    }

    public static void openSequenceText(@NonNull TextView textView) {
        setSequenceText(textView, textView.getText().toString().trim());
    }

    public static void repatSequenceText(@NonNull TextView textView, @NonNull String content) {
        setSequenceText(textView, content, true, 300);
    }

    public static void setSequenceText(@NonNull TextView textView, @NonNull String content, long time) {
        setSequenceText(textView, content, false, time);
    }

    private static void setSequenceText(@NonNull TextView textView, @NonNull String content, boolean repeat, long time) {
        if (TextUtils.isEmpty(content)) {
            content = textView.getText().toString().trim();
        }
        if (!TextUtils.isEmpty(content)) {
            length = content.length();
            taskSequenceText(textView, content, 0, repeat, time);
        }
    }

    private static void taskSequenceText(TextView textView, String content, final int index, boolean repeat, long time) {
        ThreadPool.FLAG_WORKING = true;
        ThreadPool.getInstance().getThreadPoolExecutor().execute(() -> {
            try {
                if (index <= content.length()) {
                    String result = content.substring(0, index);
                    textView.post(() -> textView.setText(result));
                    Thread.sleep(time);
                    nextIndex = index + 1;//多截取一个
                    if (nextIndex <= length) {//如果还有汉字，那么继续开启线程
                        taskSequenceText(textView, content, nextIndex, repeat, time);
                    } else {
                        if (repeat) {
                            //在hideProgressBar的时设置它为false,当设置完成以后，再还原为true
                            if (ThreadPool.FLAG_WORKING) {
                                taskSequenceText(textView, content, 0, repeat, time);
                            } else {
                                ThreadPool.FLAG_WORKING = true;
                            }
                            taskSequenceText(textView, content, 0, repeat, time);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    //android中为textview动态设置字体为粗体,paint may not use ,so suggestion use thie
    public static void setTextBold(@NonNull TextView textView) {
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    //设置不为加粗 paint may not use ,so suggestion use thie
    public static void setTextUnBold(@NonNull TextView textView) {
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
    }

    public static void setTextBoldWithPaint(@NonNull TextView textView) {
        textView.getPaint().setFakeBoldText(true);//加粗
    }

    /**
     * 字体大小设置
     */
    public static void setTextSize(@NonNull TextView textView, @DimenRes int sizeDimenId) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getContext().getResources().getDimensionPixelSize(sizeDimenId));
    }

    public static void setTextSize(@NonNull TextView textView, float size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public static void setTextBoldWithHtml(@NonNull TextView textView, @NonNull String txt) {
        setRichText(textView, "<font><b>" + txt + "</b></font>");
    }

    //给TextView设置部分大小
    public static void setPartialSize(TextView tv, int start, int end, int textSize) {
        try {
            String s = tv.getText().toString();
            //size：默认单位为px。
            //dip：true为size的单位是dip，false为px。
            Spannable spannable = new SpannableString(s);
            spannable.setSpan(new AbsoluteSizeSpan(textSize, true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(spannable);
        } catch (Exception e) {
        }
    }

    //给TextView设置部分颜色
    public static void setPartialColor(TextView tv, int start, int end, int textColor) {
        try {
            String s = tv.getText().toString();
            Spannable spannable = new SpannableString(s);
            spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(tv.getContext(), textColor)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(spannable);
        } catch (Exception e) {

        }
    }

    /**
     * 仅用于简单的富文本显示
     */
    public static void setRichText(@NonNull TextView textView, @NonNull String source) {
        if (source.contains("<font") && source.contains("</font>")) {
            textView.setText(Html.fromHtml(source));
        } else {
            textView.setText(source);
        }
    }

    //给TextView设置下划线
    public static void setUnderLine(TextView tv) {
        if (tv.getText() != null) {
            String udata = tv.getText().toString();
            SpannableString content = new SpannableString(udata);
            content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
            tv.setText(content);

        } else {
            tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    public static void setCircleText(@NonNull TextView textView, @NonNull String content, @NonNull String suffixName) {
        SpannableString spannableString = new SpannableString(suffixName + content);
        spannableString.setSpan(new RoundBackgroundColorSpan(AnApplication.getApplication(), Color.parseColor("#FFFF3441"), Color.parseColor("#FFFFFF")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(new RoundBackgroundColorSpan(activity, R.color.ColorRed, Color.parseColor("#FFFFFF")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(0.8f), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }

    public static void setSuffixText(@NonNull TextView textView, @NonNull String content, @NonNull String suffixName) {
        setSuffixText(textView, content, "#FFFF3441", suffixName);
    }

    /**
     * @param roundBackground 背景颜色 ，参考：#FFFF3441
     * @param roundColor      字体颜色 ，参考：#FFFFFF
     */
    public static void setSuffixText(@NonNull TextView textView, @NonNull String content, @NonNull String roundBackground, @NonNull String roundColor, @NonNull String suffixName) {
        localSuffix(textView, content, ColorDrawer.string2Int(roundBackground), ColorDrawer.string2Int(roundColor), suffixName);
    }

    public static void setSuffixText(@NonNull TextView textView, @NonNull String content, @ColorRes int roundBackground, @ColorRes int roundColor, @NonNull String suffixName) {
        localSuffix(textView, content, ColorDrawer.getColor(roundBackground), ColorDrawer.getColor(roundColor), suffixName);
    }

    public static void setSuffixText(@NonNull TextView textView, @NonNull String content, @NonNull String roundBackground, @NonNull String suffixName) {
        localSuffix(textView, content, ColorDrawer.string2Int(roundBackground), textView.getCurrentTextColor(), suffixName);
    }

    public static void setSuffixText(@NonNull TextView textView, @NonNull String content, @ColorRes int roundBackground, @NonNull String suffixName) {
        localSuffix(textView, content, ColorDrawer.getColor(roundBackground), textView.getCurrentTextColor(), suffixName);
    }

    public static void setSuffixText(@NonNull TextView textView, @NonNull String content, RichConfig richConfig) {
        localSuffix(textView, content, richConfig);
    }

    private static void localSuffix(@NonNull TextView textView, @NonNull String content, @ColorInt int roundBackground, @ColorInt int roundColor, @NonNull String suffixName) {
        textView.setText(getSuffixSpannableString(textView, content, roundBackground, roundColor, suffixName, 0));
    }

    private static void localSuffix(@NonNull TextView textView, @NonNull String content, RichConfig richConfig) {
        textView.setText(getSuffixSpannableString(textView, content, richConfig.getSuffixBackground(), richConfig.getSuffixColor(), richConfig.getSuffixName(), richConfig.getRadius()));
    }

    private static SpannableString getSuffixSpannableString(@NonNull TextView textView, String content, @ColorInt int roundBackground, @ColorInt int roundColor, @NonNull String suffixName, int radius) {
        SpannableString spannableString;
        if (TextUtils.isEmpty(content)) {
            spannableString = new SpannableString(suffixName);
        } else {
            spannableString = new SpannableString(suffixName + content);
        }
        spannableString.setSpan(new RelativeSizeSpan(0.81f), 0, suffixName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RoundBackgroundColorSpan1(textView.getContext(), roundBackground, roundColor == 0 ? textView.getCurrentTextColor() : roundColor, radius), 0, suffixName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }


    public static void setUnderLine(TextView tv, int start, int end) {
        if (tv.getText() != null) {
            String udata = tv.getText().toString();
            SpannableString content = new SpannableString(udata);
            content.setSpan(new UnderlineSpan(), start, end, 0);
            tv.setText(content);

        } else {
            tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    /**
     * 设置textview字体颜色和下划线
     *
     * @param tv
     * @param str
     * @param textColor
     */
    public static void setUnderLineAndColor(TextView tv, String str, int textColor) {
        if (tv.getText() != null) {
            String udata = tv.getText().toString();
            if (udata.contains(str)) {
                int start = udata.indexOf(str);
                SpannableString content = new SpannableString(udata);
                content.setSpan(new UnderlineSpan(), start, start + str.length(), 0);
                content.setSpan(new ForegroundColorSpan(ContextCompat.getColor(tv.getContext(), textColor)), start, start + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(content);
            }
        }
    }

    public static void setUnderLineAndColor02(TextView tv, String str01, String str02, int textColor) {
        if (tv.getText() != null) {
            String udata = tv.getText().toString();
            if (udata.contains(str01) && udata.contains(str02)) {
                int start = udata.indexOf(str01);
                SpannableString content = new SpannableString(udata);
                content.setSpan(new UnderlineSpan(), start, start + str01.length(), 0);
                content.setSpan(new ForegroundColorSpan(ContextCompat.getColor(tv.getContext(), textColor)), start, start + str01.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                int start02 = udata.indexOf(str02);
                content.setSpan(new UnderlineSpan(), start02, start02 + str02.length(), 0);
                content.setSpan(new ForegroundColorSpan(ContextCompat.getColor(tv.getContext(), textColor)), start02, start02 + str02.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(content);
            }
        }
    }

    //取消TextView的置下划线
    public static void clearUnderLine(TextView tv) {
        tv.getPaint().setFlags(0);
    }

    //去除特殊字符或将所有中文标号替换为英文标号
    public static String replaceCharacter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":")
                .replaceAll("（", "(").replaceAll("（", ")")
                .replaceAll("#", "#");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    //设置textview 的图片
    public static void setPictureLeft(TextView tv, @DrawableRes int res) {
        Drawable drawable = ContextCompat.getDrawable(tv.getContext(), res);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(drawable, null, null, null);
    }

    //设置textview 的图片
    public static void setPictureRight(TextView tv, @DrawableRes int res) {
        Drawable drawable = ContextCompat.getDrawable(tv.getContext(), res);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(null, null, drawable, null);
    }

    public static void setPictureLeftDrawableTop(TextView tv, @DrawableRes int res, int patchSize) {
        Drawable drawable = ContextCompat.getDrawable(tv.getContext(), res);
        assert drawable != null;
        drawable.setBounds(0, -(drawable.getMinimumHeight() + patchSize), drawable.getMinimumWidth() + patchSize, 0);
        tv.setCompoundDrawables(drawable, null, null, null);
    }

    //设置左右图片
    public static void setPictureRightLeft(TextView tv, @DrawableRes int left, @DrawableRes int right) {
        Drawable drawableLeft = ContextCompat.getDrawable(tv.getContext(), left);
        drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
        Drawable drawableRight = ContextCompat.getDrawable(tv.getContext(), right);
        drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());
        tv.setCompoundDrawables(drawableLeft, null, drawableRight, null);
    }

    //设置金额样式
    public static void setMoneyBoldStyle(TextView tv, int textSize) {
        try {
            String s = tv.getText().toString();
            //size：默认单位为px。
            //dip：true为size的单位是dip，false为px。
            Spannable spannable = new SpannableString(s);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 1, tv.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new AbsoluteSizeSpan(textSize, true), 1, tv.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(spannable);
        } catch (Exception e) {

        }
    }

    //设置金额样式
    public static void setMoneyBigStyle(TextView tv, int textSize) {
        try {
            String s = tv.getText().toString();
            //size：默认单位为px。
            //dip：true为size的单位是dip，false为px。
            Spannable spannable = new SpannableString(s);
            spannable.setSpan(new AbsoluteSizeSpan(textSize, true), 1, tv.getText().length() - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(spannable);
        } catch (Exception e) {

        }
    }

    public static ColorStateList getColorStateList(Context context, int colorId) {
        return context.getResources().getColorStateList(colorId);
    }

    public static ColorStateList getColorStateList(int colorId) {
        return AnApplication.getApplication().getResources().getColorStateList(colorId);
    }

    //设置金额样式
    public static void setMoneyDayBoldStyle(TextView tv, int textSize) {
        try {
            String s = tv.getText().toString();
            //size：默认单位为px。
            //dip：true为size的单位是dip，false为px。
            Spannable spannable = new SpannableString(s);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 1, tv.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new AbsoluteSizeSpan(textSize, true), 1, tv.getText().length() - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(spannable);
        } catch (Exception e) {

        }
    }

    //设置金额样式
    public static void setMoneyBoldStyleEnd(TextView tv, int textSize) {
        try {
            String s = tv.getText().toString();
            //size：默认单位为px。
            //dip：true为size的单位是dip，false为px。
            Spannable spannable = new SpannableString(s);
            spannable.setSpan(new StyleSpan(Typeface.NORMAL), tv.getText().length() - 1, tv.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new AbsoluteSizeSpan(textSize, true), tv.getText().length() - 1, tv.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(spannable);
        } catch (Exception e) {

        }
    }

    /**
     * textview设置中划线
     *
     * @param textView
     */
    public static void setMiddleLine(TextView textView) {
        TextPaint paint = textView.getPaint();
        paint.setAntiAlias(true);
        paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
    }

    /**
     * textview设置颜色，可以是选择器，也可以是单独的颜色资源
     *
     * @param textView
     */
    public static void setTextColor(@NonNull TextView textView, int colorResId) {
// textView.setTextColor(ContextCompat.getColorStateList(MainActivity.this, colorResId));
//        textView.setTextColor(textView.getContext().getResources().getColorStateList(colorResId));
        textView.setTextColor(getColorStateList(textView.getContext(), colorResId));
    }

    /*
     * 根据关键字keywords来设置textView颜色，以及是否加粗，以及是否换行显示，默认不会加粗
     * keyWords关键词数组，colorStr字符串颜色资源类似#000000，originalText，原始需要处理的数据
     * needWrap =，设置是否根据keyWord添加换行显示，默认true,
     * */
    public static void setKeyTextColorBold(@NonNull TextView textView, String originalStr, @NonNull String keyWord, @NonNull String colorStr, boolean needWrap) {
        setWholeKeyColor(textView, originalStr, new String[]{keyWord}, colorStr, needWrap, true);
    }

    public static void setKeyTextColorBold(@NonNull TextView textView, @NonNull String keyWord, @NonNull String colorStr, boolean needWrap) {
        setWholeKeyColor(textView, AnConstants.EMPTY, new String[]{keyWord}, colorStr, needWrap, true);
    }

    public static void setKeyTextColorBold(@NonNull TextView textView, @NonNull String keyWord, @NonNull String colorStr) {
        setWholeKeyColor(textView, AnConstants.EMPTY, new String[]{keyWord}, colorStr, true, true);
    }

    public static void setKeyTextColorBold(@NonNull TextView textView, String originalStr, @NonNull String[] keyWords, @NonNull String colorStr, boolean needWrap) {
        setWholeKeyColor(textView, originalStr, keyWords, colorStr, needWrap, true);
    }

    public static void setKeyTextColorBold(@NonNull TextView textView, @NonNull String[] keyWords, @NonNull String colorStr, boolean needWrap) {
        setWholeKeyColor(textView, AnConstants.EMPTY, keyWords, colorStr, needWrap, true);
    }

    public static void setKeyTextColorBold(@NonNull TextView textView, @NonNull String[] keyWords, @NonNull String colorStr) {
        setWholeKeyColor(textView, AnConstants.EMPTY, keyWords, colorStr, true, true);
    }

    public static void setKeyTextColor(@NonNull TextView textView, String originalStr, @NonNull String keyWord, @NonNull String colorStr, boolean needWrap) {
        setWholeKeyColor(textView, originalStr, new String[]{keyWord}, colorStr, needWrap, false);
    }

    public static void setKeyTextColor(@NonNull TextView textView, @NonNull String keyWord, @NonNull String colorStr, boolean needWrap) {
        setWholeKeyColor(textView, AnConstants.EMPTY, new String[]{keyWord}, colorStr, needWrap, false);
    }

    public static void setKeyTextColor(@NonNull TextView textView, @NonNull String keyWord, @NonNull String colorStr) {
        setWholeKeyColor(textView, AnConstants.EMPTY, new String[]{keyWord}, colorStr, true, false);
    }

    public static void setKeyTextColor(@NonNull TextView textView, String originalStr, @NonNull String[] keyWords, @NonNull String colorStr, boolean needWrap) {
        setWholeKeyColor(textView, originalStr, keyWords, colorStr, needWrap, false);
    }

    public static void setKeyTextColor(@NonNull TextView textView, @NonNull String[] keyWords, @NonNull String colorStr, boolean needWrap) {
        setWholeKeyColor(textView, AnConstants.EMPTY, keyWords, colorStr, needWrap, false);
    }

    public static void setKeyTextColor(@NonNull TextView textView, @NonNull String[] keyWords, @NonNull String colorStr) {
        setWholeKeyColor(textView, AnConstants.EMPTY, keyWords, colorStr, true, false);
    }

    private static void setWholeKeyColor(@NonNull TextView textView, @NonNull String originalStr, @NonNull String[] keyWords, @NonNull String colorStr, boolean needWrap, boolean bold) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean contains = false;
        if (TextUtils.isEmpty(originalStr)) {
            originalStr = textView.getText().toString();
        }
        if (TextUtils.isEmpty(originalStr)) {
            LaLog.e("setWholeColor originalStr is Empty");
            return;
        }
        if (!colorStr.contains("#")) {
            LaLog.e("setWholeColor colorStr is ilegal");
            return;
        }
        for (String keyWord : keyWords) {
            if (originalStr.contains(keyWord)) {
                contains = true;
            }
        }
        if (contains) {
// int onceBeginIndex = 0;
            for (int i = 0; i < keyWords.length; i++) {
                String keyWord = keyWords[i];
                int onceEndIndex = originalStr.indexOf(keyWord);
                if (needWrap) {
                    if (bold) {
                        if (i == 0) {
                            if (onceEndIndex == 0) {
                                stringBuilder.append(originalStr.substring(0, onceEndIndex) + "<font color=\"" + colorStr + "\"><b>" + keyWord + "<b></font>");
                            } else {
                                stringBuilder.append(originalStr.substring(0, onceEndIndex) + "<br/><font color=\"" + colorStr + "\"><b>" + keyWord + "<b></font>");
                            }
                        } else {
                            stringBuilder.append(originalStr.substring(0, onceEndIndex) + "<br/><font color=\"" + colorStr + "\"><b>" + keyWord + "<b></font>");
                        }
                    } else {
                        if (i == 0) {
                            stringBuilder.append(originalStr.substring(0, onceEndIndex) + "<font color=\"" + colorStr + "\">" + keyWord + "</font>");
                        } else {
                            stringBuilder.append(originalStr.substring(0, onceEndIndex) + "<br/><font color=\"" + colorStr + "\">" + keyWord + "</font>");
                        }
                    }
                } else {
                    if (bold) {
                        stringBuilder.append(originalStr.substring(0, onceEndIndex) + "<font color=\"" + colorStr + "\"><b>" + keyWord + "<b></font>");
                    } else {
                        stringBuilder.append(originalStr.substring(0, onceEndIndex) + "<font color=\"" + colorStr + "\">" + keyWord + "</font>");
                    }
                }
                originalStr = originalStr.substring(keyWord.length() + onceEndIndex);
            }
            stringBuilder.append(originalStr);
            setRichText(textView, stringBuilder.toString());
        } else {
            LaLog.e("setWholeColor not contains the keyWords[]");
        }
    }

    public static void setTextofSpannableBuilder(@NonNull TextView textView, @NonNull String content, RichConfig richConfig, OnSmartClickListener<String> onSmartClickListener) {
        if (richConfig.getLinkColor() == 0) {
            richConfig.setLinkColor(R.color.picture_color_blue);
        }
        textView.setText(getSpannableBuilder(textView, content, richConfig, onSmartClickListener));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void setTextofSpannableBuilder(@NonNull TextView textView, @NonNull String content, RichConfig richConfig) {
        setTextofSpannableBuilder(textView, content, richConfig, null);
    }

    public static void setTextofSpannableBuilder(@NonNull TextView textView, @NonNull String content, String[] markFilterKey, OnSmartClickListener<String> onSmartClickListener) {
        setTextofSpannableBuilder(textView, content, 0, 0, markFilterKey, onSmartClickListener);
    }

    public static void setTextofSpannableBuilder(@NonNull TextView textView, @NonNull String content, String[] markFilterKey) {
        setTextofSpannableBuilder(textView, content, markFilterKey, null);
    }

    public static void setTextofSpannableBuilder(@NonNull TextView textView, @NonNull String content, @ColorRes int markBackground, @ColorRes int markColor, String[] markFilterKey) {
        setTextofSpannableBuilder(textView, content, markBackground, markColor, markFilterKey, null);
    }

    public static void setTextofSpannableBuilder(@NonNull TextView textView, @NonNull String content, @ColorRes int markBackground, @ColorRes int markColor, String[] markFilterKey, OnSmartClickListener<String> onSmartClickListener) {
        RichConfig richConfig = new RichConfig();
        richConfig.setMarkFilterKey(markFilterKey);
        if (markBackground == 0) {
            richConfig.setMarkBackground(R.color.ColorGold);
        } else {
            richConfig.setMarkBackground(markBackground);
        }
        if (markColor == 0) {
            richConfig.setMarkColor(R.color.ColorWhite);
        } else {
            richConfig.setMarkColor(markColor);
        }
        setTextofSpannableBuilder(textView, content, richConfig, onSmartClickListener);
    }


    public static SpannableStringBuilder getSpannableBuilder(@NonNull TextView textView, @NonNull String content, RichConfig richConfig, OnSmartClickListener<String> onSmartClickListener) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        if (!TextUtils.isEmpty(richConfig.getSuffixName()) && richConfig.isFirstSuffix()) {
            ssb.append(getSuffixSpannableString(textView, AnConstants.EMPTY, richConfig.getSuffixBackground(), richConfig.getSuffixColor(), richConfig.getSuffixName(), richConfig.getRadius()));
        }
        ssb.append(content);
        if (richConfig != null) {
            if (richConfig.getMarkFilterKey() != null && richConfig.getMarkFilterKey().length > 0) {
                for (String filterKeyName : richConfig.getMarkFilterKey()) {
                    if (!TextUtils.isEmpty(filterKeyName)) {
                        Pattern pattern = Pattern.compile("@" + filterKeyName + "\\{(.*?)\\}");
                        Matcher re = pattern.matcher(ssb);
                        int index = 0;
                        while (re.find()) {
                            String reGroup = re.group();
                            reGroup = reGroup.replaceAll("@" + filterKeyName + "\\{", "");
                            reGroup = reGroup.replaceAll("\\}", "");
                            if (filterKeyName.equals("url")) {
                                SpannableString spannableString;
                                if (richConfig.isLineBreak()) {
                                    spannableString = new SpannableString(reGroup + richConfig.getMarkLink().trim() + "\n");
                                } else {
                                    spannableString = new SpannableString(reGroup + richConfig.getMarkLink().trim());
                                }
                                String finalLink = reGroup;
                                ClickableSpan clickableSpan = new ClickableSpan() {
                                    @Override
                                    public void onClick(@NonNull View view) {
                                        String resultUrl = finalLink;
                                        if (!resultUrl.contains("http://") && !resultUrl.contains("https://")) {
                                            resultUrl = richConfig.isHttpsLink() ? "https://" + resultUrl : "http://" + resultUrl;
                                        }
                                        DataService.getInstance().copyClipboad(textView.getContext(), resultUrl);
                                        resultUrl = Uri.decode(resultUrl);
                                        if (onSmartClickListener != null) {
                                            onSmartClickListener.onSmartClick(filterKeyName + "#" + resultUrl.trim());
                                        } else {
                                            ToastUtils.s(textView.getContext(), filterKeyName + (TextUtils.isEmpty(richConfig.getCopyTips()) ? "已复制" : richConfig.getCopyTips()));
                                        }
                                    }

                                    @Override
                                    public void updateDrawState(@NonNull TextPaint paint) {
                                        if (richConfig.getLinkColor() != 0) {
                                            paint.setColor(richConfig.getLinkColor());
                                        }
                                        paint.setUnderlineText(true);
                                    }
                                };
                                spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                int markLinkLenght = richConfig.isLineBreak() ? richConfig.getMarkLink().trim().length() + 1 : richConfig.getMarkLink().trim().length();
                                spannableString.setSpan(new RelativeSizeSpan(0.8f), spannableString.length() - markLinkLenght, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannableString.setSpan(new RelativeSizeSpan(1f), 0, spannableString.length() - markLinkLenght, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannableString.setSpan(new RoundBackgroundColorSpan1(textView.getContext(), ColorDrawer.getColor(R.color.ColorTransparent), richConfig.getLightColor() == 0 ? textView.getCurrentTextColor() : richConfig.getLightColor(), richConfig.getRadius()), spannableString.length() - markLinkLenght, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ssb.replace(re.start() - index, (re.start() + re.group().length() - index), spannableString);
                                index = index + (filterKeyName.length() + 3 - markLinkLenght);
                            } else {
                                SpannableString spannableString;
                                if (richConfig.isLineBreak()) {
                                    spannableString = new SpannableString(reGroup + richConfig.getMarkLight().trim() + "\n");
                                } else {
                                    spannableString = new SpannableString(reGroup + richConfig.getMarkLight().trim());
                                }
                                String resultValue = reGroup;
                                ClickableSpan clickableSpan = new ClickableSpan() {
                                    @Override
                                    public void onClick(@NonNull View view) {
                                        DataService.getInstance().copyClipboad(textView.getContext(), resultValue.trim());
                                        if (onSmartClickListener != null) {
                                            onSmartClickListener.onSmartClick(filterKeyName + "#" + resultValue.trim());
                                        } else {
                                            ToastUtils.s(textView.getContext(), filterKeyName + (TextUtils.isEmpty(richConfig.getCopyTips()) ? "已复制" : richConfig.getCopyTips()));
                                        }
                                    }

                                    @Override
                                    public void updateDrawState(@NonNull TextPaint paint) {
                                        if (richConfig.getLightColor() != 0)
                                            paint.setColor(richConfig.getLightColor());
                                        paint.setUnderlineText(false);
                                    }
                                };
                                spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                int markLightLenght = richConfig.isLineBreak() ? richConfig.getMarkLight().trim().length() + 1 : richConfig.getMarkLight().trim().length();
                                spannableString.setSpan(new RelativeSizeSpan(0.8f), spannableString.length() - markLightLenght, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannableString.setSpan(new RelativeSizeSpan(0.9f), 0, spannableString.length() - markLightLenght, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannableString.setSpan(new RoundBackgroundColorSpan1(textView.getContext(), richConfig.getMarkBackground(), richConfig.getMarkColor() == 0 ? textView.getCurrentTextColor() : richConfig.getMarkColor(), richConfig.getRadius()), 0, spannableString.length() - markLightLenght, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ssb.replace(re.start() - index, (re.start() + re.group().length() - index), spannableString);
                                index = index + (filterKeyName.length() + 3 - markLightLenght);
                            }
                        }
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(richConfig.getSuffixName()) && !richConfig.isFirstSuffix()) {
            ssb.append(getSuffixSpannableString(textView, AnConstants.EMPTY, richConfig.getSuffixBackground(), richConfig.getSuffixColor(), richConfig.getSuffixName(), richConfig.getRadius()));
        }
        return ssb;
    }
}