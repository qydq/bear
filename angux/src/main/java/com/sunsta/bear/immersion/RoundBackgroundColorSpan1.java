package com.sunsta.bear.immersion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.ColorInt;

import com.sunsta.bear.faster.ScreenUtils;

public class RoundBackgroundColorSpan1 extends ReplacementSpan {
    private int bgColor;
    private int textColor;
    private int radius;
    private Context mContext;

    public RoundBackgroundColorSpan1(Context context, @ColorInt int bgColor, @ColorInt int textColor) {
        super();
        this.mContext = context;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.radius = 27;
    }

    public RoundBackgroundColorSpan1(Context context, @ColorInt int bgColor, @ColorInt int textColor, int radius) {
        super();
        this.mContext = context;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.radius = radius;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return ((int) paint.measureText(text, start, end) + ScreenUtils.px2dp(16));//宽度为文字宽度加16dp
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int originalColor = paint.getColor();
        paint.setColor(this.bgColor);
//画圆角矩形背景
        float topRect = top + ScreenUtils.px2dp(3);
        float rightRect = x + ((int) paint.measureText(text, start, end) + ScreenUtils.px2dp(16));
        float bottomRect = top + ScreenUtils.dip2px(24);
        RectF rectF = new RectF(x, topRect, rightRect, bottomRect);
        canvas.drawRoundRect(rectF, ScreenUtils.px2dp(radius == 0 ? 27 : radius), ScreenUtils.px2dp(radius == 0 ? 27 : radius), paint);
        paint.setColor(this.textColor);
//两边各增加8dp
        canvas.drawText(text, start, end, x + ScreenUtils.px2dp(8), y, paint);
        paint.setColor(originalColor);
    }
}