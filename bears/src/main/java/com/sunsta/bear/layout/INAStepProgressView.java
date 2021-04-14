package com.sunsta.bear.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/*进度条展示框
* shiyong INAStepProgressView.public void method
* */
public class INAStepProgressView extends View {
    private static final int[] colors = new int[]{Color.parseColor("#FFFFFA8F"), Color.parseColor("#FFFFD33F")};
    private float b = 1.0f;
    private float c;
    private Paint d;
    private int e;
    private int f;
    public INAStepProgressView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }
    public INAStepProgressView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    public INAStepProgressView(Context context) {
        super(context);
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.d = new Paint();
        this.d.setAntiAlias(true);
        float f = this.c / this.b;
        this.d.setShader(new LinearGradient(0.0f, 0.0f, f * ((float) this.e), 0.0f, colors, null, Shader.TileMode.CLAMP));
        canvas.drawRect(0.0f, 0.0f, f * ((float) this.e), (float) this.f, this.d);
    }
    private int a(int i) {
        return (int) ((((float) i) * getContext().getResources().getDisplayMetrics().density) + (((float) (i >= 0 ? 1 : -1)) * 1056964608));
    }
    public void setMaxCount(float f) {
        if (f != 0.0f) {
            if (this.b != f) {
                this.b = f;
                if (this.c <= f && this.c > 0.0f) {
                    invalidate();
                }
            }
        }
    }
    public void setCurrentCount(float f) {
        if (f > this.b) {
            f = this.b;
        }
        this.c = f;
        invalidate();
    }
    protected void onMeasure(int i, int i2) {
        int mode = MeasureSpec.getMode(i);
        i = MeasureSpec.getSize(i);
        int mode2 = MeasureSpec.getMode(i2);
        i2 = MeasureSpec.getSize(i2);
        if (mode != 1073741824) {
            if (mode != Integer.MIN_VALUE) {
                this.e = 0;
                if (mode2 != Integer.MIN_VALUE) {
                    if (mode2 == 0) {
                        this.f = i2;
                        setMeasuredDimension(this.e, this.f);
                    }
                }
                this.f = a(28);
                setMeasuredDimension(this.e, this.f);
            }
        }
        this.e = i;
        if (mode2 != Integer.MIN_VALUE) {
            if (mode2 == 0) {
                this.f = i2;
                setMeasuredDimension(this.e, this.f);
            }
        }
        this.f = a(28);
        setMeasuredDimension(this.e, this.f);
    }
}