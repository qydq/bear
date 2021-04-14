package com.sunsta.bear.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.sunsta.bear.R;

/**
 * <h2>请关注个人知乎Bgwan， 在【an系列】专栏会有本【livery框架】的使用案例（20190922-正在持续更新中...</h2>
 * 中文描述：两边都是矩形的圆角视图 * <br/>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期：2016/04/28
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 1.0 |   2016/04/28           |   两边都是矩形的圆角视图。
 */
public class INARectangleImgeView extends AppCompatImageView {
    private Paint paint;
    private Bitmap sbmp;
    private float left = 20f;
    private float top = 20f;
    private float right = 20f;
    private float bottom = 20f;

    public INARectangleImgeView(Context context) {
        this(context, null);

    }

    public INARectangleImgeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public INARectangleImgeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
    }

    protected void onDraw(Canvas canvas) {
        int roundPx = 20;//圆角的大小，这个单位是像素
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.ColorTransparent)); //这里的颜色决定了边缘的颜色
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        if (drawable instanceof BitmapDrawable) {

            Bitmap b = ((BitmapDrawable) drawable).getBitmap();
            Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
            int w = getWidth();
            int h = getHeight();
            RectF rectF = new RectF(0, 0, w, h);

            Bitmap roundBitmap = getCroppedBitmap(bitmap, w, h, roundPx);

            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawBitmap(roundBitmap, 0, 0, null);
        }

    }

    public Bitmap getCroppedBitmap(Bitmap bmp, int lengthx, int lengthy, int roundPx) {

        if (bmp.getWidth() != lengthx || bmp.getHeight() != lengthy)
            sbmp = Bitmap.createScaledBitmap(bmp, lengthx, lengthy, false);
        else
            sbmp = bmp;

        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
        //left、right -:向右 +:向左   top、bottom -:向下 +:向上
        final RectF rectF = new RectF(left, top, sbmp.getWidth() + right, sbmp.getHeight() + bottom);
//        final RectF rectF = new RectF(-3, 0, sbmp.getWidth()+3, sbmp.getHeight() - 2);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    //这个方法是通过外部来控制边框的大小
    public void setRectFSize(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

}