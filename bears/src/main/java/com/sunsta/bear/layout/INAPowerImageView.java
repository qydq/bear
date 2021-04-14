package com.sunsta.bear.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.sunsta.bear.R;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * <h2>请关注个人知乎Bgwan， 在【an系列】专栏会有本【livery框架】的使用案例（20190922-正在持续更新中...</h2>
 * 中文描述：PowerImageView * <br/>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期：2016/04/28
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 *
 * @author sunst // sunst0069
 * @version 1.0 |   2016/04/28           |   PowerImageView。
 */
public class INAPowerImageView extends AppCompatImageView implements View.OnClickListener {
    /**
     * 播放GIF动画的关键类
     */
    private Movie mMovie;

    /**
     * 开始播放按钮图片
     */
    private Bitmap mStartButton;
    /**
     * 记录动画开始的时间
     */
    private long mMovieStart;
    /**
     * GIF图片的宽度
     */
    private int mImageWidth;

    /**
     * GIF图片的高度
     */
    private int mImageHeight;
    /**
     * 图片是否正在播放
     */
    private boolean isPlaying;
    /**
     * 是否允许自动播放
     */
    private boolean isAutoPlay;

    /**
     * PowerImageView构造函数。
     *
     * @param context
     */
    public INAPowerImageView(Context context) {
        super(context);
    }

    /**
     * PowerImageView构造函数。
     *
     * @param context
     */
    public INAPowerImageView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }

    /**
     * PowerImageView构造函数，在这里完成所有必要的初始化操作。
     *
     * @param context
     */
    public INAPowerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.INAPowerImageView);
        int resourceId = getResourceId(a, context, attrs);
        if (resourceId != 0) {
// 当资源id不等于0时，就去获取该资源的流
            InputStream is = getResources().openRawResource(resourceId);
// 使用Movie类对流进行解码
            mMovie = Movie.decodeStream(is);
            if (mMovie != null) {
// 如果返回值不等于null，就说明这是一个GIF图片，下面获取是否自动播放的属性
                isAutoPlay = a.getBoolean(R.styleable.INAPowerImageView_asPowerAuto, false);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                mImageWidth = bitmap.getWidth();
                mImageHeight = bitmap.getHeight();
                bitmap.recycle();
                if (!isAutoPlay) {
// 当不允许自动播放的时候，得到开始播放按钮的图片，并注册点击事件
                    mStartButton = BitmapFactory.decodeResource(getResources(), R.mipmap.base_image_music_play);
                    setOnClickListener(this);
                }
            }
        }
    }

    @Override

    public void onClick(View v) {
        if (v.getId() == getId()) {
// 当用户点击图片时，开始播放GIF动画
            isPlaying = true;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie == null) {
// mMovie等于null，说明是张普通的图片，则直接调用父类的onDraw()方法
            super.onDraw(canvas);
        } else {
// mMovie不等于null，说明是张GIF图片
            if (isAutoPlay) {
// 如果允许自动播放，就调用playMovie()方法播放GIF动画
                playMovie(canvas);
                invalidate();
            } else {
// 不允许自动播放时，判断当前图片是否正在播放
                if (isPlaying) {
// 正在播放就继续调用playMovie()方法，一直到动画播放结束为止
                    if (playMovie(canvas)) {
                        isPlaying = false;
                    }
                    invalidate();
                } else {
// 还没开始播放就只绘制GIF图片的第一帧，并绘制一个开始按钮
                    mMovie.setTime(0);
                    mMovie.draw(canvas, 0, 0);
                    int offsetW = (mImageWidth - mStartButton.getWidth()) / 2;
                    int offsetH = (mImageHeight - mStartButton.getHeight()) / 2;
                    canvas.drawBitmap(mStartButton, offsetW, offsetH, null);
                }
            }
        }
    }


    @Override

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMovie != null) {
// 如果是GIF图片则重写设定PowerImageView的大小
            setMeasuredDimension(mImageWidth, mImageHeight);
        }
    }

    /**
     * 开始播放GIF动画，播放完成返回true，未完成返回false。
     *
     * @param canvas
     * @return 播放完成返回true，未完成返回false。
     */

    private boolean playMovie(Canvas canvas) {
        long now = SystemClock.uptimeMillis();
        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        int duration = mMovie.duration();
        if (duration == 0) {
            duration = 1000;
        }
        int relTime = (int) ((now - mMovieStart) % duration);
        mMovie.setTime(relTime);
        mMovie.draw(canvas, 0, 0);
        if ((now - mMovieStart) >= duration) {
            mMovieStart = 0;
            return true;
        }
        return false;
    }


    /**
     * 通过Java反射，获取到src指定图片资源所对应的id。
     *
     * @param a
     * @param context
     * @param attrs
     * @return 返回布局文件中指定图片资源所对应的id，没有指定任何图片资源就返回0。
     */

    private int getResourceId(TypedArray a, Context context, AttributeSet attrs) {
        try {
            Field field = TypedArray.class.getDeclaredField("mValue");
            field.setAccessible(true);
            TypedValue typedValueObject = (TypedValue) field.get(a);
            return typedValueObject.resourceId;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
        return 0;
    }
}
