package com.sunsta.bear.layout;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.sunsta.bear.R;

import java.util.Timer;
import java.util.TimerTask;

public class INAStartAnimationView extends View {
    private long animationInterval = 2000;
    private long animationFinishTime = 50;
    private int colorOfBackground = Color.WHITE;

    private float fraction;
    private Drawable mDrawable = null;
    private static final int FINISHANIMATION = 1;

    private Context mContext;

    public INAStartAnimationView(Context context) {
        super(context);
        mContext = context;
    }

    public INAStartAnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public INAStartAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setImage(int id) {
        mDrawable = mContext.getResources().getDrawable(id);
    }

    /**
     * 设置完成的百分比
     * @param fraction 百分比
     */
    private void setFraction(float fraction) {
        this.fraction = fraction;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(colorOfBackground); //绘制背景色
        super.onDraw(canvas);
        drawAppIcon(canvas, fraction, mDrawable);
        drawAppStatement(canvas, fraction, "你好，我是测试");
    }

    public void drawAppIcon(Canvas canvas, float fraction, Drawable icon) {
        canvas.save();
        int width = getWidth();
        int height = getHeight();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int radius = bitmapWidth * 3 / 2;
        int centerX = width / 2 + bitmapWidth / 2;
        int centerY = height / 2 - 100;
//逐渐显示图片
        Path path = new Path();
        Matrix matrix = new Matrix();
        matrix.postScale(1f, 1f, centerX - bitmapWidth / 2, centerY - bitmapHeight / 2);
        path.addCircle(centerX, centerY, radius * (fraction - 0.1f) * 2, Path.Direction.CW);
        paint.setAlpha((int) (fraction * 255));
        canvas.concat(matrix);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, centerX - bitmapWidth, centerY - bitmapHeight, paint);
        canvas.restore();
    }

    public void drawAppStatement(Canvas canvas, float fraction, String statement) {
        int width = getWidth();
        int height = getHeight();

        int centerY = height / 2 - 100;
        canvas.save();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#A7A7A7"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(mContext.getResources().getDimension(R.dimen.an_font_tips));
        paint.setTextSkewX(-0.2f);
        paint.setTextAlign(Paint.Align.CENTER);

        Path path = new Path();
        int radius = width * 3 / 2;

        path.addCircle(0, centerY, radius * (fraction - 0.1f) * 2, Path.Direction.CW);

        Rect rect = new Rect(width / 4, centerY, width * 3 / 4, height);

        canvas.clipPath(path);
        canvas.drawText(statement, rect.centerX(), centerY + mContext.getResources().getDimensionPixelOffset(R.dimen.an_dimen_margin), paint);
        canvas.restore();
    }

    /**
     * 显示动画
     */
    public void show(RelativeLayout view) {
        view.addView(this);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "fraction", 0, 1);
        objectAnimator.setDuration(animationInterval - 50);
        objectAnimator.start();
//处理动画定时
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == FINISHANIMATION) {
//                    Toast.makeText(mContext, "我是测试的数据，时间到", Toast.LENGTH_SHORT).show();
                    moveAnimation();

                }
                return false;
            }
        });
//动画定时器
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = FINISHANIMATION;
                handler.sendMessage(message);
            }
        }, animationInterval);
    }

    /**
     * 隐藏动画view
     */
    private void moveAnimation() {
        this.animate().withLayer().setDuration(animationFinishTime);
    }
}