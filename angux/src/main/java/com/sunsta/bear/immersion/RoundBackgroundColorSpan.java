package com.sunsta.bear.immersion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RoundBackgroundColorSpan extends ReplacementSpan {
    private Context context;
    private int mBgColor;
    private int mTextColor;

    public RoundBackgroundColorSpan(Context context, int bgColorResId, int textColorResId) {
        super();
        this.context = context;
        mBgColor = bgColorResId;
        mTextColor = textColorResId;
    }


    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fontMetricsInt) {
        return (int) paint.measureText(text, start, end);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        //记录原始配置值~
        int originalColor = paint.getColor();
        float originalTextSize = paint.getTextSize();
        float originalBottom = paint.getFontMetrics().bottom;
        //高度~
        int height = bottom - top;
        //半径~
        float radius = height / 2f - 1;
        //缩放值~
//        var mScale = contextSize / paint.textSize
        float mScale = 32 / paint.getTextSize();
        if (mScale > 1) {
            mScale = 1f;
        }

        paint.setTextSize(18f);
        //记录文字的偏移量~
        float excursionL = paint.measureText(text, start, end) / 2f;

        //计算缩放后的偏移量~
        float excursionP = (height - (paint.getFontMetrics().bottom - paint.getFontMetrics().top)) / 2;

        paint.setColor(mBgColor);
        //y 需要bottom
        canvas.drawCircle(x + excursionL + 10, y + originalBottom - (height / 2), radius * mScale, paint);
        paint.setColor(mTextColor);

        canvas.drawText(text, start, end, x + 2, y - excursionP * mScale, paint);
        paint.setColor(originalColor);
        paint.setTextSize(originalTextSize);
    }
}
