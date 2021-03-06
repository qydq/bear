package com.sunsta.bear.faster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.sunsta.bear.AnApplication;
import com.sunsta.bear.R;
import com.sunsta.bear.config.PictureMimeType;

import java.util.regex.Pattern;

/**
 * @author：luck
 * @data：2017/5/25 19:12
 * @描述: String Utils
 */
public class StringUtils {
    public static void tempTextFont(TextView tv, int mimeType) {
        String text = tv.getText().toString().trim();
        String str = mimeType == PictureMimeType.ofAudio() ?
                tv.getContext().getString(R.string.picture_empty_audio_title)
                : tv.getContext().getString(R.string.picture_empty_title);
        String sumText = str + text;
        Spannable placeSpan = new SpannableString(sumText);
        placeSpan.setSpan(new RelativeSizeSpan(0.8f), str.length(), sumText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(placeSpan);
    }

    /**
     * 匹配数值
     * @param str
     * @return
     */
    public static int stringToInt(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]+$");
        return pattern.matcher(str).matches() ? Integer.valueOf(str) : 0;
    }

    /**
     * 根据类型获取相应的Toast文案
     * @param context
     * @param mimeType
     * @param maxSelectNum
     * @return
     */
    @SuppressLint("StringFormatMatches")
    public static String getMsg(Context context, String mimeType, int maxSelectNum) {
        if (PictureMimeType.eqVideo(mimeType)) {
            return context.getString(R.string.picture_message_video_max_num, maxSelectNum);
        } else if (PictureMimeType.eqAudio(mimeType)) {
            return context.getString(R.string.picture_message_audio_max_num, maxSelectNum);
        } else {
            return context.getString(R.string.picture_message_max_num, maxSelectNum);
        }
    }

    /**
     * 重命名相册拍照
     * @param fileName
     * @return
     */
    public static String rename(String fileName) {
        String temp = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return temp + "_" + DateUtils.getCreateFileName() + suffix;
    }

    /**
     * 重命名后缀
     * @param fileName
     * @return
     */
    public static String renameSuffix(String fileName, String suffix) {
        String temp = fileName.substring(0, fileName.lastIndexOf("."));
        return temp + suffix;
    }

    public static String getString(@StringRes int resId) {
        return AnApplication.getApplication().getString(resId);
    }
}
